//package com.hlz.meizi.utils;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Debug;
//import android.os.Environment;
//import android.util.Log;
//
//import com.azero.core.MDb;
//import com.azero.core.MHttp;
//import com.azero.db.utils.MCnt;
//import com.azero.db.utils.MCnt.MEnum;
//import com.azero.db.utils.Selector;
//import com.azero.http.AjaxCallBack;
//import com.azero.http.AjaxParams;
//import com.azero.log.MLog;
//import com.azero.res.MPreferences;
//import com.azero.utils.MCheck;
//import com.azero.utils.MDate;
//import com.azero.utils.MToast;
//import com.azero.utils.MTools;
//import com.hlz.meizi.activitys.R;
//import com.hlz.meizi.entity.Feedback;
//import com.hlz.meizi.entity.Info;
//import com.hlz.meizi.service.StartDownDatasService;
//import com.hlz.meizi.service.StartLoadDatasService;
//
//public class CopyOfMUtils {
//	private static MHttp http = new MHttp();
//
//	// 发送基础数据上传
//	public static void sendBaseInfo(Context context) {
//		AjaxParams params = new AjaxParams();
//		params.put(MContent.T_PHONE,MPreferences.getString(context, MContent.T_PHONE));
//		params.put(MContent.T_MAC,MPreferences.getString(context, MContent.T_MAC));
//		params.put(MContent.T_IMSI,MPreferences.getString(context, MContent.T_IMSI));
//		params.put(MContent.T_PROVIDERSNAME,MPreferences.getString(context, MContent.T_PROVIDERSNAME));
//		params.put(MContent.T_SDK,MPreferences.getString(context, MContent.T_SDK));
//		params.put(MContent.T_SDK_NAME,MPreferences.getString(context, MContent.T_SDK_NAME));
//		params.put("token", MContent.T_TOKEN);
//		http.post(MContent.T_URL_BASE_INFO, params);
//	}
//
//	// 用户反馈上传
//	public static void sendFeedBack(final Context context,final Feedback feedBack, final boolean isShowMsg) {
//
//		AjaxParams params = new AjaxParams();
//		params.put(MContent.T_FEEDBACK_CONTENT, feedBack.getContent());
//		params.put(MContent.T_FEEDBACK_QQINFO, feedBack.getContact());
//		params.put(MContent.T_FEEDBACK_UID, feedBack.getUid());
//		params.put(MContent.T_ADDTIME, feedBack.getAddTime());
//		params.put(MContent.T_IMSI, MPreferences.getString(context, MContent.T_IMSI));
//		params.put(MContent.T_MAC, MPreferences.getString(context, MContent.T_MAC));
//		params.put(MContent.T_FEEDBACK_TOKEN, MContent.T_TOKEN);
//		params.put(MContent.T_MARKID, feedBack.getMarkid());
//		
//		http.post(MContent.T_URL_FEEDBACK, params,new AjaxCallBack<Object>() {
//
//					@Override
//					public void onStart() {
//						MToast.show(context, R.string.str_message_sending);
//					}
//
//					@Override
//					public void onSuccess(Object t) {
//						// ----------更新已发送
//						feedBack.setIsSend(1);// 已发送
//						MDb.create(context).update(feedBack,"addTime='" + feedBack.getAddTime() + "'");
//						if (isShowMsg) {
//							MToast.show(context,context.getResources().getString(R.string.str_message_feedback_success));
//						}
//					}
//
//					@Override
//					public void onFailure(Throwable t, int errorNo,	String strMsg) {
//						if (isShowMsg) {
//							MToast.show(context,context.getResources().getString(R.string.str_messae_feedback_fails));
//						}
//					}
//				});
//	}
//
//	// 检查未发送成功的进行发送
//	public static void sendFeedBackCheckNoSend(final Context context) {
//		List<Feedback> list = MDb.create(context).findAllByWhere(Feedback.class, " isSend=0 ");
//		for (Feedback obj : list) {
//			sendFeedBack(context, obj, false);
//		}
//	}
//
//	// 获取管理员是否回复反馈
//	public static void getFeedBack(final Context context) {
//		AjaxParams params = new AjaxParams();
//		params.put(MContent.T_MAC,MPreferences.getString(context, MContent.T_MAC));
//		params.put(MContent.T_IMSI,MPreferences.getString(context, MContent.T_IMSI));
//		params.put("token", MContent.T_TOKEN);
//		http.get(MContent.T_URL_SEND, params,new AjaxCallBack<Object>() {
//					@Override
//					public void onSuccess(Object t) {
//						//更新服务数据
//						if(MCheck.isEmpty(t)){
//							return;
//						}		
//						try{
//							JSONObject jsonObj=new JSONObject(t.toString());
//							int upNum=jsonObj.getInt("num");//更新的条数
//							if(upNum<1){
//								return;
//							}
//							String datas=jsonObj.getString("datas");
//							if(MCheck.isEmpty(datas)){
//								return;
//							}
//							JSONArray jsonArr=new JSONArray(datas);
//							int jsonSize=jsonArr.length();
//							JSONObject obj=null;
//							for(int i=0;i<jsonSize;i++){
//								obj=jsonArr.getJSONObject(i);
//								Feedback back=new Feedback();
//								back.setReplayName(obj.getString("replayName"));
//								back.setReplayContent(obj.getString("replayContent"));
//								back.setReplayTime(obj.getString("replayTime"));
//								MDb.create(context).update(back, "markId='"+obj.getString("markId")+"'");
//							}
//						}catch(Exception e){
//							MLog.me().e(e);
//						}
//					}
//				});
//	}
//	
//	// 从本地抓取数据
//	//有点乱吧,loadNews:是加载isNew=0,loadOld:加载非isNew=0数据,只在loadMoreImages处使用
//	private static List<Info> getLocalDatas(final Context context,boolean loadNews,boolean loadOld) {
//		
//		MCnt me=MCnt.me();
//		String imageType=MContent.T_TYPE;
//		String local_token=MPreferences.getString(context, MContent.T_LOCAL_DATAS_TOKEN);
//		Selector sqlBuilder = Selector.from(Info.class);
//		me.first_eq("status", 0);
//		MLog.me().e("当前加载【"+imageType+"】");
//		if(MCheck.isEmpty(imageType)||imageType.equals(MContent.T_TYPE_ALL)||imageType.equals("ALL")){
//			
//		}else if(imageType.equals(MContent.T_TYPE_YW)){//尤物/诱惑
//			me.and_in("type","'尤物','诱惑'");
//		}else if(imageType.equals(MContent.T_TYPE_QC)){//清纯美女/自拍
//			me.and_in("type","'清纯美女','萌','小清新','自拍'");
//		}else if(imageType.equals(MContent.T_TYPE_MT)){//T_TYPE_MT
//			me.and_in("type","'美腿','美臀'");
//		}else if(imageType.equals(MContent.T_TYPE_DX)){//大胸
//			me.and_eq("type", "巨乳");
//		}else{
//			me.and_eq("type", imageType);
//		}
//		
//		
//		if(loadNews){
//			me.and_eq("isNews", 0);
//		}else{
//			if(loadOld){
//				me.and("isNews", MEnum.NOT_EQ, 0);
//			}else{
//				me.and("token", MEnum.NOT_EQ, local_token).and("isNews", MEnum.NOT_EQ, 0);
//			}
//			me.and_eq("isDown", "1");//读取已下载的
//		}
//		String whereS=me.toString();
//		MLog.me().e("【EYS】"+whereS);
//		sqlBuilder.whereStr(whereS);
//		sqlBuilder.orderBy("loadNums").limit(MContent.T_PAGE_SIZE);
//		MLog.me().e("【RUN SQL->】"+sqlBuilder);
//		List<Info> lst = MDb.create(context).findBySql(Info.class,sqlBuilder.toString());
//		if(MCheck.isEmpty(lst)){
//			return lst;
//		}
//		StringBuffer sb=new StringBuffer();
//		for (Info obj : lst) {
//			sb.append(obj.getId()).append(",");
//		}
//		if(sb.length()>0){
//			String where=sb.toString();
//			if(where.lastIndexOf(",")>0){
//				where=where.substring(0, where.lastIndexOf(","));
//			}
//			String upSql=null;
//			if(loadNews){
//				//upSql="update table_info set loadNums=loadNums+1 ,isNews=1 where id in ("+where+")";
//				upSql=String.format(MContent.UPDATE_BY_LOCAL, where);
//			}else{
//				//upSql="update table_info set loadNums=loadNums+1 ,isNews=1 ,token='"+local_token+"' where id in ("+where+")";
//				upSql=String.format(MContent.UPDATE_BY_LOCAL_2, local_token,where);
//			}
//			MDb.create(context).execSql(upSql);			
//		}
//		//下载
//		downloadImage(context);
//		return lst;
//	}
//
//	public static List<Info>getLocalDatasNews(final Context context){
//		return getLocalDatas(context,true,false);
//	}
//	
//	public static List<Info>getLocalDatasOlds(final Context context){
//		return getLocalDatas(context,false,false);
//	}
//	
//	public static List<Info> getLocalDatasOldsNoNews(final Context context){
//		return getLocalDatas(context, false, true);
//	}
//	//此方法主要负责从远程下载数据
//	public static void getRemoteDatas(final Context context) {
//		final String imageType=MContent.T_TYPE;
//		//由于XML支付不了”/“斜线,所以在此需要转换
//		final String imageTypeXml=imageType.replace("/", "-");
//		final AjaxParams params = new AjaxParams();
//		final String serId=MPreferences.getString(context, MContent.T_LAST_SERVICE_ID+imageTypeXml,"");
//		String serviceTime=MPreferences.getString(context, MContent.T_SERVICE_TIME+imageTypeXml,"");
//		serviceTime=MCheck.isEmpty(serviceTime)?MContent.T_DEFAULT_TIME:serviceTime;
//		
//		params.put(MContent.T_OTHER_TYPE, imageType);
//		params.put(MContent.T_ADDTIME,serviceTime);
//		params.put(MContent.T_PAGESIZE, MContent.T_SERVICE_PAGE_SIZE+"");
//		params.put(MContent.T_FEEDBACK_TOKEN, MContent.T_TOKEN);
//		params.put(MContent.T_SERVICE_ID, serId);
//		//试试采用创建新线程启动
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				http.get(MContent.T_URL_REMOTE, params,new AjaxCallBack<Object>() {
//					@Override
//					public void onSuccess(Object t) {
//						//更新最后下拉数据
//						MPreferences.putString(context, MContent.T_LAST_TIME, MDate.get_md_hm());
//						//更新服务数据
//						if(MCheck.isEmpty(t)||t.equals("null")){
//							return;
//						}
//						try {
//							Log.e("getRemoteDatas ","成功抓取【"+imageType+"】");
//							JSONArray json=new JSONArray(t.toString());
//							int jsonSize=json.length();
//							int countSize=jsonSize-1;
//							int maxId=MCheck.isEmpty(serId)?0:Integer.parseInt(serId);
//							JSONObject obj=null;
//							List<Info> saveValues=new ArrayList<Info>();
//							Info info=null;
//							for(int i=0;i<jsonSize;i++){
//								obj=json.getJSONObject(i);
//								int serviceId=obj.getInt("id");
//								boolean bool=MDb.create(context).checkIsExist(Info.class, "serviceId='"+serviceId+"'");
//								if(!bool){
//									maxId=maxId>serviceId?maxId:serviceId;
//									info=new Info();
//									info.setImageUrl(MContent.T_DOWN_PATH+obj.getString("filename"));
//									info.setServiceId(serviceId);
//									info.setServiceAddTime(obj.getString("create_time"));
//									info.setType(obj.getString("from_name"));
//									info.setToken("");
//									saveValues.add(info);
//									if(i==countSize){
//										MPreferences.putString(context, MContent.T_SERVICE_TIME+imageTypeXml, obj.getString("create_time"));//更新加载时间表
//										MPreferences.putString(context, MContent.T_LAST_SERVICE_ID+imageTypeXml,String.valueOf(maxId));
//									}
//								}
//							}
//							if(saveValues.size()>0){
//								MDb.create(context).insertBatch(info,saveValues);
//								CopyOfMUtils.downloadImage(context);//开始下载 
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//							MLog.me().e(e);
//						}	
//					}
//				});
//			}
//		}).start();
//	}
//	//启动service下载数据
//	public static void startServiceLoads(Context context){
//		context.startService(new Intent(context,StartLoadDatasService.class));
//	}
//	public static void startServiceDown(Context context){
//		context.startService(new Intent(context,StartDownDatasService.class));
//	}
//	
//	//更新Token
//	public static void refreshToken(Context context){
//		MPreferences.putString(context, MContent.T_LOCAL_DATAS_TOKEN,MTools.MD5(System.currentTimeMillis()+""));
//	}
//	
//	
//	public static void displayBriefMemory() { 
//		Log.e("内存数据---->","NativeHeapSizeTotal:"+(Debug.getNativeHeapSize()>>10));
//		Log.e("内存数据---->","NativeAllocatedHeapSize:"+(Debug.getNativeHeapAllocatedSize()>>10));
//		Log.e("内存数据---->","NativeAllocatedFree:"+(Debug.getNativeHeapFreeSize()>>10));
//	} 
//	
//	
//	//启动下载
//	public static void downloadImage(final Context context) {
//		//首先查询
//		List<Info> lst=MDb.create(context).findBySql(Info.class, MContent.UPDATE_SELECT_DOWN);
//		for(final Info obj:lst){
//			final String imageUrl=obj.getImageUrl();
//			final String imagePath=getImagePath(imageUrl);
//			File imageFile = new File(imagePath);
//			if(imageFile.exists()){
//				obj.setIsDown(1);
//				MDb.create(context).update(obj);
//			}else{
//				http.download(imageUrl, null, imagePath, true,new AjaxCallBack<File>() {
//					@Override
//					public void onSuccess(File t) {
//						if (t.exists()) {
//							Log.e("downloadImage",t.getPath());
//							//下载完后进行压缩?
//							obj.setIsDown(1);//更新状态
//							MDb.create(context).update(obj);							
//						}
//					}
//					@Override
//					public void onFailure(Throwable t, int errorNo,String strMsg) {
//						MLog.me().e("下载失败,错误原因:" + strMsg);
//					}
//				});
//			}
//		}
//	}
//
//	public static String getImagePath(String imageUrl) {
//		int lastSlashIndex = imageUrl.lastIndexOf("/");
//		String imageName = imageUrl.substring(lastSlashIndex + 1);
//		File file = new File(Environment.getExternalStorageDirectory(), MContent.T_SD_FILENAME);
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		file=new File(file,imageName);
//		return file.getPath();
//	}
//	
//	//定时任务
//	public static void startAlarm(Context context){
//		AlarmManager am=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//		Intent intent=new Intent(MContent.T_ALERT_RECEIVE);
//		/**
//		 * Flags为PendingIntent.FLAG_CANCEL_CURRENT，则只有最后一次PendingIntent有效，之前的都无效了。
//		 */
//		PendingIntent sender=PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		am.setRepeating(AlarmManager.RTC_WAKEUP, MContent.T_FIVE_MINUTE, MContent.T_ONE_HOUR, sender);//5分钟后执行
//		
//	}
//}
