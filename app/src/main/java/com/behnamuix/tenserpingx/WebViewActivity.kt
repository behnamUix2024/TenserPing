package com.behnamuix.tenserpingx

import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.behnamuix.tenserpingx.databinding.ActivityCommentBinding


class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private lateinit var webView: WebView
    private lateinit var swipe: SwipeRefreshLayout

    companion object {
        private const val POLICY_URL = "https://behnamuix2024.com/api/policy.html"
        private const val BIO_URL = "https://behnamuix2024.com/api/bio.html"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        initViews()
        handleIntent()
    }

    private fun initViews() {
        webView = binding.webView
        swipe = binding.swipeRefreshLayout
    }

    private fun handleIntent() {
        val key = intent.getStringExtra("policy") ?: run {
            Toast.makeText(this, "Invalid page requested", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUrl(key)
    }

    private fun loadUrl(key: String) {
        configWebViewSettings()

        try {
            val url = getUrlForKey(key)
            webView.loadUrl(url)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Invalid page requested", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupRefreshLayout()
    }

    private fun configWebViewSettings() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = false
        webSettings.databaseEnabled = false
    }

    private fun getUrlForKey(key: String): String {
        return when (key) {
            "bio" -> BIO_URL
            "policy" -> POLICY_URL
            else -> throw IllegalArgumentException("Invalid key: $key")
        }
    }

    private fun setupRefreshLayout() {
        swipe.setOnRefreshListener {
            webView.reload()
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                swipe.isRefreshing = false
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                swipe.isRefreshing = false }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}