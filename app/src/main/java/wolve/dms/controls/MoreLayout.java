package wolve.dms.controls;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import wolve.dms.R;
import wolve.dms.callback.CallbackClickAdapter;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/19/17.
 */

public class MoreLayout {

    public static void DialogSpinner(View view , int width, Drawable color, ArrayList<String> list , final CallbackClickAdapter listener){
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
                listItem.setTextColor(Util.getInstance().getCurrentActivity().getResources().getColor(R.color.black_text_color));
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
                listener.onRespone(parent.getItemAtPosition(position).toString() , position);
                popup.dismiss();
            }
        });
        popup.setFocusable(true);

        popup.setBackgroundDrawable(color);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(width);
        popup.setContentView(listViewDogs);
        popup.showAsDropDown(view , 50, 0);
    }

}
