package com.eryansky.modules.sys.utils;

import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collection;
import java.util.Enumeration;

/**
 * 文件下载工具类
 */
public class DownloadFileUtils {


    private static Logger logger = LoggerFactory.getLogger(DownloadFileUtils.class);

    private DownloadFileUtils(){}

    /**
     * 文件下载 支持断点续传
     * @param downloadFile
     * @param downloadFileName
     * @param response
     * @param request
     * @throws Exception
     */
    public static void downRangeFile(File downloadFile,String downloadFileName, HttpServletResponse response, HttpServletRequest request) throws Exception {
        // 文件不存在
        if (!downloadFile.exists() || !downloadFile.canRead()) {
            // 404
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 记录文件大小
        long fileLength = downloadFile.length();
        String _fileName = null != downloadFileName ? downloadFileName:downloadFile.getName();
        // 记录已下载文件大小
        long pastLength = 0;
        // 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
        int rangeSwitch = 0;
        // 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
        long toLength = 0;
        // 客户端请求的字节总量
        long contentLength = 0;
        // 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
        String rangeBytes = "";

        // 缓冲区大小
        int bsize = 1024;
        // 暂存容器
        byte[] b = new byte[bsize];

        if (request.getParameter("showHeader") != null) {
            Enumeration<String> paramNames = request.getHeaderNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                if (name != null && name.length() > 0) {
                    String value = request.getHeader(name);
                    logger.info("request {}:{}", name, value);
                }
            }
        }

        String range = request.getHeader("Range");
        // if(range == null)
        // range = "bytes=0-";
        int responseStatus = 206;
        if (range != null && range.trim().length() > 0 && !"null".equals(range)) {
            // 客户端请求的下载的文件块的开始字节
            responseStatus = javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT;
            logger.debug("request.getHeader(\"Range\")={}",range);
            rangeBytes = range.replaceAll("bytes=", "");

            if (rangeBytes.endsWith("-")) {
                // 行如：bytes=969998336-
                rangeSwitch = 1;
                rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                pastLength = Long.parseLong(rangeBytes.trim());
                // 客户端请求的是969998336之后的字节（包括bytes下标索引为969998336的字节）
                contentLength = fileLength - pastLength;
            } else {
                // 行如：bytes=1275856879-1275877358
                rangeSwitch = 2;
                String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
                // bytes=1275856879-1275877358，从第1275856879个字节开始下载
                pastLength = Long.parseLong(temp0.trim());
                // bytes=1275856879-1275877358，到第1275877358 个字节结束
                toLength = Long.parseLong(temp2);
                // 客户端请求的是1275856879-1275877358之间的字节
                contentLength = toLength - pastLength + 1;
            }
        } else {
            // 从开始进行下载
            contentLength = fileLength;// 客户端要求全文下载
        }

        /**
         * 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。 响应的格式是:
         * Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
         * ServletActionContext.getResponse().setHeader("Content-Length", new Long(file.length() - p).toString());
         */

       String contentType = response.getContentType();
        // 来清除首部的空白行
        response.reset();
        if(null != contentType){
            response.setContentType(contentType);
        }else{
            //设置response的Content-Type,set the MIME type
            String mimeType = AppUtils.getServletContext().getMimeType(_fileName);
            if (null != mimeType) {
                response.setContentType(mimeType);
            } else {
                response.setContentType("application/x-download");
            }
        }
        // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
        response.setHeader("Accept-Ranges", "bytes");
        // 如果是第一次下,还没有断点续传,状态是默认的 200,无需显式设置;响应的格式是:HTTP/1.1
        // response.addHeader("Cache-Control", "max-age=1296000");
        // response.addHeader("Expires", "Fri, 12 Oct 2012 03:43:01 GMT");
        // response.addHeader("Last-Modified", "Tue, 31 Jul 2012 03:58:36 GMT");
        // response.addHeader("Connection", "keep-alive");
        // response.addHeader("ETag", downloadFile.getName() + "-" +
        // downloadFile.lastModified());
        // response.addHeader("Last-Modified", "Thu, 27 Sep 2012 05:24:44 GMT");

        /**
         * 设置response的Content-Range。响应的格式是:Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
         */
        if (rangeSwitch != 0) {
            // 不是从最开始下载，断点下载响应号为206
            response.setStatus(responseStatus);
            switch (rangeSwitch) {
                case 1: {
                    // 针对 bytes=27000- 的请求
                    String contentRange = new StringBuffer("bytes ")
                            .append(new Long(pastLength)).append("-")
                            .append(new Long(fileLength - 1))
                            .append("/").append(new Long(fileLength))
                            .toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                case 2: {
                    // 针对 bytes=27000-39000 的请求
                    String contentRange = range.replace("=", " ") + "/"
                            + new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                default: {
                    break;
                }
            }
        } else {
            // 是从开始下载
            String contentRange = new StringBuffer("bytes ").append("0-")
                    .append(fileLength - 1).append("/").append(fileLength)
                    .toString();
            response.setHeader("Content-Range", contentRange);
        }

        // /////////////////////////设置文件下载名称Content-Disposition///////////////////////////
//            if("bytes=0-1".equals(range)){
//                response.reset();
//                304
//                response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED);
//            }
        response.setHeader("Content-Length", String.valueOf(contentLength));
        if (request.getParameter("showHeader") != null) {
            Collection<String> responseHeaderNames = response.getHeaderNames();
            for (String name : responseHeaderNames) {
                String value = response.getHeader(name);
                logger.info("response {}:{}", name, value);
            }
        }
        WebUtils.setDownloadableHeader(request, response, _fileName);
        try (OutputStream os = response.getOutputStream();
             OutputStream out = new BufferedOutputStream(os);
             RandomAccessFile raf = new RandomAccessFile(downloadFile, "r")){
            // 实际输出字节数
            long outLength = 0;
            switch (rangeSwitch) {
                case 0: {
                    // 普通下载，或者从头开始的下载
                    // 同1，没有break
                }
                case 1: {
                    // 针对 bytes=27000- 的请求
                    raf.seek(pastLength);// 形如 bytes=969998336- 的客户端请求，跳过969998336 个字节
                    int n = 0;
                    while ((n = raf.read(b)) != -1) {
                        out.write(b, 0, n);
                        outLength += n;
                    }
                    // while ((n = raf.read(b, 0, 1024)) != -1) {
                    // out.write(b, 0, n);
                    // }
                    break;
                }
                case 2: {
                    // 针对 bytes=27000-39000 的请求，从27000开始写数据
                    raf.seek(pastLength);
                    int n = 0;
                    // 记录已读字节数
                    long readLength = 0;
                    while (readLength <= contentLength - bsize) {
                        // 大部分字节在这里读取
                        n = raf.read(b);
                        readLength += n;
                        out.write(b, 0, n);
                        outLength += n;
                    }
                    if (readLength <= contentLength) {
                        // 余下的不足 1024 个字节在这里读取
                        n = raf.read(b, 0, (int) (contentLength - readLength));
                        out.write(b, 0, n);
                        outLength += n;
                    }
                    break;
                }
                default: {
                    break;
                }
            }
            logger.debug("Content-Length为：" + contentLength + "；实际输出字节数：" + outLength);
            out.flush();
        } catch (IOException ie) {
            /**
             * 在写数据的时候， 对于 ClientAbortException 之类的异常，
             * 是因为客户端取消了下载，而服务器端继续向浏览器写入数据时， 抛出这个异常，这个是正常的。
             * 尤其是对于迅雷这种吸血的客户端软件， 明明已经有一个线程在读取 bytes=1275856879-1275877358，
             * 如果短时间内没有读取完毕，迅雷会再启第二个、第三个。。。线程来读取相同的字节段， 直到有一个线程读取完毕，迅雷会 KILL
             * 掉其他正在下载同一字节段的线程， 强行中止字节读出，造成服务器抛 ClientAbortException。
             * 所以，我们忽略这种异常
             */
            // ignore
        }
    }
}