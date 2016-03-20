package com.neurondigital.flappybot;

import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;

import com.mygame.unit.Button;
import com.mygame.unit.HighScoreManager;
import com.mygame.unit.Instance;
import com.mygame.unit.Physics;
import com.mygame.unit.Screen;
import com.mygame.unit.Sprite;

public class MainGame extends Screen {

	//paints定义
	Paint background_shader = new Paint();
	Paint Title_Paint = new Paint();
	//副标题
	Paint SubTitle_Paint = new Paint();
	Paint Score_Paint = new Paint();
	Paint Instruction_Paint = new Paint();
	Paint Black_shader = new Paint();
	Paint White_shader = new Paint();
	Paint Yellow_shader = new Paint();
	Paint Grey_shader = new Paint();

	//background
	Bitmap background;

	//instances
	ArrayList<Instance> clouds = new ArrayList<Instance>();
	Instance background_building_1, background_building_2;
	Sprite cloud_sprite, cloud_sprite2, column_edge;
	Instance bird;

	//physics
	Physics physics = new Physics();

	//states位置
	final int MENU = 0, GAMEPLAY = 1, HIGHSCORES = 2, GAMEOVER = 3;
	int state = MENU;
	boolean pause = false, notstarted = true;

	//menu buttons
	Button btn_Play, btn_Home;

	//score
	int score = 0;
	HighScoreManager highscoreManager;
	HighScoreManager.Highscore[] highscore_list;
	//Sprite score_cup;

	//sound
	SoundPool sp;
	MediaPlayer music;
	int sound_score, sound_fall, sound_beep;
	boolean sound_muted = false, music_muted = false;

	//Colors
	
	final int BLACK = Color.argb(255, 51, 41, 51);
	final int RED = Color.argb(255, 243, 120, 93);
	final int WHITE = Color.argb(255, 242,232, 242);
	final int YELLOW = Color.argb(299, 253, 220, 81);
	final int GREY = Color.argb(255, 128, 100, 128);

	//ad
	//game over counter游戏计数器
	int gameover_counter = 0;
	boolean game_over = false;

	//columns holesize洞口间距的大小   final类不被其他的类继承
	final int X = 0, Y = 1;
	float hole_size;
	int next_column_to_create = 0;

	
	int touch_speed = 16;//移动速度
	int gameover_delay = 20;
	int hole_size_ComparedToBird = 5;//障碍物间距
	int gravity = 17;//重力下降


	//柱状障碍物的位置设置
	//this array represents the holes in the columns. 
	final float column_positions[][] = new float[][] {
			//{X 障碍物, Y 间距}
			{ 600, 0.5f },
			{ 900, 0.3f },
			{ 1200, 0.2f },
			{ 1500, 0.3f },
			{ 1800, 0.1f },
			{ 2100, 0.4f },
			{ 2400, 0.5f },
			{ 2600, 0.8f },
			{ 2800, 0.2f },
			{ 3100, 0.1f },
			{ 3454, 0.4f },
			{ 3700, 0.5f },
			{ 4000, 0.4f },
			{ 4400, 0.3f },
			{ 4600, 0.1f },
			{ 4900, 0.3f },
			{ 5200, 0.8f },
			{ 5500, 0.4f },
			{ 5705, 0.5f },
			{ 6000, 0.1f },
			{ 6300, 0.4f },
			{ 6700, 0.1f },
			{ 7100, 0.6f },
			{ 7300, 0.4f },
			{ 7500, 0.6f },
			{ 7800, 0.8f },
			{ 8100, 0.1f },
			{ 8400, 0.9f },
			{ 8700, 0.5f },
			{ 8900, 0.6f },
			{ 9300, 0.2f },
			{ 9600, 0.6f },
			{ 9900, 0.8f },
			{ 10200, 0.1f },
			{ 10500, 0.9f },
			{ 10800, 0.2f }
	};

	@Override
	//重写oncreate
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//setDebugMode(true);
		initialiseAccelerometer();

		//highscores
		highscoreManager = new HighScoreManager(this, savedInstanceState, layout);

	}

	//线程打开广告，目前并没有实现，预留了该功能
	public void openAd() {
		runOnUiThread(new Runnable() {
			public void run() {
			}
		});
	}

	@Override
	//设置字体
	public void Start() {
		super.Start();
		//fonts
		Typeface SCRIPTBL = Typeface.createFromAsset(getAssets(), "SCRIPTBL.TTF");

		//set paints
		//title
		Title_Paint.setTextSize(dpToPx(60));
		//锯齿标志
		Title_Paint.setAntiAlias(true);
		Title_Paint.setColor(BLACK);
		Title_Paint.setTypeface(SCRIPTBL);

		//subtitle副标题
		SubTitle_Paint.setTextSize(dpToPx(20));
		SubTitle_Paint.setAntiAlias(true);
		SubTitle_Paint.setColor(BLACK);
		SubTitle_Paint.setTypeface(Typeface.DEFAULT_BOLD);

		//score Paint
		//设置分数
		Score_Paint.setTextSize(dpToPx(50));
		Score_Paint.setAntiAlias(true);
		Score_Paint.setColor(RED);
		Score_Paint.setTypeface(SCRIPTBL);

		//Instruction Paint
		Instruction_Paint.setTextSize(dpToPx(50));
		Instruction_Paint.setAntiAlias(true);
		Instruction_Paint.setColor(BLACK);
		Instruction_Paint.setTypeface(SCRIPTBL);

		Black_shader.setColor(BLACK);
		White_shader.setColor(WHITE);
		Yellow_shader.setColor(YELLOW);
		Grey_shader.setColor(GREY);

		//get menu ready
		//play button
		btn_Play = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.play), ScreenHeight() * 0.3f), 0, 0, this, false);
		btn_Play.x = (ScreenWidth() / 2) - btn_Play.getWidth() / 2;
		btn_Play.y = (ScreenHeight() / 2);

		//home button
		btn_Home = new Button(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.home), ScreenWidth() * 0.1f), 0, 0, this, false);
		btn_Home.x = ScreenWidth() - btn_Home.getWidth() * 1.2f;
		btn_Home.y = ScreenHeight() - btn_Home.getHeight() * 1.2f;

		//set world origin
		setOrigin(BOTTOM_LEFT);

		//initialise character
		bird = new Instance(new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.bird), (ScreenHeight() * 0.15f), 3, 3, 498), 50, 300, this, true);

		//initialise clouds
		cloud_sprite = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.cloud_1), ScreenHeight() * 0.45f);
		cloud_sprite2 = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.cloud_2), ScreenHeight() * 0.3f);

		//initialise buildings
		Sprite back = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.background), (ScreenWidth() * 1.01f));
		background_building_1 = new Instance(back, 0, ScreenHeight() - back.getHeight(), this, false);
		background_building_2 = new Instance(back, back.getWidth() - 2, ScreenHeight() - back.getHeight(), this, false);

		//initialise column edge
		column_edge = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.column_edge), ScreenHeight() * 0.2f);
		hole_size = bird.getHeight() * hole_size_ComparedToBird;

		//initialise score image
		//score_cup = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.score), ScreenHeight() * 0.3f);

		//initialise sound fx
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

		//initialise music
		music = MediaPlayer.create(activity, R.raw.music);
		sound_score = sp.load(activity, R.raw.coin, 1);
		sound_fall = sp.load(activity, R.raw.fall, 1);
		sound_beep = sp.load(activity, R.raw.beep, 1);

	}

	@Override
	synchronized public void Step(int rand) {
		super.Step(rand);
		if (state == MENU) {

		} else if (state == GAMEPLAY) {

			//things to pause
			if (!notstarted && !pause && !game_over) {
				//bird movement
				bird.Update();
				if (-bird.speedy > -70)
					bird.rotate(-bird.speedy);

				//碰撞边缘
				if (bird.y < bird.getHeight()) {
					game_over = true;
					if (sound_fall != 0 && !sound_muted)
						sp.play(sound_fall, 1, 1, 0, 0, 1);
				}
				if (bird.y > ScreenHeight()) {
					bird.y = ScreenHeight();
					bird.speedy = 0;
				}

				int temp_score = 0;
				for (int i = 0; i < column_positions.length; i++) {
					float y = column_positions[i][Y] * ScreenHeight();
					float x = ScreenX((int) column_positions[i][X] * ScreenWidth() * 0.0015f);
					float birdx = ScreenX(bird.x) + (bird.getHeight() * 0.15f);
					float birdy = ScreenY(bird.y) + (bird.getWidth() * 0.15f);
					float birdw = bird.getWidth() * 0.7f;
					float birdh = bird.getHeight() * 0.7f;
					if (physics.intersect((int) birdx, (int) birdy, (int) birdw, (int) birdh, (int) x, (int) (y + hole_size / 2), (int) column_edge.getWidth(), (int) (ScreenHeight() - (y + hole_size / 2))) || physics.intersect((int) birdx, (int) birdy, (int) birdw, (int) birdh, (int) x, 0, (int) column_edge.getWidth(), (int) ((int) y - (hole_size / 2) + column_edge.getHeight()))) {
						bird.speedy = -dpToPx(40);
					}
					if (bird.x > column_positions[i][X] * ScreenWidth() * 0.0015f) {
						temp_score++;
					}
				}
				if (temp_score > score) {
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
				}
				score = temp_score;

				//如果小鸟的x长度大于屏幕的一半，
				if (ScreenX(bird.x) > ScreenWidth() / 2) {
					//就可以移动云，做出动画效果
					for (int i = 0; i < clouds.size(); i++)
						clouds.get(i).x += ((ScreenWidth() / 2) - ScreenX(bird.x)) * 0.6 * (i + 1);
					//移动速度为小鸟的速度加
				cameraX += bird.speedx;
				background_building_1.Update();
				background_building_2.Update();
				}
				//两朵云的移动
			//	for (int i = 0; i < clouds.size(); i++)
				//	clouds.get(i).x += -(2 * (i + 1));

				
				//if (background_building_1.x < -ScreenWidth())
				//	background_building_1.x = ScreenWidth();
				//if (background_building_2.x < -ScreenWidth())
				//	background_building_2.x = ScreenWidth();

			}

			//检查游戏的结束
			if (game_over)
				gameover_counter++;
			else
			gameover_counter = 0;
			if (gameover_counter > gameover_delay)
				GameOver();

			//移动云
			for (int i = clouds.size() - 1; i >= 0; i--) {
			if (clouds.size() > i) {
					if (clouds.get(i).x < -clouds.get(i).getWidth()) {
					clouds.remove(i);
					createCloud((float) (ScreenWidth() + (Math.random() * dpToPx(300))));
				}
			}
		}
		}

	}

	@Override
	public synchronized void onAccelerometer(PointF point) {

	}

	@Override
	public synchronized void BackPressed() {
		if (state == GAMEPLAY) {
			StopMusic();
			state = MENU;
		} else if (state == HIGHSCORES) {
			state = MENU;
		} else if (state == MENU) {
			StopMusic();
			Exit();

		} else if (state == GAMEOVER) {
			//获得一个新的分数，得到资源先得到字符分数名字。
			highscoreManager.newScore(score, getResources().getString(R.string.Default_topscore_name));
			state = MENU;
		}
	}

	@Override
	public synchronized void onTouch(float TouchX, float TouchY, MotionEvent event) {

		if (state == MENU) {
			//如果获得的返回值得到按下参数，那么play键的事件发生，则就会变成黄色
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Play.isTouched(event)) {
					btn_Play.Highlight(YELLOW);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//这里出现MotionEvent.ACTION_UP分支未响应，是因为return super.onTouchEvent(event);返回的是false
				btn_Play.LowLight();

				//如果声音不等于0或者没有消音，声音将继续 开始游戏
				if (btn_Play.isTouched(event)) {
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
					StartGame();
				}
			}
			
		//	if (event.getAction() == MotionEvent.ACTION_MOVE) {

			//}
			//如果位置是分数，得到的参数是游戏分数，则发生的事件是点击返回到主菜单界面 按键变为红色
		} else if (state == HIGHSCORES) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Home.isTouched(event)) {
					btn_Home.Highlight(RED);
				}
			}
			
			//返回值false
			if (event.getAction() == MotionEvent.ACTION_UP) {
				
				btn_Home.LowLight();

				if (btn_Home.isTouched(event)) {
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
					state = MENU;
				}
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {

			}
		} else if (state == GAMEOVER) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Home.isTouched(event)) {
					btn_Home.Highlight(RED);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//refresh all
				btn_Home.LowLight();
				if (btn_Home.isTouched(event)) {
					//show_enter_highscore();
					highscoreManager.newScore(score, getResources().getString(R.string.Default_topscore_name));
					state = MENU;
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
				}
			}
		} else if (state == GAMEPLAY) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				
				if (notstarted) {
					notstarted = false;
				}
				//during gameplay
				//move bird up
				if (!notstarted && !pause && !game_over) {
					bird.speedy = ScreenHeight() * touch_speed * 0.0015f;
				}

			}

		}
	}

	//游戏功能 
	/**
	 * 游戏开始
	 */
	public void StartGame() {
		
		score = 0;

		//小鸟的下降速度
		bird.accelerationy = -ScreenHeight() * 0.0001f * gravity;
		//x上的加速和y上的
		bird.speedx = ScreenWidth() * 0.01f;
		bird.speedy = 0;
		//x和y上的距离
		
		bird.y = ScreenHeight() / 2;
		bird.x = dpToPx(30);
		bird.rotate(0);//旋转

		//refresh camera
		cameraY = 0;
		cameraX = 0;

		//clouds随机
		clouds.clear();
		createCloud((float) (Math.random() * dpToPx(300)));
		createCloud((float) (Math.random() * dpToPx(300)));

		//background buildings
		background_building_1.speedx = -dpToPx(3);
		background_building_2.speedx = -dpToPx(3);
		background_building_1.x = 0;
		background_building_2.x = background_building_1.getWidth();

		//游戏未开始
		notstarted = true;
		game_over = false;
		state = GAMEPLAY;
		PlayMusic();

		
		pause = false;
	}

	/**
	 * 游戏结束
	 */
	public synchronized void GameOver() {
		//打开广告
		openAd();
		//关闭音乐
		StopMusic();
		//当前状态为游戏结束
		state = GAMEOVER;
	
	}

	public void OpenHighscores() {
		state = HIGHSCORES;
		
		highscore_list = highscoreManager.load_localscores();
	}

	public void createCloud(float x) {
		if (Math.random() > 0.5)
			clouds.add(new Instance(cloud_sprite, x, (float) ((Math.random() * ScreenHeight() / 2) - (cloud_sprite.getHeight() / 2)), this, false));
		else
			clouds.add(new Instance(cloud_sprite2, x, (float) ((Math.random() * ScreenHeight() / 2) - (cloud_sprite.getHeight() / 2)), this, false));
	}

	public void PlayMusic() {
		if (!music_muted && state == GAMEPLAY) {
			music = MediaPlayer.create(activity, R.raw.music);
			music.start();
			music.setLooping(true);
		}
	}

	public void StopMusic() {
		music.stop();
	}


	public void pause() {
		if (state == GAMEPLAY) {
			pause = true;
			StopMusic();
		}
	}

	//触发暂停
	public void togglePause() {
		if (state == GAMEPLAY) {
			if (pause) {
				pause = false;
				if (!music_muted)
					PlayMusic();
			} else {
				pause();
			}
		}
	}


	@Override
	public void Draw(Canvas canvas) {
		//draw background
		renderBackground(canvas);

		if (state == MENU) {
			//绘制云，选择合适的位置和大小
			cloud_sprite2.draw(canvas, dpToPx(10), dpToPx(50));
			cloud_sprite.draw(canvas, ScreenWidth() - (cloud_sprite.getWidth() * 0.75f), ScreenHeight() - (cloud_sprite.getHeight() * 0.8f));

			//画布上获得资源先得到字符定义的appname，放在屏幕中央的位置  frawtext绘制文本
			canvas.drawText(getResources().getString(R.string.app_name), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.app_name)) / 2), (float) (ScreenHeight() * 0.25), Title_Paint);
			btn_Play.draw(canvas);

		} else if (state == GAMEPLAY) {
			//draw clouds
			for (int i = 0; i < clouds.size(); i++) {
				clouds.get(i).draw(canvas);
			}

			
			background_building_1.draw(canvas);
			background_building_2.draw(canvas);

			//draw floor
			canvas.drawRect(0, ScreenHeight() - (ScreenHeight() * 0.04f), ScreenWidth(), ScreenHeight(), Black_shader);

			//draw columns
			for (int i = 0; i < column_positions.length; i++) {
				float y = column_positions[i][Y] * ScreenHeight();
				float x = ScreenX((int) column_positions[i][X] * ScreenWidth() * 0.0015f);

				//top绘制矩形
				canvas.drawRect(x, 0, x + column_edge.getWidth(), y - hole_size / 2, Black_shader);
				column_edge.draw(canvas, x, y - hole_size / 2);
				//bottom
				canvas.drawRect(x, y + hole_size / 2, x + column_edge.getWidth(), ScreenHeight(), Black_shader);
				column_edge.draw(canvas, x, y + hole_size / 2);

			}

			//draw bird
			bird.draw(canvas);

			//分数
			canvas.drawText("" + score, (ScreenWidth() * 0.5f) - (Title_Paint.measureText("" + score) / 2), (float) (ScreenHeight() * 0.35f), Score_Paint);

			//游戏开始之后
			if (notstarted) {
				canvas.drawText(getResources().getString(R.string.Tap_to_start), (ScreenWidth() / 2) - (Instruction_Paint.measureText(getResources().getString(R.string.Tap_to_start)) / 2), (float) (ScreenHeight() * 0.5), Instruction_Paint);
			}
			if (pause) {
				canvas.drawText(getResources().getString(R.string.Paused), (ScreenWidth() / 2) - (Instruction_Paint.measureText(getResources().getString(R.string.Paused)) / 2), (float) (ScreenHeight() * 0.5), Instruction_Paint);
			}


		} else if (state == HIGHSCORES) {
			//draw clouds
			cloud_sprite2.draw(canvas, dpToPx(10), dpToPx(50));
			cloud_sprite.draw(canvas, ScreenWidth() - (cloud_sprite.getWidth() * 0.75f), ScreenHeight() - (cloud_sprite.getHeight() * 0.8f));

		

			if (highscore_list != null) {
				//绘制分数
				for (int i = 0; i < highscore_list.length; i++) {
					canvas.drawText(highscore_list[i].hiscorename, (ScreenWidth() / 2) - (ScreenWidth() / 4), (ScreenHeight() * 0.35f) + (i * SubTitle_Paint.getTextSize() * 1.5f), SubTitle_Paint);
					canvas.drawText("" + highscore_list[i].highscore, (ScreenWidth() / 2) + (ScreenWidth() / 6), (ScreenHeight() * 0.35f) + (i * SubTitle_Paint.getTextSize() * 1.5f), SubTitle_Paint);
				}
			}

			btn_Home.draw(canvas);
		} else if (state == GAMEOVER) {
			//draw clouds
			cloud_sprite2.draw(canvas, dpToPx(10), dpToPx(50));
			cloud_sprite.draw(canvas, ScreenWidth() - (cloud_sprite.getWidth() * 0.75f), ScreenHeight() - (cloud_sprite.getHeight() * 0.8f));

			canvas.drawText(getResources().getString(R.string.game_over), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.game_over)) / 2), (float) (ScreenHeight() * 0.25), Title_Paint);

			//绘制的文本加上分数
			canvas.drawText("" + score, (ScreenWidth() / 2) - (Score_Paint.measureText("" + score) / 2), (float) (ScreenHeight() /2), Score_Paint);

			btn_Home.draw(canvas);

		}

		//physics防止出现bug，加一个超类。
		super.Draw(canvas);
	}

	//Rendering of background
	/**
	 * 绘制屏幕背景
	 * @param canvas
	 */
	public void renderBackground(Canvas canvas) {

		//可以改变背景颜色设置背景的摆设
		canvas.drawColor(Color.rgb(153, 204, 255));

		background_shader.setARGB(255, 255, 229, 240);
		int radius = DrawBackgroundCloud(canvas, (int) (ScreenHeight() / 2.5), 10);
		canvas.drawRect(0, (float) ((ScreenHeight() / 3.6) + radius * 1.5), ScreenWidth(), ScreenHeight(), background_shader);

		background_shader.setARGB(255, 108, 181, 100);
		radius = DrawBackgroundCloud(canvas, (int) (ScreenHeight() / 1.7), 7);
		canvas.drawRect(0, (float) ((ScreenHeight() / 2.3) + radius * 1.9), ScreenWidth(), ScreenHeight(), background_shader);

	}

	//云的绘制 云的形状和摆放位置
	public int DrawBackgroundCloud(Canvas canvas, int y, int circles) {
		int radius = (int) (ScreenWidth() / (circles * 1.3));
		for (int i = 0; i < circles; i++) {
			canvas.drawCircle((float) (i * radius * 1.5), (float) (y + radius + (Math.cos(i * circles * y) * radius * 0.35f)), radius, background_shader);
		}
		return radius;
	}

	/**
	 * 当从其他界面回到主界面时调用
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//分数管理器调用
		highscoreManager.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 当界面从堆栈中回来调用
	 */
	@Override
	protected void onResume() {
		super.onResume();
		//同样还是分数管理器调用
		highscoreManager.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		highscoreManager.onSaveInstanceState(outState);
	}

	/**
	 * 在界面暂停的时候调用
	 */
	@Override
	public void onPause() {
		super.onPause();
		//游戏也暂停
		pause();
		highscoreManager.onPause();
	}

	/**
	 * 当前app销毁调用
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		highscoreManager.onDestroy();
	}
}
