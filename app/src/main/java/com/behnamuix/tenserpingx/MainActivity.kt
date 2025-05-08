package com.behnamuix.tenserpingx

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
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
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.behnamuix.tenserping.Network.NetworkCheck
import com.behnamuix.tenserpingx.Dialog.HistoryDialogFragment
import com.behnamuix.tenserpingx.MyTools.MoToast
import com.behnamuix.tenserpingx.MyketRate.MyketRate
import com.behnamuix.tenserpingx.Network.InternetSpeedTester
import com.behnamuix.tenserpingx.Network.Location.UserLocationProvider
import com.behnamuix.tenserpingx.Retrofit.ApiResponse
import com.behnamuix.tenserpingx.Retrofit.RetrofitClient
import com.behnamuix.tenserpingx.databinding.ActivityMainBinding
import com.behnamuix.tenserpingx.util.IabHelper
import com.google.android.material.button.MaterialButton
import ir.myket.billingclient.util.Purchase
import ir.myket.billingclient.util.Security
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private val KEY_FIRST_LAUNCH = "first_launch"
    val SKU_PREMIUM: String = "hist_chart_prem"
    val RC_REQUEST: Int = 10001
    lateinit var mHelper: IabHelper

    private var isDialogShowing = false
    private lateinit var myketrate: MyketRate
    private lateinit var motoast: MoToast
    private var MAC = ""
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
    private lateinit var btn_export_pdf: MaterialButton
    private lateinit var img_hist: ImageView
    private lateinit var tv_ip: TextView
    private lateinit var tv_type: TextView
    private lateinit var tv_speed_upload: TextView
    private lateinit var tv_status_ping: TextView
    private lateinit var tv_ping: TextView
    private lateinit var tv_status: TextView
    private lateinit var vw_start: ConstraintLayout
    private lateinit var networkTester: InternetSpeedTester
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
        main()
    }

    private fun main() {
        changeNavbarStyle()
        keepScreenAwake(this, true)
        config()
        rateOnScreen()

    }

    private fun rateOnScreen() {
        lifecycleScope.launch {
            delay(60000 * 2) // 1 دقیقه بعد
            myketrate.showRateDialog()
        }
    }


    private fun config() {
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        myketrate = MyketRate(this)
        btn_export_pdf = binding.btnExportPdf
        motoast = MoToast(this)
        btn_save_hist = binding.btnSaveHist
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
        btn_export_pdf.setOnClickListener {
            exportToPDF()
        }
        img_hist.setOnClickListener {

            if (checkValidPerm()) {
                showHistDialog()

            } else {
                val payAlert = AlertDialog.Builder(this, R.style.cardAlertDialog)
                payAlert.setTitle(R.string.pay_alert_title)
                payAlert.setIcon(R.drawable.icon_pro)
                payAlert.setMessage(R.string.pay_alert_msg)
                payAlert.setPositiveButton(R.string.pay_alert_btn_positive_text) { _, _ ->
                    payConfig()
                }
                payAlert.setPositiveButtonIcon(getDrawable(R.drawable.icon_buy))
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
                var intent=Intent(this,WebViewActivity::class.java)
                startActivity(intent)
            }
            dialog.setNeutralButton(
                "درباره ما"
            ) { _, _ ->
                val intent = Intent(
                    "android.intent.action.VIEW", "https://behnamuix2024.com/api/bio.html".toUri()
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

    fun keepScreenAwake(activity: AppCompatActivity, keepScreenOn: Boolean) {
        if (keepScreenOn) {
            // افزودن FLAG_KEEP_SCREEN_ON به پنجره
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            // حذف FLAG_KEEP_SCREEN_ON از پنجره
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun changeNavbarStyle() {
        val window = window
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // تغییر رنگ پس‌زمینه نوار ناوبری
        val navigationBarColor = ContextCompat.getColor(this, R.color.transparent)
        window.navigationBarColor = navigationBarColor
        windowInsetsController.isAppearanceLightNavigationBars = false
    }


    private val sharedPreferences by lazy {
        getSharedPreferences("my", Context.MODE_PRIVATE)
    }

    fun setFirstLaunchStatus(isFirstLaunch: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch)
            .apply() // یا commit() برای اعمال تغییرات به صورت همزمان
    }


    fun checkValidPerm(): Boolean {
        return sharedPreferences.getBoolean(
            KEY_FIRST_LAUNCH,
            false
        ) // مقدار پیش‌فرض true است اگر کلید وجود نداشته باشد.
    }

    private fun payConfig() {
        mHelper = IabHelper(this, BuildConfig.IAB_PUBLIC_KEY)
        mHelper.enableDebugLogging(false)
        if (mHelper != null) {
            mHelper.startSetup { result ->
                if (result != null) {
                    payIntent()
                }
            }
        }


    }

    private fun payIntent() {
        // 1. بررسی وجود mHelper (آبجکت مدیریت خرید درون‌برنامه‌ای)

        // 2. تعریف لیسنر برای رویداد مصرف (consume) محصول
        IabHelper.OnConsumeFinishedListener { purchase, result ->
            when {
                // 2-1. اگر mHelper نال بود عملیات متوقف می‌شود
                false -> return@OnConsumeFinishedListener

                // 2-2. اگر مصرف موفقیت‌آمیز بود
                result?.isSuccess == true -> Log.d("TAG", "Consumption successful. Provisioning.")

                // 2-3. در صورت خطا در مصرف محصول
                else -> Log.e("TAG", "Error while consuming: $result")
            }
        }

        // 3. تعریف لیسنر برای بررسی موجودی محصولات
        val inventoryListener = IabHelper.QueryInventoryFinishedListener { result, inv ->
            when {
                // 3-1. اگر خطا در دریافت لیست محصولات
                result?.isFailure == true -> {
                    Log.e("TAG", "Failed to query inventory: $result")
                    return@QueryInventoryFinishedListener
                }

                // 3-2. اگر محصول مورد نظر قبلا خریداری شده
                inv?.getPurchase(SKU_PREMIUM) != null -> {
                    inv.getPurchase(SKU_PREMIUM)?.let { purchase ->
                        if (developerPayload(purchase) == true) {
                            // محصول قبلاً خریداری شده و payload معتبر است.
                            // برای محصولات مصرف نشدنی، نیازی به مصرف نیست.
                            Log.d("TAG", "User already owns the non-consumable item: $SKU_PREMIUM")
                            // در اینجا می‌توانید وضعیت پریمیوم کاربر را فعال کنید
                            setFirstLaunchStatus(true) // فرض بر اینکه 'perm' وضعیت پریمیوم را نگه می‌دارد
                            showHistDialog() // اگر کاربر قبلاً خریده، ممکن است بخواهید مستقیماً محتوای پریمیوم را نمایش دهید
                            return@QueryInventoryFinishedListener
                        } else {
                            // payload نامعتبر است
                            Log.e("TAG", "Error: Invalid payload for owned item")
                            return@QueryInventoryFinishedListener
                        }
                    }
                }

                // 3-3. اگر محصول خریداری نشده بود
                else -> {
                    startPurchaseFlow()
                }
            }
        }

        // 4. شروع فرآیند بررسی موجودی
        mHelper.queryInventoryAsync(inventoryListener)
    }

    // 5. تابع شروع فرآیند خرید
    private fun startPurchaseFlow() {
        mHelper.launchPurchaseFlow(
            this,
            SKU_PREMIUM, // شناسه محصول
            RC_REQUEST, // کد درخواست
            IabHelper.OnIabPurchaseFinishedListener { result, info ->
                when {
                    // 5-1. اگر mHelper نال بود
                    false -> return@OnIabPurchaseFinishedListener

                    // 5-2. اگر خطا در فرآیند خرید
                    result?.isFailure == true -> Log.e("TAG", "Error purchasing: $result")

                    // 5-3. اگر اطلاعات خرید نامعتبر
                    info == null || !developerPayload(info) -> {
                        Log.e("TAG", "Purchase authenticity failed")
                    }

                    // 5-4. اگر خرید محصول پریمیوم موفق بود
                    info.sku == SKU_PREMIUM -> handleSuccessfulPurchase(info)
                }
            },
            "" // developerPayload
        )
    }

    // 6. تابع مدیریت خرید موفق
    private fun handleSuccessfulPurchase(purchase: Purchase) {
        Log.d("TAG", "Premium upgrade purchased")
        motoast.MoSuccess(msg = "تبریک , نسخه پرمیوم برای همیشه برای شما فعال شد")

        // 6-1. ذخیره وضعیت خرید
        setFirstLaunchStatus(true)

        // 6-2. نمایش دیالوگ تاریخچه
        showHistDialog()

        // 6-3. مصرف محصول برای امکان خرید مجدد
        mHelper.consumeAsync(purchase) { _, result ->
            if (result?.isFailure == true) {
                Log.e("TAG", "Consumption failed: $result")
            }
        }
    }

    private fun developerPayload(purchase: Purchase): Boolean {
        return try {
            val sig = purchase.signature
            val date = purchase.originalJson
            Security.verifyPurchase(BuildConfig.IAB_PUBLIC_KEY, date, sig)
        } catch (e: Exception) {
            false
        }
    }


    private fun getHistData() {
        DATE = getDate()
        MAC = getAndroidId(applicationContext)
        Toast.makeText(this, MAC, Toast.LENGTH_LONG).show()
        val builder1 = AlertDialog.Builder(this, R.style.cardAlertDialog)
        builder1.setMessage("آیا شما میخواهید داده ها در تاریخچه ذخیره شود؟")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "بله"
        ) { _, _ -> insertToHistDb(MAC, DATE, IP, NET_TYPE, PING_SPEED) }

        builder1.setNegativeButton(
            "خیر"
        ) { dialog, _ -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()

    }

    /**
     * دریافت Android ID دستگاه
     * (منحصر به فرد برای هر دستگاه + اپلیکیشن)
     */
    @SuppressLint("HardwareIds")
    private fun getAndroidId(context: Context): String {
        return try {
            android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            ) ?: "unknown"
        } catch (e: Exception) {
            Log.e("DeviceUtils", "Error getting Android ID", e)
            "unknown"
        }
    }

    private fun insertToHistDb(
        mac: String,
        date: String,
        ip: String,
        netType: String,
        pingSpeed: String
    ) {

        Log.d("ALPHA", "$mac/$date/$ip/$netType/$pingSpeed / $DOWN_SPEED ")
        val call = RetrofitClient.apiService.sendHist(
            mac,
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
        supportFragmentManager.executePendingTransactions()
        if (supportFragmentManager.findFragmentByTag("History") == null) {
            HistoryDialogFragment().show(supportFragmentManager, "History")
        }
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


    private fun req() {

        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            PHONE_STATUS_REQUEST_CODE
        )
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("TAG", "onActivityResult($requestCode,$resultCode,$data")

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data)
        } else {
            Log.d("TAG", "....")

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHelper.dispose()

    }

    override fun onStop() {
        super.onStop()
        try {
            if (networkReceiver != null) {
                unregisterReceiver(networkReceiver)// حذف ثبت Receiver
            }
        } catch (e: IllegalArgumentException) {
            // Receiver wasn't registered, ignore
            e.printStackTrace()
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

    private fun exportToPDF() {


    }


}


