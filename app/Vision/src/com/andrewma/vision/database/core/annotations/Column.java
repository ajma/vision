package com.andrewma.vision.database.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.andrewma.vision.database.core.DbDataType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	String columnName() default "";
	DbDataType dataType() default DbDataType.TEXT;
	boolean	nullable() default false;
}
