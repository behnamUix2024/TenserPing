package com.behnamuix.tenserpingx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.airbnb.lottie.LottieAnimationView
import com.behnamuix.tenserping.Network.NetworkCheck
import com.behnamuix.tenserpingx.Network.InternetSpeedTester
import com.behnamuix.tenserpingx.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var et_addr: EditText
    private lateinit var lav_info: LottieAnimationView
    private lateinit var tv_speed_download: TextView
    private lateinit var tv_speed_upload: TextView
    private lateinit var tv_status_ping: TextView
    private lateinit var tv_ping: TextView
    private lateinit var tv_status: TextView
    private lateinit var vw_start: ConstraintLayout
    private lateinit var networkTester: InternetSpeedTester
    private var isDialogShowing = false
    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && !NetworkCheck.isInternetAvailable(context)) {
                showNoInternetDialog()
            } else {
                dismissNoInternetDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        enableEdgeToEdge()
        setContentView(binding.root)
        changeNavbarStyle()

        config()


    }

    private fun changeNavbarStyle() {
        val window = window
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // تغییر رنگ پس‌زمینه نوار ناوبری
        val navigationBarColor = ContextCompat.getColor(this, R.color.transparent)
        window.navigationBarColor = navigationBarColor
        windowInsetsController?.isAppearanceLightNavigationBars = false
    }

    private fun config() {
        tv_status_ping=binding.tvStatusPing
        tv_ping=binding.tvPing
        tv_status=binding.tvStatus
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        lav_info=binding.lavInfo
        networkTester = InternetSpeedTester(this)
        et_addr = binding.etAddr
        tv_speed_upload = binding.tvSpeedUpload
        tv_speed_download = binding.tvSpeedDownload
        vw_start = binding.vwStart
        lav_info.setOnClickListener(){
            val dialog=AlertDialog.Builder(this)
            dialog.setMessage("تَنسَر  روحانی زردشتی در اواخرِ عصرِ اشکانی و از نزدیکان و حامیان اردشیر بابکان بود. تنسر پس از قدرت  گیری اردشیر بابکان به او پیوست و سمت هیربدانْ هیربد را — که بالاترین مقام در میان هیربدان بود — داشت. او نویسندهٔ نامهٔ تنسر به گشنسب است که اصل آن به زبان فارسی میانه بود.")
            dialog.setTitle("تنسر چه کسی بود؟")
            dialog.setNegativeButton("باشه",null)
            dialog.setPositiveButton("متن نامه تنسر به گشتاسب",
                DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent("android.intent.action.VIEW", Uri.parse("https://behnamuix2024.com/letter.html"))
                    val b = Bundle()
                    b.putBoolean("new_window", true) //sets new window
                    intent.putExtras(b)
                    startActivity(intent)
                })
            dialog.show()
            dialog.setNeutralButton("بیوگرافی توسعه دهنده",
                DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent("android.intent.action.VIEW", Uri.parse("https://behnamuix2024.com/bio.html"))
                    val b = Bundle()
                    b.putBoolean("new_window", true) //sets new window
                    intent.putExtras(b)
                    startActivity(intent)
                })
            dialog.show()
        }
        vw_start.setOnClickListener() {
           testStart()

        }
    }

    private fun testStart() {
        val website = et_addr.text.toString()
        if(!website.startsWith("https://")){
            Toast.makeText(this,"لطفا ادرس اینترنتی را با https:// شروع کنید",Toast.LENGTH_SHORT).show()
            et_addr.requestFocus()
        }else{
            val uploadUrl = "https://httpbin.org/post" // آدرس سرور آپلود خود را اینجا قرار دهید

            CoroutineScope(Dispatchers.Main).launch {
                tv_speed_download.text = "در حال تست دانلود..."
                tv_speed_upload.text = "در حال تست آپلود..."
                tv_status_ping.text = "در حال گرفتن پینگ ..."
                val pingResult = withContext(Dispatchers.IO) {
                    networkTester.ping()
                }
                tv_ping.text = if (pingResult != null) " ${pingResult} " else "خطا "
                tv_status_ping.text = "پینگ"

                val downloadSpeed = withContext(Dispatchers.IO) {
                    networkTester.getDownloadSpeed(website,3000000)
                }
                tv_speed_download.text = if (downloadSpeed != null) " ${
                    String.format(
                        "%.2f",
                        downloadSpeed
                    )
                } " else "خطا "
                Log.i("tenser",downloadSpeed.toString())
                val uploadSpeed = withContext(Dispatchers.IO) {
                    networkTester.getUploadSpeed(uploadUrl)
                }
                tv_speed_upload.text = if (uploadSpeed != null) " ${
                    String.format(
                        "%.2f",
                        uploadSpeed
                    )
                } " else "خطا "
                // برای پینگ: نیاز به پیاده سازی جداگانه دارید
                // pingTextView.text = "قابلیت پینگ با okhttp به طور مستقیم وجود ندارد."
        }

        }
    }

    private fun showNoInternetDialog() {
        if (!isDialogShowing) {
            val dialog = NoInternetDialogFragment().apply {
                setRetryListener {
                    // اقدامات لازم برای تلاش مجدد (مثلاً رفرش صفحه)
                    Toast.makeText(this@MainActivity, "تلاش مجدد...", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show(supportFragmentManager, "NoInternetDialog")
            Log.i("testing", "Show OffScreen!")
            isDialogShowing = true
        }
    }

    private fun dismissNoInternetDialog() {
        supportFragmentManager.findFragmentByTag("NoInternetDialog")?.let {
            (it as NoInternetDialogFragment).dismiss()
            isDialogShowing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver) // حذف ثبت Receiver

    }

}