package com.eryansky.common.utils.jackson;

import com.eryansky.common.orm._enum.GenericEnumUtils;
import com.eryansky.common.orm._enum.IGenericEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * IGenericEnum 类型枚举类转换
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2020-02-12
 *
 */
public class GenericEnumDeserializer<T extends Enum<T> & IGenericEnum<T>> extends JsonDeserializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(GenericEnumDeserializer.class);

    private final Class<T> type;

    public GenericEnumDeserializer(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }


    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getText();
        try {
            return value == null ? null : GenericEnumUtils.getByValue(type,value);
        } catch (Exception e) {
            logger.error("解析IGenericEnum错误", e);
            return null;
        }
    }

}