package wolve.dms.models;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Util;


/**
 * Created by VB on 4/17/16.
 */
public class User extends BaseModel {
//    String token;
//    String id_user;
//    String displayName;
//    static User currentUser;

    //Role = 1 : Manager
    //Role = 2 : WAREHOUSE
    //Role = 3 : DELIVER
    //Role = 4 : Sale

    public User() {
        jsonObject = null;
    }

    public User(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public User(String strUser) {
        try {
            jsonObject = new JSONObject(strUser);
        } catch (JSONException e) {
            jsonObject = new JSONObject();
        }
    }

    public static String getUserId(){
        int id_user = 0;
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);

        if (currentUser != null) {
            id_user = currentUser.getInt("id");
        }
        return String.valueOf(id_user);
    }

    public static int getId(){
        int id_user = 0;
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);

        if (currentUser != null) {
            id_user = currentUser.getInt("id");
        }
        return id_user;
    }

    public static String getToken(){
        String token = "";
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);

        if (currentUser != null) {
            token = currentUser.getString("token");
        }
        return token;
    }

    public static String getFullName(){
        String name = "";
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);

        if (currentUser != null) {
            name = currentUser.getString("displayName");
        }
        return name;

    }

    public static String getRole(){
        String role = "";
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);

        if (currentUser != null) {
            role = currentUser.getString("role");
        }
        return role;
    }

    public static String getPhone(){
        String phone = "";
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);

        if (currentUser != null) {
            phone = currentUser.getString("phone");
        }
        return phone;
    }

    public static BaseModel getCurrentUser(){
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);
        BaseModel user = new BaseModel();
        user.put("id", currentUser.getInt("id"));
        user.put("displayName", currentUser.getString("displayName"));
        user.put("phone", currentUser.getString("phone"));
        user.put("role", currentUser.getString("role"));

        return user;
    }

    public static JSONObject getCurrentUserString(){
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);
        JSONObject user = new JSONObject();
        try {
            user.put("id", currentUser.getInt("id"));
            user.put("displayName", currentUser.getString("displayName"));
            user.put("phone", currentUser.getString("phone"));
            user.put("role", currentUser.getString("role"));
            user.put("currentTime", Util.CurrentMonthYearHour());


        } catch (JSONException e) {
            //e.printStackTrace();
        }

        return user;
    }



}
