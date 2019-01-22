package com.eryansky.modules.fop.service.impl;

import com.eryansky.modules.fop.service.FilePreview;
import com.eryansky.modules.fop.manager.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * @author : kl
 * @authorboke : kailing.pub
 * @create : 2018-03-25 上午11:58
 * @description:
 **/
@Service
public class MediaFilePreviewImpl implements FilePreview {

    @Autowired
    FileManager fileManager;

    @Override
    public String filePreviewHandle(String url, Model model) {
        model.addAttribute("mediaUrl", url);
        return "media";
    }


}
