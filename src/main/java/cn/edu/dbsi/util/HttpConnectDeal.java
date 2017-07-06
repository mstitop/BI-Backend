package cn.edu.dbsi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;


/**
 * 类说明
 * 
 * @Description
 * @author huanqiang
 * @update 2016年9月22日 上午10:46:33
 */
@Service
public class HttpConnectDeal {
	/**
	 * 处理get请求.
	 * 
	 * @param url
	 *            请求路径
	 * @return json
	 */
	public static String get(String url) {
		// 实例化httpclient
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 实例化get方法
		HttpGet httpget = new HttpGet(url);
		// 请求结果
		CloseableHttpResponse response = null;
		String content = "";
		try {
			// 执行get方法
			response = httpclient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == 200) {
				content = EntityUtils.toString(response.getEntity(), "utf-8");

				// content =
				// inputStreamToString(response.getEntity().getContent());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 处理post请求.
	 * 
	 * @param url
	 *            请求路径
	 * @param params
	 *            参数
	 * @return json
	 */
	public static String post(String url, Map<String, String> params) {
		// 实例化httpClient
		CloseableHttpClient httpclient = HttpClients.createDefault();

		// 实例化post方法
		HttpPost httpPost = new HttpPost(url);

		// 处理参数
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		// 结果
		CloseableHttpResponse response = null;
		String content = "";
		try {
			// 提交的参数
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
			// 将参数给post方法
			httpPost.setEntity(uefEntity);

			// 执行post方法
			response = httpclient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				content = EntityUtils.toString(response.getEntity(), "utf-8");
				System.out.println(content);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String postJson(String url, JSONObject json) {
		// 实例化httpClient
		CloseableHttpClient httpclient = HttpClients.createDefault();

		// 实例化post方法
		HttpPost httpPost = new HttpPost(url);

		// 结果
		CloseableHttpResponse response = null;
		String content = "";
		try {
			// 提交的参数
			StringEntity stringEntity = new StringEntity(json.toString());
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/json");

			// 将参数给post方法
			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Content-type", "application/json");
			// 执行post方法
			response = httpclient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				content = EntityUtils.toString(response.getEntity(), "utf-8");
				System.out.println(content);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public String postStream(String urlStr) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConn.getOutputStream().write("theCityName=北京".getBytes("UTF-8"));
			httpConn.getOutputStream().flush();
			InputStreamReader is = new InputStreamReader(httpConn.getInputStream(), "utf-8");
			BufferedReader in = new BufferedReader(is);
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println("测试" + inputLine);
			}
			in.close();
			httpConn.disconnect();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	// public String postWebService(String url) {
	// CloseableHttpClient httpclient = HttpClients.createDefault();
	// String soapRequestData = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
	// "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
	// xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"
	// xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
	// "<soap12:Body>" +
	// " <SetData xmlns=\"http://tempuri.org/\">" +
	// " <dz>宁波海曙市</dz>" +
	// " <id>27000</id>" +
	// " <xx>27000</xx>" +
	// " <yy>13112</yy>" +
	// " <bui_id>4209</bui_id>" +
	// " <mc>天一数码</mc>" +
	// "</SetData>" +
	// " </soap12:Body>" +
	// "</soap12:Envelope>" ;
	//
	// HttpPost httppost = new HttpPost(url);
	// *//*把Soap请求数据添加到PostMethod*//*
	// //byte[] b = soapRequestData.getBytes("utf-8");
	// //InputStream is = new ByteArrayInputStream(b,0,b.length);
	// try {
	// HttpEntity re = new StringEntity(soapRequestData, "utf-8");
	// httppost.setHeader("Content-Type","application/soap+xml; charset=utf-8");
	// //httppost.setHeader("Content-Length",
	// String.valueOf(soapRequestData.length()));
	// httppost.setEntity(re);
	// HttpResponse response = httpclient.execute(httppost);
	// System.out.println(EntityUtils.toString(httppost.getEntity()));
	// System.out.println(response.getStatusLine());
	// System.out.println(EntityUtils.toString(response.getEntity()));
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ClientProtocolException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return "";
	// }
	//
	//

	// String inputStreamToString(InputStream is) throws IOException {
	// BufferedReader in = new BufferedReader(new InputStreamReader(is));
	// StringBuffer buffer = new StringBuffer();
	// String line = "";
	// while ((line = in.readLine()) != null) {
	// buffer.append(line);
	// }
	// return buffer.toString();
	// }

	// public static void main(String[] args) {
	// http hd = new HttpDeal();
	// hd.get("http://localhost:8080/springMVC/userType/getAll.do");
	// Map<String, String> map = new HashMap();
	// map.put("id", "1");
	// hd.post("http://localhost:8080/springMVC/menu/getChildren.do", map);
	// }
}
