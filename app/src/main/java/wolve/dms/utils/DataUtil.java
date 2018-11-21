package wolve.dms.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.models.Distributor;
import wolve.dms.models.Product;
import wolve.dms.models.User;

public class DataUtil {
    public static String createPostBillParam(int customerId, final Double total, final Double paid, List<JSONObject> listProduct, String note){
        final JSONObject params = new JSONObject();
        try {
            params.put("debt", total - paid);
            params.put("total", total);
            params.put("paid", paid);
            params.put("customerId", customerId);
            params.put("distributorId", Distributor.getDistributorId());
            params.put("userId", User.getUserId());
            params.put("note", note);

            JSONArray array = new JSONArray();
            for (int i=0; i< listProduct.size(); i++){
                JSONObject object = new JSONObject();
                object.put("productId", listProduct.get(i).getInt("id"));
                object.put("discount", listProduct.get(i).getDouble("discount"));
                object.put("quantity", listProduct.get(i).getDouble("quantity"));

                array.put(object);
            }
            params.put("billDetails", (Object) array);
        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }

    public static JSONArray convertListObject2Array(List<JSONObject> list){
        JSONArray array = new JSONArray();
        for (int i=0; i< list.size(); i++){
            array.put(list.get(i));

        }

        return array;

    }

    public static List<JSONObject> array2ListObject(String array){
        List<JSONObject> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(array);
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                list.add(object);
            }


        } catch (JSONException e) {
            return list;
        }

        return list;
    }

}