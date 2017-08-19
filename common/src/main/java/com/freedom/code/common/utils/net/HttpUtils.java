package com.freedom.code.common.utils.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;

/*
 * http的常用工具,不采用http client 是因为在并发的get上，http client 不支持 
 */
public class HttpUtils {

	private final static Logger log = LoggerFactory.getLogger(HttpUtils.class);
	private int timeout = 10000; // 10秒
	private boolean useProxy = false;
	private Map<String, String> defaultHeaders = new HashMap<String, String>();

	public String getDirectHost() {
		return directHost;
	}

	public void setDirectHost(String directHost) {
		this.directHost = directHost;
	}

	private String host;
	private String proxyServerType = "http";
	private String directHost = "127.0.0.1,localhost";

	public String getProxyServerType() {
		return proxyServerType;
	}

	public void setProxyServerType(String proxyServerType) {
		this.proxyServerType = proxyServerType;
	}

	private String proxyServer;
	private int proxyPort;
	private String proxyUser;
	private String proxyPassword;
	private String defaultEncoding = "UTF-8";

	public void init() {
		if (useProxy) {
			log.info("proxyServerType={},proxyServer={},proxyPort={},proxyPassword={}",
					new Object[] { proxyServerType, proxyServer, String.valueOf(proxyPort), proxyUser, proxyPassword });
		}
		log.info("default headers={}", Objects.toStringHelper(defaultHeaders).toString());
		log.info("timeout={}", timeout);
		log.info("HttpUtils init");

	}

	// ---------------------------------------
	public void destory() {
		log.info("HttpUtils destory");
	}

	HostnameVerifier hv = new HostnameVerifier() {
		@Override
        public boolean verify(String urlHostName, SSLSession session) {
            return true;
        }
    };


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

	public HttpURLConnection openConnection(String u, Map<String, String> headerParameters) throws Exception {
		//https认证
		if(u.startsWith("https://")){
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		}
		HttpURLConnection httpConnection = null;
		if (!Strings.isNullOrEmpty(getHost()) && !u.startsWith("http://")) {
			// 如果是不是host开头的地址，加个默认地址
			u = getHost() + u;
		}
		URL url = new URL(u);
		log.debug("open url connection.url={}", u);
		// 默认的会处理302请求
		if (useProxy && directHost.indexOf(url.getHost()) < 0) {
			log.debug("using proxy.ProxyServer={},ProxyPort={}", proxyServer, proxyPort);
			SocketAddress addr = new InetSocketAddress(proxyServer, proxyPort);
			Proxy proxy = null;
			if ("http".equalsIgnoreCase(proxyServerType)) {
				proxy = new Proxy(Proxy.Type.HTTP, addr);
			}
			if ("socks".equalsIgnoreCase(proxyServerType)) {
				proxy = new Proxy(Proxy.Type.SOCKS, addr);
			}
			httpConnection = (HttpURLConnection) url.openConnection(proxy);
			if (!Strings.isNullOrEmpty(proxyUser) && Strings.isNullOrEmpty(proxyPassword)) {
				String string = proxyUser + ":" + proxyPassword;
				String encoded = new String(BaseEncoding.base64().encode(string.getBytes("UTF-8")));
				httpConnection.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
			}
		} else {
			httpConnection = (HttpURLConnection) url.openConnection();
		}

		httpConnection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded; charset=" + defaultEncoding);
		httpConnection.setUseCaches(true);
		httpConnection.setConnectTimeout(timeout);
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

	public HttpURLConnection openPostConnection(String u, String queryString, Map<String, String> headerParameters)
			throws Exception {
		//https认证
		if(u.startsWith("https://")){
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		}
		HttpURLConnection httpConnection = null;
		if (!Strings.isNullOrEmpty(getHost()) && !u.startsWith("http://")) {
			// 如果是不是host开头的地址，加个默认地址
			u = getHost() + u;
		}
		URL url = new URL(u);
		log.debug("open url connection.url={}", u);
		// 默认的会处理302请求
		if (useProxy && directHost.indexOf(url.getHost()) < 0) {
			log.debug("using proxy.ProxyServer={},ProxyPort={}", proxyServer, proxyPort);
			SocketAddress addr = new InetSocketAddress(proxyServer, proxyPort);
			Proxy proxy = null;
			if ("http".equalsIgnoreCase(proxyServerType)) {
				proxy = new Proxy(Proxy.Type.HTTP, addr);
			}
			if ("socks".equalsIgnoreCase(proxyServerType)) {
				proxy = new Proxy(Proxy.Type.SOCKS, addr);
			}
			httpConnection = (HttpURLConnection) url.openConnection(proxy);
			if (!Strings.isNullOrEmpty(proxyUser) && Strings.isNullOrEmpty(proxyPassword)) {
				String string = proxyUser + ":" + proxyPassword;
				String encoded = new String(BaseEncoding.base64().encode(string.getBytes("UTF-8")));
				httpConnection.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
			}
		} else {
			httpConnection = (HttpURLConnection) url.openConnection();
		}

		httpConnection.setConnectTimeout(timeout);
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

	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Map<String, String> getDefaultHeaders() {
		return defaultHeaders;
	}

	public void setDefaultHeaders(Map<String, String> defaultHeaders) {
		this.defaultHeaders = defaultHeaders;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	// 兼容以前程序
	public int getTimeOut() {
		return timeout;
	}

	public void setTimeOut(int timeout) {
		this.timeout = timeout;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}
}