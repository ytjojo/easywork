package com.ring.ytjojo.util.IO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;


/**
 * 对象文件类。该类用于将对象序列化写入文件，及从文件读取出序列化的对象。
 * 
 * @author Geek_Soledad (66704238@51uc.com)
 */
public class ObjectFileUtils {
	/**
	 * 写入对象。
	 * 
	 * @param object
	 *            要写入的对象
	 * @param path
	 *            存储写入对象的文件的路径
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void writeObject(final Serializable object, final String path)
			throws FileNotFoundException, IOException {
		writeObject(object, new FileOutputStream(path));
	}

	/**
	 * 写入对象文件。
	 * 
	 * @param object
	 *            要写入的对象
	 * @param os
	 *            写入对象文件的输出流
	 * @throws IOException
	 */
	public static void writeObject(final Serializable object,
			final OutputStream os) throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(os);
			oos.writeObject(object);
		} finally {
			IOUtils.closeQuietly(oos);
			IOUtils.closeQuietly(os);
		}
	}

	/**
	 * 读出对象
	 * 
	 * @param path
	 *            要读取的对象文件的路径
	 * @return 返回读出的对象
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws StreamCorruptedException
	 * @throws ClassNotFoundException
	 */
	public static Object readObject(String path)
			throws StreamCorruptedException, FileNotFoundException,
			IOException, ClassNotFoundException {
		return readObject(new FileInputStream(path));
	}

	/**
	 * 读出对象。
	 * 
	 * @param is
	 *            对象文件的输入流
	 * @return 读出的对象
	 * @throws StreamCorruptedException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object readObject(InputStream is)
			throws StreamCorruptedException, IOException, ClassNotFoundException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(is);
			return ois.readObject();
		} finally {
			IOUtils.closeQuietly(ois);
			IOUtils.closeQuietly(is);
		}
	}
}