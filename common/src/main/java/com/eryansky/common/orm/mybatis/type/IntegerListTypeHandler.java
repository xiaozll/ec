package com.eryansky.common.orm.mybatis.type;

public class IntegerListTypeHandler extends ListTypeHandler<Integer> {

    public IntegerListTypeHandler() {
        super("int");
    }
}