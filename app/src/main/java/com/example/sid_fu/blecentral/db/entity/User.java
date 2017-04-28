package com.example.sid_fu.blecentral.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;

@DatabaseTable(tableName = "tb_user")
public class User implements Serializable
{
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(columnName = "name")
	private String name;

	@DatabaseField(columnName = "photoNumber")
	private String photoNumber;

	@DatabaseField(columnName = "passWord")
	private String passWord;


	@ForeignCollectionField
	private Collection<Device> devices;

	public Collection<Device> getDevices()
	{
		return devices;
	}

	public void setDevices(Collection<Device> devices)
	{
		this.devices = devices;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getPhotoNumber() {
		return photoNumber;
	}

	public void setPhotoNumber(String photoNumber) {
		this.photoNumber = photoNumber;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "User [id=" + id + ", name=" + name + ", articles=" + devices + ", photoNumber=" + photoNumber+ ", passWord=" + passWord
				+ "]";
	}
}
