package com.mygame.unit;

import android.graphics.RectF;

/**
 * �����ϰ�����
 * @author heshaohua
 *
 */
public class Physics {
	RectF a = null, b = null;

	//�����ϰ���
	/**
	 * �ж��Ƿ񽻲�
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param x2
	 * @param y2
	 * @param width2
	 * @param height2
	 * @return
	 */
	public boolean intersect(int x, int y, int width, int height, int x2, int y2, int width2, int height2) {
		a = new RectF(x, y, x + width, y + height);
		b = new RectF(x2, y2, x2 + width2, y2 + height2);
		return a.intersect(b);
	}

	/**
	 * �ж�С��ɹ��Ƿ�÷�
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param pointx
	 * @param pointy
	 * @return
	 */
	public boolean intersect(int x, int y, int width, int height, int pointx, int pointy) {
	a = new RectF(x, y, x + width, y + height);
		if ((pointx > x) && (pointy > y) && (pointx < (x + width)) && (pointy < (y + height))) {
			return (true);
	}
		return false;
}

//	public void drawDebug(Canvas canvas) {
		//��ײ

	//	Paint paint = new Paint();
	//	paint.setColor(Color.RED);
		//paint.setStyle(Style.STROKE);
	//	paint.setStrokeWidth(5);
	//	if (a != null)
	//		canvas.drawRect(a, paint);
//		if (b != null)
	//		canvas.drawRect(b, paint);

//	}
}
