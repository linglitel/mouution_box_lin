package xyz.srcbox.lin.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.media.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.alibaba.fastjson.JSONArray
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.lxj.xpopup.XPopup
import com.permissionx.guolindev.PermissionX
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.contract.MainContract
import com.srcbox.file.data.AppStorageData
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.data.`object`.ScreenCaptureInfo
import com.srcbox.file.ui.fragment.main_pager.FragmentFileLobby
import com.srcbox.file.ui.fragment.main_pager.FragmentHome
import com.srcbox.file.ui.fragment.main_pager.FragmentMe
import com.srcbox.file.ui.popup.CompressImagePopup
import com.srcbox.file.util.*
import kotlinx.android.synthetic.main.activity_main.*
import me.rosuh.filepicker.config.FilePickerManager
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), MainContract.View {
    private var transaction: FragmentTransaction? = null
    private var currentPageInt = 0
    private var isAnimRunning = false
    private var fragmentHome: FragmentHome? = null
    private var fragmentFileLobby: FragmentFileLobby? = null
    private var fragmentMe: FragmentMe? = null


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionX.init(this)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, grantedList, deniedList ->
                if (!allGranted) {
                    Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show()
                }
            }


        GlobUtil.changeTitle(this, false)
        fragmentHome = FragmentHome()
        fragmentFileLobby = FragmentFileLobby()
        fragmentMe = FragmentMe()
        initEvent()
        initUi()
        switchNav(img_home_bg)
        GlobUtil.signAProtocol(this)

        if (EggApplication.isDebug) {
            EggUtil.toast("??????DeBug??????")
        }
    }

    override fun onRestart() {
        super.onRestart()
        initUi()
    }

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ScreenCaptureInfo.CODE -> {
                if (data != null) {
                    if (!ScreenCaptureInfo.isStart) {
                        FloatWin(this).show()
                    }

                    ScreenCaptureInfo.intentData = data
                    ScreenCaptureInfo.resultCode = resultCode
                    val intent = Intent(this, ScreenCaptureUtil::class.java)
                    if (Build.VERSION.SDK_INT >= 26) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                } else {
                    EggUtil.toast("????????????????????????")
                }
            }

            0x110 -> {
                if (requestCode == 0x110 && resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    val filePathColumn =
                        arrayOf<String>(MediaStore.Images.Media.DATA)
                    val cursor: Cursor = contentResolver?.query(
                        selectedImage!!,
                        filePathColumn, null, null, null
                    )!!
                    cursor.moveToFirst()
                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath: String = cursor.getString(columnIndex)
                    cursor.close()
                    CompressImagePopup.file = File(picturePath)
                    println("???????????????????????????${File(picturePath)}")
                    EggUtil.toast("?????????")
                }
            }

            5 -> {
                val asT = XPopup.Builder(this).asLoading("?????????...")
                asT.show()
                val list = FilePickerManager.obtainData()
                if (list.isNotEmpty()) {
                    val oneStr = list[0]
                    val duration = getDuration(oneStr)
//                    val fFmpegAsyncUtils = FFmpegAsyncUtils()
                    val outF = File(
                        AppStorageData.getFileOutFile(),
                        "???????????????/${EggUtil.getFileNameNoEx(File(oneStr).name)}.mp3"
                    ).absolutePath
                    File(outF).apply {
                        if (exists()) delete()
                        parentFile?.let { if (!it.exists()) it.mkdirs() }
                    }
                    thread {
//                        convertToMP3(File(oneStr), File(outF))
//                        exactorMedia(File(oneStr))
                        extractAudio(oneStr, outF)


                        runOnUiThread {
                            asT.setTitle("????????????")
                            asT.delayDismissWith(1500) {
                                XPopup.Builder(this@MainActivity)
                                    .asConfirm("????????????", "???????????????????????????${outF}", null).show()
                            }
                        }
                    }

                } else {
                    asT.dismiss()
                }
            }

            6 -> {
                val asT = XPopup.Builder(this).asLoading("?????????...")
                asT.show()
                val list = FilePickerManager.obtainData()
                if (list.isNotEmpty()) {
                    val oneStr = list[0]
                    val outF = File(
                        AppStorageData.getFileOutFile(),
                        "????????????/${EggUtil.getFileNameNoEx(File(oneStr).name)}.mp4"
                    ).absolutePath
                    File(outF).apply {
                        if (exists()) delete()
                        parentFile?.let { if (!it.exists()) it.mkdirs() }
                    }
                    thread {
                        extractVideo(oneStr, outF)
                        runOnUiThread {
                            asT.setTitle("????????????")
                            asT.delayDismissWith(1500) {
                                XPopup.Builder(this@MainActivity)
                                    .asConfirm("????????????", "???????????????????????????${outF}", null).show()
                            }
                        }
                    }
                } else {
                    asT.dismiss()
                }
            }
        }
    }


    private fun extractVideo(sourcePath: String, outPath: String) {
        val mediaExtractor = MediaExtractor()
        var mediaMuxer: MediaMuxer? = null
        try {
            // ???????????????
            mediaExtractor.setDataSource(sourcePath)
            // ???????????? ID
            var videoIndex = -1
            // ????????????????????????
            var mediaFormat: MediaFormat? = null
            // ???????????????????????????????????????????????????????????????
            val trackCount = mediaExtractor.trackCount
            // ????????????????????????????????????????????????
            for (i in 0 until trackCount) {
                val format = mediaExtractor.getTrackFormat(i)
                val mimeType = format.getString(MediaFormat.KEY_MIME)
                // ???????????????????????????
                if (mimeType!!.startsWith("video/")) {
                    videoIndex = i
                    mediaFormat = format
                    break
                }
            }
            if (mediaFormat == null) {
                return
            }

            // ????????????????????????
            val maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            // ????????????
            val mimeType = mediaFormat.getString(MediaFormat.KEY_MIME)
            // ??????????????????
            var bitRate = 0
            if (mediaFormat.containsKey(MediaFormat.KEY_BIT_RATE)) {
                bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE)
            }
            // ????????????
            val width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH)
            // ????????????
            val height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT)
            // ??????????????????????????????????????????
            val duration = mediaFormat.getLong(MediaFormat.KEY_DURATION)
            // ???????????????
            val frameRate = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
            // ????????????????????????
            val colorFormat = -1
            if (mediaFormat.containsKey(MediaFormat.KEY_COLOR_FORMAT)) {
                mediaFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT)
            }
            // ???????????????????????????
            var iFrameInterval = -1
            if (mediaFormat.containsKey(MediaFormat.KEY_I_FRAME_INTERVAL)) {
                iFrameInterval = mediaFormat.getInteger(MediaFormat.KEY_I_FRAME_INTERVAL)
            }
            //  ???????????????????????????
            var rotation = -1
            if (mediaFormat.containsKey(MediaFormat.KEY_ROTATION)) {
                rotation = mediaFormat.getInteger(MediaFormat.KEY_ROTATION)
            }
            // ???????????????
            var bitRateMode = -1
            if (mediaFormat.containsKey(MediaFormat.KEY_BITRATE_MODE)) {
                bitRateMode = mediaFormat.getInteger(MediaFormat.KEY_BITRATE_MODE)
            }
            /* logger.info(
                 "mimeType:{}, maxInputSize:{}, bitRate:{}, width:{}, height:{}" +
                         ", duration:{}ms, frameRate:{}, colorFormat:{}, iFrameInterval:{}" +
                         ", rotation:{}, bitRateMode:{}",
                 mimeType,
                 maxInputSize,
                 bitRate,
                 width,
                 height,
                 duration / 1000,
                 frameRate,
                 colorFormat,
                 iFrameInterval,
                 rotation,
                 bitRateMode
             )*/
            //?????????????????????
            mediaExtractor.selectTrack(videoIndex)
            mediaMuxer = MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            //????????????????????? MediaMuxer????????????????????????
            val trackIndex = mediaMuxer.addTrack(mediaFormat)
            val byteBuffer = ByteBuffer.allocate(maxInputSize)
            val bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
            // ????????????
            mediaMuxer.start()
            while (true) {
                // ???????????????????????????????????????????????????????????????
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                //  ?????????????????????????????????????????????
                if (readSampleSize < 0) {
                    mediaExtractor.unselectTrack(videoIndex)
                    break
                }
                // ????????????????????????
                bufferInfo.size = readSampleSize
                bufferInfo.offset = 0
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.presentationTimeUs = mediaExtractor.sampleTime
                //??????????????????
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo)
                //???????????????????????????????????????
                mediaExtractor.advance()
            }
        } catch (e: IOException) {

        } finally {
            if (mediaMuxer != null) {
                try {
                    mediaMuxer.stop()
                    mediaMuxer.release()
                } catch (e: java.lang.IllegalStateException) {

                }
            }
            mediaExtractor.release()
        }
    }

    private fun extractAudio(sourcePath: String, outPath: String) {
        val mediaExtractor = MediaExtractor()
        var mediaMuxer: MediaMuxer? = null
        try {
            mediaExtractor.setDataSource(sourcePath)
            val trackCount = mediaExtractor.trackCount
            var mediaFormat: MediaFormat? = null
            var audioIndex = -1
            for (i in 0 until trackCount) {
                val format = mediaExtractor.getTrackFormat(i)
                val mimeType = format.getString(MediaFormat.KEY_MIME)
                if (mimeType!!.startsWith("audio/")) {
                    audioIndex = i
                    mediaFormat = format
                    break
                }
            }
            if (mediaFormat == null) {
                return
            }
            // MediaFormat ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
            // MediaFormat ???????????? key ????????????????????????????????????????????????????????? key ?????????????????????????????????
            // ????????????????????????
            val maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            // ??????
            val mimeType = mediaFormat.getString(MediaFormat.KEY_MIME)
            // ?????????
            val bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE)
            // ?????????
            val channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            // ?????????
            val sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            // ??????????????????????????????????????????
            val duration = mediaFormat.getLong(MediaFormat.KEY_DURATION)
            /*logger.info(
                "maxInputSize:{}, mimeType:{}, bitRate:{}, channelCount:{}" +
                        ", sampleRate:{}, duration:{}ms",
                maxInputSize,
                mimeType,
                bitRate,
                channelCount,
                sampleRate,
                duration / 1000
            )*/
            mediaExtractor.selectTrack(audioIndex)
            mediaMuxer = MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val trackIndex = mediaMuxer.addTrack(mediaFormat)
            val byteBuffer = ByteBuffer.allocate(maxInputSize)
            val bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
            mediaMuxer.start()
            while (true) {
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleSize < 0) {
                    mediaExtractor.unselectTrack(audioIndex)
                    break
                }
                bufferInfo.size = readSampleSize
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.offset = 0
                bufferInfo.presentationTimeUs = mediaExtractor.sampleTime
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo)
                mediaExtractor.advance()
            }
        } catch (e: IOException) {
        } finally {
            if (mediaMuxer != null) {
                try {
                    mediaMuxer.stop()
                    mediaMuxer.release()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
            mediaExtractor.release()
        }
    }


    fun getDuration(videoPath: String?): Int {
        return try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(videoPath)
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private fun initUi() {
        EggUtil.loadIcon(this, AppSetting.colorStress, img_home, img_file_lobby, img_me)
        EggUtil.loadIcon(this, AppSetting.colorStress, search_icon, img_file_lobby, img_me)
        EggUtil.setViewRadius(
            img_file_lobby_bg,
            AppSetting.colorTransTress,
            1,
            AppSetting.colorTransTress,
            60f
        )

        EggUtil.setViewRadius(
            img_home_bg,
            AppSetting.colorTransTress,
            1,
            AppSetting.colorTransTress,
            60f
        )

        EggUtil.setViewRadius(
            img_me_bg,
            AppSetting.colorTransTress,
            1,
            AppSetting.colorTransTress,
            60f
        )
        img_file_lobby_bg.visibility = View.GONE
        img_me_bg.visibility = View.GONE
    }

    private fun initEvent() {
        switchFragment(fragmentHome!!)
        main_home_on.setOnClickListener {
            println(isAnimRunning)
            if (isAnimRunning) return@setOnClickListener
            if (currentPageInt == 0) return@setOnClickListener
            switchFragment(fragmentHome!!)
            switchNav(img_home_bg)
            currentPageInt = 0
        }

        main_file_lobby_on.setOnClickListener {
            if (isAnimRunning) return@setOnClickListener
            if (currentPageInt == 1) return@setOnClickListener
            switchFragment(fragmentFileLobby!!)
            switchNav(img_file_lobby_bg)
            currentPageInt = 1
        }

        main_me_on.setOnClickListener {
            if (isAnimRunning) return@setOnClickListener
            if (currentPageInt == 2) return@setOnClickListener
            switchFragment(fragmentMe!!)
            switchNav(img_me_bg)
            currentPageInt = 2
        }


        search_edit.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val ja = JSONArray()
                ja.add("all")
                AppList.typeAppsMessage = ja
                val intent = Intent(this, AppList::class.java)
                intent.putExtra("search_app_name", search_edit.text.toString().replace(" ",""))
                startActivity(intent)
                EggUtil.hideKeyboard(this)
            }
            false
        }
        search_icon.setOnClickListener {
            val ja = JSONArray()
            ja.add("all")
            AppList.typeAppsMessage = ja
            val intent = Intent(this, AppList::class.java)
            intent.putExtra("search_app_name", search_edit.text.toString())
            startActivity(intent)
        }
    }

    private fun switchFragment(targetFragment: Fragment) {
        supportFragmentManager.popBackStack(null, 1)
        transaction = supportFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.slide_left_in,
            R.anim.slide_right_out,
            R.anim.slide_right_in,
            R.anim.slide_left_out
        )
        transaction!!.replace(R.id.main_fragment_view, targetFragment).commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val home = Intent(Intent.ACTION_MAIN)
            home.addCategory(Intent.CATEGORY_HOME)
            startActivity(home)
        }
        return true
    }

    private fun setNavAnimStyle(v: View, isIn: Boolean) {
        val duration: Long = 500
        if (isIn) {
            YoYo.with(Techniques.SlideInUp).duration(duration).onStart {
                isAnimRunning = true
                v.visibility = View.VISIBLE
            }.onEnd {
                isAnimRunning = false
            }.playOn(v)
        } else {
            YoYo.with(Techniques.SlideOutDown).duration(duration).onStart {
                isAnimRunning = true
            }.onEnd {
                isAnimRunning = false
                v.visibility = View.GONE
            }.playOn(v)
        }
    }

    private fun switchNav(v: View) {
        elseHideNav(v)
    }

    private fun elseHideNav(v: View) {
        var currI = 0
        val arrayListBg = arrayListOf<View>(img_home_bg, img_file_lobby_bg, img_me_bg)
        val arrayList = arrayListOf<TextView>(img_home, img_file_lobby, img_me)
        arrayListBg.forEach {
            if (v.id != it.id) {
                setNavAnimStyle(it, false)
                arrayList[currI].setTextColor(Color.parseColor(AppSetting.colorGray))
            } else {
                setNavAnimStyle(v, true)
                arrayList[currI].setTextColor(Color.parseColor(AppSetting.colorStress))
            }
            currI++
        }
    }
}