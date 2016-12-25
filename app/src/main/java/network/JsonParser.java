package network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.ContactModel;


/**
 * Created by haseeb on 23/12/16.
 */
public class JsonParser {
    private static JsonParser instance;

    private JsonParser() {

    }

    public static synchronized JsonParser getInstance() {
        if (instance == null)
            instance = new JsonParser();
        return instance;
    }

    // Parsers
    public ArrayList<ContactModel> parseResponse(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        } else {

            JSONObject maindata = null;
            JSONArray contacts = null;
            ArrayList<ContactModel> data = new ArrayList<ContactModel>();
            try {
                maindata = jsonArray.getJSONObject(0);
                contacts = maindata.getJSONArray("contacts");
                for (int i = 0; i < contacts.length(); i++) {
                    ContactModel contactModel = new ContactModel();
                    if (!contacts.getJSONObject(i).isNull("name")) {
                        contactModel.setName(contacts.getJSONObject(i).getString("name"));
                    }
                    else {
                        contactModel.setName(null);
                    }
                    if (!contacts.getJSONObject(i).isNull("email")) {
                        contactModel.setEmail(contacts.getJSONObject(i).getString("email"));
                    }
                    else {
                        contactModel.setEmail(null);
                    }
                    if (!contacts.getJSONObject(i).isNull("phone")) {
                        contactModel.setPhone(String.valueOf(contacts.getJSONObject(i).getInt("phone")));
                    }
                    else {
                        contactModel.setPhone(null);
                    }
                    if (!contacts.getJSONObject(i).isNull("officePhone")) {
                        contactModel.setOfficePhone(String.valueOf(contacts.getJSONObject(i).getInt("officePhone")));
                    }
                    else {
                        contactModel.setOfficePhone(null);
                    }

                    if (!contacts.getJSONObject(i).isNull("latitude")) {
                        contactModel.setLatitude(contacts.getJSONObject(i).getDouble("latitude"));
                    }
                    else {
                        contactModel.setLatitude(null);
                    }
                    if (!contacts.getJSONObject(i).isNull("longitude")) {
                        contactModel.setLongitude(contacts.getJSONObject(i).getDouble("longitude"));
                    }
                    else {
                        contactModel.setLongitude(null);
                    }
                    data.add(contactModel);
//                    data.add(contactModel);
                }
                return data;


            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

    }


}