package wolve.dms.models;

import org.json.JSONObject;

/**
 * Created by macos on 9/16/17.
 */

public class Product extends BaseModel{
    public Product() {
        jsonObject = null;
    }

    public Product(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String ProducttoString(){
        return jsonObject.toString();
    }

    public JSONObject ProductJSONObject(){
        return jsonObject;
    }

}
