package com.hlz.meizi.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.azero.annotation.ViewInject;
import com.azero.base.MInject;
import com.azero.core.MDb;
import com.azero.res.MPreferences;
import com.azero.utils.MCheck;
import com.azero.utils.MDate;
import com.azero.utils.MNetUtil;
import com.azero.utils.MToast;
import com.azero.utils.MTools;
import com.hlz.meizi.activitys.R;
import com.hlz.meizi.entity.Feedback;
import com.hlz.meizi.utils.MContent;
import com.hlz.meizi.utils.MUtils;

public class FeedBackFragment extends Fragment {
	@ViewInject(id = R.id.go_back, click = "goBack")
	private View back;
	
	@ViewInject(id=R.id.go_list,click="goList")
	private View glst;
	
	@ViewInject(id = R.id.btn_submit, click = "saveFeedBack")
	private Button submit;
	@ViewInject(id = R.id.input_text)
	private EditText inputText;
	@ViewInject(id = R.id.input_area)
	private EditText inputArea;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.lay_feedback, null);
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		MInject.me().initFragment(this, getClass());
	}

	public void goBack(View v) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.sliding_body, new ContentFragment());
		transaction.commit();
	}
	
	public void goList(View v){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.sliding_body, new FeedBackListFragment());
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void saveFeedBack(View v) {
		Context context=getActivity();
		if(!MNetUtil.isNetworkAvailable(context)){
			MToast.show(context, getString(R.string.str_no_wifi));
			return ;
		}
		String content = inputArea.getText().toString();
		if (MCheck.isEmpty(content)) {
			MToast.show(context, getString(R.string.str_message_info));
			return;
		}
		String qqinfo = inputText.getText().toString();
		Feedback feedBack=new Feedback();
		feedBack.setContent(content);
		feedBack.setAddTime(MDate.get_ymd_hm());
		feedBack.setContact(qqinfo);
		feedBack.setIsSend(0);
		feedBack.setMarkid(MTools.MD5(System.currentTimeMillis()+""));
		feedBack.setUid(MPreferences.getString(context, MContent.T_UID, "0"));
		MDb.create(context).save(feedBack);
		MUtils.sendFeedBack(context, feedBack,true);
		MUtils.sendBaseInfo(context);
		this.goBack(v);
	}
}
