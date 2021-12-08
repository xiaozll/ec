/**
 * Copyright (c) 2012-2020 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky;

import com.eryansky.common.utils.ThreadUtils;
import com.eryansky.common.utils.http.HttpCompoents;
import com.eryansky.common.utils.http.HttpPoolCompoents;
import org.apache.http.client.fluent.Response;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2020-06-17 
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        HttpPoolCompoents httpCompoents = HttpPoolCompoents.getInstance();
        HttpCompoents httpCompoents = HttpCompoents.getInstance();
//        System.out.println(httpCompoents.get("https://mp.jxtobacco.gov.cn/dcs/rest/is/item/list?appCode=mp&appKey=mp"));
//        for(int i=0;i<100;i++){
//            System.out.println(httpCompoents.get("http://localhost:8081/dcs/rest/dc/demo/test?appCode=mp&appKey=mp" ));;
//            Response response = httpCompoents.getResponse("http://localhost:8081/dcs/rest/dc/demo/test?test=1&appCode=mp&appKey=mp",null );
//            System.out.println(i);
//            System.out.println(response.returnContent());
//
//        }
        for(int i=0;i<100;i++){

            int finalI = i;
            new Thread(() -> {
                Response response = null;
                try {
                    System.out.println(finalI);
                    for(int m=0;m<100;m++){
                       httpCompoents.get("http://www.jfit.com.cn/nexus/repository/releases/com/eryansky/ec-app/maven-metadata.xml" );
                    }
//                    response = httpCompoents.getResponse("http://localhost:8081/dcs/rest/dc/demo/test?test=1&appCode=mp&appKey=mp",null );
//                System.out.println(response.returnContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }


        ThreadUtils.sleep(30*1000);

    }
}
