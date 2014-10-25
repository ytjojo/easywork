package com.ring.ytjojo.utill;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

public class FileUtile {
	public static final int BUFFER_SIZE = 1024;  

	public File getMemoryMappingFile(String path) {
		
		File file = new File(path);
		FileInputStream in;
		ByteArrayInputStream byteIn = null;
		try {
			in = new FileInputStream(file);
			FileChannel channel = in.getChannel();
			MappedByteBuffer buff = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					channel.size());

			byte[] b = new byte[1024];
			int len = (int) file.length();

			long begin = System.currentTimeMillis();

			for (int offset = 0; offset < len; offset += 1024) {

				if (len - offset > BUFFER_SIZE) {
					  buff.get(b);
				} else {
					buff.get(new byte[len - offset]);
				}
				byteIn.read(buff.array());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return file;
	}
	public byte[] getBitmapData(String path){
		byte[] data = null;  
        MappedByteBuffer buffer=null;  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {  
            RandomAccessFile file = new RandomAccessFile(path,"rw");
            FileChannel channel = file.getChannel();
            int length = (int) file.length();
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,channel.size() );  
           
            byte[] b = new byte[1024];
            long t1=System.currentTimeMillis(); 
            ByteBuffer bf;
        	for (int offset = 0; offset < length; offset += 1024) {

				if (length - offset > BUFFER_SIZE) {
					bf = buffer.get(b);
				} else {
					bf= buffer.get(new byte[length - offset]);
				}
				baos.write(bf.array());
			}
        	baos.flush();
        	data = baos.toByteArray();
            long t=System.currentTimeMillis()-t1;  
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }finally{
        	if(buffer != null){
        		try {
					
        			buffer.clear();
				} catch (Exception e2) {
					// TODO: handle exception
				}
        	}
        	if(baos != null){
        		
        		try {
					baos.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
        	}
        	
        }
        return data;
	}

}
