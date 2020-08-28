package wolve.dms.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.models.User;

public class Constants {
    public final static String DMS_EMAIL = "tranvanhuy112@gmail.com";
    public final static String DMS_NAME = "DMS_NAME";
    public final static String DMS_LOGS = "DMS_LOGS";
    public final static String PRODUCTGROUP = "productgroup";
    public final static String DEPOT = "depot";
    public final static String PRODUCT = "product";
    public final static String STATUS = "status";
    public final static String CUSTOMER = "customer";
    public final static String CUSTOMER_RECEIVE = "customer_receive";
    public final static String CUSTOMER_ID = "customer_id";
    public final static String BILLS = "bills";
    public final static String DEBTS = "debts";
    public final static String BILL = "bill";
    public final static String WAREHOUSE = "warehouse";
    public static final String TEMPWAREHOUSE = "temp_warehouse";
    public final static String BILL_DETAIL = "billDetails";
    public final static String PAYMENT = "payment";
    public final static String PAYMENTS = "payments";
    public final static String CHECKINS = "checkins";
    public final static String SHOP_CART_ACTIVITY = "shopcart";
    public final static String PRINT_BILL_ACTIVITY = "printbill";
    public final static String IMPORT_ACTIVITY = "import";
    public final static String USER = "user";
    public final static String DISTRIBUTOR = "distributor";
    public final static String USER_USERNAME = "username";
    public final static String USER_PASSWORD = "password";
    public final static String IMAGES_DIRECTORY = "Lubsolution_DMS";
    public final static String APP_DIRECTORY = "DMS";
    public final static int ROLE_ADMIN = 1;
    public final static int ROLE_WAREHOUSE = 2;
    public final static int ROLE_DELIVER = 3;
    public final static int ROLE_SALE = 4;

    public final static String IS_ADMIN = "is_admin";
    public final static String VERSION_CODE = "version_code";
    public final static String PRINTER_SIZE = "printer_size";
    public static final String CHECKIN_FLAG = "check_in";
    public static final long CHECKIN_DISTANCE = 500;
    public static final String CURRENT_DISTANCE = "current_distance";
    public static final String CHECKIN_TIME = "check_in_time";
    public static final String RELOAD_DATA = "reload_data";
    public static final String RE_PRINT = "reprint";
    public static final String BARCODE = "code";
    public static final String ISBILLRETURN = "isBillReturn";
    public static final String HAVEBILLRETURN = "haveBillReturn";
    public static final String PAYBYTRETURN = "havePaymentReturn";
    public static final String DELIVER_BY = "deliverBy";
    public static final String TEMPBILL = "temp_bill";
    public static final String LAST_PRODUCT_UPDATE = "last_product_update";
    public static final String addressFormat = "%s %s, %s, %s";
    public final static String FLAG = "flag";

    public static final String FILTER_BY_DATE = "Lọc theo ngày";
    public static final String FILTER_BY_MONTH = "Lọc theo tháng";
    public static final String FILTER_BY_YEAR = "Lọc theo năm";

    public static final String TYPE = "type";
    public static final String BILL_RETURN = "bill_return";
    public static final String BILL_DELETE = "bill_delete";
    public static final String PAYMENT_DELETE = "payment_delete";
    public static final String BILL_PAY = "bill_pay";
    public static final String BILL_DELIVER = "bill_deliver";

    public static final int PRINTER_57_WIDTH = 384;
    public static final int PRINTER_80_WIDTH = 576;

    public static final String DATE_DEFAULT = "Chọn ngày";
    public static final String MONTH_DEFAULT = "Chọn tháng";
    public static final String YEAR_DEFAULT = "Chọn năm";

    public final static String IS_PROMOTION = "BÁN VÀ KHUYẾN MÃI";
    public final static String NO_PROMOTION = "CHỈ BÁN";

    public final static String LOGIN_SUCCESS = "login_success";

    public static final int REQUEST_GOOGLE_PLAY_ACCOUNT_PICKER = 2000;
    public static final int REQUEST_GOOGLE_PLAY_AUTHORIZATION = 2001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 2002;
    public static final int REQUEST_GOOGLE_PLAY_PERMISSION_GET_ACCOUNTS = 2003;
    public static final String PREF_GOOGLE_PLAY_ACCOUNT_NAME = "accountName";
    public static final String GOOGLE_PLAY_INSTALL = "This app requires Google Play Services. Please install \n Google Play Services on your device and relaunch this app.";

    public final static int RESULT_CUSTOMER_ACTIVITY = 1000;
    public final static int RESULT_SHOPCART_ACTIVITY = 1001;
    public final static int RESULT_PRINTBILL_ACTIVITY = 1002;
    public final static int REQUEST_CHOOSE_IMAGE = 1003;
    public final static int REQUEST_IMAGE_CAPTURE = 1004;
    public final static int REQUEST_READ_PERMISSION = 1005;
    public final static int RESULT_BLUETOOTH_ACTIVITY = 1006;
    public final static int REQUEST_PERMISSION_LOCATION = 1007;
    public final static int REQUEST_CAMERA_PERMISSION = 1008;
    public final static int REQUEST_PHONE_PERMISSION = 1009;
    public final static int REQUEST_PERMISSION = 1010;
    public final static int RESULT_IMPORT_ACTIVITY = 1011;
    public final static String RESULT_ERROR_TITLE = "Có lỗi xảy ra";
    public final static String RESULT = "result";
    public final static boolean RESULT_FALSE = false;
    public final static String RESULT_MESSAGE = "message";

    public static final int REQUEST_ENABLE_BT = 0 * 1000;

    public final static String BADGE = "badge";

    public final static String MARKER_ALL = "all";
    public final static String MARKER_INTERESTED = "interested";
    public final static String MARKER_ORDERED = "ordered";
    public static final String BLUETOOTH_DEVICE = "bluetooth_device";
    public static final String STATUS_LIST = "status_list";
    public static final String DISTRICT_LIST = "district_list";
    public static final String PRODUCTGROUP_LIST = "productgroup_list";
    public static final String PRODUCT_LIST = "product_list";
    public static final String PROVINCE_LIST = "province_list";

    public static final String CONNECTING_PRINTER = "Đang kết nối máy in ....";
    public static final String CONNECTED_PRINTER = "Đã kết nối máy in: %s";
    public static final String CONNECTED_PRINTER_ERROR = "Không thể kết nối tới máy in";

    public static final String ON_MAP_SCREEN = "on_map";

    public final static String PRINTER_57 = "57mm";
    public final static String PRINTER_80 = "80mm";
    public final static String currentEmulatorDevice = "Google Android SDK built for x86";

    public final static String ONSTART = "on_start";
    public final static String ONFAIL = "on_fail";
    public final static String ONSUCCESS = "on_success";

    public final static String ALL_FILTER = "TẤT CẢ";
    public static final String USER_LIST = "user_list";

    public static final String ALL_TOTAL = "all total";
    public static final String ALL_COLLECT = "all collect";
    public static final String ALL_DEBT = "all debt";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String PRODUCT_POPULAR = "product_popular";
    public static final String PRODUCT_SUGGEST_LIST = "product_suggest_list";
    public static final String IMAGES = "images";
    public static final String LOADING_TIMES = "loading_times";

    public static String CHECK_ALL = Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_check) + " CHỌN TẤT CẢ";
    public static String UNCHECK = "BỎ CHỌN";

    public static String TYPE_OBJECT = "Object";
    public static String TYPE_ARRAY = "Array";


    //Home constant
    public static ArrayList<JSONObject> HomeItemList() {
        String[] icons = new String[]{
                Util.getIcon(R.string.icon_edit_map),
                Util.getIcon(R.string.icon_chart),
                Util.getIcon(R.string.icon_depot),
                Util.getIcon(R.string.icon_setting),
                Util.getIcon(R.string.icon_barcode),
                ""};
        String[] texts = new String[]{
                "Bán hàng",
                "Thống kê",
                "Nhập - Tồn kho",
                "Cài đặt",
                "Quét mã",
                ""};
        String[] colors = new String[]{
                "Bán hàng",
                "Thống kê",
                "Xuất nhập tồn",
                "Thông báo",
                "Cài đặt",
                "Tài khoản"};
        String[] roles = new String[]{
                "SALE",
                "SALE",
                "MANAGER",
                "SALE",
                "MANAGER",
                "MANAGER"};

        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < icons.length; i++) {
            JSONObject object = new JSONObject();
            try {
                object.put("icon", icons[i]);
                object.put("text", texts[i]);
                object.put("color", colors[i]);
                object.put("role", roles[i]);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(object);
        }

        return list;
    }


    public static String[] shopName = new String[]{
            "SỬA XE",
            "RỬA XE",
            "PHỤ TÙNG",
            "BẢO TRÌ",
            "GARAGE",
            "SHOP",
            "KHÁCH LẺ"};

    public static String[] shopNameShortened = new String[]{
            "SX",
            "RX",
            "PT",
            "BT",
            "GR",
            "SH",
            "KL"};


    public static void throwError(String err) {
        Util.showSnackbarError(err);

    }

//    public static boolean responeIsSuccess(BaseModel respone) {
//        if (!respone.isNull("status") && respone.getInt("status") == 200) {
//            return true;
//
//        } else {
//            return false;
//        }
//    }
//
//    public static BaseModel getResponeObjectSuccess(BaseModel respone) {
//        return new BaseModel(respone.getJsonObject("data"));
//
//    }
//
//    public static List<BaseModel> getResponeArraySuccess(BaseModel respone) {
//        List<BaseModel> list = new ArrayList<>();
//        JSONArray array = respone.getJSONArray("data");
//        try {
//            for (int i = 0; i < array.length(); i++) {
//                list.add(new BaseModel(array.getJSONObject(i)));
//            }
//
//        } catch (JSONException e) {
//            throwError(e.toString());
//            //e.printStackTrace();
//        }
//
//        return list;
//
//    }

    public static List<BaseModel> homeSettingSetup() {
        List<BaseModel> list = new ArrayList<>();

        BaseModel item0 = new BaseModel();
        item0.put("position", 0);
        item0.put("icon", Util.getIcon(R.string.icon_info));
        item0.put("text", "Thông tin nhà phân phối");
        list.add(0, item0);

        BaseModel item1 = new BaseModel();
        item1.put("position", 1);
        item1.put("icon", Util.getIcon(R.string.icon_group));
        item1.put("text", "Nhân viên");
        list.add(1, item1);

        BaseModel item2 = new BaseModel();
        item2.put("position", 2);
        item2.put("icon", Util.getIcon(R.string.icon_product_group));
        item2.put("text", "Nhóm sản phẩm");
        list.add(2, item2);

        BaseModel item3 = new BaseModel();
        item3.put("position", 3);
        item3.put("icon", Util.getIcon(R.string.icon_product));
        item3.put("text", "Sản phẩm");
        list.add(3, item3);

        BaseModel item4 = new BaseModel();
        item4.put("position", 4);
        item4.put("icon", Util.getIcon(R.string.icon_status));
        item4.put("text", "Trạng thái");
        list.add(4, item4);

        return list;
    }
}