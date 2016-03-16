package com.mygame.unit;

import android.graphics.Canvas;
import android.view.MotionEvent;

public class Instance {
	public float x, y, speedx = 0, speedy = 0, accelerationx = 0, accelerationy = 0;
	public Sprite sprite;
	Screen screen;
	Physics physics = new Physics();
	boolean world = true;

	public Instance(Sprite sprite, float x, float y, Screen screen, boolean world) {
		this.sprite = sprite;
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.world = world;
	}

	//update the Object
	public void Update() {
		x += speedx;
		y += speedy;
		speedx += accelerationx;
		speedy += accelerationy;
	}

	public void rotate(float direction) {
		sprite.rotate(direction);
	}

	public float getDirection() {
		return sprite.getDirection();
	}

	public int getHeight() {
		return sprite.getHeight();
	}

	public int getWidth() {
		return sprite.getWidth();
	}

	//draw the sprite to screen
	public void draw(Canvas canvas) {
		//draw image
		if (world)
			sprite.draw(canvas, screen.ScreenX((int) x), screen.ScreenY((int) y));
		else
			sprite.draw(canvas, x, y);

	 //gai    if (screen.debug_mode)
	//gai		physics.drawDebug(canvas);
	}
	
//事件触碰发生 返回参数  现象时的屏幕坐标，精灵坐标 
	public boolean isTouched(MotionEvent event) {
		if (world)
			return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), sprite.getWidth(), (int) sprite.getHeight(), (int) event.getX(), (int) event.getY());
		else
			return physics.intersect((int) x, (int) y, sprite.getWidth(), (int) sprite.getHeight(), (int) event.getX(), (int) event.getY());
	}

	//碰撞
	public boolean CollidedWith(Instance b) {
		if (world)
			return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), sprite.getWidth(), (int) sprite.getHeight(), screen.ScreenX((int) b.x), screen.ScreenY((int) b.y), b.sprite.getWidth(), (int) b.sprite.getHeight());
		else
			return physics.intersect((int) x, (int) y, sprite.getWidth(), (int) sprite.getHeight(), (int) b.x, (int) b.y, b.sprite.getWidth(), (int) b.sprite.getHeight());
	}
}
