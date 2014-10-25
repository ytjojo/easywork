package com.ring.ytjojo.model;

import java.util.Comparator;

public class SongCompare implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		 Song t1=(Song) o1;
		 Song t2=(Song) o2;
	        int result=t1.getDuration()>t2.getDuration() ? 1: (t1.getDuration() == t2.getDuration() ? 0 : -1);
	        return result=result == 0?( t1.duration<t2.duration ? 1 : -1):result;
	}
	 
	
}
