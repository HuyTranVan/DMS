package wolve.dms.utils;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CustomSQL {
    final static String MY_PREFS = "DMS_data";
    static SharedPreferences prefs;

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

    public static void setObject (String title, Object value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, new Gson().toJson(value)).commit();

    }

    public static void setListObject (String title, List<Object> value){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(title, new Gson().toJson(value)).commit();

    }

    //----------------------------

    public static String getString(String title){
        prefs = Util.getInstance().getCurrentActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if(prefs != null)
            return prefs.getString(title, "");
        return "";
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


}
