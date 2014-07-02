package com.bionicsheep.lppie;

import android.content.Context;
import android.graphics.Path;
import android.view.View;

public class PieButton {
	
	private View mView;
	
	private boolean mSelected;
	private Path mPath;
	
	public PieButton(View view, Context context){
		mSelected = false;
		mView = view;
	}
	
	public void setPath(Path path){
		mPath = path;
	}
	
	public Path getPath(){
		return mPath;
	}
	
	public void setAlpha(float alpha){
		if(mView != null){
			mView.setAlpha(alpha);
		}
	}
	
	public float getAlpha(){
		if(mView != null){
			return mView.getAlpha();
		}
		return 0;
	}
	
	public boolean isSelected(){
		return mSelected;
	}
	
	public void setSelected(boolean selected){
		mSelected = selected;
	}
	
	public View getView(){
		return mView;
	}
}
