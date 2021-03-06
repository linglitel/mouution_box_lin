package xyz.srcbox.lin.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import com.srcbox.file.R
import com.srcbox.file.application.EggApplication
import com.srcbox.file.util.EggUtil
import kotlinx.android.synthetic.main.activity_welcome.*
import okhttp3.*
import java.lang.Exception

class WelcomeActivity : AppCompatActivity() {
    var isJump = false
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)/*
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_welcome)



        try {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = Color.TRANSPARENT;
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } catch (e: Exception) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
    }

    class DepthPageTransformer : ViewPager.PageTransformer {
        override fun transformPage(
            view: View,
            position: Float
        ) {
            Log.d("pos", position.toString())
            val pageWidth = view.width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    view.alpha = 1f
                    view.translationX = 0f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    view.alpha = 1 - position
                    // Counteract the default slide transition
                    view.translationX = pageWidth * -position
                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = (MIN_SCALE
                            + (1 - MIN_SCALE) * (1 - Math.abs(
                        position
                    )))
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.alpha = 0f
                }
            }
        }

        companion object {
            private const val MIN_SCALE = 0.75f
        }
    }

    class ZoomOutPageTransformer : ViewPager.PageTransformer {
        @SuppressLint("NewApi")
        override fun transformPage(
            view: View,
            position: Float
        ) {
            val pageWidth = view.width
            val pageHeight = view.height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.alpha = 0f
                }
                position <= 1 //a????????????b??? ??? a?????? 0.0 -1 ???b??????1 ~ 0.0
                -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    val scaleFactor = Math.max(
                        MIN_SCALE,
                        1 - Math.abs(position)
                    )
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2
                    if (position < 0) {
                        view.translationX = horzMargin - vertMargin / 2
                    } else {
                        view.translationX = -horzMargin + vertMargin / 2
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor

                    // Fade the page relative to its size.
                    view.alpha = (MIN_ALPHA + (scaleFactor - MIN_SCALE)
                            / (1 - MIN_SCALE) * (1 - MIN_ALPHA))
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.alpha = 0f
                }
            }
        }

        companion object {
            private const val MIN_SCALE = 0.85f
            private const val MIN_ALPHA = 0.5f
        }
    }

    class HorizontalStackTransformerWithRotation(private val boundViewPager: ViewPager) :
        ViewPager.PageTransformer {
        private val offscreenPageLimit: Int = boundViewPager.offscreenPageLimit
        override fun transformPage(
            view: View,
            position: Float
        ) {
            val pagerWidth = boundViewPager.width
            val horizontalOffsetBase: Float =
                (pagerWidth - pagerWidth * CENTER_PAGE_SCALE) / 2 / offscreenPageLimit + EggUtil.dp2px(
                    EggApplication.context, 15f
                )
            /*if (position >= offscreenPageLimit || position <= -1) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }*/
            if (position >= 0) {
                val translationX = (horizontalOffsetBase - view.width) * position
                view.translationX = translationX
            }
            if (position > -1 && position < 0) {
                val rotation = position * 30
                view.rotation = rotation
                view.alpha = position * position * position + 1
            } else if (position > offscreenPageLimit - 1) {
                view.alpha = (1 - position + Math.floor(position.toDouble())).toFloat()
            } else {
                view.rotation = 0f
                view.alpha = 1f
            }
            if (position == 0f) {
                view.scaleX = CENTER_PAGE_SCALE
                view.scaleY = CENTER_PAGE_SCALE
            } else {
                val scaleFactor = Math.min(
                    CENTER_PAGE_SCALE - position * 0.1f,
                    CENTER_PAGE_SCALE
                )
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
            }

            // test code: view????????????????????????tag
            //        LogUtil.e("viewTag" + tag, "viewTag: " + (String) view.getTag() + " --- transformerPosition: " + position + " --- floor: " + Math.floor(position) + " --- childCount: "+ boundViewPager.getChildCount());
            ViewCompat.setElevation(view, (offscreenPageLimit - position) * 5)
        }

        companion object {
            private const val CENTER_PAGE_SCALE = 0.8f
        }

    }
}