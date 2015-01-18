package com.hlz.meizi.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.azero.annotation.ViewInject;
import com.azero.base.MInject;
import com.azero.core.MDb;
import com.hlz.meizi.activitys.R;
import com.hlz.meizi.entity.Feedback;
import com.hlz.meizi.utils.MContent;

public class FeedBackListFragment extends Fragment {
	@ViewInject(id = R.id.go_back, click = "goBack")
	private View back;
	@ViewInject(id = R.id.lay_header_right, click = "goFeedBack")
	private Button button;

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		MInject.me().initFragment(this, getClass());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.lay_feedback_list, null);
		ListView listView = (ListView) view.findViewById(R.id.lay_list_view);
		
		Cursor cursor=MDb.create(getActivity()).findAllCursorBySql(Feedback.class,MContent.SQL_FEEDLIST);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),R.layout.lay_feedback_item, cursor, new String[] {"addTime","content","replayContent" }, new int[] {R.id.lay_time, R.id.lay_content,R.id.lay_replay });
		listView.setAdapter(adapter);
		return view;
	}

	public void goBack(View v) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.sliding_body, new ContentFragment());
		transaction.commit();
	}

	public void goFeedBack(View v) {
		FragmentTransaction transaction = getActivity().getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.sliding_body, new FeedBackFragment(),
				MContent.T_FRAG_FEED);
		transaction.commit();
	}

}
