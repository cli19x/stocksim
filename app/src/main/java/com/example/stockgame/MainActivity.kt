package com.example.stockgame

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager

class MainActivity : AppCompatActivity() {


    companion object {
        const val URL = "https://cloud.iexapis.com/"
        const val URL_TWO = "https://www.alphavantage.co/"
        const val URL_THREE = "https://stocknewsapi.com/api/"
        const val APIKEY = "pk_91022ca847754dccaa4a1509ea744b83"
        const val APIKEY_TWO = "0B74GCF2CGVKWAS1"
        const val APIKEY_THREE = "q9hsv4tal5a87644e7q4bg5izxdu5prgl7ndkapg"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            kotlin.run {
                val intent = Intent(this,SecondMainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
        }, 1000)
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}

