package com.ring.ytjojo.util.IO;

import android.database.Cursor;
import android.os.StatFs;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * @author Geek_Soledad (66704238@51uc.com)
 */
public class IOUtils {
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * Unconditionally close a <code>Closeable</code>.
	 * <p>
	 * Equivalent to {@link Closeable#close()}, except any exceptions will be
	 * ignored. This is typically used in finally blocks.
	 * <p>
	 * Example code:
	 *
	 * <pre>
	 * Closeable closeable = null;
	 * try {
	 * 	closeable = new FileReader(&quot;foo.txt&quot;);
	 * 	// process closeable
	 * 	closeable.close();
	 * } catch (Exception e) {
	 * 	// error handling
	 * } finally {
	 * 	IOUtils.closeQuietly(closeable);
	 * }
	 * </pre>
	 *
	 * @param closeable
	 *            the object to close, may be null or already closed
	 * @since Commons IO 2.0
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ioe) {
				// ignore
			}
		}
	}
	/**
	 * 
	 * 
	 * close cursor
	 * 
	 * @param cursor
	 */
	
	   public static void closeQuietly(Cursor cursor) {
	        if (cursor != null) {
	            try {
	                cursor.close();
	            } catch (Exception e) {
	            }
	        }
	    }

	/**
	 * 将输入流转换为字符串。默认采用UTF-8编码。
	 *
	 * @param in
	 *            输入流
	 * @return 转换之后的字符串。
	 * @throws IOException
	 */
	public static String inputStreamToString(InputStream in) throws IOException {
		return readFully(new InputStreamReader(in, UTF_8));
	}

	/**
	 * 将输入流转换为字符串。
	 *
	 * @param in
	 *            输入流
	 * @param charset
	 *            字符编码。
	 * @return 转换之后的字符串。
	 * @throws IOException
	 */
	public static String inputStreamToString(InputStream in, Charset charset) throws IOException {
		return readFully(new InputStreamReader(in, charset));
	}

	/**
	 * 以字符串类型返回{@code reader}的剩下的内容。
	 *
	 * @param reader
	 * @return 返回Reader对象剩下的内容。
	 * @throws IOException
	 */
	public static String readFully(Reader reader) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			char[] buffer = new char[1024];
			int count;
			while ((count = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, count);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * 获取文件路径空间大小
	 * 
	 * @param path
	 * @return 返回文件路径的空间大小
	 */
	public static long getUsableSpace(File path) {
		final StatFs sf = new StatFs(path.getPath());
		return (long) sf.getBlockSize() * (long) sf.getAvailableBlocks();
	}
}