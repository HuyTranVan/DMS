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
import android.util.Log;
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
    private List<Marker> markers;





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

            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            currentMap.moveCamera(cu);
        }
//        else {
//            Marker marker = markers.get(0);
//            CameraUpdate cu = CameraUpdateFactory.newLatLng(marker.getPosition());
//            currentMap.animateCamera(cu);
//        }
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

//    public static void checkGetLocationGotoMap() {
//        //Util.getInstance().showLoading("Đang kiểm tra vị trí...");
//        if (Util.getInstance().getCurrentLocation() != null) {
//            Transaction.gotoMapPage(Util.getInstance().getCurrentActivity(), Constants.FROM_LISTING_ACTIVITY);
//        } else {
//            final Handler h = new Handler();
//            h.postDelayed(new Runnable() {
//                public void run() {
//                    if (Util.getInstance().getCurrentLocation() != null) {
//                        Transaction.gotoMapPage(Util.getInstance().getCurrentActivity(), Constants.FROM_LISTING_ACTIVITY);
//                    } else {
//                        h.postDelayed(this, 1000);
//                    }
//                }
//            }, 1000);
//        }
//    }


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

    public void addMarkerToMap(final GoogleMap mMap, final ArrayList<Customer> listCustomer, LatLng currentlatlng, Boolean isAll) {
        currentMap = mMap;
        markers = new ArrayList<>();

        for (int i = 0; i < listCustomer.size(); i++) {
            try {
                Customer customer = listCustomer.get(i);

                LatLng markerPoint = new LatLng(customer.getDouble("lat"), customer.getDouble("lng"));
                String title = Constants.getShopInfo(customer.getString("shopType"), null) +" " + customer.getString("signBoard");
                String add = customer.getString("address") + " " + customer.getString("street") ;

                int markertype = new JSONObject(customer.getString("status")).getInt("id");
                int icon=0;
                if (markertype ==1){
                    icon = R.drawable.ico_pin_red;
                }else if (markertype == 2){
                    icon = R.drawable.ico_pin_grey;
                }else {
                    icon = R.drawable.ico_pin_blue;
                }

                if (isAll){
                    Marker pointMarker = currentMap.addMarker(new MarkerOptions().position(markerPoint).snippet(add).title(title));
                    Bitmap bitmap = GetBitmapMarker(Util.getInstance().getCurrentActivity(), icon, customer.getString("checkinCount"), R.color.pin_waiting);
                    pointMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    pointMarker.setTag(customer.CustomerJSONObject());

                    markers.add(pointMarker);

                    if (currentlatlng != null && currentlatlng.latitude == customer.getDouble("lat") &&  currentlatlng.longitude == customer.getDouble("lng")){
                        pointMarker.showInfoWindow();
                    }

                }else {
                    if (markertype == 1 || markertype == 3){
                        Marker pointMarker = currentMap.addMarker(new MarkerOptions().position(markerPoint).snippet(add).title(title));
                        Bitmap bitmap = GetBitmapMarker(Util.getInstance().getCurrentActivity(), icon, customer.getString("checkinCount"), R.color.pin_waiting);
                        pointMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        pointMarker.setTag(customer.CustomerJSONObject());

                        markers.add(pointMarker);

                        if (currentlatlng != null && currentlatlng.latitude == customer.getDouble("lat") &&  currentlatlng.longitude == customer.getDouble("lng")){
                            pointMarker.showInfoWindow();
                        }
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



        reboundMap();
    }

}
