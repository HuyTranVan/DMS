package wolve.dms.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.SummaryAdapter;
import wolve.dms.apiconnect.ApiUtil;
import wolve.dms.apiconnect.apigooglesheet.GoogleSheetGetData;
import wolve.dms.apiconnect.apiserver.GDriveMethod;
import wolve.dms.apiconnect.apiserver.GetPostMethod;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.NewCallbackCustom;
import wolve.dms.libraries.BitmapView;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class SummaryFragment extends Fragment implements View.OnClickListener{
    private View view;
    private TextView tvTitle, tvDistributor, tvPrint, tvSign;
    private ImageView btnBack;
    private RecyclerView rvContent;
    private NestedScrollView mLayout;

    private StatisticalActivity mActivity;
    private SummaryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary, container, false);

        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    public void intitialData() {
        tvDistributor.setText(Distributor.getName());
        BaseModel rangeTime = new BaseModel(getArguments().getString(Constants.RANGE_TIME));
        tvTitle.setText(rangeTime.getString("text") );
        tvSign.setText(String.format("DMS export at %s", Util.CurrentMonthYearHour()));

        loadSummary(rangeTime);


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        tvPrint.setOnClickListener(this);

    }

    private void initializeView() {
        mActivity = (StatisticalActivity) getActivity();
        btnBack = view.findViewById(R.id.icon_back);
        rvContent = view.findViewById(R.id.summary_rvcontent);
        tvTitle = view.findViewById(R.id.summary_title);
        tvDistributor = view.findViewById(R.id.summary_distributor);
        tvPrint = view.findViewById(R.id.summary_print);
        tvSign = view.findViewById(R.id.summary_copyright);
        mLayout = view.findViewById(R.id.summary_content_parent);


    }

    private void backEvent() {
        getActivity().getSupportFragmentManager().popBackStack();

    }


    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                backEvent();

                break;

            case R.id.summary_print:
                share();

                break;





        }
    }

    private  void loadSummary(BaseModel range){
        BaseModel param = mActivity.createGetParam(
                String.format(ApiUtil.SUMMARIES(), range.getLong("start"), range.getLong("end")),
                true
        );
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                createSummaryRV(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }

    private void createSummaryRV(List<BaseModel> list){
        adapter = new SummaryAdapter(list, new CallbackClickAdapter() {
            @Override
            public void onRespone(String data, int position) {

            }
        });
        Util.createLinearRV(rvContent, adapter);

    }

    private void share(){
        Uri currentImagePath = Util.storeImage(BitmapView.getBitmapFromView(mLayout) ,
                "SHARE",
                "SHARE",
                true);

        Transaction.shareImage(currentImagePath, null);
    }




    //    private void updateSheetTab(){
//        String startday = Util.DateString(getStartDay());
//        String endday = Util.DateString(Util.CurrentTimeStamp()< getEndDay() ? Util.CurrentTimeStamp() : getEndDay());
//        SheetConnect.getALlTab(Api_link.STATISTICAL_SHEET_KEY, new GoogleSheetGetAllTab.CallbackListSheet() {
//            @Override
//            public void onRespone(List<Sheet> results) {
//                boolean check = false;
//                for (Sheet sheet : results) {
//                    if (sheet.getProperties().getTitle().equals(rdMonth.getText().toString())){
//                        check = true;
//                        break;
//                    }
//
//                }
//
//                if (!check){
//
//                    SheetConnect.createNewTab(Api_link.STATISTICAL_SHEET_KEY, rdMonth.getText().toString(), new GoogleSheetGetData.CallbackListList() {
//                        @Override
//                        public void onRespone(List<List<Object>> results) {
//                            updateSheetIncomeData(rdMonth.getText().toString() ,
//                                            SHEET_COLUM,
//                                            DataUtil.updateIncomeByUserToSheet(startday, endday, listUser, listInitialBill, listInitialBillDetail, listInitialPayment, listInitialDebt));
//
////                                            DataUtil.getCashByUser(listInitialBill, listInitialPayment, listUser)));
//
//                        }
//                    },false);
//
//                }else {
//                    updateSheetIncomeData(rdMonth.getText().toString(),
//                                    SHEET_COLUM,
//                                    DataUtil.updateIncomeByUserToSheet(startday, endday, listUser, listInitialBill, listInitialBillDetail, listInitialPayment, listInitialDebt));
//
////                                    DataUtil.getCashByUser(listInitialBill, listInitialPayment, listUser)));
//
//                }
//
//            }
//        }, false);
//    }

//    private void updateSheetIncomeData(final String tabtitle, String sheet_direction, final List<List<Object>> params) {
//        GDriveMethod.postValue(ApiUtil.STATISTICAL_SHEET_KEY,
//                String.format(ApiUtil.STATISTICAL_SHEET_TAB, tabtitle, 1),
//                params,
//                sheet_direction,
//                new GoogleSheetGetData.CallbackListList() {
//                    @Override
//                    public void onRespone(List<List<Object>> results) {
//
//
//                    }
//                }, true);
//
//
//    }

}
