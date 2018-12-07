package wolve.dms.apiconnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackJSONArray;
import wolve.dms.callback.CallbackJSONObject;
import wolve.dms.callback.CallbackList;
import wolve.dms.libraries.connectapi.CustomDeleteMethod;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomPostListMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.libraries.printerdriver.PrinterCommands;
import wolve.dms.libraries.printerdriver.UtilPrinter;
import wolve.dms.models.Bill;
import wolve.dms.models.Customer;
import wolve.dms.models.Distributor;
import wolve.dms.models.Product;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/19/17.
 */

public class CustomerConnect {

    public static void CreateCustomer(String params, final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CUSTOMER_NEW ;

        new CustomPostMethod(url,params, false,new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);

                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONObject("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void ListCustomer(String param, final CallbackJSONArray listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CUSTOMERS+ String.format(Api_link.DEFAULT_RANGE, 1,500);

        new CustomPostMethod(url,param, false, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONArray("data"));

                    } else {
                        listener.onError("Unknow error");

                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void ListCustomerLocation(String lat, String lng, final CallbackJSONArray listener, final Boolean stopLoading){
        //Util.getInstance().showLoading();

        String url = Api_link.CUSTOMERS_NEAREST
                + String.format(Api_link.DEFAULT_RANGE,1,20)
                + String.format(Api_link.CUSTOMER_NEAREST_PARAM, lat, lng,1, 20);


        new CustomGetMethod(url, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONArray("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void ListCustomerSearch(String param, final CallbackJSONArray listener, final Boolean stopLoading){
        String url = Api_link.CUSTOMERS+ String.format(Api_link.DEFAULT_RANGE, 1,8);

        new CustomPostMethod(url,param, false, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONArray("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void DeleteCustomer(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CUSTOMER_DELETE + params;

        new CustomDeleteMethod(url, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(null);

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void GetCustomerDetail(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CUSTOMER_GETDETAIL + params;

        new CustomGetMethod(url, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONObject("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void PostCheckin(String params, final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.CHECKIN_NEW ;

        new CustomPostMethod(url,params, false,new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONObject("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void PostBill(String params, final CallbackJSONObject listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();

        String url = Api_link.BILL_NEW;

        new CustomPostMethod(url, params,true, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);

                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONObject("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(true);
            }
        }).execute();
    }

    public static void PostListPay(List<String> listParams, final CallbackList listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();

        String url = Api_link.PAY_NEW;

        new CustomPostListMethod(url, listParams, false, new CallbackList() {
            @Override
            public void onResponse(List result) {
                Util.getInstance().stopLoading(stopLoading);
                listener.onResponse(result);
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void PostPay(String param, final CallbackJSONObject listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();

        String url = Api_link.PAY_NEW;

        new CustomPostMethod(url, param, false, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                listener.onResponse(result);
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void DeleteBill(String params,final CallbackJSONObject listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.BILL_DELETE + params;

        new CustomDeleteMethod(url, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(null);

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void ListBill(String param, final CallbackJSONArray listener, final Boolean stopLoading){
        if (stopLoading){
            Util.getInstance().showLoading();
        }

        String url = Api_link.BILLS+ String.format(Api_link.DEFAULT_RANGE, 1,1500) + param;

        new CustomGetMethod(url, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONArray("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void ListBillHavePayment(String param, final CallbackJSONArray listener, final Boolean stopLoading){
        if (stopLoading){
            Util.getInstance().showLoading();
        }

        String url = Api_link.BILLS_HAVE_PAYMENT+ String.format(Api_link.DEFAULT_RANGE, 1,1500) + param;

        new CustomGetMethod(url, new Callback() {
            @Override
            public void onResponse(JSONObject result) {
                Util.getInstance().stopLoading(stopLoading);
                try {
                    if (result.getInt("status") == 200) {
                        listener.onResponse(result.getJSONArray("data"));

                    } else {
                        listener.onError("Unknow error");
                    }
                } catch (JSONException e) {
                    listener.onError(e.toString());
                }
            }


            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(stopLoading);
            }
        }).execute();
    }

    public static void printBill(OutputStream outputStream, Customer currentCustomer, List<Product> listProduct, List<Bill> listBills , Double total, Double paid, CallbackBoolean mListener){
        String shortLine ="---------------------";
        String longLine = "--------------------------------";
//        Double total  = Util.getTotalMoney(listBills);

        try {
            if (CustomSQL.getString("logo").equals("")){
//                UtilPrinter.printDrawablePhoto(outputStream, Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.ic_logo_print));
            }else {
                UtilPrinter.printPhoto(outputStream,CustomSQL.getString("logo"));
            }

            UtilPrinter.printCustomText(outputStream,"CTY TNHH XNK TRAN VU ANH",1,1);
            UtilPrinter.printCustomText(outputStream, "1 duong 57,P.Binh Trung Dong,Q 2", 1,1);
            UtilPrinter.printCustomText(outputStream,"ĐT:0931.07.22.23",4,1);
            UtilPrinter.printCustomText(outputStream,"www.wolver.vn",4,1);
            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomText(outputStream, "HÓA ĐƠN BÁN HÀNG",3,1);
            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomText(outputStream,"CH    : " + Constants.getShopInfo(currentCustomer.getString("shopType") , null) + " "+ currentCustomer.getString("signBoard") , 1,0);
            UtilPrinter.printCustomText(outputStream,"KH    : " + currentCustomer.getString("name"), 1,0);

            String phone = currentCustomer.getString("phone").equals("")? "--" : Util.PhoneFormat(currentCustomer.getString("phone"));
            UtilPrinter.printCustomText(outputStream,"SDT   : " + phone, 1,0);
            String add = String.format("%s, %s", currentCustomer.getString("street"), currentCustomer.getString("district"));
            String address = add.length()>23 ? add.substring(0,23) : add;
            UtilPrinter.printCustomText(outputStream,"D.CHI : " + address , 1,0);
            UtilPrinter.printCustomText(outputStream,"NGAY  : " + Util.CurrentMonthYearHour(),1,0);
            UtilPrinter.printCustomText(outputStream,"N.VIEN: " + User.getFullName(), 1,0);
            UtilPrinter.printCustomText(outputStream,shortLine,3,1);

            //List<Product> listProduct = getListProduct();
            for (int i=0; i<listProduct.size(); i++){
                UtilPrinter.printCustomText(outputStream,String.format("%d.%s (%s)" ,i+1, listProduct.get(i).getString("name"), Util.FormatMoney(listProduct.get(i).getDouble("unitPrice"))) , 1,0);

                if (listProduct.get(i).getBoolean("isPromotion")){
                    UtilPrinter.printCustom2Text(outputStream ,Constants.PRINTER_57, "(Khuyen mai)" , Util.FormatMoney(listProduct.get(i).getDouble("totalMoney")) , 1,0);

                }else {
                    String discount = listProduct.get(i).getDouble("discount") == 0? "" : " -" + Util.FormatMoney(listProduct.get(i).getDouble("discount"));
                    UtilPrinter.printCustom2Text(outputStream ,Constants.PRINTER_57,
                            " "+ Util.FormatMoney(listProduct.get(i).getDouble("quantity")) + "x" + Util.FormatMoney(listProduct.get(i).getDouble("unitPrice")) + discount ,
                            Util.FormatMoney(listProduct.get(i).getDouble("totalMoney")) , 1,0);
                }

                if (i != listProduct.size() -1){
                    UtilPrinter.printCustomText(outputStream,longLine,1,1);
                }
            }
            UtilPrinter.printCustomText(outputStream,shortLine,3,1);
            UtilPrinter.printCustom2Text(outputStream,Constants.PRINTER_57,"TONG:", Util.FormatMoney(total),4,2);

            Double sumDebt =0.0;
            for (int j=0; j<listBills.size(); j++){

                if (listBills.get(j).getDouble("debt") > 0){
                    sumDebt += listBills.get(j).getDouble("debt");
                    UtilPrinter.printCustom2Text(outputStream,Constants.PRINTER_57,"NO "+ Util.DateMonthString(listBills.get(j).getLong("createAt")), Util.FormatMoney(listBills.get(j).getDouble("debt")),4,2);

                }
            }

            if (listBills.size() >0){
                UtilPrinter.printCustomText(outputStream,longLine,1,1);
            }

            UtilPrinter.printCustom2Text(outputStream,Constants.PRINTER_57,"TRA:", Util.FormatMoney(paid),4,2);
            UtilPrinter.printCustomText(outputStream,longLine,1,1);
            UtilPrinter.printCustom2Text(outputStream,Constants.PRINTER_57,"CON LAI:", Util.FormatMoney(total + sumDebt - paid),4,2);
            UtilPrinter.printCustomText(outputStream,shortLine,3,1);
            UtilPrinter.printCustomText(outputStream, String.format("Đặt hàng: %s", Util.PhoneFormat(User.getPhone())), 1,1);
            UtilPrinter.printCustomText(outputStream, "Tran trong cam on quy khach hang", 1,1);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.flush();

            mListener.onRespone(true);
            //Util.getInstance().dismissDialog(true);



        } catch (IOException e) {
            mListener.onRespone(false);
        }
    }

    public static void printBillCustom(String printerSize,OutputStream outputStream, Customer currentCustomer, List<JSONObject> listProduct, List<JSONObject> listDebts , Double total, Double paid, CallbackBoolean mListener){
        String boltLine="";
        String line="";

        if (printerSize.equals(Constants.PRINTER_57)){
            boltLine = "================================";
            line =     "--------------------------------";
        }else if (printerSize.equals(Constants.PRINTER_80)){
            boltLine =  "================================";
            line =      "------------------------------------------------";

        }


        try {

            if (CustomSQL.getString("logo").equals("")){
                outputStream.write(PrinterCommands.FEED_LINE);
                outputStream.write(PrinterCommands.FEED_LINE);
//                UtilPrinter.printDrawablePhoto(outputStream, Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.ic_logo_print));
            }else {
                UtilPrinter.printPhoto(outputStream,CustomSQL.getString("logo"));
            }

            UtilPrinter.printCustomTextNew(outputStream,"CTY TNHH XNK TRAN VU ANH",2,1);
            UtilPrinter.printCustomTextNew(outputStream, "863, DT743A, P.Binh Thang,TX Di An, Binh Duong", 2,1);
            UtilPrinter.printCustomTextNew(outputStream,"ĐT:0931.07.22.23",11,1);
            UtilPrinter.printCustomTextNew(outputStream,"www.wolver.vn",11,1);
            UtilPrinter.printCustomTextNew(outputStream,"*********************************",1,1);
            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomTextNew(outputStream, "HÓA ĐƠN BÁN HÀNG",33,1);
            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomTextNew(outputStream,"CH     : " + Constants.getShopInfo(currentCustomer.getString("shopType") , null) + " "+ currentCustomer.getString("signBoard") , 2,0);
            UtilPrinter.printCustomTextNew(outputStream,"KH     : " + currentCustomer.getString("name"), 2,0);

            String phone = currentCustomer.getString("phone").equals("")? "--" : Util.PhoneFormat(currentCustomer.getString("phone"));
            UtilPrinter.printCustomTextNew(outputStream,"SDT    : " + phone, 2,0);
            String add = String.format("%s, %s", currentCustomer.getString("street"), currentCustomer.getString("district"));
            int len = printerSize.equals(Constants.PRINTER_57)?23:40;
            String address = add.length()>len ? add.substring(0,len) : add;
            UtilPrinter.printCustomTextNew(outputStream,"D.CHI  : " + address , 2,0);
            UtilPrinter.printCustomTextNew(outputStream,"NGAY   : " + Util.CurrentMonthYearHour(),2,0);
            UtilPrinter.printCustomTextNew(outputStream,"N.VIEN : " + User.getFullName(), 2,0);
            UtilPrinter.printCustomTextNew(outputStream,boltLine,11,1);

            Double totalCurrentMoney = 0.0;
            for (int i=0; i<listProduct.size(); i++){
                UtilPrinter.printCustomTextNew(outputStream,
                        String.format("%d.%s" ,i+1, listProduct.get(i).getString("name")) ,
                        22,0);

                String discount = listProduct.get(i).getDouble("discount") == 0? "" : "-" + Util.FormatMoney(listProduct.get(i).getDouble("discount"));
                UtilPrinter.printCustom2TextNew(outputStream ,
                        printerSize,
                        String.format(" %sx(%s)",Util.FormatMoney(listProduct.get(i).getDouble("quantity")) ,Util.FormatMoney(listProduct.get(i).getDouble("unitPrice")) + discount) ,
                        Util.FormatMoney(listProduct.get(i).getDouble("totalMoney")) , 11,0);

                totalCurrentMoney += listProduct.get(i).getDouble("totalMoney");
                if (i != listProduct.size() -1){
                    UtilPrinter.printCustomTextNew(outputStream,line,2,1);
                }
            }
            UtilPrinter.printCustomTextNew(outputStream,boltLine,11,1);
            UtilPrinter.printCustomTextNew(outputStream,  Util.FormatMoney(totalCurrentMoney),11,2);

            Double sumDebt =0.0;
            for (int j=0; j<listDebts.size(); j++){

                if (listDebts.get(j).getDouble("debt") > 0){
                    sumDebt += listDebts.get(j).getDouble("debt");
                    UtilPrinter.printCustom2TextNew(outputStream,printerSize,"No HD "+ Util.DateMonthYearString(listDebts.get(j).getLong("createAt")), Util.FormatMoney(listDebts.get(j).getDouble("debt")),11,2);

                }
            }

            UtilPrinter.printCustomTextNew(outputStream,boltLine,11,1);

            UtilPrinter.printCustom2TextNew(outputStream,printerSize,"       TONG", Util.FormatMoney(total),22,2);
            UtilPrinter.printCustomTextNew(outputStream,line,2,1);

            UtilPrinter.printCustom2TextNew(outputStream,printerSize,"       TRA", Util.FormatMoney(paid),22,2);
            UtilPrinter.printCustomTextNew(outputStream,line,2,1);

            UtilPrinter.printCustom2TextNew(outputStream,printerSize,"       CON LAI", Util.FormatMoney(total - paid),22,2);
            UtilPrinter.printCustomTextNew(outputStream,boltLine,11,1);

            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomTextNew(outputStream, String.format("Đặt hàng: %s", Util.PhoneFormat(User.getPhone())), 22,1);
            //UtilPrinter.printCustomText(outputStream, "Tran trong cam on quy khach hang", 1,1);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.flush();

            mListener.onRespone(true);
            //Util.getInstance().dismissDialog(true);



        } catch (IOException e) {
            mListener.onRespone(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void printOldBillCustom(String printerSize,OutputStream outputStream, Customer currentCustomer, List<JSONObject> listBill , Double debt, CallbackBoolean mListener){
        String boltLine="";
        String line="";

        if (printerSize.equals(Constants.PRINTER_57)){
            boltLine = "================================";
            line =     "--------------------------------";
        }else if (printerSize.equals(Constants.PRINTER_80)){
            boltLine =  "================================";
            line =      "------------------------------------------------";

        }


        try {

            if (CustomSQL.getString("logo").equals("")){
                outputStream.write(PrinterCommands.FEED_LINE);
                outputStream.write(PrinterCommands.FEED_LINE);
//                UtilPrinter.printDrawablePhoto(outputStream, Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.ic_logo_print));
            }else {
                UtilPrinter.printPhoto(outputStream,CustomSQL.getString("logo"));
            }

            UtilPrinter.printCustomTextNew(outputStream,"CTY TNHH XNK TRAN VU ANH",2,1);
            UtilPrinter.printCustomTextNew(outputStream, "863, DT743A, P.Binh Thang,TX Di An, Binh Duong", 2,1);
            UtilPrinter.printCustomTextNew(outputStream,"ĐT:0931.07.22.23",11,1);
            UtilPrinter.printCustomTextNew(outputStream,"www.wolver.vn",11,1);
            UtilPrinter.printCustomTextNew(outputStream,"*********************************",1,1);
            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomTextNew(outputStream, "HÓA ĐƠN (In lai)",33,1);
            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomTextNew(outputStream,"CH     : " + Constants.getShopInfo(currentCustomer.getString("shopType") , null) + " "+ currentCustomer.getString("signBoard") , 2,0);
            UtilPrinter.printCustomTextNew(outputStream,"KH     : " + currentCustomer.getString("name"), 2,0);

            String phone = currentCustomer.getString("phone").equals("")? "--" : Util.PhoneFormat(currentCustomer.getString("phone"));
            UtilPrinter.printCustomTextNew(outputStream,"SDT    : " + phone, 2,0);
            String add = String.format("%s, %s", currentCustomer.getString("street"), currentCustomer.getString("district"));
            int len = printerSize.equals(Constants.PRINTER_57)?23:40;
            String address = add.length()>len ? add.substring(0,len) : add;
            UtilPrinter.printCustomTextNew(outputStream,"D.CHI  : " + address , 2,0);
            UtilPrinter.printCustomTextNew(outputStream,"NGAY   : " + Util.CurrentMonthYearHour(),2,0);
            UtilPrinter.printCustomTextNew(outputStream,"N.VIEN : " + User.getFullName(), 2,0);
            UtilPrinter.printCustomTextNew(outputStream,boltLine,11,1);

            Double totalCurrentMoney = 0.0;

            for (int i=0; i<listBill.size(); i++){
                UtilPrinter.printCustomTextNew(outputStream, "Hoa don "+Util.DateHourString(listBill.get(i).getLong("createAt")) , 2,0);

                List<JSONObject> listDetail = DataUtil.array2ListObject(listBill.get(i).getString("billDetails"));

                for (int a=0; a<listDetail.size(); a++){
                    UtilPrinter.printCustomTextNew(outputStream,
                            String.format("%d.%s" ,i+1, listDetail.get(a).getString("productName")) ,
                            22,0);

                    String discount = listDetail.get(a).getDouble("discount") == 0? "" : "-" + Util.FormatMoney(listDetail.get(a).getDouble("discount"));
                    Double total = (listDetail.get(a).getDouble("unitPrice") - listDetail.get(a).getDouble("discount")) * listDetail.get(a).getDouble("quantity");

                    UtilPrinter.printCustom2TextNew(outputStream ,
                            printerSize,
                            String.format(" %sx(%s)",Util.FormatMoney(listDetail.get(a).getDouble("quantity")) ,Util.FormatMoney(listDetail.get(a).getDouble("unitPrice")) + discount) ,
                            Util.FormatMoney(total) , 11,0);

                    UtilPrinter.printCustomTextNew(outputStream,line,2,1);

                }

                UtilPrinter.printCustomTextNew(outputStream,
                        Util.combine2String("TONG", Util.FormatMoney(listBill.get(i).getDouble("total")), 20),
                        11,
                        2);

                UtilPrinter.printCustomTextNew(outputStream,
                        Util.combine2String("TRA", Util.FormatMoney(listBill.get(i).getDouble("paid")), 20),
                        11,
                        2);

                UtilPrinter.printCustomTextNew(outputStream,
                        Util.combine2String("NO", Util.FormatMoney(listBill.get(i).getDouble("debt")), 20),
                        11,
                        2);

                UtilPrinter.printCustomTextNew(outputStream,boltLine,11,1);

                totalCurrentMoney += listBill.get(i).getDouble("debt");

            }
//            UtilPrinter.printCustomTextNew(outputStream,boltLine,11,1);

            UtilPrinter.printCustomTextNew(outputStream,Util.combine2String("    TONG NO", Util.FormatMoney(totalCurrentMoney), 25),22,2);
            UtilPrinter.printCustomTextNew(outputStream,line,2,1);

            outputStream.write(PrinterCommands.FEED_LINE);

            UtilPrinter.printCustomTextNew(outputStream, String.format("Đặt hàng: %s", Util.PhoneFormat(User.getPhone())), 22,1);
            //UtilPrinter.printCustomText(outputStream, "Tran trong cam on quy khach hang", 1,1);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.flush();

            mListener.onRespone(true);
            //Util.getInstance().dismissDialog(true);



        } catch (IOException e) {
            mListener.onRespone(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String createParamCustomer(Customer customer){
        String param = String.format(Api_link.CUSTOMER_CREATE_PARAM, customer.getInt("id") == 0? "" : String.format("id=%s&",customer.getString("id") ),
                Util.encodeString(customer.getString("name")),//name
                Util.encodeString(customer.getString("signBoard")),//signBoard
                Util.encodeString(customer.getString("address")), //address
                Util.encodeString(customer.getString("phone")), //phone
                Util.encodeString(customer.getString("street")), //street
                Util.encodeString(customer.getString("note")), //note
                Util.encodeString(customer.getString("district")), //district
                Util.encodeString(customer.getString("province")), //province
                customer.getDouble("lat"), //lat
                customer.getDouble("lng"), //lng
                customer.getInt("volumeEstimate"), //province
                Util.encodeString(customer.getString("shopType")), //shopType
                customer.getInt("status.id"), //currentStatusId
                Distributor.getDistributorId() //DistributorId
        );

        return param;
    }

}
