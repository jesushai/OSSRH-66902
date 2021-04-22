package com.lemon.framework.cache.redis.serializer;

import org.springframework.data.redis.serializer.SerializationException;

import java.io.*;

/**
 * 名称：对象序列化<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/23
 */
public class ObjectSerializer implements RedisSerializer<Object> {

    public byte[] serialize(Object object) throws SerializationException {
        byte[] result = new byte[0];
        if (object == null) {
            return result;
        } else {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
            if (!(object instanceof Serializable)) {
                throw new SerializationException("Requires a Serializable payload but received an object of type [" + object.getClass().getName() + "]");
            } else {
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
                    objectOutputStream.writeObject(object);
                    objectOutputStream.flush();
                    result = byteStream.toByteArray();
                    return result;
                } catch (IOException var5) {
                    throw new SerializationException("Serialize error, object=" + object, var5);
                }
            }
        }
    }

    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes != null && bytes.length != 0) {
            try {
                ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new MultiClassLoaderObjectInputStream(byteStream);
                return objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException var5) {
                throw new SerializationException("Deserialize error. ", var5);
            }
        } else {
            return null;
        }
    }
}
