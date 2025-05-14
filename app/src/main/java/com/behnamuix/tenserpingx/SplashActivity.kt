package com.behnamuix.tenserpingx

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.behnamuix.tenserping.Network.NetworkCheck
import com.behnamuix.tenserpingx.MyTools.MoToast

class SplashActivity : AppCompatActivity() {
    private var netCheck: Boolean = false
    private var motoast = MoToast(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        changeNavbarStyle()
        netCheck = NetworkCheck.isInternetAvailable(this)
        if (netCheck) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 400)
        } else {
            motoast.MoError(title = "ارتباط با سرور برقرار نشد", msg = "اینترنت شما متصل نیست لطفا از اپلیکیشن خارج شده و مجدد وارد شوید")
        }
    }

    private fun changeNavbarStyle() {
        val window = window
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // تغییر رنگ پس‌زمینه نوار ناوبری
        val statusBarColor = ContextCompat.getColor(this, R.color.black_mat)

        val navigationBarColor = ContextCompat.getColor(this, R.color.black_mat)
        window.navigationBarColor = navigationBarColor
        window.statusBarColor=statusBarColor
        windowInsetsController.isAppearanceLightNavigationBars = false
    }

}