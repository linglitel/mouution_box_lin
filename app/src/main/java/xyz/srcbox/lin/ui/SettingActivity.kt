package xyz.srcbox.lin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.SimpleCallback
import com.srcbox.file.R
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.ui.popup.ChangeThemePopup
import com.srcbox.file.ui.popup.SelectFilePopup
import com.srcbox.file.ui.popup.SignAProtocolPopup
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        initUi()
        //加群按钮单击事件
        jump_qun_container.setOnClickListener {
            EggUtil.joinQQGroup(this, AppSetting.QUN_KEY)
        }

        //修改存储路径单击事件
        change_out_file.setOnClickListener {
            XPopup.Builder(this).asCustom(SelectFilePopup(this)).show()
        }

        //修改主题单击事件
        change_theme.setOnClickListener {
            val x = XPopup.Builder(this).setPopupCallback(object : SimpleCallback() {
                override fun onDismiss() {
                    super.onDismiss()
                    initUi()
                }
            }).asCustom(ChangeThemePopup(this)).show()

        }

        /*showProtocol.setOnClickListener {
            GlobUtil.signAProtocol(this, true)
        }
*/
        userXy.setOnClickListener {
            val signAProtocolPopup = SignAProtocolPopup(this)
            signAProtocolPopup.XY_TYPE = SignAProtocolPopup.TYPE_USER
            XPopup.Builder(this)
                .asCustom(signAProtocolPopup)
                .show()
        }

        privateXy.setOnClickListener {
            val signAProtocolPopup = SignAProtocolPopup(this)
            signAProtocolPopup.XY_TYPE = SignAProtocolPopup.TYPE_PRIVATE
            XPopup.Builder(this)
                .asCustom(signAProtocolPopup)
                .show()
        }
    }

    override fun onRestart() {
        super.onRestart()
        initUi()
    }

    private fun initUi() {
        GlobUtil.changeTitle(this)
        EggUtil.loadIcon(this, AppSetting.colorStress, update_link)
        EggUtil.loadIcon(this, AppSetting.colorStress, jump_qun)
        EggUtil.loadIcon(this, AppSetting.colorGray, setting_font)
        EggUtil.loadIcon(this, AppSetting.colorStress, setting_theme)
    }
}