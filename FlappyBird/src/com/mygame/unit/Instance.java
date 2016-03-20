package com.mygame.unit;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * ���࣬������Ϸ��С��ĸ��࣬���ڻ���С���޸ķ����ж��Ƿ���ײ�ĸ���
 * @author heshaohua
 *
 */
public class Instance {
	public float x, y, speedx = 0, speedy = 0, accelerationx = 0, accelerationy = 0;
	public Sprite sprite;
	Screen screen;
	Physics physics = new Physics();
	boolean world = true;

	/**
	 * ���캯����ʵ��������
	 * @param sprite
	 * @param x
	 * @param y
	 * @param screen
	 * @param world
	 */
	public Instance(Sprite sprite, float x, float y, Screen screen, boolean world) {
		this.sprite = sprite;
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.world = world;
	}

	//update the Object
	/**
	 * ���µ�ǰʵ���ĳ�Ա����
	 * ��Ҫ�����ٶȸ����ٶȵĺ�������
	 */
	public void Update() {
		x += speedx;
		y += speedy;
		speedx += accelerationx;
		speedy += accelerationy;
	}

	/**
	 * ����һ�������ȡ��Ӧ�ĽǶ�
	 * @param direction
	 */
	public void rotate(float direction) {
		sprite.rotate(direction);
	}

	/**
	 * ��ȡ��ǰ������
	 * @return
	 */
	public float getDirection() {
		return sprite.getDirection();
	}

	/**
	 * ��ȡ�߶�
	 * @return
	 */
	public int getHeight() {
		return sprite.getHeight();
	}

	/**
	 * ��ȡ���
	 * @return
	 */
	public int getWidth() {
		return sprite.getWidth();
	}

	//draw the sprite to screen
	/**
	 * ���ƶ��󵽻���
	 * @param canvas
	 */
	public void draw(Canvas canvas) {
		//draw image
		if (world)
			sprite.draw(canvas, screen.ScreenX((int) x), screen.ScreenY((int) y));
		else
			sprite.draw(canvas, x, y);

	 //gai    if (screen.debug_mode)
	//gai		physics.drawDebug(canvas);
	}
	
	/**
	 * �¼��������� ���ز���  ����ʱ����Ļ���꣬�������� 
	 * @param event
	 * @return
	 */
	public boolean isTouched(MotionEvent event) {
		if (world)
			return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), sprite.getWidth(), (int) sprite.getHeight(), (int) event.getX(), (int) event.getY());
		else
			return physics.intersect((int) x, (int) y, sprite.getWidth(), (int) sprite.getHeight(), (int) event.getX(), (int) event.getY());
	}

	/**
	 * �ж��Ƿ���ײ
	 * @param b
	 * @return
	 */
	public boolean CollidedWith(Instance b) {
		if (world)
			return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), sprite.getWidth(), (int) sprite.getHeight(), screen.ScreenX((int) b.x), screen.ScreenY((int) b.y), b.sprite.getWidth(), (int) b.sprite.getHeight());
		else
			return physics.intersect((int) x, (int) y, sprite.getWidth(), (int) sprite.getHeight(), (int) b.x, (int) b.y, b.sprite.getWidth(), (int) b.sprite.getHeight());
	}
}
