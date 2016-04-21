package cn.chioy.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chioy on 16-3-29.
 */
public class HttpHelper {
	public static final String SUBMIT_METHOD_POST = "POST";
	public static final String SUBMIT_METHOD_GET = "GET";

	public static String pub(URL url, String method) {
		StringBuilder response = new StringBuilder();
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);
			InputStream inputStream = connection.getInputStream();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (connection != null) {
			connection.disconnect();
		}

		return response.toString();
	}
}
