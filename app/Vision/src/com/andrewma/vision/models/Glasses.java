package com.andrewma.vision.models;

import java.util.Date;

import com.andrewma.vision.database.core.DbDataType;
import com.andrewma.vision.database.core.annotations.Column;
import com.andrewma.vision.database.core.annotations.PrimaryKey;
import com.andrewma.vision.database.core.annotations.Table;

@Table
public class Glasses {
	@PrimaryKey
	public int GlassesId;

	@Column(dataType = DbDataType.INTEGER)
	public int Group;
	@Column(dataType = DbDataType.INTEGER)
	public int Number;

	@Column(dataType = DbDataType.INTEGER)
	public boolean OD_Blind;
	@Column(dataType = DbDataType.REAL)
	public float OD_Spherical;
	@Column(dataType = DbDataType.REAL)
	public float OD_Cylindrical;
	@Column(dataType = DbDataType.INTEGER)
	public int OD_Axis;
	@Column(dataType = DbDataType.REAL)
	public float OD_Add;

	@Column(dataType = DbDataType.INTEGER)
	public boolean OS_Blind;
	@Column(dataType = DbDataType.REAL)
	public float OS_Spherical;
	@Column(dataType = DbDataType.REAL)
	public float OS_Cylindrical;
	@Column(dataType = DbDataType.INTEGER)
	public int OS_Axis;
	@Column(dataType = DbDataType.REAL)
	public float OS_Add;

	@Column(dataType = DbDataType.TEXT, nullable = true)
	public Boolean Sunglasses;
	@Column(dataType = DbDataType.TEXT, nullable = true)
	public char Size;
	@Column(dataType = DbDataType.TEXT, nullable = true)
	public char Gender;

	@Column(dataType = DbDataType.INTEGER)
	public Date AddedDate;
	@Column(dataType = DbDataType.INTEGER)
	public Date RemovedDate;
	@Column(dataType = DbDataType.TEXT, nullable = true)
	public String RemovedReason;
	@Column(dataType = DbDataType.TEXT, nullable = true)
	public String RemovedBy;
}
