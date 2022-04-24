package xyz.srcbox.lin.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.srcbox.file.R
import com.srcbox.file.util.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {
    companion object {
        var userState: Int = 1
        const val USER_STATE_LOGIN = 1
        const val USER_STATE_REGISTER = 2
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        GlobUtil.changeTitle(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun switch(id: Int) {
        val v = findViewById<LinearLayout>(id)
        when (id) {
            R.id.log_on -> {
                val otherV = findViewById<LinearLayout>(R.id.reg_on)
            }

            R.id.reg_on -> {
                val otherV = findViewById<LinearLayout>(R.id.log_on)
            }
        }
    }
}