package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class InterfaceSummaryHour extends InterfaceSummary {

    @JSONField(name = "ref_hour")
    private Integer refHour;

    public Integer getRefHour() {
        return refHour;
    }

    public InterfaceSummaryHour setRefHour(Integer refHour) {
        this.refHour = refHour;
        return this;
    }
}
