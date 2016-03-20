package com.mygame.unit;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

/**
 * ����APP����Ļ�࣬����С���ϰ�������ȶ�������Ļ��
 * ����̳�Activity�࣬ʵ����Runnable�ӿڡ�ʵ����Ļ����ʱ�䡢������Ӧ�ļ���
 * @author heshaohua
 *
 */
public class Screen extends Activity implements Runnable, OnTouchListener, SensorEventListener {
	private SurfaceHolder holder;
	private boolean locker = true, initialised = false;
	private Thread thread;
	//public WakeLock WL;
	private int width = 0, height = 0;
	public float cameraX = 0, cameraY = 0;

	public Activity activity = this;
	public boolean debug_mode = true;
	private long now = SystemClock.elapsedRealtime(), lastRefresh, lastfps;
	public SurfaceView surface;
	private int fps = 0, frames = 0;

	//�����Ӧ��������
	SensorManager sm;
	//�����Ӧ��
	Sensor s;
	
	float sensorx, calibratex = 0;
	float sensory, calibratey = 0;
	private boolean default_lanscape = false;
	private int default_lanscape_rotation = 0;

	//world origin
	public final int TOP_LEFT = 0, BOTTOM_LEFT = 1;
	public int origin = TOP_LEFT;

	//����������Ļ����
	public RelativeLayout layout;

	/**
	 * ��Ӧ���������ִ���������
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = this;

		//full screen��APPռ����Ļ����������Ļ����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//create surface�������ָ���ͼ
		layout = new RelativeLayout(this);
		surface = new SurfaceView(this);
		layout.addView(surface);
		setContentView(layout);
		holder = surface.getHolder();

		//listeners������Ļ�Ĵ���������
		surface.setOnTouchListener(this);

		// start game loop��Ϸ��ʼ���߳�����
		thread = new Thread(this);
		thread.start();

		onCreate();

	}

	/* Main game loop.......................................................... */
	/**
	 * ����Ϸ��ʼ���߳�����֮�󣬻�ִ��run����
	 * ִ�з���Ҳ��ʾ���濪ʼ����
	 */
	@Override
	public void run() {
		int rand = (int) (Math.random() * 100);
		synchronized (ACCESSIBILITY_SERVICE) {
			//���locker=true��ʾ��Ϸ�������У���֮��ʾ��Ϸ��ͣ��������Ļ
			while (locker) {
				//System.out.println("start-");

				//��ȡ��ǰʱ��
				now = SystemClock.elapsedRealtime();
				//�����ǰʱ��
				if (now - lastRefresh > 28) {
					lastRefresh = SystemClock.elapsedRealtime();
					if (!holder.getSurface().isValid()) {
						continue;
					}

					//fps
					if (now - lastfps > 1000) {
						fps = frames;
						frames = 0;
						lastfps = SystemClock.elapsedRealtime();
					} else {
						frames++;
					}

					//step
					/**
					 * ����Ѿ���ʼ������ʼ
					 */
					if (initialised)
						Step(rand);

					//draw screen
					Canvas canvas = holder.lockCanvas();
					if (initialised)
						Draw(canvas);
					else {
						//initialise game
						width = canvas.getWidth();
						height = canvas.getHeight();
						Start();
						initialised = true;
					}
					holder.unlockCanvasAndPost(canvas);
				}
				//System.out.println("finish-----");
				//try {
				//	Thread.sleep(10);
				//} catch (InterruptedException e) {
				//	e.printStackTrace();
				//}
			}
		}

	}

	/* Detect and override back press */
	/**
	 * �����ֻ��İ������ֻ����µ�3-4������
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		//�ж�����ǰ������ؼ���
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//���ذ����Ĵ�����
			BackPressed();
			return false;
		}

		return false;
	}

	/* Events.................................................................. */
	/**
	 * ��APP����֮����Ϸ����������֮�����
	 * �����ﶨ��ķ������������л��о���ʵ��
	 */
	public void onCreate() {

	}
	
	/**
	 * ��Ϸ��ʼ��ʱ�����
	 * �����ﶨ��ķ������������л��о���ʵ��
	 */
	public void Start() {

	}
	
	/**
	 * �˷�������Ϸչʾ����ͼ�������Ķ���Ч��
	 * �����ﶨ��ķ������������л��о���ʵ��
	 */
	synchronized public void Step(int rand) {

	}

	/**
	 * ��APP����֮�󣬿�ʼ���ƽ��棬����ֻ�ǽ���������չʾ�ڽ����ϣ�
	 * ������Ҫ���ڿ����������ڽ����ϲ鿴�������ݣ�
	 * ����������£���Ҫ���ڻ��ƽ���Ԫ��
	 * @param canvas
	 */
	public void Draw(Canvas canvas) {
		if (debug_mode) {
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setTextSize(dpToPx(20));
			canvas.drawText("Width: " + width + ", Height: " + height, 5, dpToPx(20), paint);
			canvas.drawText("default landscape: " + default_lanscape + " Rotation: " + default_lanscape_rotation, 5, 5 + dpToPx(20) * 2, paint);
			canvas.drawText("FPS: " + fps, 5, 5 + dpToPx(20) * 3, paint);
		}

	}
	
	/**
	 * �˷�������Ϸ������ʱ�����
	 * �����ﶨ��ķ������������л��о���ʵ��
	 */
	public void Finish() {

	}

	/**
	 * ��Ϸ��ͣ�ķ���
	 */
	public void Pause() {
		//��locker����Ϊfalse,��ʾ������ǰ��Ļ
		locker = false;

		while (true) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
		thread = null;
	}

	/**
	 * �����½������APP�����ʱ����ô˷���
	 */
	public void Resume() {
		//�������ſ���Ȼ����Ϸ�߳�����
		locker = true;
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * ���ذ����Ĵ���
	 */
	public synchronized void BackPressed() {

	}

	/**
	 * ���������Ļ�Ĵ���
	 * @param TouchX
	 * @param TouchY
	 * @param event
	 */
	public synchronized void onTouch(float TouchX, float TouchY, MotionEvent event) {
	}

	/**
	 * ���ٶȴ�����ʹ�õĴ���
	 * @param point
	 */
	public synchronized void onAccelerometer(PointF point) {
	}

	/* Functions............................................................... */
	/**
	 * ��Ϸ�˳�����
	 */
	public void Exit() {
		//��Ϸ����
		locker = false;

		while (true) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
		thread = null;

		//app�˳����������ٵ�ǰ����
		System.exit(0);
		activity.finish();
	}

	/**
	 * ��ȡ��ǰ������
	 * @return
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * ���ǵ���ģʽ�������������Ƿ���ʾ������Ϣ
	 * @param debugModeOn
	 */
	public void setDebugMode(boolean debugModeOn) {
		debug_mode = debugModeOn;
	}

	//screen related
	/**
	 * ��ȡ��ǰ��Ļ���
	 * @return
	 */
	public int ScreenWidth() {
		return width;
	}

	/**
	 * ��ȡ��ǰ��Ļ�߶�
	 * @return
	 */
	public int ScreenHeight() {
		return height;
	}

	/**
	 * ��ǰ��Ļ�еĺ����������ȫ��ͼ�ĺ�����
	 * World X to Screen X
	 * @param worldX
	 *            The x-coordinate relative to the world
	 */
	public int ScreenX(float worldX) {
		return (int) (worldX - cameraX);
	}

	/**
	 * ��ǰ��Ļ�е������������ȫ��ͼ��������
	 * World Y to Screen Y
	 * @param worldY
	 *            The Y-coordinate relative to the world
	 */
	public int ScreenY(float worldY) {
		if (origin == TOP_LEFT)
			return (int) (worldY - cameraY);
		else
			return ScreenHeight() - (int) (worldY - cameraY);
	}

	/**
	 * ��������ԭ��
	 * World origin (0,0)
	 * @param origin
	 *            TOP_LEFT or BOTTOM_LEFT
	 */
	public void setOrigin(int origin) {
		this.origin = origin;
	}

	/**
	 * �ж������Ƿ�����Ļ��
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean inScreen(float x, float y) {
		return ((ScreenY(y) > 0 && ScreenY(y) < ScreenHeight()) && (ScreenX(x) > 0 && ScreenX(x) < ScreenWidth()));
	}

	/**
	 * ��dp��λת�������ص�λpx
	 * @param dp
	 * @return
	 */
	public int dpToPx(int dp) {
		float density = getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

	//sensor related
	/**
	 * �ֻ��������������������Ǻ�������Ĭ������Ϊ������ʾ
	 */
	public void initialiseAccelerometer() {
		//device has its default landscape or portrait
		Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int rotation = display.getRotation();
		
		//�����ǰ����������
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			//portrait
			if (rotation == Surface.ROTATION_0)
				default_lanscape = false;
			if (rotation == Surface.ROTATION_180)
				default_lanscape = false;
			if (rotation == Surface.ROTATION_90)
				default_lanscape = true;
			if (rotation == Surface.ROTATION_270)
				default_lanscape = true;
		} else {//�����ǰ�����Ǻ���
			//landscape
			if (rotation == Surface.ROTATION_0)
				default_lanscape = true;
			if (rotation == Surface.ROTATION_180)
				default_lanscape = true;
			if (rotation == Surface.ROTATION_90)
				default_lanscape = false;
			if (rotation == Surface.ROTATION_270)
				default_lanscape = false;
		}
		default_lanscape_rotation = rotation;

		sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
			s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}

	}

	/**
	 * ��ȡ���ٶȴ������ı�׼����
	 */
	public void CalibrateAccelerometer() {
		calibratex = sensorx * Math.abs(sensorx);
		calibratey = sensory * Math.abs(sensory);
	}

	/**
	 * �����ȡ����
	 * @return
	 */
	public PointF getAccelerometer() {
		return new PointF((sensorx * Math.abs(sensorx) - calibratex), (sensory * Math.abs(sensory) - calibratey));
	}

	/* Touch events.......................................................... */
	/**
	 * ������Ļ������ʱ�䣬����С����й����еĵ����������
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (initialised) {
			onTouch(event.getX(), event.getY(), event);
		}
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * ��Ӧ���ı��ʱ�����������ı��ʱ�����
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (initialised) {
			//read values
			if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				if (default_lanscape) {
					sensorx = -event.values[1];
					sensory = -event.values[0];
				} else {
					sensory = event.values[1];
					sensorx = -event.values[0];
				}
			} else {
				if (default_lanscape) {
					sensory = event.values[1];
					sensorx = -event.values[0];
				} else {
					sensorx = event.values[1];
					sensory = event.values[0];
				}
			}

			//call accelerometer event
			onAccelerometer(new PointF((sensorx - calibratex), (sensory - calibratey)));

		}
		//sleep for a while
		try {
			Thread.sleep(16);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	/* pause, destroy, resume................................................ */
	/**
	 * ������Ӷ�ջ�л�������
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Resume();
	}

	/**
	 * �ڽ�����ͣ��ʱ�����
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Pause();

	}

	/**
	 * ��ǰapp���ٵ���
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Finish();
	}

}
