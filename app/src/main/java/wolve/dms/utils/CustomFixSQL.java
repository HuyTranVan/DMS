package wolve.dms.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wolve.dms.models.BaseModel;

public class CustomFixSQL {
    final static String MY_PREFS = "DMS_fix_data";
    static SharedPreferences prefs;

    public static void setBaseModel(String title, BaseModel value) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, value.BaseModelstoString()).apply();

    }

    public static void setString(String title, String value) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, value).apply();

    }

    public static void setInt(String title, int value) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putInt(title, value).apply();
    }

    public static void setBoolean(String title, boolean boole) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(title, boole).apply();
    }

    public static void setLong(String title, long lon) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putLong(title, lon).apply();
    }

    public static void setObject(String title, Object value) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, new Gson().toJson(value)).apply();

    }



    public static void setBasemodel(String title, BaseModel value) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, value.BaseModelstoString()).apply();

    }

    public static void setListObject(String title, List<Object> value) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, new Gson().toJson(value)).apply();

    }

    public static void setListJSONObject(String title, List<JSONObject> value) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        for (int i = 0; i < value.size(); i++) {
            array.put(value.get(i));
        }

        prefs.edit().putString(title, array.toString()).apply();

    }

    public static void setListBaseModel(String title, List<BaseModel> value) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        for (int i = 0; i < value.size(); i++) {
            array.put(value.get(i).BaseModelJSONObject());
        }

        prefs.edit().putString(title, array.toString()).apply();

    }

    public void setListString(String title, List<String> list){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        prefs.edit().putString(title, new Gson().toJson(list)).apply();
    }

    public static void setStringToList(String title, String value) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        String json = prefs.getString(title, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> currentList = new ArrayList<>();
        if (json != null){
            currentList =  new Gson().fromJson(json, type);
        }
        currentList.add(value);

        prefs.edit().putString(title, new Gson().toJson(currentList)).apply();

    }



    //----------------------------


    public static BaseModel getBaseModel(String title) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (prefs != null) {
            return new BaseModel(prefs.getString(title, ""));
        } else {
            return new BaseModel();
        }

    }


    public static String getString(String title) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (prefs != null) {
            return prefs.getString(title, "");
        } else {
            return "";
        }

    }

    public static int getInt(String title) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (prefs != null)
            return prefs.getInt(title, 0);
        return 0;
    }


    public static boolean getBoolean(String title) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (prefs != null)
            return prefs.getBoolean(title, false);
        return false;
    }

    public static long getLong(String title) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (prefs != null)
            return prefs.getLong(title, 0);
        return 0;
    }

    public static <T extends Object> T getObject(String name, Class<T> type) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (prefs != null) {
            Gson gson = new Gson();
            String json = prefs.getString(name, "");
            T obj = gson.fromJson(json, type);

            return obj;
        }
        return null;
    }

    public static List<BaseModel> getListObject(String name) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        List<BaseModel> listObject = new ArrayList<>();

        String value = prefs.getString(name, "");
        try {
            if (prefs != null && !value.equals("")) {
                JSONArray array = new JSONArray(value);
                for (int i = 0; i < array.length(); i++){
                    listObject.add(new BaseModel(array.getJSONObject(i)));
                }

            }
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return listObject;
    }

    public static void removeKey(String title) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (prefs != null) {
            prefs.edit().remove(title).commit();
        }

    }

    public static void clear() {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (prefs != null) {
            prefs.edit().clear().commit();
        }

    }

    public static List<String> getListString(String title) {
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        String json = prefs.getString(title, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> currentList = new ArrayList<>();
        if (json != null){
            currentList =  new Gson().fromJson(json, type);
        }

        return currentList;
    }

//    public static boolean hasKey(String key){
//        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
//        String value = prefs.getString(key,null);
//        if (value == null){
//            return false;
//        }else {
//            return true;
//        }
//    }



}
