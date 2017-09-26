package wolve.dms.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomSharedPrefer {
    SharedPreferences prefs;

    public final static String MY_PREFS = "MyPrefs";
    public CustomSharedPrefer(Context context){
        if(context != null)
            prefs = context.getSharedPreferences(
                    MY_PREFS, Context.MODE_PRIVATE);
    }

    public void setBoolean(String title, boolean boole){
        if(prefs != null)
            prefs.edit().putBoolean(title, boole).commit();
    }

    public void setString(String title, String str){
        if(prefs != null)
            prefs.edit().putString(title, str).commit();
    }

    public boolean getBoolean(String title){
        if(prefs != null)
            return prefs.getBoolean(title, false);
        return false;
    }

    public String getString(String title){
        if(prefs != null)
            return prefs.getString(title, "");
        return "";
    }

    public void setObject (String name, Object object){
        if(prefs != null) {
            SharedPreferences.Editor prefsEditor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(object);
            prefsEditor.putString(name, json);
            prefsEditor.commit();
        }
    }

    public <T extends Object> T getObject(String name, Class<T> type){
        if(prefs != null) {
            Gson gson = new Gson();
            String json = prefs.getString(name, "");
            T obj = gson.fromJson(json, type);

            return obj;
        }
        return null;
    }

    public void setInt(String title, int value){
        if(prefs != null)
            prefs.edit().putInt(title, value).commit();
    }

    public int getInt(String title){
        if(prefs != null)
            return prefs.getInt(title, 0);
        return 0;
    }


    public static void Remove(Context context, String title){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.remove(title);
        prefsEditor.commit();
    }

    public void Remove(String title){
        if(prefs != null){
            prefs.edit().remove(title).commit();
        }

    }

    //    public ArrayMap<String,?> getAllSharepreferences(){
    public List<ArrayMap<String,Object>> getAllSharepreferences(){
        Map<String, ?> allEntries = prefs.getAll();
        List<ArrayMap<String,Object>> listObject = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if(entry.getKey().contains(Constants.CATEGORY)){
                try {

                    ArrayMap<String,Object> object = Util.JSON2ArrayMap(new JSONObject(entry.getValue().toString()));
                    object.put("name",entry.getKey() );

                    listObject.add(object);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());

        }

        return listObject;
    }

    public List<JSONObject> getAllSharepreferencesObject(){
        Map<String, ?> allEntries = prefs.getAll();
        List<JSONObject> listObject = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            if(entry.getKey().contains(Constants.CATEGORY)){
//                try {
//                    JSONObject object = new JSONObject(entry.getValue().toString());
//                    object.put("name",entry.getKey() );
//
//                    listObject.add(object);
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());

        }

        return listObject;
    }

}
