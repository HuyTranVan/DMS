package wolve.dms.utils;

import android.util.Log;

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


            String currentNote = Security.decrypt(currentBill.getString("note"));

            JSONArray arrayReturnNote =new JSONArray();
            JSONArray arrayPayNote =new JSONArray();

            if (Util.isJSONObject(currentNote)){
                objectNote = new JSONObject(currentNote);
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

            //arrayReturnNote.put(objPay);
            objectNote.put(Constants.HAVEBILLRETURN, arrayReturnNote);
            objectNote.put(Constants.PAYBYTRETURN, arrayPayNote);


            params.put("note", Security.encrypt(objectNote.toString()));



        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return params.toString();

    }

    public static String updateBillDelivered(int customerId, BaseModel currentBill, Object user){
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

            params.put("note", createBillNote(currentBill.getString("note"), Constants.TEMPBILL, false));
            params.put("note", createBillNote(params.getString("note"), Constants.DELIVER_BY, user));


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
                objectNote = new JSONObject(currentNote);
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

    public static String createBillNote(String currentnote, String key, Object value){
        JSONObject jsonNote = new JSONObject();

        try {
            if (currentnote.equals("")){
                jsonNote = new JSONObject();
                jsonNote.put(key, value);

            }else {
                String note = Security.decrypt(currentnote);
                if (Util.isJSONObject(note)){
                    jsonNote = new JSONObject(note);
                    jsonNote.put(key, value);

                }else {
                    jsonNote = new JSONObject();
                    jsonNote.put(key, value);

                }


            }

        } catch (JSONException e) {

        }

        return Security.encrypt(jsonNote.toString());

    }


    public static List<String> createListPaymentParam(int customerId,List<BaseModel> list, boolean payByReturn){
        List<String> results = new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            String s = String.format(Api_link.PAY_PARAM,
                    customerId,
                    String.valueOf(Math.round(list.get(i).getDouble("paid"))),
                    list.get(i).getInt("billId"),
                    User.getId(),
                    String.valueOf(Math.round(list.get(i).getDouble("billTotal"))),
                    payByReturn?1:0);
//                        note.equals("")?"" :"&note="+note )

            results.add(s);

        }

        return results;
    }

    public static String createPostPaymentParam(int customerId, double paid, int billId, double billTotal, boolean payByReturn){
        String s = String.format(Api_link.PAY_PARAM,
                customerId,
                String.valueOf(Math.round(paid)),
                billId,
                User.getId(),
                String.valueOf(Math.round(billTotal)),
                payByReturn?1:0);


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



            }

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
        List<BaseModel> bills = new ArrayList<>();
        List<BaseModel> listBillByUser = new ArrayList<>();

        for (BaseModel baseModel : listbill){
            if (Util.isEmpty(baseModel.getString("note")) ){
                baseModel.put(Constants.TEMPBILL,  false);
                bills.add(baseModel);

            }else {
                String note = Security.decrypt(baseModel.getString("note"));

                if (Util.isJSONObject(note)){
                    BaseModel noteObject = new BaseModel(note);
                    double debtreturn = baseModel.getDouble("debt");

                    if (!noteObject.hasKey(Constants.ISBILLRETURN)){
                        if (noteObject.hasKey(Constants.PAYBYTRETURN)){
                            List<BaseModel> array = DataUtil.array2ListObject(noteObject.getString(Constants.PAYBYTRETURN));

                            for (int i=0; i<array.size(); i++){
                                debtreturn -= array.get(i).getDouble("paid");
                            }
                            baseModel.putList(Constants.PAYBYTRETURN, array);

                        }

                        if (noteObject.hasKey(Constants.HAVEBILLRETURN)){
                            List<BaseModel> array = DataUtil.array2ListObject(noteObject.getString(Constants.HAVEBILLRETURN));
                            for (int i=0; i<array.size(); i++){
                                debtreturn -= array.get(i).getDouble("total");
                            }
                            baseModel.putList(Constants.HAVEBILLRETURN, array);

                        }
                        baseModel.put("debt", debtreturn);

                        baseModel.put(Constants.TEMPBILL, noteObject.hasKey(Constants.TEMPBILL) && noteObject.getBoolean(Constants.TEMPBILL)? true : false);

                        if (noteObject.hasKey(Constants.DELIVER_BY)){
                            baseModel.put(Constants.DELIVER_BY, noteObject.getJsonObject(Constants.DELIVER_BY));
                        }

                        bills.add(baseModel);


                    }

                }else {
                    baseModel.put(Constants.TEMPBILL,  false);
                    bills.add(baseModel);

                }

            }

        }
        for (BaseModel model: bills){
            if (User.getRole().equals(Constants.ROLE_ADMIN)) {
                listBillByUser.add(model);

            } else {
                if (model.getBoolean(Constants.TEMPBILL)){
                    listBillByUser.add(model);
                }else if (User.getId() == model.getBaseModel("user").getInt("id")) {
                    listBillByUser.add(model);
                }
            }
        }

        return listBillByUser;

    }

    public static BaseModel rebuiltCustomer(BaseModel customer){
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
        customerResult.put("currentDebt", customer.getDouble("currentDebt"));
        customerResult.put("status", customer.getJsonObject("status"));
        customerResult.put("distributor", customer.getJsonObject("distributor"));

        List<BaseModel> listOriginalBill= new ArrayList<>(DataUtil.array2ListObject(customer.getString("bills")));

        if (listOriginalBill.size() >0) {
            BaseModel tempBill = DataUtil.getTempBill(listOriginalBill);
            if (tempBill != null) {
                customerResult.putBaseModel(Constants.TEMPBILL, tempBill);
            }

        }
        List<BaseModel> listBill= new ArrayList<>(remakeBill(listOriginalBill));
        customerResult.putList(Constants.BILLS, listBill);
        customerResult.putList(Constants.DEBTS, getAllBillHaveDebt(listBill));

        return customerResult;
    }

    public static List<BaseModel> getAllBillDetail(List<BaseModel> listbill){
        final List<BaseModel> listResult = new ArrayList<>();

        for (int i=0; i<listbill.size(); i++){
            List<BaseModel> billDetails  = new ArrayList<>(array2ListObject(listbill.get(i).getString("billDetails")));
            listResult.addAll(billDetails);

        }


        return listResult;

    }

    public static List<BaseModel> getAllBillHaveDebt(List<BaseModel> listbill){
        List<BaseModel> list = new ArrayList<>();
        for (int i=0; i<listbill.size(); i++) {
            if (listbill.get(i).getDouble("debt") > 0 && !listbill.get(i).getBoolean(Constants.TEMPBILL)) {
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

    public static BaseModel getTempBill(List<BaseModel> listbill){
        BaseModel result = null;
        for (BaseModel bill: listbill){
            if (!Util.isEmpty(bill.getString("note")) ){
                String note = Security.decrypt(bill.getString("note"));
                if (Util.isJSONObject(note)){
                    BaseModel noteObject = new BaseModel(note);
                    if (noteObject.hasKey(Constants.TEMPBILL) && noteObject.getBoolean(Constants.TEMPBILL)){
                        result = bill;
                        break;

                    }
                }

            }
        }
        return result;
    }

    public static String defineBDFPercent(List<BaseModel> listDetails){
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

    public static List<List<Object>> updateIncomeByUserToSheet(String startDate, String endDate,List<BaseModel> users,  List<BaseModel> listbill, List<BaseModel> listbilldetail, List<BaseModel> listpayment, List<BaseModel> listdebt){
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
                if (bill.getBaseModel("user").getString("displayName").equals(users.get(i).getString("displayName"))){
                    totalbill += bill.getDouble("total");
                }
            }
            column1.add(totalbill);
            column2.add("--");
//payment
            double pay =0.0;
            for (BaseModel payment: listpayment){
                if (payment.getBaseModel("user").getString("displayName").equals(users.get(i).getString("displayName"))){
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
                if (debt.getString("userName").equals(users.get(i).getString("displayName"))){
                    deb += debt.getDouble("currentDebt");

                    String add = String.format("%s %s\n(%s - %s)",
                            Constants.getShopName(debt.getString("shopType")).toUpperCase(),
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

    public static boolean checkDuplicateDouble(List<Double> list, double value){
        boolean check = false;
        for (int i=0; i<list.size(); i++){
            if (list.get(i).doubleValue() == value){
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
                    data.add(Constants.getShopName(customer.getString("shopType")) + " " + customer.getString("signBoard"));
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

    public static List<BaseModel> convertPaymentList(String user, List<BaseModel> list, long starDay, long lastDay){
        List<BaseModel>  listInitialPayment = new ArrayList<>();
        for (int i=0; i<list.size(); i++){
            List<BaseModel> payments = DataUtil.array2ListBaseModel(list.get(i).getJSONArray("payments"));

            if (payments.size() >0){
                for (int a=0; a<payments.size(); a++){
                    if (payments.get(a).getLong("createAt") - starDay >= 0 &&
                            payments.get(a).getLong("createAt") - lastDay <= 0){

                        if (!DataUtil.checkDuplicate(listInitialPayment,"id", payments.get(a))){
                            BaseModel newCash = new BaseModel();
                            newCash.put("id", payments.get(a).getInt("id"));
                            newCash.put("createAt", payments.get(a).getLong("createAt"));
                            newCash.put("updateAt", payments.get(a).getLong("updateAt"));
                            newCash.put("note", payments.get(a).getString("note"));
                            newCash.put("paid", payments.get(a).getDouble("paid"));
                            newCash.put("user", list.get(i).getJsonObject("user"));
                            newCash.put("customer", list.get(i).getJsonObject("customer"));
                            newCash.put("billTotal", list.get(i).getDouble("total"));
                            newCash.put("billProfit", Util.getProfitByPayment(list.get(i), payments.get(a).getDouble("paid")));

                            //updateListUser(new BaseModel(list.get(i).getJsonObject("user")));
                            if (user.equals(Constants.ALL_FILTER)){
                                listInitialPayment.add(newCash);

                            }else {
                                if (payments.get(a).getBaseModel("user").getString("displayName").equals(user)){
                                    listInitialPayment.add(newCash);
                                }

                            }

                            //listInitialPayment.add(newCash);

                        }

                    }
                }
            }

        }

        return listInitialPayment;

    }

    public static List<BaseModel> groupCustomerPayment(List<BaseModel> list){
        List<BaseModel> listResult = new ArrayList<>();
        for ( int i=0; i<list.size(); i++ ){
            boolean check = false;
            for (int a=0; a<listResult.size(); a++){
                if (Util.DateString(listResult.get(a).getLong("createAt")).equals(Util.DateString(list.get(i).getLong("createAt")))
                        && listResult.get(a).getString("customer").equals(list.get(i).getString("customer"))){

                    check = true;
                    break;
                }
            }



            if (!check){
                BaseModel row = new BaseModel();
                row.put("createAt", list.get(i).getLong("createAt"));
                row.put("user", list.get(i).getJsonObject("user"));
                row.put("customer", list.get(i).getJsonObject("customer"));

                double paid = list.get(i).getDouble("paid");
                for (int ii= i+1; ii<list.size(); ii++){
                    if (Util.DateString(row.getLong("createAt")).equals(Util.DateString(list.get(ii).getLong("createAt")))
                            && row.getString("customer").equals(list.get(ii).getString("customer"))){

                        paid += list.get(ii).getDouble("paid");

                    }
                }
                row.put("paid", paid);
                row.put("billTotal", list.get(i).getDouble("billTotal"));
                row.put("billProfit", list.get(i).getDouble("billProfit"));


                listResult.add(row);

            }
        }


        return listResult;
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

    public static BaseModel createNewStatusParam(String param){
        BaseModel paramCustomer = new BaseModel();
        paramCustomer.put("url", Api_link.STATUS_NEW );
        paramCustomer.put("method", "POST");
        paramCustomer.put("isjson", false );
        paramCustomer.put("param", param );

        return paramCustomer;

    }

    public static BaseModel createNewDistributorParam(String param){
        BaseModel paramDistributor = new BaseModel();
        paramDistributor.put("url", Api_link.DISTRIBUTOR_NEW );
        paramDistributor.put("method", "POST");
        paramDistributor.put("isjson", false );
        paramDistributor.put("param", param );

        return paramDistributor;

    }

}
