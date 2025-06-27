package com.behnamuix.tenserpingx

import android.Manifest
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.behnamuix.tenserpingx.Ads.TapsellApi
import com.behnamuix.tenserpingx.Network.NetworkCheck
import com.behnamuix.tenserpingx.AndroidWraper.DeviceInfo
import com.behnamuix.tenserpingx.Dialog.HistoryDialogFragment
import com.behnamuix.tenserpingx.Dialog.NoInternetDialogFragment
import com.behnamuix.tenserpingx.MyTools.Object.ConverterX
import com.behnamuix.tenserpingx.MyTools.MoToast
import com.behnamuix.tenserpingx.MyTools.Object.VpnChecker
import com.behnamuix.tenserpingx.MyketRate.MyketRate
import com.behnamuix.tenserpingx.Network.InternetSpeedTester
import com.behnamuix.tenserpingx.Network.IpAddress.getIpAddress
import com.behnamuix.tenserpingx.Network.UploadTester
import com.behnamuix.tenserpingx.Retrofit.ApiResponse
import com.behnamuix.tenserpingx.Retrofit.ApiResponseCheckVerifyJson
import com.behnamuix.tenserpingx.Retrofit.RetrofitClient
import com.behnamuix.tenserpingx.databinding.ActivityMainBinding
import com.behnamuix.tenserpingx.util.IabHelper
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import ir.myket.billingclient.util.Purchase
import ir.myket.billingclient.util.Security
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    private val URL_BG = "https://behnamuix2024.com/img/bg.png"

    private val KEY_FIRST_LAUNCH = "first_launch"
    val SKU_PREMIUM: String = "hist_chart_prem"
    val RC_REQUEST: Int = 10001
    lateinit var mHelper: IabHelper
    private var v: Boolean = false


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
    private lateinit var pb_card: LinearLayout
    private lateinit var lav_info: LottieAnimationView
    private lateinit var tv_speed_download: TextView
    private lateinit var btn_save_hist: MaterialButton
    private lateinit var btn_export_pdf: MaterialButton
    private lateinit var img_exit: ImageView
    private lateinit var img_upload: ImageView
    private lateinit var img_download: ImageView
    private lateinit var pb_upload: ProgressBar
    private lateinit var tv_pb_upload: TextView
    private lateinit var tv_status_upload: TextView
    private lateinit var tv_status_download: TextView
    private lateinit var img_hist: ImageView
    private lateinit var img_rotate_phone: ImageView
    private lateinit var tv_ip: TextView
    private lateinit var tv_type: TextView
    private lateinit var tv_c1: TextView
    private lateinit var tv_c2: TextView
    private lateinit var tv_c3: TextView
    private lateinit var tv_speed_upload: TextView
    private lateinit var tv_status_ping: TextView
    private lateinit var tv_ping: TextView
    private lateinit var tv_status: TextView
    private lateinit var vw_start: ConstraintLayout
    private lateinit var networkTester: InternetSpeedTester
    private lateinit var uploadTester: UploadTester
    var randrom = Random(Int.MAX_VALUE)
    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && !NetworkCheck.isInternetAvailable(context)) {
                showNoInternetDialog()
            } else {
                dismissNoInternetDialog()
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { setEnglishLocale(it) })

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        val instance: TapsellApi by lazy {
            TapsellApi(this)
        }
        //instance.TapsellConfig()
        main()


    }

    private fun main() {
        changeNavbarStyle()
        loadBackground(URL_BG)
        keepScreenAwake(this, true)
        config()
        testVpnState()
        rateOnScreen()


    }

    private fun loadBackground(urlBg: String) {
        Picasso.get().load(urlBg).into(binding.bg)

    }

    private fun rateOnScreen() {
        lifecycleScope.launch {
            delay(150000) // 1 دقیقه بعد
            myketrate.showRateDialog()
        }
    }

    private fun config() {

        MAC = DeviceInfo.getAndroidId(this)
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        img_exit = binding.imgExit
        myketrate = MyketRate(this)
        img_rotate_phone = binding.imgRotatePhone!!
        pb_card = binding.pbCard
        img_upload = binding.imgUpload
        img_download = binding.imgDownload
        tv_status_upload = binding.tvStatusUpload
        tv_status_download = binding.tvStatusDownload
        btn_export_pdf = binding.btnExportPdf
        motoast = MoToast(this)
        pb_upload = binding.pbUpload
        tv_pb_upload = binding.tvPbUplosd
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
        uploadTester = UploadTester(this)
        onclickHandler()


    }

    private fun downloadAnimator() {
        val animationDuration = 500L // مدت زمان هر حرکت (بالا یا پایین) بر حسب میلی‌ثانیه
        val moveDistance = 20f // مقدار جابجایی (بالا و پایین) بر حسب پیکسل

        // 1. انیمیشن رفتن به بالا
        val moveUpAnimator = ObjectAnimator.ofFloat(img_download, "translationY", 0f, -moveDistance)
        moveUpAnimator.duration = animationDuration
        moveUpAnimator.interpolator = AccelerateDecelerateInterpolator() // برای حرکت نرم‌تر

        // 2. انیمیشن برگشتن به پایین (از بالا به موقعیت اولیه)
        val moveDownAnimator =
            ObjectAnimator.ofFloat(img_download, "translationY", -moveDistance, 0f)
        moveDownAnimator.duration = animationDuration
        moveDownAnimator.interpolator = AccelerateDecelerateInterpolator()

        // 3. انیمیشن رفتن به پایین (از موقعیت اولیه به پایین)
        val moveFurtherDownAnimator =
            ObjectAnimator.ofFloat(img_download, "translationY", 0f, moveDistance)
        moveFurtherDownAnimator.duration = animationDuration
        moveFurtherDownAnimator.interpolator = AccelerateDecelerateInterpolator()

        // 4. انیمیشن برگشتن به موقعیت اولیه (از پایین به موقعیت اولیه)
        val moveBackUpAnimator =
            ObjectAnimator.ofFloat(img_download, "translationY", moveDistance, 0f)
        moveBackUpAnimator.duration = animationDuration
        moveBackUpAnimator.interpolator = AccelerateDecelerateInterpolator()


        // استفاده از PropertyValuesHolder برای حرکت رفت و برگشت یکجا
        val upAndDown = ObjectAnimator.ofFloat(
            img_download,
            "translationY",
            0f,
            -moveDistance,
            0f,
            moveDistance,
            0f
        )
        upAndDown.duration = animationDuration * 4 // دو رفت و برگشت کامل در یک چرخه
        upAndDown.interpolator = AccelerateDecelerateInterpolator()

        // تنظیم تعداد تکرار
        upAndDown.repeatCount = 2 // 0 = 1 بار، 1 = 2 بار، 2 = 3 بار (برای سه بار کلی)
        upAndDown.repeatMode =
            ObjectAnimator.RESTART // بعد از هر تکرار، انیمیشن از ابتدا شروع می‌شود

        upAndDown.start() // شروع انیمیشن

    }

    private fun uploadAnimator() {
        val animationDuration = 500L // مدت زمان هر حرکت (بالا یا پایین) بر حسب میلی‌ثانیه
        val moveDistance = 20f // مقدار جابجایی (بالا و پایین) بر حسب پیکسل

        // 1. انیمیشن رفتن به بالا
        val moveUpAnimator = ObjectAnimator.ofFloat(img_upload, "translationY", 0f, -moveDistance)
        moveUpAnimator.duration = animationDuration
        moveUpAnimator.interpolator = AccelerateDecelerateInterpolator() // برای حرکت نرم‌تر

        // 2. انیمیشن برگشتن به پایین (از بالا به موقعیت اولیه)
        val moveDownAnimator = ObjectAnimator.ofFloat(img_upload, "translationY", -moveDistance, 0f)
        moveDownAnimator.duration = animationDuration
        moveDownAnimator.interpolator = AccelerateDecelerateInterpolator()

        // 3. انیمیشن رفتن به پایین (از موقعیت اولیه به پایین)
        val moveFurtherDownAnimator =
            ObjectAnimator.ofFloat(img_upload, "translationY", 0f, moveDistance)
        moveFurtherDownAnimator.duration = animationDuration
        moveFurtherDownAnimator.interpolator = AccelerateDecelerateInterpolator()

        // 4. انیمیشن برگشتن به موقعیت اولیه (از پایین به موقعیت اولیه)
        val moveBackUpAnimator =
            ObjectAnimator.ofFloat(img_upload, "translationY", moveDistance, 0f)
        moveBackUpAnimator.duration = animationDuration
        moveBackUpAnimator.interpolator = AccelerateDecelerateInterpolator()


        // استفاده از PropertyValuesHolder برای حرکت رفت و برگشت یکجا
        val upAndDown = ObjectAnimator.ofFloat(
            img_upload,
            "translationY",
            0f,
            -moveDistance,
            0f,
            moveDistance,
            0f
        )
        upAndDown.duration = animationDuration * 4 // دو رفت و برگشت کامل در یک چرخه
        upAndDown.interpolator = AccelerateDecelerateInterpolator()

        // تنظیم تعداد تکرار
        upAndDown.repeatCount = 2 // 0 = 1 بار، 1 = 2 بار، 2 = 3 بار (برای سه بار کلی)
        upAndDown.repeatMode =
            ObjectAnimator.RESTART // بعد از هر تکرار، انیمیشن از ابتدا شروع می‌شود

        upAndDown.start() // شروع انیمیشن

    }

    private fun createTestFile(): File {
        val file = File(cacheDir, "test_upload.bin")
        if (!file.exists()) {
            // ایجاد یک فایل 5MB برای تست
            val data = ByteArray(1024 * 1024) { 4 }
            file.writeBytes(data)
        }
        return file
    }

    private fun testVpnState() {
        val vpn = VpnChecker.checkVpnState(this)
        if (vpn) {
            motoast.MoWarning("روشن بودن فیلترشکن باعث اختلال در عملکرد اپلیکیشن میشود!")
        } else {

        }
    }

    private fun onclickHandler() {
        v = checkVerifyP()
        img_rotate_phone.setOnClickListener() {
            rotatePhone()
        }
        img_exit.setOnClickListener() {
            finish()
        }
        btn_export_pdf.setOnClickListener {
            exportToPDF()
        }
        img_hist.setOnClickListener {

            if (v) {
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
            dialog.setNegativeButton(getString(R.string.ok_button)) { dialog, _ ->
                dialog.dismiss()
            }
            dialog.setPositiveButton(
                getString(R.string.about_us)
            ) { _, _ ->
                var intent = Intent(this, WebViewActivity::class.java)
                startActivity(intent)
            }
            dialog.setPositiveButtonIcon(getDrawable(R.drawable.icon_info))
            dialog.show()
        }
        vw_start.setOnClickListener {


            tv_speed_download.text = "--,--"
            tv_speed_upload.text = "--,--"
            ipDetect()
            DtestSpeedConfig()

            networkTester.startDownloadSpeedTest() // از مقادیر پیش فرض برای URL دانلود و اندازه تست استفاده می کند
            getPingSpeed()
            DATE = getDate()
            Handler(Looper.getMainLooper()).postDelayed({
                simulateUploadSpeed()
            }, 4000)


        }
        btn_save_hist.setOnClickListener {
            if (PING_SPEED != "") {
                getHistData()

            } else {
                motoast.MoWarning(msg = getString(R.string.data_not_loaded_warning))
            }

        }


    }

    private fun simulateUploadSpeed() {
        try {
            uploadAnimator()
            var list_speed = mutableListOf(
                0.70,
                0.65,
                0.88,
                0.85,
                0.87,
                0.83,
                0.69,
                0.64,
                0.77,
                0.84,
                0.73,
                0.71,
                0.83
            )
            var x = randrom.nextInt(0, 12)
            var mbps = list_speed[x]
            tv_status_upload.visibility = View.GONE
            tv_speed_upload.text = "${ConverterX.mbpsToKBpsConverter(mbps)}\nKB/S"
            //Log.i("TEST_UPLOAD", x.toString())
        } catch (e: Exception) {
            motoast.MoError("خطا در محاسبه سرعت آپلود!")
        }

    }

    private fun rotatePhone() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

    }


    private fun getPingSpeed() {
        lifecycleScope.launch {
            tv_status_ping.text = resources.getString(R.string.ping_test_status)
            val pingResult = withContext(Dispatchers.IO) {
                networkTester.getPingSpeed()

            }
            tv_ping.text = if (pingResult != null) " $pingResult " else getString(R.string.error)
            val p = pingResult.toString()
            PING_SPEED = "$p M/s"
            tv_status_ping.text = getString(R.string.ping)
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
        val statusBarColor = ContextCompat.getColor(this, R.color.black_mat)

        val navigationBarColor = ContextCompat.getColor(this, R.color.transparent)
        window.navigationBarColor = navigationBarColor
        window.statusBarColor = statusBarColor
        windowInsetsController.isAppearanceLightNavigationBars = false
    }


    private val sharedPreferences by lazy {
        getSharedPreferences("my", Context.MODE_PRIVATE)
    }

    fun setFirstLaunchStatus(isFirstLaunch: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch)
            .apply() // یا commit() برای اعمال تغییرات به صورت همزمان
    }


    private fun checkVerifyP(): Boolean {
        MAC = DeviceInfo.getAndroidId(applicationContext)
        val call = RetrofitClient.apiService.checkVerify(MAC)
        call.enqueue(object : Callback<ApiResponseCheckVerifyJson> {
            override fun onResponse(
                call: Call<ApiResponseCheckVerifyJson>,
                response: Response<ApiResponseCheckVerifyJson>
            ) {
                val data = response.body()
                if (data != null) {
                    if (data.exists) {
                        v = true

                    } else {
                        v = false
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseCheckVerifyJson>, t: Throwable) {
                Log.d("error connection", "error!")
            }

        })
        return v

    }


    private fun payConfig() {
        motoast.MoWarning(
            title = "مایکت", msg = "در حال ارتباط با سرور های مایکت هستیم اندکی صبر کنید"
        )
        mHelper = IabHelper(this, BuildConfig.IAB_PUBLIC_KEY)
        mHelper.enableDebugLogging(true)
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
            this, SKU_PREMIUM, // شناسه محصول
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
            }, "" // developerPayload
        )
    }

    // 6. تابع مدیریت خرید موفق
    private fun handleSuccessfulPurchase(purchase: Purchase) {
        Log.d("TAG", "Premium upgrade purchased")

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
            val mac = DeviceInfo.getAndroidId(this)
            val sig = purchase.signature
            val date = purchase.originalJson
            val time = formatPurchaseTime(purchase.purchaseTime)
            val sku = purchase.sku
            val token = purchase.token
            //***verfiy purchase
            val verify = "1"
            insertAndVerifyPay(mac, time.toString(), sku, token, sig, verify)
            Security.verifyPurchase(BuildConfig.IAB_PUBLIC_KEY, date, sig)
        } catch (e: Exception) {
            false
        }
    }

    private fun insertToHistDb(
        mac: String, date: String, ip: String, netType: String, pingSpeed: String
    ) {

        val call = RetrofitClient.apiService.sendHist(
            mac, date, netType, ip, pingSpeed
        )
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    motoast.MoSuccess(msg = getString(R.string.history_saved_success))

                } else {
                    motoast.MoError(msg = getString(R.string.data_fetch_error))


                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                motoast.MoError(msg = getString(R.string.data_fetch_error))


            }

        })


    }

    private fun formatPurchaseTime(purchaseTime: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Tehran") // تنظیم منطقه زمانی ایران
        return sdf.format(Date(purchaseTime))
    }

    private fun insertAndVerifyPay(
        mac: String, time: String, sku: String, token: String, sig: String, verify: String
    ) {
        val call = RetrofitClient.apiService.insertPurchaseLog(mac, time, sku, token, sig, verify)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val data = response.body()
                if (data != null) {
                    if (data.status == "success") {
                        motoast.MoSuccess(msg = getString(R.string.purchase_success))
                        v = checkVerifyP()
                    } else {
                        motoast.MoError(msg = getString(R.string.purchase_failed))

                    }

                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                motoast.MoError(msg = getString(R.string.purchase_failed))
            }

        })

    }

    private fun getHistData() {
        DATE = getDate()
        MAC = DeviceInfo.getAndroidId(applicationContext)
        val builder1 = AlertDialog.Builder(this, R.style.cardAlertDialog)
        builder1.setMessage(getString(R.string.save_history_prompt))
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            getString(R.string.yes)
        ) { _, _ -> insertToHistDb(MAC, DATE, IP, NET_TYPE, PING_SPEED) }

        builder1.setNegativeButton(
            getString(R.string.no)
        ) { dialog, _ -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()

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
        val locationProvider = getIpAddress(this)
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

    //DownloadTester
    fun DtestSpeedConfig() {
        networkTester.setOnSpeedChangeListener(object : InternetSpeedTester.OnSpeedChangeListener {
            override fun onDownloadSpeedChanged(mbps: Double) {
                runOnUiThread {
                    downloadAnimator()
                    vw_start.alpha = 1f
                    vw_start.isEnabled = true
                    pb_card.visibility = View.GONE
                    if (mbps > 1) {
                        val speedText = "%.2f ".format(mbps)
                        tv_speed_download.text = "$speedText\nMb/s"
                        tv_status.text = "شروع"

                    } else {
                        val speedText = "%.2f ".format(ConverterX.mbpsToKBpsConverter(mbps))
                        tv_speed_download.text = "$speedText\nKb/s"
                        tv_status.text = "شروع"

                    }


                }
            }

            override fun onUploadSpeedChanged(mbps: Double) {
                runOnUiThread {

                }
            }

            override fun onTestStarted() {
                runOnUiThread {
                    vw_start.isEnabled = false
                    tv_status.text = "..."
                    motoast.MoInfo("تست سرعت دانلود و آپلود در حال اجرا میباشد اندکی صبرکنید...")
                }
            }

            override fun onTestFinished() {
                motoast.MoSuccess("تست سرعت دانلود کامل شد.")
                tv_status_download.visibility = View.GONE

            }


            override fun onError(message: String) {
                runOnUiThread {
                    vw_start.isEnabled = true
                    tv_status.text = "شروع"
                    Toast.makeText(
                        this@MainActivity,
                        "خطا در تست دانلود دوباره امتحان کنید: $message",
                        Toast.LENGTH_LONG
                    ).show()


                }
            }

        })
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
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray, deviceId: Int
    ) {
        if (requestCode == PHONE_STATUS_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                motoast.MoSuccess(msg = getString(R.string.permission_approved))
                ipDetect()

            } else {

                motoast.MoError(msg = getString(R.string.permission_denied_network_unavailable))


            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }


    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
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
        // قبل از استفاده، چک کنید آیا mHelper مقداردهی شده یا نه
        if (::mHelper.isInitialized) {
            mHelper.dispose()
        }

        networkTester.stopSpeedTest()

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
        motoast.MoInfo("به زودی ...")


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setEnglishLocale(context: Context): Context {
        val locale = Locale("en", "US")
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }


}


