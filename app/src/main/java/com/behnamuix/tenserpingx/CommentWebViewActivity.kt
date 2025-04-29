package com.behnamuix.tenserpingx

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.behnamuix.tenserpingx.databinding.ActivityCommentBinding


class CommentWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private var URL="https://behnamuix2024.com/comment.php"
    private lateinit var webView:WebView
    private lateinit var swipe:SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        webView=binding.webView
        val webSetting=webView.settings
        webSetting.javaScriptEnabled=true
        webView.webViewClient=WebViewClient()
        webView.loadUrl(URL)
        swipe=binding.swipeRefreshLayout
        swipe.setOnRefreshListener {
            webView.reload()

        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Toast.makeText(applicationContext,"شما در حال استفاده از مرورگر داخلی اپلیکیشن هستید!",Toast.LENGTH_SHORT).show()
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