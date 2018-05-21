package com.mxi.wazooapp.model;

import java.util.ArrayList;

public class categorya_list {
	String category_name;
	String category_id;
	String cat_value;
	/*ArrayList<categorya_list>alist;
	public ArrayList<categorya_list> getAlist() {
		return alist;
	}

	public void setAlist(ArrayList<categorya_list> alist) {
		this.alist = alist;
	}*/

	public String getCat_value() {
		return cat_value;
	}

	public void setCat_value(String cat_value) {
		this.cat_value = cat_value;
	}

	ArrayList<scategory_list> sublist;

	public ArrayList<scategory_list> getSublist() {
		return sublist;
	}

	public void setSublist(ArrayList<scategory_list> sublist) {
		this.sublist = sublist;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_img() {
		return category_img;
	}

	public void setCategory_img(String category_img) {
		this.category_img = category_img;
	}

	public String getCategory_description() {
		return category_description;
	}

	public void setCategory_description(String category_description) {
		this.category_description = category_description;
	}

	String category_img;
	String category_description;
}
