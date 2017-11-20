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
import wolve.dms.controls.CTextIcon;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class StatisticalViewpagerAdapter extends FragmentPagerAdapter{

    private Context mContext;

    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mFragmentIcons = new ArrayList<>();
    private List<String> mFragmentText = new ArrayList<>();

    public StatisticalViewpagerAdapter( FragmentManager fm) {
        super(fm);
        this.mContext = Util.getInstance().getCurrentActivity();
    }

    public void addFragment(Fragment fragment, String title, String  text) {
        mFragments.add(fragment);
        mFragmentIcons.add(title);
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
        View tab = LayoutInflater.from(mContext).inflate(R.layout.view_statistical_tab, null);
        TextView tvTitle = (TextView) tab.findViewById(R.id.statistical_tab_title);
        CTextIcon tvIcon = (CTextIcon) tab.findViewById(R.id.statistical_tab_icon);

        tvIcon.setText(mFragmentIcons.get(position));
        tvTitle.setText(mFragmentText.get(position));

        if (position == 0) {
            tab.setSelected(true);
        }
        return tab;
    }

//    public View getNotifyBaged(int count){
//        View tab = LayoutInflater.from(mContext).inflate(R.layout.view_tabbar, null);
//        CTextView tabTextTitle = (CTextView) tab.findViewById(R.id.tabTextIcon);
//        CTextView tabText = (CTextView) tab.findViewById(R.id.tabText);
//        CTextViewDefault tabNotify = (CTextViewDefault) tab.findViewById(R.id.tabNotify);
//        tabTextTitle.setText(mFragmentIcons.get(2));
//        tabText.setText(mFragmentText.get(2));
//        tabNotify.setVisibility(count ==0?View.GONE: View.VISIBLE);
//        tabNotify.setText(count +"");
//
//
//        return tab;
//
//    }


}