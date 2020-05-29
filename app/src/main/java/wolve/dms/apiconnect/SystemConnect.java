package wolve.dms.apiconnect;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackCustomListList;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.callback.CallbackString;
import wolve.dms.libraries.connectapi.CustomGetListMethod;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomGetPostListMethod;
import wolve.dms.libraries.connectapi.CustomPostMethod;
import wolve.dms.libraries.connectapi.UploadCloudaryMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by macos on 1/28/18.
 */

public class SystemConnect {

    public static void getFCMToken(CallbackString listener){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            listener.Result("");
                            Log.e("tag", "getInstanceId failed", task.getException());
                            return;
                        }

                        listener.Result(task.getResult().getToken());


                    }

                });

    }

    public static void getAllData(final CallbackListCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        String[] url =new String[]{
                Api_link.STATUS+ String.format(Api_link.DEFAULT_RANGE, 1,20),
                Api_link.DISTRICTS + Distributor.getLocationId(),
                Api_link.PRODUCT_GROUPS+ String.format(Api_link.DEFAULT_RANGE, 1,10),
                Api_link.PRODUCTS+ String.format(Api_link.DEFAULT_RANGE, 1,500),
                Api_link.PROVINCES};

        new CustomGetListMethod(url, new CallbackListCustom() {
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

    public static void getDistrict(int provinceId, final CallbackCustomList listener){
        //Util.getInstance().showLoading();

        String url = Api_link.DISTRICTS+provinceId;
        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                //Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeArraySuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                //Util.getInstance().stopLoading(stopLoading);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();

    }

    public static void getCheckLogin(final CallbackCustom listener, boolean showLoding){
        Util.getInstance().showLoading(showLoding);

        String url = Api_link.CHECK_LOGIN ;
        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(true);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();

    }

    public static void getCategories(final CallbackCustom listener, boolean stoploading){
        Util.getInstance().showLoading();

        String url = Api_link.CATEGORIES;
        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                if (Constants.responeIsSuccess(result)){
                    Util.getInstance().stopLoading(stoploading);
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Util.getInstance().stopLoading(true);
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();

    }

    public static void getStatisticals(String range, final CallbackCustom listener){
        Util.getInstance().showLoading();

        String url = Api_link.STATISTICALS + range;
        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(true);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();

    }

    public static void loadListObject(List<BaseModel> listParam, CallbackCustomListList listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        new CustomGetPostListMethod(listParam, new CallbackCustomList() {
            @Override
            public void onResponse(List<BaseModel> results) {
                Util.getInstance().stopLoading(stopLoading);
                List<List<BaseModel>> listResult = new ArrayList<>();

                for (int i=0; i<results.size(); i++){
                    if (Constants.responeIsSuccess(results.get(i))){
                        listResult.add(Constants.getResponeArraySuccess(results.get(i)));

                    }else {
                        listResult.add(new ArrayList<>());
                        Constants.throwError(String.format("list position at %d error",i));
                        listener.onError("");

                    }
                }

                listener.onResponse(listResult);


            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);
            }
        }).execute();
    }

    public static void GetDistributorDetail(String params,final CallbackCustom listener, Boolean stopLoading){
        Util.getInstance().showLoading();

        String url = Api_link.DISTRIBUTOR_DETAIL + params;
        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();
    }

    public static void CreateDistributor(String params, final CallbackCustom listener, final Boolean showLoading){

        Util.getInstance().showLoading();

        new CustomPostMethod(DataUtil.createNewDistributorParam(params),new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {

                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));
                    Util.getInstance().stopLoading(showLoading);

                }else {
                    Util.getInstance().stopLoading(true);
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();
    }

    public static void getLastestProductUpdated(final CallbackCustom listener, boolean showLoading){
        Util.getInstance().showLoading(showLoading);

        String url = Api_link.PRODUCT_LASTEST;
        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(true);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();

    }

    public static void ListWarehouse(Boolean loading, final CallbackCustomList listener, final Boolean stopLoading){
        if (loading)
            Util.getInstance().showLoading();

        String url = Api_link.WAREHOUSES;

        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeArraySuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));
                    Util.getInstance().stopLoading(true);
                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();
    }

    public static void CreateDepot(String params,final CallbackCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        new CustomPostMethod(DataUtil.createNewDepotParam(params),new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Util.getInstance().stopLoading(true);
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();
    }

    public static void GetInventoryList(int warehouse_id, final CallbackListCustom listener, Boolean stopLoading){
        Util.getInstance().showLoading(stopLoading);

        String url = Api_link.INVENTORIES + String.format("?id=%d", warehouse_id);
        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(true);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeArraySuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }


        }).execute();
    }

    public static void UpdateInventoryQuantity(String params, final CallbackCustom listener, final Boolean stopLoading){
        Util.getInstance().showLoading();

        new CustomPostMethod(DataUtil.postInventoryQuantityParam(params),new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(stopLoading);
                if (Constants.responeIsSuccess(result)){
                    listener.onResponse(Constants.getResponeObjectSuccess(result));

                }else {
                    Util.getInstance().stopLoading(true);
                    Constants.throwError(result.getString("message"));
                    listener.onError(result.getString("message"));

                }

            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                listener.onError(error);

            }

        }).execute();
    }


    public static void uploadImage(String uriPath, final CallbackString mListener){
        Util.getInstance().showLoading();
        new UploadCloudaryMethod(uriPath, new CallbackString() {
            @Override
            public void Result(String s) {
                Util.getInstance().stopLoading(true);
                if (s != null){
                    mListener.Result(s);
                }
            }

        }).execute();

    }

}
