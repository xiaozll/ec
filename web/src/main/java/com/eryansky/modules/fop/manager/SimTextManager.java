package com.eryansky.modules.fop.manager;

import com.eryansky.modules.fop.model.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 读取类文本文件
 *
 * @author yudian-it
 * @date 2017/12/13
 */
@Component
public class SimTextManager {
    @Value("${file.dir}")
    String fileDir;
    @Autowired
    DownloadManager downloadManager;

    public ReturnResponse<String> readSimText(String url, String fileName) {
        ReturnResponse<String> response = downloadManager.downLoad(url, "txt", fileName);
        return response;
    }
}
