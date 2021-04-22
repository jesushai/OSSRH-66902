package com.lemon.framework.storage.model;

/**
 * 名称：存储对象接口<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
public interface StorageInfo {

    /**
     * @return 文件的唯一索引
     */
    String getKey();

    /**
     * @return 文件名
     */
    String getName();

    /**
     * @return 文件类型
     */
    String getType();

    /**
     * @return 文件大小
     */
    Long getSize();

    /**
     * @return 文件访问链接
     */
    String getUrl();

}
