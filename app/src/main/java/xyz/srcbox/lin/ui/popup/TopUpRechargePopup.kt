package xyz.srcbox.lin.ui.popup

import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R

class TopUpRechargePopup(context: Context) : CenterPopupView(context) {
    override fun onCreate() {
        super.onCreate()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.top_up_recharge_popup
    }
}