package xyz.srcbox.lin.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.srcbox.file.application.EggApplication
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.pow


fun toast(string: String) {
    Toast.makeText(EggApplication.context, string, Toast.LENGTH_LONG).show()
}

class EggUtil {

    companion object {
        private val MIME_MapTable =
            arrayOf(
                arrayOf(".3gp", "video/3gpp"),
                arrayOf(".apk", "application/vnd.android.package-archive"),
                arrayOf(".asf", "video/x-ms-asf"),
                arrayOf(".avi", "video/x-msvideo"),
                arrayOf(".bin", "application/octet-stream"),
                arrayOf(".bmp", "image/bmp"),
                arrayOf(".c", "text/plain"),
                arrayOf(".class", "application/octet-stream"),
                arrayOf(".conf", "text/plain"),
                arrayOf(".cpp", "text/plain"),
                arrayOf(".doc", "application/msword"),
                arrayOf(".docx", "application/msword"),
                arrayOf(".exe", "application/octet-stream"),
                arrayOf(".gif", "image/gif"),
                arrayOf(".gtar", "application/x-gtar"),
                arrayOf(".gz", "application/x-gzip"),
                arrayOf(".h", "text/plain"),
                arrayOf(".htm", "text/html"),
                arrayOf(".html", "text/html"),
                arrayOf(".jar", "application/java-archive"),
                arrayOf(".java", "text/plain"),
                arrayOf(".jpeg", "image/jpeg"),
                arrayOf(".JPEG", "image/jpeg"),
                arrayOf(".jpg", "image/jpeg"),
                arrayOf(".js", "application/x-javascript"),
                arrayOf(".log", "text/plain"),
                arrayOf(".m3u", "audio/x-mpegurl"),
                arrayOf(".m4a", "audio/mp4a-latm"),
                arrayOf(".m4b", "audio/mp4a-latm"),
                arrayOf(".m4p", "audio/mp4a-latm"),
                arrayOf(".m4u", "video/vnd.mpegurl"),
                arrayOf(".m4v", "video/x-m4v"),
                arrayOf(".mov", "video/quicktime"),
                arrayOf(".mp2", "audio/x-mpeg"),
                arrayOf(".mp3", "audio/x-mpeg"),
                arrayOf(".mp4", "video/mp4"),
                arrayOf(".mpc", "application/vnd.mpohun.certificate"),
                arrayOf(".mpe", "video/mpeg"),
                arrayOf(".mpeg", "video/mpeg"),
                arrayOf(".mpg", "video/mpeg"),
                arrayOf(".mpg4", "video/mp4"),
                arrayOf(".mpga", "audio/mpeg"),
                arrayOf(".msg", "application/vnd.ms-outlook"),
                arrayOf(".ogg", "audio/ogg"),
                arrayOf(".pdf", "application/pdf"),
                arrayOf(".png", "image/png"),
                arrayOf(".pps", "application/vnd.ms-powerpoint"),
                arrayOf(".ppt", "application/vnd.ms-powerpoint"),
                arrayOf(".pptx", "application/vnd.ms-powerpoint"),
                arrayOf(".prop", "text/plain"),
                arrayOf(".rar", "application/x-rar-compressed"),
                arrayOf(".rc", "text/plain"),
                arrayOf(".rmvb", "audio/x-pn-realaudio"),
                arrayOf(".rtf", "application/rtf"),
                arrayOf(".sh", "text/plain"),
                arrayOf(".tar", "application/x-tar"),
                arrayOf(".tgz", "application/x-compressed"),
                arrayOf(".txt", "text/plain"),
                arrayOf(".wav", "audio/x-wav"),
                arrayOf(".wma", "audio/x-ms-wma"),
                arrayOf(".wmv", "audio/x-ms-wmv"),
                arrayOf(".wps", "application/vnd.ms-works"),
                arrayOf(".xml", "text/plain"),
                arrayOf(".z", "application/x-compress"),
                arrayOf(".zip", "application/zip"),
                arrayOf("", "*/*")
            )

        /*MediaScannerConnection.scanFile(
               context,
               arrayOf(filePath),
               null,
               MediaScannerConnection.OnScanCompletedListener { path, uri ->
                   println("$path$uri")
               })*/

        fun getClipVal(context: Context): String {
            return (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip!!.description.label.toString()
        }

        fun trimClipChineseValue(context: Context): String {
            return try {
                Pattern.compile(
                    "[\u4e00-\u9fa5|\u3002|\u0023|\uff1f|\uff01|\uff0c|\u3001|\uff1b|\uff1a|\u201c|\u201d|\u2018|\u2019|\uff08|\uff09|\u300a|\u300b|\u3008|\u3009|\u3010|\u3011|\u300e|\u300f|\u300c|\u300d|\ufe43|\ufe44|\u3014|\u3015|\u2026|\u2014|\uff5e|\ufe4f|\uffe5|\uff0d\uff3f|\u002d]"
                )
                    .matcher((context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip!!.description.label)
                    .replaceAll("").trim()
            } catch (e: Exception) {
                ""
            }
        }

        fun byteToHexString(byteArray: ByteArray): String? {
            var str = ""
            if (byteArray.isEmpty()) {
                return null
            }
            byteArray.forEach { it ->
                val v: Int = it.toInt() and 0xFF
                val hx = Integer.toHexString(v)
                if (hx.length < 2) {
                    str += "0"
                }
                str += hx
            }
            return str
        }

        fun filterSpecialStr(data: String): String {
            //sb???????????????????????????
            val regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]"
            val sb = StringBuffer()
            //?????????????????????
            val p = Pattern.compile(regex)
            //?????????????????????
            val matcher: Matcher = p.matcher(data)
            //??????????????????????????????????????????
            while (matcher.find()) {
                //?????????sb??????"\r\n"??????????????????????????????????????????
                sb.append(matcher.group().toString() + "\r\n")
            }
            return sb.toString()
        }

        fun downloadFile(
            webUrl: String,
            isPhone: Boolean = false, file: File,
            onProgress: (p: Float) -> Unit
        ) {
            val okHttp2 = OkHttpClient()
            val okRequest: Request = if (isPhone) {
                Request.Builder().url(webUrl).header(
                    "user-agent",
                    "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Mobile Safari/537.36"
                ).build()
            } else {
                Request.Builder().url(webUrl).build()
            }
            val response2 = okHttp2.newCall(okRequest).execute()
            val byteS = response2.body?.byteStream()
            val buff = ByteArray(524)
            val length = response2.body!!.contentLength()
            file.parentFile?.mkdirs()
            val fos = FileOutputStream(file.absolutePath)
            var len = 0
            var sum = 0
            while ((byteS!!.read(buff).apply { len = this }) != -1) {
                fos.write(buff, 0, len)
                sum += len
                onProgress(computeProgress(sum, length))
            }
            fos.flush()
            fos.close()
            byteS.close()
            notifyDCIM(EggApplication.context, file)
        }


        fun notifyDCIM(context: Context, file: File) {
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                BitmapFactory.decodeFile(file.absolutePath),
                file.name,
                null
            )
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = Uri.fromFile(file)
            intent.data = uri
            context.sendBroadcast(intent)
        }

        private fun isChinese(c: Char): Boolean {
            val ub = Character.UnicodeBlock.of(c)
            return ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
        }

        fun unicodeToChinese(unicode: String): String? {
            val string = StringBuffer()
            val hex = unicode.split("\\\\u".toRegex()).toTypedArray()
            for (i in hex.indices) {
                try {
                    // ???????????? \u4e00-\u9fa5 (??????)
                    if (hex[i].length >= 4) { //????????????????????????????????????
                        val chinese = hex[i].substring(0, 4)
                        try {
                            val chr = chinese.toInt(16)
                            val isChinese: Boolean = isChinese(chr.toChar())
                            //??????????????????????????????  ???????????????
                            if (isChinese) { //??????????????????
                                // ?????????string
                                string.append(chr.toChar())
                                //????????????  ???????????????
                                val behindString = hex[i].substring(4)
                                string.append(behindString)
                            } else {
                                string.append(hex[i])
                            }
                        } catch (e1: NumberFormatException) {
                            string.append(hex[i])
                        }
                    } else {
                        string.append(hex[i])
                    }
                } catch (e: NumberFormatException) {
                    string.append(hex[i])
                }
            }
            return string.toString()
        }

        fun getSizeExt(size: Long): String {
            //????????????size??????1705230
            val GB = 1024 * 1024 * 1024 //??????GB???????????????
            val MB = 1024 * 1024 //??????MB???????????????
            val KB = 1024 //??????KB???????????????
            val df = DecimalFormat("0.00") //???????????????
            var resultSize = ""
            resultSize = when {
                size / GB >= 1 -> {
                    //????????????Byte??????????????????1GB
                    df.format(size / GB.toFloat()).toString() + "GB   "
                }
                size / MB >= 1 -> {
                    //????????????Byte??????????????????1MB
                    df.format(size / MB.toFloat()).toString() + "MB   "
                }
                size / KB >= 1 -> {
                    //????????????Byte??????????????????1KB
                    df.format(size / KB.toFloat()).toString() + "KB   "
                }
                else -> {
                    size.toString() + "B   "
                }
            }
            return resultSize
        }

        fun getSizeExt(size: Int): String {
            //????????????size??????1705230
            val GB = 1024 * 1024 * 1024 //??????GB???????????????
            val MB = 1024 * 1024 //??????MB???????????????
            val KB = 1024 //??????KB???????????????
            val df = DecimalFormat("0.00") //???????????????
            var resultSize = ""
            resultSize = when {
                size / GB >= 1 -> {
                    //????????????Byte??????????????????1GB
                    df.format(size / GB.toFloat()).toString() + "GB   "
                }
                size / MB >= 1 -> {
                    //????????????Byte??????????????????1MB
                    df.format(size / MB.toFloat()).toString() + "MB   "
                }
                size / KB >= 1 -> {
                    //????????????Byte??????????????????1KB
                    df.format(size / KB.toFloat()).toString() + "KB   "
                }
                else -> {
                    size.toString() + "B   "
                }
            }
            return resultSize
        }

        /**
         * ?????????????????????????????????
         *
         * @param src        ?????????
         * @param sampleSize ???????????????
         * @param recycle    ????????????
         * @return ??????????????????????????????
         */
        fun compressBySampleSize(
            src: Bitmap?,
            sampleSize: Int,
            recycle: Boolean
        ): Bitmap? {
            if (src == null || src.width == 0 || src.height == 0) {
                return null
            }
//            Log.i("yc????????????", "???????????????" + src.byteCount)
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inSampleSize = sampleSize
            val baos = ByteArrayOutputStream()
            src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val bytes = baos.toByteArray()
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            //            Log.i("yc????????????", "???????????????" + bitmap.byteCount)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        }

        /**
         * ???????????????????????????
         *
         * @param src     ?????????
         * @param quality ??????
         * @param recycle ????????????
         * @return ????????????????????????
         */
        fun compressByQuality(
            src: Bitmap?,
            quality: Int,
            recycle: Boolean
        ): Bitmap? {
            if (src == null || src.width == 0 || src.height == 0) {
                return null
            }
            val baos = ByteArrayOutputStream()
            src.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            val bytes = baos.toByteArray()
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            return bitmap
        }

        fun sectionStr(main: String, start: String, end: String): String {
            return main.let {
                it.substring(it.indexOf(start) + 1, it.lastIndexOf(end))
            }
        }

        fun computeProgress(pos: Int, size: Int): Float {
            return String.format("%.2f", (pos.toFloat() / size.toFloat()) * 100).toFloat()
        }

        fun computeProgress(pos: Int, size: Long): Float {
            return String.format("%.2f", (pos.toFloat() / size.toFloat()) * 100).toFloat()
        }

        fun getQQIconLink(qq: String): String {
            return "http://q1.qlogo.cn/g?b=qq&nk=${qq}&s=640"
        }

        //??????????????????
        fun startInstallPermissionSettingActivity(context: Context?) {
            if (context == null) {
                return
            }
            val intent = Intent()
            //????????????apk???URI???????????????intent??????????????????????????????????????????????????????????????????????????????????????????????????????
            val packageURI =
                Uri.parse("package:" + context.packageName)
            intent.data = packageURI
            //?????????????????????????????????????????????
            if (Build.VERSION.SDK_INT >= 26) { //intent = new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
                intent.action = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
            } else {
                intent.action = Settings.ACTION_SECURITY_SETTINGS
            }
            //Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            (context as Activity).startActivityForResult(
                intent,
                8
            )
        }

        private fun getMIMEType(file: File): String? {
            var type = "*/*"
            val fName = file.name
            //??????????????????????????????"."???fName???????????????
            val dotIndex = fName.lastIndexOf(".")
            if (dotIndex < 0) return type
            /* ???????????????????????? */
            val fileType = fName.substring(dotIndex, fName.length).toLowerCase(Locale.ROOT)
            if ("" == fileType) return type
            //???MIME?????????????????????????????????????????????MIME?????????
            for (i in MIME_MapTable.indices) {
                if (fileType == MIME_MapTable[i][0]) type = MIME_MapTable[i][1]
            }
            return type
        }

        //????????????
        fun openAndroidFile(context: Context, filepath: String?) {
            val intent = Intent()
            val file = File(filepath!!)
            //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//????????????
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.action = Intent.ACTION_VIEW //???????????????
            intent.setDataAndType(
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileProvider",
                    file
                ), getMIMEType(file)
            ) //????????????
            println(intent.dataString)
            context.startActivity(intent)
        }

        fun bv2av(bv: String): String {
            if (bv.substring(0, 2).toLowerCase(Locale.ROOT) != "bv") {
                return "?????????BV???"
            }
            var str = ""
            when (bv.length) {
                12 -> {
                    str = bv
                }

                10 -> {
                    str = "BV$bv"
                }

                9 -> {
                    str = "BV1$bv"
                }

                else -> {
                    return "??????????????????"
                }
            }

            var result: Double = 0.0
            val table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF"

            val add = 8728348608
            val xor = 177451812
            val sA = arrayListOf<Int>(11, 10, 3, 8, 4, 6)
            println(str)
            for (index in 0 until 6) {
                result += (table.indexOf(str[sA[index]]) * 58).toDouble().pow(index)
            }
            return "av${(result - add).toInt().xor(xor)}"
            /*if (!str.matches(Regex("/[Bb][Vv][fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF]{10}/gu"))) str =
                "??????????????????2"*/
        }


        fun getAppVersionName(context: Context): String {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }

        fun getWindowWidth(context: Context): Int {
            val point = Point()
            val winM = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            winM.defaultDisplay.getSize(point)
            return point.x
        }

        fun getWindowHeight(context: Context): Int {
            val point = Point()
            val winM = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            winM.defaultDisplay.getSize(point)
            return point.y
        }

        fun getFileNameNoEx(filename: String?): String {
            if (filename != null && filename.isNotEmpty()) {
                val dot = filename.lastIndexOf('.')
                if (dot > -1 && dot < filename.length) {
                    return filename.substring(0, dot)
                }
            }
            return ""
        }

        fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
            val bmp = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_4444
            )

            bmp.setHasAlpha(true)


            val canvas = Canvas()
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            canvas.setBitmap(bmp)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bmp
        }

        fun reNameExt(old: String, newExt: String): String {
            val oldL = old.split(".")
            val newN = old.replace(oldL[oldL.size - 1], newExt)
            if (newN == newExt) {
                return "$old.$newExt"
            }
            return newN
        }

        fun getPathExtend(name: String): String {
            return name.split(".")[name.split(".").size - 1]
        }

        @SuppressLint("SimpleDateFormat")
        fun tome2Date(time: Long): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(time))
        }


        fun toRgba(color: Int): ArrayList<Int> {
            val red = color and 0xff0000 shr 16
            val green = color and 0x00ff00 shr 8
            val blue = color and 0x0000ff
            return arrayListOf(red, green, blue)
        }

        fun goBrowser(context: Context, url: String) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(browserIntent)
        }

        fun startApp(context: Context, pack: String) {
            val intent: Intent? = context.packageManager.getLaunchIntentForPackage(pack)
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }

        fun uninstallNormal(
            context: Context,
            packageName: String?
        ): Boolean {
            if (packageName == null || packageName.isEmpty()) {
                return false
            }
            val i = Intent(
                Intent.ACTION_DELETE, Uri.parse(
                    StringBuilder().append("package:")
                        .append(packageName).toString()
                )
            )
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
            return true
        }

        fun shareFile(activity: Activity, fileUrl: File, fileName: String) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    activity,
                    "${activity.packageName}.fileProvider",
                    fileUrl
                )
            )
            shareIntent.type = "*/*"
            activity.startActivity(Intent.createChooser(shareIntent, "????????????"))
        }


        fun deletes(file: File?) {
            if (file!!.isFile) {
                file.delete()
            }
            if (file.isDirectory) {
                val childFile = file.listFiles()
                if (childFile!!.isEmpty()) {
                    file.delete()
                }
                childFile.forEach {
                    deletes(it)
                }
            }
            file.delete()
        }

        fun joinQQGroup(context: Context, key: String): Boolean {
            val intent = Intent()
            intent.data =
                Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")

            return try {
                context.startActivity(intent)
                true
            } catch (e: java.lang.Exception) {
                toast("????????????QQ")
                false
            }
        }

        private fun scanFile(context: Context, file: File) {
            context.sendBroadcast(
                Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileProvider",
                        file
                    )
                )
            )
        }

        private fun checkDirExist(file: File): Boolean {
            return if (!file.exists()) {
                file.mkdirs()
            } else {
                true
            }

        }

        fun saveFile(context: Activity, input: InputStream, file: File) {
            try {
                checkDirExist(file.parentFile!!)
                val fos = FileOutputStream(file)
                var len = 0
                val buffer = ByteArray(524)
                while (input.read(buffer).apply { len = this } != -1) {
                    fos.write(buffer, 0, len)
                }
                fos.flush()
                fos.close()
                input.close()
            } catch (e: FileNotFoundException) {
                context.runOnUiThread {
                    toast("?????????????????????")
                }
                file.parentFile?.mkdirs()
            }
        }

        fun saveDrawable(
            draw: Drawable?,
            file: File,
            context: Context? = null, isUpdate: Boolean = false
        ) {
            checkDirExist(file.parentFile!!)
            val bit = (draw as BitmapDrawable).bitmap
            val fos = FileOutputStream(file)
            bit.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            if (isUpdate) {
                scanFile(context!!, file)
            }
        }

        fun dp2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }


        fun copyText(activity: Activity, string: String) {
            val clip = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("label", string)
            clip.setPrimaryClip(clipData)
        }

        fun isOsApp(applicationInfo: ApplicationInfo): Boolean {
            if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                return true
            }
            return false
        }

        fun hideKeyboard(activity: Activity) {
            val inputMethodsMan =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodsMan.hideSoftInputFromWindow(
                activity.window.decorView.applicationWindowToken,
                0
            )
        }

        fun getFileDiffType(fkb: Int): String {
            return when {
                fkb in 1024..1048575 -> { //mb
                    "${String.format("%.2f", fkb.toFloat() / 1024.toFloat())}M"
                }
                fkb > 1048576 -> { //G
                    "${String.format("%.2f", fkb.toFloat() / 1024.toFloat() / 1024.toFloat())}G"
                }
                else -> { //kb
                    "${fkb}kb"
                }
            }
        }

        fun transStatusBar(context: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                context.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                context.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                context.window.statusBarColor = Color.TRANSPARENT;
            }
        }

        fun getStatusBarHeight(context: Context): Int {
            val resources: Resources = context.resources
            val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

        fun getStatusBarWidth(context: Context): Int {
            val resources: Resources = context.resources
            val resourceId: Int = resources.getIdentifier("status_bar_width", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

        fun <T> setAssetsIcon(context: Context, view: T, path: String) {
            if (view is ImageView) {
                view.setImageBitmap(
                    BitmapFactory.decodeStream(
                        context.applicationContext.assets.open(
                            path
                        )
                    )
                )
            }
        }


        fun cutBitmap(bit: Bitmap?, x: Int, y: Int, w: Int, h: Int): Bitmap? {
            return try {
                Bitmap.createBitmap(bit!!, x, y, w, h)
            } catch (e: Exception) {
                println(e.message)
                null
            }
        }

        fun goQQ(activity: Activity, qqNum: String) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=$qqNum&ve    rsion=1")
                )
            )
        }

        fun alterStateBarColor(activity: Activity, color: String) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.statusBarColor = Color.parseColor(color)
//        ContextCompat.getColor(activity, Color.parseColor("#5555"))
        }

        fun saveBitmapFile(bitmap: Bitmap?, path: File) {
            path.parentFile!!.mkdirs()
            val bos = BufferedOutputStream(FileOutputStream(path))
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, bos)
            bos.flush()
            bos.close()
            scanFile(path)
        }

        fun scanFile(file: File) {
            val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            scanIntent.data = Uri.fromFile(file)
            EggApplication.context.sendBroadcast(scanIntent)
        }

        fun toast(string: String) {
            Toast.makeText(EggApplication.context, string, Toast.LENGTH_LONG).show()
        }

        fun loadIcon(context: Context, color: String, vararg v: TextView) {
            val iconFont = Typeface.createFromAsset(context.assets, "font/iconfont.ttf")

            v.forEach {
                it.typeface = iconFont
                it.setTextColor(Color.parseColor(color))
            }
        }

        fun setViewRadius(
            v: View,
            color: String,
            strokeWidth: Int,
            strokeColor: String,
            float: Float
        ) {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = float
            gradientDrawable.setColor(Color.parseColor(color))
            gradientDrawable.setStroke(strokeWidth, Color.parseColor(strokeColor))
            v.background = gradientDrawable
        }

        fun getAppSHA1(context: Context, packageName: String): String? {
            try {
                @SuppressLint("PackageManagerGetSignatures") val info =
                    context.packageManager.getPackageInfo(
                        packageName, PackageManager.GET_SIGNATURES
                    )
                val cert = info.signatures[0].toByteArray()
                val md =
                    MessageDigest.getInstance("SHA1")
                val publicKey = md.digest(cert)
                val hexString = java.lang.StringBuilder()
                for (b in publicKey) {
                    val appendString = Integer.toHexString(0xFF and b.toInt())
                        .toUpperCase(Locale.US)
                    if (appendString.length == 1) hexString.append("0")
                    hexString.append(appendString)
                    hexString.append(":")
                }
                val result = hexString.toString()
                return result.substring(0, result.length - 1)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return null
        }

        fun getAppSHA256(context: Context, packageName: String): String? {
            try {
                @SuppressLint("PackageManagerGetSignatures") val info =
                    context.packageManager.getPackageInfo(
                        packageName, PackageManager.GET_SIGNATURES
                    )
                val cert = info.signatures[0].toByteArray()
                val md =
                    MessageDigest.getInstance("SHA256")
                val publicKey = md.digest(cert)
                val hexString = java.lang.StringBuilder()
                for (b in publicKey) {
                    val appendString = Integer.toHexString(0xFF and b.toInt())
                        .toUpperCase(Locale.US)
                    if (appendString.length == 1) hexString.append("0")
                    hexString.append(appendString)
                    hexString.append(":")
                }
                val result = hexString.toString()
                return result.substring(0, result.length - 1)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return null
        }

    }
}

/*
            val fmb = fkb.toFloat() / (1024).toFloat()
            if (fmb < 1 && fmb > 0) {

            }
            return ""*/