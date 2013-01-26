package com.placeme.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class DrawerView extends FrameLayout implements OnClickListener
{
	public static final int				STATE_CLOSED			= 0;
	public static final int				STATE_ANIMATING_OPEN	= 1;
	public static final int				STATE_ANIMATING_CLOSE	= 2;
	public static final int				STATE_MOVING			= 3;
	public static final int				STATE_OPEN				= 4;

	protected static final int			ANIMATION_DURATION		= 400;

	protected View						drawer_V;
	protected View						content_V;

	protected final Scroller			scroller				= new Scroller(getContext(), new DrawerInterpolator());
	protected final ViewConfiguration	viewConfiguration		= ViewConfiguration.get(getContext());
	protected final GradientDrawable	shadowDrawable;
	protected final Paint				drawerOverlayPaint		= new Paint();
	protected final Rect				drawerOverlayRect		= new Rect();
	protected final int					shadowWidth;
	protected VelocityTracker			velocityTracker			= null;
	protected int						state					= STATE_CLOSED;
	protected int						drawerWidth				= 0;
	protected int						drawerInitialOffset		= 0;
	protected float						drawerOffsetWidthRatio	= 0;

	protected float						touchX;
	protected int						contentCurrentOffset	= 0;
	protected int						drawerCurrentOffset		= 0;

	public DrawerView(Context context)
	{
		this(context, null);
	}

	public DrawerView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public DrawerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		// Shadow
		shadowDrawable = new GradientDrawable(Orientation.RIGHT_LEFT, new int[] { 0xFF333333, 0x00333333 });
		shadowWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

		// Overlay
		drawerOverlayPaint.setColor(Color.BLACK);
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();

		if (getChildCount() == 2)
		{
			drawer_V = getChildAt(0);
			drawer_V.setVisibility(View.GONE);
			content_V = getChildAt(1);
		}
	}

	// Layout
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		calcDrawerSizes();
		shadowDrawable.setBounds(-shadowWidth, 0, 0, h);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		content_V.offsetLeftAndRight(contentCurrentOffset);
		drawer_V.offsetLeftAndRight(drawerCurrentOffset);
	}

	// Draw
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		// Draw the children
		super.dispatchDraw(canvas);

		if (state != STATE_CLOSED)
		{
			// Overlay
			canvas.drawRect(drawerOverlayRect, drawerOverlayPaint);

			// Shadow
			canvas.save();
			canvas.translate(contentCurrentOffset, 0);
			shadowDrawable.draw(canvas);
			canvas.restore();
		}
	}

	// Public methods
	// --------------------------------------------------------------------------------------------------------------------------------

	public void toggle(boolean animated)
	{
		if (state == STATE_CLOSED || state == STATE_ANIMATING_CLOSE)
			openDrawer(animated);
		else if (state == STATE_OPEN || state == STATE_ANIMATING_OPEN)
			closeDrawer(animated);
	}

	public void openDrawer(boolean animated)
	{
		// Cancel current animation
		cancelCurrentAnimation();

		if (animated)
		{
			onStartOpening();
			animateOpen();
		}
		else
		{
			onStartOpening();
			content_V.offsetLeftAndRight(drawerWidth - contentCurrentOffset);
			contentCurrentOffset = drawerWidth;
			onOpened();
		}
	}

	public void closeDrawer(boolean animated)
	{
		// Cancel current animation
		cancelCurrentAnimation();

		if (animated)
		{
			onStartClosing();
			animateClose();
		}
		else
		{
			onStartClosing();
			content_V.offsetLeftAndRight(-contentCurrentOffset);
			contentCurrentOffset = 0;
			onClosed();
		}
	}

	// Protected methods
	// --------------------------------------------------------------------------------------------------------------------------------

	protected void updateOffset(int deltaX)
	{
		final int newOffset = contentCurrentOffset + deltaX;
		if (newOffset >= 0 && newOffset <= drawerWidth)
		{
			// Content
			content_V.offsetLeftAndRight(deltaX);
			contentCurrentOffset = content_V.getLeft();

			// Drawer
			drawer_V.offsetLeftAndRight(((int) (contentCurrentOffset * drawerOffsetWidthRatio + drawerInitialOffset)) - drawer_V.getLeft());
			drawerCurrentOffset = drawer_V.getLeft();
		}
		else if (newOffset < 0 && contentCurrentOffset > 0)
		{
			// Content
			content_V.offsetLeftAndRight(-contentCurrentOffset);
			contentCurrentOffset = 0;

			// Drawer
			drawer_V.offsetLeftAndRight(-drawerCurrentOffset);
			drawerCurrentOffset = drawerInitialOffset;
		}
		else if (newOffset > drawerWidth && contentCurrentOffset < drawerWidth)
		{
			// Content
			content_V.offsetLeftAndRight(drawerWidth - contentCurrentOffset);
			contentCurrentOffset = drawerWidth;

			// Drawer
			drawer_V.offsetLeftAndRight(-drawerCurrentOffset);
			drawerCurrentOffset = 0;
		}
		drawerOverlayPaint.setAlpha(Math.max(200 - (int) (255.0f * contentCurrentOffset / drawerWidth), 0));
		drawerOverlayRect.set(0, 0, contentCurrentOffset, getMeasuredHeight());
		invalidate();
	}

	protected void cancelCurrentAnimation()
	{
		removeCallbacks(updateOffsetRunnable);
	}

	protected void animateOpen()
	{
		onStartAnimationOpen();
		scroller.startScroll(contentCurrentOffset, 0, drawerWidth - contentCurrentOffset, 0, ANIMATION_DURATION);
		post(updateOffsetRunnable);
	}

	protected void animateClose()
	{
		onStartAnimationClose();
		scroller.startScroll(contentCurrentOffset, 0, -contentCurrentOffset, 0, ANIMATION_DURATION);
		post(updateOffsetRunnable);
	}

	protected void animateDrawer(final boolean open, boolean decelerateOnly)
	{
		// cancelDrawerAnimation();
		// animatorSet = new AnimatorSet();
		// final float contentTranslationX = ViewHelper.getTranslationX(shadow_V);
		// final float[] values = new float[] { contentTranslationX, open ? drawerWidth : 0 };
		// final ObjectAnimator contentAnimator = ObjectAnimator.ofFloat(content_V, "translationX", values);
		// final ObjectAnimator shadowAnimator = ObjectAnimator.ofFloat(shadow_V, "translationX", values);
		// final ObjectAnimator drawerAnimator = ObjectAnimator.ofFloat(drawer_V, "translationX", new float[] {
		// drawerInitialOffset + contentTranslationX * drawerOffsetWidthRatio, open ? 0 : drawerInitialOffset });
		// final float ratio = (open ? drawerWidth - contentTranslationX : contentTranslationX) / drawerWidth;
		// long duration = (long) (ANIMATION_DURATION * ratio);
		// final Interpolator interpolator = decelerateOnly ? new DecelerateInterpolator(2.5f) : new AccelerateDecelerateInterpolator();
		// contentAnimator.setDuration(duration);
		// contentAnimator.setInterpolator(interpolator);
		// shadowAnimator.setDuration(duration);
		// shadowAnimator.setInterpolator(interpolator);
		// drawerAnimator.setDuration(duration);
		// drawerAnimator.setInterpolator(interpolator);
		// animatorSet.addListener(new AnimatorListener()
		// {
		// @Override
		// public void onAnimationStart(Animator animator)
		// {
		// if (open)
		// {
		// onStartOpening();
		// onStartAnimationOpen();
		// }
		// else
		// {
		// onStartClosing();
		// onStartAnimationClose();
		// }
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animator animator)
		// {
		// }
		//
		// @Override
		// public void onAnimationEnd(Animator animator)
		// {
		// if (open && ViewHelper.getTranslationX(content_V) == drawerWidth)
		// onOpened();
		// else if (!open && ViewHelper.getTranslationX(content_V) == 0)
		// onClosed();
		// }
		//
		// @Override
		// public void onAnimationCancel(Animator animator)
		// {
		// }
		// });
		// animatorSet.playTogether(contentAnimator, shadowAnimator, drawerAnimator);
		// animatorSet.start();
	}

	protected void cancelDrawerAnimation()
	{
		// if (animatorSet != null && animatorSet.isRunning())
		// animatorSet.cancel();
	}

	protected void calcDrawerSizes()
	{
		drawerWidth = (int) (getMeasuredWidth() - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64.0f, getResources().getDisplayMetrics()));
		drawer_V.getLayoutParams().width = drawerWidth;
		drawerInitialOffset = -drawerWidth / 3;
		drawerCurrentOffset = drawerInitialOffset;
		drawerOffsetWidthRatio = -(float) drawerInitialOffset / (float) drawerWidth;
	}

	protected void onStartOpening()
	{
		drawer_V.setVisibility(View.VISIBLE);
	}

	protected void onStartAnimationOpen()
	{
		state = STATE_ANIMATING_OPEN;
	}

	protected void onOpened()
	{
		state = STATE_OPEN;
	}

	protected void onStartClosing()
	{
	}

	protected void onStartAnimationClose()
	{
		state = STATE_ANIMATING_CLOSE;
	}

	protected void onClosed()
	{
		state = STATE_CLOSED;
		drawer_V.setVisibility(View.GONE);
	}

	// OnClickListener
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	public void onClick(View v)
	{
	}

	// Touch
	// --------------------------------------------------------------------------------------------------------------------------------

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				touchX = ev.getX();
				if (state == STATE_CLOSED || state == STATE_OPEN)
				{
					// If drawer is fully open or closed then we wait until we are sure that user actually intends to scroll
					return false;
				}
				else
				{
					// If drawer is already animating, stop animation and change to user moving state.
					cancelCurrentAnimation();
					state = STATE_MOVING;
					return false;
				}

			case MotionEvent.ACTION_MOVE:
				// Check if user actually intends to scroll
				final int deltaX = (int) Math.abs(touchX - ev.getX());
				if (deltaX > viewConfiguration.getScaledTouchSlop())
				{
					cancelCurrentAnimation();
					if (state == STATE_CLOSED)
						onStartOpening();
					else if (state == STATE_OPEN)
						onStartClosing();
					state = STATE_MOVING;

					return true;
				}
				break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{

			case MotionEvent.ACTION_MOVE:
			{
				if (state == STATE_MOVING)
				{
					// Update velocity
					if (velocityTracker == null)
						velocityTracker = VelocityTracker.obtain();
					velocityTracker.addMovement(ev);

					// Calculate deltaX
					final int deltaX = (int) (ev.getX() - touchX);
					touchX = ev.getX();

					// Update offset
					updateOffset(deltaX);

					return true;
				}
				break;
			}

			case MotionEvent.ACTION_UP:
			{
				// Update and get velocity
				final float velocity;
				if (velocityTracker != null)
				{
					velocityTracker.addMovement(ev);
					velocityTracker.computeCurrentVelocity(1000);
					velocity = velocityTracker.getXVelocity();
					velocityTracker.recycle();
					velocityTracker = null;
				}
				else
				{
					velocity = 0;
				}

				// Animate view
				if (Math.abs(velocity) > viewConfiguration.getScaledMinimumFlingVelocity())
				{
					if (velocity > 0)
						animateOpen();
					else
						animateClose();
				}
				else
				{
					if (contentCurrentOffset > (getMeasuredWidth() / 2))
						animateOpen();
					else
						animateClose();
				}

				return true;
			}
		}
		return super.onTouchEvent(ev);
	}

	// Runnable
	// --------------------------------------------------------------------------------------------------------------------------------

	private final Runnable	updateOffsetRunnable	= new Runnable()
													{
														@Override
														public void run()
														{
															if (!scroller.computeScrollOffset())
															{
																if (scroller.getFinalX() == 0)
																	onClosed();
																else
																	onOpened();
																return;
															}

															final int deltaX = scroller.getCurrX() - contentCurrentOffset;
															updateOffset(deltaX);
															post(this);
														}
													};

	// Interpolator
	// --------------------------------------------------------------------------------------------------------------------------------

	private static class DrawerInterpolator implements Interpolator
	{
		@Override
		public float getInterpolation(float input)
		{
			return (float) (Math.pow(input - 1, 5) + 1);
		}
	}
}