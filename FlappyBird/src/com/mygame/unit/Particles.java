package com.mygame.unit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Particles {

	//粒数
	Bitmap particle_img;
	int number = 10;
	float[] Particle_x = new float[number], Particle_y = new float[number], direction = new float[number], life = new float[number], alpha = new float[number];
	Paint paint = new Paint();
	int counter = 0, delay = 2, particle_direction = -1, speed = 1, particle_life;
	int initialx_offset = 70, initialy_offset = 40;

	/**
	 * 实例化方法，参数有图片，移动的速度、方向，偏移量等
	 * @param particle_img
	 * @param delay
	 * @param speed
	 * @param particle_direction
	 * @param initialx_offset
	 * @param initialy_offset
	 * @param particle_life
	 * @param numberOfParticles
	 */
	public Particles(Bitmap particle_img, int delay, int speed, int particle_direction, int initialx_offset, int initialy_offset, int particle_life, int numberOfParticles) {
		this.particle_img = particle_img;
		this.delay = delay;
		this.speed = speed;
		this.particle_direction = particle_direction;
		this.initialx_offset = initialx_offset;
		this.initialy_offset = initialy_offset;
		this.particle_life = particle_life;
		this.number = numberOfParticles;

		for (int i = 0; i < number; i++) {
			Particle_x[i] = (float) ((Math.random() * initialx_offset) - (Math.random() * initialx_offset));//initial particle x-------CHANGE ME for larger initial cloud
			Particle_y[i] = (float) ((Math.random() * initialy_offset) - (Math.random() * initialy_offset));//initial particle y-------CHANGE ME for larger initial cloud
			if (particle_direction == -1)
				direction[i] = (int) (Math.random() * 60);//direction of each particle-------CHANGE ME to change direction of particle
			else
				direction[i] = (float) ((int) (Math.random() * 20) - (Math.random() * 20) + particle_direction);
			life[i] = particle_life + (int) (Math.random() * particle_life);//life of each particle-------CHANGE ME for growing clouds
			alpha[i] = (int) (Math.random() * 10);//alpha of each particle-------CHANGE ME to change transparency
		}
	}

	/**
	 * 更新云朵图像，包括数量、位置的偏移等
	 */
	public void update() {
		counter++;
		if (counter > delay) {
			counter = 0;
			for (int i = 0; i < number; i++) {
				life[i]--;
				if (life[i] > 0) {
					Particle_x[i] = (float) (Particle_x[i] + (Math.cos(-((direction[i] * Math.PI / 180) - (Math.PI / 2))) * (Math.random() * speed)));
					Particle_y[i] = (float) (Particle_y[i] - (Math.sin(-((direction[i] * Math.PI / 180) - (Math.PI / 2))) * (Math.random() * speed)));
				} else {
					Particle_x[i] = (float) ((Math.random() * initialx_offset) - (Math.random() * initialx_offset));
					Particle_y[i] = (float) ((Math.random() * initialy_offset) - (Math.random() * initialy_offset));
					if (particle_direction == -1)
						direction[i] = (int) (Math.random() * 360);
					else
						direction[i] = (float) ((int) (Math.random() * 15) - (Math.random() * 15) + particle_direction);
					life[i] = particle_life + (int) (Math.random() * particle_life);
				}
			}
		}
	}

	/**
	 * 设置方向
	 * @param Direction
	 */
	public void setDirection(int Direction) {
		particle_direction = Direction;
	}

	/**
	 * 将图像绘制到界面上
	 * @param canvas
	 * @param x
	 * @param y
	 */
	public void draw(Canvas canvas, float x, float y) {
		//绘制云朵
		for (int i = 0; i < number; i++) {
			//设置透明度
			paint.setAlpha((int) life[i]);
			//将图片画在画板上
			canvas.drawBitmap(particle_img, x + Particle_x[i], y + Particle_y[i], paint);
		}
	}
}
