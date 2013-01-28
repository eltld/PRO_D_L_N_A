package com.brunjoy.weiget;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class LKCoverView extends HorizontalScrollView  implements Runnable{


	public LKCoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	Context mContext;
	public LKCoverView(Context context) {
		super(context);
		mContext=context;
		// TODO Auto-generated constructor stub
		// 取消边缘的阴影
		this.setHorizontalFadingEdgeEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
		// 取消滚动条显示
		this.setHorizontalScrollBarEnabled(false);
		this.setVerticalScrollBarEnabled(false);
		setBackgroundColor(Color.TRANSPARENT);
		// 设置布局
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(layoutParams);

		// 填充ScrollView
		contentLayout = new LinearLayout(mContext);
		// 主要显示的View
		contentView = new FrameLayout(mContext);
	}
	public void initLKCoverView(int viewWidth)
	{
		// 填充ScrollView		
		contentLayout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams contentLayoutParams = new LayoutParams(viewWidth * 3,
				LayoutParams.FILL_PARENT);
		contentLayout.setLayoutParams(contentLayoutParams);
		addView(contentLayout);
		contentLayout.setBackgroundColor(Color.TRANSPARENT);
		this.setPageWidth(viewWidth);

		// 主要显示的View		
		LinearLayout.LayoutParams contentViewParams = new LinearLayout.LayoutParams(
				viewWidth, LayoutParams.FILL_PARENT);
		contentViewParams.leftMargin = viewWidth;
		contentViewParams.rightMargin = viewWidth;
		contentView.setLayoutParams(contentViewParams);
		contentView.setBackgroundColor(Color.BLACK);
		contentLayout.addView(contentView);
		
		this.post(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				scrollTo(pageWidth, 0);		
			}
		});
	}
	int lastScrollX = 0;
	boolean isWaitComparison = false;
	int comparisonCount = 0;
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
	    lastScrollX = getScrollX();	    
	    if(!isWaitComparison)
	    {
	    	isWaitComparison = true;
	    	this.postDelayed(this, 500);	    	
	    }
	}
	OnLKCoverViewListener coverViewListener;
	
	public void setOnCoverViewListener(OnLKCoverViewListener coverViewListener) {
		this.coverViewListener = coverViewListener;
	}

	public void run() {
		// TODO Auto-generated method stub
//		Log.d("LKCoverView", " lastScrollX:" + lastScrollX + "  currentScrollX :" + getScrollX());
		if(lastScrollX == getScrollX())
		{
			comparisonCount ++;
			if(comparisonCount >= 2)
			{
				isWaitComparison = false;
				if(coverViewListener != null)
				{
					coverViewListener.onScrollFinished(this);
				}
			}
			else
			{
				this.postDelayed(this, 500);
			}
		}
		else
		{
			comparisonCount = 0;
			this.postDelayed(this, 500);
		}
	}
	private LinearLayout contentLayout;

	public LinearLayout getContentLayout() {
		return contentLayout;
	}

	private FrameLayout contentView;

	public FrameLayout getContentView() {
		return contentView;
	}

	private boolean isLeftShow = false, isRightShow = false;

	public boolean isLeftShow() {
		return isLeftShow;
	}

	public boolean isRightShow() {
		return isRightShow;
	}

	public void showLeftPage() {
		isLeftShow = true;
		isRightShow = false;
		this.smoothScrollTo(leftShowPoint, 0);
	}

	public void showRightPage() {
		isRightShow = true;
		isLeftShow = false;
		this.smoothScrollTo(rightShowPoint, 0);
	}

	/**
	 * 还原视图 左右菜单都不显示
	 */
	public void resetView() {
		this.smoothScrollTo(pageWidth, 0);
		isLeftShow = false;
		isRightShow = false;
	}
	
	/**
	 * 重置状态
	 */
	public void resetStatus() {
		int scrollX = getScrollX();
		if (isLeftShow) {
			if (scrollX > leftSlidePoint * 0.7) {
				resetView();
			} else {
				showLeftPage();
			}
		} else if (isRightShow) {
			if (scrollX < leftSlidePoint * 0.5 + pageWidth) {
				resetView();
			} else {
				showRightPage();
			}
		} else {
			resetView();
		}
	}

	private boolean isDraging = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (!isShowMenu()) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 当不是在 主界面点击 时 设置不能拖动主界面
			float touchX = event.getX();
			// Log.d(" touch X:", " touch X:" + touchX);
			isDraging = true;
			if (isLeftShow) {
				if (touchX < leftSlidePoint) {
					isDraging = false;
					return false;
				}
			} else if (isRightShow) {
				if (touchX + pageWidth + pageWidth > rightSlidePoint) {
					isDraging = false;
					return false;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (checkMove() == false) {
				return false;
			}
			break;
		case MotionEvent.ACTION_UP: {
			resetStatus();
			return false;
		}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 检测拖动状态  return false 表示 不能拖动  true 可以
	 * @return
	 */
	private boolean checkMove() {
		if (!isDraging) {
			return false;
		}
		int scrollX = this.getScrollX();
		// Log.d(" scroll X:", " scroll X:" + scrollX);
		if (isLeftShow) {
			if (scrollX > pageWidth) {
				this.smoothScrollTo(pageWidth, 0);
				return false;
			} else if (scrollX < leftShowPoint) {
				this.smoothScrollTo(leftShowPoint, 0);
				return false;
			}
		} else if (isRightShow) {
			if (scrollX < pageWidth) {
				this.smoothScrollTo(pageWidth, 0);
				return false;
			} else if (scrollX > rightShowPoint) {
				this.smoothScrollTo(rightShowPoint, 0);
				return false;
			}
		}

		return true;
	}

	public boolean isShowMenu() {
		return isLeftShow || isRightShow;
	}

	// leftSlideWidth 当显示左菜单时 左边的位置， 显示右菜单时 右边的位置
	private int leftSlidePoint, rightSlidePoint;
	// 一页的宽度 当前设置就3页 用于左右滑动
	private int pageWidth;
	// 显示时的坐标点
	private int leftShowPoint, rightShowPoint;

	public void setPageWidth(int width) {

		this.pageWidth = width;
		this.leftSlidePoint = (int) (width * 0.6);
		this.rightSlidePoint = pageWidth * 3 - leftSlidePoint;
		this.leftShowPoint = pageWidth - leftSlidePoint;
		this.rightShowPoint = pageWidth + leftSlidePoint;
	}

	public int getPageWidth() {
		
		return pageWidth;
	}
	
	public interface OnLKCoverViewListener
	{
		 void onScrollFinished(LKCoverView view);
	}
	
}
