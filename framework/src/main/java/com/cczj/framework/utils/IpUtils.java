
package com.cczj.framework.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP地址
 */
@Slf4j
public class IpUtils {
    /**
     * 默认IP地址
     */
    public final static String ERROR_IP = "127.0.0.1";

    /**
     * IP地址正则表达式
     */
    public final static Pattern pattern = Pattern.compile(
            "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})");


    /**
     * @param ip
     * @return boolean
     * @author HuaZai
     * @contact who.seek.me@java98k.vip
     * @title isValidIP
     * <ul>
     * @description 验证IP地址是否符合规则
     * </ul>
     * @createdTime 2017年12月30日 下午6:49:21
     * @version : V1.0.0
     */
    public static boolean isValidIP(String ip) {
        if (StrUtil.isEmpty(ip)) {
            return false;
        }
        Matcher matcher = pattern.matcher(ip);
        boolean isValid = matcher.matches();
        return isValid;
    }


    /**
     * @return String
     * @author HuaZai
     * @contact who.seek.me@java98k.vip
     * @title getServerIP
     * <ul>
     * @description 获取服务器IP地址
     * </ul>
     * @createdTime 2017年12月30日 下午7:44:16
     * @version : V1.0.0
     */
    public static String getServerIP() {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
            String hostAddress = inetAddress.getHostAddress();
            return hostAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ERROR_IP;
    }

    /**
     * @param request
     * @return String
     * @author HuaZai
     * @contact who.seek.me@java98k.vip
     * @title getIpAddress
     * <ul>
     * @description              <li>获取IP地址
     * <li><strong>注意：</strong>IP地址经过多次反向代理后会有多个IP值，
     * <li>其中第一个IP才是真实IP，所以不能通过request.getRemoteAddr()获取IP地址，
     * <li>如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
     * <li>X-Forwarded-For中第一个非unknown的有效IP才是用户真实IP地址。
     * </ul>
     * @createdTime 2017年12月30日 下午7:52:35
     * @version : V1.0.0
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        try {
            // 获取用户真是的地址
            String Xip = request.getHeader("X-Real-IP");
            // 获取多次代理后的IP字符串值
            String XFor = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
                // 多次反向代理后会有多个IP值，第一个用户真实的IP地址
                int index = XFor.indexOf(",");
                if (index >= 0) {
                    return XFor.substring(0, index);
                } else {
                    return XFor;
                }
            }
            ip = Xip;
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.error("获取ip失败", e);
        }
        return ip;
    }

    public static String getLocalHostAddress() {
        InetAddress address = getLocalHostExactAddress();
        return address == null ? "" : address.getHostAddress();
    }

    public static InetAddress getLocalHostExactAddress() {
        try {
            InetAddress candidateAddress = null;

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了 就是我们要找的
                            // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
                            return inetAddr;
                        }

                        // 若不是site-local地址 那就记录下该地址当作候选
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }

                    }
                }
            }

            // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
            return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
        } catch (Exception e) {
            log.error("获取本机ip地址失败", e);
        }
        return null;
    }

    public static String getUserIP(HttpServletRequest request) {
        // 优先取 X-Real-IP
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = ERROR_IP;
            }
        }
        if ("unknown".equalsIgnoreCase(ip)) {
            ip = ERROR_IP;
            return ip;
        }
        int index = ip.indexOf(',');
        if (index >= 0) {
            ip = ip.substring(0, index);
        }

        return ip;
    }

    public static void main(String[] args) {
        String localhostStr = IpUtils.getLocalHostAddress();
        System.out.println(localhostStr);
    }

}
