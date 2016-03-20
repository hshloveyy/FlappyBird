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
 * 整个APP的屏幕类，包括小鸟、障碍物、分数等都画在屏幕上
 * 该类继承Activity类，实现了Runnable接口、实现屏幕触摸时间、重力感应的监听
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

	//定义感应器管理器
	SensorManager sm;
	//定义感应器
	Sensor s;
	
	float sensorx, calibratex = 0;
	float sensory, calibratey = 0;
	private boolean default_lanscape = false;
	private int default_lanscape_rotation = 0;

	//world origin
	public final int TOP_LEFT = 0, BOTTOM_LEFT = 1;
	public int origin = TOP_LEFT;

	//定义整个屏幕布局
	public RelativeLayout layout;

	/**
	 * 在应用启动后会执行这个方法
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = this;

		//full screen将APP占满屏幕，并设置屏幕常亮
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//create surface创建布局跟视图
		layout = new RelativeLayout(this);
		surface = new SurfaceView(this);
		layout.addView(surface);
		setContentView(layout);
		holder = surface.getHolder();

		//listeners设置屏幕的触屏监听器
		surface.setOnTouchListener(this);

		// start game loop游戏开始的线程启动
		thread = new Thread(this);
		thread.start();

		onCreate();

	}

	/* Main game loop.......................................................... */
	/**
	 * 在游戏开始的线程启动之后，会执行run方法
	 * 执行方法也表示界面开始绘制
	 */
	@Override
	public void run() {
		int rand = (int) (Math.random() * 100);
		synchronized (ACCESSIBILITY_SERVICE) {
			//如果locker=true表示游戏正在运行，反之表示游戏暂停，锁定屏幕
			while (locker) {
				//System.out.println("start-");

				//获取当前时间
				now = SystemClock.elapsedRealtime();
				//如果当前时间
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
					 * 如果已经初始化，则开始
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
	 * 监听手机的按键，手机底下的3-4个按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		//判断如果是按“返回键”
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//返回按键的处理方法
			BackPressed();
			return false;
		}

		return false;
	}

	/* Events.................................................................. */
	/**
	 * 在APP启动之后，游戏界面绘制完成之后调用
	 * 在这里定义的方法，在子类中会有具体实现
	 */
	public void onCreate() {

	}
	
	/**
	 * 游戏开始的时候调用
	 * 在这里定义的方法，在子类中会有具体实现
	 */
	public void Start() {

	}
	
	/**
	 * 此方法在游戏展示背景图，背景的动画效果
	 * 在这里定义的方法，在子类中会有具体实现
	 */
	synchronized public void Step(int rand) {

	}

	/**
	 * 在APP启动之后，开始绘制界面，这里只是将个别数据展示在界面上，
	 * 这里主要用于开发过程中在界面上查看部分数据，
	 * 在其他情况下，主要用于绘制界面元素
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
	 * 此方法在游戏结束的时候调用
	 * 在这里定义的方法，在子类中会有具体实现
	 */
	public void Finish() {

	}

	/**
	 * 游戏暂停的方法
	 */
	public void Pause() {
		//将locker设置为false,表示锁定当前屏幕
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
	 * 当重新进入这个APP界面的时候调用此方法
	 */
	public void Resume() {
		//把锁定放开，然后游戏线程启动
		locker = true;
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * 返回按键的处理
	 */
	public synchronized void BackPressed() {

	}

	/**
	 * 点击触摸屏幕的处理
	 * @param TouchX
	 * @param TouchY
	 * @param event
	 */
	public synchronized void onTouch(float TouchX, float TouchY, MotionEvent event) {
	}

	/**
	 * 加速度传感器使用的处理
	 * @param point
	 */
	public synchronized void onAccelerometer(PointF point) {
	}

	/* Functions............................................................... */
	/**
	 * 游戏退出调用
	 */
	public void Exit() {
		//游戏锁定
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

		//app退出，并且销毁当前界面
		System.exit(0);
		activity.finish();
	}

	/**
	 * 获取当前主界面活动
	 * @return
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * 这是调试模式，控制主界面是否显示调试信息
	 * @param debugModeOn
	 */
	public void setDebugMode(boolean debugModeOn) {
		debug_mode = debugModeOn;
	}

	//screen related
	/**
	 * 获取当前屏幕宽度
	 * @return
	 */
	public int ScreenWidth() {
		return width;
	}

	/**
	 * 获取当前屏幕高度
	 * @return
	 */
	public int ScreenHeight() {
		return height;
	}

	/**
	 * 当前屏幕中的横坐标相对于全地图的横坐标
	 * World X to Screen X
	 * @param worldX
	 *            The x-coordinate relative to the world
	 */
	public int ScreenX(float worldX) {
		return (int) (worldX - cameraX);
	}

	/**
	 * 当前屏幕中的纵坐标相对于全地图的纵坐标
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
	 * 设置坐标原点
	 * World origin (0,0)
	 * @param origin
	 *            TOP_LEFT or BOTTOM_LEFT
	 */
	public void setOrigin(int origin) {
		this.origin = origin;
	}

	/**
	 * 判断坐标是否在屏幕中
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean inScreen(float x, float y) {
		return ((ScreenY(y) > 0 && ScreenY(y) < ScreenHeight()) && (ScreenX(x) > 0 && ScreenX(x) < ScreenWidth()));
	}

	/**
	 * 将dp单位转换成像素单位px
	 * @param dp
	 * @return
	 */
	public int dpToPx(int dp) {
		float density = getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}

	//sensor related
	/**
	 * 手机横屏，不管是竖屏还是横屏，都默认设置为横屏显示
	 */
	public void initialiseAccelerometer() {
		//device has its default landscape or portrait
		Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int rotation = display.getRotation();
		
		//如果当前界面是竖屏
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
		} else {//如果当前界面是横屏
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
	 * 获取加速度传感器的标准坐标
	 */
	public void CalibrateAccelerometer() {
		calibratex = sensorx * Math.abs(sensorx);
		calibratey = sensory * Math.abs(sensory);
	}

	/**
	 * 计算获取坐标
	 * @return
	 */
	public PointF getAccelerometer() {
		return new PointF((sensorx * Math.abs(sensorx) - calibratex), (sensory * Math.abs(sensory) - calibratey));
	}

	/* Touch events.......................................................... */
	/**
	 * 触摸屏幕触发的时间，即在小鸟飞行过程中的点击做出处理
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
	 * 感应器改变的时候，重力发生改变的时候调用
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
	 * 当界面从堆栈中回来调用
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Resume();
	}

	/**
	 * 在界面暂停的时候调用
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Pause();

	}

	/**
	 * 当前app销毁调用
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Finish();
	}

}
