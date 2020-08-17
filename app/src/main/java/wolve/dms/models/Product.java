package wolve.dms.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.callback.CallbackListObject;
import wolve.dms.apiconnect.libraries.DownloadImageMethod;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class Product extends BaseModel {
    static List<Product> mListProducts = null;

    public Product() {
        jsonObject = new JSONObject();
    }

    public Product(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String ProducttoString() {
        return jsonObject.toString();
    }

    public JSONObject ProductJSONObject() {
        return jsonObject;
    }

    public static void saveProductList(JSONArray groups) {
        List<BaseModel> mProducts = new ArrayList<>();
        try {
            for (int i = 0; i < groups.length(); i++) {
                JSONObject object = groups.getJSONObject(i);
                JSONArray arrayProduct = object.getJSONArray("product");

                for (int ii = 0; ii < arrayProduct.length(); ii++) {
                    BaseModel mProduct = new BaseModel(arrayProduct.getJSONObject(ii));
                    mProducts.add(mProduct);

                }


            }

            new DownloadImageMethod(mProducts, "image", "PRODUCT", new CallbackListObject() {
                @Override
                public void onResponse(List<BaseModel> list) {
                    Util.getInstance().stopLoading(true);
                    CustomSQL.setListBaseModel(Constants.PRODUCT_LIST, list);
                }
            }).execute();


        } catch (JSONException e) {

        }
        mListProducts = null;

    }

    public static List<BaseModel> getProductList() {
        List<BaseModel> mProducts = new ArrayList<>();

        if (mListProducts == null) {
            try {
                JSONArray array = new JSONArray(CustomSQL.getString(Constants.PRODUCT_LIST));
                for (int i = 0; i < array.length(); i++) {
                    BaseModel product = new BaseModel(array.getJSONObject(i));
                    product.put("product_id", product.getInt("id"));
                    mProducts.add(product);
                }

            } catch (JSONException e) {
                return mProducts;
            }
        }

        DataUtil.sortProduct(mProducts, false);

        return mProducts;
    }

    public static List<String> getProductListString() {
        List<String> mProducts = new ArrayList<>();

        if (mListProducts == null) {
            try {
                JSONArray array = new JSONArray(CustomSQL.getString(Constants.PRODUCT_LIST));
                for (int i = 0; i < array.length(); i++) {
                    BaseModel product = new BaseModel(array.getJSONObject(i));
                    mProducts.add(product.getString("name"));
                }

            } catch (JSONException e) {
                return mProducts;
            }
        }
        Collections.sort(mProducts);

        return mProducts;
    }

}
