package com.mxi.wazooapp.model;

public class offers {

	public String offerid;
	public String ownerid;
	public String name;
	public String descriptions;
	public String audio;
	public String lat;
	public String lng;
	public String address;
	public String price;
	public double distance;
	public String sub_name;
	public String store_name;
	public String category_name;
	public String cat_id;
	public String sub_cat;
	public String sub_cat_id;
	public double score;
	public String valid;
	public String offerImage;
	public boolean hasimage;

	public boolean isHasimage() {
		return hasimage;
	}

	public void setHasimage(boolean hasimage) {
		this.hasimage = hasimage;
	}


	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}


	public String getSub_cat_id() {
		return sub_cat_id;
	}

	public void setSub_cat_id(String sub_cat_id) {
		this.sub_cat_id = sub_cat_id;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}


	/*public String getOffer_value() {
		return offer_value;
	}

	public void setOffer_value(String offer_value) {
		this.offer_value = offer_value;
	}*/

	public String getCat_id() {
		return cat_id;
	}

	public void setCat_id(String cat_id) {
		this.cat_id = cat_id;
	}

	public String getSub_cat() {
		return sub_cat;
	}

	public void setSub_cat(String sub_cat) {
		this.sub_cat = sub_cat;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getSub_name() {
		return sub_name;
	}

	public void setSub_name(String sub_name) {
		this.sub_name = sub_name;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}


	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getOfferid() {
		return offerid;
	}

	public void setOfferid(String offerid) {
		this.offerid = offerid;
	}

	public String getOwnerid() {
		return ownerid;
	}

	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOfferImage() {
		return offerImage;
	}

	public void setOfferImage(String offerImage) {
		this.offerImage = offerImage;
	}
}
