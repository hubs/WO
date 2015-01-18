package com.hlz.meizi.dialog;

import java.util.List;

import net.youmi.android.diy.banner.DiyAdSize;
import net.youmi.android.diy.banner.DiyBanner;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.azero.core.MDb;
import com.azero.utils.MToast;
import com.azero.weiget.MGifView;
import com.hlz.meizi.activitys.R;
import com.hlz.meizi.datas.MImageLoader;
import com.hlz.meizi.datas.MListData;
import com.hlz.meizi.utils.MContent;
import com.hlz.meizi.utils.MUtils;

public class MImageDialog extends Dialog {
	
	private LayoutInflater inflater;
	
	private int currentIndex;
	private int width;
	private int height;
	private MImageLoader imageLoader;
	private List<ImageView> lst;
	
	public MImageDialog(Context context) {
		super(context);
		initInflater();
	}

	public MImageDialog(Context context, int theme) {
		super(context, theme);
		initInflater();
	}
	
	private void initInflater(){
		inflater=LayoutInflater.from(getContext());
		imageLoader = MImageLoader.getInstance(getContext());
	}

	private int xDown;
	private int xUp;
	
	private int minDist=50;//最少移动距离
	// 判断滑动
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				xDown=(int) event.getX();
				break;
			case MotionEvent.ACTION_UP:
				xUp=(int) event.getX();
				int distanct=xUp-xDown;
				if(Math.abs(distanct)<minDist){//当作点击事件
					hide();
				}else if(distanct>0){
					showPreImage();				
				}else{
					showNextImage();				
				}
				break;
		}
		return super.onTouchEvent(event);
	}
	
	//显示上一张图
	private void showPreImage(){
		currentIndex--;
		if(currentIndex<0){
			hide();
			return;
		}
		ImageView obj=lst.get(getCurrent());
		String imageUrl = (String) obj.getTag(R.string.str_image_url);
		showCommon(imageUrl);
	}
	
	private void showCommon(String imageUrl){
		Bitmap bitmap = imageLoader.get(imageUrl);
		showDialog(bitmap);		
	}
	
	//显示下一张图
	private void showNextImage(){
		currentIndex++;
		if(currentIndex>=lst.size()){
			hide();
			return;
		}
		ImageView obj=lst.get(getCurrent());
		String imageUrl = (String) obj.getTag(R.string.str_image_url);
		showCommon(imageUrl);
	}
	
	 

	 
	public void init(int currentIndex,List<ImageView> lst,int width,int height){
		this.currentIndex=currentIndex;
		this.lst=lst;
		this.width=width;
		this.height=height;
	}
	private Bitmap getBitmap(Bitmap bitmap,int scrollViewWidth,int scrollViewHeight){
		int width=bitmap.getWidth();
		int height=bitmap.getHeight();
		float scale=getScale(bitmap,scrollViewWidth,scrollViewHeight);
		if(scale!=1f){
			int _scaledWidth= (int)(width*scale);
			int _scaleHeight=(int)( height*scale);
			_scaleHeight=_scaleHeight>scrollViewHeight?(scrollViewHeight-50):_scaleHeight;
	        bitmap=Bitmap.createScaledBitmap(bitmap,_scaledWidth,_scaleHeight, true);
		}		
		return bitmap;
	}
	private float getScale(Bitmap bitmap,int scrollViewWidth,int scrollViewHeight){
		int width=bitmap.getWidth();
		int height=bitmap.getHeight();
		float  calculateWidth=scrollViewWidth>scrollViewHeight?scrollViewHeight:scrollViewWidth;//屏幕宽度
		float  _temp_width=scrollViewWidth>scrollViewHeight?height:width;//图片宽度
		float half=(int)(calculateWidth/1.2);
		return _temp_width>half?(_temp_width/half):(half/_temp_width);
	}

	
	private int getCurrent(){
		return currentIndex<0?0:currentIndex>=lst.size()?currentIndex-1:currentIndex;
	}
	
	private String getCurrentUrl(){
		ImageView obj=getImageView();
		String imageUrl = (String) obj.getTag(R.string.str_image_url);
		return imageUrl;
	}
	private ImageView getImageView(){
		int _temp_current=currentIndex>1?currentIndex-1:currentIndex;
		return lst.get(_temp_current);
	}
	

	
	public void showDialog(Bitmap bitmap) {
		if(isShowing()){
			hide();
		}
		LinearLayout convertView;
		if(MContent.T_TYPE.equals("GIF")){
			convertView=(LinearLayout) inflater.inflate(R.layout.lay_image_dialog_gif, null);
			MGifView imageView = (MGifView) convertView.findViewById(R.id.bg_image_dialog);
			float scale=getScale(bitmap,width,height);
			imageView.setMovieFilename(MUtils.getImagePath(getCurrentUrl()));
			imageView.setMScale(scale);
			Log.e("GIF",scale+"");
		}else{
			Bitmap nBitmap=getBitmap(bitmap,width,height);
			convertView=(LinearLayout) inflater.inflate(R.layout.lay_image_dialog, null);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.bg_image_dialog);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(nBitmap.getWidth(), nBitmap.getHeight());
			params.topMargin=-20;
			imageView.setImageBitmap(nBitmap);
			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			setTitle(R.string.str_dialog_title);

			
		}
		ImageView delView=(ImageView) convertView.findViewById(R.id.lay_del);
		delView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideImage();
			}
		});
		
		RelativeLayout adLayout=(RelativeLayout)convertView.findViewById(R.id.AdLayout);
		DiyBanner banner = new DiyBanner(getContext(), DiyAdSize.SIZE_MATCH_SCREENx32);
		adLayout.addView(banner);
		setContentView(convertView);
		show();
		
	}
	
	private void hideImage(){
		ImageView obj=getImageView();
		String imageUrl = (String) obj.getTag(R.string.str_image_url);
		MDb.create(getContext()).execSql(String.format(MContent.SQL_HIDE, imageUrl));
		MListData.getInstance().remove(obj);
		MToast.show(getContext(), getContext().getResources().getString(R.string.str_ok_del));
		showNextImage();
	}
//	public void showDialog(Bitmap bitmap) {
//		if(isShowing()){
//			hide();
//		}
//	
//			Bitmap nBitmap=getBitmap(bitmap,width,height);
//			
//			LinearLayout layout=(LinearLayout) inflater.inflate(R.layout.lay_image_dialog, null);
//			ImageView imageView = (ImageView) layout.findViewById(R.id.bg_image_dialog);
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(nBitmap.getWidth(), nBitmap.getHeight());
//			params.topMargin=-20;
//			imageView.setImageBitmap(nBitmap);
//			imageView.setLayoutParams(params);
//			setTitle(R.string.str_dialog_title);
//
//			ImageView delView=(ImageView) layout.findViewById(R.id.lay_del);
//			delView.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					String imageUrl=getCurrentUrl();
//					MDb.create(getContext()).execSql(String.format(MContent.SQL_HIDE, imageUrl));
//					showNextImage();
//				}
//			});
//		setContentView(layout);
//		show();
//	}	
}
