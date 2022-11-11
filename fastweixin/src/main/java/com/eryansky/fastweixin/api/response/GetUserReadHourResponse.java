package com.eryansky.fastweixin.api.response;

import com.eryansky.fastweixin.api.entity.UserReadHour;

import java.util.List;

/**
 * @author Eryan
 * @date 2016-03-15
 */
public class GetUserReadHourResponse extends BaseResponse {

    private List<UserReadHour> list;

    public List<UserReadHour> getList() {
        return list;
    }

    public void setList(List<UserReadHour> list) {
        this.list = list;
    }
}
