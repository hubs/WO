package com.hlz.meizi.datas;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;

public class MListData {
	// 记录所有界面上的图片,用以可以随机控制对图片的释放
	private static List<ImageView> imageViewList = new ArrayList<ImageView>();
	
	public static MListData getInstance() {
		return MListDataInstance.getSingleInstance();
	}

	private static class MListDataInstance {
		public static MListData getSingleInstance() {
			return new MListData();
		}
	}
	public  void add(ImageView view){
		if (!imageViewList.contains(view)) {
			imageViewList.add(view);	
		}
	}
	
	public  List<ImageView> getImageList(){
		return imageViewList;
	}
	public void remove(ImageView view){
		if(imageViewList.contains(view)){
			imageViewList.remove(view);
		}
	}
}
