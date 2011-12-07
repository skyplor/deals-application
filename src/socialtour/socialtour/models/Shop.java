package socialtour.socialtour.models;

import socialtour.socialtour.R;

public class Shop {
	public int id;
	public String address;
	public String name;
	public String lat;
	public String lng;
	public String type;
	public String distance;
	
	public Shop(){
		
	}
	public Shop(String add, String nm, String lt, String lg)
	{
		address = add;
		name = nm;
		lat = lt;
		lng = lg;
	}
	public Shop(String add, String nm, String lt, String lg, String dt)
	{
		address = add;
		name = nm;
		lat = lt;
		lng = lg;
		distance = dt;
	}
	public Shop(int id, String add, String nm, String lt, String lg, String type)
	{
		this.id = id;
		this.type = type;
		address = add;
		name = nm;
		lat = lt;
		lng = lg;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAddress()
	{
		return address;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getLat()
	{
		return lat;
	}
	
	public String getLng()
	{
		return lng;
	}
	
	public String getDist()
	{
		return distance;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getIcon(){
		if (this.getType().equals("men")){
			return R.drawable.men;
		}else if (this.getType().equals("women")){
			return R.drawable.women;
		}else if (this.getType().equals("children")){
			return R.drawable.children;
		}else if (this.getType().equals("unisex")){
			return R.drawable.unisex;
		}else if (this.getType().equals("bags")){
			return R.drawable.bag;
		}else if (this.getType().equals("accessories")){
			return R.drawable.accessories;
		}else if (this.getType().equals("shoes")){
			return R.drawable.shoe;
		}
		return -1;
	}
	
}
