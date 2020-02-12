package com.eryansky.common.orm.mybatis.type;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2020-02-12
 */
public class IntegerListTypeHandler extends ListTypeHandler<Integer> {

    public IntegerListTypeHandler() {
        super("int");
    }
}