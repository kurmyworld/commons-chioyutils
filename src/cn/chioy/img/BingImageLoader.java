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
	private static URL mImgURL;
	private static SimpleLog mLog = new SimpleLog("BingImageLoader");
	public BingImageLoader() {
		super();
		mImgURL = getUrl();
	}

	/**
	 * @param url
	 *            �����Զ���ͼƬ��URL
	 */
	public void setImgURL(URL url) {
		mImgURL = url;
	}

	/**
	 * @return ��û���ֶ�����ImgURL�򷵻�Ĭ�ϵ�BingImageURL
	 */
	public URL getImgURL() {
		return mImgURL;
	}

	/**
	 * 
	 * @param response
	 *            ֱ����ͻ��˷���ͼƬ�ֽ�����
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
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param response
	 *            ֱ����ͻ��˷����ѻ����ͼƬ�ֽ�����
	 * @param file
	 *            ָ�������ļ�
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
	 *            �����ļ�
	 * @return ���ļ��Ƿ����
	 */
	public boolean hasCache(File file) {
		mLog.info("Checkking file is exists...");
		if(file.exists()){
			mLog.info("File exists!");
		}else{
			mLog.info("File is not exists!");
		}
		return file.exists();
	}

	/**
	 * 
	 * @param file
	 *            Ҫ������ļ�
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

	private URL getUrl() {
		URL _url = null;
		try {
			URL addr = new URL("http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1");
			String json_str = HttpHelper
					.pub(addr, HttpHelper.SUBMIT_METHOD_GET);
			String pattern = "url\":(.*)\\.jpg";
			// ���� Pattern ����
			Pattern r = Pattern.compile(pattern);
			// ���ڴ��� matcher ����
			Matcher m = r.matcher(json_str);
			String img_url = "";
			if (m.find()) {
				img_url = m.group(0).replace("url\":\"", "");
			}
			_url = new URL(img_url);
		} catch (MalformedURLException e) {

		}
		return _url;
	}

	private static byte[] getURLFileData() throws Exception {
		HttpURLConnection httpConn = (HttpURLConnection) mImgURL
				.openConnection();
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
