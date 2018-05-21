package com.mxi.wazooapp.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mxi.wazooapp.db.SQLiteWander;
import com.mxi.wazooapp.model.categorya_list;
import com.mxi.wazooapp.model.general_bus;
import com.mxi.wazooapp.model.offers;
import com.mxi.wazooapp.model.scategory_list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class JsonGetData {

	public static ArrayList<categorya_list> arraylist = new ArrayList<categorya_list>();
	public ArrayList<scategory_list> subcatlistarraylist;

	public static ArrayList<offers> offeralist = new ArrayList<offers>();
	public static ArrayList<offers> tempofferalist = new ArrayList<offers>();
	public static ArrayList<general_bus> generalbusArray;
	CommonClass cc;
	Context context = null;
	SQLiteWander dbcon;


	public void fatchCategory(Context mcontext,String jsonStr) {

		context = mcontext;
		dbcon = new SQLiteWander(context);
		dbcon.deleteTable();
		arraylist.clear();
		cc=new CommonClass(context);
		if (jsonStr != null) {
			try {
				JSONObject jsonObj = new JSONObject(jsonStr);

				// Getting JSON Array node
				JSONArray data = jsonObj.getJSONArray("data");

				// Retrieve  category data through cursor
				Cursor local = dbcon.getLocalSetting();
				// Retrieve category add price value through cursor
				Cursor pref = dbcon.getPref();

				JSONObject c = null;
				// looping through All Category
				for (int i = 0; i < data.length(); i++) {
					c = data.getJSONObject(i);
					JSONObject category = c.getJSONObject("category");
					categorya_list project = new categorya_list();

					String category_name = category.getString("category_name");
					String category_img = category.getString("category_img");
					String category_id = category.getString("category_id");
					String category_description = category
							.getString("category_description");

					project.setCategory_name(category_name);
					project.setCategory_img(category_img);
					project.setCategory_id(category_id);
					project.setCategory_description(category_description);

					// insert all category data into database
					dbcon.insertCategory(category.getString("category_id"),
							category.getString("category_name"),
							category.getString("category_description"),
							category.getString("category_img"));

					if (local.getCount() == 0) {
						dbcon.insertLocalSetting(
								category.getString("category_id"),
								category.getString("category_name"), "0");

						dbcon.insertTravelSetting(
								category.getString("category_id"),
								category.getString("category_name"), "0");
					}

					JSONObject subcategory = c.getJSONObject("subcategory");
					subcatlistarraylist = new ArrayList<scategory_list>();
					JSONArray subcategory_list = subcategory
							.getJSONArray("subcategory_list");
					for (int j = 0; j < subcategory_list.length(); j++) {
						JSONObject d = subcategory_list.getJSONObject(j);

						scategory_list sublist = new scategory_list();
						String subcat_id = d.getString("subcat_id");
						String cat_id = d.getString("cat_id");
						String subcat_name = d.getString("subcat_name");
						String subcat_description = d
								.getString("subcat_description");
						sublist.setCat_id(cat_id);
						sublist.setSubcat_name(subcat_name);
						sublist.setSubcat_description(subcat_description);
						sublist.setSubcat_id(subcat_id);

						// insert all subcategory data into database
						dbcon.insertSubCategory(d.getString("cat_id"),
								d.getString("subcat_id"),
								d.getString("subcat_name"),
								d.getString("subcat_description"));

						if (pref.getCount() == 0) {
							dbcon.insertPref(d.getString("cat_id"),
									d.getString("subcat_id"), "");
						}

						subcatlistarraylist.add(sublist);
						Log.e("menu sublist", subcatlistarraylist.size() + "");
					}
					project.setSublist(subcatlistarraylist);
					arraylist.add(project);
				}
				Log.e("arraylist", arraylist.size() + "");

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "can't get any data from the url");
			cc.showToast("No data");

		}
	}


	/*
		changed on 23-09-2016
		set flags to show offer dialog or show local business pick dialog
	 */
	// Vollay method
	public void parseData(Context mContext, String response) {
		context = mContext;

		Log.e("offer we respose ==== ", response);

		cc=new CommonClass(context);
		cc.savePrefString("getPartnerOffers", "");
		cc.savePrefString("getPartnerOffers", response);
		dbcon = new SQLiteWander(context);
				if (response != null) {
					try {

						JSONObject jObject = new JSONObject(response);

						if (jObject.getString("status").equals("1")) {

							//Delete Table partner offer first
							dbcon.deletePartnerOffer();

							JSONObject jOffer =jObject.getJSONObject("offer");
							offeralist.clear();
							tempofferalist.clear();
							JSONArray offerArray = jOffer.getJSONArray("offer_list");
							for (int i = 0; i < offerArray.length(); i++) {

								JSONObject jObject2 = offerArray.getJSONObject(i);

								offers data = new offers();

								data.setOfferid(jObject2.getString("offerid"));
								data.setOwnerid(jObject2.getString("ownerid"));
								data.setName(jObject2.getString("name"));
								data.setDescriptions(jObject2.getString("descriptions"));
								Log.e("distance... ", "dhaval distance... " + jObject2.getString("distance"));
								data.setDistance(Double.parseDouble(jObject2.getString("distance")));
								data.setAudio(jObject2.getString("audio"));
								data.setLat(jObject2.getString("lat"));
								data.setLng(jObject2.getString("long"));
								data.setAddress(jObject2.getString("address"));
								data.setAudio(jObject2.getString("audio"));
								data.setPrice(jObject2.getString("price"));
								data.setSub_name(jObject2.getString("sub_name"));
								data.setOfferImage(jObject2.getString("imagename"));

								data.setHasimage(jObject2.getBoolean("hasimage"));
								if(jObject2.getBoolean("hasimage")){
									Log.e("HasImage","true");
                                    Log.e("true",jObject2.getString("imagename")+"");
                                    Log.e("true",jObject2.getString("descriptions")+"");
								}else{
									Log.e("HasImage","false");
                                    Log.e("false",jObject2.getString("imagename")+"");
                                    Log.e("false",jObject2.getString("descriptions")+"");
								}

								data.setStore_name(jObject2.getString("store_name").replace("'", ""));
								// applied Changes - 1
								String subcat_id = jObject2.getString("sub_cat");
								data.setSub_cat_id(subcat_id);

								String cat_id = jObject2.getString("cat_id");
								data.setCat_id(cat_id);
								int catPref_val;
								if (cc.loadPrefString("mode").equals("local")) {
									catPref_val = Integer.parseInt(dbcon.getLocalPrefByCatId(cat_id));
								} else {
									catPref_val = Integer.parseInt(dbcon.getTravelPrefByCatId(cat_id));
								}
								Log.e("Dhaval...", "catPref_val = " + catPref_val);
								int subCatPref_val=Integer.parseInt(dbcon.getPrefBySubcatId(subcat_id));
								Log.e("Dhaval...", "subCatPref_val = " + subCatPref_val);
								double distance= data.getDistance();
								//formula aaplied to calculate the score
								double score = subCatPref_val * (Double.parseDouble(cc.loadPrefString("partner_miles"))-distance) * catPref_val;
								Log.e("Dhaval...", "price = " + jObject2.getString("price"));
								Log.e("Dhaval...", "distance = " + distance);
								Log.e("Dhaval...", "partner_miles = " + Double.parseDouble(cc.loadPrefString("partner_miles")));
								Log.e("Dhaval...", "score = " + score);
								DecimalFormat precision = new DecimalFormat("0.000000");
								precision.format(score);
								data.setScore(score);

								if (!jObject2.getString("start_time").equals("")) {
									data.setValid("Valid Between : "+jObject2.getString("start_time")+" - "+jObject2.getString("end_time")+" "+jObject2.getString("day"));
								} else {
									data.setValid("");
								}

								//Log.e("OfferList",offeralist);
								offeralist.add(data);
//								Log.e("get_offer_list", offeralist.size() + "");

								dbcon.insertPartnerOffer(Integer.parseInt(data.getOfferid()), data.getStore_name(), data.getSub_name(), String.valueOf(data.getDistance()), data.getCategory_name(), data.getPrice(), data.getAddress(), data.getLat(), data.getLng(), data.getDescriptions(),"no",data.getCat_id(),data.getSub_cat(),data.getValid());
						}

							if (offeralist.size() > 0) {
								Collections.sort(offeralist, new Comparator<offers>() {
									public int compare(offers obj1, offers obj2) {
										// TODO Auto-generated method stub
										return (obj1.score < obj2.score) ? -1
												: (obj1.score > obj2.score) ? 1 : 0;
									}
								});
								Collections.reverse(offeralist);

								cc.savePrefBoolean("isOfferFound", true);

							} else {
								cc.savePrefBoolean("isOfferFound", false);
							}
						}

//------------------------------->
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					Log.e("ServiceHandler", "can't get any data from the url");
					cc.showToast("No data");
				}

	}

}
