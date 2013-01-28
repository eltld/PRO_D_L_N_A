package com.cattsoft.multimediaviewer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.brunjoy.video.R;
import com.cattsoft.util.UtilImpl;

public class ImageViewer extends Activity implements OnTouchListener {

	private static final int NONE = 0;// ��ʼ״̬
	private static final int DRAG = 1;// �϶�״̬
	private static final int ZOOM = 2;// ����״̬
	private int currentMode = NONE;// ��ǰģʽ
	private static final float MAX_SCALE = 4f;// ������ű���
	private float minScale;// ��С���ű���
	private Bitmap bitmap;// ���ͼ��λͼ
	private ImageView imageview;
	private UtilImpl utilimpl;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private DisplayMetrics dm;
	private PointF pre = new PointF();
	private PointF mid = new PointF();
	private float dist = 1f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_viewer);
		imageview = (ImageView) findViewById(R.id.image_view);
		utilimpl = new UtilImpl();
		
		imageview.setOnTouchListener(this);//����ͼ��������
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);// ��ȡ�ֱ���
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Toast.makeText(this, "ͨ��˵�ѡ�����������", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_image_player, menu);
		return true;
	}

	/**
	 * ����˵�ѡ�������Ӧ�Ķ���
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		String sdcardDir = Environment.getExternalStorageDirectory().getPath();

		if (item.getTitle().equals("From Path")) {
			bitmap = utilimpl.CheckPictureByPath(sdcardDir + "/chromelogo.png");
			if (bitmap != null) {
				Toast.makeText(ImageViewer.this,
						sdcardDir + "/chromelogo.png", Toast.LENGTH_SHORT)
						.show();
				imageview.setImageBitmap(bitmap);
				
				minZoom();
				center();
				imageview.setImageMatrix(matrix);
				return true;
			} else {
				Toast.makeText(ImageViewer.this, "load bitmap error",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		} else if (item.getTitle().equals("From Uri")) {
			bitmap = utilimpl
					.CheckPictureByUri("http://img.citydog.me/2010/07/chrome.jpg");
			if (bitmap != null) {
				Toast.makeText(ImageViewer.this,
						"http://img.citydog.me/2010/07/chrome.jpg",
						Toast.LENGTH_SHORT).show();
				imageview.setImageBitmap(bitmap);
				
				minZoom();
				center();
				imageview.setImageMatrix(matrix);
				return true;
			} else {
				Toast.makeText(ImageViewer.this, "bitmap is null",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * ��������
	 */
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		/**
		 * ���㰴��
		 */
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			pre.set(event.getX(), event.getY());
			currentMode = DRAG;
			break;

		/**
		 * ���㰴��
		 */
		case MotionEvent.ACTION_POINTER_DOWN:
			dist = distance(event);
			// �����������������10�����ж�Ϊ���ģʽ
			if (dist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				currentMode = ZOOM;
			}
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			currentMode = NONE;
			break;
			
		/**
		 * �����ƶ�
		 */
		case MotionEvent.ACTION_MOVE:
			if (currentMode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - pre.x, event.getY()
						- pre.y);
			} else if (currentMode == ZOOM) {
				float newDist = distance(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float tScale = newDist / dist;
					matrix.postScale(tScale, tScale, mid.x, mid.y);
				}
			}
			break;
		}
		
		imageview.setImageMatrix(matrix);
		checkView();
		return true;
	}
	
	/**
	 * ���������С���ű����Զ�����
	 */
	private void checkView() {
		float p[] = new float[9];
		matrix.getValues(p);
		if (currentMode == ZOOM) {
			if (p[0] < minScale) {
				matrix.setScale(minScale, minScale);
			}
			if (p[0] > MAX_SCALE) {
				matrix.set(savedMatrix);
			}
		}
		center();
	}

	/**
	 * ��С���ű������Ϊ100%
	 */
	private void minZoom() {
		minScale = Math.min(
				(float) dm.widthPixels / (float) bitmap.getWidth(),
				(float) dm.heightPixels / (float) bitmap.getHeight());
		if (minScale < 1.0) {
			matrix.postScale(minScale, minScale);
		}
	}

	private void center() {
		center(true, true);
	}

	/**
	 * �����������
	 */
	protected void center(boolean horizontal, boolean vertical) {
		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// ͼƬС����Ļ��С���������ʾ��������Ļ���Ϸ������������ƣ��·�������������
			int screenHeight = dm.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = imageview.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = dm.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
	}
	
	/**
	 * ����ľ���
	 */
	private float distance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * ������е�
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}
