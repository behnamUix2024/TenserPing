package com.behnamuix.tenserpingx.Network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.ByteString
import okio.IOException
import java.net.InetAddress
import kotlin.math.pow
import kotlin.random.Random


class InternetSpeedTester(private val ctx: Context) {
    private val client = OkHttpClient()
    suspend fun getPingSpeed(host: String = "216.239.38.120", count: Int = 4): Long? = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()
            val inetAddress = InetAddress.getByName(host)
            val reachable = inetAddress.isReachable(1000) // Timeout of 1 second
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
    suspend fun getDownloadSpeed(url: String, fileSizeIInByte: Long = 10 * 1024 * 1024): Double? =
        withContext(Dispatchers.IO) {
            val req = Request.Builder().url(url).build()
            val startTime = System.currentTimeMillis()
            try {
                client.newCall(req).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext null
                    }
                    val contentLength = response.body?.contentLength() ?: fileSizeIInByte
                    val endTime = System.currentTimeMillis()
                    val durationSeconds = (endTime - startTime) / 1000.0
                    if (durationSeconds <= 0) {
                        return@withContext null
                    }
                    val bytesPerSecond = contentLength / durationSeconds
                    Log.d("DownloadTest", "StartTime: $startTime")
                    Log.d("DownloadTest", "EndTime: $endTime")
                    Log.d("DownloadTest", "ContentLength: $contentLength")
                    Log.d("DownloadTest", "DurationSeconds: $durationSeconds")
                    Log.d("DownloadTest", "BytesPerSecond: $bytesPerSecond")
                    val downloadSpeedMbps = (bytesPerSecond * 8) / 1000.0.pow(2.0)
                    Log.d("DownloadTest", "DownloadSpeedMbps (Calculated): $downloadSpeedMbps")
                    return@withContext (bytesPerSecond * 8) / 1000.0.pow(2.0)

                }
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext null
            }
        }

    suspend fun getUploadSpeed(
        uploadUrl: String,
        fileSizeInBytes: Long = 1 * 1024 * 1024
    ): Double? = withContext(Dispatchers.IO) {
        val randomBytes = ByteArray(fileSizeInBytes.toInt())
        Random.Default.nextBytes(randomBytes)
        val reqBody = object : okhttp3.RequestBody() {
            override fun contentType() = "application/octet-stream".toMediaTypeOrNull()
            override fun contentLength() = fileSizeInBytes
            override fun writeTo(sink: BufferedSink) {
                sink.write(randomBytes)
            }
        }
        val req = Request.Builder().url(uploadUrl).post(reqBody).build()
        val startTime = System.currentTimeMillis()
        try {
            client.newCall(req).execute().use { res ->
                if (!res.isSuccessful) {
                    return@withContext null
                }
                val endTime = System.currentTimeMillis()
                val durationSecond = (endTime - startTime) / 1000.0
                if (durationSecond <= 0) {
                    return@withContext null
                }
                val bytesPerSec = fileSizeInBytes / durationSecond
                return@withContext (bytesPerSec * 8) / 1000.0.pow(2.0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext null
        }
    }


}