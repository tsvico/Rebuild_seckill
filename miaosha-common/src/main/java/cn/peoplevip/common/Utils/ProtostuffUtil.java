package cn.peoplevip.common.Utils;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/24 21:50
 * 功能
 */
public class ProtostuffUtil {
    /**
     * 序列化
     */
    public static <T> byte[] serializer(T t) {
        Schema schema = RuntimeSchema.getSchema(t.getClass());
        return ProtostuffIOUtil.toByteArray(t, schema,
                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

    }

    /**
     * 反序列化
     */
    public static <T> T deserializer(byte[] bytes, Class<T> c) {
        if (bytes == null) {
            return null;
        }
        T t = null;
        try {
            t = c.newInstance();
            Schema<T> schema = RuntimeSchema.getSchema(c);
            ProtostuffIOUtil.mergeFrom(bytes, t, schema);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }
}
