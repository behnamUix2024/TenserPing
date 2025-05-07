package com.behnamuix.tenserpingx.Dialog

import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.behnamuix.tenserpingx.Adapter.HistoryDialogAdapter
import com.behnamuix.tenserpingx.MyTools.MoToast
import com.behnamuix.tenserpingx.R
import com.behnamuix.tenserpingx.Retrofit.ApiResponseJson
import com.behnamuix.tenserpingx.Retrofit.RetrofitClient
import com.behnamuix.tenserpingx.databinding.FragHistDialogBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable
import kotlinx.coroutines.delay

class HistoryDialogFragment : DialogFragment() {
    private var ANDROID_IDS = ""
    private var currentCurveMode = LineDataSet.Mode.CUBIC_BEZIER
    private lateinit var chart: LineChart
    private lateinit var sp_type: Spinner
    private lateinit var btn_show_chart: MaterialButton
    private lateinit var btn_show_list: MaterialButton
    private lateinit var hist_list: ConstraintLayout
    private lateinit var hist_chart: ConstraintLayout
    private lateinit var pingChart: LineChart
    private lateinit var motoast: MoToast
    private lateinit var rec_hist: RecyclerView
    private var _binding: FragHistDialogBinding? = null // اصلاح نوع و مقداردهی اولیه
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragHistDialogBinding.inflate(inflater, container, false)
        main()

        return binding.root
        // Inflate the layout for this fragment
    }

    private fun main() {
        config()
        setupCurveModeSpinner()
    }

    private fun setupCurveModeSpinner() {
        // ایجاد لیست گزینه‌ها
        val modes = listOf(
            "منحنی نرم" to LineDataSet.Mode.CUBIC_BEZIER,
            "خط مستقیم" to LineDataSet.Mode.LINEAR,
            "سیگنالی" to LineDataSet.Mode.STEPPED
        )

        // تنظیم آداپتر برای اسپینر
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            modes.map { it.first }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        sp_type.adapter = adapter

        // مدیریت انتخاب کاربر
        sp_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentCurveMode = modes[position].second
                updateChartCurveMode()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


    }

    private fun updateChartCurveMode() {
        chart.data?.dataSets?.firstOrNull()?.let { dataSet ->
            (dataSet as LineDataSet).mode = currentCurveMode
            chart.invalidate()
        }
    }

    private fun config() {
        ANDROID_IDS = getDeviceId()
        sp_type = binding.spType
        btn_show_list = binding.btnShowList
        btn_show_chart = binding.btnShowChart
        chart = binding.pingChart
        pingChart = binding.pingChart
        hist_list = binding.listHist
        hist_chart = binding.histChart
        motoast = MoToast(requireActivity())
        rec_hist = binding.recHist
        getHist()
        btn_show_chart.setOnClickListener {
            lifecycleScope.launch {
                btn_show_chart.text = "⌛"
                delay(2000)
                showChart()


            }

        }
        btn_show_list.setOnClickListener {
            getHist()

        }
    }

    private fun showChart() {
        btn_show_chart.text = "نمایش نمودار"
        hist_list.visibility = View.GONE
        hist_chart.visibility = View.VISIBLE
        btn_show_list.visibility = View.VISIBLE
        btn_show_chart.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getPing(ANDROID_IDS)

                if (response.status == "success") {
                    response.data?.let { configChart(it) }
                }
            } catch (e: Exception) {
                Log.d("err", "${e.message}")
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getDeviceId(): String {
        return try {
            Settings.Secure.getString(
                requireContext().contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getHist() {
        hist_chart.visibility = View.GONE
        hist_list.visibility = View.VISIBLE
        btn_show_list.visibility = View.GONE
        btn_show_chart.visibility = View.VISIBLE
        lifecycleScope.launch {
            motoast.MoInfo(msg = "در حال دریافت داده ها ...")

            try {
                val call = RetrofitClient.apiService.getHist(ANDROID_IDS)
                call.enqueue(object : Callback<ApiResponseJson> {
                    override fun onResponse(
                        call: Call<ApiResponseJson>,
                        response: Response<ApiResponseJson>
                    ) {
                        if (response.isSuccessful) {
                            val apiresp = response.body()
                            if (apiresp?.status == "success") {
                                rec_hist.layoutManager =
                                    LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                val adapter = HistoryDialogAdapter(apiresp.data, requireContext())
                                rec_hist.adapter = adapter
                            }
                        } else {
                            motoast.MoError(msg = "داده ای دریافت نشد")
                        }
                    }

                    override fun onFailure(call: Call<ApiResponseJson>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()

                    }

                })
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun configChart(data: List<String>) {
        // تبدیل داده‌های رشته‌ای به مقادیر عددی
// مثال: "97 M/s" → 97.0f
        val values = data.map { it.replace(" M/s", "").toFloat() }

// ایجاد نقاط نمودار (Entry) با اندیس به عنوان X و مقدار به عنوان Y
        val entries = values.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

// تنظیمات مجموعه داده‌های خطی
        val dataSet = LineDataSet(entries, "سرعت (مگابیت بر ثانیه)").apply {
            color = Color.rgb(67, 153, 226)  // رنگ خط آبی آسمانی
            valueTextColor = Color.WHITE      // رنگ متن مقادیر سفید
            valueTextSize = 12f               // سایز متن مقادیر 12 پیکسل
            lineWidth = 2f                    // ضخامت خط 2 پیکسل
            setCircleColor("#000000".toColorInt())  // رنگ نقاط قرمز
            circleRadius = 5f
            fillColor = "#AF4399E2".toColorInt()    // رنگ پرکردن زیر خط با شفافیت
            setDrawFilled(true)               // فعال کردن پرکردن زیر خط
            mode = currentCurveMode // استفاده از حالت انتخاب شده

        }

// تنظیمات محور X (افقی)
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM  // نمایش محور در پایین
            textColor = Color.WHITE                // رنگ متن سفید
            setDrawGridLines(true)                 // نمایش خطوط راهنما
            gridColor = "#3DFFFFFF".toColorInt() // رنگ خطوط راهنما با شفافیت
            axisLineColor = Color.WHITE            // رنگ خط محور سفید
            axisLineWidth = 2f                     // ضخامت خط محور
            granularity = 1f                       // فاصله بین مقادیر
            // فرمت دهنده متن‌های محور X
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        0 -> "اندازه‌گیری اول"
                        1 -> "اندازه‌گیری دوم"
                        else -> ""
                    }
                }
            }

        }

// تنظیمات محور Y (عمودی)
        chart.axisLeft.apply {
            textColor = Color.WHITE                // رنگ متن سفید
            setDrawGridLines(true)                 // نمایش خطوط راهنما
            gridColor = "#3DFFFFFF".toColorInt() // رنگ خطوط راهنما
            axisLineColor = Color.WHITE            // رنگ خط محور سفید
            axisLineWidth = 1f                     // ضخامت خط محور
            granularity = 50f                      // فاصله بین مقادیر 50 واحد
        }

// غیرفعال کردن محور Y سمت راست
        chart.axisRight.isEnabled = false

// تنظیمات کلی نمودار
        chart.apply {
            this.data = LineData(dataSet)  // اتصال داده‌ها به نمودار
            description.text = "نمودار دقیق سرعت پینگ شبکه شما (بر اساس M/s)"
            description.textColor = Color.WHITE  // رنگ توضیحات سفید
            setNoDataText("در حال دریافت داده...")  // متن هنگام عدم وجود داده
            setNoDataTextColor(Color.WHITE)      // رنگ متن بدون داده
            legend.textColor = Color.WHITE       // رنگ راهنما سفید
            setDrawGridBackground(false)        // غیرفعال کردن پس‌زمینه شبکه
            setDrawBorders(true)                // فعال کردن حاشیه
            setTouchEnabled(true)               // فعال کردن لمسی
            isDragEnabled = true                // فعال کردن کشیدن
            setScaleEnabled(true)               // فعال کردن زوم
            setPinchZoom(true)                  // فعال کردن زوم دو انگشتی
            setBackgroundColor(Color.TRANSPARENT) // پس‌زمینه شفاف
            animateX(1800, Easing.EaseInOutQuad) // انیمیشن1   ثانیه‌ای
            invalidate()  // بروزرسانی نمودار
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog?.setCancelable(true)
        val win = dialog?.window
        if (win != null) {
            win.setGravity(Gravity.BOTTOM)
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
            win.setLayout(width, height)

        }

        view.findViewById<Button>(R.id.btn_hist_remove).setOnClickListener {
            removeAllHist()
            dismiss()
        }
    }

    private fun removeAllHist() {

    }


}

