package com.lemon.framework.storage;

import com.lemon.framework.storage.model.StorageInfo;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 名称：存储信息服务<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/29
 */
@RequestMapping("/storage")
public interface StorageInfoService<T extends StorageInfo> {

    /**
     * 保存对象存储信息到数据库
     *
     * @param fileName      文件名
     * @param contentLength 大小
     * @param contentType   文件类型
     * @param key           key
     * @param url           url
     * @return 存储对象实体
     */
    T save(String fileName, Long contentLength, String contentType, String key, String url);

    /**
     * 查询对象存储信息
     *
     * @param key key
     * @return 存储对象实体
     */
    T findByKey(String key);

    /**
     * 对象存储信息是否存在
     *
     * @param key key
     * @return 是否已经存在
     */
    boolean existsByKey(String key);
}
