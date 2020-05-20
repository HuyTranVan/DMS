package wolve.dms.models;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;

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
        Product.saveProductList(productgroup);

        mListProductGroups = null;
    }

    public static List<BaseModel> getProductGroupList(){
        List<BaseModel> mProductGroups = new ArrayList<>();

        if (mListProductGroups == null){
            try {
                JSONArray array = new JSONArray(CustomSQL.getString(Constants.PRODUCTGROUP_LIST));
                for (int i=0; i<array.length(); i++){
                    BaseModel productGroup = new BaseModel(array.getJSONObject(i));
                    mProductGroups.add(productGroup);
                }

            } catch (JSONException e) {
                return mProductGroups;
            }

        }

        DataUtil.sortProductGroup(mProductGroups, false);
        return mProductGroups;
    }
}
