package com.eryansky.fastweixin.company.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.eryansky.fastweixin.api.entity.BaseModel;

public class QYUserOpen extends BaseModel {

    @JSONField(name = "openid")
    private String openId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
