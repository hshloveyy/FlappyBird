package com.mygame.unit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;

public class Sprite {
	Bitmap unrotated_image_sequence[], image_sequence[];
	int current_image = 0;
	private float direction = 0;
	//从开机到现在的毫秒数，睡眠不包括在内
	long start_time = SystemClock.uptimeMillis();/**时间间隔*/
	int animation_speed;
	//初始化图片绘制
	public Paint imagePaint = new Paint();

	public Sprite(Bitmap image, float scale) {
		image_sequence = new Bitmap[1];

		unrotated_image_sequence = new Bitmap[1];
		image_sequence[0] = unrotated_image_sequence[0] = Bitmap.createScaledBitmap(image, (int) ((float) (scale)), (int) (((float) (scale) / image.getWidth()) * image.getHeight()), true);

	}

	public Sprite(Bitmap sprite_sheet, float scale, int itemsX, int length, int animation_speed) {
		this.animation_speed = animation_speed;
		unrotated_image_sequence = convert(sprite_sheet, itemsX, length);
		image_sequence = new Bitmap[length];
		//比例图片
		for (int count = 0; count < length; count++) {
			//在Android中，修改图片大小是件很简单的事，只需要createscaledbitmap函数  第一个参数是待修改的Bitmap,第二第三个参数分别为修改后的宽、高，
			image_sequence[count] = unrotated_image_sequence[count] = Bitmap.createScaledBitmap(unrotated_image_sequence[count], (int) ((float) (scale)), (int) (((float) (scale) / unrotated_image_sequence[count].getWidth()) * unrotated_image_sequence[count].getHeight()), true);
		}
		start_time = (long) (SystemClock.uptimeMillis() + (Math.random() * 400));
	}

	public int getWidth() {
		return image_sequence[0].getWidth();
	}

	public int getHeight() {
		return image_sequence[0].getHeight();
	}

	public float getDirection() {
		return direction;
	}

	//在屏幕上绘制图形
	public void draw(Canvas canvas, float x, float y) {
		//draw image
		canvas.drawBitmap(image_sequence[current_image], x, y, imagePaint);
		//修改下一个图片
		if (image_sequence.length > 1) {
			long now = SystemClock.uptimeMillis();
				//卡通图片加速
			if (now > start_time + (500 - animation_speed)) {
				start_time = SystemClock.uptimeMillis();
				current_image++;
				if (current_image + 1 > image_sequence.length)
					current_image = 0;
			}
		}
	}

	//图片变为数组
	private Bitmap[] convert(Bitmap sprite_sheet, int itemsX, int length) {
		//定义的int是整数类型  math.ceil向上舍入，即它总是将数值向上舍入为最接近的整数；
		int itemsY = (int) Math.ceil(length / itemsX);
		int tile_height = (int) (sprite_sheet.getHeight() / itemsY);
		int tile_width = (int) (sprite_sheet.getWidth() / itemsX);
		Bitmap image_sequence[] = new Bitmap[length];

		for (int y = 0; y < itemsY; y++) {
			for (int x = 0; x < itemsX; x++) {
				if ((x + (itemsX * y)) < length)
					image_sequence[x + (itemsX * y)] = Bitmap.createBitmap(sprite_sheet, (x * tile_width), (y * tile_height), tile_width, tile_height);
			}
		}
		return image_sequence;
	}

	/**
	 * 指定角度，
	 * 
	 * @param 方向为0
	 *       
	 */
	//angle位指定的角度  direction为方向
	//构造函数
	public void rotate(float direction) {
		float angle = direction;
		this.direction = direction;

		//第一个函数是用来防止边缘的锯齿，第二个函数是用来对位图进行滤波处理。
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap m = Bitmap.createBitmap((int) 5, 5, Bitmap.Config.ARGB_8888);

		
		RectF a = new RectF(0, 0, unrotated_image_sequence[0].getWidth(), unrotated_image_sequence[0].getHeight());
		//图片旋转
		Matrix mat = new Matrix();
		mat.setRotate(direction, (unrotated_image_sequence[0].getWidth() / 2), (unrotated_image_sequence[0].getHeight() / 2));
		mat.mapRect(a);

		for (int i = 0; i < image_sequence.length; i++) {
			image_sequence[i] = Bitmap.createScaledBitmap(m, (int) a.width(), (int) a.height(), true);
			Canvas canvas = new Canvas(image_sequence[i]);
		
			canvas.rotate(angle, image_sequence[i].getWidth() / 2, image_sequence[i].getHeight() / 2);
			canvas.drawBitmap(unrotated_image_sequence[i], (image_sequence[i].getWidth() / 2) - (unrotated_image_sequence[i].getWidth() / 2), (image_sequence[i].getHeight() / 2) - (unrotated_image_sequence[i].getHeight() / 2), paint);
			canvas.rotate(-angle, image_sequence[i].getWidth() / 2, image_sequence[i].getHeight() / 2);
		}

	}

	public static Bitmap Scale(Bitmap unscaled, float scale) {
		return Bitmap.createScaledBitmap(unscaled, (int) ((float) (scale)), (int) (((float) (scale) / unscaled.getWidth()) * unscaled.getHeight()), true);
	}
}
