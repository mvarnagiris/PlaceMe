package com.placeme.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.code44.imageloader.ImageLoader;
import com.code44.imageloader.ImageSettings;
import com.code44.imageloader.ImageSettings.SizeType;
import com.code44.imageloader.info.URLBitmapInfo;

public class ImageCardView extends CardView
{
	private final ImageLoader	imageLoader;
	private ImageSettings		imageSettings	= null;
	private String				url				= null;
	private boolean				needLoadImage	= false;

	public ImageCardView(Context context)
	{
		this(context, null);
	}

	public ImageCardView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ImageCardView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		imageLoader = new ImageLoader(context);
	}

	// Layout
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		imageSettings = new ImageSettings.Builder().withSize(content_V.getMeasuredWidth(), content_V.getMeasuredHeight()).withSizeType(SizeType.MAX).build();
		if (!TextUtils.isEmpty(url) && imageSettings != null && needLoadImage)
		{
			needLoadImage = false;
			imageLoader.loadImage((ImageView) content_V, new URLBitmapInfo(url));
		}
	}

	// CardView
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected View initContentView(Context context)
	{
		final ImageView image_V = new ImageView(context);
		image_V.setScaleType(ScaleType.CENTER_INSIDE);
		return image_V;
	}

	@Override
	public void setData(String data)
	{
		url = data;
		if (!TextUtils.isEmpty(url) && imageSettings != null)
			imageLoader.loadImage((ImageView) content_V, new URLBitmapInfo(url));
		else
			needLoadImage = true;
	}
}