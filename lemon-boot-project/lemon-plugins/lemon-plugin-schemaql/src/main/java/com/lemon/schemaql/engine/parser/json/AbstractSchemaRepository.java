package com.lemon.schemaql.engine.parser.json;

import com.lemon.framework.util.JacksonUtils;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.schemaql.config.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;

/**
 * 名称：结构仓库虚拟基类<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
@Slf4j
public abstract class AbstractSchemaRepository<T extends Schema> implements ISchemaRepository<T> {

    /**
     * 资源文件的路径，在工程的classpath下
     */
    private String resourcePath;

    protected AbstractSchemaRepository(String resourcePath) {
        Assert.isTrue(StringUtils.isNoneBlank(resourcePath), "Resource path must not be empty.");
        this.resourcePath = resourcePath;
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
        } catch (FileNotFoundException e) {
            return FileUtils.getFile(resourcePath);
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
            return JacksonUtils.readValue(file, clazz);
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            throw new RuntimeException("Cannot parse resource file: " + file.getPath());
        }
    }

    /**
     * 将对象保存到json文件中
     *
     * @param schema      保存的对象
     * @param deepProcess 是否保存子对象
     * @throws RuntimeException 保存文件失败
     */
    @Override
    public void save(T schema, boolean deepProcess) {
        File file = getResourceFile();
        try {
            FileUtils.write(file,
                    JacksonUtils.parsePrettyString(schema),
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            throw new RuntimeException("Cannot save resource file: " + file.getPath());
        }
    }
}
