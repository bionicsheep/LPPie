package com.bionicsheep.lppie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PieControls extends View{
	
	SharedPreferences sp;
	SharedPreferences.Editor editor;

	int height, width;
	int origX, origY;
	
	int innerRadius;
	int outerRadius;
	int selected = 0;
	
	RectF outsideBounds;
	RectF insideBounds;
	RectF middleBounds;

	private Paint painter;
	
	Canvas canvas;

	public PieControls(Context context) {
		super(context);
		painter = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public PieControls(Context context, SharedPreferences prefs) {
		super(context);
		painter = new Paint(Paint.ANTI_ALIAS_FLAG);
		sp = prefs;
	}

	@Override
	protected void onDraw(Canvas canvas){
		width = canvas.getWidth();
		height = canvas.getHeight();
		
		origY = height;
		origX = width / 2;

		drawPie(canvas);
	}
	
	private void drawPie(Canvas canvas){
		drawOutlines(canvas);
		drawIcons(canvas);
	}

	private void drawOutlines(Canvas canvas){
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
		middleBounds = new RectF(origX - (outerRadius + innerRadius)/2, origY - (outerRadius + innerRadius)/2, origX + (outerRadius + innerRadius)/2, origY + (outerRadius + innerRadius)/2);
		
		painter.setStrokeWidth((float)(outerRadius-innerRadius-1));
		painter.setStyle(Paint.Style.STROKE);
		
		painter.setColor(Color.parseColor(sp.getString("p1", "#FF000000")));
		canvas.drawArc(middleBounds, -165, 50, false, painter);
		
		painter.setColor(Color.parseColor(sp.getString("p2", "#FF000000")));
		canvas.drawArc(middleBounds, -115, 50, false, painter);
		
		painter.setColor(Color.parseColor(sp.getString("p3", "#FF000000")));
		canvas.drawArc(middleBounds, -65, 50, false, painter);
		
		painter.setColor(Color.argb(150,255,255,255));
		painter.setStrokeWidth((float)5.0);
		
		canvas.drawArc(insideBounds, -165, 150, false, painter);
		canvas.drawArc(outsideBounds, -165, 150, false, painter);

		canvas.drawLine(p1.x, p1.y, p5.x, p5.y, painter);
		canvas.drawLine(p2.x, p2.y, p6.x, p6.y, painter);
		canvas.drawLine(p3.x, p3.y, p7.x, p7.y, painter);
		canvas.drawLine(p4.x, p4.y, p8.x, p8.y, painter);
	}

	private void drawIcons(Canvas canvas){
		painter.setColor(Color.argb(255,255,255,255));
		
		Point homePt = new Point(origX,origY - (int)((innerRadius + outerRadius)/1.8));
		Bitmap home = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sysbar_home);		
		canvas.drawBitmap(home, homePt.x - home.getWidth()/2,(int) (homePt.y - home.getHeight()/2), painter);
		
		
		Point backPt = new Point((int) (origX + ((innerRadius + outerRadius)/1.8 * Math.cos(Math.toRadians(140)))), (int) (origY - ((innerRadius + outerRadius)/1.8 * Math.sin(Math.toRadians(140)))));
		Bitmap back = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sysbar_back);
		back = rotate(back,-50);
		canvas.drawBitmap(back, backPt.x - back.getWidth()/2, backPt.y - back.getHeight()/2,painter);
		
		Point recentPt = new Point((int) (origX + ((innerRadius + outerRadius)/1.8 * Math.cos(Math.toRadians(40)))), (int) (origY - ((innerRadius + outerRadius)/1.8 * Math.sin(Math.toRadians(40)))));
		Bitmap recent = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sysbar_recent);
		recent = rotate(recent,50);
		canvas.drawBitmap(recent, recentPt.x - recent.getWidth()/2, recentPt.y - recent.getHeight()/2,painter);
		
		
	}
	
	private Bitmap rotate(Bitmap img, int angle){
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		
		Bitmap bmp = Bitmap.createScaledBitmap(img,img.getWidth(),img.getHeight(),true);
		bmp = Bitmap.createBitmap(bmp , 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		
		return bmp;
	}
	
	@SuppressLint("InlinedApi")
	public int checkForAction(MotionEvent event){
		int x = (int) (event.getRawX() - origX);
		int y = (int) (this.getHeight() - event.getRawY());
		
		Log.d("pie","x " + x);
		Log.d("pie", "y " + y);
		
		double radius = Math.sqrt( x * x + y * y );
		double angle = Math.toDegrees(Math.acos( x / radius ));
		
		if(radius < outerRadius && radius > innerRadius){
			if(angle > 115 && angle < 165){
				highlight("p1");
				return 1;
			}else if(angle > 65 && angle < 115){
				highlight("p2");
				return 2;
			}else if(angle > 15 && angle < 65){
				highlight("p3");
				return 3;
			}
		}
		highlight("none");
		return -1;
	}
	
	private void highlight(String slot){
		editor = sp.edit();
		editor.putString("p1", "#73000000");
		editor.putString("p2", "#73000000");
		editor.putString("p3", "#73000000");
		
		if(slot.equals("p1")){
			editor.putString("p1", "#BFFFFFFF");
		}else if(slot.equals("p2")){
			editor.putString("p2", "#BFFFFFFF");
		}else if(slot.equals("p3")){
			editor.putString("p3", "#BFFFFFFF");
		}
		editor.commit();
		invalidate();
	}
}
