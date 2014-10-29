package com.ring.ytjojo.util;

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

import android.os.StatFs;

public class FileUtil {
	public static final int BUFFER_SIZE = 1024;

	public byte[] getMemoryMappingFile(String path) {

		File file = new File(path);
		FileInputStream in;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			in = new FileInputStream(file);
			FileChannel channel = in.getChannel();
			MappedByteBuffer buff = channel.map(FileChannel.MapMode.READ_ONLY,
					0, channel.size());

			byte[] b = new byte[1024];
			int len = (int) file.length();

			long begin = System.currentTimeMillis();

			for (int offset = 0; offset < len; offset += 1024) {

				if (len - offset > BUFFER_SIZE) {
					buff.get(b);
				} else {
					buff.get(new byte[len - offset]);
				}
				baos.write(buff.array());
			}

			return baos.toByteArray();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			
		}

		return null;
	}

	/**
	 * 检查给定的路径有多大的可用空间。
	 * 
	 * @param path
	 *            给定的路径。
	 * @return 返回给定路径的可用空间大小。
	 */
	public static long getUsableSpace(File path) {
		final StatFs stats = new StatFs(path.getPath());
		return stats.getBlockCount() * stats.getAvailableBlocks();
	}

}
