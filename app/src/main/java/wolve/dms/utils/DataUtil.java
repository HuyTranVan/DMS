package wolve.dms.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import wolve.dms.R;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.callback.CallbackListObject;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;

import static wolve.dms.activities.BaseActivity.createGetParam;

public class DataUtil {
    public static String newBillJsonParam(int customerId,
                                          int userId,
                                          final Double total,
                                          double netTotal,
                                          final Double paid,
                                          List<BaseModel> listProduct,
                                          String note,
                                          int deliverBy,
                                          int isreturnBill) {
        final JSONObject params = new JSONObject();
        try {
            params.put("debt", total - paid);
            params.put("total", total);
            params.put("total_net", netTotal);
            params.put("paid", paid);
            params.put("customerId", customerId);
            params.put("distributorId", Distributor.getDistributorId());
            params.put("userId", userId);
            params.put("note", Util.encodeString(note));
            params.put("deliverBy", deliverBy);
            params.put("isReturn", isreturnBill);
            params.put("importFunction", Distributor.getImportFunction());

            if (User.getCurrentUser().hasKey("warehouse_id")) {
                params.put("warehouse_id", User.getCurrentUser().getInt("warehouse_id"));
            }

            JSONArray array = new JSONArray();
            for (int i = 0; i < listProduct.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("product_id", listProduct.get(i).getInt("id"));
                object.put("discount", listProduct.get(i).getDouble("discount"));
                object.put("quantity", listProduct.get(i).getInt("quantity"));

                array.put(object);
            }
            params.put("billDetails", (Object) array);

        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }


    public static String updateBillDeliveredJsonParam(int customerId, BaseModel currentBill, int userid, List<BaseModel> listProduct) {
        JSONObject params = new JSONObject();
        try {
            params.put("id", currentBill.getInt("id"));
            params.put("note", currentBill.getString("note"));
            params.put("deliverBy", userid);
            params.put("isReturn", currentBill.getInt("isReturn"));
            params.put("customerId", customerId);
            params.put("user_id", currentBill.getInt("user_id"));
            params.put("warehouse_id", User.getCurrentUser().getInt("warehouse_id"));
            params.put("importFunction", Distributor.getImportFunction());

            JSONArray array = new JSONArray();
            for (int i = 0; i < listProduct.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("product_id", listProduct.get(i).getInt("product_id"));
                object.put("discount", listProduct.get(i).getDouble("discount"));
                object.put("quantity", listProduct.get(i).getInt("quantity"));
                object.put("productName", Util.encodeString(listProduct.get(i).getString("productName")));
                object.put("purchasePrice", listProduct.get(i).getDouble("purchasePrice"));
                object.put("basePrice", listProduct.get(i).getDouble("basePrice"));

                array.put(object);
            }
            params.put("billDetails", (Object) array);

        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }

    public static String updateTempBillJsonParam(int temp_id,
                                                 int bill_id,
                                                 int user_id,
                                                 int warehouse_id,
                                                 double total,
                                                 double total_net,
                                                 String note,
                                                 List<BaseModel> details) {
        JSONObject params = new JSONObject();
        try {
            params.put("temp_id", temp_id);
            params.put("id", bill_id);
            params.put("user_id", user_id);
            params.put("warehouse_id", warehouse_id);
            params.put("total", total);
            params.put("total_net", total_net);
            params.put("note", Util.encodeString(note));


            JSONArray array = new JSONArray();
            for (int i = 0; i < details.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("id", details.get(i).getInt("id"));
                object.put("discount", details.get(i).getDouble("discount"));
                object.put("quantity", details.get(i).getInt("quantity"));
                object.put("deleted", details.get(i).getBoolean("deleted"));

                array.put(object);
            }
            params.put("billDetails", (Object) array);

        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }


    public static String createPostImportJsonParam(int warehouse_id,
                                                   int from_warehouse,
                                                   List<BaseModel> listProduct,
                                                   int userAccept,
                                                   String note) {
        final JSONObject params = new JSONObject();
        try {
            params.put("warehouse_id", warehouse_id);
            params.put("from_warehouse", from_warehouse);
            params.put("note", Util.encodeString(note));
            params.put("acceptBy", userAccept);

            JSONArray array = new JSONArray();
            for (int i = 0; i < listProduct.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("id", listProduct.get(i).hasKey("product_id") ? listProduct.get(i).getInt("product_id") : listProduct.get(i).getInt("id"));
                object.put("quantity", listProduct.get(i).getInt("quantity"));

                array.put(object);
            }
            params.put("importDetails", (Object) array);
        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }

    public static String createUpdateAcceptImportParam(int import_id, int user_id) {
        final JSONObject params = new JSONObject();
        try {
            params.put("id", import_id);
            params.put("acceptBy", user_id);

        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }

    public static void checkInventory(List<BaseModel> listproduct, int warehouse_id, CallbackListObject listener) {
        BaseModel param = createGetParam(String.format(ApiUtil.INVENTORIES(), warehouse_id, 0), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                List<BaseModel> listresult = compareBillAndInventory(DataUtil.mergeProductDetail(listproduct), list);
                if (checkNegative(listresult, "differenceQuantity")) {
                    listener.onResponse(listresult);
                } else {
                    listener.onResponse(new ArrayList<>());
                }
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }

    public static List<BaseModel> compareBillAndInventory(List<BaseModel> listproduct, List<BaseModel> listinventory) {
        List<BaseModel> results = new ArrayList<>();

        for (int i = 0; i < listproduct.size(); i++) {
            BaseModel object = new BaseModel();
            object.put("id", listproduct.get(i).hasKey("product_id") ? listproduct.get(i).getInt("product_id") : listproduct.get(i).getInt("id"));
            object.put("productName", listproduct.get(i).hasKey("productName") ? listproduct.get(i).getString("productName") : listproduct.get(i).getString("name"));
            object.put("productQuantity", listproduct.get(i).getInt("quantity"));
            object.put("inventoryQuantity", 0);

            BaseModel modelDup = DataUtil.reuturnDuplicate(listinventory, "product_id", listproduct.get(i), listproduct.get(i).hasKey("product_id") ? "product_id" : "id");
            if (modelDup != null) {
                object.put("inventoryQuantity", modelDup.getInt("currentQuantity"));
            }

            object.put("differenceQuantity", object.getInt("inventoryQuantity") - object.getInt("productQuantity"));

            results.add(object);

        }
        return results;
    }

    public static boolean checkNegative(List<BaseModel> list, String key) {
        boolean check = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getInt(key) < 0) {
                check = true;
                break;
            }
        }

        return check;
    }

    public static List<String> createListPaymentParam(int customerId, List<BaseModel> list, boolean payByReturn) {
        List<String> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String s = String.format(ApiUtil.PAY_PARAM,
                    customerId,
                    String.valueOf(Math.round(list.get(i).getDouble("paid"))),
                    list.get(i).getInt("billId"),
                    list.get(i).getInt("user_id"),
                    "",
                    //String.valueOf(Math.round(list.get(i).getDouble("billTotal"))),
                    payByReturn ? 1 : 0,
                    User.getId());

            results.add(s);

        }

        return results;
    }

    public static String createPostPaymentParam(int customerId, int user_id, double paid, int billId, String note, boolean payByReturn) {
        return String.format(ApiUtil.PAY_PARAM,
                customerId,
                String.valueOf(Math.round(paid)),
                billId,
                user_id,
                Util.encodeString(note),
                payByReturn ? 1 : 0,
                User.getId());



    }

    public static List<String> convertListObject2AString(List<BaseModel> list, String key) {
        List<String> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            results.add(list.get(i).getString(key));

        }

        return results;

    }



    public static JSONArray convertListObject2Array(List<BaseModel> list) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            array.put(list.get(i).BaseModelJSONObject());

        }

        return array;

    }

    public static List<BaseModel> array2ListObject(String array) {
        List<BaseModel> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(array);
            for (int i = 0; i < jsonArray.length(); i++) {
                BaseModel object = new BaseModel(jsonArray.getJSONObject(i));
                list.add(object);
            }

        } catch (JSONException e) {
            return list;
        }

        return list;
    }

    public static List<BaseModel> array2ListBaseModel(JSONArray array) {
        List<BaseModel> list = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                list.add(new BaseModel(array.getJSONObject(i)));
            }

        } catch (JSONException e) {
            return list;
        }

        return list;
    }

    public static String createCheckinNote(List<String> products, String note) {
        String result = "";
        if (products.size() > 0) {
            result = "SP giới thiệu: " + products.toString().replace("{", "").replace("}", "") + "\n";
        }
        if (!Util.isEmpty(note)) {
            result = result + note;
        }

        return result;
    }

    public static void saveProductPopular(List<BaseModel> list) {
        List<BaseModel> products = CustomFixSQL.getListObject(Constants.PRODUCT_POPULAR);
        for (BaseModel model : list) {
            boolean check = false;
            for (BaseModel mProduct : products) {
                if (model.getInt("product_id") == mProduct.getInt("product_id")) {
                    mProduct.put("value", mProduct.getInt("value") + 1);
                    check = true;
                    break;
                }
            }
            if (!check) {
                BaseModel object = new BaseModel();
                object.put("product_id", model.getInt("product_id"));
                object.put("value", 1);
                products.add(object);

            }
        }
        CustomFixSQL.setListBaseModel(Constants.PRODUCT_POPULAR, products);

    }

    public static List<BaseModel> getProductPopular(List<BaseModel> list) {
        List<BaseModel> products = CustomFixSQL.getListObject(Constants.PRODUCT_POPULAR);
        List<BaseModel> product_top = CustomSQL.getListObject(Constants.PRODUCT_SUGGEST_LIST);
        for (BaseModel model : list) {
            model.put("value", 0);
            for (BaseModel mProduct : products) {
                if (model.getInt("product_id") == mProduct.getInt("product_id")) {
                    model.put("value", mProduct.getInt("value"));
                    break;
                }
            }

        }
        DataUtil.sortbyStringKey("value", list, true);

        for (int i = 0; i < list.size(); i++) {
            if (checkDuplicate(product_top, "product_id", list.get(i))) {

                list.add(0, list.remove(i));
            }
        }

        return list;

    }

    public static List<BaseModel> sortProductGroup(List<BaseModel> list, boolean reverse) {
        Collections.sort(list, new Comparator<BaseModel>() {
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString("name").compareToIgnoreCase(obj2.getString("name"));
            }
        });

        if (reverse) {
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BaseModel> sortProduct(List<BaseModel> list, boolean reverse) {
        Collections.sort(list, new Comparator<BaseModel>() {
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString("name").compareToIgnoreCase(obj2.getString("name"));
            }
        });

        if (reverse) {
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BaseModel> sortbyStringKey(final String key, List<BaseModel> list, boolean reverse) {
        Collections.sort(list, new Comparator<BaseModel>() {
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString(key).compareToIgnoreCase(obj2.getString(key));
            }
        });

        if (reverse) {
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BaseModel> sortbyDoubleKey(final String key, List<BaseModel> list, boolean reverse) {
        Collections.sort(list, new Comparator<BaseModel>() {
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getDouble(key).compareTo(obj2.getDouble(key));
            }
        });

        if (reverse) {
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BaseModel> listTempBill(List<BaseModel> list) {
        List<BaseModel> listBillByUser = new ArrayList<>();
        for (BaseModel model : list) {
            if (User.getCurrentRoleId() == Constants.ROLE_ADMIN || User.getCurrentRoleId() == Constants.ROLE_DELIVER) {
                listBillByUser.add(model);

            } else if (User.getId() == model.getInt("user_id")) {
                listBillByUser.add(model);
            }

        }

        return listBillByUser;


    }

    public static List<BaseModel> mergeProductDetail(List<BaseModel> list) {
        List<BaseModel> listResult = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (!DataUtil.checkDuplicate(listResult, "id", list.get(i))) {
                BaseModel newDetail = new BaseModel();
                newDetail.put("product_id", list.get(i).hasKey("product_id") ? list.get(i).getInt("product_id") : list.get(i).getInt("id"));
                newDetail.put("productName", list.get(i).hasKey("productName") ? list.get(i).getString("productName") : list.get(i).getString("name"));

                int quantity = list.get(i).getInt("quantity");
                for (int ii = i + 1; ii < list.size(); ii++) {
                    if (list.get(ii).getInt("id") == list.get(i).getInt("id")) {
                        quantity += list.get(ii).getInt("quantity");
                    }
                }

                newDetail.put("quantity", quantity);

                listResult.add(newDetail);
            }

        }

        return listResult;
    }

    public static BaseModel rebuiltCustomer(BaseModel customer, boolean isDeliver) {
        BaseModel customerResult = new BaseModel();

        customerResult.put("id", customer.getInt("id"));
        customerResult.put("createAt", customer.getLong("createAt"));
        customerResult.put("updateAt", customer.getLong("updateAt"));
        customerResult.put("name", customer.getString("name"));
        customerResult.put("note", customer.getString("note"));
        customerResult.put("signBoard", customer.getString("signBoard"));
        customerResult.put("phone", customer.getString("phone"));
        customerResult.put("address", customer.getString("address"));
        customerResult.put("street", customer.getString("street"));
        customerResult.put("district", customer.getString("district"));
        customerResult.put("province", customer.getString("province"));
        customerResult.put("lat", customer.getDouble("lat"));
        customerResult.put("lng", customer.getDouble("lng"));
        customerResult.put("volumeEstimate", customer.getInt("volumeEstimate"));
        customerResult.put("checkinCount", customer.getInt("checkinCount"));
        customerResult.put("shopType", customer.getString("shopType"));
        customerResult.put("checkIns", customer.getJsonObject("checkIns"));
        //customerResult.put("currentDebt", customer.getDouble("currentDebt"));
        customerResult.put("status", customer.getJsonObject("status"));
        customerResult.put("distributor_id", customer.getInt("distributor_id"));
        customerResult.put("status_id", customer.getInt("status_id"));
        customerResult.put("rating", customer.getDouble("rating"));
        customerResult.put("rating_count", customer.getInt("rating_count"));
        customerResult.put("waiting_list", customer.getBoolean("waiting_list"));

        List<BaseModel> listOriginalBill = new ArrayList<>(DataUtil.array2ListObject(customer.getString("bills")));
        List<BaseModel> listCheckin = new ArrayList<>(DataUtil.array2ListObject(customer.getString("checkins")));

        if (listOriginalBill.size() > 0) {
            customerResult.put("last_time_order", Util.countDay(listOriginalBill.get(listOriginalBill.size() - 1).getLong("createAt")));
            customerResult.put("user_order", listOriginalBill.get(listOriginalBill.size() - 1).getBaseModel("user").getString("displayName"));

        }

        if (listCheckin.size() > 0) {
            customerResult.put("count_checkin", listCheckin.size());
            customerResult.put("last_time_checkin", Util.countDay(listCheckin.get(listCheckin.size() - 1).getLong("createAt")));
            customerResult.put("user_checkin", listCheckin.get(listCheckin.size() - 1).getBaseModel("user").getString("displayName"));

        }

        if (getTempBill(listOriginalBill) != null) {
            customerResult.putBaseModel(Constants.TEMPBILL, getTempBill(listOriginalBill));
        }

        List<BaseModel> listBill = new ArrayList<>(remakeBill(listOriginalBill, isDeliver));
        customerResult.putList(Constants.BILLS, listBill);
        customerResult.putList(Constants.DEBTS, getAllBillHaveDebt(listBill));
        customerResult.putList(Constants.PAYMENTS, filterPaymentByUser(DataUtil.array2ListObject(customer.getString("payments"))));
        customerResult.putList(Constants.CHECKINS, listCheckin);

        return customerResult;
    }

    public static List<BaseModel> remakeBill(List<BaseModel> listbill, boolean isDeliver) {
        List<BaseModel> listBillByUser = new ArrayList<>();
        for (BaseModel model : listbill) {
            if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                listBillByUser.add(model);

            } else if (User.getCurrentRoleId() == Constants.ROLE_DELIVER && isDeliver) {
                listBillByUser.add(model);

            } else if (User.getId() == model.getInt("user_id")) {
                listBillByUser.add(model);
            }

        }

        return listBillByUser;

    }

    public static List<BaseModel> filterListImport(List<BaseModel> list, int id) {
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model : list) {
            if (model.getInt("warehouse_id") == id) {
                results.add(model);
            }
        }
        return results;
    }

    public static List<BaseModel> filterListTempImport(List<BaseModel> list) {
        List<BaseModel> results = new ArrayList<>();
        for (BaseModel model : list) {
            if (Util.isAdmin()
                    || User.getId() == model.getBaseModel("fr_warehouse").getInt("user_id")
                    || User.getId() == model.getBaseModel("warehouse").getInt("user_id")) {
                results.add(model);
            }
        }
        return results;
    }

    public static List<BaseModel> filterPaymentByUser(List<BaseModel> listpayment) {
        List<BaseModel> listPaymentByUser = new ArrayList<>();
        for (BaseModel model : listpayment) {
            if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                listPaymentByUser.add(model);

            } else if (User.getId() == model.getInt("user_id")) {
                listPaymentByUser.add(model);
            }

        }

        return listPaymentByUser;

    }

    public static List<BaseModel> remakePaymentByDate(List<BaseModel> listpayment) {
        List<BaseModel> mResults = new ArrayList<>();
        for (int i = 0; i < listpayment.size(); i++) {
            boolean check = false;

            for (int ii = 0; ii < mResults.size(); ii++) {
                if (listpayment.get(i).getLong("createAt") / 1000 == mResults.get(ii).getLong("createAt") / 1000) {
                    double paid = mResults.get(ii).getDouble("paid") + listpayment.get(i).getDouble("paid");
                    mResults.get(ii).put("paid", paid);
                    check = true;
                    break;
                }
            }

            if (!check) {
                BaseModel object = new BaseModel();
                object.put("id", listpayment.get(i).getInt("id"));
                object.put("createAt", listpayment.get(i).getLong("createAt"));
                object.put("note", listpayment.get(i).getString("note"));
                object.put("paid", listpayment.get(i).getDouble("paid"));
                object.put("user_id", listpayment.get(i).getInt("user_id"));
                object.put("customer_id", listpayment.get(i).getInt("customer_id"));
                object.put("payByReturn", listpayment.get(i).getInt("payByReturn"));
                object.put("user_collect", listpayment.get(i).getInt("user_collect"));

                mResults.add(object);
            }

        }

        return mResults;

    }

    public static List<BaseModel> getAllBillDetail(List<BaseModel> listbill) {
        final List<BaseModel> listResult = new ArrayList<>();

        for (int i = 0; i < listbill.size(); i++) {
            List<BaseModel> billDetails = new ArrayList<>(array2ListObject(listbill.get(i).getString("billDetails")));
            listResult.addAll(billDetails);

            List<BaseModel> billReturns = new ArrayList<>(array2ListObject(listbill.get(i).getString("billReturn")));
            for (int ii = 0; ii < billReturns.size(); ii++) {
                List<BaseModel> billReturnDetails = new ArrayList<>(array2ListObject(billReturns.get(ii).getString("billDetails")));
                listResult.addAll(billReturnDetails);
            }


        }


        return listResult;

    }

    public static List<BaseModel> getAllBillHaveDebt(List<BaseModel> listbill) {
        List<BaseModel> list = new ArrayList<>();
        for (int i = 0; i < listbill.size(); i++) {
            if (listbill.get(i).getDouble("debt") > 0 && !listbill.get(i).isNull(Constants.DELIVER_BY)) {
                list.add(listbill.get(i));

            }
        }

        DataUtil.sortbyStringKey("createAt", list, true);
        return list;
    }

    public static List<BaseModel> getListDebtRemain(List<BaseModel> listdebt, BaseModel currentBill) {
        List<BaseModel> list = new ArrayList<>();

        for (BaseModel bill : listdebt) {

            if (bill.getInt("id") != currentBill.getInt("id")) {
                list.add(bill);
            }
        }

        return list;

    }

    public static BaseModel getTempBill(List<BaseModel> listbill) {
        BaseModel result = null;
        for (BaseModel bill : listbill) {
            if (bill.isNull("deliverBy")) {
                result = bill;
                break;

            }

        }
        return result;
    }

    public static String defineBDFPercent(List<BaseModel> listDetails) {
        double total = 0.0;
        double bdf = 0.0;

        for (int i = 0; i < listDetails.size(); i++) {
            if (listDetails.get(i).getBoolean("promotion")
                    && listDetails.get(i).getDouble("unitPrice").equals(listDetails.get(i).getDouble("discount"))) {
                bdf += listDetails.get(i).getInt("quantity") * listDetails.get(i).getDouble("purchasePrice");
            } else {
                total += listDetails.get(i).getInt("quantity") * listDetails.get(i).getDouble("purchasePrice");

            }
        }

        double percent = bdf * 100 / total;

        return new DecimalFormat("#.##").format(percent);

        //tvBDF.setText(String.format("BDF: %s ",new DecimalFormat("#.##").format(percent)) +"%");

    }

    public static double defineBDFValue(List<BaseModel> listDetails) {
        double bdf = 0.0;

        for (int i = 0; i < listDetails.size(); i++) {
            if (listDetails.get(i).getBoolean("promotion")
                    && listDetails.get(i).getDouble("unitPrice").equals(listDetails.get(i).getDouble("discount"))) {
                bdf += listDetails.get(i).getInt("quantity") * listDetails.get(i).getDouble("purchasePrice");
            }

        }

        return bdf;

    }

    public static List<List<Object>> updateIncomeByUserToSheet(String startDate, String endDate, List<BaseModel> users, List<BaseModel> listbill, List<BaseModel> listbilldetail, List<BaseModel> listpayment, List<BaseModel> listdebt) {
        List<List<Object>> values = new ArrayList<>();

        List<Object> column0 = new ArrayList<>();
        column0.add(String.format("Từ ngày %s đến %s", startDate, endDate));
        column0.add("TÊN NHÂN VIÊN");
        column0.add("DOANH SỐ BÁN HÀNG TRONG THÁNG");
        column0.add("TIỀN MẶT THU VỀ");
        column0.add("CÔNG NỢ HIỆN TẠI");
        column0.add("GIÁ BÁN - GIÁ NHẬP");
        values.add(0, column0);


        for (int i = 1; i < users.size(); i++) {

            List<Object> column1 = new ArrayList<>();
            List<Object> column2 = new ArrayList<>();

            column1.add(0, "");
            column2.add(0, "");

            column1.add(users.get(i));
            column2.add(0, "");
//total bill
            double totalbill = 0.0;
            for (BaseModel bill : listbill) {
                if (bill.getBaseModel("user").getString("displayName").equals(users.get(i).getString("displayName"))) {
                    totalbill += bill.getDouble("total");
                }
            }
            column1.add(totalbill);
            column2.add("--");
//payment
            double pay = 0.0;
            for (BaseModel payment : listpayment) {
                if (payment.getBaseModel("user").getString("displayName").equals(users.get(i).getString("displayName"))) {
                    pay += payment.getDouble("paid");
                }
            }
            column1.add(pay);
            column2.add("--");

//profit
            column1.add("--");
            column2.add("--");
//line seperate
            column1.add("--");
            column2.add("--");

//debt list
//debt
            double deb = 0.0;

            column1.add("TÊN CỬA HÀNG");
            column2.add("CÒN NỢ");
            for (BaseModel debt : listdebt) {
                if (debt.getString("userName").equals(users.get(i).getString("displayName"))) {
                    deb += debt.getDouble("currentDebt");

                    String add = String.format("%s %s\n(%s - %s)",
                            Constants.shopName[debt.getInt("shopType")].toUpperCase(),
                            debt.getString("signBoard").toUpperCase(),
                            debt.getString("street"),
                            debt.getString("district"));
                    column1.add(add);
                    column2.add(debt.getDouble("currentDebt"));

                }
            }
            column1.add(4, deb);
            column2.add(4, "--");

            values.add(column1);
            values.add(column2);

        }

        return values;
    }

    public static double sumValueFromList(List<BaseModel> list, String key) {
        double result = 0.0;
        for (BaseModel item : list) {
            result += item.getDouble(key);
        }

        return result;
    }

    public static double sumMoneyFromList(List<BaseModel> list, String key, String quantitykey) {
        double result = 0.0;
        for (BaseModel item : list) {
            result += item.getDouble(key) * item.getInt(quantitykey);
        }

        return result;
    }


    public static int sumNumberFromList(List<BaseModel> list, String key) {
        int result = 0;
        for (BaseModel item : list) {
            result += item.getInt(key);
        }

        return result;
    }

    public static boolean checkDuplicate(List<BaseModel> list, String key, BaseModel object) {
        boolean check = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getString(key).equals(object.getString(key))) {
                check = true;
                break;
            }
        }

        return check;
    }

    public static boolean checkDuplicate(List<BaseModel> list, String key_list, BaseModel object, String key_object) {
        boolean check = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getString(key_list).equals(object.getString(key_object))) {
                check = true;
                break;
            }
        }

        return check;
    }

    public static boolean checkDuplicate(List<String> list, String value) {
        boolean check = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(value)) {
                check = true;
                break;
            }
        }

        return check;
    }

    public static BaseModel reuturnDuplicate(List<BaseModel> list, String key1, BaseModel object, String key2) {
        BaseModel result = null;
        //boolean check = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getString(key1).equals(object.getString(key2))) {
                //check = true;
                result = list.get(i);
                break;
            }
        }

        return result;
    }

    public static boolean checkDuplicateMarker(List<Marker> list, BaseModel object) {
        boolean check = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTitle().equals(object.getString("id"))) {
                check = true;
                break;
            }
        }

        return check;
    }

    public static BaseModel countMarkerStatus(List<Marker> list) {
        BaseModel model = new BaseModel();
        int interest = 0;
        int ordered = 0;
        for (Marker item : list) {
            switch (item.getSnippet()) {
                case "0":
                    interest += 1;
                    break;

                case "1":
                    interest += 1;
                    break;

                case "2":

                    break;

                case "3":
                    ordered += 1;
                    break;
            }

        }

        model.put(Constants.MARKER_ALL, list.size());
        model.put(Constants.MARKER_INTERESTED, interest);
        model.put(Constants.MARKER_ORDERED, ordered);

        return model;
    }

    public static List<BaseModel> removeObjectFromList(List<BaseModel> list, BaseModel value, String key) {
        List<BaseModel> listResutl = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getString(key).equals(value.getString(key))) {
                listResutl.add(list.get(i));
            }
        }

        return listResutl;
    }

    public static List<BaseModel> listImportNotEnough(List<BaseModel> listSelect){
        List<BaseModel> results = new ArrayList<>();
        for (int i=0; i<listSelect.size(); i++){
            if (listSelect.get(i).getInt("quantity") > listSelect.get(i).getInt("currentQuantity") ){
                BaseModel item = BaseModel.copyToNewBaseModel(listSelect.get(i));
                item.put("quantity",listSelect.get(i).getInt("quantity") - listSelect.get(i).getInt("currentQuantity") );
                results.add(item);

            }

        }

        return results;
    }

    public static void listImportNotEnough(List<BaseModel> listRequest, BaseModel tempWarehouse, CallbackListObject listener){
        List<BaseModel> results = new ArrayList<>();
        BaseModel param = createGetParam(String.format(ApiUtil.INVENTORIES(), tempWarehouse.getInt("id"), 0), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                List<BaseModel> mProducts = mergeProductDetail(listRequest);

                for (int i=0; i<mProducts.size(); i++){
                    boolean check = false;
                    BaseModel item = BaseModel.copyToNewBaseModel(mProducts.get(i));
                    item.put("masterWarehouse", list.get(0).getBaseModel("masterWarehouse").BaseModelJSONObject());
                    for (int ii=0; ii<list.size(); ii++){
                        //if (!list.get(ii).hasKey("currentQuantity")){
                        if (mProducts.get(i).getInt("product_id") == list.get(ii).getInt("product_id")){
                            if (mProducts.get(i).getInt("quantity") > list.get(ii).getInt("currentQuantity")){
                                item.put("quantity", mProducts.get(i).getInt("quantity") - list.get(ii).getInt("currentQuantity") );
                                results.add(item);

                            }
                            check = true;
                            break;

                        }
                    }
                    if (!check){
                        item.put("quantity",mProducts.get(i).getInt("quantity") );
                        results.add(item);
                    }

                }
                listener.onResponse(results);
            }

            @Override
            public void onError(String error) {
                listener.onResponse(new ArrayList<>());
            }
        }, 1).execute();

    }

    public static List<String> listObject2ListString(List<BaseModel> list, String key){
        List<String> results = new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            results.add(list.get(i).getString(key));
        }
        return results;
    }

    public static void setHighLightedText(TextView tv, String textToHighlight) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight, 0);
        Spannable wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1)
                break;
            else {
                // set color here
                wordToSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), ofe, ofe + textToHighlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
            }
        }
    }

    public static List<String> createList2String(String one, String two){
        List<String> list = new ArrayList<>();
        list.add(one);
        list.add(two);

        return list;

    }

    public static List<String> createList3String(String one, String two, String three){
        List<String> list = new ArrayList<>();
        list.add(one);
        list.add(two);
        list.add(three);

        return list;

    }

}
