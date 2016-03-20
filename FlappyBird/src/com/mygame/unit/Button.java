package com.mygame.unit;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;

/**
 * 按钮类
 * @author heshaohua
 */
public class Button extends Instance {
	public final int TEXT_BTN = 0, SPRITE_BTN = 1;
	public int type;
	public Paint textPaint = new Paint();
	public String text;

	/**
	 * Create new sprite button
	 * 按钮实例方法，构造函数，此按钮跟android有所区别，为自定义的按钮类
	 * @param sprite
	 *            sprite to bisplay on button
	 * @param x
	 *            x-coordinate to draw button
	 * @param y
	 *            y-coordinate to draw button
	 * @param screen
	 *            A reference to the main nudge engine screen instance
	 * @param world
	 *            true if you wish to draw the button relative to the camera or false if you wish to draw it relative to screen
	 */
	public Button(Sprite sprite, float x, float y, Screen screen, boolean world) {
		super(sprite, x, y, screen, world);
		type = SPRITE_BTN;
	}

	/**
	 * Create new text button
	 * 同样为实例化，参数不一样，实例化的时候还有文本，字体参数设置
	 * @param text
	 *            text to bisplay on button
	 * @param dpSize
	 *            size of text in dp
	 * @param font
	 *            Typface of text
	 * @param color
	 *            Color to use for text
	 * @param x
	 *            x-coordinate to draw button
	 * @param y
	 *            y-coordinate to draw button
	 * @param screen
	 *            A reference to the main nudge engine screen instance
	 * @param world
	 *            true if you wish to draw the button relative to the camera or false if you wish to draw it relative to screen
	 */
	public Button(String text, int dpSize, Typeface font, int color, float x, float y, Screen screen, boolean world) {
		super(null, x, y, screen, world);
		type = TEXT_BTN;
		textPaint = new Paint();
		textPaint.setTextSize(screen.dpToPx(dpSize));
		textPaint.setAntiAlias(true);
		textPaint.setColor(color);
		textPaint.setTypeface(font);
		this.text = text;
	}

	/**
	 * 高亮文本
	 * @param color
	 */
	public void Highlight(int color) {
		ColorFilter filter = new LightingColorFilter(1, color);
		if (type == SPRITE_BTN)
			sprite.imagePaint.setColorFilter(filter);
		else
			textPaint.setColorFilter(filter);
	}

	/**
	 * 取消高亮
	 */
	public void LowLight() {
		ColorFilter filter = null;
		if (type == SPRITE_BTN)
			sprite.imagePaint.setColorFilter(filter);
		else
			textPaint.setColorFilter(filter);
	}

	/**
	 * 覆盖父类的获取宽度的方法，重新定义获取当前按钮宽度
	 */
	@Override
	public int getWidth() {
		if (type == SPRITE_BTN)
			return super.getWidth();
		else {
			Rect bounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			return bounds.width();
		}
	}

	/**
	 * 覆盖父类的获取高度的方法，重新定义获取当前按钮高度
	 */
	@Override
	public int getHeight() {
		if (type == SPRITE_BTN)
			return super.getHeight();
		else {
			Rect bounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			return bounds.height();
		}
	}

	//draw the sprite to screen
	/**
	 * 绘制图像到界面上
	 */
	@Override
	public void draw(Canvas canvas) {
		if (type == SPRITE_BTN)
			super.draw(canvas);
		else {
			canvas.drawText(text, x, y + getHeight(), textPaint);
	//gai		if (screen.debug_mode)
	//gai			physics.drawDebug(canvas);
		}

	}

	/**
	 * 覆盖父类事件触碰发生 返回参数  现象时的屏幕坐标，精灵坐标 
	 */
	@Override
	public boolean isTouched(MotionEvent event) {
		if (world)
			return physics.intersect(screen.ScreenX((int) x), screen.ScreenY((int) y), getWidth(), (int) getHeight(), (int) event.getX(), (int) event.getY());
		else
			return physics.intersect((int) x, (int) y, getWidth(), (int) getHeight(), (int) event.getX(), (int) event.getY());

	}

}
