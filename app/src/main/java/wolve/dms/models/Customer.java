package wolve.dms.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by macos on 9/16/17.
 */

public class Customer extends BaseModel{
    public Customer() {
        jsonObject = null;
    }

    public Customer(JSONObject objOrder) {
        jsonObject = objOrder;
    }
    public Customer(String customer){
        try {
            jsonObject = new JSONObject(customer);
        } catch (JSONException e) {
            jsonObject = new JSONObject();
        }
    }

    public String CustomertoString(){
        return jsonObject.toString();
    }

    public JSONObject CustomerJSONObject(){
        return jsonObject;
    }
}
