package wolve.dms.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;

/**
 * Created by macos on 9/16/17.
 */

public class ProductGroup extends BaseModel{
    static List<ProductGroup> mListProductGroups = null;

    public ProductGroup() {
        jsonObject = new JSONObject();
    }

    public ProductGroup(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public ProductGroup(String objOrder) {
        try {
            jsonObject = new JSONObject(objOrder);
        } catch (JSONException e) {
//            e.printStackTrace();
            jsonObject = new JSONObject();
        }
    }

    public String ProductGrouptoString(){
        return jsonObject.toString();
    }

    public static void saveProductGroupList(JSONArray productgroup){
        CustomSQL.setString(Constants.PRODUCTGROUP_LIST, productgroup.toString());
        mListProductGroups = null;
    }

    public static List<ProductGroup> getProductGroupList(){
        if (mListProductGroups == null){
            mListProductGroups = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(CustomSQL.getString(Constants.PRODUCTGROUP_LIST));
                for (int i=0; i<array.length(); i++){
                    ProductGroup productGroup = new ProductGroup(array.getJSONObject(i));
                    mListProductGroups.add(productGroup);
                }

            } catch (JSONException e) {
                return mListProductGroups;
            }

        }


        return mListProductGroups;
    }
}
