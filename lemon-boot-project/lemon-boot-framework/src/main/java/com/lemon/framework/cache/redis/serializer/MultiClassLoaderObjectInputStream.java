package com.lemon.framework.cache.redis.serializer;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/23
 */
@Slf4j
public class MultiClassLoaderObjectInputStream extends ObjectInputStream {

    MultiClassLoaderObjectInputStream(InputStream str) throws IOException {
        super(str);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();

        ClassLoader cl;
        try {
            cl = Thread.currentThread().getContextClassLoader();
            return Class.forName(name, false, cl);
        } catch (Throwable var6) {
            LoggerUtils.debug(log, "Cannot access thread context ClassLoader!", var6);

            try {
                cl = MultiClassLoaderObjectInputStream.class.getClassLoader();
                return Class.forName(name, false, cl);
            } catch (Throwable var5) {
                LoggerUtils.debug(log, "Cannot access application ClassLoader", var5);

                try {
                    cl = ClassLoader.getSystemClassLoader();
                    return Class.forName(name, false, cl);
                } catch (Throwable var4) {
                    LoggerUtils.debug(log, "Cannot access system ClassLoader", var4);
                    return super.resolveClass(desc);
                }
            }
        }
    }
}
