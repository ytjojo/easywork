package com.ring.ytjojo.model;

import android.support.v4.app.Fragment;

public class MenuItem {
	public int imgResource;
	public String title;
	public int postion;
	public Fragment fragment;
	public int getImgResource() {
		return imgResource;
	}
	public void setImgResource(int imgResource) {
		this.imgResource = imgResource;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getPostion() {
		return postion;
	}
	public void setPostion(int postion) {
		this.postion = postion;
	}
	
}
