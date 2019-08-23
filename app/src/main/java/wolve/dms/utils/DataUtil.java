package wolve.dms.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wolve.dms.apiconnect.Api_link;
import wolve.dms.libraries.Security;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.BillDetail;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;

public class DataUtil {
    public static String createPostBillParam(int customerId, final Double total, final Double paid, List<BaseModel> listProduct, String note){
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

    public static String updateBillHaveReturnParam(int customerId, BaseModel currentBill, BaseModel billReturn, Double sumreturn){
        JSONObject params = new JSONObject();
        JSONObject objectNote = new JSONObject();
        try {
            params.put("billDetails", (Object) null);
            params.put("id", currentBill.getInt("id"));
            params.put("total", currentBill.getDouble("total"));
            params.put("paid", 0.0);
            params.put("debt", currentBill.getDouble("total") );

            params.put("customerId", customerId);
            params.put("distributorId", currentBill.getBaseModel("distributor").getInt("id"));
            params.put("userId", currentBill.getBaseModel("user").getInt("id"));

            JSONObject objReturn = new JSONObject();
            objReturn.put("id", billReturn.getInt("id"));
            objReturn.put("createAt", billReturn.getLong("createAt"));
            objReturn.put("updateAt", billReturn.getLong("updateAt"));
            objReturn.put("user", (billReturn.getJsonObject("user")));
            objReturn.put("total",0.0 );
            objReturn.put("paid", 0.0);
            objReturn.put("debt", 0.0);
            objReturn.put("billDetails", billReturn.getJSONArray("billDetails"));

//            JSONObject objPay = new JSONObject();
//            objPay.put("createAt",billReturn.getLong("createAt") );
//            objPay.put("user", (billReturn.getJsonObject("user")));
//            objPay.put("paid", sumreturn);
//            objPay.put("idbillreturn", billReturn.getInt("id"));
//            objPay.put("bill_date", billReturn.getLong("createAt"));


            String currentNote = Security.decrypt(currentBill.getString("note"));

            JSONArray arrayReturnNote =new JSONArray();
            JSONArray arrayPayNote =new JSONArray();

            if (Util.isJSONObject(currentNote)){
                BaseModel noteObject = new BaseModel(currentNote);
                if (noteObject.hasKey(Constants.HAVEBILLRETURN)){
                    JSONArray arrReturn = noteObject.getJSONArray(Constants.HAVEBILLRETURN);
                    for (int i=0; i<arrReturn.length(); i++){
                        arrayReturnNote.put(arrReturn.getJSONObject(i));
                    }

                }

                if (noteObject.hasKey(Constants.PAYBYTRETURN)){
                    JSONArray arrPay = noteObject.getJSONArray(Constants.PAYBYTRETURN);
                    for (int j=0; j<arrPay.length(); j++){
                        arrayPayNote.put(arrPay.getJSONObject(j));
                    }
                }

            }

            arrayReturnNote.put(objReturn);
            //arrayPayNote.put(objPay);
            objectNote.put(Constants.HAVEBILLRETURN, arrayReturnNote);
            objectNote.put(Constants.PAYBYTRETURN, arrayPayNote);


            params.put("note", Security.encrypt(objectNote.toString()));



        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }

    public static String updateBillHavePaymentParam(int customerId, BaseModel currentBill, BaseModel billReturn, Double paid){
        JSONObject params = new JSONObject();
        JSONObject objectNote = new JSONObject();
        try {
            params.put("billDetails", (Object) null);
            params.put("id", currentBill.getInt("id"));
            params.put("total", currentBill.getDouble("total"));
            params.put("paid", 0.0);
            params.put("debt", currentBill.getDouble("total") );

            params.put("customerId", customerId);
            params.put("distributorId", currentBill.getBaseModel("distributor").getInt("id"));
            params.put("userId", currentBill.getBaseModel("user").getInt("id"));

            JSONObject objPay = new JSONObject();
            objPay.put("createAt",billReturn.getLong("createAt") );
            objPay.put("user", (billReturn.getJsonObject("user")));
            objPay.put("paid", paid);
            objPay.put("idbillreturn", billReturn.getInt("id"));
            objPay.put("bill_date", billReturn.getLong("createAt"));


            String currentNote = Security.decrypt(currentBill.getString("note"));

            JSONArray arrayReturnNote =new JSONArray();
            JSONArray arrayPayNote =new JSONArray();

            if (Util.isJSONObject(currentNote)){
                BaseModel noteObject = new BaseModel(currentNote);

                if (noteObject.hasKey(Constants.HAVEBILLRETURN)){
                    JSONArray arrReturn = noteObject.getJSONArray(Constants.HAVEBILLRETURN);
                    for (int i=0; i<arrReturn.length(); i++){
                        arrayReturnNote.put(arrReturn.getJSONObject(i));
                    }

                }

                if (noteObject.hasKey(Constants.PAYBYTRETURN)){
                    JSONArray arrPay = noteObject.getJSONArray(Constants.PAYBYTRETURN);
                    for (int j=0; j<arrPay.length(); j++){
                        arrayPayNote.put(arrPay.getJSONObject(j));
                    }
                }

            }

            arrayPayNote.put(objPay);
            objectNote.put(Constants.HAVEBILLRETURN, arrayReturnNote);
            objectNote.put(Constants.PAYBYTRETURN, arrayPayNote);

            params.put("note", Security.encrypt(objectNote.toString()));

        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }


    public static List<String> createListPaymentParam(int customerId,List<BaseModel> list){
        List<String> results = new ArrayList<>();
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

        return results;
    }

    public static String createPostPaymentParam(int customerId, double paid, int billId){
        String s = String.format(Api_link.PAY_PARAM,
                customerId,
                String.valueOf(Math.round(paid)),
                billId,
                User.getId(),
                "");


        return s;
    }


    public static JSONArray convertListObject2Array(List<BaseModel> list){
        JSONArray array = new JSONArray();
        for (int i=0; i< list.size(); i++){
            array.put(list.get(i).BaseModelJSONObject());

        }

        return array;

    }

    public static List<BaseModel> array2ListObject(String array){
        List<BaseModel> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(array);
            for (int i=0; i<jsonArray.length(); i++){
                BaseModel object = new BaseModel(jsonArray.getJSONObject(i));
                list.add(object);
            }

        } catch (JSONException e) {
            return list;
        }

        return list;
    }

    public static List<BaseModel> array2ListBaseModel(JSONArray array){
        List<BaseModel> list = new ArrayList<>();
        try {
            for (int i=0; i<array.length(); i++){
                list.add(new BaseModel(array.getJSONObject(i)));
            }

        } catch (JSONException e) {
            return list;
        }

        return list;
    }


    public static List<BaseModel> groupDebtByCustomer(final List<BaseModel> list){
        final List<BaseModel> listResult = new ArrayList<>();
//        try {
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
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        return listResult;
    }

    public static List<BaseModel> sortProductGroup(List<BaseModel> list, boolean reverse){
        Collections.sort(list, new Comparator<BaseModel>(){
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString("name").compareToIgnoreCase(obj2.getString("name"));
            }
        });

        if (reverse){
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BaseModel> sortProduct(List<BaseModel> list, boolean reverse){
        Collections.sort(list, new Comparator<BaseModel>(){
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getString("name").compareToIgnoreCase(obj2.getString("name"));
            }
        });

        if (reverse){
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BaseModel> sortbyStringKey(final String key, List<BaseModel> list, boolean reverse){
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

    public static List<BaseModel> sortbyDoubleKey(final String key, List<BaseModel> list, boolean reverse){
        Collections.sort(list, new Comparator<BaseModel>(){
            public int compare(BaseModel obj1, BaseModel obj2) {
                return obj1.getDouble(key).compareTo(obj2.getDouble(key));
            }
        });

        if (reverse){
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BaseModel> remakeBill(List<BaseModel> listbill){
        List<BaseModel> listResult = new ArrayList<>();

        for (BaseModel baseModel : listbill){
            if (Util.isEmpty(baseModel.getString("note")) ){
                baseModel.put("quantityMergeWithReturn", baseModel.getInt("quantity"));
                listResult.add(baseModel);

            }else {
                try {
                    String note = Security.decrypt(baseModel.getString("note"));

                    if (Util.isJSONObject(note)){
                        BaseModel noteObject = new BaseModel(note);
                        double debtreturn = baseModel.getDouble("debt");

                        if (!noteObject.hasKey(Constants.ISBILLRETURN)){

                            if (noteObject.hasKey(Constants.PAYBYTRETURN)){
                                JSONArray array = noteObject.getJSONArray(Constants.PAYBYTRETURN);
                                for (int i=0; i<array.length(); i++){
                                    debtreturn -= array.getJSONObject(i).getDouble("paid");
                                }
                                baseModel.put(Constants.PAYBYTRETURN, array);

                            }

                            if (noteObject.hasKey(Constants.HAVEBILLRETURN)){
                                JSONArray array = noteObject.getJSONArray(Constants.HAVEBILLRETURN);
                                for (int i=0; i<array.length(); i++){
                                    debtreturn -= array.getJSONObject(i).getDouble("total");
                                }
                                baseModel.put(Constants.HAVEBILLRETURN, array);

                            }
                            baseModel.put("debt", debtreturn);
                            listResult.add(baseModel);

                        }

                    }else {
                        baseModel.put("quantityMergeWithReturn", baseModel.getInt("quantity"));
                        listResult.add(baseModel);

                    }
                } catch (JSONException e) {
                    baseModel.put("quantityMergeWithReturn", baseModel.getInt("quantity"));
                    listResult.add(baseModel);

                }
            }

        }

        return listResult;

    }

    public static List<BaseModel> getAllBillDetail(List<BaseModel> listbill){
        final List<BaseModel> listBillDetail = new ArrayList<>();

        try {
            for (int i=0; i<listbill.size(); i++){
                JSONArray arrayBillDetail  = new JSONArray(listbill.get(i).getString("billDetails"));

                for (int ii=0; ii<arrayBillDetail.length(); ii++){
                    BillDetail billDetail = new BillDetail(arrayBillDetail.getJSONObject(ii));
                    listBillDetail.add(billDetail);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listBillDetail;

    }

    public static List<BaseModel> getAllBillHaveDebt(List<BaseModel> listbill){
        List<BaseModel> list = new ArrayList<>();
        for (int i=0; i<listbill.size(); i++) {
            if (listbill.get(i).getDouble("debt") > 0) {
                list.add(listbill.get(i));

            }
        }
        DataUtil.sortbyStringKey("createAt", list, true);
        return list;
    }

    public static List<BaseModel> getListDebtRemain(List<BaseModel> listdebt, BaseModel currentBill){
        List<BaseModel> list = new ArrayList<>();

        for (BaseModel bill: listdebt){

            if (bill.getInt("id") != currentBill.getInt("id")){
                list.add(bill);
            }
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

    public static String defineBDFPercent(List<BaseModel> listDetails){
//        List<BaseModel> listDetails = getAllBillDetail(listbill);

        double total = 0.0;
        double bdf =0.0;

        for (int i=0; i<listDetails.size(); i++){
            if (listDetails.get(i).getBoolean("promotion")
                    && listDetails.get(i).getDouble("unitPrice").equals(listDetails.get(i).getDouble("discount"))){
                bdf += listDetails.get(i).getInt("quantity") * listDetails.get(i).getDouble("purchasePrice");
            }else {
                total += listDetails.get(i).getInt("quantity") * listDetails.get(i).getDouble("purchasePrice");

            }
        }

        double percent = bdf *100 /total;

        return new DecimalFormat("#.##").format(percent);

        //tvBDF.setText(String.format("BDF: %s ",new DecimalFormat("#.##").format(percent)) +"%");

    }

    public static double defineBDFPercentValue(List<BaseModel> listDetails){
//        List<BaseModel> listDetails = getAllBillDetail(listbill);

        double total = 0.0;
        double bdf =0.0;

        for (int i=0; i<listDetails.size(); i++){
            if (listDetails.get(i).getBoolean("promotion")
                    && listDetails.get(i).getDouble("unitPrice").equals(listDetails.get(i).getDouble("discount"))){
                bdf += listDetails.get(i).getInt("quantity") * listDetails.get(i).getDouble("purchasePrice");
            }else {
                total += listDetails.get(i).getInt("quantity") * listDetails.get(i).getDouble("purchasePrice");

            }
        }

        double percent = bdf *100 /total;

        return percent;

        //tvBDF.setText(String.format("BDF: %s ",new DecimalFormat("#.##").format(percent)) +"%");

    }

    public static double defineBDFValue(List<BaseModel> listDetails){
//        List<BaseModel> listDetails = getAllBillDetail(listbill);

//        double total = 0.0;
        double bdf =0.0;

        for (int i=0; i<listDetails.size(); i++){
            if (listDetails.get(i).getBoolean("promotion")
                    && listDetails.get(i).getDouble("unitPrice").equals(listDetails.get(i).getDouble("discount"))){
                bdf += listDetails.get(i).getInt("quantity") * listDetails.get(i).getDouble("purchasePrice");
            }
//            else {
//                total += listDetails.get(i).getInt("quantity") * listDetails.get(i).getDouble("purchasePrice");
//
//            }
        }

//        double percent = bdf *100 /total;

        return bdf;

        //tvBDF.setText(String.format("BDF: %s ",new DecimalFormat("#.##").format(percent)) +"%");

    }

    public static List<BaseModel> getCashByUser(List<BaseModel> listbill, List<BaseModel> listpayment, List<BaseModel> users){
        for (BaseModel user: users){
            double paid = 0.0;
            user.put("paid", 0);

            for (BaseModel bill: listpayment){
                BaseModel us = new BaseModel(bill.getString("user"));
                if (us.getString("name").equals(user.getString("name"))){
                    paid += bill.getDouble("paid");
                    user.put("paid", paid);
                }
            }
        }

        for (BaseModel user: users){
            double paid = 0.0;
            user.put("total", 0);

            for (BaseModel bill: listbill){
                BaseModel us = new BaseModel(bill.getString("user"));
                if (us.getString("name").equals(user.getString("name"))){
                    paid += bill.getDouble("total");
                    user.put("total", paid);
                }
            }
        }

        return users;

    }

//    public static List<BaseModel> getRevenueByUser(List<BaseModel> listbill, List<BaseModel> users){
//        try {
//            for (BaseModel user: users){
//                double paid = 0.0;
//                user.put("total", 0);
//
//                for (BaseModel bill: listbill){
//                    BaseModel us = new BaseModel(bill.getString("user"));
//                    if (us.getString("name").equals(user.getString("name"))){
//                        paid += bill.getDouble("total");
//                        user.put("total", paid);
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return users;
//
//    }

    public static List<List<Object>> updateIncomeByUserToSheet(String startDate, String endDate,List<String> users,  List<BaseModel> listbill, List<BaseModel> listbilldetail, List<BaseModel> listpayment, List<BaseModel> listdebt){
        List<List<Object>> values = new ArrayList<>();

        List<Object> column0 = new ArrayList<>();
        column0.add(String.format("Từ ngày %s đến %s", startDate, endDate));
        column0.add("TÊN NHÂN VIÊN");
        column0.add("DOANH SỐ BÁN HÀNG TRONG THÁNG");
        column0.add("TIỀN MẶT THU VỀ");
        column0.add("CÔNG NỢ HIỆN TẠI");
        column0.add("GIÁ BÁN - GIÁ NHẬP");
        values.add( 0,column0);


        for (int i=1; i<users.size(); i++){

            List<Object> column1 = new ArrayList<>();
            List<Object> column2 = new ArrayList<>();

            column1.add(0,"");
            column2.add(0, "");

            column1.add(users.get(i));
            column2.add(0, "");
//total bill
            double totalbill =0.0;
            for (BaseModel bill: listbill){
                if (bill.getBaseModel("user").getString("displayName").equals(users.get(i))){
                    totalbill += bill.getDouble("total");
                }
            }
            column1.add(totalbill);
            column2.add("--");
//payment
            double pay =0.0;
            for (BaseModel payment: listpayment){
                if (payment.getBaseModel("user").getString("displayName").equals(users.get(i))){
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
            double deb =0.0;

            column1.add("TÊN CỬA HÀNG");
            column2.add("CÒN NỢ");
            for (BaseModel debt : listdebt){
                if (debt.getString("userName").equals(users.get(i))){
                    deb += debt.getDouble("currentDebt");

                    String add = String.format("%s %s\n(%s - %s)",
                            Constants.getShopTitle(debt.getString("shopType") , null).toUpperCase(),
                            debt.getString("signBoard").toUpperCase(),
                            debt.getString("street"),
                            debt.getString("district"));
                    column1.add(add);
                    column2.add(debt.getDouble("currentDebt"));

                }
            }
            column1.add(4,deb);
            column2.add(4,"--");



            values.add(column1);
            values.add(column2);





        }





        return values;
    }

    public static List<BaseModel> converArray2List(JSONArray array){
        List<BaseModel> listResult = new ArrayList<>();
        try {
            for (int i=0; i<array.length(); i++){
                BaseModel billDetail = new BaseModel(array.getJSONObject(i));

                listResult.add(billDetail);
            }
        } catch (JSONException e) {
            return listResult;
        }
        return listResult;
    }

    public static double sumMoneyFromList(List<BaseModel> list, String key){
        double result = 0.0;
        for (BaseModel item: list){
            result += item.getDouble(key);
        }

        return result;
    }

    public static boolean checkDuplicate(List<BaseModel> list, String key, BaseModel object){
        boolean check = false;
        for (int i=0; i<list.size(); i++){
            if (list.get(i).getString(key).equals( object.getString(key))){
                check = true;
                break;
            }
        }

        return check;
    }


    private List<List<Object>> getListValueExportToSheet(List<Object> listSheetID, List<Bill> listbill){
        List<List<Object>> values = new ArrayList<>();
        try {
            for (int i=0; i<listbill.size(); i++){
                Bill bill = listbill.get(i);
                if (!listSheetID.toString().contains(bill.getString("id"))){
                    final Customer customer = new Customer(bill.getJsonObject("customer"));
                    List<Object> data = new ArrayList<>();
                    data.add(bill.getString("id"));
                    data.add(Util.DateString(bill.getLong("createAt")));
                    data.add(bill.getJsonObject("user").getString("displayName"));
                    data.add(Constants.getShopTitle(customer.getString("shopType") , null) + " " + customer.getString("signBoard"));
                    data.add(customer.getString("phone"));
                    data.add(bill.getDouble("total"));
                    data.add(bill.getDouble("paid"));
                    data.add(bill.getDouble("debt"));
                    data.add(bill.getString("note"));

                    values.add(data);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return values;
    }

    public static BaseModel createBillParam(long starDay, long lastDay){
        BaseModel billparam = new BaseModel();
        billparam.put("url", Api_link.BILLS
                + String.format(Api_link.DEFAULT_RANGE, 1,5000)
                + String.format(Api_link.BILL_RANGE_PARAM, starDay, lastDay));
        billparam.put("method", "GET");
        billparam.put("isjson", null );
        billparam.put("param", null );

        return billparam;

    }


    public static BaseModel createPaymentParam(long starDay, long lastDay){
        BaseModel payparam = new BaseModel();
        payparam.put("url", Api_link.BILLS_HAVE_PAYMENT
                + String.format(Api_link.DEFAULT_RANGE, 1,1500)
                + String.format(Api_link.BILL_HAVE_PAYMENT_RANGE_PARAM, starDay, lastDay));
        payparam.put("method", "GET");
        payparam.put("isjson", null );
        payparam.put("param", null );

        return payparam;

    }

    public static BaseModel createDebtParam(){
        BaseModel payparam = new BaseModel();
        payparam.put("url", Api_link.CUSTOMERS+ String.format(Api_link.DEFAULT_RANGE, 1,500));
        payparam.put("method", "POST");
        payparam.put("isjson", false );
        payparam.put("param", String.format(Api_link.CUSTOMER_DEBT_PARAM, 0) );

        return payparam;

    }

    public static BaseModel createNewCustomerParam(String param){
        BaseModel paramCustomer = new BaseModel();
        paramCustomer.put("url", Api_link.CUSTOMER_NEW );
        paramCustomer.put("method", "POST");
        paramCustomer.put("isjson", false );
        paramCustomer.put("param", param );

        return paramCustomer;

    }

    public static BaseModel getListCustomerParam(String param, int countinPage){
        BaseModel paramCustomer = new BaseModel();
        paramCustomer.put("url", Api_link.CUSTOMERS+ String.format(Api_link.DEFAULT_RANGE, 1,countinPage) );
        paramCustomer.put("method", "POST");
        paramCustomer.put("isjson", false );
        paramCustomer.put("param", param );

        return paramCustomer;

    }

    public static BaseModel postCheckinParam(String param){
        BaseModel paramCheckin = new BaseModel();
        paramCheckin.put("url", Api_link.CHECKIN_NEW );
        paramCheckin.put("method", "POST");
        paramCheckin.put("isjson", false );
        paramCheckin.put("param", param );

        return paramCheckin;

    }

    public static BaseModel postBillParam(String param){
        BaseModel paramCheckin = new BaseModel();
        paramCheckin.put("url", Api_link.BILL_NEW);
        paramCheckin.put("method", "POST");
        paramCheckin.put("isjson", true );
        paramCheckin.put("param", param );

        return paramCheckin;

    }

    public static BaseModel postPayParam(String param){
        BaseModel paramCheckin = new BaseModel();
        paramCheckin.put("url", Api_link.PAY_NEW );
        paramCheckin.put("method", "POST");
        paramCheckin.put("isjson", false );
        paramCheckin.put("param", param );

        return paramCheckin;

    }

    public static BaseModel postLoginParam(String param){
        BaseModel paramCheckin = new BaseModel();
        paramCheckin.put("url", Api_link.LOGIN);
        paramCheckin.put("method", "POST");
        paramCheckin.put("isjson", false );
        paramCheckin.put("param", param );

        return paramCheckin;

    }

    public static BaseModel createNewProductParam(String param){
        BaseModel paramCustomer = new BaseModel();
        paramCustomer.put("url", Api_link.PRODUCT_NEW );
        paramCustomer.put("method", "POST");
        paramCustomer.put("isjson", false );
        paramCustomer.put("param", param );

        return paramCustomer;

    }

    public static BaseModel createNewProductGroupParam(String param){
        BaseModel paramCustomer = new BaseModel();
        paramCustomer.put("url", Api_link.PRODUCT_GROUP_NEW );
        paramCustomer.put("method", "POST");
        paramCustomer.put("isjson", false );
        paramCustomer.put("param", param );

        return paramCustomer;

    }

}
