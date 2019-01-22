package com.eryansky.modules.fop.service.impl;

import com.eryansky.modules.fop.model.FileAttribute;
import com.eryansky.modules.fop.model.ReturnResponse;
import com.eryansky.modules.fop.service.FilePreview;
import com.eryansky.modules.fop.manager.DownloadManager;
import com.eryansky.modules.fop.manager.FileManager;
import com.eryansky.modules.fop.manager.OfficeToPdfManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.File;

/**
 * Created by kl on 2018/1/17.
 * Content :处理office文件
 */
@Service
public class OfficeFilePreviewImpl implements FilePreview {

    @Autowired
    FileManager fileManager;

    @Value("${file.dir}")
    String fileDir;

    @Autowired
    DownloadManager downloadManager;

    @Autowired
    private OfficeToPdfManager officeToPdfManager;

    @Override
    public String filePreviewHandle(String url, Model model) {
        FileAttribute fileAttribute = fileManager.getFileAttribute(url);
        String suffix = fileAttribute.getSuffix();
        String fileName = fileAttribute.getName();
        String decodedUrl = fileAttribute.getDecodedUrl();
        boolean isHtml = suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx");
        String pdfName = fileName.substring(0, fileName.lastIndexOf(".") + 1) + (isHtml ? "html" : "pdf");
        // 判断之前是否已转换过，如果转换过，直接返回，否则执行转换
        if (!fileManager.listConvertedFiles().containsKey(pdfName)) {
            String filePath = fileDir + fileName;
            if (!new File(filePath).exists()) {
                ReturnResponse<String> response = downloadManager.downLoad(decodedUrl, suffix, null);
                if (0 != response.getCode()) {
                    model.addAttribute("msg", response.getMsg());
                    return "fileNotSupported";
                }
                filePath = response.getContent();
            }
            String outFilePath = fileDir + pdfName;
            if (StringUtils.hasText(outFilePath)) {
                officeToPdfManager.openOfficeToPDF(filePath, outFilePath);
                File f = new File(filePath);
                if (f.exists()) {
                    f.delete();
                }
                if (isHtml) {
                    // 对转换后的文件进行操作(改变编码方式)
                    fileManager.doActionConvertedFile(outFilePath);
                }
                // 加入缓存
                fileManager.addConvertedFile(pdfName, fileManager.getRelativePath(outFilePath));
            }
        }
        String pdfUrl = StringUtils.startsWithIgnoreCase(pdfName,"http:") || StringUtils.startsWithIgnoreCase(pdfName,"https:") ? pdfName:
                ((String) RequestContextHolder.currentRequestAttributes().getAttribute("baseUrl", 0))+pdfName;

        model.addAttribute("pdfUrl", pdfUrl);
        return isHtml ? "html" : "pdf";
    }
}
