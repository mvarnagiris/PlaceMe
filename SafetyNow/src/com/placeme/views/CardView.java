package com.placeme.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.placeme.R;
import com.placeme.model.CardInfo;

public abstract class CardView extends ViewGroup
{
	protected final int			SEPARATOR_HEIGHT;

	protected final Paint		separatorPaint;
	protected final Paint		bgPaint;
	protected final Rect		bgRect	= new Rect();

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

		int padding = getResources().getDimensionPixelSize(R.dimen.margin_normal);
		setPadding(padding, padding, padding, padding);

		// Init
		SEPARATOR_HEIGHT = getResources().getDimensionPixelSize(R.dimen.card_separator);
		final int accent = getResources().getColor(R.color.accent);
		separatorPaint = new Paint();
		separatorPaint.setColor(accent);
		separatorPaint.setStyle(Style.STROKE);
		separatorPaint.setStrokeWidth(SEPARATOR_HEIGHT);
		bgPaint = new Paint();
		bgPaint.setColor(getResources().getColor(R.color.bg_card));

		// Init views
		title_TV = new TextView(context);
		title_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_large));
		title_TV.setTextColor(accent);
		title_TV.setPadding(padding, 0, 0, 0);
		content_V = initContentView(context);

		// Add views
		addView(content_V);
		addView(title_TV);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		bgRect.set(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
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

	// Draw
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		canvas.drawRect(bgRect, bgPaint);

		final int left = getPaddingLeft();
		final int right = getMeasuredWidth() - getPaddingRight();
		canvas.drawLine(left, getPaddingTop() + (SEPARATOR_HEIGHT / 2), right, getPaddingTop() + (SEPARATOR_HEIGHT / 2), separatorPaint);

		final int secondY = getMeasuredHeight() - getPaddingBottom() - (SEPARATOR_HEIGHT / 2);
		canvas.drawLine(left, secondY, right, secondY, separatorPaint);
		super.dispatchDraw(canvas);
	}

	// Layout
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight() - getPaddingLeft();
		final int height = width * 2 / 3;

		final int wMS = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		final int hMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		title_TV.measure(wMS, hMS);

		final int contentHMS = MeasureSpec.makeMeasureSpec(height - title_TV.getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - SEPARATOR_HEIGHT
						- SEPARATOR_HEIGHT, MeasureSpec.EXACTLY);
		content_V.measure(wMS, contentHMS);

		setMeasuredDimension(width + getPaddingRight() + getPaddingLeft(), height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int top = SEPARATOR_HEIGHT + getPaddingTop();
		final int left = getPaddingLeft();

		title_TV.layout(left, top, getMeasuredWidth() - getPaddingRight(), top + title_TV.getMeasuredHeight());
		content_V.layout(left, title_TV.getBottom(), title_TV.getRight(), title_TV.getBottom() + content_V.getMeasuredHeight());
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