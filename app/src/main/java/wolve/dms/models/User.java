package wolve.dms.models;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSharedPrefer;
import wolve.dms.utils.Util;


/**
 * Created by VB on 4/17/16.
 */
public class User extends BaseModel {
    String token;
    String id_user;
    String displayName;
    static User currentUser;

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public User() {
        jsonObject = null;
    }

    public User(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getToken(){
        return token;
    }

    public static User getCurrentUser(){
        CustomSharedPrefer customSharedPrefer = new CustomSharedPrefer();
        if(customSharedPrefer.getObject(Constants.USER, User.class) == null){
            currentUser = new User();
        }else {
            currentUser = customSharedPrefer.getObject(Constants.USER, User.class);
        }
        return currentUser;
    }

    public static JSONObject getCurrentUserInfo() {
        currentUser = getCurrentUser();
        return currentUser.jsonObject;
    }

    public static String getCurrentUserId(){
        String id_user = "";
        User currentUser = User.getCurrentUser();
        if (currentUser != null && currentUser.getToken() != null) {
            id_user = currentUser.getId_user();
        }
        return id_user;
    }

    public static String getFullName(){
        String name = "";
        User currentUser = User.getCurrentUser();
        if (currentUser != null && currentUser.getToken() != null) {
            name = currentUser.getString("displayName");
        }
        return name;
    }
}
