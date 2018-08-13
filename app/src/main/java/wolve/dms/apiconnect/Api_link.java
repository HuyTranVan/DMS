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
//    public final static String GOOGLESHEET_KEY ="1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//    public final static String GOOGLESHEET_TAB ="Class Data!A2:E8";
    public final static String GOOGLESHEET_KEY ="1uooLXWdA6huFaBmRJl6Zs_1H9jOf4-2MVlord2busEU";
    public final static String GOOGLESHEET_TAB ="Sheet1!A1:d";
    public final static String GOOGLESHEET_CREDENTIALS_FILE_PATH = "/credentials.json";
    public final static String GOOGLESHEET_TOKENS_PATH = "tokens";

    public final static String LOGO_BILL = "http://res.cloudinary.com/lubsolution/image/upload/v1514902531/wolver_logo.jpg";

    public final static String PROVINCES = BASE_URL + "provinces";
    public final static String DISTRICTS = BASE_URL + "districts?provinceid=";

    public final static String MAP_GET_ADDRESS = MAP_API + "geocode/json?latlng=%1$f,%2$f&sensor=false&language=vi&key=" + Util.getInstance().getCurrentActivity().getResources().getString(R.string.map_id);

    public final static String LOGIN = BASE_URL + "auth/login";
    public final static String LOGIN_PARAM ="phone=%s&password=%s";
    public final static String LOGOUT= BASE_URL + "auth/logout";
    public final static String LOGOUT_PARAM ="x-wolver-accesstoken=%s&x-wolver-accessid=%s";

    public final static String PRODUCT_GROUPS = BASE_URL + "token/productGroups";
    public final static String PRODUCT_GROUP_NEW = BASE_URL + "token/productGroup/create_or_update";
    public final static String PRODUCT_GROUP_DELETE = BASE_URL + "token/productGroup/";
    public final static String PRODUCTGROUPS_PARAM ="?page=%d&size=%d";
    public final static String PRODUCTGROUP_CREATE_PARAM ="%sname=%s";

    public final static String PRODUCTS = BASE_URL + "token/products";
    public final static String PRODUCT_NEW = BASE_URL + "token/product/create_or_update";
    public final static String PRODUCT_DELETE = BASE_URL + "token/product/";
    public final static String PRODUCTS_PARAM ="?page=%d&size=%d";
    public final static String PRODUCT_CREATE_PARAM ="%sname=%s&promotion=%s&unitPrice=%s&purchasePrice=%s&volume=%s&productGroup.id=%d&image=%s";

    public final static String STATUS = BASE_URL + "token/status";
    public final static String STATUS_NEW = BASE_URL + "token/status/create_or_update";
    public final static String STATUS_DELETE = BASE_URL + "token/status/";
    public final static String STATUS_PARAM ="?page=%d&size=%d";
    public final static String STATUS_CREATE_PARAM ="%sname=%s&color=%s&defaultStatus=%s";

    public final static String CUSTOMERS = BASE_URL + "token/customers";
    public final static String CUSTOMERS_NEAREST = BASE_URL + "token/customers/nearest";
    public final static String CUSTOMER_NEW = BASE_URL + "token/customer/create_or_update";
    public final static String CUSTOMER_DELETE = BASE_URL + "token/customer/";
    public final static String CUSTOMER_GETDETAIL = BASE_URL + "token/customer/";
    public final static String CUSTOMER_PARAM ="?page=%d&size=%d";
    public final static String CUSTOMER_NEAREST_PARAM ="?lat=%s&lng=%s&page=%d&size=%d";
    public final static String CUSTOMER_CREATE_PARAM ="%sname=%s&signBoard=%s&address=%s&phone=%s&street=%s&note=%s&district=%s&province=%s&lat=%s&lng=%s&volumeEstimate=%s&shopType=%s&status.id=%d&distributor.id=%s";

    public final static String CHECKIN_NEW = BASE_URL + "token/customer/checkin";
    public final static String SCHECKIN_CREATE_PARAM ="customer.id=%d&status.id=%d&note=%s&user.id=%s";

    public final static String BILLS = BASE_URL + "token/bills";
    public final static String BILL_NEW = BASE_URL + "token/bill/create_or_update";
    public final static String BILL_DELETE = BASE_URL + "token/bill/";
    public final static String BILL_PARAM ="?page=%d&size=%d";


}



