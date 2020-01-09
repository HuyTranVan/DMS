package wolve.dms.activities;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.TestAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackListCustom;
import wolve.dms.libraries.Security;
import wolve.dms.libraries.connectapi.CustomGetMethod;
import wolve.dms.libraries.connectapi.CustomPostListMethod;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class TestActivity extends BaseActivity implements View.OnClickListener{
    private ImageView btnBack;
    private RecyclerView rvTest;
    private TestAdapter adapter;
    private Button btnConfirm;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_test;
    }

    @Override
    public int setIdContainer() {
        return R.id.product_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvTest = (RecyclerView) findViewById(R.id.rvtest);
        btnConfirm =  findViewById(R.id.confirm);

    }

    @Override
    public void initialData() {
        loadListBill();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                Transaction.gotoHomeActivityRight(true);
                onBackPressed();
                break;

            case R.id.confirm:
//                UpdateIsBillReturn();
//                UpdateBillDelivered();
                //loadListBill();
                UpdateCustomerDebt();
                break;

        }
    }

    protected void loadListBill() {

        Util.getInstance().showLoading(true);
        //String url = Api_link.BASE_URL + "token/bills/have_note";
        String url = Api_link.BASE_URL + "token/temp/CustomerHaveBill";

        new CustomGetMethod(url, new CallbackCustom() {
            @Override
            public void onResponse(BaseModel result) {
                Util.getInstance().stopLoading(true);
                if (Constants.responeIsSuccess(result)){
                    createRVTest(Constants.getResponeArraySuccess(result));

                }else {
                    Constants.throwError(result.getString("message"));
                    //listener.onError(result.getString("message"));
                    Util.getInstance().stopLoading(true);
                }
            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
                Constants.throwError(error);
                //listener.onError(error);

            }

        }).execute();
    }


    private void createRVTest(List<BaseModel> list){
//        List<BaseModel> list1 = listBillIsReturn(list);
//        List<BaseModel> list1 = listBillDeliver(list);
        List<BaseModel> list1 = listCustomer(list);
        adapter = new TestAdapter(list1);
        Util.createLinearRV(rvTest, adapter);

    }

    private List<BaseModel> listBillIsReturn(List<BaseModel> list){
        List<BaseModel> listBill = new ArrayList<>();

        for (int i=0; i<list.size(); i++){
            String note = Security.decrypt(list.get(i).getString("note"));

            if (!note.equals("")) {
                if (Util.isJSONObject(note)) {
                    BaseModel notttt = new BaseModel(note);
//                    if (notttt.hasKey("haveBillReturn")){
                    if (notttt.hasKey("isBillReturn")){
                        list.get(i).put("isReturnId", notttt.getBaseModel("isBillReturn").getInt("id"));
                        listBill.add(list.get(i));
                    }

                }

            }
        }
        return listBill;
    }
    private List<BaseModel> listBillDeliver(List<BaseModel> list){
        List<BaseModel> listBill = new ArrayList<>();

        for (int i=0; i<list.size(); i++){
            String note = Security.decrypt(list.get(i).getString("note"));

            if (!note.equals("")) {
                if (Util.isJSONObject(note)) {
                    BaseModel notttt = new BaseModel(note);
                    if (!notttt.hasKey("isBillReturn") && !notttt.hasKey("haveBillReturn")
                            && notttt.hasKey("temp_bill") && !notttt.getBoolean("temp_bill")){
                        list.get(i).put("deliverId", notttt.getBaseModel("deliverBy").getInt("id"));
                        listBill.add(list.get(i));
                    }

                }

            }
        }
        return listBill;
    }
    private List<BaseModel> listBillHaveReturn(List<BaseModel> list){
        List<BaseModel> listBill = new ArrayList<>();

        for (int i=0; i<list.size(); i++){
            String note = Security.decrypt(list.get(i).getString("note"));

            if (!note.equals("")) {
                if (Util.isJSONObject(note)) {
                    listBill.add(list.get(i));

//                    BaseModel notttt = new BaseModel(note);
//                    if (notttt.hasKey("haveBillReturn")){
////                    if (notttt.hasKey("isBillReturn")){
//                        listBill.add(list.get(i));
//                    }

                }

            }
        }
        return listBill;
    }

    private List<BaseModel> listCustomer(List<BaseModel> list){
        List<BaseModel> listCustomer = new ArrayList<>();

        for (BaseModel baseModel : list){
            List<BaseModel> listOriginalBill= new ArrayList<>(DataUtil.array2ListObject(baseModel.getString("bills")));
            List<BaseModel> listBill= new ArrayList<>(DataUtil.remakeBill(listOriginalBill, false));
            baseModel.putList(Constants.BILLS, listBill);

            listCustomer.add(baseModel);


        }
        return listCustomer;
    }


    private void UpdateIsBillReturn(){
        List<BaseModel> list = adapter.getmData();
        List<String> params = new ArrayList<>();
        String patern = "id=%d&isReturn=%d";
        for (BaseModel baseModel: list){
            params.add(String.format(patern, baseModel.getInt("id"), baseModel.getInt("isReturnId")));

        }

        String url = Api_link.BASE_URL + "token/temp/IsBillReturnUpdate.php";

        Util.getInstance().showLoading();
        new CustomPostListMethod(url, params, false, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
            }
        }).execute();

    }
    private void UpdateBillDelivered(){
        List<BaseModel> list = adapter.getmData();
        List<String> params = new ArrayList<>();
        String patern = "id=%d&deliverBy=%d";
        for (BaseModel baseModel: list){
            params.add(String.format(patern, baseModel.getInt("id"), baseModel.getInt("deliverId")));

        }

        String url = Api_link.BASE_URL + "token/temp/BillDelivered.php";

        Util.getInstance().showLoading();
        new CustomPostListMethod(url, params, false, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
            }
        }).execute();

    }

    private void UpdateCustomerDebt(){
        List<BaseModel> list = adapter.getDebtData();
        List<String> params = new ArrayList<>();
        //String patern = "id=%d&deliverBy=%d";

        //String param = String.format(Api_link.DEBT_PARAM, 25000, 7, 1325);

        for (int i=0; i< list.size(); i++){
            params.add(String.format(Api_link.DEBT_PARAM,
                    list.get(i).getDouble("debt"),
                    list.get(i).getInt("user_id"),
                    list.get(i).getInt("id"),
                    list.get(i).getInt("distributor_id")));

        }

        String url = Api_link.DEBT_NEW;

        Util.getInstance().showLoading();
        new CustomPostListMethod(url, params, false, new CallbackListCustom() {
            @Override
            public void onResponse(List result) {
                Util.getInstance().stopLoading(true);
            }

            @Override
            public void onError(String error) {
                Util.getInstance().stopLoading(true);
            }
        }).execute();

    }



}
