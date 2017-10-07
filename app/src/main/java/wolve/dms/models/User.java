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

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
    }

    String id_user;
    static User currentUser;

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
        CustomSharedPrefer customSharedPrefer = new CustomSharedPrefer(Util.getInstance().getCurrentActivity());
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

}
