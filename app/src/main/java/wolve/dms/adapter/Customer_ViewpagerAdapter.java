package wolve.dms.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.customviews.CTextIcon;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class Customer_ViewpagerAdapter extends FragmentPagerAdapter{

    private Context mContext;

    private List<Fragment> mFragments = new ArrayList<>();
    private List<Integer> mFragmentCount = new ArrayList<>();
    private List<String> mFragmentText = new ArrayList<>();

    public Customer_ViewpagerAdapter(FragmentManager fm) {
        super(fm);
        this.mContext = Util.getInstance().getCurrentActivity();
    }

    public void addFragment(Fragment fragment, String  text, int count) {
        mFragments.add(fragment);
        mFragmentCount.add(count);
        mFragmentText.add(text);
    }

    public void addCountNotify(int count, int position){
        //mFragmentNotify.remove(position);
        //mFragmentNotify.add(position, count);
        //notifyDataSetChanged();
    }

    public void replaceFragment(Fragment fragment, String title, String  text, int count, int position) {
        mFragments.remove(position);
        mFragments.add(position, fragment);

        //mFragmentNotify.remove(position);
        //mFragmentNotify.add(position, count);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public View getTabView(int position) {
        View tab = LayoutInflater.from(mContext).inflate(R.layout.view_tab_customer, null);
        TextView tvTitle = (TextView) tab.findViewById(R.id.tabTitle);
        TextView tvNotify = (TextView) tab.findViewById(R.id.tabNotify);

        //tvIcon.setText(mFragmentIcons.get(position));
        tvTitle.setText(mFragmentText.get(position));
        if (mFragmentCount.get(position) !=0){
            tvNotify.setVisibility(View.VISIBLE);
            tvNotify.setText(String.valueOf(mFragmentCount.get(position)));
        }else {
            tvNotify.setVisibility(View.GONE);
        }

        if (position == 0) {
            tab.setSelected(true);
        }
        return tab;
    }

    public View getNotifyBaged(int count){
        View tab = LayoutInflater.from(mContext).inflate(R.layout.view_tab_customer, null);
        TextView tvTitle = (TextView) tab.findViewById(R.id.tabTitle);
        TextView tvNotify = (TextView) tab.findViewById(R.id.tabNotify);

        tvTitle.setText(mFragmentText.get(1));
        tvNotify.setVisibility(count ==0?View.GONE: View.VISIBLE);
        tvNotify.setText(String.valueOf(count));


        return tab;

    }


}