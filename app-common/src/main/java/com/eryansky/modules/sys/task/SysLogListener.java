/**
 * Copyright (c) 2014-2020 http://www.jfit.com.cn
 * <p>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.task;

import com.eryansky.modules.sys.event.SysLogEvent;
import com.eryansky.modules.sys.mapper.Log;
import com.eryansky.modules.sys.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author 温春平 jfwencp@jx.tobacco.gov.cn
 * @date 2020-03-16
 */
@Component
public class SysLogListener implements ApplicationListener<SysLogEvent> {

    @Autowired
    private LogService logService;

    @Async
    @Override
    public void onApplicationEvent(SysLogEvent event) {
        Log log = (Log) event.getSource();
        logService.save(log);
    }


}
