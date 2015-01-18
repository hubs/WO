package com.hlz.meizi.fragment;

import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.OffersWallDialogListener;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hlz.meizi.activitys.MainActivity;
import com.hlz.meizi.activitys.R;
import com.hlz.meizi.utils.MContent;

public class AdFragment extends Fragment implements OffersWallDialogListener {
	private Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		this.context=getActivity();
		//初始化
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		OffersManager.getInstance(context).showOffersWallDialog(getActivity()); 
		View v= inflater.inflate(R.layout.lay_main, null);
		return v;
	}

	@Override
	public void onDialogClose() {
		// TODO Auto-generated method stub
		Log.e("onDialogClose","【onDialogClose】");
		((MainActivity)getActivity()).getFragmentManager().beginTransaction().replace(R.id.sliding_body, new ContentFragment(),MContent.T_FRAG_CONTENT).commit();
	}
	


}
