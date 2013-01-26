package com.placeme.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageCardView extends CardView
{
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