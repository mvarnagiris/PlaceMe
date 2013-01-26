package com.placeme.views;

import com.code44.imageloader.ImageLoader;
import com.code44.imageloader.ImageSettings;
import com.code44.imageloader.ImageSettings.SizeType;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageCardView extends CardView
{
	private final ImageLoader	imageLoader;
	private ImageSettings		imageSettings	= null;

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
}