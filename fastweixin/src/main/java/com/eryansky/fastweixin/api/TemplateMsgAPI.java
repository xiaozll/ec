package com.eryansky.fastweixin.api;

import com.alibaba.fastjson.JSONArray;
import com.eryansky.fastweixin.api.enums.ResultType;
import com.eryansky.fastweixin.api.config.ApiConfig;
import com.eryansky.fastweixin.api.entity.Industry;
import com.eryansky.fastweixin.api.entity.TemplateMsg;
import com.eryansky.fastweixin.api.response.AddTemplateResponse;
import com.eryansky.fastweixin.api.response.BaseResponse;
import com.eryansky.fastweixin.api.response.PrivateTemplate;
import com.eryansky.fastweixin.api.response.SendTemplateResponse;
import com.eryansky.fastweixin.util.BeanUtil;
import com.eryansky.fastweixin.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 模版消息 api
 */
public class TemplateMsgAPI extends BaseAPI {
    private static final Logger LOG = LoggerFactory.getLogger(CustomAPI.class);

    public TemplateMsgAPI(ApiConfig config) {
        super(config);
    }

    /**
     * 设置行业
     *
     * @param industry 行业参数
     * @return 操作结果
     */
    public ResultType setIndustry(Industry industry) {
        LOG.debug("设置行业......");
        BeanUtil.requireNonNull(industry, "行业对象为空");
        String url = BASE_API_URL + "cgi-bin/template/api_set_industry?access_token=#";
        BaseResponse response = executePost(url, industry.toJsonString());
        return ResultType.get(response.getErrcode());
    }

    /**
     * 获得模板ID
     *
     * @param shortTemplateId 模版短id
     * @return 操作结果
     */
    public AddTemplateResponse addTemplate(String shortTemplateId) {
        LOG.debug("添加模版......");
        BeanUtil.requireNonNull(shortTemplateId, "短模版id必填");
        String url = BASE_API_URL + "cgi-bin/template/api_add_template?access_token=#";
        Map<String, String> params = new HashMap<String, String>();
        params.put("template_id_short", shortTemplateId);
        BaseResponse r = executePost(url, JSONUtil.toJson(params));
        String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
        AddTemplateResponse result = JSONUtil.toBean(resultJson, AddTemplateResponse.class);
        return result;
    }

    /**
     * 发送模版消息
     *
     * @param msg 消息
     * @return 发送结果
     */
    public SendTemplateResponse send(TemplateMsg msg) {
        LOG.debug("发送模版消息......");
        BeanUtil.requireNonNull(msg.getTouser(), "openid is null");
        BeanUtil.requireNonNull(msg.getTemplateId(), "template_id is null");
        BeanUtil.requireNonNull(msg.getData(), "data is null");
//        BeanUtil.requireNonNull(msg.getTopcolor(), "top color is null");
//        BeanUtil.requireNonNull(msg.getUrl(), "url is null");
        String url = BASE_API_URL + "cgi-bin/message/template/send?access_token=#";
        BaseResponse r = executePost(url, msg.toJsonString());
        String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
        SendTemplateResponse result = JSONUtil.toBean(resultJson, SendTemplateResponse.class);
        return result;
    }

    /**
     * 获取已添加至帐号下所有模板列表
     *
     * @return 所有模板
     */
    public PrivateTemplate[] getAllPrivateTemplate() {
        LOG.debug("获取已添加至帐号下所有模板列表......");
        String url = BASE_API_URL + "cgi-bin/template/get_all_private_template?access_token=#";
        BaseResponse r = executeGet(url);
        String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
        PrivateTemplate[] templates = JSONArray.toJavaObject((JSONArray)JSONUtil.getJSONFromString(resultJson).get("template_list"), PrivateTemplate[].class);
        return templates;
    }

    /**
     * 删除模板
     *
     * @param templateId 模板ID
     * @return 删除结果
     */
    public BaseResponse delTemplate(String templateId) {
        LOG.debug("删除模板......");
        BeanUtil.requireNonNull(templateId, "templateId is null");
        String url = BASE_API_URL + "cgi-bin/template/del_private_template?access_token=#";
        Map<String, String> map = new HashMap<String, String>();
        map.put("template_id", templateId);
        BaseResponse r = executePost(url, JSONUtil.toJson(map));
        String resultJson = isSuccess(r.getErrcode()) ? r.getErrmsg() : r.toJsonString();
        return JSONUtil.toBean(resultJson, BaseResponse.class);
    }


}
