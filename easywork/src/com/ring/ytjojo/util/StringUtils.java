package com.ring.ytjojo.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.annotation.SuppressLint;
import android.text.TextUtils;

/**
 * 字符串操作工具包
 */
@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class StringUtils {
	private final static Pattern EMAILER = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	public static boolean hasChinese(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).matches("[\\u4e00-\\u9fa5]+")) {
				return true;
			}
		}
		return false;

	}

	public static boolean isNumber(String str) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile("[0-9]*");
		java.util.regex.Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isEmptyOrNull(String input) {
		if (input == null || "".equals(input) || input.equals("null"))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check that the given CharSequence is neither {@code null} nor of length
	 * 0. Note: Will return {@code true} for a CharSequence that purely consists
	 * of whitespace.
	 * <p>
	 * 
	 * <pre class="code">
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * 
	 * @param str
	 *            the CharSequence to check (may be {@code null})
	 * @return {@code true} if the CharSequence is not null and has length
	 * @see #hasText(String)
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return EMAILER.matcher(email).matches();
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isSphone(String str) {
		if (str.length() != 6) {
			return false;
		}
		if (!isNumber(str)) {
			return false;
		}
		return true;
	}

	public static boolean isPhone(String str) {
		if (str.length() != 11) {
			return false;
		}
		if (!isNumber(str)) {
			return false;
		}
		return true;
	}

	
	/** 
     * 检查对象是否为数字型字符串。 
     */  
    public static boolean isNumeric(Object obj) {  
        if (obj == null) {  
            return false;  
        }  
        String str = obj.toString();  
        int sz = str.length();  
        for (int i = 0; i < sz; i++) {  
            if (!Character.isDigit(str.charAt(i))) {  
                return false;  
            }  
        }  
        return true;  
    }  
      
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	// ----------------------------------------------------------------------------
	/**
	 * 得到 全拼
	 * 
	 * @param src
	 * @return
	 */
	public static String getPingYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (java.lang.Character.toString(t1[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					t4 += t2[0];
				} else {
					t4 += java.lang.Character.toString(t1[i]);
				}
			}
			return t4;
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return t4;
	}

	/**
	 * 得到首字母
	 * 
	 * @param str
	 * @return
	 */
	public static String getHeadChar(String str) {

		String convert = "";
		char word = str.charAt(0);
		String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
		if (pinyinArray != null) {
			convert += pinyinArray[0].charAt(0);
		} else {
			convert += word;
		}
		return convert.toUpperCase();
	}

	/**
	 * 得到中文首字母缩写
	 * 
	 * @param str
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String getPinYinHeadChar(String str) {

		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert.toUpperCase();
	}

	public static boolean isEmptyNull(String s) {
		if (TextUtils.isEmpty(s)) {
			return true;
		}
		if (s.equals("null")) {
			s = "";
			return true;
		}
		return false;
	}

	public static String trimEmptyChar(String source) {
		String result = "";
		if (TextUtils.isEmpty(source)) {
			return result;
		}
		result = source.replaceAll("[\\t\\n\\r]", "");
		return result;
	}

	/**
	 * Trim <i>all</i> whitespace from the given String: leading, trailing, and
	 * in between characters.
	 * 
	 * @param str
	 *            the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimAllWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		int index = 0;
		while (sb.length() > index) {
			if (Character.isWhitespace(sb.charAt(index))) {
				sb.deleteCharAt(index);
			} else {
				index++;
			}
		}
		return sb.toString();
	}

	/**
	 * encoded in utf-8
	 * 
	 * <pre>
	 * utf8Encode(null)        =   null
	 * utf8Encode("")          =   "";
	 * utf8Encode("aa")        =   "aa";
	 * utf8Encode("鍟婂晩鍟婂晩")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
	 * </pre>
	 * 
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 *             if an error occurs
	 */
	public static String utf8Encode(String str) {
		if (!isEmpty(str) && str.getBytes().length != str.length()) {
			try {
				return URLEncoder.encode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"UnsupportedEncodingException occurred. ", e);
			}
		}
		return str;
	}
}