package wolve.dms.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;

import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;

/**
 * Created by macos on 11/16/17.
 */

public class CustomDropdow {

    public static void createDropdown(View view, List<String> list, final CallbackString mListener) {
        final PopupWindow popup = new PopupWindow(Util.getInstance().getCurrentActivity());
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), R.layout.view_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String text = getItem(position);
                TextView listItem = new TextView(Util.getInstance().getCurrentActivity());
                listItem.setText(text);
                listItem.setTag(position);
                listItem.setTextSize(16);
                listItem.setPadding(30, 20, 30, 20);

                listItem.setTextColor(Util.getInstance().getCurrentActivity().getResources().getColor(R.color.white_text_color));

                return listItem;
            }
        };

        for (int i = 0; i < list.size(); i++) {
            adapter.add(list.get(i));

        }

        ListView listViewDogs = new ListView(Util.getInstance().getCurrentActivity());
        listViewDogs.setAdapter(adapter);

        listViewDogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.Result(parent.getItemAtPosition(position).toString());
                popup.dismiss();
            }
        });
        popup.setFocusable(true);

        popup.setBackgroundDrawable(Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.bg_corner5_grey));
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(400);


//        popup.setWidth(Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._100sdp));
        popup.setContentView(listViewDogs);
        popup.showAsDropDown(view, 20, 0);
    }

    public static void createListDropdown(View view, List<String> list, final CallbackString mListener) {
        Context mContext = Util.getInstance().getCurrentActivity();
        ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);

        ListAdapter listAdapter = new ArrayAdapter(mContext, R.layout.view_spinner_item, list);
        listPopupWindow.setAdapter(listAdapter);
        listPopupWindow.setAnchorView(view);
        listPopupWindow.setWidth(measureContentWidth(mContext, listAdapter));
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.Result(adapterView.getItemAtPosition(i).toString());
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.show();

    }

    private static int measureContentWidth(Context mContext, ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(mContext);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }


}
