package com.andrewma.vision.database.core;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.andrewma.vision.database.core.annotation.Column;
import com.andrewma.vision.database.core.annotation.PrimaryKey;
import com.andrewma.vision.database.core.annotation.Table;

public class DbTable {
	private final List<DbColumn> columns = new LinkedList<DbColumn>();
	private Field primaryKeyField;
	private PrimaryKey primaryKeyAnnotation;
	private Class<?> tableClass;
	private Table tableAnnotation;

	public DbTable(Class<?> clazz, Table annotation) {
		tableClass = clazz;
		tableAnnotation = annotation;
	}

	public Field getPrimaryKeyField() {
		return primaryKeyField;
	}

	public PrimaryKey getPrimaryKeyAnnotation() {
		return primaryKeyAnnotation;
	}

	public void setPrimarykey(Field field, PrimaryKey annotation) {
		primaryKeyField = field;
		primaryKeyAnnotation = annotation;
	}

	public List<DbColumn> columns() {
		return columns;
	}

	public String generateCreateTableSql() {
		final StringBuilder sb = new StringBuilder("create table ")
				.append(getTableName()).append("([")
				.append(primaryKeyField.getName())
				.append("] integer primary key autoincrement");

		for (DbColumn column : columns) {
			final Column annotation = column.columnAnnotation;
			sb.append(", [");
			sb.append(column.getColumnName()).append("] ")
					.append(annotation.dataType().toString().toLowerCase());
			if (!column.columnAnnotation.nullable()) {
				sb.append(" not null");
			}
		}
		sb.append(");");
		return sb.toString();
	}

	public String getTableName() {
		if (tableAnnotation.tableName() != "") {
			return tableAnnotation.tableName();
		} else {
			return tableClass.getSimpleName();
		}
	}
}
