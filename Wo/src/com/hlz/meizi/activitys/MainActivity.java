package com.hlz.meizi.activitys;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.azero.core.MAppManager;
import com.azero.utils.MToast;
import com.azero.weiget.slidingmenu.app.SlidingActivity;
import com.azero.weiget.slidingmenu.lib.SlidingMenu;
import com.hlz.meizi.fragment.ContentFragment;
import com.hlz.meizi.fragment.FeedBackFragment;
import com.hlz.meizi.fragment.MenuFragment;
import com.hlz.meizi.helper.MUpdateHelper;
import com.hlz.meizi.utils.MContent;

public class MainActivity extends SlidingActivity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lay_frame_body);
		initSlidingMenu();
		
		//检查更新
		new MUpdateHelper(getApplicationContext()).execute();
	}

	private void initSlidingMenu() {
		setBehindContentView(R.layout.lay_frame_left);// 左边菜单
		MenuFragment menuFragment = new MenuFragment();
		ContentFragment content = new ContentFragment();// 默认为全部

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.sliding_left, menuFragment, MContent.T_FRAG_MENU);
		transaction.replace(R.id.sliding_body, content,MContent.T_FEEDBACK_CONTENT);
		transaction.commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidth(50);// 设置阴影图片的宽度
		sm.setShadowDrawable(R.drawable.d_shadow);
		Point point = new Point();
		getWindowManager().getDefaultDisplay().getSize(point);
		sm.setBehindOffset(point.x / 3);// 设置菜单占屏幕的比例
		sm.setFadeDegree(0.35f);// SlidingMenu滑动时的渐变程度
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// toggle就是程序自动判断是打开还是关闭
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	private boolean doubleBackToExitPressedOnce=false;
	
	@Override
	protected void onResume() {
		super.onResume();
		this.doubleBackToExitPressedOnce = false;
	}
	
	@Override
	public void onBackPressed() {
		Fragment contentFrag=getFragmentManager().findFragmentByTag(MContent.T_FRAG_CONTENT);
		Log.e("backPress",contentFrag+","+contentFrag.getTag());
		if(contentFrag!=null){
		    if (doubleBackToExitPressedOnce) {
		        super.onBackPressed();
		        MAppManager.me().finishAllActivity();
		        return;
		    }
	
		    this.doubleBackToExitPressedOnce = true;
		    MToast.show(this, getString(R.string.str_exit));
		}
	}
	
	//
	public void showLeftMenu(View v) {
		(getSlidingMenu()).toggle();
	}
	public void goFeedBack(View v) {
		FragmentTransaction transaction=getFragmentManager().beginTransaction();
		transaction.replace(R.id.sliding_body, new FeedBackFragment(),MContent.T_FRAG_FEED);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
}
