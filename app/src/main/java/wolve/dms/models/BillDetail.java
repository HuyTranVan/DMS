package wolve.dms.models;

import org.json.JSONObject;

/**
 * Created by macos on 9/16/17.
 */

public class BillDetail extends BaseModel{
    public BillDetail() {
        jsonObject = null;
    }

    public BillDetail(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String BillDetailtoString(){
        return jsonObject.toString();
    }
}
