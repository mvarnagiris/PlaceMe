package com.placeme.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.placeme.R;
import com.placeme.model.CardInfo;

public abstract class CardView extends ViewGroup
{
	protected final TextView	title_TV;
	protected final View		content_V;

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
		title_TV = new TextView(context);
		title_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_large));
		content_V = initContentView(context);

		// Add views
		addView(content_V);
		addView(title_TV);
	}

	// Instance
	// --------------------------------------------------------------------------------------------------------------------------------

	public static CardView newInstance(Context context, CardInfo cardInfo)
	{
		final CardView card_V = cardInfo.getType().equals("png") ? new ImageCardView(context) : new WebCardView(context);
		card_V.setTitle(cardInfo.getTitle());
		card_V.setData(cardInfo.getContent());

		return card_V;
	}

	// Layout
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = width * 2 / 3;

		final int wMS = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		final int hMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		title_TV.measure(wMS, hMS);

		final int contentHMS = MeasureSpec.makeMeasureSpec(height - title_TV.getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
		content_V.measure(wMS, contentHMS);

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		title_TV.layout(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), title_TV.getMeasuredHeight());
		content_V.layout(getPaddingLeft(), title_TV.getBottom(), title_TV.getRight(), title_TV.getBottom() + content_V.getMeasuredHeight());
	}

	// Public method
	// --------------------------------------------------------------------------------------------------------------------------------

	public void setTitle(String title)
	{
		title_TV.setText(title);
	}

	// Abstract methods
	// --------------------------------------------------------------------------------------------------------------------------------

	protected abstract View initContentView(Context context);

	public abstract void setData(String data);
}