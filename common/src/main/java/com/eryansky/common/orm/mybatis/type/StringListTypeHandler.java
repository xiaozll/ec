package com.eryansky.common.orm.mybatis.type;

public class StringListTypeHandler extends ListTypeHandler<String> {

    public StringListTypeHandler() {
        super("text");
    }
}