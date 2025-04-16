package com.behnamuix.tenserpingx

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.behnamuix.tenserping.Network.NetworkCheck
import com.behnamuix.tenserpingx.Network.InternetSpeedTester
import com.behnamuix.tenserpingx.Network.Location.UserLocationProvider
import com.behnamuix.tenserpingx.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private val PHONE_STATUS_REQUEST_CODE = 1
    private lateinit var binding: ActivityMainBinding

    private lateinit var lav_info: LottieAnimationView
    private lateinit var tv_speed_download: TextView
    private lateinit var img_bg: ImageView
    private lateinit var img_hist: ImageView
    private lateinit var tv_ip: TextView
    private lateinit var tv_type: TextView
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
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        img_bg = binding.imgBg
        img_hist = binding.imgHist
        tv_type = binding.tvType
        tv_ip = binding.tvIp
        tv_status_ping = binding.tvStatusPing
        tv_ping = binding.tvPing
        tv_status = binding.tvStatus
        lav_info = binding.lavInfo
        networkTester = InternetSpeedTester(this)
        tv_speed_upload = binding.tvSpeedUpload
        tv_speed_download = binding.tvSpeedDownload
        vw_start = binding.vwStart
        img_hist.setOnClickListener() {
            val pay = false
            if (pay) {
                showHistDialog()

            } else {
                val payAlert = AlertDialog.Builder(this, R.style.cardAlertDialog)
                payAlert.setTitle("توجه")
                payAlert.setIcon(R.drawable.icon_pro)
                payAlert.setMessage("برای استفاده از ویژگی تاریخچه شما کاربر عزیز میتوانید از قسمت پایین اشتراک خریداری کنید")
                payAlert.setPositiveButton("خریداری اشتراک", null)
                payAlert.setNegativeButton("بی خیال", null)
                payAlert.show()
            }
        }
        lav_info.setOnClickListener() {


            val dialog = AlertDialog.Builder(this, R.style.cardAlertDialog)
            dialog.setMessage("تَنسَر  روحانی زردشتی در اواخرِ عصرِ اشکانی و از نزدیکان و حامیان اردشیر بابکان بود. تنسر پس از قدرت  گیری اردشیر بابکان به او پیوست و سمت هیربدانْ هیربد را — که بالاترین مقام در میان هیربدان بود — داشت. او نویسندهٔ نامهٔ تنسر به گشنسب است که اصل آن به زبان فارسی میانه بود.")
            dialog.setTitle("تنسر چه کسی بود؟")
            dialog.setNegativeButton("باشه", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
            dialog.setPositiveButton(
                "سیاست های حفظ حریم خصوصی",
                DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(
                        "android.intent.action.VIEW",
                        Uri.parse("https://behnamuix2024.com/letter.html")
                    )
                    val b = Bundle()
                    b.putBoolean("new_window", true) //sets new window
                    intent.putExtras(b)
                    startActivity(intent)
                })
            dialog.setNeutralButton(
                "بیوگرافی توسعه دهنده",
                DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(
                        "android.intent.action.VIEW",
                        Uri.parse("https://behnamuix2024.com/bio.html")
                    )
                    val b = Bundle()
                    b.putBoolean("new_window", true) //sets new window
                    intent.putExtras(b)
                    startActivity(intent)
                })
            dialog.show()
        }
        vw_start.setOnClickListener() {
            testStart()
            ipDetect()


        }


    }

    private fun showHistDialog() {
        val builder = AlertDialog.Builder(this).setView(R.layout.hist_dialog)
        // ... تنظیمات دیگر دیالوگ
        val dialog = builder.create()

// دسترسی به Window شیء دیالوگ
        val window = dialog.window
        if (window != null) {
            // تنظیم عرض دیالوگ (به عنوان مثال، 80% عرض صفحه)
            val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

            // تنظیم موقعیت دیالوگ (به عنوان مثال، در مرکز صفحه - پیش‌فرض)
            window.setGravity(Gravity.BOTTOM)

            // تنظیم انیمیشن ورود و خروج (اختیاری)
            // window.attributes.windowAnimations = R.style.DialogAnimation
        }

        dialog.show()
    }

    private fun ipDetect() {
        val locationProvider = UserLocationProvider(this)
        lifecycleScope.launch {
            val ip = locationProvider.getUserIPAddress()
            if (ip != null) {
                tv_ip.text = ip
            } else {
                Log.w("LocationInfo", "Failed to get IP address.")
                tv_ip.text = "متاسفانه IP پیدا نشد"
            }
            if (ActivityCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.READ_PHONE_STATE
                ) !== PackageManager.PERMISSION_GRANTED
            ) {

                reqPerm();

            } else {
                val netType = locationProvider.getNetworkType()
                tv_type.text = netType

            }


        }
    }

    private fun reqPerm() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity, Manifest.permission.READ_PHONE_STATE
            )
        ) {
            AlertDialog.Builder(this).setTitle("درخواست مجوز")
                .setMessage("برای تشخیص نوع شبکه لطفا به تنسرپینگ دسترسی بدهید").setPositiveButton(
                    "دسترسی میدم"
                ) { dialogInterface, i -> req() }.setNegativeButton(
                    "لغو"
                ) { dialogInterface, i -> dialogInterface.dismiss() }.create().show()
        } else {
            req()
        }
    }

    private fun testStart() {
        val Url = "https://httpbin.org/" // آدرس سرور آپلود خود را اینجا قرار دهید
        CoroutineScope(Dispatchers.Main).launch {
            tv_speed_download.text = "..."
            tv_speed_upload.text = "..."
            tv_status_ping.text = "در حال محاسبه سرعت پینگ ..."
            val pingResult = withContext(Dispatchers.IO) {
                networkTester.getPingSpeed()
            }
            tv_ping.text = if (pingResult != null) " ${pingResult} " else "خطا "
            tv_status_ping.text = "پینگ"

            val downloadSpeed = withContext(Dispatchers.IO) {
                networkTester.getDownloadSpeed("216.239.38.120")
            }
            tv_speed_download.text = if (downloadSpeed != null) " ${
                String.format(
                    "%.2f", downloadSpeed
                )
            } " else "خطا "
            Log.i("tenser", downloadSpeed.toString())
            val uploadSpeed = withContext(Dispatchers.IO) {
                networkTester.getUploadSpeed(Url)
            }
            tv_speed_upload.text = if (uploadSpeed != null) " ${
                String.format(
                    "%.2f", uploadSpeed
                )
            } " else "خطا "
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

    private fun req() {

        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf<String>(Manifest.permission.READ_PHONE_STATE),
            PHONE_STATUS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray, deviceId: Int
    ) {
        if (requestCode == PHONE_STATUS_REQUEST_CODE) {

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "مجوز تایید شد", Toast.LENGTH_SHORT).show();
                ipDetect()

            } else {

                Toast.makeText(this, "مجوز رد شد نوع شبکه قابل دسترسی نیست", Toast.LENGTH_SHORT)
                    .show();

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver) // حذف ثبت Receiver

    }
}