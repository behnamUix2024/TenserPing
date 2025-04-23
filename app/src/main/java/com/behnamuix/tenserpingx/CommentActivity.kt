package com.behnamuix.tenserpingx

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.behnamuix.tenserpingx.databinding.ActivityCommentBinding
import com.behnamuix.tenserpingx.databinding.ActivityMainBinding


class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding

    private lateinit var webView:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        webView=binding.webView
        val webSetting=webView.settings
        webSetting.javaScriptEnabled=true
        webView.webViewClient= WebViewClient()
        webView.loadUrl("https://behnamuix2024.com/comment.php")


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