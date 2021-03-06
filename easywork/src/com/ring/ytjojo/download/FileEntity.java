package com.ring.ytjojo.download;

import java.util.List;
import com.ring.ytjojo.download.DaoSession;
import com.ring.ytjojo.helper.NotificationHelper;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table FILE_ENTITY.
 */
public class FileEntity {

    private Long id;
    private String url;
    private String path;
    private Long length;
    private Integer threads;
    private Boolean range;
    private Boolean isSucess;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient FileEntityDao myDao;

    private List<ThreadEntity> threadEntityList;

    public FileEntity() {
    }

    public FileEntity(Long id) {
        this.id = id;
    }

    public FileEntity(Long id, String url, String path, Long length, Integer threads, Boolean range, Boolean isSucess) {
        this.id = id;
        this.url = url;
        this.path = path;
        this.length = length;
        this.threads = threads;
        this.range = range;
        this.isSucess = isSucess;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFileEntityDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Boolean getRange() {
        return range;
    }

    public void setRange(Boolean range) {
        this.range = range;
    }

    public Boolean getIsSucess() {
        return isSucess;
    }

    public void setIsSucess(Boolean isSucess) {
        this.isSucess = isSucess;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<ThreadEntity> getThreadEntityList() {
        if (threadEntityList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreadEntityDao targetDao = daoSession.getThreadEntityDao();
            List<ThreadEntity> threadEntityListNew = targetDao._queryFileEntity_ThreadEntityList(id);
            synchronized (this) {
                if(threadEntityList == null) {
                    threadEntityList = threadEntityListNew;
                }
            }
        }
        return threadEntityList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetThreadEntityList() {
        threadEntityList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }
    
    
	private transient boolean again;

	private transient boolean isUpdate;

	private transient NotfiEntity notfi;

	private transient long loadedLength;

	private transient String real_url;


	private transient NotificationHelper helper;


    
    public boolean isAgain() {
		return again;
	}

	public void setAgain(boolean again) {
		this.again = again;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public NotfiEntity getNotfi() {
		return notfi;
	}

	public void setNotfi(NotfiEntity notfi) {
		this.notfi = notfi;
	}

	public long getLoadedLength() {
		return loadedLength;
	}

	public void setLoadedLength(long loadedLength) {
		this.loadedLength = loadedLength;
	}

	public String getReal_url() {
		return real_url;
	}

	public void setReal_url(String real_url) {
		this.real_url = real_url;
	}



	public NotificationHelper getHelper() {
		return helper;
	}

	public void setHelper(NotificationHelper helper) {
		this.helper = helper;
	}


	


}
