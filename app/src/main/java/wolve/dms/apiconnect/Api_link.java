package wolve.dms.apiconnect;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

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
    public final static String LUB_LINK = "http://dmslub.com?";
    public final static String HOST_IMAGE="http://lubsolution.com:8800";
    public final static String HOST_IMAGE_USER="paramaxgo";
    public final static String HOST_IMAGE_PASS="paramaxgo@1";

    public final static String GOOGLESHEET_CREDENTIALS_FILE_PATH = "/credentials.json";
    public final static String GOOGLESHEET_TOKENS_PATH = "tokens";
    public final static String STATISTICAL_SHEET_KEY ="1uooLXWdA6huFaBmRJl6Zs_1H9jOf4-2MVlord2busEU";
    public final static String STATISTICAL_SHEET_TAB ="%s!A%d:z";
    public final static String STATISTICAL_SHEET_TAB1 ="Sheet1!A%d:z";
    public final static String STATISTICAL_SHEET_TAB2 ="Sheet2!A%d:z";
    public final static String SCANNER_SHEET_KEY ="1sWcFlXG-7UZlrcBDFocIBlk3SxxRxplPsvELP0WWxzQ";
    public final static String SCANNER_DISTRIBUTOR_TAB ="DISTRIBUTOR!A1:z";
    public final static String SCANNER_CODE_TAB ="CODE!A%d:z";



    public final static String LOGO_BILL = "http://res.cloudinary.com/lubsolution/image/upload/v1514902531/wolver_logo.jpg";

    public final static String PROVINCES = BASE_URL + "provinces";
    public final static String DISTRICTS = BASE_URL + "districts?provinceid=";

    public final static String MAP_GET_ADDRESS = MAP_API + "geocode/json?latlng=%1$f,%2$f&sensor=false&language=vi&key=" + Util.getInstance().getCurrentActivity().getResources().getString(R.string.map_id);

    public final static String DEFAULT_RANGE ="?page=%d&size=%d";

    public final static String LOGIN = BASE_URL + "/login";
    public final static String LOGIN_PARAM ="phone=%s&password=%s";
    public final static String LOGOUT= BASE_URL + "auth/logout";
    public final static String LOGOUT_PARAM ="x-wolver-accesstoken=%s&x-wolver-accessid=%s";

    public final static String USER = BASE_URL + "token/user/";
    public final static String USER_PARAM = "password=%s&distributor.id=%d";

    public final static String PRODUCT_GROUPS = BASE_URL + "token/productGroups";
    public final static String PRODUCT_GROUP_NEW = BASE_URL + "token/productGroup/create_or_update";
    public final static String PRODUCT_GROUP_DELETE = BASE_URL + "token/productGroup/";
    public final static String PRODUCTGROUP_CREATE_PARAM ="%sname=%s";

    public final static String WAREHOUSES = BASE_URL + "token/warehouses";
    public final static String WAREHOUSE_NEW = BASE_URL + "token/warehouse/create_or_update";
    public final static String WAREHOUSE_CREATE_PARAM ="%sname=%s&user_id=%d&isMaster=%d";

    public final static String INVENTORIES = BASE_URL + "token/inventories";

    public final static String IMPORT_NEW = BASE_URL + "token/import/create_or_update";

    public final static String PRODUCTS = BASE_URL + "token/products";
    public final static String PRODUCT_NEW = BASE_URL + "token/product/create_or_update";
    public final static String PRODUCT_DELETE = BASE_URL + "token/product/";
    public final static String PRODUCT_CREATE_PARAM ="%sname=%s&promotion=%s&unitPrice=%s&purchasePrice=%s&volume=%s&productGroup.id=%d&image=%s";
    public final static String PRODUCT_LASTEST = BASE_URL +"token/lastproduct";

    public final static String USERS = BASE_URL + "token/users";
    public final static String USER_NEW = BASE_URL + "token/user/create_or_update";
    public final static String USER_DELETE = BASE_URL + "token/user/";
    public final static String USER_CREATE_PARAM ="%sdisplayName=%s&gender=%d&email=%s&phone=%s&role=%d&image=%s%s";

    public final static String STATUS = BASE_URL + "token/status";
    public final static String STATUS_NEW = BASE_URL + "token/status/create_or_update";
    public final static String STATUS_DELETE = BASE_URL + "token/status/";
    public final static String STATUS_CREATE_PARAM ="%sname=%s&color=%s&defaultStatus=%s";

    public final static String CUSTOMERS = BASE_URL + "token/customers";
    public final static String CUSTOMERS_NEAREST = BASE_URL + "token/customers/nearest";
    public final static String CUSTOMERS_HAVEDEBT = BASE_URL + "token/customers/havedebt";
    public final static String CUSTOMER_NEW = BASE_URL + "token/customer/create_or_update";
    public final static String CUSTOMER_DELETE = BASE_URL + "token/customer/";
    public final static String CUSTOMER_GETDETAIL = BASE_URL + "token/customer/";
    public final static String CUSTOMER_NEAREST_PARAM ="&lat=%s&lng=%s";
    public final static String CUSTOMER_CHECKIN_RANGE_PARAM ="&checkinFrom=%s&checkinTo=%s";
    public final static String CUSTOMER_DEBT_PARAM ="debt=%d";
    public final static String CUSTOMER_CREATE_PARAM ="%sname=%s&signBoard=%s&address=%s&phone=%s&street=%s&note=%s&district=%s&province=%s&lat=%s&lng=%s&volumeEstimate=%s&shopType=%s&status_id=%d&distributor_id=%s&currentDebt=%s&checkinCount=%d";

    public final static String DISTRIBUTOR_DETAIL = BASE_URL + "token/distributor/";
    public final static String DISTRIBUTOR_NEW = BASE_URL + "token/distributor/create_or_update";
    public final static String DISTRIBUTOR_CREATE_PARAM ="%scompany=%s&address=%s&phone=%s&website=%s&thanks=%s&image=%s";

    public final static String CHECKIN_NEW = BASE_URL + "token/customer/checkin";
    public final static String SCHECKIN_CREATE_PARAM ="customer_id=%d&status_id=%d&note=%s&user_id=%d";

    public final static String CHECK_LOGIN = BASE_URL + "token/checklogin";
    public final static String CATEGORIES = BASE_URL + "token/categories";
    public final static String STATISTICALS = BASE_URL + "token/statistical";
    public final static String STATISTICAL_PARAM ="?from=%s&to=%s";

    public final static String BILLS = BASE_URL + "token/bills";
    public final static String BILLS_HAVE_PAYMENT = BASE_URL + "token/bills/have_payment";
    public final static String BILLS_NOT_YET_PAID = BASE_URL + "token/bills_not_yet_paid";
    public final static String BILL_NEW = BASE_URL + "token/bill/create_or_update";
    public final static String BILL_DELETE = BASE_URL + "token/bill/";
    public final static String BILL_RANGE_PARAM ="&billingFrom=%s&billingTo=%s";
    public final static String BILL_HAVE_PAYMENT_RANGE_PARAM ="&paymentFrom=%s&paymentTo=%s";
    public final static String BILL_NOT_YET_PAID_RANGE_PARAM ="&fromTime=%s&toTime=%s";

    public final static String PAY_NEW = BASE_URL + "token/payment";
    public final static String PAYMENTS = BASE_URL + "token/payments";
    public final static String PAY_PARAM ="customerId=%d&paid=%s&billId=%d&userId=%d&note=%s&payByReturn=%d&user_collect=%d";
    public final static String PAYMENTS_PARAM ="?from=%s&to=%s";

    public final static String DEBT_NEW = BASE_URL + "token/debt/create_or_update";
    public final static String DEBT_PARAM ="debt=%s&user_id=%d&customer_id=%d&distributor_id=%d";



    public final static String NOTE_PARAM = "http://lubsolution.com/";

    public final static String LUB_LINK_PARAM ="id=%s";

    public static Cloudinary getImageCloud() {
        Map config = new HashMap();
        config.put("cloud_name", "lubsolution");
        config.put("api_key", "482386522287271");
        config.put("api_secret", "Mh2EsnmYHBAsTAp7jsNLoJ5dXhk");
        Cloudinary cloudinary = new Cloudinary(config);

        return cloudinary;
    }

}



