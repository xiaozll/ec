package com.eryansky.modules.sys.event;

import com.eryansky.modules.sys.mapper.Log;
import org.springframework.context.ApplicationEvent;

/**
 * 系统日志事件
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2020-03-16
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(Log log) {
        super(log);
    }
}
