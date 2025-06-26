package com.behnamuix.tenserpingx.Network

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import okio.IOException
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
                        var bytesRead=0
                        var totalBytesRead: Long = 0

                        while (totalBytesRead < dataSizeKb * 1024L &&
                            inputStream.read(buffer).also { bytesRead = it } != -1) {
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
                Log.e("ALPHA",e.localizedMessage.toString())
                onSpeedChangeListener?.onError("Download test error: ${e.localizedMessage}")
            }
        }
    }
    suspend fun testUploadSpeed(
        uploadUrl: String = "https://eu.httpbin.org/post",
        dataSizeKB: Int = 2048 // 1MB default test data
    ): Double = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null

        try {
            // Generate random test data
            val testData = ByteArray(dataSizeKB * 1024) { (0..255).random().toByte() }

            val url = URL(uploadUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 15000
                readTimeout = 30000
                setRequestProperty("Content-Type", "application/octet-stream")
                setRequestProperty("Connection", "close")
            }

            val startTime = System.currentTimeMillis()

            // Start upload
            outputStream = connection.outputStream
            outputStream.write(testData)
            outputStream.flush()

            // Get response
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                Log.d("UploadTest", "Server response: $response")
            }

            val endTime = System.currentTimeMillis()
            val durationSeconds = (endTime - startTime) / 1000.0

            // Calculate speed in Mbps (Megabits per second)
            val speedMbps = (dataSizeKB * 8) / (durationSeconds * 1000)
            speedMbps.roundToInt().toDouble() // Return rounded value

        } catch (e: Exception) {
            Log.e("UploadTest", "Error: ${e.localizedMessage}")
            0.0 // Return 0 if failed
        } finally {
            outputStream?.close()
            inputStream?.close()
            connection?.disconnect()
        }
    }





    private val client = OkHttpClient()
    suspend fun getPingSpeed(host: String = "185.147.178.12", count: Int = 3): Long? = withContext(Dispatchers.IO) {
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
                val rtts = rttRegex.findAll(output).mapNotNull { it.groupValues[1].toFloatOrNull() }
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