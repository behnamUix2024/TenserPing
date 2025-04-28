package com.behnamuix.tenserpingx.Dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.behnamuix.tenserpingx.Adapter.HistoryDialogAdapter
import com.behnamuix.tenserpingx.MoToast
import com.behnamuix.tenserpingx.R
import com.behnamuix.tenserpingx.Retrofit.ApiResponseJson
import com.behnamuix.tenserpingx.Retrofit.RetrofitClient
import com.behnamuix.tenserpingx.databinding.FragHistDialogBinding
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

class HistoryDialogFragment : DialogFragment() {

    private lateinit var chart: LineChart
    private lateinit var cl_show_chart: ConstraintLayout
    private lateinit var cl_show_list: ConstraintLayout
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
    ): View? {

        _binding = FragHistDialogBinding.inflate(inflater, container, false)
        config()


        return binding.root
        // Inflate the layout for this fragment
    }

    private fun config() {
        cl_show_list = binding.clShowList
        cl_show_chart = binding.clShowChart
        chart = binding.pingChart
        pingChart = binding.pingChart
        hist_list = binding.listHist
        hist_chart = binding.histChart
        motoast = MoToast(requireActivity())
        rec_hist = binding.recHist
        cl_show_chart.setOnClickListener() {
            showChart()

        }
        cl_show_list.setOnClickListener() {
            getHist()

        }

    }

    private fun showChart() {
        hist_list.visibility = View.GONE
        hist_chart.visibility = View.VISIBLE
        cl_show_list.visibility = View.VISIBLE
        cl_show_chart.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getPing()
                if (response.status == "success") {
                    setupChart(response.data)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getHist() {
        hist_chart.visibility = View.GONE
        hist_list.visibility = View.VISIBLE
        cl_show_list.visibility = View.GONE
        cl_show_chart.visibility = View.VISIBLE

        motoast.MoInfo(msg = "در حال دریافت داده ها از سرور...")
        lifecycleScope.launch {
            try {
                val call = RetrofitClient.apiService.getHist()
                call.enqueue(object : Callback<ApiResponseJson> {
                    override fun onResponse(
                        call: Call<ApiResponseJson>,
                        response: Response<ApiResponseJson>
                    ) {
                        if (response.isSuccessful) {
                            val apiresp = response.body()
                            if (apiresp?.status == "success" && apiresp.data != null) {
                                rec_hist.layoutManager =
                                    LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                val adapter = HistoryDialogAdapter(apiresp.data, requireContext())
                                rec_hist?.adapter = adapter
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

    private fun setupChart(data: List<String>) {
        // تبدیل داده‌ها
        val values = data.map { it.replace(" M/s", "").toFloat() }
        val entries = values.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        // تنظیمات XAxis
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            axisMinimum = 0f // حداقل مقدار محور X
            axisMaximum = (data.size - 1).toFloat() // حداکثر مقدار محور X
            labelCount = data.size // تعداد لیبل‌ها
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        0 -> "First Value"
                        1 -> "Second Value"
                        else -> ""
                    }
                }
            }
        }

        // تنظیمات YAxis
        chart.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = values.maxOrNull()?.times(1.1f) ?: 100f // 10% بیشتر از حداکثر مقدار
            granularity = 100f
        }

        // غیرفعال کردن محور سمت راست
        chart.axisRight.isEnabled = false

        // تنظیم داده‌ها
        val dataSet = LineDataSet(entries, "Speed (M/s)").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
        }

        chart.data = LineData(dataSet)
        chart.description.text = "Server Speed Data"
        chart.invalidate() // رفرش نمودار
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(true)
        val win = dialog?.window
        if (win != null) {
            win.setGravity(Gravity.BOTTOM)
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
            win.setLayout(width, height)

        }

        view.findViewById<Button>(R.id.btn_hist_remove).setOnClickListener() {
            removeAllHist()
            dismiss()
        }
    }

    private fun removeAllHist() {

    }


}

