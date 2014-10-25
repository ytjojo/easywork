package com.ring.ytjojo.download;

import java.util.List;

import com.ring.ytjojo.app.AppContext;
import com.ring.ytjojo.app.Constants;
import com.ring.ytjojo.download.DaoMaster.DevOpenHelper;

import android.app.Application;
import android.content.Context;

public class DbService {

	private static final String TAG = DbService.class.getSimpleName();
	private static DbService instance;
	private Application context;
	private DaoSession mDaoSession;
	private FileEntityDao mFileEntityDao;
	private ThreadEntityDao mThreadEntityDao;

	public static DbService getInstance() {
		if (instance == null) {
			synchronized (DbService.class) {
				if(instance == null){
					instance = new DbService();
					
				}
			}

		}
		return instance;
	}

	private DbService() {
		if (context == null) {
			context = AppContext.mAppContext;
		}
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
				Constants.DOLOAD_DB_NAME, null);
		DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
		mDaoSession = daoMaster.newSession();
		mFileEntityDao = mDaoSession.getFileEntityDao();
		mThreadEntityDao = mDaoSession.getThreadEntityDao();
	}
	public void insertFileEntity(FileEntity entity){
		mFileEntityDao.insert(entity);
	}
	public void deleteFileEntity(long id){
		mFileEntityDao.deleteByKey(id);
	}
	public void updateFileEntity(FileEntity entity){
		mFileEntityDao.update(entity);
	}
	public List<FileEntity> getAllFileEntity(){
		return mFileEntityDao.loadAll();
		
	}
	public List<FileEntity> getAllFinishedFileEntity(){
		return mFileEntityDao.queryRaw( "where isSucess = ?", "11");
	}
	
	public List<FileEntity> getAllFailureFileEntity(){
		return mFileEntityDao.queryRaw( "where isSucess = ?", "01");
	}
	
	public FileEntity getFileEntityByUrl(String url){
		List<FileEntity> list = mFileEntityDao.queryRaw("where url = ?", url);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
			
		return null;
	}
	public void clear(){
		mFileEntityDao.deleteAll();
		mThreadEntityDao.deleteAll();
	}
	public void insertOrUpdateThreadEntity(final List<ThreadEntity> list){
		if(list == null || list.size() == 0){
			return;
		}
		mThreadEntityDao.getSession().runInTx(new Runnable() {
			
			@Override
			public void run() {
				for( ThreadEntity entity : list){
					mThreadEntityDao.insertOrReplace(entity);
				}
				
			}
		});
	}
}
