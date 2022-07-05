package com.eryansky.fastweixin.api.response;

import com.eryansky.fastweixin.api.entity.UserRead;

import java.util.List;

/**
 * @author eryan
 * @date 2016-03-15
 */
public class GetUserReadResponse extends BaseResponse {

    private List<UserRead> list;

    public List<UserRead> getList() {
        return list;
    }

    public void setList(List<UserRead> list) {
        this.list = list;
    }
}
