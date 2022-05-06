/**
 *  Copyright (c) 2012-2022 https://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.net;

import com.eryansky.common.utils.NumberUtil;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.encode.EncodeUtils;
import com.google.common.net.InetAddresses;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 *
 * 主要包含int, String/IPV4String, InetAdress/Inet4Address之间的互相转换；客户端IP地址获取.
 *
 * 先将字符串传换为byte[]再用InetAddress.getByAddress(byte[])，避免了InetAddress.getByName(ip)可能引起的DNS访问.
 *
 * InetAddress与String的转换其实消耗不小，如果是有限的地址，建议进行缓存.
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-04-19 17:38
 */
public class IpUtils {

    private static final String IP_UNKNOW = "unknown";

    private IpUtils() {
    }

    /**
     * 获取客户端IP
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return IP_UNKNOW;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_VIA");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }

        if (ip == null || ip.length() == 0 || IP_UNKNOW.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotBlank(ip)){
            ip = EncodeUtils.xssFilter(ip);
        }
        if (StringUtils.isBlank(ip)){
            ip = IP_UNKNOW;
        }
        return ip;
    }

    /**
     * 获取客户端IP （有多个时，仅取第一个）
     * @param request
     * @return
     */
    public static String getIpAddr0(HttpServletRequest request) {
        String ip = getIpAddr(request);
        if (StringUtils.isNotBlank(ip) && !IP_UNKNOW.equalsIgnoreCase(ip)){
            ip = StringUtils.split(ip, ",")[0];
        }
        if (StringUtils.isBlank(ip)){
            ip = IP_UNKNOW;
        }
        return ip;
    }

    /**
     * 获取本地有效IP
     * @return
     */
    public static String getActivityLocalIp(){
        InetAddress inetAddress;
        String local = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            local = InetAddresses.toAddrString(inetAddress);
        } catch (Exception e) {
        }
        return "127.0.0.1".equals(local) ? "":local;
    }

    /**
     * 从InetAddress转化到int, 传输和存储时, 用int代表InetAddress是最小的开销.
     *
     * InetAddress可以是IPV4或IPV6，都会转成IPV4.
     *
     * @see com.google.common.net.InetAddresses#coerceToInteger(InetAddress)
     */
    public static int toInt(InetAddress address) {
        return InetAddresses.coerceToInteger(address);
    }

    /**
     * InetAddress转换为String.
     *
     * InetAddress可以是IPV4或IPV6. 其中IPV4直接调用getHostAddress()
     *
     * @see com.google.common.net.InetAddresses#toAddrString(InetAddress)
     */
    public static String toIpString(InetAddress address) {
        return InetAddresses.toAddrString(address);
    }

    /**
     * 从int转换为Inet4Address(仅支持IPV4)
     */
    public static Inet4Address fromInt(int address) {
        return InetAddresses.fromInteger(address);
    }

    /**
     * 从String转换为InetAddress.
     *
     * IpString可以是ipv4 或 ipv6 string, 但不可以是域名.
     *
     * 先字符串传换为byte[]再调getByAddress(byte[])，避免了调用getByName(ip)可能引起的DNS访问.
     */
    public static InetAddress fromIpString(String address) {
        return InetAddresses.forString(address);
    }

    /**
     * 从IPv4String转换为InetAddress.
     *
     * IpString如果确定ipv4, 使用本方法减少字符分析消耗 .
     *
     * 先字符串传换为byte[]再调getByAddress(byte[])，避免了调用getByName(ip)可能引起的DNS访问.
     */
    public static Inet4Address fromIpv4String(String address) {
        byte[] bytes = ip4StringToBytes(address);
        if (bytes == null) {
            return null;
        } else {
            try {
                return (Inet4Address) Inet4Address.getByAddress(bytes);
            } catch (UnknownHostException e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * int转换到IPV4 String, from Netty NetUtil
     */
    public static String intToIpv4String(int i) {
        return new StringBuilder(15).append((i >> 24) & 0xff).append('.').append((i >> 16) & 0xff).append('.')
                .append((i >> 8) & 0xff).append('.').append(i & 0xff).toString();
    }

    /**
     * Ipv4 String 转换到int
     */
    public static int ipv4StringToInt(String ipv4Str) {
        byte[] byteAddress = ip4StringToBytes(ipv4Str);
        if (byteAddress == null) {
            return 0;
        } else {
            return NumberUtil.toInt(byteAddress);
        }
    }

    /**
     * Ipv4 String 转换到byte[]
     */
    private static byte[] ip4StringToBytes(String ipv4Str) {
        if (ipv4Str == null) {
            return null;
        }

        List<String> it = StringUtils.split(ipv4Str, '.', 4);
        if (it.size() != 4) {
            return null;
        }

        byte[] byteAddress = new byte[4];
        for (int i = 0; i < 4; i++) {
            int tempInt = Integer.parseInt(it.get(i));
            if (tempInt > 255) {
                return null;
            }
            byteAddress[i] = (byte) tempInt;
        }
        return byteAddress;
    }
}