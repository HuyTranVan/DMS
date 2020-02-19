package wolve.dms.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    public static List< BaseModel> listRole(){
        List<BaseModel> roles = new ArrayList<>();
        roles.add(BaseModel.put2ValueToNewObject( "text", "QUẢN LÝ", "index", 1));
        roles.add( BaseModel.put2ValueToNewObject( "text", "THỦ KHO", "index", 2));
        roles.add(BaseModel.put2ValueToNewObject( "text", "GIAO HÀNG", "index", 3));
        roles.add( BaseModel.put2ValueToNewObject( "text", "SALE", "index", 4));

        return roles;
    }

    public static int getIndex(String role){
        int index = 0;
        for (BaseModel model: listRole()){
            if (model.getString("text").equals(role)){
                index =  model.getInt("index");

                break;

            }
        }
        return index;
    }

    public static int getCurrentRoleId(){
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);
        return currentUser.getInt("role");
    }

    public static String getImage(){
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);

        return currentUser.getString("image");
    }


    public static String getCurrentRoleString(){
        String role = "";
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);

        if (currentUser != null) {
            for (BaseModel model: listRole()){
                if (model.getInt("index") == currentUser.getInt("role")){
                    role =  model.getString("text");
                    break;
                }
            }

        }
        return role;
    }

    public static String getRoleString(int id){
        String role = "";

        for (BaseModel model: listRole()){
            if (model.getInt("index") == id){
                role =  model.getString("text");
                break;
            }

        }
        return role;
    }

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



    public static String getPhone(){
        String phone = "";
        BaseModel currentUser = CustomSQL.getBaseModel(Constants.USER);

        if (currentUser != null) {
            phone = currentUser.getString("phone");
        }
        return phone;
    }

    public static BaseModel getCurrentUser(){
        return CustomSQL.getBaseModel(Constants.USER);
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
