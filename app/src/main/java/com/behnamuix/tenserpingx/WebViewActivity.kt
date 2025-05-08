package com.behnamuix.tenserpingx

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.behnamuix.tenserpingx.databinding.ActivityCommentBinding


class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private var URL1 = "https://behnamuix2024.com/api/policy.html"
    private var URL2 = "https://behnamuix2024.com/api/bio.html"
    private lateinit var webView: WebView
    private lateinit var swipe: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        Main()

    }

    private fun Main() {
        config()
    }

    private fun config() {
        loadUrl(URL1)


    }

    private fun loadUrl(url: String) {
        webView = binding.webView
        val webSetting = webView.settings
        // غیرفعال کردن ویژگی‌های خطرناک
        // غیرفعال کردن ویژگی‌های خطرناک
        webSetting.domStorageEnabled = false
        webSetting.databaseEnabled = false
        webSetting.allowFileAccess = false
        webSetting.allowContentAccess = false
        webSetting.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
        swipe = binding.swipeRefreshLayout
        swipe.setOnRefreshListener {
            webView.reload()

        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipe.isRefreshing = false
            }
        }
    }

    // فعال کردن دکمه بازگشت برای رفتن به صفحات قبلی WebView
    override fun onBackPressed() {
        val myWebView: WebView = binding.webView
        if (myWebView.canGoBack()) {
            myWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}