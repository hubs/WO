package com.hlz.meizi.widget;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.azero.cache.MCache;
import com.azero.core.MDb;
import com.azero.core.MHttp;
import com.azero.http.AjaxCallBack;
import com.azero.res.MPreferences;
import com.azero.utils.MBitmapUtil;
import com.azero.utils.MCheck;
import com.azero.utils.MNetUtil;
import com.azero.utils.MToast;
import com.hlz.meizi.activitys.R;
import com.hlz.meizi.datas.MImageLoader;
import com.hlz.meizi.datas.MListData;
import com.hlz.meizi.dialog.MImageDialog;
import com.hlz.meizi.entity.Info;
import com.hlz.meizi.utils.MContent;
import com.hlz.meizi.utils.MUtils;

public class MScrollView extends LinearLayout  {

	
	
	// -----------------------------------------------------------移入数据模块开始
	// 每一列的宽度
	private int columnWidth;

	// 第一列，二列，三列宽度
	private int firstColumnHeight, secondColumnHeight, thirdColumnHeight;

	// 是否加载过一次Layout,这里初始化只需加载一次
	private boolean loadOnce;

	// 对图片进行管理的工具类
	private MImageLoader imageLoader;

	// 第一、二、三列布局
	private LinearLayout firstColumn, secondColumn, thirdColumn;

	// 记录所有正在下载或等待下载的任务
	private static Set<LoadImageTask> taskCollection;

	// 子布局
	private static ScrollView scrollLayout;
	// 布局的高度,宽度
	private static int scrollViewHeight, scrollViewWidth;
	
	private static Handler handler = new Handler() ;
	// -----------------------------------------------------------移入数据模块结束

	// 下拉,下拉状态
	private enum ORIENTATION {
		NONE, UP, DOWN
	}

	private ORIENTATION currentOrientation = ORIENTATION.NONE;

	/**
	 * last y
	 */
	private int mLastMotionY;
	/**
	 * layout inflater
	 */
	private LayoutInflater mInflater;

	private MImageDialog dialog;

	/**
	 * PULL_TO_REFRESH:下拉状态 RELEASE_TO_REFRESH:释放立即刷新状态 REFRESHING:正在刷新状态
	 * REFRESH_FINISHED:刷新完成或未刷新状态
	 */
	private enum STATUS {
		PULL_TO_REFRESH, RELEASE_TO_REFRESH, REFRESHING, REFRESH_FINISHED
	}

	/**
	 * 当前状态
	 */
	private STATUS currentStatus = STATUS.REFRESH_FINISHED;
	private STATUS currentFootStatus = STATUS.REFRESH_FINISHED;
	/**
	 * 头部View
	 */
	private View headFreshView, bottomFreshView;
	/**
	 * 下拉头部高度
	 */
	private int headFreshHeight, bottomFreshHeight;
	private TextView refreshText, bottomRefreshText;
	private TextView refreshTime;
	private ProgressBar progressBar, bottomProgressBar;
	private ImageView refreshImage, bottomRefreshImage;//
	/**
	 * 为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分
	 */
	// private int mId = -1;

	private LayoutParams headerLayoutParams;

	// private String imageType;//图片分类

	private Context context;


	private int sleepTime = 3000;//默认10秒
	private int sleepTimeGif=5000;//25秒
//	// 记录所有界面上的图片,用以可以随机控制对图片的释放
	private static List<ImageView> imageViewList=MListData.getInstance().getImageList();
	
	
	private  static MHttp http = new MHttp();
	// private View convertView;
	public MScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (imageLoader == null) {
			imageLoader = MImageLoader.getInstance(context);
		}
		if (taskCollection == null) {
			taskCollection = new HashSet<LoadImageTask>();
		}
		// 意思应该是触发移动事件的最短距离，如果小于这个距离就不触发移动控件
		setOrientation(VERTICAL);
		mInflater = LayoutInflater.from(context);
		// convertView = mInflater.inflate(R.layout.lay_image_item, null);
		this.context = context;
		addHeadView();
	}


	// ----------------------------------------------------加载头部与底部开始
	private void addHeadView() {
		headFreshView = mInflater.inflate(R.layout.lay_head_loadmore, this,false);
		progressBar = (ProgressBar) headFreshView.findViewById(R.id.lay_progress);
		refreshImage = (ImageView) headFreshView.findViewById(R.id.lay_pull_to_refresh_image);
		refreshText = (TextView) headFreshView.findViewById(R.id.lay_pull_to_refresh_text);
		refreshTime = (TextView) headFreshView.findViewById(R.id.lay_pull_to_refresh_time);

		// 下面计算这些可以移去onLyaout中去直接取
		measureView(headFreshView);
		headFreshHeight = headFreshView.getMeasuredHeight();
		headerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,headFreshHeight);
		// 设置topMargin的值为负的header View高度,即将其隐藏在最上方
		headerLayoutParams.topMargin = -(headFreshHeight);
		addView(headFreshView, headerLayoutParams);
	}

	// 计算高度
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	private void addFootView() {
		bottomFreshView = mInflater.inflate(R.layout.lay_footer_loadmore, this,false);
		bottomProgressBar = (ProgressBar) bottomFreshView.findViewById(R.id.lay_progress);
		bottomRefreshImage = (ImageView) bottomFreshView.findViewById(R.id.lay_pull_to_refresh_image);
		bottomRefreshText = (TextView) bottomFreshView.findViewById(R.id.lay_pull_to_refresh_text);

		measureView(bottomFreshView);
		bottomFreshHeight = bottomFreshView.getMeasuredHeight();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,bottomFreshHeight);
		// 由于是线性布局可以直接添加,只要AdapterView的高度是MATCH_PARENT,那么footer view就会被添加到最后,并隐藏
		addView(bottomFreshView, params);
	}

	// footer view 在此添加保证添加到linearlayout中的最后
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		addFootView();
	}

	// ----------------------------------------------------加载头部与底部开始

	// ----------------------------------------------------处理完执行开始
	/**
	 * header view 完成更新后恢复初始状态 头部加载完后运行此方法
	 */
	public void onHeaderRefreshComplete() {
		setHeaderTopMargin(-headFreshHeight);
		refreshImage.setVisibility(View.VISIBLE);
		refreshImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
		refreshText.setText(R.string.str_pull_to_refresh_release_label);
		refreshTime.setText(getUpdateLastTimeStr());
		progressBar.setVisibility(View.GONE);
		currentStatus = STATUS.PULL_TO_REFRESH;
	}

	private void noFocus() {
		scrollLayout.setPressed(false);
		scrollLayout.setFocusable(false);
		scrollLayout.setFocusableInTouchMode(false);
	}

	/**
	 * footer view 完成更新后恢复初始状态
	 */
	public void onFooterRefreshComplete() {
		setHeaderTopMargin(-headFreshHeight);
		bottomRefreshImage.setVisibility(View.VISIBLE);
		bottomRefreshImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
		bottomRefreshText.setText(R.string.str_pullup_to_refresh_pull_label);
		bottomProgressBar.setVisibility(View.GONE);
		currentFootStatus = STATUS.PULL_TO_REFRESH;
	}

	/**
	 * 设置header view 的topMargin的值
	 * 
	 * @param topMargin
	 *            ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
	 */
	private void setHeaderTopMargin(int topMargin) {
		headerLayoutParams.topMargin = topMargin;
		headFreshView.setLayoutParams(headerLayoutParams);
		invalidate();
	}

	// ----------------------------------------------------处理完执行结束

	// 页面布局初始化
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed && !loadOnce) {
			scrollLayout = (ScrollView) getChildAt(1);
			// 当前屏幕宽、高
			scrollViewWidth = scrollLayout.getWidth();
			scrollViewHeight = scrollLayout.getHeight();
			firstColumn = (LinearLayout) scrollLayout.findViewById(R.id.first_column);
			secondColumn = (LinearLayout) scrollLayout.findViewById(R.id.second_column);
			thirdColumn = (LinearLayout) scrollLayout.findViewById(R.id.third_column);
			columnWidth = firstColumn.getWidth();
			loadOnce = true;
			loadMoreImages();
			MCache.me(context).setBitMaxWidth(columnWidth);
		}
	}



	// ---------------------------------------------------拦截开始
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int y = (int) e.getRawY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 首先拦截down事件,记录y坐标
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			// deltaY > 0 是向下运动,< 0是向上运动
			int deltaY = y - mLastMotionY;
			if (isRefreshViewScroll(deltaY)) {
				// 返回值为true时事件会传递给当前控件的onTouchEvent()，而不在传递给子控件
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}

	/**
	 * 是否应该到了父View,即PullToRefreshView滑动
	 * 
	 * @param deltaY
	 *            deltaY > 0 是向下运动,< 0是向上运动
	 */
	private boolean isRefreshViewScroll(int deltaY) {
		if (currentStatus == STATUS.REFRESHING|| currentFootStatus == STATUS.REFRESHING) {
			return false;
		}

		// 对于ScrollView
		if (scrollLayout != null) {
			View child = scrollLayout.getChildAt(0);// 第一个是LinnLayout
			if (deltaY > 0 && scrollLayout.getScrollY() == 0) {
				currentOrientation = ORIENTATION.DOWN;
				return true;
			} else if (deltaY < 0&& child.getMeasuredHeight() <= getHeight()+ scrollLayout.getScrollY()) {
				currentOrientation = ORIENTATION.UP;
				return true;
			}
		}

		return false;
	}

	// ---------------------------------------------------拦截结束

	// ---------------------------------------------------触摸开始
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// onInterceptTouchEvent已经记录
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaY = y - mLastMotionY;
			if (currentOrientation == ORIENTATION.DOWN) {// 执行下拉
				headerPrepareToRefresh(deltaY);
			} else if (currentOrientation == ORIENTATION.UP) {// 执行上拉
				footerPrepareToRefresh(deltaY);
			}
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			// 如果不行，在这里进行检测
			int topMargin = getHeaderTopMargin();
			if (currentOrientation == ORIENTATION.DOWN) {
				if (topMargin >= 0) {
					// 开始刷新
					headerRefreshing();
				} else {
					// 还没有执行刷新，重新隐藏
					setHeaderTopMargin(-headFreshHeight);
				}
			} else if (currentOrientation == ORIENTATION.UP) {
				if (Math.abs(topMargin) >= headFreshHeight + bottomFreshHeight) {
					// 开始执行footer 刷新
					footerRefreshing();
				} else {
					// 还没有执行刷新，重新隐藏
					setHeaderTopMargin(-headFreshHeight);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	// ---------------------------------------------头部开始

	// 修改Header view top margin的值
	private int changingHeaderViewTopMargin(int deltaY) {
		float newTopMargin = headerLayoutParams.topMargin + deltaY * 0.3f;// 0.3它的三分之一,看起来有拉力感

		// 这里对上拉做一下限制,因为当前上拉后然后不释放手指直接下拉,会把下拉刷新给触发了,感谢网友yufengzungzhe的指出
		// 表示如果是在上拉后一段距离,然后直接下拉
		if (deltaY > 0 && currentOrientation == ORIENTATION.UP&& Math.abs(headerLayoutParams.topMargin) <= headFreshHeight) {
			return headerLayoutParams.topMargin;
		}
		// 同样地,对下拉做一下限制,避免出现跟上拉操作时一样的bug
		if (deltaY < 0 && currentOrientation == ORIENTATION.DOWN&& Math.abs(headerLayoutParams.topMargin) >= headFreshHeight) {
			return headerLayoutParams.topMargin;
		}
		headerLayoutParams.topMargin = (int) newTopMargin;
		headFreshView.setLayoutParams(headerLayoutParams);
		invalidate();
		return headerLayoutParams.topMargin;
	}

	// 执行头部下拉操作
	private void headerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		// 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
		if (newTopMargin >= 0 && currentStatus != STATUS.RELEASE_TO_REFRESH) {
			refreshText.setText(R.string.str_pull_to_refresh_release_label);
			refreshTime.setVisibility(View.VISIBLE);
			refreshTime.setText(getUpdateLastTimeStr());
			refreshImage.setImageResource(R.drawable.pulltorefresh_up_arrow);// ??
			currentStatus = STATUS.RELEASE_TO_REFRESH;
		} else if (newTopMargin < 0 && newTopMargin > -headFreshHeight) {// 拖动时没有释放
			refreshText.setText(R.string.str_pulldown_to_refresh_pull_label);
			refreshImage.setImageResource(R.drawable.pulltorefresh_down_arrow);// ??
			currentStatus = STATUS.PULL_TO_REFRESH;
		}
	}

	// 获取当前header view 的topMargin
	private int getHeaderTopMargin() {
		return headerLayoutParams.topMargin;
	}

	// 正在刷新状态
	private void headerRefreshing() {
		currentStatus = STATUS.REFRESHING;
		setHeaderTopMargin(0);
		refreshImage.setVisibility(View.GONE);
		refreshTime.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		refreshText.setText(R.string.str_pull_to_refresh_refreshing_label);
		// 判断是否能连上网
		if (!MNetUtil.isNetworkAvailable(context)) {
			MToast.show(context, context.getString(R.string.str_no_wifi));
			return;
		}
		new LoadHeaderTask().execute();
	}

	// ----------------------------------头部操作结束

	// ----------------------------------底部操作开始
	/**
	 * footer 准备刷新,手指移动过程,还没有释放 移动footer view高度同样和移动header view
	 * 高度是一样，都是通过修改header view的topmargin的值来达到
	 * 
	 * @param deltaY
	 *            ,手指滑动的距离
	 */
	private void footerPrepareToRefresh(int deltaY) {
		int newTopMargin = changingHeaderViewTopMargin(deltaY);
		// 如果header view topMargin 的绝对值大于或等于header + footer 的高度
		// 说明footer view 完全显示出来了，修改footer view 的提示状态
		if (Math.abs(newTopMargin) >= (headFreshHeight + bottomFreshHeight)&& currentFootStatus != STATUS.RELEASE_TO_REFRESH) {
			bottomRefreshText.setText(R.string.str_pullup_to_refresh_release_label);
			bottomRefreshImage.setVisibility(View.VISIBLE);
			bottomRefreshImage.setImageResource(R.drawable.pulltorefresh_down_arrow);
			currentFootStatus = STATUS.RELEASE_TO_REFRESH;
		} else if (Math.abs(newTopMargin) < (headFreshHeight + bottomFreshHeight)) {
			bottomRefreshText.setText(R.string.str_pullup_to_refresh_pull_label);
			bottomRefreshImage.setVisibility(View.VISIBLE);
			bottomRefreshImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
			currentFootStatus = STATUS.PULL_TO_REFRESH;
		}
	}

	// 正在刷新状态
	private void footerRefreshing() {
		currentFootStatus = STATUS.REFRESHING;
		int top = headFreshHeight + bottomFreshHeight;
		setHeaderTopMargin(-top);

		bottomRefreshImage.setVisibility(View.GONE);
		bottomProgressBar.setVisibility(View.VISIBLE);
		bottomRefreshText.setText(R.string.str_pull_to_refresh_updating_label);

		// 判断是否能连上网
		if (!MNetUtil.isNetworkAvailable(context)) {
			MToast.show(context, context.getString(R.string.str_no_wifi));
//			return;
		}
		new LoadFooterTask().execute();
	}

	// ----------------------------------底部操作结束

	// ---------------------------------接口开始
	private OnLoadDatasListener onLoadDatas;

	public interface OnLoadDatasListener {
		public void onLoadBefore();

		public void onLoadAfter();
	}

	public void setOnLoadDatasListener(OnLoadDatasListener onLoadDatas) {
		this.onLoadDatas = onLoadDatas;
	}

	// ---------------------------------接口结束

	private String getUpdateLastTimeStr() {
		return "上次刷新: " + MPreferences.getString(context, MContent.T_LAST_TIME);
	}

	// --------------------------------------------------------这些需要移到数据模块中去------------开始

	// 开始加载下一页的图片，每张图片都会开启一个异步线程去下载。
	private void loadMoreImages() {
		if (hasSDCard()) {
			if (onLoadDatas != null) {
				onLoadDatas.onLoadBefore();
			}
			// 判断是否能连上网
			if (!MNetUtil.isNetworkAvailable(context)) {
				MToast.show(context, context.getString(R.string.str_no_wifi));
			}
			
			List<Info> dataLst=MUtils.getLocalDatasNoToken(context);
			if(MCheck.isEmpty(dataLst)){
				dataLst= MUtils.getLocalDatasToken(context);
			}
			int currentSize=dataLst.size();
			if(currentSize<MContent.T_PAGE_SIZE){
				List<Info> ndataLst= MUtils.getLocalDatasToken(context);
				dataLst.addAll(ndataLst);
			}
			if (MCheck.isEmpty(dataLst)) {
				MUtils.startServiceLoads(context);
				MToast.show(context, context.getString(R.string.str_no_datas));
				onLoadDatas.onLoadAfter();
				//如果没有，则跳到首页面
				dataLst=MUtils.getLocalDatasByType(context,MContent.T_TYPE_ALL);
				foreachDatas(dataLst);
				return;
			} 
			
			foreachDatas(dataLst);
			int sleep=MContent.T_TYPE.equals("GIF")?sleepTimeGif:sleepTime;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// 如果没有数据,再尝试加载一下
//					if (firstColumn.getChildCount() < 1) {
//						List<Info> dataLst = MUtils.getLocalDatasOldsNoNews(context);
//						foreachDatas(dataLst);
//					}
					if (onLoadDatas != null) {
						onLoadDatas.onLoadAfter();
					}
				}
			}, sleep);
		} else {
			MToast.show(context, context.getString(R.string.str_no_find_sd));
		}
	}

	private static Executor cacheExecutor=Executors.newFixedThreadPool(40);


	private void foreachDatas(List<Info> dataLst) {
		for (Info obj : dataLst) {
			LoadImageTask task = new LoadImageTask();
			taskCollection.add(task);
			
		    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		        task.executeOnExecutor(cacheExecutor, obj.getImageUrl());
		     }else {
		    	 task.execute(obj.getImageUrl());
		     }
		}
	}

	private boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	class LoadImageTask extends AsyncTask<String, Void, Boolean> {

		// 图片的URL地址
		private String mImageUrl;

		// 可重复使用的ImageView
		private ImageView mImageView;

		public LoadImageTask() {
		}

		public LoadImageTask(ImageView imageView) {
			this.mImageView = imageView;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			mImageUrl = params[0];
			Bitmap imageBitmap = imageLoader.get(mImageUrl);
			if(imageBitmap!=null){
				return true;
			}

			//不存在,则先读取文件
			File imageFile = new File(MUtils.getImagePath(mImageUrl));
			if (imageFile.exists()) {//存在,则读取写入
				if(imageFile.length()<1){//可能由于上一次没写完,只创建了
					 downloadImage(mImageUrl);//重新下载
				}
				Bitmap bitmap=MUtils.getBitmap(imageFile, columnWidth);
			    if (bitmap != null) {
					imageLoader.put(mImageUrl, bitmap);
					if(bitmap.isRecycled()){
						bitmap.recycle();
					}
					return true;
				}						
			} else {
				 downloadImage(mImageUrl);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isOK) {
			if (isOK) {
				Bitmap bitmap = imageLoader.get(mImageUrl);
				if (bitmap != null) {
					int _temp_width=bitmap.getWidth();
					int scaleHeight=bitmap.getHeight();
					int half=(int) (columnWidth/1.8);
					if(_temp_width>columnWidth){
						float ratio   = _temp_width / (columnWidth * 1f);
						scaleHeight = (int) (scaleHeight / ratio);
						scaleHeight = scaleHeight>MContent.T_MAX_HEIGHT?MContent.T_MAX_HEIGHT:scaleHeight;
					}else if(_temp_width<half){//如果小于宽度,则重新加载
						File imageFile = new File(MUtils.getImagePath(mImageUrl));
						bitmap=MUtils.getBitmap(imageFile, columnWidth);
					}
					addImage(bitmap, scaleHeight);
				}
			}
			taskCollection.remove(this);
		}

		// 向ImageView中添加一张图片
		private void addImage(Bitmap bitmap, int scaleHeight) {
			if (mImageView != null) {
				mImageView.setImageBitmap(bitmap);
			} else {
				View convertView = mInflater.inflate(R.layout.lay_image_item,null);
				ImageView imageView = (ImageView) convertView.findViewById(R.id.bg_image_item);
				imageView.setTag(R.string.str_image_url, mImageUrl);
				imageView.setImageBitmap(bitmap);
				imageView.setOnClickListener(new ImageClick(bitmap,imageViewList.size() + 1));
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setDrawingCacheEnabled(true);
				findColumnToAdd(imageView, scaleHeight).addView(imageView);
				imageViewList.add(imageView);
				
			}
		}

		// 点击图片弹出层
		private class ImageClick implements OnClickListener {
			private Bitmap bitmap;
			private int currentIndex;

			public ImageClick(Bitmap map, int currentIndex) {
				this.bitmap = map;
				this.currentIndex = currentIndex;
			}

			@Override
			public void onClick(View v) {
				if (dialog == null) {
					dialog = new MImageDialog(context, R.style.MyDialog);
				}
				dialog.init(currentIndex, imageViewList, scrollViewWidth,scrollViewHeight);
				dialog.showDialog(bitmap);
			}
		};

		// 找到此时应该添加图片的一列。原则就是对三列的高度进行判断，当前高度最小的一列就是应该添加的一列。
		private LinearLayout findColumnToAdd(ImageView imageView,int scaleHeight) {
			if (firstColumnHeight <= secondColumnHeight) {
				if (firstColumnHeight <= thirdColumnHeight) {
					imageView.setTag(R.string.str_border_top, firstColumnHeight);
					firstColumnHeight += scaleHeight;
					imageView.setTag(R.string.str_border_bottom,firstColumnHeight);
					return firstColumn;
				}
				imageView.setTag(R.string.str_border_top, thirdColumnHeight);
				thirdColumnHeight += scaleHeight;
				imageView.setTag(R.string.str_border_bottom, thirdColumnHeight);
				return thirdColumn;
			} else {
				if (secondColumnHeight <= thirdColumnHeight) {
					imageView.setTag(R.string.str_border_top,secondColumnHeight);
					secondColumnHeight += scaleHeight;
					imageView.setTag(R.string.str_border_bottom,secondColumnHeight);
					return secondColumn;
				}
				imageView.setTag(R.string.str_border_top, thirdColumnHeight);
				thirdColumnHeight += scaleHeight;
				imageView.setTag(R.string.str_border_bottom, thirdColumnHeight);
				return thirdColumn;
			}
		}
		
		

		private void downloadImage(final String imageUrl) {
			http.download(imageUrl, null, MUtils.getImagePath(imageUrl), true,
					new AjaxCallBack<File>() {
						@Override
						public void onSuccess(File t) {
							if (t.exists()) {
					            Bitmap bitmap=MUtils.getBitmap(t, columnWidth);
					            if(bitmap==null){
					            	return;
					            }
					            int maxHeight=MBitmapUtil.getBitmapMaxHeight(t.getAbsolutePath(), columnWidth);
					            if(bitmap.getWidth()>columnWidth){
					            	bitmap = Bitmap.createScaledBitmap(bitmap, columnWidth , maxHeight , true);// convert decoded bitmap into well scalled Bitmap format.
								}
					            if (bitmap != null) {
									imageLoader.put(imageUrl, bitmap);
									//更新状态
									MDb.create(context).execSql(String.format(MContent.UPDATE_DOWN_WHERE, imageUrl));
									addImage(bitmap, maxHeight);
									//测试
									if(bitmap.isRecycled()){
										bitmap.recycle();
									}
								}
							}
						}

						@Override
						public void onFailure(Throwable t, int errorNo,String strMsg) {
							Log.e("下载失败,错误原因:" , errorNo + "," + strMsg);
						}
					});
			}
		}

	// --------------------------------------------------------这些需要移到数据模块中去------------结束

	// -------------------------------------------------------------------------异步加载数据开始

	/**
	 * 加载新内容,从网站加载数据
	 */
	class LoadHeaderTask {

		private void execute() {
			int sleep=MContent.T_TYPE.equals("GIF")?sleepTimeGif:sleepTime;
			noFocus();
			MUtils.startServiceLoads(context);
			MUtils.refreshToken(context);

			List<Info> dataLst = MUtils.getLocalDatasToken(context);
			
			if (MCheck.isEmpty(dataLst)) {
				
				MToast.show(context,context.getResources().getString(R.string.str_no_datas));
				onHeaderRefreshComplete();
				return;
			}
			firstColumn.removeAllViews();
			secondColumn.removeAllViews();
			thirdColumn.removeAllViews();

			foreachDatas(dataLst);

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {

//					if (firstColumn.getChildCount() < 1) {
//						List<Info> dataLst = MUtils.getLocalDatasOlds(context);
//						foreachDatas(dataLst);
//					}
					onHeaderRefreshComplete();
				}
			}, sleep);
		}
	}

	/**
	 * 加载旧内容(从本地数据库进行查询)
	 */
	class LoadFooterTask {

		private void execute() {
			noFocus();
			List<Info> dataLst = MUtils.getLocalDatasToken(context);
			if (MCheck.isEmpty(dataLst)) {
				MUtils.startServiceLoads(context);
				MToast.show(context,context.getResources().getString(R.string.str_no_datas_local));
				onFooterRefreshComplete();
				return;
			}
			if(imageViewList.size()%60==0){
				firstColumn.removeAllViews();
				secondColumn.removeAllViews();
				thirdColumn.removeAllViews();		
				MUtils.startServiceLoads(context);
			}
			foreachDatas(dataLst);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					onFooterRefreshComplete();
				}

			}, sleepTime);
		}
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void disableDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}



}
