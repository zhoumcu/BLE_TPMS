package com.example.sid_fu.blecentral.utils.pinyin;

public class SortModel {

	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	private String ename;

	public int getId() {
		return id;
	}

	private int id;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getEname() {
		return ename;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public void setId(int id) {
		this.id = id;
	}
}
