package com.behnamuix.tenserpingx.Dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.behnamuix.tenserpingx.Adapter.HistoryDialogAdapter
import com.behnamuix.tenserpingx.Model.HistoryModel
import com.behnamuix.tenserpingx.R
import com.behnamuix.tenserpingx.Retrofit.ApiResponseJson
import com.behnamuix.tenserpingx.Retrofit.ApiService
import com.behnamuix.tenserpingx.databinding.FragHistDialogBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HistoryDialogFragment : DialogFragment() {
    private var URL = "https://behnamuix2024.com/api/"
    private lateinit var retrofit: Retrofit

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
        retrofit=Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        rec_hist = binding.recHist

        getHist()

    }

    private fun getHist() {

        val apiservice = retrofit.create(ApiService::class.java)
        val call = apiservice.getHist()
        call.enqueue(object : Callback<ApiResponseJson> {
            override fun onResponse(
                call: Call<ApiResponseJson>,
                response: Response<ApiResponseJson>
            ) {
                if (response.isSuccessful) {
                    val apiresp = response.body()
                    if (apiresp?.status == "success" && apiresp.data != null) {
                        rec_hist.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        val adapter=HistoryDialogAdapter(apiresp.data,requireContext())
                        rec_hist?.adapter=adapter
                    }
                }else{
                    Toast.makeText(context, "داده ای ای یافت نشد!", Toast.LENGTH_LONG).show()

                }
            }

            override fun onFailure(call: Call<ApiResponseJson>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()

            }

        })

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