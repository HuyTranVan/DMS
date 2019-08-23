package wolve.dms.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.adapter.Statistical_ProductGroupAdapter;
import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class StatisticalProductFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvProductGroup;
    private Statistical_ProductGroupAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistical_products,container,false);
        Util.productFragment = this;
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {

    }



    private void addEvent() {

    }

    private void initializeView() {
        rvProductGroup = (RecyclerView) view.findViewById(R.id.statistical_products_rvproduct);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    public void reloadData(String username, List<BaseModel> listDetail){
//        List<BaseModel> detailList = new ArrayList<>();
//        try {
//            for (int i=0; i<list.size(); i++){
//                JSONArray array = list.get(i).getJSONArray("billDetails");
//                for (int j=0; j<array.length(); j++){
//                    JSONObject object= array.getJSONObject(j);
//                    detailList.add(new BaseModel(object));
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        List<BaseModel> listTemp ;
        if (username.equals(Constants.ALL_FILTER)){
            listTemp = listDetail;

        }else {
            listTemp = new ArrayList<>();
            for (BaseModel row : listDetail){
                if (row.getBaseModel("user").getString("displayName").equals(username)){
                    listTemp.add(row);
                }
            }
        }

        createRVProductGroup(listTemp);
    }

    private void createRVProductGroup(List<BaseModel> list) {
        adapter = new Statistical_ProductGroupAdapter( list);
        Util.createLinearRV(rvProductGroup, adapter);

    }

}
