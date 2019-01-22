package com.eryansky.modules.fop.service.impl;

import com.eryansky.modules.fop.model.FileAttribute;
import com.eryansky.modules.fop.service.FilePreview;
import com.eryansky.modules.fop.manager.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * Created by kl on 2018/1/17.
 * Content :其他文件
 */
@Service
public class OtherFilePreviewImpl implements FilePreview {
    @Autowired
    FileManager fileManager;

    @Override
    public String filePreviewHandle(String url, Model model) {
        FileAttribute fileAttribute = fileManager.getFileAttribute(url);

        model.addAttribute("fileType", fileAttribute.getSuffix());
        model.addAttribute("msg", "系统还不支持该格式文件的在线预览，" +
                "如有需要请按下方显示的邮箱地址联系系统维护人员");
        return "fileNotSupported";
    }
}
