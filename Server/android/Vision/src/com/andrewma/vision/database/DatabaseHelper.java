package com.andrewma.vision.database;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;

import com.andrewma.vision.database.core.DbColumn;
import com.andrewma.vision.database.core.DbTable;
import com.andrewma.vision.database.core.annotation.Column;
import com.andrewma.vision.database.core.annotation.PrimaryKey;
import com.andrewma.vision.database.core.annotation.Table;
import com.andrewma.vision.models.Glasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "SQLite";
	private static final String DATABASE_NAME = "vision.db";
	private static final int DATABASE_VERSION = 1;

	private static DatabaseHelper instance = null;
	private final Map<Class<?>, DbTable> tables = new Hashtable<Class<?>, DbTable>();

	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context.getApplicationContext());
		}
		return instance;
	}

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		registerModel(Glasses.class);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (DbTable table : tables.values()) {
			final String createTableSql = table.generateCreateTableSql();
			Log.v(TAG, createTableSql);
			db.execSQL(createTableSql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private void registerModel(Class<?> clazz) {
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

			tables.put(clazz, table);
		}
	}

	private DbTable lookupModelTable(Class<?> modelClass) {
		if (!tables.containsKey(modelClass)) {
			Log.e(TAG,
					"Could not insert object of class "
							+ modelClass.getSimpleName());
			return null;
		} else {
			return tables.get(modelClass);
		}
	}

	public void insert(Object model) {
		final DbTable table = lookupModelTable(model.getClass());
		if(table == null) {
			return;
		}
		
		final ContentValues contentValues = table.getContentValues(model);
		final SQLiteDatabase db = getWritableDatabase();
		try {
			db.insert(table.getTableName(), null, contentValues);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public void delete(Object model) {
		final DbTable table = lookupModelTable(model.getClass());
		if(table == null) {
			return;
		}
		
		final SQLiteDatabase db = getWritableDatabase();
		try {
			String where = table.getPrimaryKeyName() + "=" + table.getPrimaryKeyField().getInt(model);
			db.delete(table.getTableName(), where, null);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
}
