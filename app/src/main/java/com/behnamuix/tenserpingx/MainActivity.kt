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
import com.behnamuix.tenserpingx.Dialog.HistoryDialogFragment
import com.behnamuix.tenserpingx.MyTools.MoToast
import com.behnamuix.tenserpingx.Network.InternetSpeedTester
import com.behnamuix.tenserpingx.Network.Location.UserLocationProvider
import com.behnamuix.tenserpingx.Retrofit.ApiResponse
import com.behnamuix.tenserpingx.Retrofit.RetrofitClient
import com.behnamuix.tenserpingx.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import androidx.core.net.toUri
import com.behnamuix.tenserpingx.PremiumAcc.MyketHelper
import ir.myket.billingclient.BuildConfig
import ir.myket.billingclient.IabHelper
import ir.myket.billingclient.util.IabResult


class MainActivity : AppCompatActivity() {
    private lateinit var myketHelper: MyketHelper
    var perm: Boolean = false
    private lateinit var motoast: MoToast
    private var DATE = ""
    private var IP = ""
    private var NET_TYPE = ""
    private var PING_SPEED = ""
    private var DOWN_SPEED = ""
    private var UP_SPEED = ""
    private val PHONE_STATUS_REQUEST_CODE = 1
    private lateinit var binding: ActivityMainBinding
    private lateinit var lav_info: LottieAnimationView
    private lateinit var tv_speed_download: TextView
    private lateinit var btn_save_hist: MaterialButton
    private lateinit var img_bg: ImageView
    private lateinit var img_comment: ImageView
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
        windowInsetsController.isAppearanceLightNavigationBars = false
    }


    private fun config() {
        myketHelper = MyketHelper(this)
        motoast = MoToast(this)
        btn_save_hist = binding.btnSaveHist
        img_comment = binding.imgComment
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
        img_comment.setOnClickListener {
            val intent = Intent(this, CommentWebViewActivity::class.java)
            startActivity(intent)
        }
        img_hist.setOnClickListener {

            if (perm) {
                showHistDialog()

            } else {
                val payAlert = AlertDialog.Builder(this, R.style.cardAlertDialog)
                payAlert.setTitle(R.string.pay_alert_title)
                payAlert.setIcon(R.drawable.icon_pro)
                payAlert.setMessage(R.string.pay_alert_msg)
                payAlert.setPositiveButton(R.string.pay_alert_btn_positive_text) { _, _ ->
                    pay()
                }
                payAlert.setNegativeButton(R.string.pay_alert_btn_negative_text, null)
                payAlert.show()
            }
        }
        lav_info.setOnClickListener {


            val dialog = AlertDialog.Builder(this, R.style.cardAlertDialog)
            dialog.setMessage(R.string.info_dialog_msg)
            dialog.setTitle(R.string.info_dialog_title)
            dialog.setNegativeButton("باشه") { dialog, _ ->
                dialog.dismiss()
            }
            dialog.setPositiveButton(
                "سیاست های حفظ حریم خصوصی"
            ) { _, _ ->
                val intent = Intent(
                    "android.intent.action.VIEW",
                    "https://behnamuix2024.com/api/policy.html".toUri()
                )
                val b = Bundle()
                b.putBoolean("new_window", true) //sets new window
                intent.putExtras(b)
                startActivity(intent)
            }
            dialog.setNeutralButton(
                "درباره ما"
            ) { _, _ ->
                val intent = Intent(
                    "android.intent.action.VIEW",
                    "https://behnamuix2024.com/api/bio.html".toUri()
                )
                val b = Bundle()
                b.putBoolean("new_window", true) //sets new window
                intent.putExtras(b)
                startActivity(intent)
            }
            dialog.show()
        }
        vw_start.setOnClickListener {
            testStart()
            ipDetect()
            DATE = getDate()


        }
        btn_save_hist.setOnClickListener {
            if (PING_SPEED != "") {
                getHistData()

            } else {
                motoast.MoWarning(msg = " داده ها بارگزاری نشده اند لطفا بر روی شروع ضربه بزنید!")
            }

        }


    }

    private fun pay() {
       if(configMyketHelperCheck()){
           myketHelper.purchasedItem(this, MyketHelper.SKU_PREM_ACC) { success ->
               if (success) {
                   showHistDialog()

               } else {
                   motoast.MoWarning(msg = "خرید انجام نشد!")
               }
           }
       }


    }

    private fun configMyketHelperCheck(): Boolean {
        var b:Boolean=false
        myketHelper.setupBiling { success ->
            motoast.MoSuccess(msg = "سرویس متصل است")
            b=true

            if (!success) {
                motoast.MoError(msg = "سرویس متصل نیست")
                b=false

            }
        }
        return b
    }

    private fun getHistData() {
        DATE = getDate()
        val builder1 = AlertDialog.Builder(this, R.style.cardAlertDialog)
        builder1.setMessage("آیا شما میخواهید داده ها در تاریخچه ذخیره شود؟")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "بله"
        ) { _, _ -> insertToHistDb(DATE, IP, NET_TYPE, PING_SPEED) }

        builder1.setNegativeButton(
            "خیر"
        ) { dialog, _ -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()

    }

    private fun insertToHistDb(date: String, ip: String, netType: String, pingSpeed: String) {

        Log.d("ALPHA", "$date$ip/$netType/$pingSpeed / $DOWN_SPEED ")
        val call = RetrofitClient.apiService.sendHist(
            date, netType, ip, pingSpeed
        )
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.status.toString() == "success") {

                        motoast.MoSuccess(msg = "داده ها در قسمت تاریخچه ذخیره شدند")


                    } else {
                        motoast.MoError(msg = "مشکلی در دریافت اطلاعات وجود دارد")


                    }

                } else {
                    motoast.MoError(msg = "مشکلی در دریافت اطلاعات وجود دارد")

                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                motoast.MoError(msg = "مشکلی در دریافت اطلاعات وجود دارد")


            }

        })


    }


    private fun getDate(): String {
        val dte = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran")).time
        val formatter = SimpleDateFormat("yyyy/MM/dd  HH:mm:ss", Locale.getDefault())
        val date = formatter.format(dte)
        return date
    }

    private fun showHistDialog() {
        val dialog = HistoryDialogFragment().apply {}
        dialog.show(supportFragmentManager, "History")
    }

    private fun ipDetect() {
        val locationProvider = UserLocationProvider(this)
        lifecycleScope.launch {
            val ip = locationProvider.getUserIPAddress()
            if (ip != null) {
                tv_ip.text = ip
                IP = ip
            } else {
                Log.w("LocationInfo", "Failed to get IP address.")
                tv_ip.text = resources.getString(R.string.ip_detect_no_value)
            }
            if (ActivityCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.READ_PHONE_STATE
                ) !== PackageManager.PERMISSION_GRANTED
            ) {

                reqPerm()

            } else {
                val netType = locationProvider.getNetworkType()
                tv_type.text = netType
                NET_TYPE = netType

            }


        }

    }

    private fun reqPerm() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity, Manifest.permission.READ_PHONE_STATE
            )
        ) {
            AlertDialog.Builder(this).setTitle(R.string.dialog_req_perm_title)
                .setMessage(R.string.pay_alert_msg).setPositiveButton(
                    R.string.pay_alert_btn_positive_text
                ) { _, _ -> req() }.setNegativeButton(
                    R.string.dialog_req_perm_negative_btn
                ) { dialogInterface, _ -> dialogInterface.dismiss() }.create().show()
        } else {
            req()
        }
    }

    private fun testStart() {
        val url = "https://httpbin.org/" // آدرس سرور آپلود خود را اینجا قرار دهید
        CoroutineScope(Dispatchers.Main).launch {
            tv_speed_download.text = "..."
            tv_speed_upload.text = "..."
            tv_status_ping.text = resources.getString(R.string.ping_test_status)
            val pingResult = withContext(Dispatchers.IO) {
                networkTester.getPingSpeed()
            }
            tv_ping.text = if (pingResult != null) " $pingResult " else "خطا"
            val p = pingResult.toString()
            PING_SPEED = "$p M/s"
            tv_status_ping.text = "پینگ"

            val downloadSpeed = withContext(Dispatchers.IO) {
                networkTester.getDownloadSpeed(url)
            }

            tv_speed_download.text = if (downloadSpeed != null) " ${
                String.format(
                    "%.2f", downloadSpeed
                )
            } " else "خطا"
            Log.i("tenser", downloadSpeed.toString())
            val uploadSpeed = withContext(Dispatchers.IO) {
                networkTester.getUploadSpeed("https://httpbin.org/")
            }
            tv_speed_upload.text = if (uploadSpeed != null) " ${
                String.format(
                    "%.2f", uploadSpeed
                )
            } " else "خطا"
            DOWN_SPEED = String.format(
                "%.2f", downloadSpeed
            )
            UP_SPEED = String.format(
                "%.2f", uploadSpeed
            )
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
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            PHONE_STATUS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray, deviceId: Int
    ) {
        if (requestCode == PHONE_STATUS_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                motoast.MoSuccess(msg = "مجوز تایید شد")
                ipDetect()

            } else {

                motoast.MoError(msg = "مجوز رد شد نوع شبکه قابل دسترسی نیست")


            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver) // حذف ثبت Receiver
        myketHelper.dispose()


    }

}

