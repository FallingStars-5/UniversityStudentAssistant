package com.srtp.assistant.ui.campus.showingCampus

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.srtp.assistant.R
import com.srtp.assistant.logic.model.getCampusAddress
import kotlinx.android.synthetic.main.activity_showing_campus.*

class ShowingCampusActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(ShowingCampusViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showing_campus)

        setSupportActionBar(toolbar_showing_campus)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_showing_campus.setNavigationOnClickListener{
            finish()
        }

        showCampus()

    }

    private fun showCampus() {
        val id = intent.getLongExtra("campus_address_id",0)
        val campusInfo = viewModel.findCampusAddressById(id)
        if (campusInfo != null) {
            showing_title.text = campusInfo.name
        }
        init()
        if (campusInfo != null) {
            showing_campus_webView.loadUrl(campusInfo.address)
        }
        showing_campus_webView.webViewClient = MyViewClient()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        showing_campus_webView.settings.javaScriptEnabled = true
        showing_campus_webView.addJavascriptInterface(InJavaScriptLocalObj(), "kotlin_obj")
        showing_campus_webView.settings.setSupportZoom(true)
        showing_campus_webView.settings.builtInZoomControls = true
        showing_campus_webView.settings.domStorageEnabled = true
        showing_campus_webView.settings.useWideViewPort = true
        showing_campus_webView.requestFocus()
        showing_campus_webView.settings.loadWithOverviewMode = true
    }

    private class MyViewClient: WebViewClient() {
//        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
//            view.loadUrl(url!!)
//            return true
//        }

        override fun onPageFinished(view: WebView, url: String?) {
            Log.e("dan", "onPageFinished ")
            view.loadUrl(
                "javascript:window.kotlin_obj.showSource('<head>'+" +
                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');"
            )
            super.onPageFinished(view, url)
        }
    }

    private class InJavaScriptLocalObj{
        @JavascriptInterface
        fun showSource(html:String){

            //Log.e("dan",html)
        }
    }

    /**
     * 重写返回键函数，回退网页
     */
    override fun onBackPressed() {

        if (showing_campus_webView.canGoBack()){
            showing_campus_webView.goBack()
        }else{
            super.onBackPressed()
        }
    }
}

