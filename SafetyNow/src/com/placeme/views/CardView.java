package com.placeme.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
	protected final ImageView	caret_IV;

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
		setBackgroundResource(R.drawable.bg_card);
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
		caret_IV = new ImageView(context);
		caret_IV.setImageResource(R.drawable.ic_action_expand);
		title_TV = new TextView(context);
		title_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_large));
		title_TV.setTextColor(accent);
		title_TV.setPadding(padding * 2, 0, 0, 0);
		title_TV.setTypeface(Typeface.createFromAsset(context.getAssets(), "font/Roboto-Light.ttf"));
		content_V = initContentView(context);

		// Add views
		addView(content_V);
		addView(title_TV);
		addView(caret_IV);
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

	// Layout
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight() - getPaddingLeft();
		final int height = width * 2 / 3;

		final int wMS = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		final int hMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		caret_IV.measure(hMS, hMS);

		title_TV.measure(wMS, hMS);

		final int contentHMS = MeasureSpec.makeMeasureSpec(height - title_TV.getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - SEPARATOR_HEIGHT
						- SEPARATOR_HEIGHT, MeasureSpec.EXACTLY);
		content_V.measure(MeasureSpec.makeMeasureSpec(width - (title_TV.getPaddingLeft()), MeasureSpec.EXACTLY), contentHMS);

		setMeasuredDimension(width + getPaddingRight() + getPaddingLeft(),
						(int) (height + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics())));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int top = SEPARATOR_HEIGHT + getPaddingTop();
		final int left = getPaddingLeft();

		title_TV.layout(left, top, getMeasuredWidth() - getPaddingRight(), top + title_TV.getMeasuredHeight());
		content_V.layout(left + title_TV.getPaddingLeft() / 2, title_TV.getBottom(), title_TV.getRight() - title_TV.getPaddingLeft() / 2, title_TV.getBottom()
						+ content_V.getMeasuredHeight());
		caret_IV.layout(title_TV.getRight() - caret_IV.getMeasuredWidth() - title_TV.getPaddingLeft(), content_V.getBottom() - caret_IV.getMeasuredHeight()
						+ (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()),
						title_TV.getRight() - title_TV.getPaddingLeft(),
						(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()) + content_V.getBottom());
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