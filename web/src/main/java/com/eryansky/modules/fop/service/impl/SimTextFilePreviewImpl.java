package com.eryansky.modules.fop.service.impl;

import com.eryansky.modules.fop.model.FileAttribute;
import com.eryansky.modules.fop.model.ReturnResponse;
import com.eryansky.modules.fop.service.FilePreview;
import com.eryansky.modules.fop.manager.FileManager;
import com.eryansky.modules.fop.manager.SimTextManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * Created by kl on 2018/1/17.
 * Content :处理文本文件
 */
@Service
public class SimTextFilePreviewImpl implements FilePreview {

    @Autowired
    SimTextManager simTextManager;

    @Autowired
    FileManager fileManager;

    @Override
    public String filePreviewHandle(String url, Model model) {
        FileAttribute fileAttribute = fileManager.getFileAttribute(url);
        String decodedUrl = fileAttribute.getDecodedUrl();
        String fileName = fileAttribute.getName();
        ReturnResponse<String> response = simTextManager.readSimText(decodedUrl, fileName);
        if (0 != response.getCode()) {
            model.addAttribute("msg", response.getMsg());
            model.addAttribute("fileType", fileAttribute.getSuffix());
            return "fileNotSupported";
        }
        model.addAttribute("ordinaryUrl", response.getMsg());
        return "txt";
    }

}
