package cn.chioy.img;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import cn.chioy.http.HttpHelper;

public class BingImageLoader {
	private static URL mImgURL;

	public BingImageLoader() {
		super();
		mImgURL = getUrl();
	}

	public void setImgURL(URL url) {
		mImgURL = url;
	}

	public URL getImgURL() {
		return mImgURL;
	}

	public void putPicFromUrl(HttpServletResponse response) {
		System.out.println("putting from url...");
		try {
			byte[] data = getURLFileData();
			response.setContentLength(data.length);
			response.setContentType("image/jpeg");
			OutputStream out = response.getOutputStream();
			out.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void putPicFromCache(HttpServletResponse response, File file) {
		try {
			if (!hasCache(file))
				this.cacheTo(file);
			byte[] data = getCacheFileData(file);
			response.setContentLength(data.length);
			response.setContentType("image/jpeg");
			OutputStream out = response.getOutputStream();
			out.write(data);
			System.out.println("putting from cache...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean hasCache(File file) {
		System.out.println("Checking has cached...");
		return file.exists();
	}

	public void cacheTo(File file) {
		System.out.println("Cacheing...");
		try {
			byte[] data = getURLFileData();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Cached!");
	}

	private URL getUrl() {
		URL u = null;
		try {
			System.out.println("getting image url...");
			String addr = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
			String json_str = HttpHelper
					.pub(addr, HttpHelper.SUBMIT_METHOD_GET);
			String pattern = "url\":(.*)\\.jpg";
			// 创建 Pattern 对象
			Pattern r = Pattern.compile(pattern);
			// 现在创建 matcher 对象
			Matcher m = r.matcher(json_str);
			String img_url = "";
			if (m.find()) {
				img_url = m.group(0).replace("url\":\"", "");
			}
			u = new URL(img_url);
		} catch (MalformedURLException e) {

		}
		return u;
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
