package com.mxi.wazooapp.model;

public class scategory_list {
	String subcat_id;
	String subcat_name;
	String cat_id;
	public String getCat_id() {
		return cat_id;
	}
	public void setCat_id(String cat_id) {
		this.cat_id = cat_id;
	}
	public String getSubcat_id() {
		return subcat_id;
	}
	public void setSubcat_id(String subcat_id) {
		this.subcat_id = subcat_id;
	}
	public String getSubcat_name() {
		return subcat_name;
	}
	public void setSubcat_name(String subcat_name) {
		this.subcat_name = subcat_name;
	}
	public String getSubcat_description() {
		return subcat_description;
	}
	public void setSubcat_description(String subcat_description) {
		this.subcat_description = subcat_description;
	}
	String subcat_description;
}
