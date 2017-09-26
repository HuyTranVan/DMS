package wolve.dms.models;

import org.json.JSONObject;

import wolve.dms.BaseActivity;

/**
 * Created by macos on 9/16/17.
 */

public class ProductGroup extends BaseModel{
    public ProductGroup() {
        jsonObject = null;
    }

    public ProductGroup(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String ProductGrouptoString(){
        return jsonObject.toString();
    }
}
