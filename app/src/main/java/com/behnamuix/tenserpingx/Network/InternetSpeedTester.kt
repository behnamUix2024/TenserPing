package com.behnamuix.tenserpingx.Network

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.BufferedSink
import okio.IOException
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.util.concurrent.ExecutorService
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random


class InternetSpeedTester(private val ctx: Context) {
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var onSpeedChangeListener: OnSpeedChangeListener? = null
    private var isTesting: Boolean = false
    var random: Random
    init {
        random = Random(System.currentTimeMillis()) // مقداردهی در init
    }

    interface OnSpeedChangeListener {
        fun onDownloadSpeedChanged(mbps: Double)
        fun onUploadSpeedChanged(mbps: Double)
        fun onTestStarted()
        fun onTestFinished()
        fun onError(message: String)
    }

    fun setOnSpeedChangeListener(listener: OnSpeedChangeListener) {
        this.onSpeedChangeListener = listener
    }

    fun startDownloadSpeedTest(
        downloadTestUrl: String = "https://httpbin.org/bytes/5242880", // Endpoint تست دانلود معتبر
        uploadTestUrl: String? = null,
        testDataSizeKb: Int = 3072
    ) {
        if (isTesting) {
            onSpeedChangeListener?.onError("عملیات تست در حال اجرا میباشد!")
            return
        }
        isTesting = true
        onSpeedChangeListener?.onTestStarted()

        executorService.execute {
            testDownloadSpeed(downloadTestUrl, testDataSizeKb)

            mainHandler.post {
                isTesting = false
                onSpeedChangeListener?.onTestFinished()
            }
        }
    }

    fun stopSpeedTest() {
        executorService.shutdownNow()
        isTesting = false
        onSpeedChangeListener?.onTestFinished()
    }

    private fun testDownloadSpeed(downloadUrl: String, dataSizeKb: Int) {
        var startTime: Long = 0
        var endTime: Long = 0

        try {
            val url = URL(downloadUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10000 // 10 seconds timeout
                readTimeout = 10000 // 10 seconds timeout
            }

            try {
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    startTime = System.currentTimeMillis()
                    connection.inputStream.use { inputStream ->
                        val buffer = ByteArray(1024)
                        var bytesRead = 0
                        var totalBytesRead: Long = 0

                        while (totalBytesRead < dataSizeKb * 1024L &&
                            inputStream.read(buffer).also { bytesRead = it } != -1
                        ) {
                            totalBytesRead += bytesRead
                        }
                        endTime = System.currentTimeMillis()

                        val timeTakenSeconds = (endTime - startTime) / 1000.0
                        if (timeTakenSeconds > 0) {
                            val bytesPerSecond = totalBytesRead / timeTakenSeconds
                            val mbps = bytesPerSecond * 8.0 / (1024.0 * 1024.0)
                            mainHandler.post {

                                onSpeedChangeListener?.onDownloadSpeedChanged(mbps)
                            }
                        } else {
                            mainHandler.post {
                                onSpeedChangeListener?.onError("Download test took too short or failed.")
                            }
                        }
                    }
                } else {
                    mainHandler.post {
                        onSpeedChangeListener?.onError("HTTP error: $responseCode")
                    }
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            mainHandler.post {
                Log.e("ALPHA", e.localizedMessage.toString())
                onSpeedChangeListener?.onError("Download test error: ${e.localizedMessage}")
            }
        }
    }

    fun createRandomFile(fileSizeMB: Int = 5): File {
        val file = File.createTempFile("upload_test_", ".bin")
        val bytes = ByteArray(fileSizeMB * 1024 * 1024)
        random.nextBytes(bytes) // پر کردن فایل با داده تصادفی
        file.writeBytes(bytes)
        println("فایل موقت ساخته شد: ${file.absolutePath} (${fileSizeMB} مگابایت)")
        return file
    }

    fun testUploadSpeed(file: File): Double {
        val client = OkHttpClient()
        val requestBody = file.asRequestBody("application/octet-stream".toMediaType())

        val request = Request.Builder()
            .url("https://httpbin.org/post")
            .post(requestBody)
            .build()

        val startTime = System.nanoTime()
        val response = client.newCall(request).execute()
        val endTime = System.nanoTime()

        response.use {
            if (!it.isSuccessful) {
                throw RuntimeException("خطا در آپلود: ${it.code}")
            }
        }

        val uploadTimeSec = (endTime - startTime) / 1e9
        val fileSizeBits = file.length() * 8
        val speedMbps = (fileSizeBits / uploadTimeSec) / 1e6 // مگابیت بر ثانیه

        return speedMbps
    }

    suspend fun getPingSpeed(host: String = "185.147.178.12", count: Int = 3): Long? =
        withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()
                val inetAddress = InetAddress.getByName(host)
                val reachable = inetAddress.isReachable(200) // Timeout of 1 second
                val endTime = System.currentTimeMillis()
                if (reachable) {
                    return@withContext endTime - startTime
                } else {
                    // تلاش برای اجرای دستور ping سیستم عامل (کمتر قابل اعتماد و ممکن است محدود شود)
                    val process = ProcessBuilder("ping", "-c", count.toString(), host)
                        .redirectErrorStream(true)
                        .start()
                    val output = process.inputStream.bufferedReader().use { it.readText() }
                    val rttRegex = Regex("time=(\\d+\\.?\\d*) ms")
                    val rtts =
                        rttRegex.findAll(output).mapNotNull { it.groupValues[1].toFloatOrNull() }
                    if (rtts.any()) {
                        return@withContext rtts.average().toLong()
                    }
                }
                return@withContext null
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext null
            }
        }
    /**
     * اندازه‌گیری سرعت دانلود از یک URL مشخص و برگرداندن سرعت در مگابیت بر ثانیه (Mbps).
     * در صورت بروز خطا، null برمی‌گرداند.
     */


}