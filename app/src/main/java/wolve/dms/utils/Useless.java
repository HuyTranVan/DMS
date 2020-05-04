package wolve.dms.utils;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mukesh.DrawingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.ProductReturnAdapter;
import wolve.dms.callback.CallbackBaseModel;
import wolve.dms.callback.CallbackDouble;
import wolve.dms.callback.CallbackPayBill;
import wolve.dms.customviews.CInputForm;
import wolve.dms.libraries.Security;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Bill;
import wolve.dms.models.Customer;
import wolve.dms.models.Product;
import wolve.dms.models.User;

public class Useless {
    public static String formatString(String text){

        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
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
                    data.add(Constants.shopName[customer.getInt("shopType")] + " " + customer.getString("signBoard"));
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

    public static List<BaseModel> groupDebtByCustomer(final List<BaseModel> list){
        final List<BaseModel> listResult = new ArrayList<>();
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

    public static void showDialogEditAddress(BaseModel address, CallbackBaseModel listener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_edit_address);

        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = (TextView) dialogResult.findViewById(R.id.dialog_editadd_title);
        CInputForm edAddress = (CInputForm) dialogResult.findViewById(R.id.dialog_editadd_address);
        CInputForm edStreet = (CInputForm) dialogResult.findViewById(R.id.dialog_editadd_street);
        CInputForm edDistrict = (CInputForm) dialogResult.findViewById(R.id.dialog_editadd_district);
        CInputForm edCity = (CInputForm) dialogResult.findViewById(R.id.dialog_editadd_city);

        tvTitle.setText("SỬA ĐỊA CHỈ");
        edAddress.setText(address.getString("address"));
        edStreet.setText(address.getString("street"));
        edDistrict.setText(address.getString("district"));
        edCity.setText(address.getString("province"));


        btnCancel.setText("HỦY");
        btnSubmit.setText("LƯU");

        edAddress.setSelection();
        Util.showKeyboardDelay(edAddress);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseModel objectAdress = new BaseModel();

                objectAdress.put("address", edAddress.getText().toString());
                objectAdress.put("street", edStreet.getText().toString());
                objectAdress.put("district",edDistrict.getText().toString());
                objectAdress.put("province", edCity.getText().toString());

                listener.onResponse(objectAdress);
                dialogResult.dismiss();

            }
        });


    }

    public static void showDialogBillImage(Customer customer, String total, List<Product> listProduct, final CallbackPayBill mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_bill);
        dialogResult.setCancelable(true);
        TextView tvShopName = (TextView) dialogResult.findViewById(R.id.dialog_bill_shopname);
        TextView tvCustomerName = (TextView) dialogResult.findViewById(R.id.dialog_bill_customer);
        TextView tvPhone = (TextView) dialogResult.findViewById(R.id.dialog_bill_phone);
        TextView tvAddress = (TextView) dialogResult.findViewById(R.id.dialog_bill_address);
        TextView tvDate = (TextView) dialogResult.findViewById(R.id.dialog_bill_time);
        TextView tvSalesman = (TextView) dialogResult.findViewById(R.id.dialog_bill_sale);
        TextView tvTotal = (TextView) dialogResult.findViewById(R.id.dialog_bill_total);
        RecyclerView rvBill = (RecyclerView) dialogResult.findViewById(R.id.dialog_bill_rvbill);


        tvShopName.setText(Constants.shopName[customer.getInt("shopType")]+ " "+ customer.getString("signBoard"));
        tvCustomerName.setText(customer.getString("name") == null? "" : customer.getString("name"));
        tvPhone.setText(customer.getString("phone") == null? "" : customer.getString("phone"));
        tvAddress.setText((customer.getString("address") == null? "" : customer.getString("address")) +" " +customer.getString("street") + " "+ customer.getString("district"));
        tvDate.setText(Util.CurrentMonthYearHour());
        tvSalesman.setText(User.getFullName());
        tvTotal.setText(total);


    }

    public static Dialog showDialogSignature(){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_signature);
        final Button btnSubmit = (Button) dialogResult.findViewById(R.id.btn_submit);
        final Button btnCancel = (Button) dialogResult.findViewById(R.id.btn_cancel);
        final DrawingView drawingView = (DrawingView) dialogResult.findViewById(R.id.scratch_pad);
        drawingView.setPenSize(10);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogResult.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);

                dialogResult.dismiss();

            }
        });

        return dialogResult;
    }

    public static void showDialogReturnProduct(List<BaseModel> listDebt, final BaseModel currentBill, final ProductReturnAdapter.CallbackReturn mListener){
        final Dialog dialogResult = CustomCenterDialog.showCustomDialog(R.layout.view_dialog_return_product);

        final Button btnCancel = dialogResult.findViewById(R.id.btn_cancel);
        final Button btnSubmit = dialogResult.findViewById(R.id.btn_submit);
        TextView tvTitle = dialogResult.findViewById(R.id.dialog_return_product_title);
        RecyclerView rvProduct = dialogResult.findViewById(R.id.dialog_return_product_rv);
        final TextView tvSum = dialogResult.findViewById(R.id.dialog_return_product_sum);
        final TextView tvDebt = dialogResult.findViewById(R.id.dialog_return_product_debt);
        TextView tvNote = dialogResult.findViewById(R.id.dialog_return_product_note);

        double totalDebt = Util.getTotalDebt(listDebt);
        btnCancel.setText("HỦY");
        btnSubmit.setText("XÁC NHẬN");
        tvTitle.setText(String.format("TRẢ HÀNG HÓA ĐƠN %s", Util.DateString(currentBill.getLong("createAt"))));
        tvSum.setText("TẠM TÍNH TRẢ HÀNG:     0");
        tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(totalDebt)));


        final ProductReturnAdapter adapter = new ProductReturnAdapter(currentBill, new CallbackDouble() {
            @Override
            public void Result(Double d) {
                tvSum.setText(String.format("TẠM TÍNH TRẢ HÀNG:     %s",Util.FormatMoney(d)));
                tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(totalDebt - d)));

                if (d < 0.0){
                    dialogResult.dismiss();
                    Util.showToast("Hóa đơn đã trả hết hàng!");
                    tvNote.setVisibility(View.GONE);
                    tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(totalDebt - d)));

                }else if (d <= totalDebt){
                    btnSubmit.setText("XÁC NHẬN");
                    tvNote.setVisibility(View.GONE);
                    tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(totalDebt - d)));

                }else {
                    btnSubmit.setText("TIẾP TỤC");
                    tvNote.setVisibility(View.VISIBLE);
                    tvNote.setText(String.format("Tiền dư trả lại khách:     %s", Util.FormatMoney(d- totalDebt)));
                    tvDebt.setText(String.format("Tạm tính các hóa đơn còn nợ:     %s", Util.FormatMoney(0.0)));

                }

            }
        });
        Util.createLinearRV(rvProduct, adapter);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResult.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                List<BaseModel> listProductSelected = new ArrayList<>(adapter.getListSelected());

                if (listProductSelected.size() >0){
                    if (adapter.sumReturnBill() > currentBill.getDouble("debt")){
                        dialogResult.dismiss();
                        mListener.returnMoreThanDebt(adapter.getListSelected(), adapter.sumReturnBill(), currentBill);

                    }else {
                        dialogResult.dismiss();
                        mListener.returnEqualLessDebt(adapter.getListSelected(), adapter.sumReturnBill(), currentBill);

                    }

                }else {
                    Util.showToast("Vui lòng chọn số lượng");

                }


            }
        });


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

}
