package cn.chioy.img;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.impl.SimpleLog;

import cn.chioy.http.HttpHelper;

/**
 * 
 * @author Chioy
 * 
 */
public class BingImageLoader {
	private static String mImgURL;
	private static SimpleLog mLog = new SimpleLog("BingImageLoader");

	public BingImageLoader() {
		super();
		mImgURL = getUrl();
	}

	/**
	 * @param url
	 *            自定义URL地址来修复图片URL
	 */
	public void fixImgURL(String url) {
		mImgURL = url;
	}

	/**
	 * @return 如果没有调用fixImgURL则返回默认解析出的URL地址，否则返回修复后的URL地址
	 */
	public String getImgURL() {
		return mImgURL;
	}

	/**
	 * 
	 * @param response
	 *            用HttpServletResponse来直接向客户端输出图片流
	 * @since JavaEE6.0
	 * 
	 */
	public void putPicFromUrl(HttpServletResponse response) {
		try {
			byte[] data = getURLFileData();
			response.setContentLength(data.length);
			response.setContentType("image/jpeg");
			OutputStream out = response.getOutputStream();
			out.write(data);
			mLog.info("Putting Image From URL to Response Client...");
		} catch (Exception e) {
			mLog.error("Some Exception");
		}
	}

	/**
	 * 
	 * @param response
	 *            用HttpServletResponse来直接向客户端输出图片流
	 * @param file
	 *            图片缓存文件
	 */
	public void putPicFromCache(HttpServletResponse response, File file) {
		try {
			if (!hasCache(file))
				this.cacheTo(file);
			byte[] data = getCacheFileData(file);
			response.setContentLength(data.length);
			response.setContentType("image/jpeg");
			OutputStream out = response.getOutputStream();
			out.write(data);
			mLog.info("Putting Image From Cache to Response Client...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param file
	 *            图片缓存文件
	 * @return 是否存在该文件
	 */
	public boolean hasCache(File file) {
		mLog.info("Checkking file is exists...");
		if (file.exists()) {
			mLog.info("File exists!");
			return true;
		} else {
			mLog.info("File is not exists!");
			return false;
		}
	}

	/**
	 * 
	 * @param file
	 *           图片缓存文件
	 */
	public void cacheTo(File file) {
		try {
			byte[] data = getURLFileData();
			FileOutputStream fos = new FileOutputStream(file);
			mLog.info("Cacheing to file...");
			fos.write(data);
			fos.close();
			mLog.info("Has Cached!");
		} catch (Exception e) {
			mLog.error("Cache false!");
		}

	}

	private String getUrl() {
		URL addr;
		try {
			addr = new URL(
					"http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1");
			String _str = HttpHelper
					.pub(addr, HttpHelper.SUBMIT_METHOD_GET);
			String regex = "url\":(.*)\\.jpg";
			
			// 创建一个使用正则表达式规则regex的正则
			Pattern pattern = Pattern.compile(regex);
			
			//用该正则去匹配待正则文本_str
			Matcher m = pattern.matcher(_str);
			
			String img_url = "";
			if (m.find()) {
				img_url = m.group(0).replace("url\":\"", "");
			}
			return img_url;
		} catch (MalformedURLException e) {
			return "";
		}

	}

	private static byte[] getURLFileData() throws Exception {
		URL url = new URL(mImgURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.connect();
		InputStream cin = httpConn.getInputStream();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = cin.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		cin.close();
		byte[] fileData = outStream.toByteArray();
		outStream.close();
		return fileData;
	}

	private static byte[] getCacheFileData(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = fis.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		fis.close();
		byte[] fileData = outputStream.toByteArray();
		return fileData;
	}

}
