package com.placeme.utils;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewUtils
{
	public static WebView createWebView(Context context)
	{
		final WebView web_V = new WebView(context);
		WebSettings webSettings = web_V.getSettings();
		webSettings.setJavaScriptEnabled(true);
		return web_V;
	}
}