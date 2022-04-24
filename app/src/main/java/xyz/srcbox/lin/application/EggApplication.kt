package xyz.srcbox.lin.application

import android.app.Application
import android.content.Context
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.hjq.bar.TitleBar
import com.hjq.bar.style.TitleBarLightStyle
import xyz.srcbox.lin.data.`object`.AppSetting
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import xyz.srcbox.lin.ui.ErrorCrash
import xyz.srcbox.lin.util.SpTool


class EggApplication : Application() {
    companion object {
        lateinit var context: Context
        var isDebug = false
        var isError = true
    }

    override fun onCreate() {
        super.onCreate()
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        QbSdk.initTbsSettings(map)
        QbSdk.initX5Environment(this, null)

        TitleBar.initStyle(TitleBarLightStyle(this));
        context = applicationContext

        CaocConfig.Builder.create().errorActivity(ErrorCrash::class.java).apply()
        SpTool.setContext(applicationContext)
        val p = SpTool.getSettingString(
            "fileOutPath",
            "山盒2.0"
        )
        AppSetting.appFileOut = p
        AppSetting.colorStress = SpTool.getSettingString("themeColor", AppSetting.colorStress)

        AppSetting.colorTransTress =
            "#${SpTool.getSettingString("themeTransColor", "572626675").toInt()
                .toString(16)}"
    }
}