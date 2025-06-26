package com.behnamuix.tenserpingx.Network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import com.behnamuix.tenserpingx.MyTools.MoToast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class UploadTester(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC

        })
        .build()

    interface UploadCallback {
        fun onProgress(percent: Int)
        fun onSuccess(uploadSpeedMbps: Double, timeTakenMs: Long)
        fun onFailure(error: String)
    }

    fun testUpload(file: File, callback: UploadCallback) {
        if (!isNetworkAvailable()) {
            callback.onFailure("خطای دسترسی به شبکه اینترنت!")
            return
        }

        if (!file.exists() || file.length() == 0L) {
            callback.onFailure("فایل تست نامعتبر است")
            return
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                ProgressRequestBody(
                    file.asRequestBody("application/octet-stream".toMediaType())
                ) { bytesWritten, totalBytes ->
                    val percent = (bytesWritten * 100 / totalBytes).toInt()
                    callback.onProgress(percent)
                }
            )
            .build()

        val server = "https://httpbin.org/post"
        //    ""


        Log.d("UploadTester", "Selected server: $server")
        //Toast.makeText(context,"سرور انتخابی:$server",Toast.LENGTH_SHORT).show()

        val request = Request.Builder()
            .url(server)
            .post(requestBody)
            .build()

        val startTime = System.nanoTime()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("UploadTester", "Upload failed", e)
                callback.onFailure("خطا در اتصال: ${e.message ?: "Unknown error"}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: ""
                        Log.e("UploadTester", "Server error: ${response.code} - $errorBody")
                        callback.onFailure("خطای سرور: ${response.code}")
                        return
                    }

                    val endTime = System.nanoTime()
                    val timeTakenNanos = endTime - startTime
                    val timeTakenMs = TimeUnit.NANOSECONDS.toMillis(timeTakenNanos)

                    // محاسبه سرعت بر حسب مگابیت بر ثانیه (Mbps)
                    val fileSizeBits = file.length() * 8
                    val uploadSpeedMbps = (fileSizeBits / (timeTakenNanos / 1e9)) / 1e6

                    Log.d("UploadTester",
                        "Upload completed. Size: ${file.length() / (1024*1024)} MB, " +
                                "Time: ${timeTakenMs}ms, Speed: %.2f Mbps".format(uploadSpeedMbps))

                    callback.onSuccess(uploadSpeedMbps, timeTakenMs)
                } catch (e: Exception) {
                    Log.e("UploadTester", "Error processing response", e)
                    callback.onFailure("خطا در پردازش پاسخ سرور")
                } finally {
                    response.close()
                }
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}

class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val listener: (bytesWritten: Long, totalBytes: Long) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? = requestBody.contentType()

    override fun contentLength(): Long = requestBody.contentLength()

    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink).buffer()
        requestBody.writeTo(countingSink)
        countingSink.flush()
    }

    inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
        private var bytesWritten = 0L

        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            bytesWritten += byteCount
            listener(bytesWritten, contentLength())
        }
    }
}