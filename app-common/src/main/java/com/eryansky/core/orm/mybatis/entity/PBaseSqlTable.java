/**
 *  Copyright (c) 2012-2020 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.mybatis.entity;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.io.Serializable;


/**
 * SqlTable支持类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2020-03-03
 */
public abstract class PBaseSqlTable<T, PK extends Serializable> extends SqlTable {

    public final SqlColumn<PK> id = column("id");

    public PBaseSqlTable(String tableName) {
        super(tableName);
    }


}
