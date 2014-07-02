package com.bionicsheep.lppie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

@SuppressLint("ViewConstructor")
public class PieView extends View{

	private Context mContext;
	private PieService mService;
	private Resources mResources;
	private WindowManager mWindowManager;
	private DisplayMetrics mDisplayMetrics;
	private Canvas mCanvas;
	SharedPreferences sharedPrefs;
	SharedPreferences colorPrefs;
	SharedPreferences.Editor editor;

	//Hardcoded for now
	private final float SIZE_BASE = 1.0f;
	private final int ANGLE_BASE = 13;
	private final float GAP_BASE = 1;
	private final int GRAVITY_BASE = 0;
	private final int NUM_BUTTONS = 3;
	private final float SLICE_WIDTH = ((180 - (ANGLE_BASE * 2) - (2 * GAP_BASE)) / NUM_BUTTONS);

	private float mPieScalingFactor;

	private float mPieSize;
	private float mSliceWidth;
	private int mPieAngle;
	private float mPieGap;
	private int mGravity;

	private int mDisplayWidth;
	private int mDisplayHeight;

	private int mPieOuterRadius;
	private int mPieInnerRadius;
	private int mPieIconRadius;
	
	private int selectedSlice;

	private Paint outlinePaint;
	private Paint backButtonPaint;
	private Paint homeButtonPaint;
	private Paint recentButtonPaint;
	private Paint iconPaint;

	private Path backOutlinePath;
	private Path homeOutlinePath;
	private Path recentOutlinePath;
	private Path backButtonPath;
	private Path homeButtonPath;
	private Path recentButtonPath;

	private Point mCenter;
	
	private int mPieOutlineColor;
	private int mPieBackButtonColor;
	private int mPieHomeButtonColor;
	private int mPieRecentButtonColor;
	private int mPieIconColor;
	
	ColorFilter filter;

	//private static final int PIE_OUTLINE_COLOR = 0x50ffffff;
	//private static final int PIE_BUTTON_COLOR = 0x65000000;
	private static final int PIE_ICON_COLOR = 0xffffffff;

	public PieView(Context context, PieService service, SharedPreferences cP, SharedPreferences sP) {
		super(context);
		
		sharedPrefs = sP;
		colorPrefs = cP;

		mService = service;
		mContext = context;
		mDisplayMetrics = new DisplayMetrics();
		mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
		mResources = mContext.getResources();
		
		selectedSlice = 0;
		
		outlinePaint = new Paint();
		backButtonPaint = new Paint();
		homeButtonPaint = new Paint();
		recentButtonPaint = new Paint();
		iconPaint = new Paint();
		
		getDimensions();
	}

	@Override
	protected void onDraw(Canvas canvas){
		mCanvas = canvas;
		getCanvasDimensions();
		showPie(canvas);
	}
	
	private void getCanvasDimensions(){
		mCenter = new Point(0,0);
		
		mDisplayWidth = mCanvas.getWidth();
		mDisplayHeight = mCanvas.getHeight();
		
		switch(mGravity){
		//bottom
		case 0:
			mCenter.x = mDisplayWidth / 2;
			mCenter.y = mDisplayHeight;
			break;
			//right
		case 1:
			mCenter.x = mDisplayWidth;
			mCenter.y = mDisplayHeight / 2;
			break;
			//top
		case 2:
			mCenter.x = mDisplayWidth / 2;
			mCenter.y = 0;
			break;
			//left
		case 3:
			mCenter.x = 0;
			mCenter.y = mDisplayHeight / 2;
			break;
		}
		
		setStyle();
	}

	private void getDimensions(){
		mPieSize = SIZE_BASE; //for now
		mPieAngle = ANGLE_BASE; //for now
		mPieGap = GAP_BASE; //for now
		mGravity = GRAVITY_BASE; //for now
		mSliceWidth = SLICE_WIDTH;

		mPieScalingFactor = Float.parseFloat(sharedPrefs.getString("pie_size", "1"));

		mPieInnerRadius = (int) (mResources.getDimensionPixelSize(R.dimen.pie_radius_start) * mPieSize * mPieScalingFactor);
		mPieOuterRadius = (int) (mPieInnerRadius + mResources.getDimensionPixelSize(R.dimen.pie_radius_increment) * mPieSize * mPieScalingFactor);
		mPieIconRadius = (int) (mResources.getDimensionPixelSize(R.dimen.icon_gap) + mPieInnerRadius * mPieScalingFactor);

		initializeStyle();
	}
	
	private void initializeStyle(){
		outlinePaint.setAntiAlias(true);
		outlinePaint.setStyle(Style.STROKE);
		outlinePaint.setStrokeWidth(mResources.getDimensionPixelSize(R.dimen.pie_outline_width));
		
		backButtonPaint.setAntiAlias(true);
		backButtonPaint.setStyle(Style.FILL);
		
		homeButtonPaint.setAntiAlias(true);
		homeButtonPaint.setStyle(Style.FILL);
		
		recentButtonPaint.setAntiAlias(true);
		recentButtonPaint.setStyle(Style.FILL);
		
		iconPaint.setAntiAlias(true);
	}

	private void setStyle(){
		mPieBackButtonColor = Color.parseColor(colorPrefs.getString("p1", "50ffffff"));
		mPieHomeButtonColor = Color.parseColor(colorPrefs.getString("p2", "50ffffff"));
		mPieRecentButtonColor = Color.parseColor(colorPrefs.getString("p3", "50ffffff"));
		mPieOutlineColor = Color.parseColor(colorPrefs.getString("secondary_reference", "NULL"));
		mPieIconColor = Color.parseColor(colorPrefs.getString("tertiary_reference", "#FFFFFFFF"));
		
		outlinePaint.setColor(mPieOutlineColor);
		backButtonPaint.setColor(mPieBackButtonColor);
		homeButtonPaint.setColor(mPieHomeButtonColor);
		recentButtonPaint.setColor(mPieRecentButtonColor);
	
		
		ColorFilter iconFilter = new PorterDuffColorFilter(PIE_ICON_COLOR, PorterDuff.Mode.MULTIPLY);
		iconPaint.setColorFilter(iconFilter);
		filter = new PorterDuffColorFilter(mPieIconColor, PorterDuff.Mode.MULTIPLY);
		iconPaint.setColorFilter(filter);
	}

	public void showPie(Canvas canvas){
		//drawBackground(canvas);
		drawButtons(canvas);
		drawOutlines(canvas);
		drawIcons(canvas);
		//drawBatteryBar(canvas);
		//drawTime(canvas);
		//drawInfoLine1(canvas);
		//drawInfoLine2(canvas);
		//drawInfoLine3(canvas);
		//drawChevrons(canvas);
	}

	private Path drawCurvedPath(int innerRadius, int outerRadius, float start, float sweep){
		RectF bb = new RectF(mCenter.x - outerRadius, mCenter.y - outerRadius, mCenter.x + outerRadius, mCenter.y + outerRadius);
		RectF bbi = new RectF(mCenter.x - innerRadius, mCenter.y - innerRadius, mCenter.x + innerRadius, mCenter.y + innerRadius);
		Path path = new Path();
		path.arcTo(bb, start, sweep);
		path.arcTo(bbi, (int)(start + sweep - (0.5 * GAP_BASE)), (int)(-sweep + (0.5 * GAP_BASE)));
		path.close();
		return path;
	}

	private void drawOutlines(Canvas canvas){
		Log.d("is hardware ", " accelerated " + canvas.isHardwareAccelerated());
		int start = ANGLE_BASE - 180;
		backOutlinePath = drawCurvedPath(mPieInnerRadius, mPieOuterRadius, start, mSliceWidth);
		start += (mSliceWidth + GAP_BASE);
		homeOutlinePath = drawCurvedPath(mPieInnerRadius, mPieOuterRadius, start, mSliceWidth);
		start += (mSliceWidth + GAP_BASE);
		recentOutlinePath = drawCurvedPath(mPieInnerRadius, mPieOuterRadius, start, mSliceWidth);


		canvas.drawPath(backOutlinePath, outlinePaint);
		canvas.drawPath(homeOutlinePath, outlinePaint);
		canvas.drawPath(recentOutlinePath, outlinePaint);
	}

	private void drawButtons(Canvas canvas){
		int start = ANGLE_BASE - 180;
		backButtonPath = drawCurvedPath(mPieInnerRadius, mPieOuterRadius, start, mSliceWidth);
		start += (mSliceWidth + GAP_BASE);
		homeButtonPath = drawCurvedPath(mPieInnerRadius, mPieOuterRadius, start, mSliceWidth);
		start += (mSliceWidth + GAP_BASE);
		recentButtonPath = drawCurvedPath(mPieInnerRadius, mPieOuterRadius, start, mSliceWidth);

		canvas.drawPath(backButtonPath, backButtonPaint);
		canvas.drawPath(homeButtonPath, homeButtonPaint);
		canvas.drawPath(recentButtonPath, recentButtonPaint);
	}

	private void drawIcons(Canvas canvas){
		final String packName = "com.android.systemui";
		String mHomeName = "ic_sysbar_home";
		String mBackName = "ic_sysbar_back";
		String mRecentName = "ic_sysbar_recent";
		int rotateDegree;
		int mDrawableResID;
		Drawable backDrawable = null;
		Drawable homeDrawable = null;
		Drawable recentDrawable = null;
		Bitmap back = null;
		Bitmap home = null;
		Bitmap recent = null;

		try {
			PackageManager manager = mContext.getPackageManager();
			Resources mApkResources = manager.getResourcesForApplication(packName);
			mDrawableResID = mApkResources.getIdentifier(mBackName, "drawable",packName);
			backDrawable = mApkResources.getDrawable(mDrawableResID);
			mDrawableResID = mApkResources.getIdentifier(mHomeName, "drawable",packName);
			homeDrawable = mApkResources.getDrawable(mDrawableResID);
			mDrawableResID = mApkResources.getIdentifier(mRecentName, "drawable",packName);
			recentDrawable = mApkResources.getDrawable(mDrawableResID);

			back = drawableToBitmap(backDrawable);
			home = drawableToBitmap(homeDrawable);	
			recent = drawableToBitmap(recentDrawable);	

		}
		catch (NameNotFoundException e) {
			recent = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sysbar_recent);
			back = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sysbar_back);
			home = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sysbar_home);		
		}

		rotateDegree = (int) (180 - (ANGLE_BASE + (mSliceWidth/2)));
		Point backPt = new Point((int) (mCenter.x + (mPieIconRadius * Math.cos(Math.toRadians(rotateDegree)))),
				(int) (mCenter.y - (mPieIconRadius * Math.sin(Math.toRadians(rotateDegree)))));
		back = rotate(back,(int)-mSliceWidth);
		canvas.drawBitmap(back, backPt.x - back.getWidth()/2, backPt.y - back.getHeight()/2, iconPaint);

		Point homePt = new Point(mCenter.x, mCenter.y - mPieIconRadius);
		canvas.drawBitmap(home, homePt.x - home.getWidth()/2,(int) (homePt.y - home.getHeight()/2), iconPaint);

		rotateDegree = (int) (ANGLE_BASE + (mSliceWidth/2));
		Point recentPt = new Point((int) (mCenter.x + (mPieIconRadius * Math.cos(Math.toRadians(rotateDegree)))), (int) (mCenter.y - (mPieIconRadius * Math.sin(Math.toRadians(rotateDegree)))));
		recent = rotate(recent,(int)mSliceWidth);
		canvas.drawBitmap(recent, recentPt.x - recent.getWidth()/2, recentPt.y - recent.getHeight()/2, iconPaint);
	}

	public static Bitmap drawableToBitmap (Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap); 
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	private Bitmap rotate(Bitmap img, int angle){
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);

		Bitmap bmp = Bitmap.createScaledBitmap(img,img.getWidth(),img.getHeight(),true);
		bmp = Bitmap.createBitmap(bmp , 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

		return bmp;
	}

	public int checkForAction(MotionEvent event){
		int x = (int) (event.getRawX() - (this.getWidth()/2));
		int y = (int) (this.getHeight() - event.getRawY());

		double radius = Math.sqrt( x * x + y * y );
		double angle = Math.toDegrees(Math.acos( x / radius ));

		if(radius < mPieOuterRadius && radius > mPieInnerRadius){
			if(angle < mPieAngle || angle > 180 - mPieAngle){
				if(selectedSlice != 0){
					selectedSlice = 0;
					highlight(0);
				}
				return 0;
			}
			else if(angle > mPieAngle && angle < (mPieAngle + mSliceWidth + (mPieGap/2))){
				//recent
				if(selectedSlice != 3){
					selectedSlice = 3;
					highlight(3);
					tickSound();
				}
				return 3;
			}
			else if(angle < (180 - mPieAngle) && angle > (180 - (mPieAngle + mSliceWidth + (mPieGap/2)))){
				//back
				if(selectedSlice != 1){
					selectedSlice = 1;
					highlight(1);
					tickSound();
				}
				return 1;
			}
			else{
				//home
				if(selectedSlice != 2){
					selectedSlice = 2;
					highlight(2);
					tickSound();
				}
				return 2;
			}
		}else{
			if(selectedSlice != 0){
				selectedSlice = 0;
				highlight(0);
				
			}
			return 0;
		}
	}

	private void highlight(int n){
		resetColor();
		editor = colorPrefs.edit();
		
		switch(n){
		case 0:
			break;
		case 1:
			editor.putString("p1", colorPrefs.getString("secondary_reference", "NULL"));
			break;
		case 2:
			editor.putString("p2", colorPrefs.getString("secondary_reference", "NULL"));
			break;
		case 3:
			editor.putString("p3", colorPrefs.getString("secondary_reference", "NULL"));
			break;
		}

		editor.commit();
		this.invalidate(0, mDisplayHeight - mPieOuterRadius, mDisplayWidth, mDisplayHeight);
	}
	
	public void resetColor(){
		editor = colorPrefs.edit();
		editor.putString("p1", colorPrefs.getString("primary_reference", "NULL"));
		editor.putString("p2", colorPrefs.getString("primary_reference", "NULL"));
		editor.putString("p3", colorPrefs.getString("primary_reference", "NULL"));
		editor.commit();
	}
	
	private void tickSound(){
		mService.tickSound();
	}

}
