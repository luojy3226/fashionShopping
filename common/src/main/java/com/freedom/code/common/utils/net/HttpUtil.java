package com.freedom.code.common.utils.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.freedom.code.common.ValidateUtils;
import com.freedom.code.common.exception.ServiceException;
import com.freedom.code.common.utils.encryption.Credentail;
import com.freedom.code.common.utils.encryption.Token;
import com.freedom.code.common.utils.json.JsonUtils;

/**
 * http工具类
 * @author luo
 *
 */
public class HttpUtil {
	private final static Log LOG = LogFactory.getLog(HttpUtil.class);

    /**
     * 请求头名称：CDN
     */
    public static final String HEADER_NAME_CDN = "Cdn-Src-Ip";

    /**
     * 请求头名称：X-Real
     */
    public static final String HEADER_NAME_X_REAL = "X-Real-IP";

    /**
     * 请求头名称：X-Forwarded-For
     */
    public static final String HEADER_NAME_X_FORWARDED_FOR = "X-Forwarded-For";

    private static final String STRING_BLANK = "";

    private static final String API_URL = "http://api.t.sina.com.cn/short_url/shorten.json";

    private static final String APPKEY = "1681459862";

    private static final String AK = "wvERGNTtXtY8nYh4gxU1IzWz";
    
    /**
     * 功能描述: 取得客户端的IP地址
     * 
     * @param request：http请求对象
     * @return IP地址
     */
    public static String getClientIP(HttpServletRequest request) {
        // 客户端IP
        String clientIP = "";

        // 检查Cdn-Src-Ip：如果取得了CDN的IP，直接返回
        clientIP = request.getHeader(HEADER_NAME_CDN);
        if (!isEmpty(clientIP)) {
            return clientIP;
        }

        // 检查X-Real-IP：如果取得了X-Real的IP，直接返回
        clientIP = request.getHeader(HEADER_NAME_X_REAL);
        if (!isEmpty(clientIP)) {
            return clientIP;
        }

        /*
         * 1.经过代理或者代理服务器以后，由于在客户端和服务之间增加了中间层， 因此服务器无法直接拿到客户端的IP，服务器端应用也无法直接通过转发请求的地址返回 给客户端。
         * 2.但是在转发请求的HTTP头信息中，增加了X－FORWARDED－FOR信息， 用以跟踪原有的客户端IP地址和原来客户端请求的服务器地址。
         * 3.如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP， 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
         */
        String forwordIPs = request.getHeader(HEADER_NAME_X_FORWARDED_FOR);
        if (!isEmpty(forwordIPs)) {
            // 存在多个IP时取第一个
            clientIP = forwordIPs.split(",")[0].trim();
            if (!isEmpty(clientIP)) {
                return clientIP;
            }
        }

        // 如果都没取到，直接取远程地址
        if (isEmpty(clientIP)) {
            clientIP = request.getRemoteAddr();
        }
        return clientIP;
    }
    
    /**
     * 功能描述: 根据KEY值从请求对象中读取cookie值
     * 
     * @param request：请求对象
     * @param key：cookie键
     * @return cookie值
     */
    public static String getCookieValue(HttpServletRequest request, String key) {
        String value = null;
        if (!isEmpty(key)) {
            Cookie[] cookie = request.getCookies();
            if (cookie != null && cookie.length > 0) {
                for (int i = 0; i < cookie.length; i++) {
                    if (((Cookie) cookie[i]).getName().equalsIgnoreCase(key)) {
                        value = ((Cookie) cookie[i]).getValue();
                        break;
                    }
                }
            }
        }
        return value;
    }
    
    /**
     * 功能描述: 判断字符串是否为null值或空串
     * 
     * @param str：字符串
     * @return 是否为空
     */
    private static boolean isEmpty(String str) {
        boolean isEmpty = false;
        if (str == null || STRING_BLANK.equals(str.trim())) {
            isEmpty = true;
        }
        return isEmpty;
    }
    
    /**
     * 处理request，用于服务端跨域请求等场景
     * 
     * @param req
     * @param resp
     * @param target
     * @throws MalformedURLException
     * @throws IOException
     */
    public static void process(HttpServletRequest req,
            HttpServletResponse resp, String redirectUrl)
            throws MalformedURLException, IOException {
        // 取得连接
        HttpURLConnection huc = (HttpURLConnection) new URL(redirectUrl)
                .openConnection();
        // 设置连接属性
        huc.setDoOutput(true);
        huc.setRequestMethod("POST");
        huc.setUseCaches(false);
        huc.setInstanceFollowRedirects(true);
        huc.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        huc.connect();

        // 往目标servlet中提供参数
        /*
         * OutputStream targetOS = huc.getOutputStream(); targetOS.write(""); targetOS.flush(); targetOS.close();
         */

        // 取得页面输出,并设置页面编码及缓存设置
        resp.setContentType(huc.getContentType());
        resp.setHeader("Cache-Control", huc.getHeaderField("Cache-Control"));
        resp.setHeader("Pragma", huc.getHeaderField("Pragma"));
        resp.setHeader("Expires", huc.getHeaderField("Expires"));
        OutputStream os = resp.getOutputStream();

        // 将目标servlet的输入流直接往页面输出
        InputStream targetIS = huc.getInputStream();
        int r;
        while ((r = targetIS.read()) != -1) {
            os.write(r);
        }
        targetIS.close();
        os.flush();
        os.close();

        huc.disconnect();
    }
    
    /**
     * 获取手机命名空间
     * @param mobile
     * @return
     * @throws Exception
     */
    public static String getMobileSegment(String mobile) throws Exception {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(
                "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel="
                        + mobile);

        HttpResponse response = null;

        try {
            response = httpclient.execute(httpGet);

            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return null;
    }
    
    /**
     * 处理request，获取城市信息
     * 
     * @param req
     * @param resp
     * @param target
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String getCityNameByRequest(HttpServletRequest req)
            throws Exception {
        // 获取IP
        String ip = getClientIP(req);
        String city=null;

		try {
			String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=";
			if(ip!=null){
				url+=ip;
			}
			HttpUtils httpUtils = new HttpUtils();
			String content = httpUtils.getUrlContent(url);
			//如果没有获取到城市，则返回南京
			if(ValidateUtils.isNull(content)||ValidateUtils.isInt(content)){
				return "南京市";
			}
			Map<String, Object> positionMap = JsonUtils.convertJsonToMap(content);
			//获取城市
			if(positionMap.containsKey("city")){
				Object object=positionMap.get("city");
				if(object!=null){
					city=(String) object;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return city;
    }
    
    /**
     * 百度API,获取相应地址经纬度
     * 
     * @param req
     * @param resp
     * @param target
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static Object[] getLatLngByAddress(String city, String address)
            throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(
                "http://api.map.baidu.com/geocoder/v2/?ak=" + AK
                        + "&output=json&address=" + address + "&city=" + city);

        HttpResponse response = null;

        try {
            response = httpclient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            String body = EntityUtils.toString(entity);
            Map<String, Object> map = JsonUtils.convertJsonToMap(body);

            Object[] res = null;
            // status是成功
            if ((Integer) map.get("status") == 0) {

                Map<String, Object> object = (Map<String, Object>) map
                        .get("result");

                if (object == null) {
                    return res;
                }

                Map<String, Object> locationMap = (Map<String, Object>) object
                        .get("location");
                res = new Object[2];
                res[0] = locationMap.get("lat");
                res[1] = locationMap.get("lng");
            }
            return res;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return null;
    }
    
    /**
     * 获取谷歌地图地址
     * @param address
     * @return
     * @throws Exception
     */
    public static Map<String, Double> getGeoInfoByAddress(String address)
            throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        address = URLEncoder.encode(address);
        HttpGet httpGet = new HttpGet(
                "http://api.map.baidu.com/geocoder/v2/?ak=" + AK
                        + "&output=json&address=" + address);

        HttpResponse response = null;
        
        Map<String,Double> latlng = new HashMap<String, Double>();        

        try {
            response = httpclient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            String body = EntityUtils.toString(entity);
            Map<String, Object> map = JsonUtils.convertJsonToMap(body);
 
            // status是成功
            if ((Integer) map.get("status") == 0) {

                @SuppressWarnings("unchecked")
				Map<String, Object> object = (Map<String, Object>) map
                        .get("result");

                if (object == null) {
                    
                } else {
                	@SuppressWarnings("unchecked")
					Map<String, Object> locationMap = (Map<String, Object>) object
                            .get("location");  
                    latlng.put("lat", Double.valueOf(locationMap.get("lat").toString()));
                    latlng.put("lng", Double.valueOf(locationMap.get("lng").toString()));
                }

                
            } 
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return latlng;
         
    }
    
    /**
     * 获取谷歌经纬度
     * @param lat
     * @param lng
     * @return
     * @throws Exception
     */
    public static Map<String, String> reGeoInfoByLatLng(String lat,String lng)
            throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(
                "http://api.map.baidu.com/geocoder/v2/?ak=" + AK
                        + "&output=json&location=" + lat+"," + lng);

        HttpResponse response = null;
        
        Map<String,String> pca = new HashMap<String, String>();        

        try {
            response = httpclient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            String body = EntityUtils.toString(entity);
            Map<String, Object> map = JsonUtils.convertJsonToMap(body);
 
            // status是成功
            if ((Integer) map.get("status") == 0) {

                @SuppressWarnings("unchecked")
				Map<String, Object> object = (Map<String, Object>) map
                        .get("result");

                if (object == null) {
                    
                } else {
                	@SuppressWarnings("unchecked")
					Map<String, Object> addressMap = (Map<String, Object>) object
                            .get("addressComponent"); 
                    pca.put("provinceName", addressMap.get("province").toString());
                    pca.put("cityName", addressMap.get("city").toString());
                    pca.put("areaName", addressMap.get("district").toString());
                }

                
            } 
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return pca;
         
    }
    
    /**
     * 获取企业上传的营业执照是否成功
     * @param imgUrl
     * @return
     */
    public static boolean getImagesExist(String imgUrl) {
        boolean result = false;
        long begin = System.currentTimeMillis();
        URL url = null;
        HttpURLConnection httpconn = null;
        try {
            int resCode = 0;
            url = new URL(imgUrl);
            httpconn = (HttpURLConnection) url.openConnection();
            httpconn.setReadTimeout(10000);
            httpconn.setConnectTimeout(10000);
            resCode = httpconn.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        } finally {
            if (httpconn != null) {
                httpconn.disconnect();
                httpconn = null;
            }
            if (url != null) {
                url = null;
            }
        }
        LOG.info("getImagesExist : " + imgUrl + " "
                + (System.currentTimeMillis() - begin));
        return result;
    }
    /**
     * 百度API,获取500米范围里的公交车信息
     * 
     * @param req
     * @param resp
     * @param target
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String getBusInfoByLatLng(Object lat, Object lng)
            throws Exception {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(
                "http://api.map.baidu.com/place/v2/search?ak="
                        + AK
                        + "&output=json&query=%E5%85%AC%E4%BA%A4&page_size=500&page_num=0&scope=2&location="
                        + lat + "," + lng + "&radius=500");

        HttpResponse response = null;

        try {
            response = httpclient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            return EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return null;
    }

    /**
     * 百度API,获取1000米范围里的地铁信息
     * 
     * @param req
     * @param resp
     * @param target
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String getMatroInfoByLatLng(Object lat, Object lng)
            throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(
                "http://api.map.baidu.com/place/v2/search?ak="
                        + AK
                        + "&output=json&query=%E5%9C%B0%E9%93%81&page_size=500&page_num=0&scope=2&location="
                        + lat + "," + lng + "&radius=1000");

        HttpResponse response = null;

        try {
            response = httpclient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            return EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        return null;
    }
    
    /**

     * 
     *  Created on 2014年9月19日 
     * <p>Description:返回短链接</p>
     * @author:崔海英
     * @param url
     * @return
     */
  public static String getShortUrl(String url) {
      if (ValidateUtils.isNull(url)) {
          throw new ServiceException("url不能为空！");
      }
      long begin = System.currentTimeMillis();
      DefaultHttpClient httpclient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(API_URL + "?source=" + APPKEY
              + "&url_long=" + url);
      HttpEntity entity = null;
      HttpResponse response;
      String shortUrl = null;
      try {
          response = httpclient.execute(httpGet);
          entity = response.getEntity();
          if (entity == null) {
              throw new ServiceException("请求失败！");
          }
          shortUrl = EntityUtils.toString(entity);
          if (shortUrl == null) {
              throw new ServiceException("生成短链接为空！");
          }
          Map<String, Object> map = JsonUtils.convertJsonToMap(shortUrl
                  .replace("[", "").replace("]", ""));
          shortUrl = (String) map.get("url_short");
      } catch (ClientProtocolException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          httpclient.getConnectionManager().shutdown();
      }
      LOG.info("getShortUrlTime : " + (System.currentTimeMillis() - begin));
      return shortUrl;

  }

  /**
   * Create a httpClient instance
   * 
   * @param isSSL
   * @return HttpClient instance
   */
  public static HttpClient getClient(boolean isSSL) {

      HttpClient httpClient = new DefaultHttpClient();
      if (isSSL) {
          X509TrustManager xtm = new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}
          };

          try {
              SSLContext ctx = SSLContext.getInstance("TLS");

              ctx.init(null, new TrustManager[] { xtm }, null);

              SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);

              httpClient.getConnectionManager().getSchemeRegistry();

          } catch (Exception e) {
              throw new RuntimeException();
          }
      }

      return httpClient;
  }
  
  /**
   * Send SSL Request
   * 
   * @param reqURL
   * @param token
   * @param str
   * @return
   */
  public static String sendHTTPRequest(URL url, Credentail credentail,
          Object dataBody, String method) {

      HttpClient httpClient = getClient(true);

      String responseContent = null;

      try {

          HttpResponse response = null;
          LOG.info("url===" + url.toURI());
          if (method.equals(HTTPMethod.METHOD_POST)) {
              HttpPost httpPost = new HttpPost(url.toURI());

              if (credentail != null) {
                  Token.applyAuthentication(httpPost, credentail);
              }
              httpPost.setEntity(new StringEntity(dataBody.toString(),
                      "UTF-8"));
              LOG.info("dataBody.toString()==" + dataBody.toString());
              response = httpClient.execute(httpPost);
          } else if (method.equals(HTTPMethod.METHOD_PUT)) {
              HttpPut httpPut = new HttpPut(url.toURI());
              if (credentail != null) {
                  Token.applyAuthentication(httpPut, credentail);
              }
              httpPut.setEntity(new StringEntity(dataBody.toString(), "UTF-8"));

              response = httpClient.execute(httpPut);
          } else if (method.equals(HTTPMethod.METHOD_GET)) {

              HttpGet httpGet = new HttpGet(url.toURI());
              if (credentail != null) {
                  Token.applyAuthentication(httpGet, credentail);
              }

              response = httpClient.execute(httpGet);
          } else if (method.equals(HTTPMethod.METHOD_DELETE)) {
              HttpDelete httpDelete = new HttpDelete(url.toURI());

              if (credentail != null) {
                  Token.applyAuthentication(httpDelete, credentail);
              }

              response = httpClient.execute(httpDelete);
          }

          HttpEntity entity = response.getEntity();

          if (null != entity) {
              responseContent = EntityUtils.toString(entity, "UTF-8");
          }
      } catch (Exception e) {
      	LOG.error(e.getMessage(), e);
          throw new RuntimeException();
      } finally {
          httpClient.getConnectionManager().shutdown();
      }
      return responseContent;
  }

  public static String sendRequest(String url, Map<String, String> params)
          throws ClientProtocolException, IOException {
      HttpPost httpost = new HttpPost(url);
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();

      Set<String> keySet = params.keySet();
      for (String key : keySet) {
          nvps.add(new BasicNameValuePair(key, params.get(key)));
      }

      HttpClient httpClient = getClient(true);

      String responseContent = null;

      try {

          HttpResponse response = null;
          LOG.info("set utf-8 form entity to httppost");
          httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
          //DefaultHttpClient httpclient = new DefaultHttpClient();
          response = httpClient.execute(httpost);
          HttpEntity entity = response.getEntity();
          if (null != entity) {
              responseContent = EntityUtils.toString(entity, "UTF-8");
          }
      } catch (Exception e) {
      	LOG.error(e.getMessage(), e);
          throw new RuntimeException();
      } finally {
          httpClient.getConnectionManager().shutdown();
      }
      return responseContent;
  }

  public static void main(String[] args) {
		String ip="baidu.com";
		String city=null;

		try {
			String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip="+ip;
		
			HttpUtils httpUtils = new HttpUtils();
			String content = httpUtils.getUrlContent(url);
			Map<String, Object> positionMap = JsonUtils.convertJsonToMap(content);
			
			if(positionMap.containsKey("city")){
				Object object=positionMap.get("city");
				if(object!=null){
					city=(String) object;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(city);
	}

  static class Ob {
      private String stid;

      private String cardnum;

      private String idcode;

      private String name;

      private String phone;

      private String order_id;

      private String code;

      public String getStid() {
          return stid;
      }

      public void setStid(String stid) {
          this.stid = stid;
      }

      public String getCardnum() {
          return cardnum;
      }

      public void setCardnum(String cardnum) {
          this.cardnum = cardnum;
      }

      public String getIdcode() {
          return idcode;
      }

      public void setIdcode(String idcode) {
          this.idcode = idcode;
      }

      public String getName() {
          return name;
      }

      public void setName(String name) {
          this.name = name;
      }

      public String getPhone() {
          return phone;
      }

      public void setPhone(String phone) {
          this.phone = phone;
      }

      public String getOrder_id() {
          return order_id;
      }

      public void setOrder_id(String order_id) {
          this.order_id = order_id;
      }

      public String getCode() {
          return code;
      }

      public void setCode(String code) {
          this.code = code;
      }

  }
}
