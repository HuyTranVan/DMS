package wolve.dms.apiconnect;

import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/14/17.
 */

public class Api_link {
    public final static String BASE_URL = BuildConfig.SERVER_URL;
    public final static String BASE_PHOTO_URL = BuildConfig.UPLOAD_URL;
    public final static String MAP_API = "https://maps.googleapis.com/maps/api/";

    public final static String PROVINCES = BASE_URL + "provinces";
    public final static String DISTRICTS = BASE_URL + "districts?provinceid=";

    public final static String MAP_GET_ADDRESS = MAP_API + "geocode/json?latlng=%1$f,%2$f&sensor=false&language=vi&key=" + Util.getInstance().getCurrentActivity().getResources().getString(R.string.map_id);

    public final static String LOGIN = BASE_URL + "auth/login";

    public final static String PRODUCT_GROUPS = BASE_URL + "token/productGroups";
    public final static String PRODUCT_GROUP_NEW = BASE_URL + "token/productGroup/create_or_update";
    public final static String PRODUCT_GROUP_DELETE = BASE_URL + "token/productGroup/";

    public final static String PRODUCTS = BASE_URL + "token/products";
    public final static String PRODUCT_NEW = BASE_URL + "token/product/create_or_update";
    public final static String PRODUCT_DELETE = BASE_URL + "token/product/";

    public final static String STATUS = BASE_URL + "token/status";
    public final static String STATUS_NEW = BASE_URL + "token/status/create_or_update";
    public final static String STATUS_DELETE = BASE_URL + "token/status/";

    public final static String CUSTOMERS = BASE_URL + "token/customers";
    public final static String CUSTOMERS_NEAREST = BASE_URL + "token/customers/nearest";
    public final static String CUSTOMER_NEW = BASE_URL + "token/customer/create_or_update";
    public final static String CUSTOMER_DELETE = BASE_URL + "token/customer/";
    public final static String CUSTOMER_GETDETAIL = BASE_URL + "token/customer/";

    public final static String CHECKIN_NEW = BASE_URL + "token/customer/checkin";



}



