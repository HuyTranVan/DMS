package wolve.dms.models;

import org.json.JSONObject;

/**
 * Created by macos on 9/16/17.
 */

public class Status extends BaseModel{
    public Status() {
        jsonObject = null;
    }

    public Status(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String StatustoString(){
        return jsonObject.toString();
    }
}
