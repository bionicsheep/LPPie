package com.bionicsheep.lppie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.View;

public class PieControls extends View{

	int height, width;
	int origX, origY;
	
	int innerRadius;
	int outerRadius;
	
	RectF outsideBounds;
	RectF insideBounds;

	private Paint painter;

	public PieControls(Context context) {
		super(context);
		painter = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas){
		width = canvas.getWidth();
		height = canvas.getHeight();
		
		origY = height;
		origX = width / 2;

		drawOutlines(canvas);
		drawFill(canvas);
		drawIcons(canvas);
		
		invalidate();
	}

	private void drawOutlines(Canvas canvas){		painter.setStyle(Paint.Style.FILL_AND_STROKE);
		painter.setColor(Color.argb(150,255,255,255));
		painter.setStyle(Paint.Style.STROKE);
		painter.setStrokeWidth((float)5.0);
		
		innerRadius = (width / 6);
		outerRadius = (width / 5) * 2;
		
		Point p1 = new Point((int) (origX + (innerRadius * Math.cos(Math.toRadians(165)))), (int) (origY - (innerRadius * Math.sin(Math.toRadians(165)))));
		Point p2 = new Point((int) (origX + (innerRadius * Math.cos(Math.toRadians(115)))), (int) (origY - (innerRadius * Math.sin(Math.toRadians(115)))));
		Point p3 = new Point((int) (origX + (innerRadius * Math.cos(Math.toRadians(65)))), (int) (origY - (innerRadius * Math.sin(Math.toRadians(65)))));
		Point p4 = new Point((int) (origX + (innerRadius * Math.cos(Math.toRadians(15)))), (int) (origY - (innerRadius * Math.sin(Math.toRadians(15)))));
		Point p5 = new Point((int) (origX + (outerRadius * Math.cos(Math.toRadians(165)))), (int) (origY - (outerRadius * Math.sin(Math.toRadians(165)))));
		Point p6 = new Point((int) (origX + (outerRadius * Math.cos(Math.toRadians(115)))), (int) (origY - (outerRadius * Math.sin(Math.toRadians(115)))));
		Point p7 = new Point((int) (origX + (outerRadius * Math.cos(Math.toRadians(65)))), (int) (origY - (outerRadius * Math.sin(Math.toRadians(65)))));
		Point p8 = new Point((int) (origX + (outerRadius * Math.cos(Math.toRadians(15)))), (int) (origY - (outerRadius * Math.sin(Math.toRadians(15)))));
		
		insideBounds = new RectF(origX - innerRadius, origY - innerRadius, origX + innerRadius, origY + innerRadius);
		outsideBounds = new RectF(origX - outerRadius, origY - outerRadius, origX + outerRadius, origY + outerRadius);
		
		canvas.drawArc(insideBounds, -165, 150, false, painter);
		canvas.drawArc(outsideBounds, -165, 150, false, painter);

		canvas.drawLine(p1.x, p1.y, p5.x, p5.y, painter);
		canvas.drawLine(p2.x, p2.y, p6.x, p6.y, painter);
		canvas.drawLine(p3.x, p3.y, p7.x, p7.y, painter);
		canvas.drawLine(p4.x, p4.y, p8.x, p8.y, painter);
	}

	private void drawFill(Canvas canvas){

	}
	
	private void drawIcons(Canvas canvas){
		Point homePt = new Point(origX,origY - (innerRadius + outerRadius)/2);
		Bitmap home = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sysbar_home);		
		canvas.drawBitmap(home, homePt.x - home.getWidth()/2,(int) (homePt.y - home.getHeight()/1.5), painter);
		
		
		Point backPt = new Point((int) (origX + ((innerRadius + outerRadius)/2 * Math.cos(Math.toRadians(165)))), (int) (origY - ((innerRadius + outerRadius)/2 * Math.sin(Math.toRadians(165)))));
		Bitmap back = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sysbar_back);
		back = rotate(back,-50);
		canvas.drawBitmap(back, backPt.x - back.getWidth()/2, backPt.y - back.getHeight(),painter);
		
		Point recentPt = new Point((int) (origX + ((innerRadius + outerRadius)/2 * Math.cos(Math.toRadians(15)))), (int) (origY - ((innerRadius + outerRadius)/2 * Math.sin(Math.toRadians(15)))));
		Bitmap recent = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sysbar_recent);
		recent = rotate(recent,50);
		canvas.drawBitmap(recent, recentPt.x - recent.getWidth()/2, recentPt.y - recent.getHeight(),painter);
		
		
	}
	
	private Bitmap rotate(Bitmap img, int angle){
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		
		Bitmap bmp = Bitmap.createScaledBitmap(img,img.getWidth(),img.getHeight(),true);
		bmp = Bitmap.createBitmap(bmp , 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		
		return bmp;
	}

}
