package com.lemon.framework.storage.model;

/**
 * <b>名称：存储对象接口</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
public interface StorageInfo {

    /**
     * 文件的唯一索引
     */
    String getKey();

    /**
     * 文件名
     */
    String getName();

    /**
     * 文件类型
     */
    String getType();

    /**
     * 文件大小
     */
    Long getSize();

    /**
     * 文件访问链接
     */
    String getUrl();

}
