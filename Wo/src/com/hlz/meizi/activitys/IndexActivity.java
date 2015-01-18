package com.hlz.meizi.activitys;

import java.io.IOException;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.azero.annotation.ViewInject;
import com.azero.base.MActivity;
import com.azero.core.MDb;
import com.azero.res.MPreferences;
import com.azero.utils.MDataMoveUtils;
import com.hlz.meizi.utils.MContent;
import com.hlz.meizi.utils.MUtils;

public class IndexActivity extends MActivity {

	private AlphaAnimation start_anima;

	@ViewInject(id = R.id.welcomeGo, click = "goIndex")
	private View index;

	public void goIndex(View view) {
		redirectTo();
	}

	private void redirectTo() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	private View view;
	
	private static int[] bgImageArr=new int[]{R.drawable.bg_1,R.drawable.bg_2,R.drawable.bg_3,R.drawable.bg_4,R.drawable.bg_5,R.drawable.bg_6,R.drawable.bg_7,R.drawable.bg_8,R.drawable.bg_9};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = View.inflate(this, R.layout.lay_index, null);
		View bgView=view.findViewById(R.id.lay_index_bg);
		bgView.setBackgroundResource(bgImageArr[MUtils.getRandNum()]);
		setContentView(view);
		
		// 有米
		AdManager.getInstance(this).init("543586709c79d9c9 ","77f3cc41c3c1c9e7", false);
		OffersManager.getInstance(this).onAppLaunch();
		
		// 实例化广告条
//		AdView adView = new AdView(this, AdSize.FIT_SCREEN);

		// 获取要嵌入广告条的布局
//		LinearLayout adLayout=(LinearLayout)findViewById(R.id.AdIndexLayout);

		// 将广告条加入到布局中
//		adLayout.addView(adView);
		
//		SpotManager.getInstance(this).loadSpotAds();
		// 有米开始
		
//		if(SpotManager.getInstance(this).checkLoadComplete()){
//			SpotManager.getInstance(this).showSpotAds(this);
//		}

		boolean isFirstLoad = MPreferences.getBoolean(getApplicationContext(),
				MContent.T_FIRST_INSTRALL, true);
		initData(isFirstLoad);
		initView(getApplicationContext(), isFirstLoad);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 请务必在应用退出的时候调用以下代码，告诉SDK应用已经关闭，可以让SDK进行一些资源的释放和清理。
		OffersManager.getInstance(this).onAppExit();
//		 SpotManager.getInstance(this).unregisterSceenReceiver();
	}

	private void initData(boolean isFirstLoad) {
		int sleepTime = isFirstLoad ? 8000 : 5000;
		start_anima = new AlphaAnimation(0.2f, 1.0f);
		start_anima.setDuration(sleepTime);// 10秒
		view.startAnimation(start_anima);
		start_anima.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				redirectTo();
			}

		});
	}

	private void initView(Context context, boolean isFirst) {
		MDb.create(context, true);
		MUtils.startAlarm(context);// 启动定时器
		MUtils.startServiceLoads(context);// 启动服务加载
		initMeizi(context, isFirst);// 初始化数据
	}

	// 这些初始方法移到IndexActivity.java类中
	// 初始化Prefrence数据
	private void initProXML(Context context) {
		// 初始化时间
		MPreferences.putString(context, "service_time_ALL", "2014-08-12");
		MPreferences.putString(context, "service_time_美腿-美臀", "2014-08-13");
		MPreferences.putString(context, "service_time_大胸", "2014-08-12");
		MPreferences.putString(context, "service_time_尤物-诱惑", "2014-11-18");
		MPreferences.putString(context, "service_time_清纯美女-自拍", "2014-08-13");
		MPreferences.putString(context, "service_time_GIF", "2014-08-21");

		// 初始化ID
		MPreferences.putString(context, "last_service_id_美腿-美臀", "37906");
		MPreferences.putString(context, "last_service_id_尤物-诱惑", "116933");
		MPreferences.putString(context, "last_service_id_清纯美女-自拍", "35421");
		MPreferences.putString(context, "last_service_id_GIF", "45112");
		MPreferences.putString(context, "last_service_id_大胸", "33611");
		MPreferences.putString(context, "last_service_id_ALL", "34046");
	}

	// 打包DB放入指定目录
	private void initDb(Context context) {
		MDataMoveUtils util = new MDataMoveUtils(context,MContent.T_DB_NAME_OK, MContent.T_DB_NAME);
		util.deleteDataBase();
		// 判断数据库是否存在
		boolean dbExist = util.checkDataBase();
//		MLog.me().e("检查数据库是否存在:"+dbExist);
		if (dbExist) {
//			Log.e("DB ", "The database is exist.");
		} else {// 不存在就把raw里的数据库写入手机
			try {
				util.copyDataBase();
//				MLog.me().e("已完成数据库移动:");
				
			} catch (IOException e) {
//				MLog.me().e("Error copying database:");
				throw new Error("Error copying database");
			}
		}
	}

	// 打包Image缓存放入指定目录
	private void initImage(Context context) {
		MDataMoveUtils util = new MDataMoveUtils(context);
		try {
			util.copyImageBaseSDK(MContent.T_IMAGE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initMeizi(Context context, boolean isFirstLoad) {
		if (isFirstLoad) {
			
			initProXML(context);
//			MLog.me().e("开始初始化数据库");
			initDb(context);
//			MLog.me().e("成功初始化数据库");
			initImage(context);
//			List<MRecord> lst=MDb.create(context).findDbModelListBySQL("SELECT  * FROM sqlite_master WHERE type ='table' AND name ='table_info';");
//			MLog.me().e("查看info表字段:"+lst.size());
//			for(MRecord row:lst){
//				MLog.me().e("name="+row.getStr("name")+", and field =>"+row.getStr("sql")+"  all ="+row);
//			}
			MPreferences.putBoolean(context, MContent.T_FIRST_INSTRALL, false);
		}
		// initImageViews(context);
	}

	// 初始化加载图片
//	private void initImageViews(Context context) {
//		// 读取指定目录下的图片,放入缓存
//		/**
//		 * 1、查询数据库 2、判断是否存在(未下载的) 3、存在则写入 4、不存在则放弃,后面会处理
//		 */
//		MImageLoader imageLoader = MImageLoader.getInstance(context);
//		// 默认只加载前50条
//		Selector sqlBuilder = Selector.from(Info.class)
//				.where("status=0 and isDown=1").orderBy("loadNums").limit(30);
//		List<Info> infoList = MDb.create(context).findBySql(Info.class,
//				sqlBuilder.toString());
//		StringBuffer sb = new StringBuffer();
//		for (Info obj : infoList) {
//			String mImageUrl = obj.getImageUrl();
//			File imageFile = new File(MUtils.getImagePath(mImageUrl));
//			if (imageFile.exists()) {
//				Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
//				if (bitmap != null) {
//					sb.append(obj.getId()).append(",");
//					imageLoader.put(mImageUrl, bitmap);
//				}
//			}
//		}
//		if (sb.length() > 0) {
//			String where = sb.toString();
//			if (where.lastIndexOf(",") > 0) {
//				where = where.substring(0, where.lastIndexOf(","));
//			}
//			String upSql = String.format(MContent.UPDATE_ISDOWN, where);
//			MDb.create(context).execSql(upSql);
//		}
//	}

}
