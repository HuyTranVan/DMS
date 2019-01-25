package wolve.dms.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wolve.dms.apiconnect.Api_link;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.Product;
import wolve.dms.models.ProductGroup;
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

    public static String updateBillParam(int customerId, final Double total, final Double paid, String note){
        final JSONObject params = new JSONObject();
        try {
            params.put("id", total - paid);
            params.put("debt", total - paid);
            params.put("total", total);
            params.put("paid", paid);
            params.put("customerId", customerId);
            params.put("distributorId", Distributor.getDistributorId());
            params.put("userId", User.getUserId());
            params.put("note", note);

            params.put("billDetails", (Object) null);
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

                boolean checkDup = false;
                for (int j=0; j<listResult.size(); j++){
                    BaseModel customer2 = new BaseModel(listResult.get(j).getJsonObject("customer"));
                    if (customer1.getString("id").equals(customer2.getString("id"))){
                        checkDup = true;
                        break;
                    }
                }

                if (!checkDup){
                    double debt = 0.0;
                    for (int a=0; a<list.size(); a++){
                        BaseModel customer3 = new BaseModel(list.get(a).getJsonObject("customer"));
                        if (customer1.getString("id").equals(customer3.getString("id"))){
                            debt += list.get(a).getDouble("debt");

                        }
                    }

                    if (debt > 0){
                        BaseModel objectDetail = new BaseModel();
                        objectDetail.put("id",list.get(i).getString("id") );
                        objectDetail.put("createAt",list.get(i).getLong("createAt") );
                        objectDetail.put("updateAt",list.get(i).getLong("updateAt") );
                        objectDetail.put("user",list.get(i).getJsonObject("user") );
                        objectDetail.put("customer",list.get(i).getJsonObject("customer") );
                        objectDetail.put("distributor",list.get(i).getJsonObject("distributor") );
                        objectDetail.put("payments",list.get(i).getString("payments") );
                        objectDetail.put("total",list.get(i).getDouble("total") );
                        objectDetail.put("debt",debt );
                        objectDetail.put("paid",list.get(i).getDouble("paid") );
                        objectDetail.put("note",list.get(i).getString("note") );

                        listResult.add(objectDetail);
                    }


                }

//                double debt = list.get(i).getDouble("debt");
//                for (int j=0; j<listResult.size(); j++){
//                    BaseModel customer2 = new BaseModel(listResult.get(j).getJsonObject("customer"));
//                    if (customer1.getString("id").equals(customer2.getString("id"))){
////                        double debt = listResult.get(j).getDouble("debt") + list.get(i).getDouble("debt");
//                        debt += listResult.get(j).getDouble("debt");
//                        listResult.get(j).put("debt", debt );
//                        total += listResult.get(j).getDouble("debt");
//                        checkDup = true;
//                        break;
//                    }
//                }
//
//                if (debt == list.get(i).getDouble("debt")){
//                    BaseModel objectDetail = new BaseModel();
//                    objectDetail.put("id",list.get(i).getString("id") );
//                    objectDetail.put("createAt",list.get(i).getLong("createAt") );
//                    objectDetail.put("updateAt",list.get(i).getLong("updateAt") );
//                    objectDetail.put("user",list.get(i).getJsonObject("user") );
//                    objectDetail.put("customer",list.get(i).getJsonObject("customer") );
//                    objectDetail.put("distributor",list.get(i).getJsonObject("distributor") );
//                    objectDetail.put("payments",list.get(i).getString("payments") );
//                    objectDetail.put("total",list.get(i).getDouble("total") );
//                    objectDetail.put("debt",list.get(i).getDouble("debt") );
//                    objectDetail.put("paid",list.get(i).getDouble("paid") );
//                    objectDetail.put("note",list.get(i).getString("note") );
//
//                    listResult.add(objectDetail);
//
//
//                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return listResult;
    }

    public static List<ProductGroup> sortProductGroup(List<ProductGroup> list, boolean reverse){
        Collections.sort(list, new Comparator<ProductGroup>(){
            public int compare(ProductGroup obj1, ProductGroup obj2) {
                return obj1.getString("name").compareToIgnoreCase(obj2.getString("name"));
            }
        });

        if (reverse){
            Collections.reverse(list);
        }

        return list;
    }

    public static List<Product> sortProduct(List<Product> list, boolean reverse){
        Collections.sort(list, new Comparator<Product>(){
            public int compare(Product obj1, Product obj2) {
                return obj1.getString("name").compareToIgnoreCase(obj2.getString("name"));
            }
        });

        if (reverse){
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BaseModel> sortbyKey(final String key, List<BaseModel> list, boolean reverse){
        Collections.sort(list, new Comparator<BaseModel>(){
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString(key).compareToIgnoreCase(obj2.getString(key));
            }
        });

        if (reverse){
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BaseModel> mergeWithReturnBill(List<BaseModel> listbill) {
        final List<BaseModel> listResult = new ArrayList<>();

        for (int i = 0; i < listbill.size(); i++) {
            if (!Util.isBillReturn(listbill.get(i))) {
                try {
                    BaseModel tempBill = new BaseModel();
                    tempBill.put("id", listbill.get(i).getInt("id"));
                    tempBill.put("createAt", listbill.get(i).getLong("createAt"));
                    tempBill.put("updateAt", listbill.get(i).getLong("updateAt"));
                    tempBill.put("user", listbill.get(i).getJsonObject("user"));
                    tempBill.put("customer", listbill.get(i).getJsonObject("customer"));
                    tempBill.put("distributor", listbill.get(i).getJsonObject("distributor"));
                    tempBill.put("note", listbill.get(i).getString("note"));
                    tempBill.put("payments", listbill.get(i).getJSONArray("payments"));
                    tempBill.put("total", listbill.get(i).getDouble("total"));
                    tempBill.put("debt", listbill.get(i).getDouble("debt"));
                    tempBill.put("paid", listbill.get(i).getDouble("paid"));
                    tempBill.put("idbill",listbill.get(i).getString("id") );

                    tempBill.put("billDetails", listbill.get(i).getJSONArray("billDetails"));

                    for (int j = 0; j < listbill.size(); j++) {
                        if (j != i) {
                            if (Util.isBillReturn(listbill.get(j))
                                    && listbill.get(i).getInt("id") == Integer.parseInt(listbill.get(j).getString("note"))) {

                                Double total = tempBill.getDouble("total") + listbill.get(j).getDouble("total");
                                tempBill.put("total", total);
                                Double debt = tempBill.getDouble("debt") + listbill.get(j).getDouble("debt");
                                tempBill.put("debt", debt);
                                Double paid = tempBill.getDouble("paid") + listbill.get(j).getDouble("paid");
                                tempBill.put("paid", paid);
                                String idbills = String.format("%s-%s", tempBill.getString("idbill"),listbill.get(j).getString("id")) ;
                                tempBill.put("idbill",idbills);

                                JSONArray array1 = tempBill.getJSONArray("billDetails");
                                JSONArray array2 = listbill.get(j).getJSONArray("billDetails");
                                JSONArray arrayMerge = new JSONArray();
                                for (int a =0; a<array1.length(); a++){
                                    JSONObject jsonObject = array1.getJSONObject(a);
                                    arrayMerge.put(jsonObject);
                                }

                                for (int aa = 0; aa < array2.length(); aa++) {
                                    JSONObject jsonObject = array2.getJSONObject(aa);
                                    arrayMerge.put(jsonObject);
                                }
                                tempBill.put("billDetails", arrayMerge);

                            }
                        }
                    }

                    listResult.add(tempBill);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return listResult;
    }

}
