package wolve.dms.models;

import org.json.JSONObject;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;


/**
 * Created by VB on 4/17/16.
 */
public class User extends BaseModel {
//    String token;
//    String id_user;
//    String displayName;
//    static User currentUser;

    public User() {
        jsonObject = null;
    }

    public User(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public static String getUserId(){
        int id_user = 0;
        User currentUser = CustomSQL.getObject(Constants.USER, User.class);

        if (currentUser != null) {
            id_user = currentUser.getInt("id");
        }
        return String.valueOf(id_user);
    }

    public static String getToken(){
        String token = "";
        User currentUser = CustomSQL.getObject(Constants.USER, User.class);

        if (currentUser != null) {
            token = currentUser.getString("token");
        }
        return token;
    }

    public static String getFullName(){
        String name = "";
        User currentUser = CustomSQL.getObject(Constants.USER, User.class);

        if (currentUser != null) {
            name = currentUser.getString("displayName");
        }
        return name;

    }

    public static String getRole(){
        String role = "";
        User currentUser = CustomSQL.getObject(Constants.USER, User.class);

        if (currentUser != null) {
            role = currentUser.getString("role");
        }
        return role;
    }



//    public String getId_user() {
//        return id_user;
//    }
//
//    public void setId_user(String id_user) {
//        this.id_user = id_user;
//    }
//
//    public static void setCurrentUser(User currentUser) {
//        User.currentUser = currentUser;
//    }
//
//    public String getDisplayName() {
//        return displayName;
//    }
//
//    public void setDisplayName(String displayName) {
//        this.displayName = displayName;
//    }
//
//    public User() {
//        jsonObject = null;
//    }
//
//    public User(JSONObject objOrder) {
//        jsonObject = objOrder;
//    }
//
//    public void setToken(String token){
//        this.token = token;
//    }
//
//    public String getToken(){
//        return token;
//    }
//
//    public static User getCurrentUser(){
//        if(CustomSQL.getObject(Constants.USER, User.class) == null){
//            currentUser = new User();
//        }else {
//            currentUser = CustomSQL.getObject(Constants.USER, User.class);
//        }
//        return currentUser;
//    }
//
//    public static JSONObject getCurrentUserInfo() {
//        currentUser = getCurrentUser();
//        return currentUser.jsonObject;
//    }
//
////    public static String getUserId(){
////        String id_user = "";
////
////        User currentUser = User.getCurrentUser();
////        if (currentUser != null && currentUser.getToken() != null) {
////            id_user = currentUser.getId_user();
////        }
////        return id_user;
////    }
//
//    public static String getCurrentToken(){
//        String token = "";
//        User currentUser = User.getCurrentUser();
//        if (currentUser != null && currentUser.getToken() != null) {
//            token = currentUser.getToken();
//        }
//        return token;
//    }
//    public static String getFullName(){
//        String name = "";
//        User currentUser = User.getCurrentUser();
//        if (currentUser != null && currentUser.getToken() != null) {
//            name = currentUser.getString("displayName");
//        }
//        return name;
//    }

}
