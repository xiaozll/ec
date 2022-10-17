package com.eryansky.modules.notice.web.api;

import com.eryansky.common.model.Result;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.annotation.RestApi;
import com.eryansky.listener.SystemInitListener;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.server.result.WSResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 消息接口
 */
@Controller
@RequestMapping(value = {"rest/message"})
public class MessageAPIController extends SimpleController {


    @Override
    protected void initBinder(WebDataBinder binder) {
        super.defaultWebDataBinder(binder);
    }

    /**
     * 发送消息
     * @param appKey 访问密钥:
     * @param data 参数说明:
     * {
     * 	 appId:"appId",
     * 	 serviceId:"serviceId",
     * 	 senderId:"",
     * 	 sendTime:"yyyy-MM-dd HH:mm:ss",
     *   title:"标题",
     *   category:"分类",
     *   content:"消息内容",
     *   linkUrl:"",
     *   receiveIds:["loginName1","loginName2",...]
     * }
     * appId      :应用编码   必选
     * serviceId  :服务ID  必选
     * senderId   :发送者账号(第三方系统账号，需在统一平台做账号映射) 可选
     * sendTime   :发布时间(格式：yyyy-MM-dd HH:mm:ss)  可选
     * title    :消息标题     可选
     * category    :消息分类     可选
     * content    :消息内容     必选
     * linkUrl    :消息链接    可选
     * receiveIds :接收者账号(第三方系统账号，需在统一平台做账号映射) 必选
     * @return
     * {
     * code:1,
     * msg:"提示信息",
     * obj:null
     * }
     * code 1:成功 0:失败
     * msg 提示信息
     * obj 返回的数据信息
     */
    @Logging(value = "消息接口-发送消息",logType = LogType.REST)
    @RestApi
    @PostMapping(value = { "sendMessage"})
    @ResponseBody
    public Result sendMessage(String appKey,String data) {
        if(SystemInitListener.Static.apiWebService != null){
            WSResult wsResult = SystemInitListener.Static.apiWebService.sendMessage(data);
            return WSResult.SUCCESS.equals(wsResult.getCode()) ? Result.successResult().setMsg(wsResult.getMessage()): Result.errorResult().setMsg(wsResult.getMessage());
        }
        return Result.errorResult();
    }
}
