package com.mxi.wazooapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class SQLiteWander {

	Context mcon;
	private static String dbname = "wazoomobileapp9.db";
	static String db_path = Environment.getExternalStorageDirectory()
			.toString() + "/"+dbname;
		SQLiteDatabase db;
	public static final String KEY_ROWID = "id";

	public SQLiteWander(Context con) {
		// TODO Auto-generated constructor stub
		mcon = con;

		db = mcon.openOrCreateDatabase(db_path, Context.MODE_PRIVATE, null);

		// Database Table for store all category list
		db.execSQL("CREATE TABLE IF NOT EXISTS category(id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "category_id VARCHAR, category_name VARCHAR, category_description VARCHAR"
				+ ",category_img VARCHAR); ");

		// Database Table for store all sub category list
		db.execSQL("CREATE TABLE IF NOT EXISTS sub_category(id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "cat_id VARCHAR, subcat_id VARCHAR, subcat_name VARCHAR ,subcat_description VARCHAR); ");

		// Database Table for store all pred values of category
		db.execSQL("CREATE TABLE IF NOT EXISTS pref_values(id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "category_id VARCHAR, subcat_id VARCHAR, pref_value VARCHAR); ");

		// Database Table for store local setting
		db.execSQL("CREATE TABLE IF NOT EXISTS local_setting(id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "category_id VARCHAR,category_name VARCHAR,slider_value VARCHAR); ");

		// Database Table for store Travel setting
				db.execSQL("CREATE TABLE IF NOT EXISTS travel_setting(id INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ "category_id VARCHAR,category_name VARCHAR,slider_value VARCHAR); ");

		// Database Table for offers
				db.execSQL("CREATE TABLE IF NOT EXISTS parteneroffers(offerid INTEGER PRIMARY KEY, "
						+ "store_name VARCHAR, sub_name VARCHAR, distance VARCHAR" +
						",category_name VARCHAR, price VARCHAR, address VARCHAR" +
						",lat VARCHAR, lng VARCHAR, descriptions VARCHAR, isvisit VARCHAR,cat_id VARCHAR,sub_cat VARCHAR,valid VARCHAR); ");

		/*created on 24-09-2016*/
		// Database Table for already visited offers
		db.execSQL("CREATE TABLE IF NOT EXISTS visitedoffers(offer_id VARCHAR); ");

		// Database Table for store Travel setting
				db.execSQL("CREATE TABLE IF NOT EXISTS my_receipts(id INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ "offerid VARCHAR,file_path VARCHAR,lat VARCHAR" +
						",lng VARCHAR,store_name VARCHAR,sub_name VARCHAR, price VARCHAR ,descriptions VARCHAR, file_name VARCHAR, redeem VARCHAR); ");

		// Database Table for local Business Pick
		db.execSQL("CREATE TABLE IF NOT EXISTS localbusinesspick(id INTEGER PRIMARY KEY, "
				+ "name VARCHAR, tag_name VARCHAR, distance VARCHAR" +
				", phone VARCHAR, address VARCHAR" +
				",place_lat VARCHAR, place_long VARCHAR,image VARCHAR,rating VARCHAR,score VARCHAR); ");

		/*created on 26-09-2016*/
		// Database Table for already visited local Business Pick
		db.execSQL("CREATE TABLE IF NOT EXISTS visitedlocalbusiness(localbusiness_id VARCHAR); ");

	}

	/////////////////////////////////////////////Local Businees Pick/////////////////////////////////////////
	// Insert data in Local Business pick
	public void insertLocalBusinessPick(int id,String name, String tag_name,
								 String distance,String phone, String address,String place_lat,String place_long,String image, String rating,double score) {

		String query = "INSERT INTO localbusinesspick(id,name,tag_name,distance,phone,address,place_lat,place_long,image,rating,score)VALUES ('"
				+ id
				+ "','"
				+ name
				+ "','"
				+ tag_name
				+ "','"
				+ distance
				+ "','"
				+ phone
				+ "','"
				+ address
				+ "','"
				+ place_lat
				+ "','"
				+ place_long
				+ "','"
				+ image
				+ "','"
				+ rating
                + "','"
                + score
				+ "')";
//		Log.e("Query my_receipts", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	// get the all records from the local business pick table
    public Cursor getLocalBusinessPick() {
        Cursor cur = null;
        try {
            String query = "SELECT  * FROM  localbusinesspick";
            cur = db.rawQuery(query, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
			e.printStackTrace();
        }

        return cur;
    }
	// delete local business pick table
    public void deleteLocalBusinessPickTable(){
        try {
            db.execSQL("delete from localbusinesspick");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


	public void updateDefaultPref() {

		String query1 = "UPDATE pref_values SET pref_value = '3'";
		String query2 = "UPDATE local_setting SET slider_value = '3'";
		String query3 = "UPDATE travel_setting SET slider_value = '3'";

		
		try {
			db.execSQL(query1);
			db.execSQL(query2);
			db.execSQL(query3);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}



	/////////////////////////// My Receipts Page ////////////////////////////////////////////////////

	public void insertMyReceipts(String offerid, String file_path,
			String lat,String lng,String store_name, String sub_name,String price,String descriptions, String file_name, String redeem) {

		String query = "INSERT INTO my_receipts(offerid,file_path,lat,lng,store_name,sub_name,price,descriptions,file_name,redeem)VALUES ('"
				+ offerid
				+ "','"
				+ file_path
				+ "','"
				+ lat
				+ "','"
				+ lng
				+ "','"
				+ store_name
				+ "','"
				+ sub_name
				+ "','"
				+ price
				+ "','"
				+ descriptions
				+ "','"
				+ file_name
				+ "','"
				+ redeem
				+ "')";
//		Log.e("Query my_receipts", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public Cursor getMyreceipts() {
		Cursor cur = null;
		try {
			String query = "SELECT  * FROM  my_receipts ORDER BY id  DESC";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cur;
	}
	
	public Cursor getMyreceiptsByofferId(String offerid) {
		Cursor cur = null;
		try {

			String query = "SELECT  * FROM  my_receipts"+ " WHERE(offerid ='"+ offerid + "')";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cur;
	}

	public void updateReceipt(String offerid) {

		String query = "UPDATE my_receipts SET redeem = '1'" + " WHERE offerid = " + "'" + offerid + "'";

//		Log.e("Query updateReceipt", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}




	public void insertPartnerOffer(int offerid, String store_name,
								   String sub_name, String distance, String category_name, String price, String address,
								   String lat, String lng, String descriptions, String isvisit, String cat_id, String sub_cat, String valid) {

		String query = "INSERT INTO parteneroffers(offerid,store_name,sub_name,distance"
				+ ",category_name,price,address,lat,lng,descriptions,isvisit,cat_id,sub_cat,valid)VALUES ("
				+ offerid
				+ ",'"
				+ store_name
				+ "','"
				+ sub_name
				+ "','"
				+ distance
				+ "','"
				+ category_name
				+ "','"
				+ price
				+ "','"
				+ address
				+ "','"
				+ lat
				+ "','"
				+ lng
				+ "','"
				+ descriptions
				+ "','" + isvisit
				+ "','" + cat_id
				+ "','" + sub_cat
				+ "','" + valid+ "')";
//		Log.e("Query parteneroffers", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Cursor getAllPartnerOffers(){
		Cursor c = null ;
		try {

			//SELECT * FROM  parteneroffers  WHERE(lat LIKE '23.037%' and lng  LIKE '72.512%' and isvisit='no')

			String query = "SELECT * FROM  parteneroffers ";
			//Toast.makeText(mcon, query+"", Toast.LENGTH_LONG).show();
//			Log.e("getPartnerOffer ", query);
			c = db.rawQuery(query,null);

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return c;
	}

// delete partenerofferstable
	public void deletePartnerOffer(){
		try {
			db.execSQL("delete from parteneroffers");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

/*  Changed on 23-09-2016
	Changed in query line
 */
	// Select partener offer in current location if user is not visited.
	public Cursor selectPartnerOffer(String lat,String lng, String isvisit) {
		// TODO Auto-generated method stub
		Cursor c = null ;
		try {

			//SELECT * FROM  parteneroffers  WHERE(lat LIKE '23.037%' and lng  LIKE '72.512%' and isvisit='no')

			String query = "SELECT * FROM  parteneroffers " + " WHERE(lat LIKE '"+ lat + "%' and lng LIKE '"+lng+"%' and isvisit LIKE '"+isvisit+"')";
			//Toast.makeText(mcon, query+"", Toast.LENGTH_LONG).show();
//			Log.e("selectPartnerOffer ", query);
			c = db.rawQuery(query,null);

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return c;

	}

	// get all partner offer
	public Cursor getPartnerOffer(String lat,String lng) {
		// TODO Auto-generated method stub
		Cursor c = null ;
		try {

			//SELECT * FROM  parteneroffers  WHERE(lat LIKE '23.037%' and lng  LIKE '72.512%' and isvisit='no')

			String query = "SELECT * FROM  parteneroffers " + " WHERE(lat LIKE '"+ lat + "%' and lng LIKE '"+lng+"%')";
			//Toast.makeText(mcon, query+"", Toast.LENGTH_LONG).show();
//			Log.e("getPartnerOffer ", query);
			c = db.rawQuery(query,null);

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return c;

	}
	
	// Select partener offer in current location if user is not visited.
		public void updatePartnerOffer(int offerid) {
		
			try {
				
				ContentValues cv=new ContentValues();
			    cv.put("isvisit", "yes");

			    String where = "offerid=?";
			    String[] whereArgs = {Integer.toString(offerid)};

			    db.update("parteneroffers", cv, where , whereArgs);   
				

			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
	
	public void insertCategory(String category_id, String category_name,
			String category_description, String category_img) {

		String query = "INSERT INTO category(category_id,category_name,category_description,category_img)VALUES ('"
				+ category_id
				+ "','"
				+ category_name
				+ "','"
				+ category_description + "','" + category_img + "')";
//		Log.e("Query category", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void insertSubCategory(String cat_id, String subcat_id,
			String subcat_name, String subcat_description) {

		String query = "INSERT INTO sub_category(cat_id,subcat_id,subcat_name,subcat_description)VALUES ('"
				+ cat_id
				+ "','"
				+ subcat_id
				+ "','"
				+ subcat_name
				+ "','"
				+ subcat_description + "')";
//		Log.e("Query sub_category", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// Insert All pref values into database.
	public void insertPref(String category_id, String subcat_id,
			String pref_value) {

		String query = "INSERT INTO pref_values(category_id,subcat_id,pref_value)VALUES ('"
				+ category_id + "','" + subcat_id + "','" + pref_value + "')";
//		Log.e("Query pref_values", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	
	public void updatePref(String subcat_id, String pref_value) {

		String query = "UPDATE pref_values SET pref_value = '" + pref_value
				+ "'" + " WHERE subcat_id = " + "'" + subcat_id + "'";

//		Log.e("Query updatePref", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Cursor getPref() {

		Cursor cur = null;
		try {

			String query = "SELECT  * FROM  pref_values";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cur;
	}

	public String getPrefBySubcatId(String subcat_id) {

		Cursor cur = null;
		String value = "0";
		try {

			String query = "SELECT  * FROM  pref_values WHERE subcat_id = "
					+ "'" + subcat_id + "'";
			cur = db.rawQuery(query, null);

			if (cur != null) {
				cur.moveToFirst();
				value = cur.getString(3);
				if (value.equals("")) {
					value = "0";
				}
			}else{
				Log.e("NoValue*****","No value get");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	// Local Settings 
	public void insertLocalSetting(String category_id, String category_name,
			String slider_value) {

		String query = "INSERT INTO local_setting(category_id,category_name,slider_value)VALUES ('"
				+ category_id
				+ "','"
				+ category_name
				+ "','"
				+ slider_value
				+ "')";
//		Log.e("Query local_setting", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// db.close();

	}

	public void updateLocalSetting(String category_name, String slider_value) {

		String query = "UPDATE local_setting SET slider_value = '"
				+ slider_value + "'" + " WHERE category_name = " + "'"
				+ category_name + "'";

		// UPDATE CUSTOMERS SET ADDRESS = 'Pune' WHERE ID = 6;

//		Log.e("Query local_setting", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public String getLocalCatId(String category_name) {

		Cursor cur = null;
		String value = "0";
		try {

			String query = "SELECT  * FROM  local_setting WHERE category_name = "
					+ "'" + category_name + "'";
			cur = db.rawQuery(query, null);

			if (cur != null) {
				cur.moveToFirst();
				value = cur.getString(1);
				if (value.equals("")) {
					value = "0";
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}

	public String getLocalPrefByCatName(String category_name){
		Cursor cur = null;
		String value = "0";
		try {

			String query = "SELECT  * FROM  local_setting WHERE category_name = "
					+ "'" + category_name + "'";
			cur = db.rawQuery(query, null);
			if (cur != null) {
				cur.moveToFirst();
				value = cur.getString(3);
				if (value.equals("")) {
					value = "0";
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}


	public String getTravelPrefByCatName(String category_name) {

		Cursor cur = null;
		String value = "0";
		try {

			String query = "SELECT  * FROM  travel_setting WHERE category_name = "
					+ "'" + category_name + "'";
			cur = db.rawQuery(query, null);

			if (cur != null) {
				cur.moveToFirst();
				value = cur.getString(3);
				if (value.equals("")) {
					value = "0";
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}
	public String getTravelPrefByCatId(String category_id) {

		Cursor cur = null;
		String value = "0";
		try {

			String query = "SELECT  * FROM  travel_setting WHERE category_id = "
					+ "'" + category_id + "'";
			cur = db.rawQuery(query, null);

			if (cur != null) {
				cur.moveToFirst();
				value = cur.getString(3);
				if (value.equals("")) {
					value = "0";
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}

	
	// Travel Settings 
		public void insertTravelSetting(String category_id, String category_name,
				String slider_value) {

			String query = "INSERT INTO travel_setting(category_id,category_name,slider_value)VALUES ('"
					+ category_id
					+ "','"
					+ category_name
					+ "','"
					+ slider_value
					+ "')";
//			Log.e("Query travel_setting", query);
			try {
				db.execSQL(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// db.close();

		}

		public void updateTravelSetting(String category_name, String slider_value) {

			String query = "UPDATE travel_setting SET slider_value = '"
					+ slider_value + "'" + " WHERE category_name = " + "'"
					+ category_name + "'";

			// UPDATE CUSTOMERS SET ADDRESS = 'Pune' WHERE ID = 6;

//			Log.e("Query travel_setting", query);
			try {
				db.execSQL(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		



// Apply changes -1
public String getLocalPrefByCatId(String category_id) {

	Cursor cur = null;
	String value = "0";
	try {

		String query = "SELECT  * FROM  local_setting WHERE category_id = "
				+ "'" + category_id + "'";
		cur = db.rawQuery(query, null);

		if (cur != null) {
			cur.moveToFirst();
			value = cur.getString(3);
			if (value.equals("")) {
				value = "0";
			}
		}

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return value;
}
	public String getSubcatId(String subcat_name) {

			Cursor cur = null;
			String value = "0";
			try {

				String query = "SELECT * FROM sub_category WHERE subcat_name = "
						+ "'" + subcat_name + "'";
				cur = db.rawQuery(query, null);

				if (cur != null) {
					cur.moveToFirst();
					value = cur.getString(2);
					if (value.equals("")) {
						value = "0";
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return value;
		}


	public Cursor getCategory() {

		Cursor cur = null;
		try {

			String query = "SELECT  * FROM  category";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cur;
	}

	public Cursor getSubCategory() {

		Cursor cur = null;
		try {

			String query = "SELECT  * FROM  sub_category";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cur;
	}

	public Cursor getLocalSetting() {

		Cursor cur = null;
		try {

			String query = "SELECT  * FROM  local_setting";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cur;
	}


	public Cursor getTravelSetting() {

		Cursor cur = null;
		try {

			String query = "SELECT  * FROM  travel_setting";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cur;
	}

	

	public void deleteTable() {

		try {
			db.execSQL("delete from category");
			db.execSQL("delete from sub_category");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public Cursor getPrefString(String category_id){

		/*SELECT sub_category.subcat_name, pref_values.pref_value FROM pref_values JOIN sub_category ON pref_values.subcat_id=sub_category.subcat_id WHERE pref_values.category_id = '19'*/

		/*SELECT sub_category.subcat_name, pref_values.pref_value FROM pref_values JOIN sub_category ON pref_values.subcat_id=sub_category.subcat_id WHERE pref_values.category_id = '19' AND pref_values.pref_value > 0 ORDER BY pref_values.pref_value DESC*/
		Cursor cur = null;
		try {

			String query = "SELECT sub_category.subcat_name, pref_values.pref_value FROM pref_values JOIN sub_category ON pref_values.subcat_id=sub_category.subcat_id WHERE pref_values.category_id = '"+category_id+"' AND pref_values.pref_value > 0 ORDER BY pref_values.pref_value DESC";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cur;
	}


	/* ------------------------Already visited Offers---------------------------------
		created on 24-09-2016
	 */
	// Insert data in seenoffers Table
	public void insertVisitedOffers(String offerID) {

		String query = "INSERT INTO visitedoffers(offer_id)VALUES ('" + offerID + "')";
		Log.e("Query my_receipts", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	// get the all records from the seenoffers table
	public Cursor getVisitedOffers() {
		Cursor cur = null;
		try {
			String query = "SELECT  * FROM  visitedoffers";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cur;
	}

	/* ------------------------Already visited local Business---------------------------------
		created on 26-09-2016
	 */
	// Insert data in visited LocalBusiness Table
	public void insertVisitedLocalBusiness(String localBusinessID) {

		String query = "INSERT INTO visitedlocalbusiness(localbusiness_id)VALUES ('" + localBusinessID + "')";
//		Log.e("Query visitedbusiness", query);
		try {
			db.execSQL(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	// get the all records from the visited LocalBusiness table
	public Cursor getVisitedLocalBusiness() {
		Cursor cur = null;
		try {
			String query = "SELECT  * FROM  visitedlocalbusiness";
			cur = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cur;
	}
}
