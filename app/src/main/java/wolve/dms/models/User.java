package wolve.dms.models;

import org.json.JSONException;
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

    public User(String strUser) {
        try {
            jsonObject = new JSONObject(strUser);
        } catch (JSONException e) {
            jsonObject = new JSONObject();
        }
    }

    public static String getUserId(){
        int id_user = 0;
        User currentUser = CustomSQL.getObject(Constants.USER, User.class);

        if (currentUser != null) {
            id_user = currentUser.getInt("id");
        }
        return String.valueOf(id_user);
    }

    public static int getId(){
        int id_user = 0;
        User currentUser = CustomSQL.getObject(Constants.USER, User.class);

        if (currentUser != null) {
            id_user = currentUser.getInt("id");
        }
        return id_user;
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

    public static String getPhone(){
        String phone = "";
        User currentUser = CustomSQL.getObject(Constants.USER, User.class);

        if (currentUser != null) {
            phone = currentUser.getString("phone");
        }
        return phone;
    }


}
