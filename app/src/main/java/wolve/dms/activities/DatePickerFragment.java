package wolve.dms.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.BaseActivity;
import wolve.dms.R;
import wolve.dms.adapter.ProductAdapter;
import wolve.dms.adapter.ProductGroupAdapter;
import wolve.dms.adapter.ViewpagerDatePickerAdapter;
import wolve.dms.adapter.ViewpagerProductAdapter;
import wolve.dms.apiconnect.Api_link;
import wolve.dms.apiconnect.ProductConnect;
import wolve.dms.apiconnect.StatusConnect;
import wolve.dms.callback.Callback;
import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.callback.CallbackCustom;
import wolve.dms.callback.CallbackCustomList;
import wolve.dms.callback.CallbackDeleteAdapter;
import wolve.dms.callback.CallbackObject;
import wolve.dms.customviews.CInputForm;
import wolve.dms.libraries.FitScrollWithFullscreen;
import wolve.dms.libraries.MySwipeRefreshLayout;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Status;
import wolve.dms.models.User;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Transaction;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class DatePickerFragment extends Fragment implements View.OnClickListener{
    private View view;
    private ImageView btnBack;
    private Button btnSubmit;
    private TextView tvTitle;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ViewpagerDatePickerAdapter viewpagerAdapter;
    private int currentPosition =0;
    private String[] titles = new String[]{"CHỌN NGÀY", "CHỌN THÁNG", "CHỌN NĂM"};
    private CallbackObject onDataPass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_date_picker,container,false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager();
    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

    }

    private void initializeView() {
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        btnSubmit = (Button) view.findViewById(R.id.date_picker_submit);
        tvTitle = view.findViewById(R.id.date_picker_title);
        viewPager = (ViewPager) view.findViewById(R.id.date_picker_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.date_picker_tabs);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()){
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();

                break;

            case R.id.date_picker_submit:
                switch (currentPosition){
                    case 0:
                        onDataPass.onResponse(viewpagerAdapter.getDateSelected());
                        break;

                    case 1:
                        onDataPass.onResponse(viewpagerAdapter.getMonthSelected());
                        break;

                    case 2:
                        onDataPass.onResponse(viewpagerAdapter.getYearSelected());
                        break;

                }

                getActivity().getSupportFragmentManager().popBackStack();

                break;


        }
    }

    private void setupViewPager(){
        viewpagerAdapter = new ViewpagerDatePickerAdapter(titles);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i=0; i<titles.length; i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(getActivity()).inflate(R.layout.view_tab_text, null);
            TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

            textTitle.setText(titles[i]);

            tab.setCustomView(customView);

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (CallbackObject) context;
    }


}
