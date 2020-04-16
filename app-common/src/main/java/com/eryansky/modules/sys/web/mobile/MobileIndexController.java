package com.eryansky.modules.sys.web.mobile;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm._enum.StatusState;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.utils.encode.EncodeUtils;
import com.eryansky.common.utils.io.IoUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.core.web.annotation.MobileValue;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.mapper.File;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys._enum.VersionLogType;
import com.eryansky.modules.sys.mapper.VersionLog;
import com.eryansky.modules.sys.service.VersionLogService;
import com.eryansky.modules.sys.utils.VersionLogUtils;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.AppUtils;
import com.google.common.collect.Maps;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.Map;

/**
 * 手机端入口
 */
@Mobile
@Controller
@RequestMapping("${mobilePath}")
public class MobileIndexController extends SimpleController {

    @Autowired
    private VersionLogService versionLogService;

    @Logging(logType = LogType.access, value = "移动APP")
    @RequestMapping("")
    public ModelAndView index() {
        return new ModelAndView("layout/index");
    }

    @Logging(logType = LogType.access, value = "移动APP")
    @RequestMapping(value = {"content"})
    public ModelAndView content() {
        return new ModelAndView("layout/index_content");
    }


    /**
     * 下载页面
     * @param app 应用标识 默认值: {@link VersionLog#DEFAULT_ID}
     * @param versionLogType {@link VersionLogType}
     * @param versionCode
     * @return
     */
    @Mobile(value = MobileValue.PC)
    @RequiresUser(required = false)
    @RequestMapping("download")
    public ModelAndView download(String app,String versionLogType, String versionCode) {
        ModelAndView modelAndView = new ModelAndView("mobile/download");
        VersionLog versionLog = null;
        String ua = UserAgentUtils.getHTTPUserAgent(SpringMVCHolder.getRequest());
        boolean likeIOS = AppUtils.likeIOS(ua);
        boolean likeAndroid = AppUtils.likeAndroid(ua);
        if (versionLogType == null) {
            if (likeIOS) {
                versionLogType = VersionLogType.iPhoneAPP.getValue();
            } else {
                versionLogType = VersionLogType.Android.getValue();
            }
        } else {
            if (VersionLogType.iPhoneAPP.getValue().equals(versionLogType)) {
                likeIOS = true;
                likeAndroid = false;
            }
        }
        if (StringUtils.isNotBlank(versionCode)) {
            versionLog = versionLogService.getByVersionCode(app,versionLogType, versionCode);
        } else {
            versionLog = versionLogService.getLatestVersionLog(app,versionLogType);
        }
        modelAndView.addObject("app", app);
        modelAndView.addObject("versionLogType", versionLogType);
        modelAndView.addObject("versionCode", versionCode);
        modelAndView.addObject("model", versionLog);
        modelAndView.addObject("likeAndroid", likeAndroid);
        modelAndView.addObject("likeIOS", likeIOS);
        return modelAndView;
    }

    /**
     * 查找更新
     * @param versionLogType {@link VersionLogType}
     * @param app 应用标识 默认值: {@link VersionLog#DEFAULT_ID}
     * @return
     */
    @RequiresUser(required = false)
    @ResponseBody
    @RequestMapping(value = {"getNewVersion/{versionLogType}"})
    public Result getNewVersion(@PathVariable String versionLogType,String app) {
        return getNewVersion(SpringMVCHolder.getRequest(),versionLogType,app);
    }


    /**
     * 查找更新
     * @param request
     * @param versionLogType {@link VersionLogType}
     * @param app 应用标识 默认值: {@link VersionLog#DEFAULT_ID}
     * @return
     */
    @RequiresUser(required = false)
    @ResponseBody
    @RequestMapping(value = {"getNewVersion"})
    public Result getNewVersion(HttpServletRequest request,String versionLogType,String app) {
        String _versionLogType = versionLogType;
        if(StringUtils.isBlank(versionLogType)){
            VersionLogType vt = VersionLogUtils.getLatestVersionLogType(request);
            _versionLogType = null != vt ? vt.getValue():null;
        }
        if(StringUtils.isBlank(_versionLogType)){
            throw new ActionException("未识别参数[versionLogType]");
        }
        VersionLog max = versionLogService.getLatestVersionLog(app,_versionLogType);
        Map<String,Object> data = Maps.newHashMap();
        data.put("versionLog",max);
        data.put("appDownLoadUrl",AppUtils.getAppURL()+"/m/download");
        data.put("apkDownLoadUrl",AppUtils.getAppURL()+"/m/downloadApp/"+VersionLogType.Android.getValue());
        return Result.successResult().setObj(data);
    }

    private static final String MIME_ANDROID_TYPE = "application/vnd.android.package-archive";

    /**
     * APP下载
     *
     * @param response
     * @param request
     * @param app 应用标识 默认值: {@link VersionLog#DEFAULT_ID}
     * @param versionCode    版本号
     * @param versionLogType {@link com.eryansky.modules.sys._enum.VersionLogType}
     *                       文件ID
     */
    @Logging(logType = LogType.access, value = "'APP下载'")
    @RequiresUser(required = false)
    @RequestMapping(value = {"downloadApp/{versionLogType}"})
    public ModelAndView downloadApp(HttpServletResponse response,
                                     HttpServletRequest request,
                                     String app,
                                     String versionCode,
                                     @PathVariable String versionLogType) {
        VersionLog versionLog = null;
        if (StringUtils.isNotBlank(versionCode)) {
            versionLog = versionLogService.getByVersionCode(app,versionLogType, versionCode);
        } else {
            versionLog = versionLogService.getLatestVersionLog(app,versionLogType);
        }
        ActionException fileNotFoldException = new ActionException("下载的APP文件不存在");
        if (versionLog == null || StringUtils.isBlank(versionLog.getFileId())) {
            throw fileNotFoldException;
        }
        try {
            File file = DiskUtils.getFile(versionLog.getFileId());
            if (VersionLogType.Android.getValue().equals(versionLogType)) {
                response.setContentType(MIME_ANDROID_TYPE);
            }
            java.io.File diskFile = file.getDiskFile();
            if (!diskFile.exists() || !diskFile.canRead()) {
                throw fileNotFoldException;
            }
            String filename = file.getName();
            long fileLength = diskFile.length();// 记录文件大小
            long pastLength = 0;// 记录已下载文件大小
            long toLength = 0;// 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
            long contentLength = 0;// 客户端请求的字节总量
            String rangeBytes = "";// 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容

            // ETag header
            // The ETag is contentLength + lastModified
            response.setHeader("ETag", "W/\"" + fileLength + "-" + diskFile.lastModified() + "\"");
            // Last-Modified header
            response.setHeader("Last-Modified", new Date(diskFile.lastModified()).toString());

            if (request.getHeader("Range") != null) {// 客户端请求的下载的文件块的开始字节
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                logger.debug("request.getHeader(\"Range\")=" + request.getHeader("Range"));
                rangeBytes = request.getHeader("Range").replaceAll("bytes=", "");
                if (rangeBytes.indexOf('-') == rangeBytes.length() - 1) {// bytes=969998336-
                    rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                    pastLength = Long.parseLong(rangeBytes.trim());
                    toLength = fileLength - 1;
                } else {// bytes=1275856879-1275877358
                    String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                    String temp2 = rangeBytes.substring(
                            rangeBytes.indexOf('-') + 1, rangeBytes.length());
                    // bytes=1275856879-1275877358，从第 1275856879个字节开始下载
                    pastLength = Long.parseLong(temp0.trim());
                    // bytes=1275856879-1275877358，到第 1275877358 个字节结束
                    toLength = Long.parseLong(temp2);
                }
            } else {// 从开始进行下载
                toLength = fileLength - 1;
            }
            // 客户端请求的是1275856879-1275877358 之间的字节
            contentLength = toLength - pastLength + 1;
            if (contentLength < Integer.MAX_VALUE) {
                response.setContentLength((int) contentLength);
            } else {
                // Set the content-length as String to be able to use a long
                response.setHeader("content-length", "" + contentLength);
            }
            String contentType = AppUtils.getServletContext().getMimeType(filename);
            if (null != contentType) {
                response.setContentType(contentType);
            } else {
                response.setContentType("application/x-download");
            }

            // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
            response.setHeader("Accept-Ranges", "bytes");
            // 必须先设置content-length再设置header
            response.addHeader("Content-Range", "bytes " + pastLength + "-" + toLength + "/" + fileLength);
            int bufferSize = 2048;
            response.setBufferSize(bufferSize);

            InputStream istream = null;
            OutputStream os = null;
            try {
                WebUtils.setDownloadableHeader(request, response, filename);
                os = response.getOutputStream();
                istream = new BufferedInputStream(new FileInputStream(diskFile), bufferSize);
                try {
                    IoUtils.copy(istream, os, pastLength, toLength);
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
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IoUtils.closeSilently(istream);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new ActionException(e);
        }
        return null;
    }

    /**
     * 文件删除
     *
     * @param fileId
     * @return
     */
    @RequestMapping(value = {"deleteFile"})
    @ResponseBody
    public Result deleteFile(@RequestParam(value = "fileId") String fileId) {
        DiskUtils.deleteFile(fileId);
        return Result.successResult();
    }

    /**
     * 图片文件上传
     */
    @RequestMapping(value = {"base64ImageUpLoad"})
    @ResponseBody
    public Result base64ImageUpLoad(@RequestParam(value = "base64Data", required = false) String base64Data) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (null == sessionInfo) {
            return Result.errorResult().setMsg("未授权");
        }
        Exception exception = null;
        File file = null;
        try {

            String dataPrix = "";
            String data = "";

            if (base64Data == null || "".equals(base64Data)) {
                return Result.errorResult().setMsg("上传失败，上传图片数据为空");
            } else {
                String[] d = base64Data.split("base64,");
                if (d != null && d.length == 2) {
                    dataPrix = d[0];
                    data = d[1];
                } else {
                    return Result.errorResult().setMsg("上传失败，数据不合法");
                }
            }

            String suffix = "";
            if ("data:image/jpeg;".equalsIgnoreCase(dataPrix)) {//data:image/jpeg;base64,base64编码的jpeg图片数据
                suffix = ".jpg";
            } else if ("data:image/x-icon;".equalsIgnoreCase(dataPrix)) {//data:image/x-icon;base64,base64编码的icon图片数据
                suffix = ".ico";
            } else if ("data:image/gif;".equalsIgnoreCase(dataPrix)) {//data:image/gif;base64,base64编码的gif图片数据
                suffix = ".gif";
            } else if ("data:image/png;".equalsIgnoreCase(dataPrix)) {//data:image/png;base64,base64编码的png图片数据
                suffix = ".png";
            } else {
                return Result.errorResult().setMsg("上传图片格式不合法");
            }
            String tempFileName = Identities.uuid() + suffix;

            byte[] bs = null;
            try {
                bs = EncodeUtils.base64Decode(data);
//                bs = Base64Utils.decodeFromString(data);
            } catch (Exception e) {
                logger.info("{},{}",new Object[]{sessionInfo.getLoginName(),base64Data});
                logger.error("图片上传失败,"+e.getMessage(),e);
                return Result.errorResult().setMsg("图片上传失败,解析异常！");
            }

            file = DiskUtils.saveSystemFile("IMAGE", sessionInfo.getUserId(), new ByteArrayInputStream(bs), tempFileName);
            file.setStatus(StatusState.LOCK.getValue());
            DiskUtils.saveFile(file);
            result = Result.successResult().setObj(file).setMsg("文件上传成功！");
        } catch (InvalidExtensionException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (FileNameLengthLimitExceededException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (ActionException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } catch (IOException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } finally {
            if (exception != null) {
                logger.error(exception.getMessage(),exception);
                if (file != null) {
                    DiskUtils.deleteFile(file.getId());
                }
            }
        }
        return result;

    }

    /**
     * 图片文件上传
     */
    @RequestMapping(value = {"imageUpLoad"})
    @ResponseBody
    public Result imageUpLoad(@RequestParam(value = "uploadFile", required = false) MultipartFile multipartFile) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Exception exception = null;
        File file = null;
        try {
            file = DiskUtils.saveSystemFile("IMAGE", sessionInfo.getUserId(), multipartFile);
            file.setStatus(StatusState.LOCK.getValue());
            DiskUtils.saveFile(file);
            Map<String, Object> _data = Maps.newHashMap();
            String data = "data:image/jpeg;base64," + Base64Utils.encodeToString(FileCopyUtils.copyToByteArray(new FileInputStream(file.getDiskFile())));
            _data.put("file", file);
            _data.put("data", data);
            _data.put("url", AppConstants.getAdminPath() + "/disk/fileDownload/" + file.getId());
            result = Result.successResult().setObj(_data).setMsg("文件上传成功！");
        } catch (InvalidExtensionException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (FileNameLengthLimitExceededException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG);
        } catch (ActionException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } catch (IOException e) {
            exception = e;
            result = Result.errorResult().setMsg(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage());
        } finally {
            if (exception != null) {
                logger.error(exception.getMessage(),exception);
                if (file != null) {
                    DiskUtils.deleteFile(file.getId());
                }
            }
        }
        return result;

    }
}