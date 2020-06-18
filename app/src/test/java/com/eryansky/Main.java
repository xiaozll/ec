/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky;

import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.collections.ListUtils;
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
        String[] arr = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        List<List<String>> data = new ArrayList<List<String>>();
        int group = 7;
        List<List<String>> ps = Collections3.averageAssign(Lists.newArrayList(arr),group);
        ps.forEach(v->{
            System.out.println(JsonMapper.toJsonString(v));
        });
//        int groupSize = Double.valueOf(Math.floor(arr.length/group)).intValue();
////        int groupSize = ( arr.length + group - 1 )/group;
//        System.out.println(groupSize);
//        List<List> ls = Collections3.splitList(Lists.newArrayList(arr),groupSize);
//        List<List> ps;
//        if(ls.size() > group){
//            ps = new ArrayList<>(ls.subList(0,group));
//            List<List> _cs =ls.subList(group,ls.size());
//            List<List> cs =new ArrayList<>(_cs);
//            _cs.clear();
//            for(int i=0;i<cs.size();i++){
//                List item = cs.get(i);
//                ps.get(i).addAll(item);
//            }
//        }else {
//            ps = ls;
//        }
//        ps.forEach(v->{
//            System.out.println(JsonMapper.toJsonString(v));
//        });

    }
}
