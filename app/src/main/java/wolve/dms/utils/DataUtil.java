package wolve.dms.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.apiconnect.Api_link;
import wolve.dms.models.BaseModel;
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

    public static List<String> createListPaymentParam(int customerId,List<JSONObject> list){
        List<String> results = new ArrayList<>();
        try {
            for (int i=0; i<list.size(); i++){
                String s = String.format(Api_link.PAY_PARAM,
                        customerId,
                        String.valueOf(Math.round(list.get(i).getDouble("paid"))),
                        list.get(i).getInt("billId"),
                        User.getId(),
                        "");
//                        note.equals("")?"" :"&note="+note )

                results.add(s);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
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

    public static List<BaseModel> groupDebtByCustomer(final List<BaseModel> list){
        final List<BaseModel> listResult = new ArrayList<>();
        try {
            for (int i=0; i<list.size(); i++){
                BaseModel customer1 = new BaseModel(list.get(i).getJsonObject("customer"));

                double total = list.get(i).getDouble("debt");
                for (int j=0; j<listResult.size(); j++){
                    BaseModel customer2 = new BaseModel(listResult.get(j).getJsonObject("customer"));
                    if (customer1.getString("id").equals(customer2.getString("id"))){
//                        double debt = listResult.get(j).getDouble("debt") + list.get(i).getDouble("debt");
                        total += listResult.get(j).getDouble("debt");
                        listResult.get(j).put("debt", total );
//                        total += listResult.get(j).getDouble("debt");
//                        checkDup = true;
//                        break;
                    }
                }

                if (total == list.get(i).getDouble("debt")){
                    BaseModel objectDetail = new BaseModel();
                    objectDetail.put("id",list.get(i).getString("id") );
                    objectDetail.put("createAt",list.get(i).getLong("createAt") );
                    objectDetail.put("updateAt",list.get(i).getLong("updateAt") );
                    objectDetail.put("user",list.get(i).getJsonObject("user") );
                    objectDetail.put("customer",list.get(i).getJsonObject("customer") );
                    objectDetail.put("distributor",list.get(i).getJsonObject("distributor") );
                    objectDetail.put("payments",list.get(i).getString("payments") );
                    objectDetail.put("total",list.get(i).getDouble("total") );
                    objectDetail.put("debt",list.get(i).getDouble("debt") );
                    objectDetail.put("paid",list.get(i).getDouble("paid") );
                    objectDetail.put("note",list.get(i).getString("note") );

                    listResult.add(objectDetail);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return listResult;
    }

}
