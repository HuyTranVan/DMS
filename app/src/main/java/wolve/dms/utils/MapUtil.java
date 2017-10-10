package wolve.dms.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.Customer;

/**
 * Created by Engine on 1/13/2017.
 */

public class MapUtil{
    private static MapUtil util;
    private long totalDistance;
    private long totalDuration;
    private int step;
    public Marker currentMarker;
    private int currentStep;
    private GoogleMap currentMap;
    public static List<Marker> markers;

    public static synchronized MapUtil getInstance() {
        if (util == null)
            util = new MapUtil();

        return util;
    }

    public void reboundMap() {
        if (markers.size() >= 1) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            currentMap.moveCamera(cu);
        }

    }

    public static Bitmap GetBitmapMarker(Context mContext, int resourceId, String mText, int textColor) {
        try
        {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

            Bitmap.Config bitmapConfig = bitmap.getConfig();

            // set default bitmap config if none
            if(bitmapConfig == null)
                bitmapConfig = Bitmap.Config.ARGB_8888;

            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(ContextCompat.getColor(mContext, textColor));
            paint.setTextSize((int) (14 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width()) / 2 - 1;
            int y = (bitmap.getHeight() / 4 + bounds.height() / 2);

            canvas.drawText(mText, x, y, paint);

            return bitmap;

        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        return distance(lat1, lon1, lat2, lon2, 0,0);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static JSONObject getAddressFromMapResult(JSONObject object){
        JSONObject objectResult = new JSONObject();

        try {
            JSONArray array = object.getJSONArray("address_components");
            for (int i=0; i<array.length(); i++){
                JSONObject objectChild = array.getJSONObject(i);

                if (objectChild.getString("types").contains("route")){
                    objectResult.put("street",objectChild.getString("long_name") );

                }else if (objectChild.getString("types").contains("administrative_area_level_2")){
                    objectResult.put("district",objectChild.getString("long_name") );

                }else if (objectChild.getString("types").contains("administrative_area_level_1")){
                    objectResult.put("province",objectChild.getString("long_name") );

                }else if (objectChild.getString("types").contains("street_number")){
                    objectResult.put("address",objectChild.getString("long_name") );

                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        return objectResult;
    }

    public void addListMarkerToMap(final GoogleMap mMap, final ArrayList<Customer> listCustomer, Boolean isAll, Boolean isBound) {
        currentMap = mMap;
        markers = new ArrayList<>();

        for (int i = 0; i < listCustomer.size(); i++) {
            try {
                Customer customer = listCustomer.get(i);

                if (isAll){
                    markers.add(addMarkerToMap(mMap, customer));

                }else {
                    int markertype = new JSONObject(customer.getString("status")).getInt("id");
                    if (markertype == 1 || markertype == 3){
                        markers.add(addMarkerToMap(mMap, customer));
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (isBound){
            reboundMap();
        }

    }

    public static Marker addMarkerToMap(GoogleMap mMap, Customer customer ){
        Marker currentMarker = null;
        LatLng markerPosition = new LatLng(customer.getDouble("lat"), customer.getDouble("lng"));
        String title = Constants.getShopInfo(customer.getString("shopType"), null) +" " + customer.getString("signBoard");
        String add = customer.getString("address") + " " + customer.getString("street") ;

        try {
            int markertype = new JSONObject(customer.getString("status")).getInt("id");
            int checkinCount = 0;
            int icon=0;
            if (markertype ==1){
                icon = R.drawable.ico_pin_red;
                checkinCount = new JSONArray(customer.getString("checkIns")).length();
            }else if (markertype == 2){
                icon = R.drawable.ico_pin_grey;
                checkinCount = new JSONArray(customer.getString("checkIns")).length();
            }else {
                icon = R.drawable.ico_pin_blue;
                if (customer.getString("bills") != null && customer.getString("bills").equals("[]")){
                    checkinCount = new JSONArray(customer.getString("bills")).length();
                }

            }

            currentMarker = mMap.addMarker(new MarkerOptions().position(markerPosition).snippet(add).title(title));
            Bitmap bitmap = GetBitmapMarker(Util.getInstance().getCurrentActivity(), icon, String.valueOf(checkinCount), R.color.pin_waiting);
            currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            currentMarker.setTag(customer.CustomerJSONObject());

            markers.add(currentMarker);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currentMarker;
    }

    private void startDropMarkerAnimation(GoogleMap mMap, final Marker marker) {
        final LatLng target = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point targetPoint = proj.toScreenLocation(target);
        final long duration = (long) (200 + (targetPoint.y * 0.6));
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        startPoint.y = 0;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new LinearOutSlowInInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later == 60 frames per second
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public static void showUpdatedMarker(GoogleMap mMap, Customer customer){
        Boolean isNew = true;
        for (int i=0; i<markers.size(); i++){
            try {
                JSONObject object = new JSONObject(markers.get(i).getTag().toString());
                if (object.getString("id").equals(customer.getString("id"))){
                    Marker marker = markers.get(i);
                    marker.showInfoWindow();


                    isNew = false;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (isNew){
            Marker marker = addMarkerToMap(mMap, customer);
            marker.showInfoWindow();
        }
    }

}
