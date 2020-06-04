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
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

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
import wolve.dms.libraries.DrawRoute;
import wolve.dms.models.BaseModel;

/**
 * Created by Engine on 1/13/2017.
 */

public class MapUtil {
    private static MapUtil util;
    private long totalDistance;
    private long totalDuration;
    private int step;
    public Marker currentMarker;
    private int currentStep;
    //private GoogleMap currentMap;
    public static List<Marker> markers;
    public static List<BaseModel> customers;
    private static int interested = 0;
    private static int ordered = 0;

    public static synchronized MapUtil getInstance() {
        if (util == null)
            util = new MapUtil();

        return util;
    }

    public static void resetMarker() {
        customers = new ArrayList<>();
        if (markers != null && markers.size() > 0) {
            for (int i = 0; i < markers.size(); i++) {
                markers.get(i).remove();
            }
        }
        markers = new ArrayList<>();
        interested = 0;
        ordered = 0;
    }

    public static void reboundMap(GoogleMap mMap, List<Marker> listMarker) {
        if (listMarker.size() >= 1) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : listMarker) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cu);
        }

    }

    public static Bitmap GetBitmapMarker(Context mContext, int resourceId, String mText, int textColor) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

            Bitmap.Config bitmapConfig = bitmap.getConfig();

            // set default bitmap config if none
            if (bitmapConfig == null)
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

        } catch (Exception e) {
            return null;
        }
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        return distance(lat1, lon1, lat2, lon2, 0, 0, unit);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        return distance(lat1, lon1, lat2, lon2, 0, 0, "m");
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, double el1, double el2, String unit) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = unit.equals("m") ? R * c * 1000 : R * c; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static BaseModel getAddressFromMapResult(BaseModel object) {
        BaseModel objectResult = new BaseModel();
        objectResult.put("address", "");
        objectResult.put("street", "");
        objectResult.put("district", "");
        objectResult.put("province", "");

        try {
            JSONArray array = object.getJSONArray("address_components");
            for (int i = 0; i < array.length(); i++) {
                JSONObject objectChild = array.getJSONObject(i);


                if (objectChild.getString("types").contains("route")) {
                    objectResult.put("street", objectChild.isNull("long_name") ? "--" : objectChild.getString("long_name"));

                } else if (objectChild.getString("types").contains("administrative_area_level_2") || objectChild.getString("types").contains("locality")) {
                    objectResult.put("district", objectChild.isNull("long_name") ? "--" : objectChild.getString("long_name").replace("thành phố ", ""));

                } else if (objectChild.getString("types").contains("administrative_area_level_1")) {
                    objectResult.put("province", objectChild.isNull("long_name") ? "--" : objectChild.getString("long_name"));

                } else if (objectChild.getString("types").contains("street_number")) {
                    objectResult.put("address", objectChild.getString("long_name"));

                }
            }


        } catch (JSONException e) {
//            e.printStackTrace();
            return objectResult;
        }

        return objectResult;
    }

    public static void addListMarkertoMap(Boolean clearMap, final GoogleMap mMap, final List<BaseModel> listCustomer, String filter, Boolean isBound) {
        if (clearMap) {
            resetMarker();
            customers = listCustomer;
            for (int i = 0; i < customers.size(); i++) {
                addMarkerToMap(mMap, customers.get(i), filter);

            }

        } else if (listCustomer.size() > 0) {
            if (customers.size() > 0) {
                for (int a = 0; a < listCustomer.size(); a++) {
                    Boolean check = false;

                    for (int b = 0; b < customers.size(); b++) {
                        int nu1 = listCustomer.get(a).getInt("id");
                        int nu2 = customers.get(b).getInt("id");
                        if (nu1 == nu2) {
                            check = true;
                            break;
                        }
                    }

                    if (!check) {
                        customers.add(listCustomer.get(a));
                        addMarkerToMap(mMap, listCustomer.get(a), filter);

                    }

                }
            } else {
                customers = listCustomer;
                for (int i = 0; i < customers.size(); i++) {
                    addMarkerToMap(mMap, customers.get(i), filter);
                }
            }


        }

//        if (isBound){
//            reboundMap(mMap);
//        }

    }

    public static String countAll() {
        if (customers.size() > 0)
            return String.format("Tất cả: %d", customers.size());
        return "Tất cả";
    }

    public static String countInterested() {
        if (interested > 0)
            return String.format("Quan tâm: %d", interested);
        return "Quan tâm";
    }

    public static String countOrdered() {
        if (ordered > 0)
            return String.format("Đã mua: %d", ordered);
        return "Đã mua";
    }

    public static Marker addMarkerToMap(GoogleMap mMap, BaseModel customer, String filter) {
        Marker currentMarker = null;

        LatLng markerPosition = new LatLng(customer.getDouble("lat"), customer.getDouble("lng"));
        currentMarker = mMap.addMarker(new MarkerOptions().position(markerPosition));
        Bitmap bitmap = GetBitmapMarker(Util.getInstance().getCurrentActivity(), customer.getInt("icon"), customer.getString("checkincount"), R.color.pin_waiting);
        currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        currentMarker.setTag(customer.BaseModelJSONObject());
        currentMarker.setTitle(customer.getString("id"));    //customer_id
        currentMarker.setSnippet(customer.getString("status_id"));     //status_id

        //markers.add(currentMarker);

        switch (filter) {
            case Constants.MARKER_ALL:
                currentMarker.setVisible(true);
                break;

            case Constants.MARKER_INTERESTED:
                if (customer.getInt("status_id") == 0
                        || customer.getInt("status_id") == 1
                        || customer.getInt("status_id") == 3) {
                    currentMarker.setVisible(true);

                } else {
                    currentMarker.setVisible(false);
                }
                break;

            case Constants.MARKER_ORDERED:
                if (customer.getInt("status_id") == 3) {
                    currentMarker.setVisible(true);
                } else {
                    currentMarker.setVisible(false);
                }
                break;
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

    public static void showUpdatedMarker(GoogleMap mMap, BaseModel customer) {
        Boolean isNew = true;
        for (int i = 0; i < markers.size(); i++) {
            try {
                if (markers.get(i).getTag() != null) {
                    JSONObject object = new JSONObject(markers.get(i).getTag().toString());
                    if (object.getString("id").equals(customer.getString("id"))) {
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

        if (isNew) {
            Marker marker = addMarkerToMap(mMap, customer, Constants.MARKER_ALL);
            marker.showInfoWindow();
            isNew = false;
        }
    }

    public static Marker getCurrentMarker(GoogleMap mMap, BaseModel customer) {
        Boolean isNew = true;
        Marker currentmarker = null;
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).getTag() != null) {
                BaseModel object = new BaseModel(markers.get(i).getTag().toString());
                if (object.getString("id").equals(customer.getString("id"))) {
                    markers.get(i).remove();

                    currentmarker = addMarkerToMap(mMap, customer, Constants.MARKER_ALL);


                    isNew = false;
                    break;
                }
            }

        }

        if (isNew) {
            currentmarker = addMarkerToMap(mMap, customer, Constants.MARKER_ALL);
//            marker.showInfoWindow();
//            isNew = false;
        }

        return currentmarker;
    }

    public static void removeMarker(String id) {
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).getTag() != null) {
                BaseModel object = new BaseModel(markers.get(i).getTag().toString());
                if (object.getString("id").equals(id)) {
                    markers.get(i).remove();
                    CustomSQL.removeKey(Constants.CUSTOMER);
                    break;
                }
            }


        }
    }

    public static void changeFragmentHeight(Fragment fragment, int height) {
        ViewGroup.LayoutParams params = fragment.getView().getLayoutParams();
        params.height = height;
        fragment.getView().setLayoutParams(params);
    }

    public static void updateCustomerFilter(List<Marker> list, String filter) {
        if (list.size() > 0)
            switch (filter) {
                case Constants.MARKER_ALL:
                    for (Marker marker : list) {
                        marker.setVisible(true);
                    }
                    break;

                case Constants.MARKER_INTERESTED:
                    for (Marker marker : list) {
                        if (marker.getSnippet().equals("0") || marker.getSnippet().equals("1") || marker.getSnippet().equals("3")) {
                            marker.setVisible(true);

                        } else {
                            marker.setVisible(false);

                        }
                    }

                    break;

                case Constants.MARKER_ORDERED:
                    for (Marker marker : list) {
                        if (marker.getSnippet().equals("3")) {
                            marker.setVisible(true);

                        } else {
                            marker.setVisible(false);

                        }
                    }
                    break;
            }

    }

    public static void drawRoute(GoogleMap mMap, Location currentLocation, List<Marker> markers) {
        LatLng lastMarkerPoint = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //lastMarkerPoint = markerPoint;

        for (int i = 0; i < markers.size(); i++) {
            DrawRoute drawRoute = DrawRoute.getInstance(Util.getInstance().getCurrentActivity());
            drawRoute.setLoader(false);
            drawRoute.callInterface = new DrawRoute.onDrawRoute() {
                @Override
                public void afterDraw(String result) {
                    String s = result;
//                try {
//                    JSONObject listingPoint = relatedListingList.getJSONObject(count);
//                    JSONObject resultObject = new JSONObject(result);
//                    if (!resultObject.has("error_message")) {
//                        JSONArray routes = resultObject.getJSONArray("routes");
//                        JSONObject route = routes.getJSONObject(0);
//
//                        JSONArray newTempARr = route.getJSONArray("legs");
//                        JSONObject newDisTimeOb = newTempARr.getJSONObject(0);
//
//                        JSONObject distOb = newDisTimeOb.getJSONObject("distance");
//                        JSONObject timeOb = newDisTimeOb.getJSONObject("duration");
//
//                        int duration = timeOb.getInt("value") + delayTime;
//
//                        listingPoint.put("distance", distOb.getString("value"));
//                        listingPoint.put("duration", String.valueOf(duration));
//
//                        totalDistance += distOb.getInt("value");  // In metres
//                        totalDuration += duration;  // In seconds
//
//                    } else {
//                        Util.alert("Lỗi", resultObject.getString("error_message"), "Đóng");
//                    }
//                    if (count == lastRunPoint) {
//                        drawLastPointBack(context, mMap, lastMarkerPoint);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                }
            };
            BaseModel customer = new BaseModel(markers.get(i).getTag().toString());
            LatLng currentPoint = new LatLng(customer.getDouble("lat"), customer.getDouble("lng"));
            drawRoute.setFromLatLong(lastMarkerPoint.latitude, lastMarkerPoint.longitude);
            drawRoute.setToLatLong(currentPoint.latitude, currentPoint.longitude);
            drawRoute.setGmapAndKey(Util.getInstance().getCurrentActivity().getResources().getString(R.string.map_route), mMap);
            drawRoute.run();

            lastMarkerPoint = currentPoint;

        }


    }

}
