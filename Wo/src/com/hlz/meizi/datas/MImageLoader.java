package com.hlz.meizi.datas;

import android.content.Context;
import android.graphics.Bitmap;

import com.azero.cache.MCache;

/**
 * 对图片进行管理的工具类。
 */
public class MImageLoader {

	private static MCache cache=null;
	private MImageLoader(Context context) {
		cache=MCache.me(context);
	}
	
	public static MImageLoader getInstance(Context context) {
		return ImageLoaderInner.getSingleInstance(context);
	}

	private static class ImageLoaderInner {
		public static MImageLoader getSingleInstance(Context context) {
			return new MImageLoader(context);
		}
	}

	public MImageLoader put(String key, Bitmap bitmap) {
		if (get(key) == null) {
			cache.put(key, bitmap);
		}
		return this;
	}


	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap get(String key) {
		return cache.getAsBitmap(key);
	}
	
}
