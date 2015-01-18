package com.hlz.meizi.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.youmi.android.diy.banner.DiyAdSize;
import net.youmi.android.diy.banner.DiyBanner;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azero.annotation.ViewInject;
import com.azero.base.MInject;
import com.azero.core.MDb;
import com.azero.db.sqlite.MRecord;
import com.azero.utils.MToast;
import com.hlz.meizi.activitys.MainActivity;
import com.hlz.meizi.activitys.R;
import com.hlz.meizi.utils.MContent;

public class MenuFragment extends Fragment {

	@ViewInject(id = R.id.left_theme, click = "changeTheme")
	private TextView theme;
	
	
	@ViewInject(id=R.id.left_all,click="changeType")
	private TextView all;//应用推荐
	
	@ViewInject(id=R.id.left_xgmn,click="changeType")
	private TextView xgmm;//性感美女
	
	@ViewInject(id=R.id.left_jr,click="changeType")
	private TextView jr;//大胸
	
	@ViewInject(id=R.id.left_gif,click="changeType")
	private TextView gif;//GIF
	
	@ViewInject(id=R.id.left_xqx,click="changeType")
	private TextView xqx;//小清新/自拍	
	
	@ViewInject(id=R.id.left_yw,click="changeType")
	private TextView yw;//尤物/诱惑	

	@ViewInject(id=R.id.left_mt,click="changeType")
	private TextView mt;//美腿/美臀


	@ViewInject(id=R.id.left_tj,click="tuijian")
	private TextView tj;//应用推荐

	private Context context;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		this.context=getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view= inflater.inflate(R.layout.lay_left, container,false);
		//广告
		RelativeLayout adLayout=(RelativeLayout)view.findViewById(R.id.AdLayout);
		DiyBanner banner = new DiyBanner(getActivity(), DiyAdSize.SIZE_MATCH_SCREENx60);
		adLayout.addView(banner);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MInject.me().initFragment(this, getClass());
//		initViewTitle();
	}
	private static Map<String,Integer> typeNum=new HashMap<String,Integer>();
	public void addNum(String key,int value){
		if(typeNum.containsKey(key)){
			value+=typeNum.get(key);
		}
		typeNum.put(key, value);
	}
	
	

	private void initViewTitle() {
		List<MRecord> lst=MDb.create(context).findDbModelListBySQL(MContent.UPDATE_SELECT_TYPE);
		int allNum=0;
		for(MRecord obj:lst){
			String type =	obj.getStr("type");
			int num		 = obj.getInt("num");
			if(MContent.T_TYPE_XGMM.equals(type)){
				addNum(MContent.T_TYPE_XGMM, num);
			}else if("尤物".equals(type)||"诱惑".equals(type)){
				addNum(MContent.T_TYPE_YW,num);
			}else if("清纯美女".equals(type)||"萌".equals(type)||"小清新".equals(type)||"自拍".equals(type)){
				addNum(MContent.T_TYPE_QC, num);
			}else if("美腿".equals(type)||"美臀".equals(type)){
				addNum(MContent.T_TYPE_MT, num);
			}else if("巨乳".equals(type)){
				addNum(MContent.T_TYPE_DX, num);
			}else if("GIF".equals(type)){
				addNum(MContent.T_TYPE_GIF, num);
			}
			allNum+=num;
		}
		typeNum.put(MContent.T_TYPE_ALL, allNum);
		
		all.setText(getResources().getString(R.string.left_all,typeNum.get(MContent.T_TYPE_ALL)));
		xgmm.setText(getResources().getString(R.string.left_xgmn, typeNum.get(MContent.T_TYPE_XGMM)));
		jr.setText(getResources().getString(R.string.left_jr,typeNum.get(MContent.T_TYPE_DX)));
		gif.setText(getResources().getString(R.string.left_gif,typeNum.get(MContent.T_TYPE_GIF)));
		xqx.setText(getResources().getString(R.string.left_xqx,typeNum.get(MContent.T_TYPE_QC)));
		yw.setText(getResources().getString(R.string.left_yw,typeNum.get(MContent.T_TYPE_YW)));
		mt.setText(getResources().getString(R.string.left_mt,typeNum.get(MContent.T_TYPE_MT)));
	}

	private void changeMenuToggle(String text){
		MContent.T_TYPE=text;
		MContent.T_TITLE=text.equals(getResources().getString(R.string.left_all))?MContent.T_TITLE_OK:text;
		((MainActivity)getActivity()).getFragmentManager().beginTransaction().replace(R.id.sliding_body, new ContentFragment(),MContent.T_FRAG_CONTENT).commit();
		 ((MainActivity)context).getSlidingMenu().toggle();
	}
	
	public void changeType(View v) {
		String text=((TextView)v).getText().toString();
		changeMenuToggle(text);
	}

	// 应用推荐
	public void tuijian(View v) {
		((MainActivity)getActivity()).getFragmentManager().beginTransaction().replace(R.id.sliding_body, new AdFragment(),MContent.T_FRAG_AD).commit();
		((MainActivity)context).getSlidingMenu().toggle();
	}

	// 更改主题
	public void changeTheme(View v) {
		MToast.show(context,"更新主题~");
	}
	
}
