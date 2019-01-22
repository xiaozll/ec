package com.eryansky.modules.fop.service;

import com.eryansky.modules.fop.model.FileAttribute;
import com.eryansky.modules.fop.manager.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by kl on 2018/1/17.
 * Content :
 */
@Service
public class FilePreviewFactory {

    @Autowired
    FileManager fileManager;

    @Autowired
    ApplicationContext context;

    public FilePreview get(String url) {
        Map<String, FilePreview> filePreviewMap = context.getBeansOfType(FilePreview.class);
        FileAttribute fileAttribute = fileManager.getFileAttribute(url);
        return filePreviewMap.get(fileAttribute.getType().getInstanceName());
    }
}
