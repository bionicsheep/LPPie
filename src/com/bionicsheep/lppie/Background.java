package com.bionicsheep.lppie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class Background extends View{
	
	private Paint painter;
	private int width;
	private int height;
	private int alpha = 0;
	RectF background = new RectF();

	public Background(Context context) {
		super(context);
		painter = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.alpha = 0;
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		this.width = canvas.getWidth();
		this.height = canvas.getHeight();
		
		background.set(0, 0, width, height);
		painter.setColor(Color.argb(alpha,0,0,0));
		canvas.drawRect(background, painter);
	}
	
	public void dim(){
		while(alpha < 250){
			alpha++;
			invalidate();
		}
	}

}
