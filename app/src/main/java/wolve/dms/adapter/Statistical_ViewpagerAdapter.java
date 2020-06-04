package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class Statistical_ViewpagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mFragmentIcons = new ArrayList<>();
    private List<String> mFragmentText = new ArrayList<>();

    public Statistical_ViewpagerAdapter(FragmentManager fm) {
        super(fm);
        this.mContext = Util.getInstance().getCurrentActivity();
    }

    public void addFragment(Fragment fragment, String title, String text) {
        mFragments.add(fragment);
        mFragmentIcons.add(title);
        mFragmentText.add(text);
    }

    public void addCountNotify(int count, int position) {
        //mFragmentNotify.remove(position);
        //mFragmentNotify.add(position, count);
        //notifyDataSetChanged();
    }

    public void replaceFragment(Fragment fragment, String title, String text, int count, int position) {
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
        View tab = LayoutInflater.from(mContext).inflate(R.layout.view_tab_statistical, null);
        TextView tvTitle = (TextView) tab.findViewById(R.id.statistical_tab_title);
        TextView tvIcon = (TextView) tab.findViewById(R.id.statistical_tab_icon);

        tvIcon.setText(mFragmentIcons.get(position));
        tvTitle.setText(mFragmentText.get(position));

        if (position == 0) {
            tab.setSelected(true);
        }
        return tab;
    }


}