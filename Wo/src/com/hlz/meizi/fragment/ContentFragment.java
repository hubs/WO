package com.hlz.meizi.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azero.annotation.ViewInject;
import com.azero.base.MInject;
import com.azero.res.MPreferences;
import com.azero.utils.MCheck;
import com.azero.utils.MDate;
import com.azero.utils.MNetUtil;
import com.azero.utils.MTools;
import com.azero.weiget.slidingmenu.app.SlidingActivity;
import com.hlz.meizi.activitys.R;
import com.hlz.meizi.utils.MContent;
import com.hlz.meizi.utils.MUtils;
import com.hlz.meizi.widget.MScrollView;
import com.hlz.meizi.widget.MScrollView.OnLoadDatasListener;

public class ContentFragment extends Fragment implements OnLoadDatasListener {
	private MScrollView mPullToRefreshView;
	private Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		this.context=getActivity();
		initOnece();
	}



	private void initOnece() {
		MUtils.refreshToken(context);
		MPreferences.putString(context, MContent.T_LAST_TIME, MDate.get_md_hm());		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v= inflater.inflate(R.layout.lay_main, null);
		mPullToRefreshView = (MScrollView) v.findViewById(R.id.lay_boay_main);
		init();
		return v;
	}
	//这里进行一些初始化值
	private void init() {
		Boolean isFirstLoad=MPreferences.getBoolean(context, MContent.T_FIRST_LOAD, true);
		//如果是第一次加载,则需要获取一些基本数据
		if(isFirstLoad){
//			MPreferences.putString(context, MContent.T_PHONE,MTools.getNativePhoneNumber(context));
			MPreferences.putString(context, MContent.T_MAC, MTools.getMacAddress(context));
			MPreferences.putString(context, MContent.T_IMSI, MTools.getIMSI(context));
			MPreferences.putString(context, MContent.T_PROVIDERSNAME,MTools.getProvidersName(context));
			MPreferences.putString(context, MContent.T_SDK,android.os.Build.VERSION.SDK_INT+"");
			MPreferences.putString(context, MContent.T_SDK_NAME, android.os.Build.VERSION.RELEASE);
			MPreferences.putBoolean(context, MContent.T_FIRST_LOAD,false);
			
			if(MNetUtil.isNetworkAvailable(context)){
				MUtils.sendBaseInfo(context);
			}
		}
		if(MCheck.isEmpty(MPreferences.getString(context, MContent.T_IMSI))){
			MPreferences.putBoolean(context, MContent.T_FIRST_LOAD,true);
		}
		mPullToRefreshView.setOnLoadDatasListener(this);
		MUtils.sendUserLog(context);
	}



	@Override
	public void onStart() {
		super.onStart();	
		MInject.me().initFragment(this, getClass());
		changeTitle();
		view_load.setVisibility(View.VISIBLE);
	}


	@ViewInject(id = R.id.lay_header_left, click = "showLeftMenu")
	private View btn_slide_left;

	@ViewInject(id = R.id.lay_header_right, click = "goFeedBack")
	private View btn_feedback;
	
	@ViewInject(id=R.id.lay_header_center)
	private View textTitle;
	
	@ViewInject(id=R.id.lay_loading,click="hideLoading")
	private View view_load;
	public void hideLoading(View v){
		view_load.setVisibility(View.GONE);
	}
	public void showLeftMenu(View v) {
		((SlidingActivity)getActivity()).toggle();
	}
	public void changeTitle(){
		((TextView)textTitle).setText(MContent.T_TITLE);
	}
	
	public void goFeedBack(View v) {
		FragmentTransaction transaction=getActivity().getFragmentManager().beginTransaction();
		transaction.replace(R.id.sliding_body, new FeedBackFragment(),MContent.T_FRAG_FEED);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		mPullToRefreshView.disableDialog();
	}


	@Override
	public void onLoadBefore() {
		view_load.setVisibility(View.VISIBLE);
	}



	@Override
	public void onLoadAfter() {
		view_load.setVisibility(View.GONE);
	}

}
