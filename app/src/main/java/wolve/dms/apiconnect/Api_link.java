package wolve.dms.apiconnect;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

import wolve.dms.BuildConfig;
import wolve.dms.R;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/14/17.
 */

public class Api_link {
    public final static String DEFAULT_RANGE ="?page=%d&size=%d";

    public final static String BASE_URL= BuildConfig.DEBUG_FLAG? "http://192.168.1.45/": BuildConfig.SERVER_URL;
    public final static String LUB_LINK = "http://dmslub.com?";

    public final static String BASE_PHOTO_URL = BuildConfig.UPLOAD_URL;
    public final static String HOST_IMAGE="http://lubsolution.com:8800";
    public final static String HOST_IMAGE_USER="paramaxgo";
    public final static String HOST_IMAGE_PASS="paramaxgo@1";

    public final static String MAP_API = "https://maps.googleapis.com/maps/api/";
    public final static String MAP_GET_ADDRESS = MAP_API + "geocode/json?latlng=%1$f,%2$f&sensor=false&language=vi&key=" + Util.getInstance().getCurrentActivity().getResources().getString(R.string.map_id);

    public final static String GOOGLESHEET_CREDENTIALS_FILE_PATH = "/credentials.json";
    public final static String GOOGLESHEET_TOKENS_PATH = "tokens";
    public final static String STATISTICAL_SHEET_KEY = Util.getInstance().getCurrentActivity().getResources().getString(R.string.sheet_key1);
    public final static String STATISTICAL_SHEET_TAB ="%s!A%d:z";
    public final static String STATISTICAL_SHEET_TAB1 ="Sheet1!A%d:z";
    public final static String STATISTICAL_SHEET_TAB2 ="Sheet2!A%d:z";
    public final static String SCANNER_SHEET_KEY = Util.getInstance().getCurrentActivity().getResources().getString(R.string.sheet_key2);
    public final static String SCANNER_DISTRIBUTOR_TAB ="DISTRIBUTOR!A1:z";
    public final static String SCANNER_CODE_TAB ="CODE!A%d:z";

    public final static String PROVINCES = BASE_URL + "dms/token/util/provinces";
    public final static String DISTRICTS = BASE_URL + "dms/token/util/districts?provinceid=";

    public final static String LOGIN = BASE_URL + "dms/token/system/Login";
    public final static String LOGIN_PARAM ="phone=%s&password=%s&fcm_token=%s";
    public final static String LOGOUT= BASE_URL + "dms/token/system/Logout";

    public final static String PRODUCT_GROUPS = BASE_URL + "dms/token/productgroup/ProductgroupList";
    public final static String PRODUCT_GROUP_NEW = BASE_URL + "dms/token/productgroup/ProductgroupNew";
    public final static String PRODUCT_GROUP_DELETE = BASE_URL + "dms/token/productgroup/ProductgroupDelete?id=";
    public final static String PRODUCTGROUP_CREATE_PARAM ="%sname=%s";

    public final static String WAREHOUSES = BASE_URL + "dms/token/warehouse/WarehouseList";
    public final static String WAREHOUSE_NEW = BASE_URL + "dms/token/warehouse/WarehouseNew";
    public final static String WAREHOUSE_CREATE_PARAM ="%sname=%s&user_id=%d&isMaster=%d";

    public final static String INVENTORIES = BASE_URL + "dms/token/warehouse/InventoryList";
    public final static String INVENTORY_EDIT_QUANTITY = BASE_URL + "dms/token/warehouse/InventoryEditQuantity";
    public final static String INVENTORY_EDIT_QUANTITY_PARAM ="id=%d&quantity=%d";

    public final static String IMPORT_NEW = BASE_URL + "dms/token/warehouse/ImportNew";
    public final static String IMPORTS = BASE_URL + "dms/token/warehouse/ImportList";
    public final static String IMPORT_DELETE = BASE_URL + "dms/token/warehouse/ImportDelete?id=";

    public final static String PRODUCTS = BASE_URL + "dms/token/product/ProductList";
    public final static String PRODUCT_NEW = BASE_URL + "dms/token/product/ProductNew";
    public final static String PRODUCT_DELETE = BASE_URL + "dms/token/product/ProductDelete?id=";
    public final static String PRODUCT_CREATE_PARAM ="%sname=%s&promotion=%s&unitPrice=%s&purchasePrice=%s&volume=%s&productGroup.id=%d&image=%s&basePrice=%s&unitInCarton=%s";
    public final static String PRODUCT_LASTEST = BASE_URL +"dms/token/system/LastProductUpdate";

    public final static String USERS = BASE_URL + "dms/token/user/UserList";
    public final static String USER_NEW = BASE_URL + "dms/token/user/UserNew";
    public final static String USER_CHANGE_PASS= BASE_URL + "dms/token/user/UserChangePassword";
    public final static String USER_DEFAULT_PASS= BASE_URL + "dms/token/user/UserDefaultPassword";
    public final static String USER_CREATE_PARAM ="%sdisplayName=%s&gender=%d&email=%s&phone=%s&role=%d&image=%s&warehouse_id=%d&warehouse_name=%s";
    public final static String USER_CHANGE_PASS_PARAM ="id=%d&current_password=%s&new_password=%s";
    public final static String USER_DEFAULT_PASS_PARAM ="user_id=%d";

    public final static String STATUS = BASE_URL + "dms/token/statu/StatusList";
    public final static String STATUS_NEW = BASE_URL + "dms/token/statu/StatusNew";
    public final static String STATUS_DELETE = BASE_URL + "dms/token/statu/StatusDelete?id=";
    public final static String STATUS_CREATE_PARAM ="%sname=%s&color=%s&defaultStatus=%s";

    public final static String CUSTOMERS = BASE_URL + "dms/token/customer/CustomerList";
    public final static String CUSTOMERS_NEAREST = BASE_URL + "dms/token/customer/CustomerNearest";
    public final static String CUSTOMERS_HAVEDEBT = BASE_URL + "dms/token/customer/CustomerHaveDebtList";
    public final static String CUSTOMER_NEW = BASE_URL + "dms/token/customer/CustomerNew";
    public final static String CUSTOMER_DELETE = BASE_URL + "dms/token/customer/CustomerDetail?id=";
    public final static String CUSTOMER_GETDETAIL = BASE_URL + "dms/token/customer/CustomerDetail?id=";
    public final static String CUSTOMER_NEAREST_PARAM ="&lat=%s&lng=%s";
    public final static String CUSTOMER_CHECKIN_RANGE_PARAM ="&checkinFrom=%s&checkinTo=%s";
    public final static String CUSTOMER_DEBT_PARAM ="debt=%d";
    public final static String CUSTOMER_CREATE_PARAM ="%sname=%s&signBoard=%s&address=%s&phone=%s&street=%s&note=%s&district=%s&province=%s&lat=%s&lng=%s&volumeEstimate=%s&shopType=%s&status_id=%d&distributor_id=%s&checkinCount=%d";
    public final static String CUSTOMER_TEMP_NEW = BASE_URL + "dms/token/customer/CustomerTempNew";
    public final static String CUSTOMER_TEMP_NEW_PARAM ="customer_id=%d&user_id=%d";
    public final static String CUSTOMER_WAITING_LIST = BASE_URL + "dms/token/customer/WaitingList";
    public final static String CUSTOMER_ORDERED = BASE_URL + "token/customer/CustomerOrderedList";

    public final static String DISTRIBUTOR_DETAIL = BASE_URL + "dms/token/distributor/DistributorDetail?id=";
    public final static String DISTRIBUTOR_NEW = BASE_URL + "dms/token/distributor/DistributorNew";
    public final static String DISTRIBUTOR_CREATE_PARAM ="%scompany=%s&address=%s&phone=%s&website=%s&thanks=%s&image=%s";

    public final static String CHECKIN_NEW = BASE_URL + "dms/token/customer/CheckinNew";
    public final static String SCHECKIN_CREATE_PARAM ="customer_id=%d&rating=%d&note=%s&user_id=%d&nextVisit=%d&meetOwner=%d";
    public final static String CHECKIN_DELETE = BASE_URL + "dms/token/customer/CheckinDelete?id=";

    public final static String CHECK_LOGIN = BASE_URL + "dms/token/system/CheckLogin";
    public final static String CATEGORIES = BASE_URL + "dms/token/system/CategoryList";
    public final static String STATISTICALS = BASE_URL + "dms/token/system/StatisticalList";
    public final static String STATISTICAL_PARAM ="?from=%s&to=%s";

    public final static String BILLS = BASE_URL + "dms/token/bill/BillList";
    public final static String BILLS_HAVE_PAYMENT = BASE_URL + "dms/token/bill/BillListHavePayment";
    public final static String BILL_NEW = BASE_URL + "dms/token/bill/BillNew";
    public final static String BILL_DELETE = BASE_URL + "dms/token/bill/BillDelete?id=";
    public final static String BILL_RANGE_PARAM ="&billingFrom=%s&billingTo=%s";
    public final static String BILL_HAVE_PAYMENT_RANGE_PARAM ="&paymentFrom=%s&paymentTo=%s";

    public final static String PAY_NEW = BASE_URL + "dms/token/bill/PayNew";
    public final static String PAYMENTS = BASE_URL + "dms/token/bill/PaymentList";
    public final static String PAY_PARAM ="customerId=%d&paid=%s&billId=%d&userId=%d&note=%s&payByReturn=%d&user_collect=%d";
    public final static String PAYMENTS_PARAM ="?from=%s&to=%s";
    public final static String PAYMENT_DELETE = BASE_URL + "dms/token/bill/PaymentDelete?id=";

    public final static String DEBT_NEW = BASE_URL + "dms/token/bill/DebtNew";
    public final static String DEBT_PARAM ="debt=%s&user_id=%d&customer_id=%d&distributor_id=%d";

    public static Cloudinary getImageCloud() {
        Map config = new HashMap();
        config.put("cloud_name", Util.getInstance().getCurrentActivity().getResources().getString(R.string.cloud_image_name));
        config.put("api_key", Util.getInstance().getCurrentActivity().getResources().getString(R.string.cloud_image_key));
        config.put("api_secret", Util.getInstance().getCurrentActivity().getResources().getString(R.string.cloud_image_secret));
        Cloudinary cloudinary = new Cloudinary(config);

        return cloudinary;
    }

}



