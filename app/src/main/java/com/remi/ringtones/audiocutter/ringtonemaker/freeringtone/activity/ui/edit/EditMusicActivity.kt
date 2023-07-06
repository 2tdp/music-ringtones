package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.edit

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager

import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.contact.ContactModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.openSettingPermission
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.showToast
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.ID_MUSIC
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.cutmusic.CutMusicActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityEditMusicBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.DialogPermissionBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class EditMusicActivity : BaseActivity<ActivityEditMusicBinding>(ActivityEditMusicBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(
            ContextCompat.getColor(this, R.color.black_background),
            ContextCompat.getColor(this, R.color.black_background)
        )
    }

    private val viewModel: EditMusicActivityViewModel by viewModels()
    private lateinit var music: MusicEntity
    private var typeRing = -2

    private var mediaPlayer: MediaPlayer? = null
    private var isPlayAudio = false
    private var timer: Timer? = null
    private var isBackCut = false

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.rcvContact.isVisible) actionContact("hide")
                else {
                    if (isPlayAudio) handStop()
                    finish()
                }
            }
        })
        binding.tvNameSong.apply {
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        lifecycleScope.launch {
            viewModel.getMusicWithId(intent.getLongExtra(ID_MUSIC, -1L))
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateMusic.collect {
                    when (it) {
                        is UiState.Success -> {
                            music = it.data
                            setUpUi()
                        }

                        is UiState.Error -> {}
                        is UiState.Loading -> {

                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (typeRing != -2) checkPermission()
        if (isBackCut) {
            viewModel.getMusicWithId(DataLocalManager.getLong(ID_MUSIC))
            isBackCut = false
        }
    }

    private fun setUpUi() {
        binding.tvNameSong.text = music.songName
        val time = Utils.formatTime(music.duration)
        binding.tvDuration.text =
            if (time.size == 4) "${time[3]}:${time[2]}:${time[1]}" else "${time[2]}:${time[1]}"
        binding.ivFavorite.setImageResource(if (music.isFavorite) R.drawable.ic_favorite else R.drawable.ic_un_favorite)

        binding.ivFavorite.setOnClickListener {
            if (music.isFavorite) {
                music.isFavorite = false
                viewModel.removeFavorite(music)
                binding.ivFavorite.setImageResource(R.drawable.ic_un_favorite)
            } else {
                music.isFavorite = true
                viewModel.addFavorite(music)
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite)
            }
        }
        binding.ivControlMusic.setOnClickListener {
            if (!isPlayAudio) handPlay() else handStop()
        }
        binding.ivBack.setOnClickListener {
            if (binding.rcvContact.isVisible) actionContact("hide")
            else {
                if (isPlayAudio) handStop()
                finish()
            }
        }

        binding.ctlCutMusic.setOnClickListener {
            isBackCut = true
            if (isPlayAudio) handStop()
            startActivity(Intent(this, CutMusicActivity::class.java).apply {
                putExtra(ID_MUSIC, music.id)
            })
        }
        binding.ctlSetContact.setOnClickListener {
            typeRing = -1
            checkPermission()
            if (isPlayAudio) handStop()
        }
        binding.ctlSetRingtone.setOnClickListener {
            typeRing = RingtoneManager.TYPE_RINGTONE
            checkPermission()
        }
        binding.ctlSetNotify.setOnClickListener {
            typeRing = RingtoneManager.TYPE_NOTIFICATION
            checkPermission()
        }
        binding.ctlSetAlarm.setOnClickListener {
            typeRing = RingtoneManager.TYPE_ALARM
            checkPermission()
        }
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun actionContact(option: String) {
        when (option) {
            "show" -> {
                if (!binding.rcvContact.isVisible) {
                    binding.rcvContact.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                    binding.rcvContact.visibility = View.VISIBLE
                    val contactAdapter = ContactAdapter(this, music.uri, object : ICallBackItem {
                        override fun callBack(ob: Any, position: Int) {
                            val contact = ob as ContactModel
                            contentResolver.update(
                                ContactsContract.Contacts.getLookupUri(
                                    contact.id.toLong(),
                                    contact.lookupKey
                                ),
                                ContentValues().apply {
                                    put(ContactsContract.CommonDataKinds.Phone.CUSTOM_RINGTONE, music.uri)
                                },
                                null, null
                            )
                            showToast("Change default Contact Ringtone", Gravity.CENTER)
                        }
                    })
                    lifecycleScope.launch {
                        viewModel.getAllContact(this@EditMusicActivity)
                        repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.uiStateContact.collect {
                                when (it) {
                                    is UiState.Success -> contactAdapter.setData(it.data)
                                    is UiState.Error -> {}
                                    is UiState.Loading -> {}
                                }
                            }
                        }
                    }
                    binding.rcvContact.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rcvContact.adapter = contactAdapter

                    binding.ctlEdit.animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
                    binding.ctlEdit.visibility = View.GONE
                }
            }

            "hide" -> {
                binding.rcvContact.animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
                binding.rcvContact.visibility = View.GONE

                binding.ctlEdit.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left_small)
                binding.ctlEdit.visibility = View.VISIBLE
            }
        }
    }

    private fun handPlay() {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@EditMusicActivity, Uri.parse(music.path))
                setOnPreparedListener {
                    isPlayAudio = true
                    start()
                    Handler(Looper.getMainLooper()).post {
                        binding.ivControlMusic.setImageResource(R.drawable.ic_stop_music)
                    }
                }
                setOnCompletionListener { handStop() }
                prepareAsync()
            }
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                @SuppressLint("SetTextI18n")
                override fun run() {
                    Handler(Looper.getMainLooper()).post {
                        val curDuration = Utils.formatTime(mediaPlayer!!.currentPosition.toLong())
                        val duration = Utils.formatTime(music.duration)
                        binding.tvDuration.text =
                            "${if (curDuration.size == 4) "${curDuration[3]}:${curDuration[2]}:${curDuration[1]}" else "${curDuration[2]}:${curDuration[1]}"} / ${if (duration.size == 4) "${duration[3]}:${duration[2]}:${duration[1]}" else "${duration[2]}:${duration[1]}"}"
                    }
                }

            }, 0, 500)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    private fun handStop() {
        binding.ivControlMusic.setImageResource(R.drawable.ic_play_music)
        val time = Utils.formatTime(music.duration)
        binding.tvDuration.text =
            if (time.size == 4) "${time[3]}:${time[2]}:${time[1]}" else "${time[2]}:${time[1]}"
        if (timer != null) {
            timer!!.purge()
            timer!!.cancel()
            timer = null
        }
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.apply {
                    stop()
                    release()
                }
            }
            isPlayAudio = false
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                if (typeRing != -1) {
                    RingtoneManager.setActualDefaultRingtoneUri(this, typeRing, Uri.parse(music.uri))
                    when (typeRing) {
                        RingtoneManager.TYPE_RINGTONE ->
                            showToast("Change default Ringtone", Gravity.CENTER)

                        RingtoneManager.TYPE_NOTIFICATION ->
                            showToast("Change default Notification", Gravity.CENTER)

                        RingtoneManager.TYPE_ALARM ->
                            showToast("Change default Alarm", Gravity.CENTER)
                    }
                } else checkPermissionContact()
            } else {
                val bindingDialog = DialogPermissionBinding.inflate(LayoutInflater.from(this))
                bindingDialog.tvTitle.apply {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 5f * w)
                    text = getString(R.string.change_systems_settings)
                }
                bindingDialog.tvAllow.setTextSize(TypedValue.COMPLEX_UNIT_PX, 5f * w)
                bindingDialog.tvDes.apply {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 3.889f * w)
                    text =
                        getString(R.string.this_permission_will_allow_the_app_to_set_a_ringtone_sms_notifications_and_alarm)
                }

                val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
                dialog.apply {
                    setView(bindingDialog.root)
                    setCancelable(true)
                    show()
                }
                bindingDialog.root.layoutParams.width = (88.889f * w).toInt()
                bindingDialog.root.layoutParams.height = (40.556f * w).toInt()

                bindingDialog.tvAllow.setOnClickListener {
                    dialog.cancel()
                    openSettingPermission(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                }
            }
        }
    }

    private fun checkPermissionContact() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
            actionContact("show")
        else {
            val bindingDialog = DialogPermissionBinding.inflate(LayoutInflater.from(this))
            bindingDialog.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 5f * w)
            bindingDialog.tvAllow.setTextSize(TypedValue.COMPLEX_UNIT_PX, 5f * w)
            bindingDialog.tvDes.apply {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, 3.889f * w)
                text = getString(R.string.allow_music_ringtone_to_access_your_contacts)
            }

            val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
            dialog.apply {
                setView(bindingDialog.root)
                setCancelable(false)
                show()
            }
            bindingDialog.root.layoutParams.width = (88.889f * w).toInt()
            bindingDialog.root.layoutParams.height = (40.556f * w).toInt()

            bindingDialog.tvAllow.setOnClickListener {
                dialog.cancel()
                Dexter.withContext(this)
                    .withPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                            actionContact("show")
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        }

                    }).check()
            }
        }
    }


}