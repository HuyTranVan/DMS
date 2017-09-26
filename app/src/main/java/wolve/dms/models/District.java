package wolve.dms.models;

import org.json.JSONObject;

/**
 * Created by macos on 9/16/17.
 */

public class District extends BaseModel{
    public District() {
        jsonObject = null;
    }

    public District(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String DistricttoString(){
        return jsonObject.toString();
    }
}
