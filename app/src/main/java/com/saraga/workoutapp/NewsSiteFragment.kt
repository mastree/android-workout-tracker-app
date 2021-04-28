package com.saraga.workoutapp

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment


class NewsSiteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var link: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            link = it.getString("link", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news_site, container, false)
        val webview = view.findViewById<WebView>(R.id.webview)
        webview.settings.javaScriptEnabled = true
        webview.settings.allowContentAccess = true
        webview.settings.domStorageEnabled = true
        webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webview.webViewClient = WebViewClient()
        Log.println(Log.DEBUG, "link", link)
        webview.loadUrl(link)
        return view
    }
}