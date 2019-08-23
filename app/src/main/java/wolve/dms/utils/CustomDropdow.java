package wolve.dms.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.callback.CallbackString;
import wolve.dms.models.Status;

/**
 * Created by macos on 11/16/17.
 */

public class CustomDropdow {

    public static void createDropdown(View view , List<String> list, final CallbackString mListener){
        final PopupWindow popup = new PopupWindow(Util.getInstance().getCurrentActivity());
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), android.R.layout.select_dialog_item){
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

        for (int i=0; i<list.size(); i++){
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

        //popup.setBackgroundDrawable(Util.getInstance().getCurrentActivity().getResources().getDrawable(R.drawable.bg_grey_corner));
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._100sdp));
        popup.setContentView(listViewDogs);
        popup.showAsDropDown(view , 0, 0);
    }


}
