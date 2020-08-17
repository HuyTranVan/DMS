package wolve.dms.apiconnect;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.callback.CallbackListCustom;
import wolve.dms.libraries.connectapi.CustomDeleteListMethod;
import wolve.dms.libraries.connectapi.CustomPostListMethod;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/19/17.
 */

public class CustomerConnect {
//    public static void CreateCustomerWaitingList(String params, final CallbackCustom listener, final Boolean showLoading) {
//        if (showLoading) {
//            Util.getInstance().showLoading();
//        }
//
//        new CustomPostMethod(DataUtil.createCustomerWaitingParam(params), new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(true);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void getWaitingList(final CallbackCustomList listener, Boolean showloading) {
//        if (showloading) {
//            Util.getInstance().showLoading();
//        }
//
//        new CustomGetMethod(Api_link.CUSTOMER_WAITING_LIST, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(true);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeArraySuccess(result));
//
//                } else {
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void DeleteCustomer(String params, final CallbackCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//
//        String url = Api_link.CUSTOMER_DELETE + params;
//
//        new CustomDeleteMethod(url, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(true);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Util.getInstance().stopLoading(true);
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//        }).execute();
//    }

//    public static void GetCustomerDetail(String params, final CallbackCustom listener, Boolean showLoading, Boolean stopLoading) {
//        if (showLoading) {
//            Util.getInstance().showLoading();
//        }
//
//        String url = Api_link.CUSTOMER_GETDETAIL + params;
//
//        new CustomGetMethod(url, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(stopLoading);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void PostCheckin(String params, final CallbackCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//
//        String url = Api_link.CHECKIN_NEW;
//
//        new CustomPostMethod(DataUtil.postCheckinParam(params), new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(stopLoading);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Util.getInstance().stopLoading(true);
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void DeleteCheckin(String params, final CallbackCustom listener, Boolean showLoading) {
//        if (showLoading) {
//            Util.getInstance().showLoading();
//        }
//
//        String url = Api_link.CHECKIN_DELETE + params;
//
//        new CustomGetMethod(url, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(true);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void DeletePayment(String params, final CallbackCustom listener, Boolean showLoading) {
//        if (showLoading) {
//            Util.getInstance().showLoading();
//        }
//
//        String url = Api_link.PAYMENT_DELETE + params;
//
//        new CustomGetMethod(url, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(true);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void DeleteImport(String params, final CallbackCustom listener, Boolean showLoading) {
//        if (showLoading) {
//            Util.getInstance().showLoading();
//        }
//
//        String url = Api_link.IMPORT_DELETE + params;
//
//        new CustomGetMethod(url, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(true);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void PostBill(String params, final CallbackCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//
//        new CustomPostMethod(DataUtil.postBillParam(params), new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(stopLoading);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Util.getInstance().stopLoading(true);
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void PostImport(String params, final CallbackCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//
//        new CustomPostMethod(DataUtil.postImportParam(params), new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(stopLoading);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Util.getInstance().stopLoading(true);
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void PostDebt(String params, final CallbackCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//
//        new CustomPostMethod(DataUtil.postDebtParam(params), new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(stopLoading);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Util.getInstance().stopLoading(true);
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

//    public static void PostListDebt(List<String> params, final CallbackListCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//
//        String url = Api_link.DEBT_NEW;
//
//        new CustomPostListMethod(url, params, true, new CallbackListCustom() {
//            @Override
//            public void onResponse(List result) {
//                Util.getInstance().stopLoading(stopLoading);
//                listener.onResponse(result);
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//            }
//        }).execute();
//    }


//    public static void PostListBill(List<String> listParams, final CallbackListCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//
//        String url = Api_link.BILL_NEW;
//
//        new CustomPostListMethod(url, listParams, true, new CallbackListCustom() {
//            @Override
//            public void onResponse(List result) {
//                Util.getInstance().stopLoading(stopLoading);
//                listener.onResponse(result);
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//            }
//        }).execute();
//    }

    public static void PostListPay(List<String> listParams, final CallbackListCustom listener, final Boolean stopLoading) {
        Util.getInstance().showLoading();

        String url = ApiUtil.PAY_NEW();

        new CustomPostListMethod(url, listParams, false, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                Util.getInstance().stopLoading(stopLoading);
                listener.onResponse(result);
            }

            @Override
            public void onError(String error) {
                listener.onError(error);
                Util.getInstance().stopLoading(true);
            }
        }).execute();
    }

//    public static void PostPay(String param, final CallbackCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//
//        new CustomPostMethod(DataUtil.postPayParam(param), new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(stopLoading);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeObjectSuccess(result));
//
//                } else {
//                    Util.getInstance().stopLoading(true);
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }


//    public static void DeleteListBill(List<String> listParams, final CallbackListCustom listener, final Boolean stopLoading) {
//        Util.getInstance().showLoading();
//
//        List<String> urls = new ArrayList<>();
//        for (int i = 0; i < listParams.size(); i++) {
//            urls.add(ApiUtil.BILL_DELETE + listParams.get(i));
//        }
//
//        new CustomDeleteListMethod(urls, new CallbackListCustom() {
//            @Override
//            public void onResponse(List result) {
//                Util.getInstance().stopLoading(stopLoading);
//                listener.onResponse(result);
//            }
//
//            @Override
//            public void onError(String error) {
//                listener.onError(error);
//                Util.getInstance().stopLoading(true);
//            }
//        }).execute();
//    }


//    public static void ListBillHavePayment(String param, final CallbackCustomList listener, final Boolean stopLoading) {
//        if (stopLoading) {
//            Util.getInstance().showLoading();
//        }
//
//        String url = Api_link.BILLS_HAVE_PAYMENT + String.format(Api_link.DEFAULT_RANGE, 1, 1500) + param;
//
//        new CustomGetMethod(url, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(stopLoading);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeArraySuccess(result));
//
//                } else {
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//
//        }).execute();
//    }

//    public static void ListImport(int offset, int warehouse_id, final CallbackCustomList listener, final Boolean loading, boolean stoploading) {
//        if (loading) {
//            Util.getInstance().showLoading();
//        }
//        String url = Api_link.IMPORTS + String.format(Api_link.DEFAULT_RANGE, offset, 20) + "&warehouse_id=" + warehouse_id;
//        new CustomGetMethod(url, new CallbackCustom() {
//            @Override
//            public void onResponse(BaseModel result) {
//                Util.getInstance().stopLoading(stoploading);
//                if (Constants.responeIsSuccess(result)) {
//                    listener.onResponse(Constants.getResponeArraySuccess(result));
//
//                } else {
//                    Constants.throwError(result.getString("message"));
//                    listener.onError(result.getString("message"));
//
//                }
//
//            }
//
//            @Override
//            public void onError(String error) {
//                Util.getInstance().stopLoading(true);
//                Constants.throwError(error);
//                listener.onError(error);
//
//            }
//
//        }).execute();
//    }

}
