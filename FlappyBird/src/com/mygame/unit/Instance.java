package com.mygame.unit;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * 基类，即在游戏用小鸟的父类，用于绘制小鸟，修改方向，判断是否碰撞的父类
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
	 * 构造函数，实例化方法
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
	 * 更新当前实例的成员变量
	 * 主要更新速度跟加速度的横纵坐标
	 */
	public void Update() {
		x += speedx;
		y += speedy;
		speedx += accelerationx;
		speedy += accelerationy;
	}

	/**
	 * 传递一个方向获取对应的角度
	 * @param direction
	 */
	public void rotate(float direction) {
		sprite.rotate(direction);
	}

	/**
	 * 获取当前对象方向
	 * @return
	 */
	public float getDirection() {
		return sprite.getDirection();
	}

	/**
	 * 获取高度
	 * @return
	 */
	public int getHeight() {
		return sprite.getHeight();
	}

	/**
	 * 获取宽度
	 * @return
	 */
	public int getWidth() {
		return sprite.getWidth();
	}

	//draw the sprite to screen
	/**
	 * 绘制对象到画布
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
	 * 事件触碰发生 返回参数  现象时的屏幕坐标，精灵坐标 
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
	 * 判断是否碰撞
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
