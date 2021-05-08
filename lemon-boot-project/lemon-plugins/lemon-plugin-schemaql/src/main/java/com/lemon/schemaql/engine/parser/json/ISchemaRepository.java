package com.lemon.schemaql.engine.parser.json;

import com.lemon.schemaql.config.Schema;

/**
 * 名称：Schema仓库标示接口<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public interface ISchemaRepository<T extends Schema> {

    /**
     * 读取json文件并解析对象
     *
     * @return 解析后的机构
     */
    T parse();

    /**
     * 将结构对象保存到json文件中
     *
     * @param schema      保存的结构对象
     * @param deepProcess 是否保存子结构对象
     * @throws RuntimeException 保存文件失败
     */
    void save(T schema, boolean deepProcess);
}
