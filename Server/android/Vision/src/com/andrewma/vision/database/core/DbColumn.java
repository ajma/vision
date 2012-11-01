package com.andrewma.vision.database.core;

import java.lang.reflect.Field;

import com.andrewma.vision.database.core.annotation.Column;

public class DbColumn {
	public DbColumn(Field field, Column annotation) {
		columnField = field;
		columnAnnotation = annotation;
	}
	
	public Field columnField;
	public Column columnAnnotation;
	
	public String getColumnName() {
		if(columnAnnotation.columnName() != "") {
			return columnAnnotation.columnName();
		} else {
			return columnField.getName();
		}
	}
}
