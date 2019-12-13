package com.eryansky.common.orm.mybatis.sensitive.annotation;

import java.lang.annotation.*;

/**
 * 对json内的key_value进行脱敏
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2019-12-13
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SensitiveJSONField {
    /**
     * 需要脱敏的字段的数组
     *
     * @return 返回结果
     */
    SensitiveJSONFieldKey[] sensitivelist();
}
