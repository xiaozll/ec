/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.sn;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-06-14
 */
public class MaxSerial implements Serializable {

    private Set<MaxSerialItem> items;

    public MaxSerial() {
        items = Sets.newHashSet();
        MaxSerialItem item = new MaxSerialItem();
        this.items.add(item);
    }


    public Set<MaxSerialItem> getItems() {
        return items;
    }

    public MaxSerial setItems(Set<MaxSerialItem> items) {
        this.items = items;
        return this;
    }

    public MaxSerial addIfNotExist(String key, Long value) {
        this.items.add(new MaxSerialItem(key,value));
        return this;
    }

    public MaxSerial update(String key, Long value) {
        MaxSerialItem item = this.items.stream().filter(v -> v.equals(key)).findFirst().orElse(null);
        if (null != item) {
            item.setValue(value);
        }
        return this;
    }

    @Override
    public String toString() {
        return JsonMapper.toJsonString(this);
    }
}
