package wolve.dms.adapter;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

import wolve.dms.R;
import wolve.dms.models.Customer;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 11/7/17.
 */

public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View myContentsView;

    public CustomWindowAdapter(){
        myContentsView = Util.getInstance().getCurrentActivity().getLayoutInflater().inflate(R.layout.view_map_infowindow, null);
    }

    @Override
    public View getInfoContents(Marker marker) {

        TextView tvname = myContentsView.findViewById(R.id.map_infowindow_name);
        TextView tvCount = myContentsView.findViewById(R.id.map_infowindow_checkin_count);
        TextView tvNote = myContentsView.findViewById(R.id.map_infowindow_note);

        final Customer customer = new Customer((JSONObject) marker.getTag());

        String title = Constants.getShopInfo(customer.getString("shopType"), null) +" " + customer.getString("signBoard");
        String add = customer.getString("address") + " " + customer.getString("street") ;

        tvname.setText(title);
        tvCount.setText(String.format("Check_in: %d lần",customer.getInt("checkinCount")));
        tvNote.setVisibility(customer.getString("note").equals("")? View.GONE : View.VISIBLE);
        tvNote.setText(customer.getString("note"));


        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }
}
