package com.eryansky.codegen.generate;

import com.eryansky.codegen.vo.Table;

import java.util.List;

/**
 * 根据模板生成文件
 */
public interface Generate {

	public void generate(Table table) throws Exception;

	public void generate(List<Table> tables) throws Exception;

}
