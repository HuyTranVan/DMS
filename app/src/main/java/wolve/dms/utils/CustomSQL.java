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
import java.util.List;

import wolve.dms.models.BaseModel;

public class CustomSQL {
    final static String MY_PREFS = "DMS_data";
    static SharedPreferences prefs;

    public static void setBaseModel(String title, BaseModel value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, value.BaseModelstoString()).commit();

    }

    public static void setString(String title, String value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, value).commit();

    }

    public void setInt(String title, int value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putInt(title, value).commit();
    }

    public static void setBoolean(String title, boolean boole){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(title, boole).commit();
    }

    public static void setLong(String title, long lon){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putLong(title, lon).commit();
    }

    public static void setObject (String title, Object value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, new Gson().toJson(value)).commit();

    }

    public static void setBasemodel (String title, BaseModel value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, value.BaseModelstoString()).commit();

    }

    public static void setListObject (String title, List<Object> value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, new Gson().toJson(value)).commit();

    }

    public static void setListJSONObject (String title, List<JSONObject> value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        for (int i=0; i<value.size(); i++){
            array.put(value.get(i));
        }

        prefs.edit().putString(title, array.toString()).commit();

    }

    public static void setListBaseModel (String title, List<BaseModel> value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        for (int i=0; i<value.size(); i++){
            array.put(value.get(i).BaseModelJSONObject());
        }

        prefs.edit().putString(title, array.toString()).commit();

    }

    //----------------------------
    public static BaseModel getBaseModel(String title){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(prefs != null){
            return new BaseModel(prefs.getString(title, ""));
        }else {
            return new BaseModel();
        }

    }


    public static String getString(String title){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(prefs != null){
            return prefs.getString(title, "");
        }else {
            return "";
        }

    }

    public static int getInt(String title){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(prefs != null)
            return prefs.getInt(title, 0);
        return 0;
    }


    public static boolean getBoolean(String title){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(prefs != null)
            return prefs.getBoolean(title, false);
        return false;
    }

    public static long getLong(String title){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(prefs != null)
            return prefs.getLong(title, 0);
        return 0;
    }

    public static <T extends Object> T getObject(String name, Class<T> type){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(prefs != null) {
            Gson gson = new Gson();
            String json = prefs.getString(name, "");
            T obj = gson.fromJson(json, type);

            return obj;
        }
        return null;
    }


    public static List<BaseModel>  getListObject(String name){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        List<BaseModel> listObject = new ArrayList<>();

        String value = prefs.getString(name, "");
        try {
            if(prefs != null && !value.equals("")) {
                JSONArray array = new JSONArray(value);
                for (int i=0; i<array.length(); i++){
                    listObject.add(new BaseModel(array.getJSONObject(i)));
                }

            }
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return listObject;
    }

//    public static List<? extends Object> getListObject(String name, Class<?> type){
//        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
//
//        if (prefs != null){
//            List<? extends Object> listResult = new ArrayList<>();
//            String json = prefs.getString(name, "");
//            listResult = (List<? extends Object>) new Gson().fromJson(json , type)
//            Type type = new TypeToken<List<Product>>() {}.getType();
//            return
//        }
//
//
//
//        productFromShared = gson.fromJson(jsonPreferences, type);
//
//        return null;
//    }

    public static void removeKey(String title){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(prefs != null){
            prefs.edit().remove(title).commit();
        }

    }

    public static void clear(){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(prefs != null){
            prefs.edit().clear().commit();
        }

    }

//    public static void saveListObject2Local(String key, List<BaseModel> list){
//        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
//
//        prefs.edit().putString(key, new Gson().toJson(list)).commit();
//
//
//    }
//
//    public static List loadObjectFromLocal(String key){
//        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
//        List<BaseModel> listResults = new ArrayList<>();
//        if(prefs != null){
//            listResults= (ArrayList<BaseModel>) new Gson().fromJson(prefs.getString(key, ""),
//                    new TypeToken<ArrayList<BaseModel>>() {}.getType());
//
//
//        }
//        return listResults;
//
//    }



}
