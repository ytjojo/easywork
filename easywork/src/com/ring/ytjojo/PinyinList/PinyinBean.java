package com.ring.ytjojo.PinyinList;

public  class PinyinBean implements Comparable<PinyinBean> {
	String name;
	String pinyin;
	boolean isSection;
	public String getPinyin() {
		// TODO Auto-generated method stub
		return pinyin;
	}

	public String getCompareableFeild() {
		// TODO Auto-generated method stub
		return name;
	}

	public void setPinyin(String letter) {
		// TODO Auto-generated method stub
		this.pinyin = letter;
	}
	
	

	public boolean isSection() {
		return isSection;
	}

	public void setSection(boolean isSection) {
		this.isSection = isSection;
	}

	@Override
	public int compareTo(PinyinBean another) {
	
		return this.pinyin.compareTo(another.pinyin);
		
	}
	

}
