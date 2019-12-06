package wolve.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ViewpagerProductAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<RecyclerView.Adapter> listAdapter = new ArrayList<>();
    private  List<String> listTitle = new ArrayList<>();
    private View view;

    public ViewpagerProductAdapter(List<RecyclerView.Adapter> listAdapter, List<BaseModel> productGroups){
        this.mContext = Util.getInstance().getCurrentActivity();
        this.listAdapter = listAdapter;

        for (int i=0; i<productGroups.size(); i++){
            listTitle.add(productGroups.get(i).getString("name"));
        }
    }

    @Override
    public int getCount() {
        return listTitle.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view ==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.adapter_customer_viewpager_item,container,false);
        RecyclerView rvList = (RecyclerView) view.findViewById(R.id.customer_viewpager_item_rv);


        //setup List
        listAdapter.get(position).notifyDataSetChanged();
        rvList.setAdapter(listAdapter.get(position));
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);
        rvList.addOnItemTouchListener(mScrollTouchListener);
        //rvList.setNestedScrollingEnabled(true);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        container.refreshDrawableState();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listTitle.get(position);
    }

    //fix recyclerview scroll
    RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int action = e.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        }
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };


//    public View getTabView(int position) {
//        View tab = LayoutInflater.from(mContext).inflate(R.layout.view_tabbar, null);
//        CTextView tabTextTitle = (CTextView) tab.findViewById(R.id.tabTextIcon);
//        CTextView tabText = (CTextView) tab.findViewById(R.id.tabText);
//        CTextViewDefault tabNotify = (CTextViewDefault) tab.findViewById(R.id.tabNotify);
//        //ImageView tabImage = (ImageView) tab.findViewById(R.id.tabImage);
//        tabTextTitle.setText(mFragmentIcons.get(position));
//        tabText.setText(mFragmentText.get(position));
//        tabNotify.setVisibility(View.GONE);
//
//        //tabImage.setBackgroundResource(mFragmentIcons.get(position));
//        if (position == 0) {
//            tab.setSelected(true);
//        }
//        return tab;
//    }
//
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