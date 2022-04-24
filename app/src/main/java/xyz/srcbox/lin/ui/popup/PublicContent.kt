package xyz.srcbox.lin.ui.popup

import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R
import www.linwg.org.lib.LCardView

class PublicContent(context: Context) : CenterPopupView(context) {
    override fun onCreate() {
        super.onCreate()
        findViewById<LCardView>(R.id.public_content_on).setOnClickListener {
            dismiss()
        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.public_content_popup
    }
}