package socialtour.socialtour.models;

import java.util.Date;

import socialtour.socialtour.R;

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
	private double dprice;
	private double oprice;
	private int percentdiscount;
	private Date created;
	private int likes;
	private int dislikes;
	
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
	public double getDprice() {
		return dprice;
	}
	public void setDprice(double dprice) {
		this.dprice = dprice;
	}
	public double getOprice() {
		return oprice;
	}
	public void setOprice(double oprice) {
		this.oprice = oprice;
	}
	public int getPercentdiscount() {
		return percentdiscount;
	}
	public void setPercentdiscount(int percentdiscount) {
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
	public int getDislikes() {
		return dislikes;
	}
	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
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
	public int getIcon(){
		if (this.getCategory().equals("Women")){
			return R.drawable.womensmall;
		}else if (this.getCategory().equals("Men")){
			return R.drawable.mensmall;
		}else if (this.getCategory().equals("Cosmetics")){
			return R.drawable.cosmeticssmall;
		}else if (this.getCategory().equals("Digital")){
			return R.drawable.digitalsmall;
		}else if (this.getCategory().equals("Household")){
			return R.drawable.householdsmall;
		}else if (this.getCategory().equals("Kids")){
			return R.drawable.kidssmall;
		}else if (this.getCategory().equals("Entertainment")){
			return R.drawable.entertainmentsmall;
		}else if (this.getCategory().equals("Groceries")){
			return R.drawable.groceriessmall;
		}else if (this.getCategory().equals("Others")){
			return R.drawable.otherssmall;
		}
		return -1;
	}
	
}
