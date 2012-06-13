package com.ntu.dealsinterest.models;

import java.util.Date;

import com.ntu.dealsinterest.R;

public class Product {
	private int id;
	private int userid;
	private String usertype;
	private String user_name;
	private String filename;
	private String sourcepath;
	private String url;
	private String type;
	private int shopid;
	private String category;
	private String subcategory;
	private String brand;
	private String dprice;
	private String oprice;
	private String percentdiscount;
	private Date created;
	private int likes;
	private int remarks;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getSourcepath() {
		return sourcepath;
	}
	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getShopid() {
		return shopid;
	}
	public void setShopid(int shopid) {
		this.shopid = shopid;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getDprice() {
		return dprice;
	}
	public void setDprice(String dprice) {
		this.dprice = dprice;
	}
	public String getOprice() {
		return oprice;
	}
	public void setOprice(String oprice) {
		this.oprice = oprice;
	}
	public String getPercentdiscount() {
		return percentdiscount;
	}
	public void setPercentdiscount(String percentdiscount) {
		this.percentdiscount = percentdiscount;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
	public int getRemarks() {
		return remarks;
	}
	public void setRemarks(int dislikes) {
		this.remarks = dislikes;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsertype() {
		return usertype;
	}
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
	public String getSubcategory() {
		return subcategory;
	}
	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
}
