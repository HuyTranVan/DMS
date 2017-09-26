package wolve.dms.models;

import org.json.JSONObject;

/**
 * Created by macos on 9/16/17.
 */

public class Province extends BaseModel{
    public Province() {
        jsonObject = null;
    }

    public Province(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String ProvincetoString(){
        return jsonObject.toString();
    }
}
