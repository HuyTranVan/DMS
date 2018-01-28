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
import wolve.dms.libraries.connectapi.CustomDeleteMethod;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.libraries.printerdriver.PrinterCommands;
import wolve.dms.libraries.printerdriver.UtilPrinter;
import wolve.dms.models.Bill;
import wolve.dms.models.Customer;
import wolve.dms.models.Product;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;
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

        String url = Api_link.CUSTOMERS+ String.format(Api_link.CUSTOMER_PARAM, 1,500);

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

        String url = Api_link.CUSTOMERS_NEAREST+ String.format(Api_link.CUSTOMER_NEAREST_PARAM, lat, lng,1, 30);

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

        String url = Api_link.BILLS+ String.format(Api_link.BILL_PARAM, 1,500) + param;

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

    public static void printBill(OutputStream outputStream, Customer currentCustomer, List<Product> listProduct, List<Bill> listBills , Double paid, CallbackBoolean mListener){
        String shortLine ="---------------------";
        String longLine = "--------------------------------";
        Double total  = Util.getTotalMoney(listBills);

        try {
            if (CustomSQL.getString("logo").equals("")){
                UtilPrinter.printDrawablePhoto(outputStream, Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.ic_logo_print));
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
                    UtilPrinter.printCustom2Text(outputStream , "(Khuyen mai)" , Util.FormatMoney(listProduct.get(i).getDouble("totalMoney")) , 1,0);

                }else {
                    String discount = listProduct.get(i).getDouble("discount") == 0? "" : " -" + Util.FormatMoney(listProduct.get(i).getDouble("discount"));
                    UtilPrinter.printCustom2Text(outputStream ,
                            " "+ Util.FormatMoney(listProduct.get(i).getDouble("quantity")) + "x" + Util.FormatMoney(listProduct.get(i).getDouble("unitPrice")) + discount ,
                            Util.FormatMoney(listProduct.get(i).getDouble("totalMoney")) , 1,0);
                }


                if (i != listProduct.size() -1){
                    UtilPrinter.printCustomText(outputStream,longLine,1,1);
                }
            }
            UtilPrinter.printCustomText(outputStream,shortLine,3,1);
            UtilPrinter.printCustom2Text(outputStream,"TONG:", Util.FormatMoney(Util.getTotalMoney(listBills)),4,2);

            Double sumDebt =0.0;
            for (int j=0; j<listBills.size(); j++){

                if (listBills.get(j).getDouble("debt") > 0){
                    sumDebt += listBills.get(j).getDouble("debt");
                    UtilPrinter.printCustom2Text(outputStream,"NO "+ Util.DateMonthString(listBills.get(j).getLong("createAt")), Util.FormatMoney(listBills.get(j).getDouble("debt")),4,2);

                }
            }

            if (listBills.size() >0){
                UtilPrinter.printCustomText(outputStream,longLine,1,1);
            }

            UtilPrinter.printCustom2Text(outputStream,"TRA:", Util.FormatMoney(paid),4,2);
            UtilPrinter.printCustomText(outputStream,longLine,1,1);
            UtilPrinter.printCustom2Text(outputStream,"CON LAI:", Util.FormatMoney(total + sumDebt - paid),4,2);
            UtilPrinter.printCustomText(outputStream,shortLine,3,1);
            UtilPrinter.printCustomText(outputStream, String.format("Đặt hàng: %s", Util.PhoneFormat(User.getPhone())), 1,1);
            UtilPrinter.printCustomText(outputStream, "Tran trong cam on quy khach hang", 1,1);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.write(PrinterCommands.FEED_LINE);
            outputStream.flush();

            mListener.onRespone(true);
            //Util.getInstance().stopLoading(true);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
