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

	//paints����
	Paint background_shader = new Paint();
	Paint Title_Paint = new Paint();
	//������
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

	//statesλ��
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
	//game over counter��Ϸ������
	int gameover_counter = 0;
	boolean game_over = false;

	//columns holesize���ڼ��Ĵ�С   final�಻����������̳�
	final int X = 0, Y = 1;
	float hole_size;
	int next_column_to_create = 0;

	
	int touch_speed = 16;//�ƶ��ٶ�
	int gameover_delay = 20;
	int hole_size_ComparedToBird = 5;//�ϰ�����
	int gravity = 17;//�����½�


	//��״�ϰ����λ������
	//this array represents the holes in the columns. 
	final float column_positions[][] = new float[][] {
			//{X �ϰ���, Y ���}
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
	//��дoncreate
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//setDebugMode(true);
		initialiseAccelerometer();

		//highscores
		highscoreManager = new HighScoreManager(this, savedInstanceState, layout);

	}

	//�̴߳򿪹�棬Ŀǰ��û��ʵ�֣�Ԥ���˸ù���
	public void openAd() {
		runOnUiThread(new Runnable() {
			public void run() {
			}
		});
	}

	@Override
	//��������
	public void Start() {
		super.Start();
		//fonts
		Typeface SCRIPTBL = Typeface.createFromAsset(getAssets(), "SCRIPTBL.TTF");

		//set paints
		//title
		Title_Paint.setTextSize(dpToPx(60));
		//��ݱ�־
		Title_Paint.setAntiAlias(true);
		Title_Paint.setColor(BLACK);
		Title_Paint.setTypeface(SCRIPTBL);

		//subtitle������
		SubTitle_Paint.setTextSize(dpToPx(20));
		SubTitle_Paint.setAntiAlias(true);
		SubTitle_Paint.setColor(BLACK);
		SubTitle_Paint.setTypeface(Typeface.DEFAULT_BOLD);

		//score Paint
		//���÷���
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

				//��ײ��Ե
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

				//���С���x���ȴ�����Ļ��һ�룬
				if (ScreenX(bird.x) > ScreenWidth() / 2) {
					//�Ϳ����ƶ��ƣ���������Ч��
					for (int i = 0; i < clouds.size(); i++)
						clouds.get(i).x += ((ScreenWidth() / 2) - ScreenX(bird.x)) * 0.6 * (i + 1);
					//�ƶ��ٶ�ΪС����ٶȼ�
				cameraX += bird.speedx;
				background_building_1.Update();
				background_building_2.Update();
				}
				//�����Ƶ��ƶ�
			//	for (int i = 0; i < clouds.size(); i++)
				//	clouds.get(i).x += -(2 * (i + 1));

				
				//if (background_building_1.x < -ScreenWidth())
				//	background_building_1.x = ScreenWidth();
				//if (background_building_2.x < -ScreenWidth())
				//	background_building_2.x = ScreenWidth();

			}

			//�����Ϸ�Ľ���
			if (game_over)
				gameover_counter++;
			else
			gameover_counter = 0;
			if (gameover_counter > gameover_delay)
				GameOver();

			//�ƶ���
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
			//���һ���µķ������õ���Դ�ȵõ��ַ��������֡�
			highscoreManager.newScore(score, getResources().getString(R.string.Default_topscore_name));
			state = MENU;
		}
	}

	@Override
	public synchronized void onTouch(float TouchX, float TouchY, MotionEvent event) {

		if (state == MENU) {
			//�����õķ���ֵ�õ����²�������ôplay�����¼���������ͻ��ɻ�ɫ
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Play.isTouched(event)) {
					btn_Play.Highlight(YELLOW);
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//�������MotionEvent.ACTION_UP��֧δ��Ӧ������Ϊreturn super.onTouchEvent(event);���ص���false
				btn_Play.LowLight();

				//�������������0����û������������������ ��ʼ��Ϸ
				if (btn_Play.isTouched(event)) {
					if (sound_beep != 0 && !sound_muted)
						sp.play(sound_beep, 1, 1, 0, 0, 1);
					StartGame();
				}
			}
			
		//	if (event.getAction() == MotionEvent.ACTION_MOVE) {

			//}
			//���λ���Ƿ������õ��Ĳ�������Ϸ�������������¼��ǵ�����ص����˵����� ������Ϊ��ɫ
		} else if (state == HIGHSCORES) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (btn_Home.isTouched(event)) {
					btn_Home.Highlight(RED);
				}
			}
			
			//����ֵfalse
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

	//��Ϸ���� 
	/**
	 * ��Ϸ��ʼ
	 */
	public void StartGame() {
		
		score = 0;

		//С����½��ٶ�
		bird.accelerationy = -ScreenHeight() * 0.0001f * gravity;
		//x�ϵļ��ٺ�y�ϵ�
		bird.speedx = ScreenWidth() * 0.01f;
		bird.speedy = 0;
		//x��y�ϵľ���
		
		bird.y = ScreenHeight() / 2;
		bird.x = dpToPx(30);
		bird.rotate(0);//��ת

		//refresh camera
		cameraY = 0;
		cameraX = 0;

		//clouds���
		clouds.clear();
		createCloud((float) (Math.random() * dpToPx(300)));
		createCloud((float) (Math.random() * dpToPx(300)));

		//background buildings
		background_building_1.speedx = -dpToPx(3);
		background_building_2.speedx = -dpToPx(3);
		background_building_1.x = 0;
		background_building_2.x = background_building_1.getWidth();

		//��Ϸδ��ʼ
		notstarted = true;
		game_over = false;
		state = GAMEPLAY;
		PlayMusic();

		
		pause = false;
	}

	/**
	 * ��Ϸ����
	 */
	public synchronized void GameOver() {
		//�򿪹��
		openAd();
		//�ر�����
		StopMusic();
		//��ǰ״̬Ϊ��Ϸ����
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

	//������ͣ
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
			//�����ƣ�ѡ����ʵ�λ�úʹ�С
			cloud_sprite2.draw(canvas, dpToPx(10), dpToPx(50));
			cloud_sprite.draw(canvas, ScreenWidth() - (cloud_sprite.getWidth() * 0.75f), ScreenHeight() - (cloud_sprite.getHeight() * 0.8f));

			//�����ϻ����Դ�ȵõ��ַ������appname��������Ļ�����λ��  frawtext�����ı�
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

				//top���ƾ���
				canvas.drawRect(x, 0, x + column_edge.getWidth(), y - hole_size / 2, Black_shader);
				column_edge.draw(canvas, x, y - hole_size / 2);
				//bottom
				canvas.drawRect(x, y + hole_size / 2, x + column_edge.getWidth(), ScreenHeight(), Black_shader);
				column_edge.draw(canvas, x, y + hole_size / 2);

			}

			//draw bird
			bird.draw(canvas);

			//����
			canvas.drawText("" + score, (ScreenWidth() * 0.5f) - (Title_Paint.measureText("" + score) / 2), (float) (ScreenHeight() * 0.35f), Score_Paint);

			//��Ϸ��ʼ֮��
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
				//���Ʒ���
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

			//���Ƶ��ı����Ϸ���
			canvas.drawText("" + score, (ScreenWidth() / 2) - (Score_Paint.measureText("" + score) / 2), (float) (ScreenHeight() /2), Score_Paint);

			btn_Home.draw(canvas);

		}

		//physics��ֹ����bug����һ�����ࡣ
		super.Draw(canvas);
	}

	//Rendering of background
	/**
	 * ������Ļ����
	 * @param canvas
	 */
	public void renderBackground(Canvas canvas) {

		//���Ըı䱳����ɫ���ñ����İ���
		canvas.drawColor(Color.rgb(153, 204, 255));

		background_shader.setARGB(255, 255, 229, 240);
		int radius = DrawBackgroundCloud(canvas, (int) (ScreenHeight() / 2.5), 10);
		canvas.drawRect(0, (float) ((ScreenHeight() / 3.6) + radius * 1.5), ScreenWidth(), ScreenHeight(), background_shader);

		background_shader.setARGB(255, 108, 181, 100);
		radius = DrawBackgroundCloud(canvas, (int) (ScreenHeight() / 1.7), 7);
		canvas.drawRect(0, (float) ((ScreenHeight() / 2.3) + radius * 1.9), ScreenWidth(), ScreenHeight(), background_shader);

	}

	//�ƵĻ��� �Ƶ���״�Ͱڷ�λ��
	public int DrawBackgroundCloud(Canvas canvas, int y, int circles) {
		int radius = (int) (ScreenWidth() / (circles * 1.3));
		for (int i = 0; i < circles; i++) {
			canvas.drawCircle((float) (i * radius * 1.5), (float) (y + radius + (Math.cos(i * circles * y) * radius * 0.35f)), radius, background_shader);
		}
		return radius;
	}

	/**
	 * ������������ص�������ʱ����
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//��������������
		highscoreManager.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ������Ӷ�ջ�л�������
	 */
	@Override
	protected void onResume() {
		super.onResume();
		//ͬ�����Ƿ�������������
		highscoreManager.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		highscoreManager.onSaveInstanceState(outState);
	}

	/**
	 * �ڽ�����ͣ��ʱ�����
	 */
	@Override
	public void onPause() {
		super.onPause();
		//��ϷҲ��ͣ
		pause();
		highscoreManager.onPause();
	}

	/**
	 * ��ǰapp���ٵ���
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		highscoreManager.onDestroy();
	}
}
