package xyz.srcbox.lin.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.srcbox.file.R
import com.srcbox.file.util.EggUtil
import com.srcbox.file.util.GlobUtil
import kotlinx.android.synthetic.main.about.*

class AboutActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
        GlobUtil.changeTitle(this)
        Glide.with(this).load("http://q1.qlogo.cn/g?b=qq&nk=1970284668&s=640")
            .placeholder(R.mipmap.placeholder).error(R.mipmap.error).into(about_egg_icon)
        Glide.with(this).load("http://q1.qlogo.cn/g?b=qq&nk=3441152376&s=640")
            .placeholder(R.mipmap.placeholder).error(R.mipmap.error).into(about_wugui_icon)
        Glide.with(this).load("http://q1.qlogo.cn/g?b=qq&nk=1720794953&s=640")
            .placeholder(R.mipmap.placeholder).error(R.mipmap.error).into(about_san_icon)
        about_egg_blog.setOnClickListener {
            EggUtil.goBrowser(this, "https://kaodan.gitee.io")
        }

        about_wugui_blog.setOnClickListener {
            EggUtil.goBrowser(this, "http://rate520.cn")

        }
        ta.setText("基于BSD开源协议发行\n" +
                "Base ON 山盒\n" +
                "Fix BV TO AV\n")
        GlobUtil.changeTheme(about_egg_blog, about_wugui_blog)
    }
}