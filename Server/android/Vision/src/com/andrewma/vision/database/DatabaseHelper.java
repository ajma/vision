package com.andrewma.vision.database;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.andrewma.vision.database.core.DbColumn;
import com.andrewma.vision.database.core.DbTable;
import com.andrewma.vision.database.core.annotation.Column;
import com.andrewma.vision.database.core.annotation.PrimaryKey;
import com.andrewma.vision.database.core.annotation.Table;
import com.andrewma.vision.models.Glasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "SQLite";
	private static final String DATABASE_NAME = "vision.db";
	private static final int DATABASE_VERSION = 1;

	private static DatabaseHelper instance = null;
	private final List<DbTable> tables = new LinkedList<DbTable>();

	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context.getApplicationContext());
		}
		return instance;
	}

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		readClass(Glasses.class);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (DbTable table : tables) {
			final String createTableSql = table.generateCreateTableSql();
			Log.v(TAG, createTableSql);
			db.execSQL(createTableSql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private void readClass(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Table.class)) {
			final DbTable table = new DbTable(clazz,
					(Table) clazz.getAnnotation(Table.class));

			final Field[] fields = clazz.getFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(PrimaryKey.class)) {
					final PrimaryKey annotation = (PrimaryKey) field
							.getAnnotation(PrimaryKey.class);
					table.setPrimarykey(field, annotation);
				} else if (field.isAnnotationPresent(Column.class)) {
					final Column annotation = (Column) field
							.getAnnotation(Column.class);
					table.columns().add(new DbColumn(field, annotation));
				}
			}

			tables.add(table);
		}
	}
}
