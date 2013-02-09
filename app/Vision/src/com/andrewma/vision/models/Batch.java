package com.andrewma.vision.models;

import java.util.Date;

import com.andrewma.vision.database.core.DbDataType;
import com.andrewma.vision.database.core.annotations.Column;
import com.andrewma.vision.database.core.annotations.PrimaryKey;
import com.andrewma.vision.database.core.annotations.Table;

/**
 * When glasses are added to the inventory in batches, this is used to keep
 * track of which glasses were added into which batch
 * 
 * @author ajma
 * 
 */
@Table(tableName="Batches")
public class Batch {
	@PrimaryKey
	public int BatchId;

	/**
	 * Name of person working on this batch
	 */
	@Column(dataType = DbDataType.TEXT)
	public String Name = "";
	
	/**
	 * Date that the batch was created on
	 */
	@Column(dataType = DbDataType.INTEGER)
	public Date CreatedDate;

	/**
	 * Comma-delimited list of {@link Glasses#GlassesId} that are in this batch
	 */
	@Column(dataType = DbDataType.TEXT)
	public String Glasses = "";
	
	/**
	 * When labels are printed, user can mark the batch as printed
	 */
	@Column(dataType = DbDataType.INTEGER)
	public boolean Printed = false;
}
