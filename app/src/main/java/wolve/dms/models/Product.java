package wolve.dms.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;

/**
 * Created by macos on 9/16/17.
 */

public class Product extends BaseModel{
    static List<Product> mListProducts = null;

    public Product() {
        jsonObject = new JSONObject();
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

    public static void saveProductList(JSONArray product){
        CustomSQL.setString(Constants.PRODUCT_LIST, product.toString());
        mListProducts = null;
    }

    public static List<Product> getProductList(){
        if (mListProducts == null){
            mListProducts = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(CustomSQL.getString(Constants.PRODUCT_LIST));
                for (int i=0; i<array.length(); i++){
                    Product product = new Product(array.getJSONObject(i));
                    mListProducts.add(product);
                }

            } catch (JSONException e) {
                return mListProducts;
            }
        }

        DataUtil.sortProduct(mListProducts, false);

        return mListProducts;
    }

}
