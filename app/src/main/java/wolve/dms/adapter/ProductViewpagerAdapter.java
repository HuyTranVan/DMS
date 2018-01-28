package wolve.dms.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.ProductGroup;
import wolve.dms.utils.Util;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductViewpagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<RecyclerView.Adapter> listAdapter = new ArrayList<>();
    private  List<String> listTitle = new ArrayList<>();
    private View view;

    public ProductViewpagerAdapter( List<RecyclerView.Adapter> listAdapter, List<ProductGroup> productGroups){
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


}