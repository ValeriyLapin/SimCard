package com.example.simcard.web;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.example.simcard.R;


public class ButteryProgressBar extends View {

	private final GradientDrawable mShadow;
	private final ValueAnimator mAnimator;

	private final Paint mPaint = new Paint();

	private final int mBarColor;
	private final int mSolidBarHeight;
	private final int mSolidBarDetentWidth;

	private final float mDensity;

	private int mSegmentCount;

	/**
	 * The baseline width that the other constants below are optimized for.
	 */
	private static final int BASE_WIDTH_DP = 300;
	/**
	 * A reasonable animation duration for the given width above. It will be
	 * weakly scaled up and down for wider and narrower widths, respectively--
	 * the goal is to provide a relatively constant detent velocity.
	 */
	private static final int BASE_DURATION_MS = 500;
	/**
	 * A reasonable number of detents for the given width above. It will be
	 * weakly scaled up and down for wider and narrower widths, respectively.
	 */
	private static final int BASE_SEGMENT_COUNT = 5;

	private static final int DEFAULT_BAR_HEIGHT_DP = 4;
	private static final int DEFAULT_DETENT_WIDTH_DP = 4;
	public static boolean fromLeftToRight = false;
	public static boolean withCenter = false;

	private ButteryProgressBar(Context c) {
		this(c, null);
	}

	private ButteryProgressBar(Context c, AttributeSet attrs) {
		super(c, attrs);

		mDensity = c.getResources().getDisplayMetrics().density;

		mBarColor = c.getResources().getColor(R.color.holo_blue_light);
		mSolidBarHeight = Math.round(DEFAULT_BAR_HEIGHT_DP * mDensity);
		mSolidBarDetentWidth = Math.round(DEFAULT_DETENT_WIDTH_DP * mDensity);

		mAnimator = new ValueAnimator();
		mAnimator.setFloatValues(2.0f, 1.0f);
		mAnimator.setRepeatCount(ValueAnimator.INFINITE);
		mAnimator.setInterpolator(new ExponentialInterpolator());
		mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				invalidate();
			}

		});

		mPaint.setColor(mBarColor);

		mShadow = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
				new int[] { (mBarColor & 0x00ffffff) | 0x22000000, 0 });
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (changed) {
			final int w = getWidth();

			mShadow.setBounds(0, mSolidBarHeight, w, getHeight()
					- mSolidBarHeight);

			final float widthMultiplier = w / mDensity / BASE_WIDTH_DP;
			// simple scaling by width is too aggressive, so dampen it first
			final float durationMult = 0.3f * (widthMultiplier - 1) + 1;
			final float segmentMult = 0.1f * (widthMultiplier - 1) + 1;
			mAnimator.setDuration((int) (BASE_DURATION_MS * durationMult));
			mSegmentCount = (int) (BASE_SEGMENT_COUNT * segmentMult);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!mAnimator.isStarted()) {
			return;
		}

		mShadow.draw(canvas);

		final float val = (Float) mAnimator.getAnimatedValue();
		// Log.i("onDraw","getAnimatedValue="+val);
		final int w = (withCenter) ? getWidth() / 2 : getWidth();

		// Because the left-most segment doesn't start all the way on the left,
		// and because it moves
		// towards the right as it animates, we need to offset all drawing
		// towards the left. This
		// ensures that the left-most detent starts at the left origin, and that
		// the left portion
		// is never blank as the animation progresses towards the right.
		final int offset = w >> mSegmentCount - 1;
		// segments are spaced at half-width, quarter, eighth (powers-of-two).
		// to maintain a smooth
		// transition between segments, we used a power-of-two interpolator.
		for (int i = 0; i < mSegmentCount; i++) {
			final float l = val * (w >> (i + 1));
			final float r = (i == 0) ? w + offset : l * 2;
			if (withCenter) {
				if (fromLeftToRight) {
					canvas.drawRect(l + mSolidBarDetentWidth - offset, 0, r
							- offset, mSolidBarHeight, mPaint);
					canvas.drawRect(2 * w - (r - offset), 0, 2 * w
							- (l + mSolidBarDetentWidth - offset),
							mSolidBarHeight, mPaint);

				} else {
					canvas.drawRect(w - (r - offset), 0, w
							- (l + mSolidBarDetentWidth - offset),
							mSolidBarHeight, mPaint);

					canvas.drawRect(w + l + mSolidBarDetentWidth - offset, 0, w
							+ r - offset, mSolidBarHeight, mPaint);
				}

			} else {
				if (fromLeftToRight) {
					canvas.drawRect(l + mSolidBarDetentWidth - offset, 0, r
							- offset, mSolidBarHeight, mPaint);
				} else {
					canvas.drawRect(w - (r - offset), 0, w
							- (l + mSolidBarDetentWidth - offset),
							mSolidBarHeight, mPaint);
				}
			}
		}
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);

		if (visibility == VISIBLE) {
			start();
		} else {
			stop();
		}
	}

	private void start() {
		if (mAnimator == null) {
			return;
		}
		mAnimator.start();
	}

	private void stop() {
		if (mAnimator == null) {
			return;
		}
		mAnimator.cancel();
	}

	private static class ExponentialInterpolator implements Interpolator {

		@Override
		public float getInterpolation(float input) {
			return (float) Math.pow(2.0, input) - 1;
		}

	}

	public static ButteryProgressBar getInstance(Context context) {
		final ButteryProgressBar obj = new ButteryProgressBar(context);
		obj.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 24));

		final FrameLayout decorView = (FrameLayout) ((Activity) context)
				.getWindow().getDecorView();
		decorView.addView(obj);
		final View contentView = decorView.findViewById(android.R.id.content);

		ViewTreeObserver observer = obj.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressLint("NewApi") @SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				obj.setY(contentView.getY());
				ViewTreeObserver observer = obj.getViewTreeObserver();
				
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					observer.removeGlobalOnLayoutListener(this);
				} else {
					observer.removeOnGlobalLayoutListener(this);
				}
			}
		});

		obj.setVisibility(View.INVISIBLE);
		return obj;

	}

}
