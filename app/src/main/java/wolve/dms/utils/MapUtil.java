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
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

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
    //private GoogleMap currentMap;
    public static List<Marker> markers ;
    public static List<Customer> customers ;
    private static int interested =0;
    private static int ordered =0;

    public static synchronized MapUtil getInstance() {
        if (util == null)
            util = new MapUtil();

        return util;
    }

    public static void resetMarker(){
        customers = new ArrayList<>();
        if (markers != null && markers.size()>0){
            for (int i =0 ; i<markers.size(); i++){
                markers.get(i).remove();
            }
        }
        markers = new ArrayList<>();
        interested =0;
        ordered =0;
    }

    public static void reboundMap(GoogleMap mMap) {
        if (markers.size() >= 1) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cu);
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
            paint.setColor(textColor);
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

    public static void addListMarkertoMap(Boolean clearMap, final GoogleMap mMap, final List<Customer> listCustomer, String filter, Boolean isBound) {
        if (clearMap){
            resetMarker();
            customers = listCustomer;
            for (int i = 0; i < customers.size(); i++) {
                addMarkerToMap(mMap, customers.get(i), filter);

//                switch (filter){
//                    case Constants.MARKER_ALL:
//                        addMarkerToMap(mMap, customers.get(i), true);
//                        if (customers.get(i).getInt("status") == 1 || customers.get(i).getInt("status") == 3){
//                            addMarkerToMap(mMap, customers.get(i), true);
//                            interested +=1;
//                        }else if (customers.get(i).getInt("status") == 3){
//                            addMarkerToMap(mMap, customers.get(i), true);
//                            ordered +=1;
//                        }
//                        break;
//
//                    case Constants.MARKER_INTERESTED:
//                        if (customers.get(i).getInt("status") == 1 || customers.get(i).getInt("status") == 3){
//                            addMarkerToMap(mMap, customers.get(i), true);
//                            interested +=1;
//                        }else {
//                            addMarkerToMap(mMap, listCustomer.get(i), false);
//                        }
//                        break;
//
//                    case Constants.MARKER_ORDERED:
//                        if (customers.get(i).getInt("status") == 3){
//                            addMarkerToMap(mMap, customers.get(i), true);
//                            ordered +=1;
//                        }else {
//                            addMarkerToMap(mMap, listCustomer.get(i), false);
//                        }
//                        break;
//                }
            }

        }else if (listCustomer.size() >0){
            if (customers.size() >0){
                for (int a=0; a<listCustomer.size(); a++){
                    Boolean check = false;

                    for (int b=0; b<customers.size(); b++){
                        int nu1 = listCustomer.get(a).getInt("id");
                        int nu2 = customers.get(b).getInt("id");
                        if (nu1 == nu2){
                            check = true;
                            break;
                        }
                    }

                    if (!check){
                        customers.add(listCustomer.get(a));
                        addMarkerToMap(mMap, listCustomer.get(a), filter);

//                        switch (filter){
//                            case Constants.MARKER_ALL:
//                                addMarkerToMap(mMap, listCustomer.get(a), true);
//                                if (listCustomer.get(a).getInt("status") == 1 || listCustomer.get(a).getInt("status") == 3){
//                                    addMarkerToMap(mMap, listCustomer.get(a), true);
//                                    interested +=1;
//                                }else if (listCustomer.get(a).getInt("status") == 3){
//                                    addMarkerToMap(mMap, listCustomer.get(a), true);
//                                    ordered +=1;
//                                }
//                                break;
//
//                            case Constants.MARKER_INTERESTED:
//                                if (listCustomer.get(a).getInt("status") == 1 || listCustomer.get(a).getInt("status") == 3){
//                                    addMarkerToMap(mMap, listCustomer.get(a), true);
//                                    interested +=1;
//                                }else {
//                                    addMarkerToMap(mMap, listCustomer.get(a), false);
//                                }
//                                break;
//
//                            case Constants.MARKER_ORDERED:
//                                if (listCustomer.get(a).getInt("status") == 3){
//                                    addMarkerToMap(mMap, listCustomer.get(a), true);
//                                    ordered +=1;
//                                }else {
//                                    addMarkerToMap(mMap, listCustomer.get(a), false);
//                                }
//                                break;
//                        }
                    }

                }
            }else {
                customers = listCustomer;
                for (int i=0; i<customers.size(); i++){
                    addMarkerToMap(mMap, customers.get(i), filter);
//                    switch (filter){
//                        case Constants.MARKER_ALL:
//                            addMarkerToMap(mMap, customers.get(i), true);
//                            if (customers.get(i).getInt("status") == 1 || customers.get(i).getInt("status") == 3){
//                                addMarkerToMap(mMap, customers.get(i), true);
//                                interested +=1;
//                            }else if (customers.get(i).getInt("status") == 3){
//                                addMarkerToMap(mMap, customers.get(i), true);
//                                ordered +=1;
//                            }
//
//                            break;
//
//                        case Constants.MARKER_INTERESTED:
//                            if (customers.get(i).getInt("status") == 1 || customers.get(i).getInt("status") == 3){
//                                addMarkerToMap(mMap, customers.get(i), true);
//                                interested +=1;
//                            }else {
//                                addMarkerToMap(mMap, customers.get(i), false);
//                            }
//                            break;
//
//                        case Constants.MARKER_ORDERED:
//                            if (customers.get(i).getInt("status") == 3){
//                                addMarkerToMap(mMap, customers.get(i), true);
//                                ordered +=1;
//                            }else {
//                                addMarkerToMap(mMap, customers.get(i), false);
//                            }
//                            break;
//                    }
                }
            }


        }

        if (isBound){
            reboundMap(mMap);
        }

//        return String.format("%d/%d",interested, customers.size());
    }

    public static String countAll(){
        if (customers.size() >0)
            return String.format("Tất cả: %d", customers.size());
        return "Tất cả";
    }

    public static String countInterested(){
        if (interested >0)
            return String.format("Quan tâm: %d", interested);
        return "Quan tâm";
    }

    public static String countOrdered(){
        if (ordered >0)
            return String.format("Đã mua: %d", ordered);
        return "Đã mua";
    }


    public static Marker addMarkerToMap(GoogleMap mMap, Customer customer ,String filter){
        Marker currentMarker = null;

        LatLng markerPosition = new LatLng(customer.getDouble("lat"), customer.getDouble("lng"));
        currentMarker = mMap.addMarker(new MarkerOptions().position(markerPosition));
        Bitmap bitmap = GetBitmapMarker(Util.getInstance().getCurrentActivity(), customer.getInt("icon"), customer.getString("checkincount"), R.color.pin_waiting);
        currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        currentMarker.setTag(customer.CustomerJSONObject());
        currentMarker.setSnippet(customer.getString("id"));

        markers.add(currentMarker);

        switch (filter){
            case Constants.MARKER_ALL:
                currentMarker.setVisible(true);
                break;

            case Constants.MARKER_INTERESTED:
                if (customer.getInt("status") == 1 || customer.getInt("status") == 3){
                    currentMarker.setVisible(true);
                }else if (customer.getInt("status") == 3){
                    currentMarker.setVisible(true);
                }else {
                    currentMarker.setVisible(false);
                }
                break;

            case Constants.MARKER_ORDERED:
                if (customer.getInt("status") == 3){
                    currentMarker.setVisible(true);
                }else {
                    currentMarker.setVisible(false);
                }
                break;
        }

        if (customer.getInt("status") == 1 || customer.getInt("status") == 3){
            interested +=1;
        }
        if (customer.getInt("status") == 3){
            ordered +=1;
        }

        return currentMarker;
    }

    private void startDropMarkerAnimation(GoogleMap mMap, final Marker marker) {
        final LatLng target = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point targetPoint = proj.toScreenLocation(target);
        final long duration = (long) (500 + (targetPoint.y * 0.6));
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
                if (markers.get(i).getTag() != null){
                    JSONObject object = new JSONObject(markers.get(i).getTag().toString());
                    if (object.getString("id").equals(customer.getString("id"))){
                        markers.get(i).remove();

                        Marker marker = addMarkerToMap(mMap, customer, Constants.MARKER_ALL);
                        marker.showInfoWindow();


                        isNew = false;
                        break;
                    }
                }

            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }

        if (isNew){
            Marker marker = addMarkerToMap(mMap, customer, Constants.MARKER_ALL);
            marker.showInfoWindow();
            isNew = false;
        }
    }

    public static void removeMarker(String id){
        for (int i=0; i<markers.size(); i++){
            try {
                if (markers.get(i).getTag() != null){
                    JSONObject object = new JSONObject(markers.get(i).getTag().toString());
                    if (object.getString("id").equals(id)){
                        markers.get(i).remove();
                        break;
                    }
                }

            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
    }

    public static void changeFragmentHeight(Fragment fragment, int height) {
        ViewGroup.LayoutParams params = fragment.getView().getLayoutParams();
        params.height = height;
        fragment.getView().setLayoutParams(params);
    }

    public static String updateCustomerFilter(String filter){
        if (markers != null)
            switch (filter){
                case Constants.MARKER_ALL:
                    for (int i=0; i<markers.size(); i++){
                        if (!markers.get(i).isVisible()){
                            markers.get(i).setVisible(true);
                        }
                    }

                    break;

                case Constants.MARKER_INTERESTED:
                    for (int i=0; i<markers.size(); i++){
                        try {
                            if (markers.get(i).getTag() != null){
                                JSONObject object = new JSONObject(markers.get(i).getTag().toString());
                                if (object.getInt("status") !=1 && object.getInt("status") !=3){
                                    markers.get(i).setVisible(false);
                                    //interested -=1;

                                }else if (!markers.get(i).isVisible()){
                                    markers.get(i).setVisible(true);
                                    //interested +=1;
                                }
                            }

                        } catch (JSONException e) {
    //                e.printStackTrace();
                        }
                    }

                    break;

                case Constants.MARKER_ORDERED:
                    for (int i=0; i<markers.size(); i++){
                        try {
                            if (markers.get(i).getTag() != null){
                                JSONObject object = new JSONObject(markers.get(i).getTag().toString());
                                if (object.getInt("status") !=3){
                                    markers.get(i).setVisible(false);
                                    //interested -=1;

                                }else if (!markers.get(i).isVisible()){
                                    markers.get(i).setVisible(true);
                                    //interested +=1;
                                }
                            }

                        } catch (JSONException e) {
    //                e.printStackTrace();
                        }
                    }

                    break;
            }
        interested =0;
        if (markers != null){
            for (int i=0; i<markers.size(); i++){
                if (markers.get(i).isVisible()){
                    interested +=1;
                }
            }
        }

        return String.format("%d/%d", interested, customers.size());
    }

}
