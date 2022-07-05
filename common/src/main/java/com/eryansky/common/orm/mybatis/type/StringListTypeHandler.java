package com.eryansky.common.orm.mybatis.type;

/**
 * @author eryan
 * @version 2020-02-12
 */
public class StringListTypeHandler extends ListTypeHandler<String> {

    public StringListTypeHandler() {
        super("text");
    }
}