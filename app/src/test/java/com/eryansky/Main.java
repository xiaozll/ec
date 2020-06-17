/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky;

import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2020-06-17 
 */
public class Main {
    public static void main(String[] args) {
        String[] arr = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11" };
        List<List<String>> data = new ArrayList<List<String>>();
        int group = 6;
        int groupSize = ( arr.length + group - 1 )/group;
        List<List> ls = Collections3.splitList(Lists.newArrayList(arr),groupSize);
        ls.forEach(v->{
            System.out.println(JsonMapper.toJsonString(v));
        });

    }
}
