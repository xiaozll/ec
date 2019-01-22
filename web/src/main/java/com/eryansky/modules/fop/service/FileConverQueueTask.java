package com.eryansky.modules.fop.service;

import com.eryansky.j2cache.CacheChannel;
import com.eryansky.modules.fop.model.FileAttribute;
import com.eryansky.modules.fop.model.FileType;
import com.eryansky.modules.fop.manager.FileManager;
import io.lettuce.core.RedisCommandTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kl on 2018/1/19.
 * Content :消费队列中的转换文件
 */
@Service
public class FileConverQueueTask {

    Logger logger = LoggerFactory.getLogger(getClass());
    public static final String queueTaskName = "FileConverQueueTask";

    @Autowired
    FilePreviewFactory filePreviewFactory;

    @Autowired
    CacheChannel cacheChannel;

    @Autowired
    FileManager fileManager;

    @PostConstruct
    public void startTask() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(new ConverTask(filePreviewFactory, cacheChannel, fileManager));
        logger.info("队列处理文件转换任务启动完成 ");
    }

    class ConverTask implements Runnable {

        FilePreviewFactory previewFactory;

        CacheChannel cacheChannel;

        FileManager fileManager;

        public ConverTask(FilePreviewFactory previewFactory, CacheChannel cacheChannel, FileManager fileManager) {
            this.previewFactory = previewFactory;
            this.cacheChannel = cacheChannel;
            this.fileManager = fileManager;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String url = cacheChannel.queuePop(FileConverQueueTask.queueTaskName);
                    if (url != null) {
                        FileAttribute fileAttribute = fileManager.getFileAttribute(url);
                        logger.info("正在处理转换任务，文件名称【{}】", fileAttribute.getName());
                        FileType fileType = fileAttribute.getType();
                        if (fileType.equals(FileType.compress) || fileType.equals(FileType.office)) {
                            FilePreview filePreview = previewFactory.get(url);
                            filePreview.filePreviewHandle(url, new ExtendedModelMap());
                        }
                    }
                } catch (RedisCommandTimeoutException e){
                    logger.error(e.getMessage(),e);
                } catch (Exception e) {
                    try {
                        Thread.sleep(1000 * 10);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }
    }

}
