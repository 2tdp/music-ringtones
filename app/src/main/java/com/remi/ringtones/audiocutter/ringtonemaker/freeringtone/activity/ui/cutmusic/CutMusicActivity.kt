package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.cutmusic

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieDrawable
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityCutMusicBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.DialogDiscardMusicCutBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.DialogLoadMusicBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.DialogPickTimeBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.DialogSaveMusicCutBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.createBackground
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.showToast
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.ID_MUSIC
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.MY_SAVED
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.TAG
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils.createBackground
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils.getStore
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom.OnRangeSeekbarResult
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom.ringdroid.soundfile.SoundFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class CutMusicActivity : BaseActivity<ActivityCutMusicBinding>(ActivityCutMusicBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(
            ContextCompat.getColor(this, R.color.black_background),
            ContextCompat.getColor(this, R.color.black_background)
        )
    }

    private val viewModel: CutMusicActivityViewModel by viewModels()
    private lateinit var music: MusicEntity
    private var mSoundFile: SoundFile? = null
    private var mFile: File? = null
    private var outPath = ""
    private var duration = 0

    private var mediaPlayer: MediaPlayer? = null
    private var timer: Timer? = null
    private var isPlayAudio = false

    private var mTouchDragging = false
    private var mLoadingKeepGoing = false
    private var mFilename = ""
    private var mKeyDown = false
    private var mMaxPos: Int = 0
    private var mStartPos: Int = 0
    private var mEndPos: Int = 0
    private var mLastDisplayedStartPos: Int = 0
    private var mLastDisplayedEndPos: Int = 0
    private var mOffset: Int = 0
    private var mOffsetGoal: Int = 0
    private var mFlingVelocity: Int = 0
    private var mLoadingLastUpdateTime: Long = 0


    override fun setUp() {
        binding.tvNameSong.apply {
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialogDiscard()
            }
        })
        binding.rangeSeekbar.onSeekbarResult = object : OnRangeSeekbarResult {
            override fun onDown(v: View) {

            }

            override fun onMove(v: View, start: Int, end: Int) {
                mStartPos = formatTime(start)
                mEndPos = formatTime(end)
                binding.tvStart.text = binding.rangeSeekbar.convertProgressToTime(start)
                binding.tvEnd.text = binding.rangeSeekbar.convertProgressToTime(end)
            }

            override fun onUp(v: View, start: Int, end: Int) {

            }

        }
        lifecycleScope.launch {
            viewModel.getMusicWithId(intent.getLongExtra(ID_MUSIC, -1L))
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateMusic.collect {
                    when (it) {
                        is UiState.Success -> {
                            music = it.data

                            isPlayAudio = false
                            mFilename = try {
                                music.path.replaceFirst("file://".toRegex(), "")
                                    .replace("%20".toRegex(), " ")
                            } catch (e: NullPointerException) {
                                music.path.replaceFirst("file://".toRegex(), "")
                                    .replace("%20".toRegex(), " ")
                            }

                            mSoundFile = null
                            mKeyDown = false

                            loadFromFile()
                        }

                        is UiState.Error -> onBackPressed(false)
                        is UiState.Loading -> {

                        }
                    }
                }
            }
        }
    }

    private fun loadGui() {
        binding.tvNameSong.text = music.songName
        val time = Utils.formatTime(music.duration)
        binding.tvDuration.text =
            if (time.size == 4) "${time[3]}:${time[2]}:${time[1]}" else "${time[2]}:${time[1]}"
        binding.rangeSeekbar.apply {
            max = 100
            duration = music.duration
            invalidate()
        }
        binding.tvStart.apply {
            createBackground(
                intArrayOf(Color.parseColor("#3F3F3F")),
                (5.5f * w).toInt(), -1, -1
            )
            text = binding.rangeSeekbar.convertProgressToTime(binding.rangeSeekbar.progressStart)
        }
        binding.tvEnd.apply {
            createBackground(
                intArrayOf(Color.parseColor("#3F3F3F")),
                (5.5f * w).toInt(), -1, -1
            )
            text =
                binding.rangeSeekbar.convertProgressToTime(100 - binding.rangeSeekbar.progressEnd)
        }
        mStartPos = formatTime(binding.rangeSeekbar.progressStart)
        mEndPos = formatTime(100 - binding.rangeSeekbar.progressEnd)

        binding.ivBack.setOnClickListener { showDialogDiscard() }
        binding.ivControlMusic.setOnClickListener { if (!isPlayAudio) handPlay() else handStop() }
        binding.tvStart.setOnClickListener { showDialogPickTime("start") }
        binding.tvEnd.setOnClickListener { showDialogPickTime("end") }

        binding.ivTick.setOnClickListener {
            if (isPlayAudio) handStop()
            showDialogSave()
        }

        mMaxPos = 0
        mLastDisplayedStartPos = -1
        mLastDisplayedEndPos = -1
        if (mSoundFile != null && !binding.waveForm.hasSoundFile()) {
            binding.waveForm.setSoundFile(mSoundFile)
            binding.waveForm.recomputeHeights(0f)
            mMaxPos = binding.waveForm.maxPos()
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun loadFromFile() {
        mFile = File(mFilename)
        mLoadingLastUpdateTime = System.nanoTime() / 1000000
        mLoadingKeepGoing = true

        val bindingDialog = DialogLoadMusicBinding.inflate(LayoutInflater.from(this))
        bindingDialog.tvTitle.apply {
            text = getString(R.string.loading)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 6.667f * w)
        }
        bindingDialog.tvDes.apply {
            text = getString(R.string.please_wait_a_moment)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 3.33f * w)
        }
        val dialogLoadFile =
            AlertDialog.Builder(this@CutMusicActivity, R.style.SheetDialog).create()
        dialogLoadFile.apply {
            setView(bindingDialog.root)
            setCancelable(false)
            show()
        }
        bindingDialog.root.layoutParams.width = (88.889f * w).toInt()
        bindingDialog.root.layoutParams.height = (36.11f * w).toInt()

        Thread {
            try {
                mFile?.let {
                    mSoundFile = SoundFile.create(it.absolutePath) {
                        val now = System.nanoTime() / 1000000
                        if (now - mLoadingLastUpdateTime > 100) mLoadingLastUpdateTime = now

                        mLoadingKeepGoing
                    }
                    if (mSoundFile == null) {
                        Handler(Looper.getMainLooper()).post {
                            showToast(getString(R.string.cant_load_file), Gravity.CENTER)
                        }
                        onBackPressed(false)
                    } else {
                        dialogLoadFile.cancel()
                        Handler(Looper.getMainLooper()).post { loadGui() }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    showToast(getString(R.string.cant_load_file), Gravity.CENTER)
                }
                onBackPressed(false)
            }

            if (mLoadingKeepGoing) Handler(Looper.getMainLooper()).post { finishOpeningSoundFile() }
            else finish()
        }.start()
    }

    private fun handPlay() {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@CutMusicActivity, Uri.parse(music.path))
                setOnPreparedListener {
                    seekTo((binding.rangeSeekbar.progressStart * music.duration / 100).toInt())
                    isPlayAudio = true
                    start()
                    music.isPlay = true
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

                        val progress = mediaPlayer!!.currentPosition * 100f / music.duration

                        binding.rangeSeekbar.apply {
                            playback =
                                if (progress.toInt() < binding.rangeSeekbar.max - binding.rangeSeekbar.progressEnd)
                                    progress
                                else {
                                    handStop()
                                    0f
                                }
                            invalidate()
                        }
                    }
                }

            }, 0, 500)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    private fun handStop() {
        if (isPlayAudio) music.isPlay = false
        if (timer != null) {
            timer!!.purge()
            timer!!.cancel()
            timer = null
        }
        mediaPlayer?.let {
            if (isPlayAudio) {
                if (it.isPlaying) {
                    it.apply {
                        stop()
                        release()
                        binding.ivControlMusic.setImageResource(R.drawable.ic_play_music)
                        binding.rangeSeekbar.apply {
                            playback = 0f
                            invalidate()
                        }
                        val time = Utils.formatTime(music.duration)
                        binding.tvDuration.text =
                            if (time.size == 4) "${time[3]}:${time[2]}:${time[1]}" else "${time[2]}:${time[1]}"
                    }
                }
            }
            isPlayAudio = false
        }

    }

    private fun finishOpeningSoundFile() {
        binding.waveForm.setSoundFile(mSoundFile)
        mMaxPos = binding.waveForm.maxPos()
        mLastDisplayedStartPos = -1
        mLastDisplayedEndPos = -1
        mTouchDragging = false
        mOffset = 0
        mOffsetGoal = 0
        mFlingVelocity = 0
        if (mEndPos > mMaxPos) mEndPos = mMaxPos
    }

    private fun saveRingtone(title: String, callBack: ICallBackItem) {
        val startFrame = binding.waveForm.secondsToFrames(mStartPos.toDouble())
        val endFrame = binding.waveForm.secondsToFrames(mEndPos.toDouble())
        duration = (mEndPos - mStartPos + 0.5).toInt()

        // Save the sound file in a background thread
        Thread {
            // Try AAC first.
            outPath = makeRingtoneFilename(title, ".m4a")
            val outFile = File(outPath)
            try {
                // Write the new file
                mSoundFile!!.writeFile(
                    duration.toDouble(),
                    outFile,
                    startFrame,
                    endFrame - startFrame,
                    this,
                    callBack
                )
            } catch (e: Exception) {
                // log the error and try to create a .wav file instead
                if (outFile.exists()) outFile.delete()
                e.printStackTrace()
            }

            // Try to load the new file to make sure it worked
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    SoundFile.create(outPath) { true }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    SoundFile.create(outPath) { true }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun makeRingtoneFilename(title: String, extension: String): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            return getStore(this) + "/" + title + extension
        var externalRootDir = Environment.getExternalStorageDirectory().path
        if (!externalRootDir.endsWith("/")) externalRootDir += "/"
        val subdir = "media/audio/music/"
        var parentdir = externalRootDir + subdir

        // Create the parent directory
        val parentDirFile = File(parentdir)
        parentDirFile.mkdirs()

        // If we can't write to that special path, try just writing directly to the sdcard
        if (!parentDirFile.isDirectory) parentdir = externalRootDir

        // Turn the title into a filename
        val filename = StringBuilder()
        for (i in title.indices)
            if (Character.isLetterOrDigit(title[i])) filename.append(title[i])

        // Try to make the filename unique
        var path = ""
        for (i in 0..99) {
            val testPath =
                if (i > 0) parentdir + filename + i + extension else parentdir + filename + extension
            try {
                val f = RandomAccessFile(File(testPath), "r")
                f.close()
            } catch (e: java.lang.Exception) {
                // Good, the file didn't exist
                path = testPath
                break
            }
        }
        return path
    }

    private fun formatTime(progress: Int): Int {
        return (progress * music.duration / 100000).toInt()
    }

    private fun showDialogPickTime(type: String) {
        val timeStart = Utils.formatTime(binding.rangeSeekbar.progressStart * music.duration / 100)
        val timeEnd =
            Utils.formatTime((100 - binding.rangeSeekbar.progressEnd) * music.duration / 100)
        val bindingPickTime = DialogPickTimeBinding.inflate(LayoutInflater.from(this))
        bindingPickTime.tvTitle.apply {
            text =
                if (type == "start") getString(R.string.start_time) else getString(R.string.end_time)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 5f * w)
        }
        bindingPickTime.edtHour.apply {
            if (type == "start") {
                if (timeStart.size == 4) {
                    visibility = View.VISIBLE
                    setText(timeStart[3])
                    bindingPickTime.ivLine1.visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                    bindingPickTime.ivLine1.visibility = View.GONE
                }
            } else {
                if (timeEnd.size == 4) {
                    visibility = View.VISIBLE
                    setText(timeEnd[3])
                    bindingPickTime.ivLine1.visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                    bindingPickTime.ivLine1.visibility = View.GONE
                }
            }

            createBackground(
                intArrayOf(ContextCompat.getColor(context, R.color.gray_main)),
                (2.5f * w).toInt(), -1, -1
            )
        }
        bindingPickTime.edtMinute.apply {
            setText(if (type == "start") timeStart[2] else timeEnd[2])
            createBackground(
                intArrayOf(ContextCompat.getColor(context, R.color.gray_main)),
                (2.5f * w).toInt(), -1, -1
            )
        }
        bindingPickTime.edtSecond.apply {
            setText(if (type == "start") timeStart[1] else timeEnd[1])
            createBackground(
                intArrayOf(ContextCompat.getColor(context, R.color.gray_main)),
                (2.5f * w).toInt(), -1, -1
            )
        }

        val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
        dialog.apply {
            setCancelable(false)
            setView(bindingPickTime.root)
            show()
        }
        bindingPickTime.root.layoutParams.width = (88.889f * w).toInt()
        bindingPickTime.root.layoutParams.height = (48.333f * w).toInt()

        bindingPickTime.tvNo.setOnClickListener { dialog.cancel() }
        bindingPickTime.tvYes.setOnClickListener {
            var time = bindingPickTime.edtMinute.text.toString()
                .toLong() * 60 * 1000 + bindingPickTime.edtSecond.text.toString().toLong() * 1000

            if (time > music.duration) time = music.duration

            if (type == "start") {
                binding.rangeSeekbar.apply {
                    progressStart = (100 * time / music.duration).toInt()
                    invalidate()
                }
                mStartPos = formatTime(binding.rangeSeekbar.progressStart)
                binding.tvStart.text =
                    binding.rangeSeekbar.convertProgressToTime(binding.rangeSeekbar.progressStart)
            } else {

                binding.rangeSeekbar.apply {
                    progressEnd = ((music.duration - time) * 100 / music.duration).toInt()
                    invalidate()
                }
                mEndPos = formatTime(100 - binding.rangeSeekbar.progressEnd)
                binding.tvEnd.text =
                    binding.rangeSeekbar.convertProgressToTime(100 - binding.rangeSeekbar.progressEnd)
            }
            dialog.cancel()
        }
    }

    private fun showDialogDiscard() {
        val bindingDiscard = DialogDiscardMusicCutBinding.inflate(LayoutInflater.from(this))
        bindingDiscard.tvTitle.apply {
            text = getString(R.string.discard_change)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 5f * w)
        }

        val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
        dialog.apply {
            setCancelable(true)
            setView(bindingDiscard.root)
            show()
        }
        bindingDiscard.root.layoutParams.width = (88.889f * w).toInt()
        bindingDiscard.root.layoutParams.height = (27.778f * w).toInt()

        bindingDiscard.tvNo.setOnClickListener { dialog.cancel() }
        bindingDiscard.tvYes.setOnClickListener {
            dialog.cancel()
            DataLocalManager.setLong(music.id, ID_MUSIC)
            finish()
        }
    }

    private fun showDialogSave() {
        val bindingSave = DialogSaveMusicCutBinding.inflate(LayoutInflater.from(this))
        bindingSave.tvTitle.apply {
            text = getString(R.string.set_name_your_new_ringtone)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 5f * w)
        }
        bindingSave.edtName.apply {
            maxLines = 1
            isSingleLine = true
            createBackground(
                intArrayOf(ContextCompat.getColor(this@CutMusicActivity, R.color.gray_main2)),
                (2.5f * w).toInt(), -1, -1
            )
        }

        val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
        dialog.apply {
            setCancelable(false)
            setView(bindingSave.root)
            show()
        }
        bindingSave.root.layoutParams.width = (88.889f * w).toInt()
        bindingSave.root.layoutParams.height = (48.11f * w).toInt()

        var nameFile = "${binding.tvNameSong.text}-${System.currentTimeMillis()}"
        bindingSave.edtName.setText(nameFile)

        bindingSave.tvCancelName.setOnClickListener { dialog.cancel() }
        bindingSave.tvSave.setOnClickListener {
            nameFile = bindingSave.edtName.text.toString()
            bindingSave.ctlName.visibility = View.GONE
            bindingSave.tvTitle.text = getString(R.string.loading)
            bindingSave.vLoad.visibility = View.VISIBLE
            bindingSave.root.layoutParams.height = (31.667f * w).toInt()

            saveRingtone(nameFile, object : ICallBackItem {
                override fun callBack(ob: Any, position: Int) {
                    Handler(Looper.getMainLooper()).post {
                        bindingSave.vLoad.visibility = View.GONE
                        if (position == 0) {
                            val newId = ob as Long
                            bindingSave.apply {
                                tvTitle.text = getString(R.string.successful_music_editing)
                                ctlSuccess.visibility = View.VISIBLE
                                root.layoutParams.height = (39.444f * w).toInt()
                            }
                            DataLocalManager.setLong(newId, ID_MUSIC)
                            viewModel.insertMusic(
                                MusicEntity(
                                    newId,
                                    nameFile,
                                    false,
                                    duration * 1000L,
                                    "/storage/emulated/0/Music/$nameFile.m4a",
                                    MY_SAVED,
                                    outPath,
                                    false
                                )
                            )
                        } else if (position == -1)
                            bindingSave.apply {
                                tvTitle.text = getString(R.string.music_editing_failed)
                                ctlFailed.visibility = View.VISIBLE
                                root.layoutParams.height = (28.88f * w).toInt()
                            }
                    }
                }
            })
        }

        bindingSave.tvDone.setOnClickListener { finish() }
        bindingSave.tvCancelFailed.setOnClickListener {
            DataLocalManager.setLong(music.id, ID_MUSIC)
            finish()
        }
        bindingSave.tvTryAgain.setOnClickListener {
            saveRingtone(nameFile, object : ICallBackItem {
                override fun callBack(ob: Any, position: Int) {
                    Handler(Looper.getMainLooper()).post {
                        bindingSave.vLoad.visibility = View.GONE
                        if (position == 0)
                            bindingSave.apply {
                                tvTitle.text = getString(R.string.successful_music_editing)
                                ctlSuccess.visibility = View.VISIBLE
                            }
                        else if (position == -1)
                            bindingSave.apply {
                                tvTitle.text = getString(R.string.music_editing_failed)
                                ctlFailed.visibility = View.VISIBLE
                            }
                    }
                }
            })
        }
    }
}