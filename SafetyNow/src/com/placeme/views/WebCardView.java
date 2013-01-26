package com.placeme.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.placeme.utils.WebViewUtils;

public class WebCardView extends CardView
{
	public WebCardView(Context context)
	{
		this(context, null);
	}

	public WebCardView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public WebCardView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	// CardView
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected View initContentView(Context context)
	{
		return WebViewUtils.createWebView(context);
	}

	@Override
	public void setData(String data)
	{

	}
}