package com.eryansky.fastweixin.api.response;

import com.eryansky.fastweixin.api.entity.UserShare;

import java.util.List;

/**
 * @author Eryan
 * @date 2016-03-15
 */
public class GetUserShareResponse extends BaseResponse {

    private List<UserShare> list;

    public List<UserShare> getList() {
        return list;
    }

    public void setList(List<UserShare> list) {
        this.list = list;
    }
}
