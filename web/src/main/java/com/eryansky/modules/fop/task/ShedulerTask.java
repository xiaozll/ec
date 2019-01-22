package com.eryansky.modules.fop.task;

import com.eryansky.modules.fop.utils.DeleteFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ShedulerTask {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${file.dir}")
    String fileDir;

    /**
     * 每晚23点执行一次
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void clean() {
        log.info("执行一次清空文件夹");
        DeleteFileUtil.deleteDirectory(fileDir);
    }
}
