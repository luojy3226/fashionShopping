package com.freedom.code.common.utils.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freedom.code.common.ValidateUtils;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

/**
 * http的常用工具,不采用http client 是因为在并发的get上，http client 不支持
 * @author luo
 *
 */
public class HttpTestUtils {
	private final static Logger log = LoggerFactory.getLogger(HttpTestUtils.class);
	private Map<String, String> defaultHeaders = new HashMap<String, String>();


	private String host;//主机
	private String proxyServerType = "http";//前缀
	private String directHost = "127.0.0.1,localhost";//直属主机
	private String defaultEncoding = "UTF-8";
	
	public String getProxyServerType() {
		return proxyServerType;
	}

	public void setProxyServerType(String proxyServerType) {
		this.proxyServerType = proxyServerType;
	}

	public void destory() {
		log.info("HttpUtils destory");
	}
	
	/**
	 * 匿名内部类，验证主机
	 */
	HostnameVerifier hv = new HostnameVerifier() {
		@Override
        public boolean verify(String urlHostName, SSLSession session) {
            return true;
        }
    };
    
    /**
     * 信任所有https请求
     * @throws Exception
     */
    public static void trustAllHttpsCertificates() throws Exception {
        // Create a trust manager that does not validate certificate chains:
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        //不验证
        sc.init(null, trustAllCerts, null);
        //验证证书有效性
//        ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());  
//        SSLContext.setDefault(ctx);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }

    public static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
    
    /**
     * 打开连接
     * @param u
     * @param headerParameters
     * @return
     * @throws Exception
     */
    public HttpURLConnection openConnection(String u, Map<String, String> headerParameters) throws Exception {
		//https认证
		if(u.startsWith("https://")){
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		}
		HttpURLConnection httpConnection = null;
		URL url = new URL(u);
		log.debug("open url connection.url={}", u);
		// 默认的会处理302请求
			httpConnection = (HttpURLConnection) url.openConnection();

		httpConnection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded; charset=" + defaultEncoding);
		httpConnection.setUseCaches(true);
		httpConnection.setConnectTimeout(60);
		Iterator<String> iteratorDefault = defaultHeaders.keySet().iterator();
		while (iteratorDefault.hasNext()) {
			String key = iteratorDefault.next();
			httpConnection.setRequestProperty(key, defaultHeaders.get(key));
			log.debug(key + "=" + headerParameters.get(key));
		}
		// -------------- 0920 设置 header
		if(headerParameters!=null){
			Iterator<String> iteratorHeader = headerParameters.keySet().iterator();
			while (iteratorHeader.hasNext()) {
				String key = iteratorHeader.next();
				httpConnection.setRequestProperty(key, headerParameters.get(key));
				log.debug(key + "=" + headerParameters.get(key));
			}
			if (headerParameters.get("Host") == null) {
				httpConnection.setRequestProperty("Host", url.getHost());
			}
		}

		int responseCode = httpConnection.getResponseCode();
		log.debug("server return:" + responseCode);
		Map<String, List<String>> headers = httpConnection.getHeaderFields();
		Iterator<String> iterator = headers.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = "";
			for (String v : headers.get(key)) {
				value = ";" + v;
			}
			log.debug(key + "=" + value);
		}
		if (responseCode != 200) {
			throw new IOException(responseCode + ":"
					+ (new String(ByteStreams.toByteArray(httpConnection.getErrorStream()), "UTF-8")));
		}
		return httpConnection;
	}
    
    /**
     * post连接
     * @param u
     * @param queryString
     * @param headerParameters
     * @return
     * @throws Exception
     */
    public HttpURLConnection openPostConnection(String u, String queryString, Map<String, String> headerParameters)
			throws Exception {
		//https认证
		if(u.startsWith("https://")){
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		}
		HttpURLConnection httpConnection = null;
		URL url = new URL(u);
		log.debug("open url connection.url={}", u);
			httpConnection = (HttpURLConnection) url.openConnection();

		httpConnection.setConnectTimeout(60);
		Iterator<String> iteratorDefault = defaultHeaders.keySet().iterator();
		while (iteratorDefault.hasNext()) {
			String key = iteratorDefault.next();
			httpConnection.setRequestProperty(key, defaultHeaders.get(key));
			log.debug("{}={}", key, defaultHeaders.get(key));
		}
		// -------------- 0920 设置 header
		if(headerParameters!=null){
			Iterator<String> iteratorHeader = headerParameters.keySet().iterator();
			while (iteratorHeader.hasNext()) {
				String key = iteratorHeader.next();
				httpConnection.setRequestProperty(key, headerParameters.get(key));
				log.debug("{}={}", key, headerParameters.get(key));
			}
			if (headerParameters.get("Host") == null) {
				httpConnection.setRequestProperty("Host", url.getHost());
			}
		}

		httpConnection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded; charset=" + defaultEncoding);
		httpConnection.setRequestMethod("POST");
		httpConnection.setDoOutput(true);
		// httpConnection.connect();
		if (!Strings.isNullOrEmpty(queryString)) {
			log.debug("queryString={}", queryString);
			// 写数据流
			OutputStream writer = httpConnection.getOutputStream();
			try {
				writer.write(queryString.getBytes(defaultEncoding));
			} finally {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			}
		}
		int responseCode = httpConnection.getResponseCode();
		log.debug("server return:" + responseCode);
		Map<String, List<String>> headers = httpConnection.getHeaderFields();
		Iterator<String> iterator = headers.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			String value = "";
			for (String v : headers.get(key)) {
				value = ";" + v;
			}
			log.debug(key + "=" + value);
		}
		if (responseCode != 200) {
			throw new IOException(responseCode + ":"
					+ (new String(ByteStreams.toByteArray(httpConnection.getErrorStream()), "UTF-8")));
		}
		return httpConnection;
	}
    
    public InputStream openStream(String u) throws Exception {
		HttpURLConnection httpConnection = openConnection(u, new HashMap<String, String>());
		return httpConnection.getInputStream();

	}

	public String getUrlContent(String u) throws Exception {
		return getUrlContent(u, null);
	}

	public String getUrlContent(String u, Map<?,?> parameters) throws Exception {
		return getUrlContent(u, parameters, null);
	}
	
	/**
	 * 获得url的内容
	 * @param u
	 * @param parameters
	 * @param headerParameters
	 * @return
	 * @throws Exception
	 */
	public String getUrlContent(String u, Map<?,?> parameters, Map<String, String> headerParameters) throws Exception {
		String ret = "";
		HttpURLConnection httpConnection = null;
		try {
			String queryString = map2string(parameters);
			String requrl = u;
			if(queryString.length()>0){
				requrl = u+"?"+queryString;
			}
			httpConnection = openConnection(requrl, headerParameters);
			// 读数据流,处理encoding
			String encoding = defaultEncoding;
			if (httpConnection.getContentType() != null && httpConnection.getContentType().indexOf("charset=") >= 0) {
				encoding = httpConnection.getContentType()
						.substring(httpConnection.getContentType().indexOf("charset=") + 8);
			}
			log.debug("ContentEncoding={}", encoding);
			if ("gzip".equals(httpConnection.getContentEncoding())) {
				log.debug("Content is GZIP");
				GZIPInputStream inStream = new GZIPInputStream(httpConnection.getInputStream());
				ret = new String(ByteStreams.toByteArray(inStream), encoding);
				inStream.close();
				inStream = null;
			} else {
				// 读数据流,处理encoding
				ret = new String(ByteStreams.toByteArray(httpConnection.getInputStream()), encoding);
			}
			log.debug("result=:{}", ret);
		} catch (IOException e) {
			log.error("can't open url:{},url={}", e.getMessage(), u);
			throw e;
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		return ret;
	}
	
	public String postUrlContent(String u, String queryString) throws Exception {
		return postUrlContent(u, queryString, new HashMap<String, String>());
	}

	public String postUrlContent(String u, Map<?,?> parameters) throws Exception {
		return postUrlContent(u, map2string(parameters), new HashMap<String, String>());
	}
	
	public String postUrlContent(String u, Map<?,?> parameters, Map<String, String> headerParameters) throws Exception {
		return postUrlContent(u, map2string(parameters), headerParameters);
	}
	
	/**
	 * 
	 * @param u
	 *            url地址
	 * @param queryString
	 *            参数
	 * @param filter
	 *            过滤器
	 * @param headerParameters
	 *            头
	 * @return
	 * @throws java.lang.Exception
	 */
	public String postUrlContent(String u, String queryString, Map<String, String> headerParameters) throws Exception {
		String ret = "";
		HttpURLConnection httpConnection = null;
		try {
			httpConnection = openPostConnection(u, queryString, headerParameters);
			String encoding = defaultEncoding;
			if (httpConnection.getContentType() != null && httpConnection.getContentType().indexOf("charset=") >= 0) {
				encoding = httpConnection.getContentType()
						.substring(httpConnection.getContentType().indexOf("charset=") + 8);
			}
			log.debug("ContentEncoding={}", encoding);
			if ("gzip".equals(httpConnection.getContentEncoding())) {
				log.debug("Content is GZIP");
				GZIPInputStream inStream = new GZIPInputStream(httpConnection.getInputStream());
				ret = new String(ByteStreams.toByteArray(inStream), encoding);
				inStream.close();
				inStream = null;
			} else {
				// 读数据流,处理encoding
				ret = new String(ByteStreams.toByteArray(httpConnection.getInputStream()), encoding);
			}
			log.debug("result is {}", ret);
		} catch (IOException e) {
			log.error("can't open url:{},url={}", e.getMessage(), u);
			throw e;
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		return ret;
	}

	public String map2string(Map<?,?> parameters) throws Exception {
		StringBuffer params=new StringBuffer();
		if(parameters!=null){
			for (Iterator<?> iter=parameters.entrySet().iterator();iter.hasNext();) {
				Entry<?, ?> element=(Entry<?, ?>) iter.next();
				params.append(element.getKey().toString());
				params.append("=");
				if(!ValidateUtils.isNull(element.getValue())){
					params.append(URLEncoder.encode(element.getValue().toString(),defaultEncoding));
				}
				params.append("&");
			}
		}

		if(params.length()>0){
			params=params.deleteCharAt(params.length()-1);
		}
		return params.toString();
	}
	
	@SuppressWarnings("unused")
    private static class DefaultTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
	
	public static void main(String[] args) throws Exception {
        String url = "https://api.netease.im/call/ecp/startcall.action";
        String para = "callerAcc=test&caller=13022563860&callee=15556906631&maxDur=60";
        HttpTestUtils http = new HttpTestUtils();
        
        Map<String, String> headerParameters = new HashMap<String, String>();
        headerParameters.put("AppKey", "25a43559b6980a5fdb2d23eb33f1adca");
        String curTime = new Date().getTime()+"";
        headerParameters.put("CurTime", curTime);
        String nonce = "4tgggergigwow323t23t";
        headerParameters.put("Nonce", nonce);
        
        headerParameters.put("CheckSum", CheckSumBuilder.getCheckSum("c83011f2cac7",nonce,curTime));
        
        headerParameters.put("charset", "utf-8");
        headerParameters.put("Content-Type", "application/x-www-form-urlencoded");
        String sr=http.postUrlContent(url,para,headerParameters);
        System.out.println(sr);
    }
}

class CheckSumBuilder {
    // 计算并获取CheckSum
    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        return encode("sha1", appSecret + nonce + curTime);
    }

    // 计算并获取md5值
    public static String getMD5(String requestBody) {
        return encode("md5", requestBody);
    }

    private static String encode(String algorithm, String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest messageDigest
                    = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
}
