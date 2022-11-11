package com.eryansky.fastweixin.api.response;

import com.eryansky.fastweixin.api.entity.UpstreamMsgMonth;

import java.util.List;

/**
 * @author Eryan
 * @date 2016-03-15
 */
public class GetUpstreamMsgMonthResponse extends BaseResponse {

    private List<UpstreamMsgMonth> list;

    public List<UpstreamMsgMonth> getList() {
        return list;
    }

    public void setList(List<UpstreamMsgMonth> list) {
        this.list = list;
    }
}
