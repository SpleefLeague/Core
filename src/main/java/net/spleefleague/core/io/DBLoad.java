/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.spleefleague.core.utils.TypeConverter;

/**
 *
 * @author Jonas
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Inherited
public @interface DBLoad {
    String fieldName();
    Class<? extends TypeConverter> typeConverter() default TypeConverter.class;
}
