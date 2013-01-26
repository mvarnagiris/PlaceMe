package com.placeme.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public abstract class CardView extends ViewGroup
{
	private final View	content_V;

	public CardView(Context context)
	{
		this(context, null);
	}

	public CardView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public CardView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		// TODO TEMP
		setBackgroundColor(Color.RED);

		// Init views
		content_V = initContentView(context);
	}

	// Layout
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = width * 2 / 3;

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		content_V.layout(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingRight());
	}

	// Abstract methods
	// --------------------------------------------------------------------------------------------------------------------------------

	protected abstract View initContentView(Context context);
}