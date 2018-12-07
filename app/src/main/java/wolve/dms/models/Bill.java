package wolve.dms.models;

import org.json.JSONObject;

/**
 * Created by macos on 9/16/17.
 */

public class Bill extends BaseModel{
    public Bill() {
        jsonObject = null;
    }

    public Bill(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String BillstoString(){
        return jsonObject.toString();
    }

    public JSONObject convertJsonObject(){
        return jsonObject;
    }
}
