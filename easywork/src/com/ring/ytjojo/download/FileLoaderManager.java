package com.ring.ytjojo.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.TextureView;

import com.ring.ytjojo.app.AppContext;
import com.ring.ytjojo.app.Constants;
import com.ring.ytjojo.helper.NotificationHelper;
import com.ring.ytjojo.utill.Handler_File;
import com.ring.ytjojo.utill.L;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.event.EventBus;

/**
 * 文件多线程断点下载（自动判断是否支持断点）
 * 
 * @author gdpancheng@gmail.com 2014-2-20 下午2:51:27
 */

public class FileLoaderManager {

	private final int stuts_finish = 0;
	private final int stuts_start = 1;
	// 存储了下载状态
	private HashMap<String, Boolean> loadingMap = new HashMap<String, Boolean>();
	private String UA = "Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)";
	// 代理服务器
	private String proxyServer;
	// 代理端口
	private int proxyPort;
	// 是否使用代理
	private boolean useProxy;
	// 代理名称
	private String proxyUser;
	// 代理密码
	private String proxyPassword;
	private int blockSize = 1024 * 4; // 4K 一个块
	// 事件总线
	;

	static FileLoaderManager instance;

	public static FileLoaderManager getInstance() {
		if (instance == null) {
			synchronized (FileLoaderManager.class) {
				if (instance == null) {
					instance = new FileLoaderManager();
				}
			}
		}
		return instance;
	}

	private FileLoaderManager() {
		EventBus.getDefault().register(this);
	}

	public void destory() {
		EventBus.getDefault().unregister(this);
		instance = null;

	}

	void onEventMainThread(NotfiEntity entity) {

	}

	// 下载集合
	private HashMap<String, FileEntity> fileEntities = new HashMap<String, FileEntity>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			FileEntity entity = (FileEntity) msg.obj;

			switch (msg.what) {
			case stuts_finish:
				loadingMap.remove(entity.getUrl());
				sendMsg(entity, FileResultEvent.status_sucess, 100);
				break;
			case stuts_start:
				synchronized (loadingMap) {
					if (!loadingMap.get(entity.getUrl())) {
						loadingMap.remove(entity.getUrl());
						sendMsg(entity, FileResultEvent.status_fail, 0);
						return;
					}
				}

				entity.setIsSucess(false);
				if ((entity.getId() == 0 || entity.isAgain())
						&& entity.getLength() > 0 && entity.getRange()) {
					// 支持断点下载 但是需要重新下载
					L.i("支持多线程下载，开始下载");
					int threads = entity.getThreads();
					long length = entity.getLength();
					long threadsLength = length / threads;
					long orther = length % threads;

					DbService.getInstance().deleteFileEntity(entity.getId());
					DownloadThreadGroup downloadGroup = new DownloadThreadGroup(
							"multi threads reload", entity);
					for (int i = 0; i < threads; i++) {
						long begin = i * threadsLength;
						long end = (i + 1) * threadsLength;
						if (threads > 1 && i == threads - 1 && orther > 0) {
							// 如果最后一个线程，有余数，需要修正
							end = end + orther;
						}
						ThreadEntity threadEntity = new ThreadEntity();
						threadEntity.setStart(begin);
						threadEntity.setEnd(end);
						threadEntity.setLoad(0L);
						threadEntity.setFileEntity(entity);
						downloadGroup.setEntity(threadEntity);
						DownloadThread downloadThread = new DownloadThread(
								downloadGroup, threadEntity, entity, i, begin,
								end);
						downloadThread.start();
					}
					if (threads > 0) {
						downloadGroup.start();
					}
					return;
				}
				if (entity.getLength() <= 0 || !entity.getRange()) {
					// 不支持断点下载 不管之前下载了多少 都需要重新下载
					DownloadSingleThread singleThread = new DownloadSingleThread(
							entity);
					singleThread.start();
					return;
				}
				if (entity.getId() != 0 && entity.getLength() > 0
						&& entity.getRange()) {
					// 支持断点下载 但是之前下载过一部分
					List<ThreadEntity> entities = entity.getThreadEntityList();
					if (entities == null) {
						return;
					}
					int size = entities.size();
					DownloadThreadGroup downloadGroup = new DownloadThreadGroup(
							"multi threads continue", entity);
					for (int i = 0; i < size; i++) {
						ThreadEntity threadEntity = entities.get(i);
						downloadGroup.setEntity(threadEntity);
						// 如果起始点加上下载的等于结束的 说明这个线程下载结束
						if (threadEntity.getLoad() + threadEntity.getStart() == threadEntity
								.getEnd()) {
							continue;
						}
						DownloadThread downloadThread = new DownloadThread(
								downloadGroup, threadEntity, entity, i,
								threadEntity.getStart(), threadEntity.getEnd());
						downloadThread.start();
					}
					downloadGroup.start();
				}
				break;
			}
		};
	};

	public String getProxyUser() {
		return proxyUser;
	}

	/**
	 * 代理用户名
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:52:05
	 * @param proxyUser
	 * @return void
	 */
	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	/**
	 * 设置代理的密码
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:53:03
	 * @param proxyPassword
	 * @return void
	 */
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	/**
	 * 设置代理服务器
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:53:19
	 * @param proxyServer
	 * @return void
	 */
	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * 设置代理端口
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:53:30
	 * @param proxyPort
	 * @return void
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	/**
	 * 设置是否使用代理
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:53:41
	 * @param useProxy
	 * @return void
	 */
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	/**
	 * 下载文件
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:53:51
	 * @param url
	 *            请求链接
	 * @param path
	 *            下载保存到本地文件夹的路径
	 * @return void
	 */
	public void download(String url, String path) {
		download(url, path, 1, false, null);
	}

	/**
	 * 下载文件
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:53:51
	 * @param url
	 *            请求链接
	 * @param path
	 *            下载保存到本地文件夹的路径
	 * @return void
	 */
	public void download(String url) {
		download(url, null, 1, false, null);
	}

	public void download(String url, String path, NotfiEntity notfi) {
		download(url, path, 1, false, notfi);
	}

	public void download(String url, NotfiEntity notfi) {
		download(url, null, 1, false, notfi);
	}

	/**
	 * 下载文件
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:54:47
	 * @param url
	 *            请求链接
	 * @param path
	 *            下载保存到本地的位置
	 * @param threads
	 *            线程数
	 * @return void
	 */
	public void download(String url, String path, int threads) {
		download(url, path, threads, false, null);
	}

	public void download(String url, int threads) {
		download(url, null, threads, false, null);
	}

	public void download(String url, String path, int threads, NotfiEntity notfi) {
		download(url, path, threads, false, notfi);
	}

	public void download(String url, int threads, NotfiEntity notfi) {
		download(url, null, threads, false, notfi);
	}

	/**
	 * 版本更新下载
	 * 
	 * @author gdpancheng@gmail.com 2014-3-2 下午10:37:27
	 * @param url
	 * @param path
	 * @return void
	 */
	public void downloadUpdate(String url, String path) {
		download(url, path, 1, true, null);
	}

	public void downloadUpdate(String url) {
		download(url, null, 1, true, null);
	}

	public void downloadUpdate(String url, String path, NotfiEntity notfi) {
		download(url, path, 1, true, notfi);
	}

	public void downloadUpdate(String url, NotfiEntity notfi) {
		download(url, null, 1, true, notfi);
	}

	/**
	 * 版本更新下载
	 * 
	 * @author gdpancheng@gmail.com 2014-3-2 下午10:37:37
	 * @param url
	 * @param path
	 * @param threads
	 *            线程数
	 * @return void
	 */
	public void downloadUpdate(String url, String path, int threads) {
		download(url, path, threads, true, null);
	}

	public void downloadUpdate(String url, int threads) {
		download(url, null, threads, true, null);
	}

	public void downloadUpdate(String url, String path, int threads,
			NotfiEntity notfi) {
		download(url, path, threads, true, notfi);
	}

	public void downloadUpdate(String url, int threads, NotfiEntity notfi) {
		download(url, null, threads, true, notfi);
	}

	// 获取正在下载的url
	public Set<String> getLoadingUrl() {
		return fileEntities.keySet();
	}

	// 显示顶部通知栏
	public boolean showNotif(String url, NotfiEntity notfi) {
		if (!fileEntities.containsKey(url)) {
			return false;
		}
		FileEntity entity = fileEntities.get(url);
		entity.setNotfi(notfi);
		NotificationHelper helper = new NotificationHelper(
				Constants.appContext, notfi.getLayout_id(), notfi.getIcon_id(),
				notfi.getProgress_id(), notfi.getProgress_txt_id(),
				notfi.getClazz());
		entity.setHelper(helper);
		if (entity.getRange()) {
			helper.initNotif();
		} else {
			helper.downNotification("下载中......");
		}
		return true;
	}

	public void hideNotif(String url) {
		if (!fileEntities.containsKey(url)) {
			return;
		}
		FileEntity entity = fileEntities.get(url);
		entity.setNotfi(null);
		NotificationHelper helper = entity.getHelper();
		entity.setHelper(null);
		helper.cancel();
	}

	public void hideNotif() {
		NotificationManager mContextNotificationManager = (NotificationManager) Constants.appContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mContextNotificationManager.cancelAll();
	}

	/**
	 * 停止下载
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:54:19
	 * @param url
	 * @return void
	 */
	public void stop(String url) {
		if (loadingMap.containsKey(url)) {
			loadingMap.put(url, false);
		}
	}

	public List<FileEntity> getAllDownload() {
		return DbService.getInstance().getAllFileEntity();
	}

	public List<FileEntity> getAllFinishDownload() {
		return DbService.getInstance().getAllFinishedFileEntity();
	}

	public List<FileEntity> getAllFailureDownload() {

		return DbService.getInstance().getAllFailureFileEntity();
	}

	/**
	 * 清除下载
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:54:27
	 * @param url
	 * @return void
	 */

	// 清除数据库中数据
	public void clearByUrl(String url) {

	}

	/**
	 * 清除所有历史记录
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:54:37
	 * @return void
	 */
	public void clearHistory() {
		DbService.getInstance().clear();
	}

	private void sendHandlarMsg(FileEntity fileEntity) {
		fileEntity.setNotfi(null);
		Message message = handler.obtainMessage();
		message.what = stuts_finish;
		message.obj = fileEntity;
		handler.sendMessage(message);
	}

	private void copyFile(final String path, FileEntity fileEntity) {
		final File newfile = new File(path);
		final File oldfile = new File(fileEntity.getPath());
		File file = newfile.getParentFile();
		if (!file.exists()) {
			file.mkdirs();
		}
		final FileEntity newFileEntity = fileEntity;
		final String newPath = path;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 文件拷贝以后必须更新数据库
				// ********************************************************************
				Handler_File.copyFile(oldfile, newfile);
				newFileEntity.setPath(newPath);
				DbService.getInstance().updateFileEntity(newFileEntity);
				// ********************************************************************
				// 如果拷贝之前已经下载完成了 那么发个通知 告诉已经下载完成了
				if (newFileEntity.getIsSucess()) {
					// 因为已经本地存在 所以无需通知栏了
					newFileEntity.setNotfi(null);
					Message message = handler.obtainMessage();
					message.what = stuts_finish;
					message.obj = newFileEntity;
					handler.sendMessage(message);
					return;
				}

			}
		}).start();
	}

	/**
	 * 下载文件
	 * 
	 * @author gdpancheng@gmail.com 2014-2-20 下午2:54:47
	 * @param url
	 *            请求链接
	 * @param path
	 *            下载保存到本地的位置
	 * @param threads
	 *            线程数
	 * @return void
	 */
	private void download(String url, String path, int threads, boolean update,
			NotfiEntity notfi) {
		// 正在下载中或者停止中
		if (loadingMap.containsKey(url)) {
			return;
		}
		loadingMap.put(url, true);

		if (TextUtils.isEmpty(path)) {
			path = Handler_File.getExternalCacheDir(Constants.appContext,
					"files").getPath()
					+ "/file/";

		}
		String folder = "";
		File file = new File(path);
		if (file.isDirectory()) {
			folder = file.getAbsolutePath();

		} else {
			folder = file.getParent();
		}
		file = new File(folder);
		if(!file.exists()){
			file.mkdirs();
		}
		
		
		FileEntity fileEntity = DbService.getInstance().getFileEntityByUrl(url);

		// 说明之前没有下载
		if (fileEntity == null) {
			fileEntity = new FileEntity();
			fileEntity.setUrl(url);
			fileEntity.setPath(path);
			fileEntity.setThreads(threads);
		}

		fileEntities.put(url, fileEntity);

		if (notfi != null) {
			NotificationHelper helper = new NotificationHelper(
					Constants.appContext, notfi.getLayout_id(),
					notfi.getIcon_id(), notfi.getProgress_id(),
					notfi.getProgress_txt_id(), notfi.getClazz());
			helper.initNotif();
			fileEntity.setHelper(helper);
		}

		fileEntity.setNotfi(notfi);
		fileEntity.setUpdate(update);
		// 如果下载的文件不存在 那么表示要重新下载
		File old = new File(fileEntity.getPath());
		if (!old.exists() || old.length() == 0) {
			fileEntity.setAgain(true);
		}
		String pathFolder = Handler_File.getDirs( fileEntity.getPath());
		// 下完了 就直接跳转
		if (folder.equals(pathFolder)&& fileEntity.getIsSucess()
				&& !fileEntity.isAgain()) {
			sendHandlarMsg(fileEntity);
			return;
		} else if (!folder.equals(pathFolder)
				&& fileEntity.getIsSucess() && !fileEntity.isAgain()) {
			// 已经下载了 并且文件存在 只需要把文件拷贝过去即可
			copyFile(path, fileEntity);
			return;
		}

		// 走到这里说明 情况 1 文件下载了一部分 继续下载 2 文件还未下载
		// 开启线程 获取服务器支持情况或者
		L.i("dowload begin");
		GetLengthThread thread = new GetLengthThread(fileEntity, path);
		new Thread(thread).start();
	}

	/**
	 * 获取文件名
	 * 
	 * @param conn
	 * @return
	 */
	private String getFileName(String downloadUrl, HttpURLConnection conn) {
		String filename = downloadUrl
				.substring(downloadUrl.lastIndexOf('/') + 1);

		if (filename == null || TextUtils.isEmpty(filename.trim())) {// 如果获取不到文件名称
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);

				if (mine == null)
					break;

				if ("content-disposition".equals(conn.getHeaderFieldKey(i)
						.toLowerCase())) {
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(
							mine.toLowerCase());
					if (m.find())
						return m.group(1);
				}
			}

			filename = UUID.randomUUID() + ".tmp";// 默认取一个文件名
		}

		return filename;
	}

	private class GetLengthThread implements Runnable {
		private FileEntity fileEntity;
		private String path;

		public GetLengthThread(FileEntity fileEntity, String path) {
			this.fileEntity = fileEntity;
			this.path = path;
		}

		@SuppressLint("NewApi")
		@Override
		public void run() {
			try {
				if (!loadingMap.get(fileEntity.getUrl())) {
					sendMsg(fileEntity, FileResultEvent.status_fail, 0);
					loadingMap.remove(fileEntity.getUrl());
					return;
				}
				sendMsg(fileEntity, FileResultEvent.status_start, 0);

				HttpURLConnection connection = getHttpConnection(0,
						fileEntity.getUrl());

				// --------------------------------------------------------------------------------------
				// 判断是否是重定向地址的办法 有两种
				String redictURL = null;
				Map<String, List<String>> fieldMap = connection
						.getHeaderFields();
				for (String key : fieldMap.keySet()) {
					if ("location".equalsIgnoreCase(key)) {
						redictURL = fieldMap.get(key).get(0);
						continue;
					}
				}
				if (redictURL == null) {
					redictURL = connection.getURL().toString();
				}
				// 说明是重定向
				if (redictURL.startsWith("http")
						&& !redictURL.equals(fileEntity.getUrl())) {
					connection.disconnect();
					fileEntity.setReal_url(redictURL);
					connection = getHttpConnection(0, redictURL);
					L.i("下载的文件是重定向地址,正在获取真实地址......");
				}
				int length = connection.getContentLength();
				if (length <= 0) {
					// 不支持多线程下载,采用单线程下载
					L.w("服务器不能返回文件大小，采用单线程下载");
					fileEntity.setThreads(1);
				}
				String filename = getFileName(fileEntity.getUrl(), connection);
				File checkFile = new File(fileEntity.getPath());
				if(checkFile .isDirectory()){
					
					fileEntity.setPath(fileEntity.getPath() + filename);
				}

				// --------------------------------------------------------------------------------------
				// 是否支持断点
				fileEntity.setRange(true);
				if (connection.getHeaderField("Content-Range") == null) {
					L.w("服务器不支持断点续传");
					fileEntity.setRange(false);
				}
				connection.disconnect();

				File file = new File(fileEntity.getPath());
				// 存储空间不够
				if (length > 0 && file.getParentFile().getFreeSpace() < length) {
					L.e("磁盘空间不够");
					sendMsg(fileEntity, FileResultEvent.status_fail, 0);
					loadingMap.remove(fileEntity.getUrl());
					return;
				}
				// 文件大小被改变 需要重下
				if (length > 0 && fileEntity.getLength() > 0
						&& fileEntity.getLength() != length) {
					fileEntity.setAgain(true);
				}
				fileEntity.setLength((long) length);

				if (length <= 1024 * 10) {
					fileEntity.setThreads(1);
				}

				Message message = handler.obtainMessage();
				message.what = stuts_start;
				message.obj = fileEntity;
				handler.sendMessage(message);
			} catch (Exception e) {
				if (fileEntity.getNotfi() != null) {
					NotfiEntity notfiEntity = fileEntity.getNotfi();
					NotificationHelper helper = new NotificationHelper(
							Constants.appContext, notfiEntity.getLayout_id(),
							notfiEntity.getIcon_id(),
							notfiEntity.getProgress_id(),
							notfiEntity.getProgress_txt_id(),
							notfiEntity.getClazz());
					helper.initNotif();
					fileEntity.setHelper(helper);
				}
				sendMsg(fileEntity, FileResultEvent.status_fail, 0);
				loadingMap.remove(fileEntity.getUrl());
			}
		}
	}

	private HttpURLConnection getHttpConnection(long pos, String urls)
			throws IOException {
		URL url = new URL(urls);
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection httpConnection = null;
		if (useProxy) {
			SocketAddress addr = new InetSocketAddress(proxyServer, proxyPort);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
			httpConnection = (HttpURLConnection) url.openConnection(proxy);
			if (proxyUser != null && proxyPassword != null) {
				String encoded = new String(Base64.encode(new String(proxyUser
						+ ":" + proxyPassword).getBytes(), Base64.DEFAULT));
				httpConnection.setRequestProperty("Proxy-Authorization",
						"Basic " + encoded);
			}
		} else {
			httpConnection = (HttpURLConnection) url.openConnection();
		}
		httpConnection.setRequestProperty("Accept-Encoding", "identity");
		httpConnection.setConnectTimeout(10 * 1000);
		httpConnection.setReadTimeout(10 * 1000);
		httpConnection.setRequestProperty("User-Agent", UA);
		if (pos >= 0) {
			httpConnection.setRequestProperty("RANGE", "bytes=" + pos + "-");
		}
		int responseCode = httpConnection.getResponseCode();
		if (responseCode < 200 || responseCode >= 400) {
			loadingMap.remove(urls);
			throw new IOException("服务器返回无效信息:" + responseCode);
		}
		return httpConnection;
	}

	private class DownloadThreadGroup extends ThreadGroup {

		public List<ThreadEntity> entitysList = new ArrayList<ThreadEntity>();
		private FileEntity entity;

		public DownloadThreadGroup(String name, final FileEntity entity) {
			super(name);
			this.entity = entity;
		}

		private class DownLoad extends Thread {
			@Override
			public void run() {
				super.run();
				int number = -1;
				while (DownloadThreadGroup.this.activeCount() > 0) {
					entity.setLoadedLength(getSize());
					int progress = (int) (entity.getLoadedLength() * 100 / entity
							.getLength());
					if (number != progress) {
						sendMsg(entity, FileResultEvent.status_loading,
								progress);
					}
					number = progress;
					DbService.getInstance().insertOrUpdateThreadEntity(
							entitysList);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
				long size = getSize();
				entity.setIsSucess(true);
				if (size != entity.getLength()) {
					// 发送下载失败
					sendMsg(entity, FileResultEvent.status_fail, 0);
					entity.setIsSucess(false);
				} else {
					// 发送下载进度
					int price = (int) (size * 100 / entity.getLength());
					sendMsg(entity, FileResultEvent.status_sucess, price);
				}
				loadingMap.remove(entity.getUrl());
				DbService.getInstance().insertOrUpdateThreadEntity(entitysList);
				DbService.getInstance().updateFileEntity(entity);
			}
		}

		private long getSize() {
			int length = entitysList.size();
			long size = 0;
			for (int i = 0; i < length; i++) {
				size = size + entitysList.get(i).getLoad();
			}
			return size;
		}

		public void setEntity(ThreadEntity entity) {
			this.entitysList.add(entity);
		}

		public void start() {
			if (entity.getNotfi() != null) {
				NotfiEntity notfiEntity = entity.getNotfi();
				NotificationHelper helper = new NotificationHelper(
						Constants.appContext, notfiEntity.getLayout_id(),
						notfiEntity.getIcon_id(), notfiEntity.getProgress_id(),
						notfiEntity.getProgress_txt_id(),
						notfiEntity.getClazz());
				helper.initNotif();
				entity.setHelper(helper);
			}
			new DownLoad().start();
		}
	}

	private class DownloadThread extends Thread {

		private RandomAccessFile destFile; // 用来实现保存的随机文件
		private long blockBegin = 0; // 开始块
		private long blockEnd = 0; // 结束块
		private long loading;// 绝对指针
		private FileEntity entity;
		private ThreadEntity threadEntity;
		private long offset;

		public DownloadThread(DownloadThreadGroup group,
				ThreadEntity threadEntity, FileEntity entity, int id,
				long blockBegin, long blockEnd) {
			super(group, "downloadThread-" + id);
			this.blockBegin = blockBegin;
			this.blockEnd = blockEnd;
			this.entity = entity;
			this.threadEntity = threadEntity;
			loading = threadEntity.getLoad();
			offset = blockBegin + loading;
			try {
				destFile = new RandomAccessFile(entity.getPath(), "rw");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			BufferedInputStream inputStream = null;
			try {
				destFile.seek(1l * offset);
				HttpURLConnection httpConnection = getHttpConnection(offset,
						entity.getReal_url() != null ? entity.getReal_url()
								: entity.getUrl());
				inputStream = new BufferedInputStream(
						httpConnection.getInputStream());
				byte[] b = new byte[blockSize];
				while (offset < blockEnd) {
					if (!loadingMap.containsKey(entity.getUrl())
							|| !loadingMap.get(entity.getUrl())) {
						break;
					}
					int read = inputStream.read(b);
					loading = loading + read;
					if ((offset = blockBegin + loading) > blockEnd) {
						loading = blockEnd - blockBegin;
					}
					destFile.write(b, 0, read);
					threadEntity.setLoad(loading);
				}
				L.d("---------------------------------------------------------------------");
				L.d(getName() + "开始：" + blockBegin + "下载了：" + loading + "结束："
						+ blockEnd);
				L.d("---------------------------------------------------------------------");
				httpConnection.disconnect();
				return;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (Exception te) {
				}
				try {
					if (destFile != null)
						destFile.close();
				} catch (Exception te) {
				}
			}
		}
	}

	private class DownloadSingleThread extends Thread {

		private RandomAccessFile destFile; // 用来实现保存的随机文件
		private long readCount;// 绝对指针
		private FileEntity entity;

		public DownloadSingleThread(FileEntity entity) {
			super();
			this.entity = entity;
			try {
				destFile = new RandomAccessFile(entity.getPath(), "rw");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				if (entity.getNotfi() != null) {
					NotificationHelper helper = entity.getHelper();
					helper.downShowNotification("下载失败");
					entity.setHelper(helper);
				}
			}
		}

		public void run() {
			BufferedInputStream inputStream = null;
			try {
				destFile.seek(0);
				HttpURLConnection httpConnection = getHttpConnection(-1,
						entity.getReal_url() != null ? entity.getReal_url()
								: entity.getUrl());
				inputStream = new BufferedInputStream(
						httpConnection.getInputStream());

				if (entity.getNotfi() != null && entity.getLength() == 0) {
					NotfiEntity notfiEntity = entity.getNotfi();
					NotificationHelper helper = new NotificationHelper(
							Constants.appContext, notfiEntity.getLayout_id(),
							notfiEntity.getIcon_id(),
							notfiEntity.getProgress_id(),
							notfiEntity.getProgress_txt_id(),
							notfiEntity.getClazz());
					helper.downNotification("下载中...");
					entity.setHelper(helper);
				}

				byte[] b = new byte[blockSize];
				int read = 0;
				int number = -1;
				while ((read = inputStream.read(b)) > 0) {
					if (!loadingMap.get(entity.getUrl())) {
						// 发送下载失败
						sendMsg(entity, FileResultEvent.status_fail, 0);
						if (entity.getNotfi() != null) {
							NotificationHelper helper = entity.getHelper();
							helper.downShowNotification("下载失败");
							entity.setHelper(helper);
						}
						entity.setIsSucess(false);
						DbService.getInstance().insertFileEntity(entity);
						return;
					}
					readCount = readCount + read;
					destFile.write(b, 0, read);

					entity.setLoadedLength(readCount);

					int progress = (int) ((int) (entity.getLoadedLength() * 100 / entity
							.getLength()));
					if (number != progress) {
						sendMsg(entity, FileResultEvent.status_loading,
								progress);
					}
					number = progress;

				}
				// 发送下载完成通知
				sendMsg(entity, FileResultEvent.status_sucess, 100);
				if (entity.getNotfi() != null) {
					NotificationHelper helper = entity.getHelper();
					if (entity.isUpdate()) {
						helper.notifyUpdateFinish(new File(entity.getPath()));
					} else {
						helper.downShowNotification("下载成功");
						entity.setHelper(helper);
					}
				}
				loadingMap.remove(entity.getUrl());
				entity.setIsSucess(true);
				DbService.getInstance().insertFileEntity(entity);
				L.d("---------------------------------------------------------------------");
				L.d("单线程下载:" + readCount);
				L.d("---------------------------------------------------------------------");
				httpConnection.disconnect();
				return;
			} catch (Exception e) {
				sendMsg(entity, FileResultEvent.status_fail, 100);
			} finally {
				loadingMap.remove(entity.getUrl());
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (Exception te) {
				}
				try {
					if (destFile != null)
						destFile.close();
				} catch (Exception te) {
				}
			}
		}
	}

	/**
	 * 发送广播 更改下载状态
	 * 
	 * @author gdpancheng@gmail.com 2014-3-2 下午10:39:28
	 * @param entity
	 * @param status
	 * @param progress
	 * @return void
	 */
	private void sendMsg(FileEntity entity, int status, int progress) {
		FileResultEvent resultEvent = new FileResultEvent();
		resultEvent.setRange(entity.getRange());
		resultEvent.setUrl(entity.getUrl());
		resultEvent.setLoadedLength(entity.getLoadedLength());
		resultEvent.setFile(new File(entity.getPath()));
		resultEvent.setStatus(status);
		resultEvent.setLength(entity.getLength());
		resultEvent.setProgress(progress);
		EventBus.getDefault().post(resultEvent);

		if (status == FileResultEvent.status_sucess
				|| status == FileResultEvent.status_fail) {
			fileEntities.remove(entity.getUrl());
		}

		// 下载成功了 但是顶部没有通知栏
		if (entity.getNotfi() == null && entity.isUpdate()
				&& status == FileResultEvent.status_sucess) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(resultEvent.getFile()),
					"application/vnd.android.package-archive");
			Context context = Constants.appContext;
			context.startActivity(intent);
		}

		// 更新顶部通知栏
		if (entity.getNotfi() != null) {
			NotificationHelper helper = entity.getHelper();
			switch (status) {
			case FileResultEvent.status_start:
				break;
			case FileResultEvent.status_loading:
				helper.refreshProgress(progress);
				break;
			case FileResultEvent.status_fail:
				helper.downShowNotification("下载失败");
				break;
			case FileResultEvent.status_sucess:
				if (entity.isUpdate()) {
					helper.notifyUpdateFinish(new File(entity.getPath()));
				} else {
					helper.downShowNotification("下载成功");
				}
				break;
			}
		}
	}

}
