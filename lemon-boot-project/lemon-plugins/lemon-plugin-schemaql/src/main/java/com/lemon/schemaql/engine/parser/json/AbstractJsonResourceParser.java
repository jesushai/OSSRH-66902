package com.lemon.schemaql.engine.parser.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.schemaql.config.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
@Slf4j
public abstract class AbstractJsonResourceParser<T extends Schema> implements IJsonParser<T> {

    /**
     * 资源文件的路径，在工程的classpath下
     */
    private String resourcePath;

    private ObjectMapper objectMapper = new ObjectMapper();

    protected AbstractJsonResourceParser(String resourcePath) {
        Assert.isTrue(StringUtils.isNoneBlank(resourcePath), "Resource path must not be empty.");
        this.resourcePath = resourcePath;
    }

    /**
     * json解析mapper，可以覆盖此方法实现自己的mapper
     *
     * @return ObjectMapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * @return 获取Json文件的内容
     */
    protected final String getJsonStringFromResource() {
        try {
            return FileUtils.readFileToString(getResourceFile(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            throw new RuntimeException("Cannot load resource file " + resourcePath);
        }
    }

    protected final File getResourceFile() {
        try {
            ClassPathResource classPathResource = new ClassPathResource(resourcePath);
            return classPathResource.getFile();
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            throw new RuntimeException("Cannot load resource file " + resourcePath);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T parse() {
        File file = getResourceFile();
        try {
            Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return getObjectMapper().readValue(file, clazz);
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            throw new RuntimeException("Cannot parse resource file: " + file.getPath());
        }
    }
}
