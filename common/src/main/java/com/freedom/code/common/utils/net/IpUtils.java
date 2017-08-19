package com.freedom.code.common.utils.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;


/**
 * ip工具类
 * @author luo
 *
 */
public class IpUtils {
	/***
	 * 
	 * 防止代理产生错误的IP 
	 * @param request
	 * @return
	 */
	public static String getRealIp(HttpServletRequest request){
		if(request==null){
			return "127.0.0.1";
		}
		String ip = request.getHeader("x-forwarded-for");  
		
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getRemoteAddr();  
	    }
	   
	    return ip;
	
	}
	
	/**
     * 判断目标ip是否为本机
     * 
     * @return
     * @throws UnknownHostException
     */
    public static boolean isLocal(String ip) {
        Set ips = getAllLocalAddress();
        if (ips.contains(ip)) {
            return true;
        }
        return false;
    }
    
    /**
     * 获得所有本机的 IP 地址
     * 
     * @return
     */
    private static Set getAllLocalAddress() {
        Set result = new HashSet();
        Enumeration enu = null;
        try {
            enu = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return result;
        }
        while (enu.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) (enu.nextElement());
            Enumeration ias = ni.getInetAddresses();
            while (ias.hasMoreElements()) {
                InetAddress i = (InetAddress) (ias.nextElement());
                if (i.getHostAddress().indexOf(":") == -1) {// 外网IP
                    String addr = i.getHostAddress();
                    result.add(addr);
                }
            }
        }

        return result;
    }
    
    /**
     * @return 本机外网IP
     * @throws SocketException
     */
    public static String getRealIp() {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException se) {
            se.printStackTrace();
        }
        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }
    
    /**
     * @return 本机内网IP
     * @throws SocketException
     */
    public static String getLocalIp() {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP

        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                        netip = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException se) {
            se.printStackTrace();
        }

        if (localip != null && !"".equals(localip)) {
            return localip;
        } else {
            return netip;
        }
    }
    
    /**
     * 获取远程ip
     * @param request
     * @return
     */
    public static String getRemoteIp(HttpServletRequest request) {
        // 登录日志
        String remoteAddress = request.getRemoteAddr();
        final String forwarded = request.getHeader("X-Forwarded-For");
        if (remoteAddress == null) {
            remoteAddress = forwarded;
        } else if (forwarded != null && !forwarded.isEmpty()) {
            remoteAddress = forwarded;
            // remoteAddress += ";" + forwarded;
        }
        return remoteAddress;
    }
}
