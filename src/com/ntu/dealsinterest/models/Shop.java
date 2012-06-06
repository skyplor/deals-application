package com.ntu.dealsinterest.models;

import com.ntu.dealsinterest.R;

public class Shop {
	public int id;
	public String address;
	public String name;
	public String lat;
	public String lng;
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
	public Shop(int id, String add, String nm, String lt, String lg)
	{
		this.id = id;
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
	
}
