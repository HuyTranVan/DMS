package wolve.dms.adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.R;
import wolve.dms.callback.LatlngListener;
import wolve.dms.models.Customer;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 11/7/17.
 */

public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View myContentsView;
    private LatlngListener mListener;

    public CustomWindowAdapter(LatlngListener listener) {
        myContentsView = Util.getInstance().getCurrentActivity().getLayoutInflater().inflate(R.layout.view_map_infowindow, null);
        this.mListener = listener;
    }

    @Override
    public View getInfoContents(Marker marker) {

        TextView tvname = myContentsView.findViewById(R.id.map_infowindow_name);
        TextView tvCount = myContentsView.findViewById(R.id.map_infowindow_checkin_count);
        TextView tvNote = myContentsView.findViewById(R.id.map_infowindow_note);
        TextView tvDistance = myContentsView.findViewById(R.id.map_infowindow_distance);
        TextView tvPhoneIcon = myContentsView.findViewById(R.id.map_infowindow_phone);
        LinearLayout lnNoteGroup = myContentsView.findViewById(R.id.map_infowindow_note_group);

        final Customer customer = new Customer((JSONObject) marker.getTag());

        JSONArray arrayCheckIns = customer.getJSONArray("checkIns");

        String title = Constants.shopName[customer.getInt("shopType")] + " " + customer.getString("signBoard");
        tvname.setText(title);

        tvPhoneIcon.setVisibility(customer.getString("phone").equals("") ? View.GONE : View.VISIBLE);

        //String add = customer.getString("address") + " " + customer.getString("street") ;

        try {
            JSONObject objectLastCheckin = arrayCheckIns.length() > 0 ? arrayCheckIns.getJSONObject(arrayCheckIns.length() - 1) : new JSONObject();
            int day = objectLastCheckin.isNull("createAt") ? 0 : (int) Util.countDay(objectLastCheckin.getLong("createAt"));

            if (arrayCheckIns.length() > 0) {
                tvCount.setVisibility(View.GONE);
                tvCount.setText(day == 0 ? "Đã checkin hôm nay" : String.format("Checkin %d ngày trước", day));

                if (objectLastCheckin.isNull("note")) {
                    lnNoteGroup.setVisibility(View.GONE);
                } else {
                    lnNoteGroup.setVisibility(View.VISIBLE);
                    String note = String.format("[%s] %s", Util.DateMonthYearString(objectLastCheckin.getLong("createAt")),
                            objectLastCheckin.getString("note").contains("[") ? objectLastCheckin.getString("note").split(" ", 2)[1] : objectLastCheckin.getString("note"));
                    tvNote.setText(note);
                }

            } else {
                tvCount.setVisibility(View.GONE);
                lnNoteGroup.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mListener.onLatlngChange(new LatLng(customer.getDouble("lat"), customer.getDouble("lng")));
        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;

    }

    public View getCurrentView() {
        return myContentsView;
    }


}
