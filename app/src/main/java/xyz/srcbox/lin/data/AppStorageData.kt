package xyz.srcbox.lin.data

import android.os.Environment
import xyz.srcbox.lin.application.EggApplication
import xyz.srcbox.lin.data.`object`.AppSetting
import java.io.File
import java.io.InputStream

class AppStorageData {


    companion object {
        fun getAppConfigStorage(): File {
            return File(EggApplication.context.filesDir, "config")
        }

        fun getFileOutFile(): File {
            return File(Environment.getExternalStorageDirectory(), AppSetting.appFileOut)
        }

        fun getAssetsIn(fileN: String): InputStream {
            return EggApplication.context.assets.open(fileN)
        }
    }
}