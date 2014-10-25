package com.ring.ytjojo.cache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import android.graphics.Bitmap;
import android.text.TextUtils;

/**
 * <p>Bitmap缓存池</p>
 * 
 * <p>基于2Q改进算法缓存</p>
 * <p>http://www.vldb.org/conf/1994/P439.PDF</p>
 *
 * 使用：
 *   static DoubleCache imgCache = new DoubleCache();
 *    
 *    ...
 *    
 *    
 *    String imgUrl = "http://monstar.ch/uploads/img/201212/22085340_Zun9.jpg";
 *    if(DoubleCache.isCached(imgUrl)){
 *        Bitmap tBitmap = DoubleCache.get(imgUrl);
 *    }else{
 *        //从服务端获取图像
 *        Bitmap tBitmap = 服务端获取的Bitmap
 *        
 *        DoubleCache.put(imgUrl,tBitmap);
 *    }
 * 
 * @author lei.guoting
 */
public class DoubleCache{
	private static final int DEFAULT_MAX_SIZE = (int)(1024 * 1024 * 3.5f);      //默认缓存池大小  3.5M
	private static final int DEFAULT_IN_QUEUE_MAX_SIZE = 40;                    //fInCacheQue队列默认大小
	private static final int DEFAULT_OUT_QUEUE_MAX_SIZE = 60;                   //fOutCacheQue队列默认大小
	private static final int DEFAULT_CACHE_QUEUE_MAX_SIZE = 0;                  //finalCacheQue队列默认大小
	
	private final HashMap<String,Ref> cache;           //缓存池
    private final Queue<String> fInCacheQue;           //一级缓存，Ain
    private final Queue<String> fOutCacheQue;          //清楚一级缓存记录，Aout
    private final Queue<String> finalCacheQue;         //最终缓存，Am
    
    private final int cacheMaxSize;
    private int size;
    private int curBitmapSize;
	
	/**
	 * 构建默认大小的Cache
	 */
	public DoubleCache() {
		this(DEFAULT_MAX_SIZE);
	}
	
	/**
	 * 构建指定大小的Cache
	 * 
	 * @param cacheMaxSize Cache最大容量
	 */
	public DoubleCache(int cacheMaxSize){
		if(0 >= cacheMaxSize){
			throw new IllegalArgumentException("cacheMaxSize must be greater than 0");
		}
		
		this.cacheMaxSize = cacheMaxSize;
		this.cache = new HashMap<String,Ref>();
		this.fInCacheQue = new Queue<String>(DEFAULT_IN_QUEUE_MAX_SIZE);
		this.fOutCacheQue = new Queue<String>(DEFAULT_OUT_QUEUE_MAX_SIZE);
		this.finalCacheQue = new Queue<String>(DEFAULT_CACHE_QUEUE_MAX_SIZE);
	}
	
	/**
	 * 
	 * @param key key值
	 * @return  true-key对应的Bitmap在该缓存池中
	 *          false-key对应的Bitmap不在该缓存池中
	 */
	public synchronized boolean isCached(String key){
		return this.cache.containsKey(key);
	}
	
	/**
	 * 获取缓存池中Bitmap
	 * 
	 * @param key key值
	 * @return   Bitmap
	 */
	public Bitmap get(String key){
		if(TextUtils.isEmpty(key)){
			throw new NullPointerException("The key can not be null");
		}
		
		
		return this.getFromCache(key);
	}
	
	/**
	 * 将Bitmap缓存到该缓存池中
	 * 
	 * @param key
	 * @param mBitmap
	 */
    public synchronized void put(String key,Bitmap mBitmap){
    	if(this.cache.containsKey(key)){
    		return ;
    	}
    	
        if(this.fOutCacheQue.contains(key)){
        	this.finalCacheQue.addToHead(key);
        	this.fOutCacheQue.remove(key);
        	this.reclaimCache(key,mBitmap);
        }
        
        else{
        	this.fInCacheQue.addToHead(key);
        	this.reclaimCache(key,mBitmap);
        	if(this.fInCacheQue.isOverflow()){
        		String tKey = this.fInCacheQue.removeFromTail();
        		this.clearCache(tKey);
        		this.fOutCacheQue.addToHead(tKey);
        		this.fOutCacheQue.trim();
        	}
        }
    }
    
    private void reclaimCache(String key, Bitmap mBitmap){
    	if(this.hasFreeCache(mBitmap)){
            this.putIntoCache(key, mBitmap);
    	}
    	
    	else if(this.fInCacheQue.isOverflow()){
    		do{
    			String tKey = this.fInCacheQue.removeFromTail();
    			this.clearCache(tKey);
    			this.fOutCacheQue.addToHead(key);
    		}while(this.cacheMaxSize < (this.size + this.curBitmapSize));    		
    		this.fOutCacheQue.trim();
    		this.putIntoCache(key, mBitmap);    		
    	}
    	
    	else{
    		do{
    			String tKey = this.finalCacheQue.removeFromTail();
    			this.clearCache(tKey);
    		}while(this.cacheMaxSize < (this.size + this.curBitmapSize));     		
    		this.putIntoCache(key, mBitmap);
    	}
    }
    
    private void putIntoCache(String key, Bitmap mBitmap){
    	this.cache.put(key, new Ref(mBitmap,this.curBitmapSize));
		this.size += this.curBitmapSize;
		this.curBitmapSize = 0;
    }
    
    private boolean hasFreeCache(Bitmap mBitmap){
    	boolean hasFreeCache = true;
    	this.curBitmapSize = this.sizeOf(mBitmap);
    	if(this.cacheMaxSize < (this.size + this.curBitmapSize)){
    		hasFreeCache = false;
    	}
    	
    	return hasFreeCache;
    }
    
    private void clearCache(String key){
    	Ref  tRef = this.cache.remove(key);
        if(null != tRef){
        	this.size -= tRef.getSize();
        	if(null != tRef.getBitmap()){
            	tRef.getBitmap().recycle();	
        	}
        }
    }
    
    private Bitmap getFromCache(String key){
    	Bitmap tBitmap = null;
    	synchronized(this){
    		if(this.finalCacheQue.contains(key)){
        		this.finalCacheQue.moveToHead(key);
        	}
    		tBitmap = this.cache.get(key).getBitmap();
    	}
    	
    	return  tBitmap;
    }
	
	/**
	 * Bitmap 存储大小
	 * 
	 * @param mBitmap
	 * @return 
	 */
	private int sizeOf (Bitmap mBitmap){
		int weight = 0;
		switch(mBitmap.getConfig()){
		case ALPHA_8 :
			weight = 1;
			break;
		case ARGB_4444 :
			weight = 2;
			break;
		case ARGB_8888 :
			weight = 4;
			break;
		case RGB_565 :
			weight = 2;
			break;
		default :
			weight = 1;
			break;
		}

		return (mBitmap.getWidth() * mBitmap.getHeight() * weight);
	}
	
	public synchronized void destory(){
		this.cache.clear();
		this.finalCacheQue.clear();
		this.fInCacheQue.clear();
		this.fOutCacheQue.clear();
	}
	
	
	/**
	 * 队列
	 */
	class Queue<T>{
	    private int capacity;
		
	    private final List<T> list;
	    
	    /**
	     * @param capacity  队列最大容量，可为0；如果capacity为0，当前队列无大小限制
	     */
		public Queue(int capacity){
			this.capacity = capacity;
			if(0 < this.capacity){
				this.list = new ArrayList<T>(this.capacity);
			}else{
				this.list = new LinkedList<T>();
			}
			
		}
		
		/**
		 * 队列容量
		 * 
		 * @return 队列容量
		 */
		public int capacity(){
			return this.capacity;
		}
		
		/**
		 * 队列当前大小
		 * 
		 * @return 队列大小
		 */
		public int size(){
			return this.list.size();
		}
		
		/**
		 * 该队列中是否有key值
		 * 
		 * @param key
		 * @return true 存在key值，false 不存在key值
		 */
		public boolean contains(T key){
			return this.list.contains(key);
		}
		
		/**
		 * 队列是否溢出
		 * 
		 * @return 
		 */
		public boolean isOverflow(){
			if(0 == capacity){
				return false;
			}
			
			else{
				return (this.list.size() > this.capacity);	
			}
		}
		
		/**
		 * 将队列中key移动到队列头
		 * 
		 * @param key 值
		 */
		public void moveToHead(T key){
			this.list.remove(key);
			this.list.add(key);
		}
		
		/**
		 * 将key添加到队列头
		 * 
		 * @param key 值
		 */
		public void addToHead(T key){
			this.list.add(key);
		}
		
		/**
		 * 删除队列最后一个值
		 * 
		 * @return 当前删除队列值
		 */
		public T removeFromTail(){
			T tKey = null;
			if(0 < this.list.size()){
				tKey = this.list.remove(0);
			}
			return tKey;
		}
		
		/**
		 * 删除队列中key值
		 * 
		 * @param key  key值
		 * @return   删除成功返回当前key，删除失败返回null
		 */
		public T remove(T key){
			if(this.list.remove(key)){
				return key;
			}
			return null;
		}
		
		/**
		 * 将Queue中超过capacity的长度截取掉
		 *      如果list.size <= capacity, 什么都不做
		 */
		public void trim(){
			if((0 == capacity) || (this.list.size() <= this.capacity)){
				return ;
			}
			
			int tNum = this.list.size() - this.capacity;
			for(int idx = 0; idx < tNum; idx ++){
				this.removeFromTail();
			}
		}
		
		public void clear(){
			this.list.clear();
		}
	}
	
	class Ref{
		//private SoftReference<Bitmap> softReference;
		private Bitmap mBitmap;
		private int size;
		
		public Ref(Bitmap mBitmap,int size){
			//this.softReference = new SoftReference<Bitmap>(mBitmap);
			this.mBitmap = mBitmap;
			this.size = size;
		}
		
		public int getSize(){
			return size;
		}
		
		public Bitmap getBitmap(){
			//return softReference.get();
			return this.mBitmap;
		}
	}
	public class ImageCache extends WeakHashMap<String, Bitmap> {

		private static final long serialVersionUID = 1L;
		
		public boolean isCached(String url){
			return containsKey(url) && get(url) != null;
		}

	}

}