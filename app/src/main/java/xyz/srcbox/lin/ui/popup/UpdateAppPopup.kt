package xyz.srcbox.lin.ui.popup

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.lxj.xpopup.core.BottomPopupView
import com.srcbox.file.R
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.update_app_popup.view.*

class UpdateAppPopup(context: Context) : BottomPopupView(context) {
    private var versionName = ""
    private var appLink = ""
    override fun onCreate() {
        super.onCreate()
        val updateBtnText = findViewById<TextView>(R.id.update_btn_text)
    }

    override fun getImplLayoutId(): Int {
        return R.layout.update_app_popup
    }

    @SuppressLint("SetTextI18n")
    fun setContent(version: String, content: String, newLink: String) {
        versionName = version
        appLink = newLink
        findViewById<TextView>(R.id.ver_c).text =
            "${EggUtil.getAppVersionName(context)} -> $version"
        findViewById<TextView>(R.id.hint_ver_update_content).text =
            content
    }
}