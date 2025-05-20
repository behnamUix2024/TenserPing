package com.behnamuix.tenserpingx.Network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import com.behnamuix.tenserpingx.MyTools.Object.GetServer
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class UploadTester(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    interface UploadCallback {
        fun onProgress(percent: Int)
        fun onSuccess(uploadSpeed: Double, timeTaken: Long)
        fun onFailure(error: String)
    }

    fun testUpload(file: File, callback: UploadCallback) {
        if (!isNetworkAvailable()) {
            callback.onFailure("خطای دسترسی به شبکه اینترنت!")
            return
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("application/octet-stream".toMediaType())
            )
            .build()

        val progressRequestBody = ProgressRequestBody(requestBody) { bytesWritten, totalBytes ->
            val percent = (bytesWritten * 100 / totalBytes).toInt()
            callback.onProgress(percent)
        }


        val server = GetServer.getRandomServer(
            "https://tmpfiles.org/api/v1/upload",
            "https://eu.httpbin.org/post"
        )
        val request = Request.Builder()
            .url(server)
            .post(progressRequestBody)
            .build()
       Log.i("server", "server in use :$server")
        val startTime = System.nanoTime()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onFailure("Server error: ${response.code}")
                    return
                }

                val endTime = System.nanoTime()
                val timeTaken = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
                val fileSizeInMB = file.length().toDouble() / (1024 * 1024)
                val timeTakenInSeconds = timeTaken.toDouble() / 1000
                val uploadSpeed = fileSizeInMB / timeTakenInSeconds // MB/s

                callback.onSuccess(uploadSpeed, timeTaken)
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