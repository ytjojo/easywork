package com.ring.ytjojo.commonCache;

/**
 * HttpConstants<br/>
 * <strong>All in lower case to put and get easy</strong>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-12
 */
public class HttpConstants {

	public static final String EXPIRES = "expires";
	public static final String CACHE_CONTROL = "cache-control";
	// a. 某次请求不想使用缓存
	// 在调用httpGet方法时设置入参HttpRequest，如下：
	// request.setRequestProperty(“cache-control”, “no-cache”);
	// b. 某次请求返回数据不想被缓存
	// 在调用httpGet方法时设置入参HttpRequest，如下：
	//request.setRequestProperty(“cache-control”, “no-store”);
}
