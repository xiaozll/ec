package com.eryansky.modules.fop.service.impl;

import com.eryansky.modules.fop.model.FileAttribute;
import com.eryansky.modules.fop.model.ReturnResponse;
import com.eryansky.modules.fop.service.FilePreview;
import com.eryansky.modules.fop.manager.DownloadManager;
import com.eryansky.modules.fop.manager.FileManager;
import com.eryansky.modules.fop.manager.ZipReaderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

/**
 * Created by kl on 2018/1/17.
 * Content :处理压缩包文件
 */
@Service
public class CompressFilePreviewImpl implements FilePreview {

    @Autowired
    FileManager fileManager;

    @Autowired
    DownloadManager downloadManager;

    @Autowired
    ZipReaderManager zipReaderManager;

    @Override
    public String filePreviewHandle(String url, Model model) {
        FileAttribute fileAttribute = fileManager.getFileAttribute(url);
        String fileName = fileAttribute.getName();
        String decodedUrl = fileAttribute.getDecodedUrl();
        String suffix = fileAttribute.getSuffix();
        String fileTree = null;
        // 判断文件名是否存在(redis缓存读取)
        if (!StringUtils.hasText(fileManager.getConvertedFile(fileName))) {
            ReturnResponse<String> response = downloadManager.downLoad(decodedUrl, suffix, fileName);
            if (0 != response.getCode()) {
                model.addAttribute("msg", response.getMsg());
                return "fileNotSupported";
            }
            String filePath = response.getContent();
            if ("zip".equalsIgnoreCase(suffix) || "jar".equalsIgnoreCase(suffix) || "gzip".equalsIgnoreCase(suffix)) {
                fileTree = zipReaderManager.readZipFile(filePath, fileName);
            } else if ("rar".equalsIgnoreCase(suffix)) {
                fileTree = zipReaderManager.unRar(filePath, fileName);
            }
            fileManager.addConvertedFile(fileName, fileTree);
        } else {
            fileTree = fileManager.getConvertedFile(fileName);
        }
        if (null != fileTree) {
            model.addAttribute("fileTree", fileTree);
            return "compress";
        } else {
            model.addAttribute("msg", "压缩文件类型不受支持，尝试在压缩的时候选择RAR4格式");
            return "fileNotSupported";
        }
    }
}
