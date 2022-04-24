package xyz.srcbox.lin.ui.popup

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.TextView
import com.lxj.xpopup.core.CenterPopupView
import com.srcbox.file.R
import com.srcbox.file.util.EggUtil
import okhttp3.*
import www.linwg.org.lib.LCardView
import java.util.regex.Matcher
import java.util.regex.Pattern


class Bv2AvPopup(context: Context) : CenterPopupView(context) {
    override fun onCreate() {
        super.onCreate()
        var re = findViewById<TextView>(R.id.re)
        val bv2AvText = findViewById<TextView>(R.id.bv2av_text)
        findViewById<LCardView>(R.id.bv2av_on).setOnClickListener {
            bv2AvText.text = "转换中"
            val bvNum = findViewById<TextView>(R.id.bv_edit).text.toString()
            if (bvNum.isEmpty()){
                EggUtil.toast("不能为空")
            }
            try {
                re.text= xyz.srcbox.lin.ui.util.Bv2av().b2v(bvNum)
                re.setOnClickListener {
                    val clipboardManager: ClipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Lable", xyz.srcbox.lin.ui.util.Bv2av()
                        .b2v(bvNum))
                    clipboardManager.setPrimaryClip(clipData)
                }
            }catch (e : Exception){
                re.text = "Error"
            }


        }
    }

    override fun getImplLayoutId(): Int {
        return R.layout.bv_av_popup
    }

    fun isContainChinese(str: String?): Boolean {
        val p: Pattern = Pattern.compile("[\u4e00-\u9fa5]")
        val m: Matcher = p.matcher(str!!)
        return m.find()
    }
}