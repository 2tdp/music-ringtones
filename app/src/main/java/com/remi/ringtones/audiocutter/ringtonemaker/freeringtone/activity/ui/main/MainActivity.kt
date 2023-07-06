package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.DataMusic
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.repository.DataRepository
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.PremiumActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.frag.AlbumsFrag
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.frag.HomeFrag
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.frag.SearchFrag
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.adapter.ViewPagerAddFragmentsAdapter
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.createBackground
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.openSettingPermission
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.edit.EditMusicActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityMainBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.DialogPermissionBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.ID_MUSIC
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.ActionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun getDispatchers(): CoroutineContext {
        return Dispatchers.IO + job
    }

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, ContextCompat.getColor(this, R.color.black_background))
    }

    @Inject
    lateinit var repository: DataRepository
    private val viewModel: MainActivityViewModel by viewModels()

    private var fragHome: HomeFrag? = null
    private var fragAlbums: AlbumsFrag? = null
    private var fragSearch: SearchFrag? = null

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.ctlNavi.isVisible) changeNavigation("hide")
                else if (fragAlbums != null)
                    fragAlbums?.let {
                        if (!it.backAlbum()) onBackPressed(true)
                    }
                else if (fragSearch != null) fragSearch?.handStop()
                else onBackPressed(true)
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            )
                showDialogPermission()
            else {
                launch { DataMusic.getSongList(this@MainActivity, repository) }
                startActivity(Intent(this@MainActivity, PremiumActivity::class.java))
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            )
                showDialogPermission()
            else {
                launch { DataMusic.getSongList(this@MainActivity, repository) }
                startActivity(Intent(this@MainActivity, PremiumActivity::class.java))
            }
        }

        setUpView()
        evenClick()
    }

    override fun onStop() {
        super.onStop()
        fragHome?.handStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragHome?.handStop()
        job.cancel()
    }

    private fun setUpView() {
        binding.edtSearch.createBackground(
            intArrayOf(Color.parseColor("#3F3F3F")), (2.5f * w).toInt(), -1, -1
        )

        val adapterPager = ViewPagerAddFragmentsAdapter(supportFragmentManager, lifecycle)
        fragHome = HomeFrag.newInstance()
        adapterPager.addFrag(fragHome!!)
        fragAlbums = AlbumsFrag.newInstance()
        adapterPager.addFrag(fragAlbums!!)
        fragSearch = SearchFrag.newInstance()
        adapterPager.addFrag(fragSearch!!)

        binding.viewPager.adapter = adapterPager

        binding.viewPager.offscreenPageLimit = 4
        binding.viewPager.isUserInputEnabled = false

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    fragSearch?.strSearch = s.toString()
                    if (it.toString() != "") viewModel.getAllMusicFromText(it.toString())
                    else viewModel.getAllMusicFromText("null")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun evenClick() {
        binding.ctlHome.setOnClickListener {
            fragHome?.let { viewModel.getAllMusic() }
            fragSearch?.handStop()
            swipePager("home")
        }
        binding.ctlAlbums.setOnClickListener {
            fragAlbums?.let { viewModel.getAllAlbum() }
            fragHome?.handStop()
            fragSearch?.handStop()
            swipePager("albums")
        }
        binding.ctlSearch.setOnClickListener {
            fragHome?.handStop()
            swipePager("search")
        }
        binding.ctlPremium.setOnClickListener {
            fragHome?.handStop()
            fragSearch?.handStop()
            startActivity(Intent(this@MainActivity, PremiumActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.ivMore.setOnClickListener {
            fragHome?.handStop()
            changeNavigation("show")
        }
        binding.bgNavi.setOnClickListener { changeNavigation("hide") }
        binding.ctlRate.setOnClickListener { ActionUtils.rateApp(this) }
        binding.ctlDownOtherApp.setOnClickListener { ActionUtils.moreApps(this) }
        binding.ctlFb.setOnClickListener { ActionUtils.openFacebook(this) }
        binding.ctlIg.setOnClickListener { ActionUtils.openInstagram(this) }
        binding.ctlFeedback.setOnClickListener { ActionUtils.sendFeedback(this) }
        binding.ctlPP.setOnClickListener { ActionUtils.openPolicy(this) }
    }

    private fun swipePager(option: String) {
        when (option) {
            "home" -> {
                binding.viewPager.setCurrentItem(0, true)
                binding.ivHome.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.main_color),
                        PorterDuff.Mode.SRC_ATOP
                    )
                binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.main_color))
                binding.ctlTitle.visibility = View.VISIBLE
                binding.lineTitle.visibility = View.VISIBLE
                binding.ctlEtSearch.visibility = View.GONE

                binding.ivAlbums.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.gray_main),
                        PorterDuff.Mode.SRC_ATOP
                    )
                binding.tvAlbums.setTextColor(ContextCompat.getColor(this, R.color.gray_main))

                binding.ivSearch.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.gray_main),
                        PorterDuff.Mode.SRC_ATOP
                    )
                binding.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.gray_main))
                binding.edtSearch.setText("")
            }

            "albums" -> {
                binding.ivHome.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.gray_main),
                        PorterDuff.Mode.SRC_ATOP
                    )
                binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray_main))

                binding.viewPager.setCurrentItem(1, true)
                binding.ivAlbums.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.main_color),
                        PorterDuff.Mode.SRC_ATOP
                    )
                binding.tvAlbums.setTextColor(ContextCompat.getColor(this, R.color.main_color))
                binding.ctlTitle.visibility = View.VISIBLE
                binding.lineTitle.visibility = View.VISIBLE
                binding.ctlEtSearch.visibility = View.GONE

                binding.ivSearch.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.gray_main),
                        PorterDuff.Mode.SRC_ATOP
                    )
                binding.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.gray_main))
            }

            "search" -> {
                binding.ivHome.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.gray_main),
                        PorterDuff.Mode.SRC_ATOP
                    )
                binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.gray_main))

                binding.ivAlbums.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.gray_main),
                        PorterDuff.Mode.SRC_ATOP
                    )
                binding.tvAlbums.setTextColor(ContextCompat.getColor(this, R.color.gray_main))

                binding.viewPager.setCurrentItem(2, true)
                binding.ivSearch.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.main_color),
                        PorterDuff.Mode.SRC_ATOP
                    )
                binding.tvSearch.setTextColor(ContextCompat.getColor(this, R.color.main_color))
                binding.ctlTitle.visibility = View.GONE
                binding.lineTitle.visibility = View.GONE
                binding.ctlEtSearch.visibility = View.VISIBLE
                binding.edtSearch.setText("")
            }
        }
    }

    private fun changeNavigation(option: String) {
        when (option) {
            "hide" -> {
                if (binding.ctlNavi.isVisible) {
                    binding.ctlNavi.animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
                    binding.ctlNavi.visibility = View.GONE
                    binding.bgNavi.visibility = View.GONE
                }
            }

            "show" -> {
                if (!binding.ctlNavi.isVisible) {
                    binding.ctlNavi.apply {
                        isFocusable = true
                        isClickable = true
                    }
                    binding.ctlNavi.animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                    binding.ctlNavi.visibility = View.VISIBLE
                    binding.ctlNavi.animation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            binding.bgNavi.visibility = View.VISIBLE
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                    })
                }
            }
        }
    }

    private fun showDialogPermission() {
        val bindingDialog = DialogPermissionBinding.inflate(LayoutInflater.from(this))
        bindingDialog.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, 5f * w)
        bindingDialog.tvAllow.setTextSize(TypedValue.COMPLEX_UNIT_PX, 5f * w)
        bindingDialog.tvDes.setTextSize(TypedValue.COMPLEX_UNIT_PX, 3.889f * w)

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
            isGranted()
        }
    }

    private fun isGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this@MainActivity)
                .withPermission(Manifest.permission.READ_MEDIA_AUDIO)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        launch { DataMusic.getSongList(this@MainActivity, repository) }
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                        openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissionRequest: PermissionRequest,
                        permissionToken: PermissionToken
                    ) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.des_permission,
                            Toast.LENGTH_SHORT
                        ).show()
                        permissionToken.continuePermissionRequest()
                    }
                }).check()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Dexter.withContext(this@MainActivity)
                    .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        }

                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                            launch { DataMusic.getSongList(this@MainActivity, repository) }
                        }
                    }).check()
            } else {
                Dexter.withContext(this@MainActivity)
                    .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        }

                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                            launch { DataMusic.getSongList(this@MainActivity, repository) }
                        }
                    }).check()
            }
        }
    }
}