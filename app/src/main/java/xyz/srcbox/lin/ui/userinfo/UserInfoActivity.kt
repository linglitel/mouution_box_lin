package xyz.srcbox.lin.ui.userinfo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.leancloud.AVException
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.alibaba.fastjson.JSON
import com.lxj.xpopup.XPopup
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.data.`object`.AppSetting
import com.srcbox.file.util.*
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlin.concurrent.thread

class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        GlobUtil.changeTitle(this)
        val mTencent = Tencent.createInstance(AppSetting.QQ_APP_ID, this)

        nameT.text = AVUser.currentUser().username
        name.setOnClickListener {
            XPopup.Builder(this).asInputConfirm("输入新昵称", null) {
                val avUser = AVUser.currentUser()
                avUser.username = it
                avUser.saveInBackground().subscribe(object : Observer<AVObject> {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(t: AVObject) {
//                        ToastUtil(this@UserInfoActivity).longShow("成功")
                        nameT.text = it
                    }

                    override fun onError(e: Throwable) {
                        println(e.message)
                        ToastUtil(this@UserInfoActivity).longShow("${e.message}")
                    }
                })
            }.show()
        }

        email.setOnClickListener {
            if (AVUser.currentUser().email != null) {
                XPopup.Builder(this).asCustom(UserInfoEmailPopup(this, emailT)).show()
            } else {
                bindEmail()
            }
        }



        Member.getVipDate {
            when (it) {
                "0" -> {
                    vipT.text = "普通用户"
                }

                "-1" -> {
                    vipT.text = "永久VIP用户"
                }

                else -> {
                    vipT.text = it
                }
            }
        }
    }


    private fun bindEmail() {
        XPopup.Builder(this).asInputConfirm("绑定新邮箱", null) {
            val avUser = AVUser.currentUser()
            avUser.email = it
            avUser.saveInBackground().subscribe(object : Observer<AVObject> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: AVObject) {
                    emailT.text = it
                    thread {
                        AVUser.requestEmailVerifyInBackground(it).blockingSubscribe()
                    }
                }

                override fun onError(e: Throwable) {
                    println(e.message)
                    ToastUtil(this@UserInfoActivity).longShow("${e.message}")
                }
            })
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    inner class QQLoginUiListener : IUiListener {
        override fun onComplete(p0: Any) {
            val json = JSON.parseObject(p0.toString())

            LeanQQUtil.result(json, true, object : Observer<AVUser> {
                override fun onComplete() {
                    println("QQ登录完成")
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: AVUser) {
                    println("QQ登录成功了")
                    qqT.text =
                        "解除QQ绑定"
//                    t.getAVObject<S>()
                }

                override fun onError(e: Throwable) {
                    if (AVException(e).code == 137) {
                        ToastUtil(EggApplication.context).longShow("此QQ已绑定")
                    } else {
                        ToastUtil(EggApplication.context).longShow("QQ登录发送错误${e.message}")
                    }
                }
            })
        }

        override fun onCancel() {
            ToastUtil(EggApplication.context).longShow("QQ登录已关闭")

        }

        override fun onWarning(p0: Int) {

        }

        override fun onError(p0: UiError?) {
            ToastUtil(EggApplication.context).longShow("QQ登录发送错误${p0?.errorMessage}")
        }
    }
}