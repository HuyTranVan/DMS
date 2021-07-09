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
    public final static String CFTYPE = "cftype";
    public final static String CUSTOMER = "customer";
    public final static String CUSTOMER_RECEIVE = "customer_receive";
    public final static String CUSTOMER_ID = "customer_id";
    public final static String BILLS = "bills";
    public final static String DEBTS = "debts";
    public final static String BILL = "bill";
    public final static String WAREHOUSE = "warehouse";
    public final static String WAREHOUSE_ID = "warehouse_id";
    public final static String CURRENT_WAREHOUSE = "current_warehouse";
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

    public static final String DATE = "date";
    public static final String MONTH = "month";
    public static final String YEAR = "year";

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
    public static final String CONNECTED_PRINTER = "[%s] %s: %s";
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
    public static final String AUTO_SAVE_CONTACT = "auto_save_contact";
    public static final String SET_DEFAULT_MAPSTYLE = "default_map_style";
    public static final String PAY_DISTRIBUTOR = "pay_distributor";
    public static final String IN_COME = "in_come";
    public static final String OUT_COME = "out_come";
    public static final String OTHERS = "others";

    public static final String CHECK_ALL = Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_check) + " CHỌN TẤT CẢ";
    public static final String UNCHECK = "BỎ CHỌN";

    public static final String TYPE_OBJECT = "Object";
    public static final String TYPE_ARRAY = "Array";

    public final static String ZALO_PACKAGE = "com.zing.zalo";
    public final static String VIBER_PACKAGE = "com.viber.voip";
    public final static String MESSENGER_PACKAGE = "com.facebook.orca";
    public static final String PACKAGE_DEFAULT = "package_default";
    public static final String RANGE_TIME = "range_time";
    public static final String ONLY_USER = "only_user";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String HISTORY_SEARCH = "history_search";

    //Home constant
    public static List<BaseModel> HomeItemList() {
        String[] icons = new String[]{
                Util.getIcon(R.string.icon_edit_map_marked),
                Util.getIcon(R.string.icon_chart),
                Util.getIcon(R.string.icon_depot),
                Util.getIcon(R.string.icon_hand_on_money),
                Util.getIcon(R.string.icon_setting),
                ""};
        String[] texts = new String[]{
                "Bán hàng",
                "Thống kê",
                "Nhập kho",
                "Thu chi",
                "Danh mục",
                ""};
        boolean[] isDistributor = new boolean[]{
                false,
                false,
                false,
                false,
                false,
                true};
        boolean[] haveDetail = new boolean[]{
                false,
                false,
                true,
                false,
                false,
                false};


        List<BaseModel> list = new ArrayList<>();
        for (int i = 0; i < icons.length; i++) {
            BaseModel object = new BaseModel();
            object.put("icon", icons[i]);
            object.put("text", texts[i]);
            object.put("isDistributor", isDistributor[i]);
            object.put("haveDetail", haveDetail[i]);


            list.add(object);
        }

        return list;
    }


    public static String[] shopName = new String[]{
            "Sửa xe",
            "Rửa xe",
            "Phụ tùng",
            "Bảo trì",
            "Garage",
            "Shop",
            "Khách lẻ"};

    public static String[] shopNameShortened = new String[]{
            "SX",
            "RX",
            "PT",
            "BT",
            "GR",
            "SH",
            "KL"};

    public static String[] sortGroups = new String[]{

            "Tất cả",
            "Khác",
            "Trả tiền NPP",
            "Bán hàng"};


    public static void throwError(String err) {
        Util.showSnackbarError(err);

    }


    public static List<BaseModel> homeSettingSetup(){
        List<BaseModel> list = new ArrayList<>();

        BaseModel item1 = new BaseModel();
        item1.put("position", 0);
        item1.put("icon", Util.getIcon(R.string.icon_group));
        item1.put("text", "Nhân viên");
        if (Util.isAdmin()){
            list.add(item1);
        }
        //list.add(item1);

        BaseModel item6 = new BaseModel();
        item6.put("position", 1);
        item6.put("icon", Util.getIcon(R.string.icon_depot));
        item6.put("text", "Kho hàng");
        if (Util.isAdmin()){
            list.add(item6);
        }


        BaseModel item4 = new BaseModel();
        item4.put("position", 2);
        item4.put("icon", Util.getIcon(R.string.icon_hand_on_money));
        item4.put("text", "Thu - chi");
        if (Util.isAdmin()){
            list.add(item4);
        }
        //list.add(2, item4);

        BaseModel item2 = new BaseModel();
        item2.put("position", 3);
        item2.put("icon", Util.getIcon(R.string.icon_product_group));
        item2.put("text", "Nhóm sản phẩm");
        if (Util.isAdmin()){
            list.add(item2);
        }
        //list.add(item2);

        BaseModel item3 = new BaseModel();
        item3.put("position", 4);
        item3.put("icon", Util.getIcon(R.string.icon_product));
        item3.put("text", "Sản phẩm");
        list.add(item3);

        BaseModel item5 = new BaseModel();
        item5.put("position", 5);
        item5.put("icon", Util.getIcon(R.string.icon_list_check));
        item5.put("text", "Danh sách nhà phân phối");

        if (User.getId() ==  2){
            list.add(5, item5);
        }


        return list;
    }

    public static List<BaseModel> listSharePackage(){
        List<BaseModel> list = new ArrayList<>();

        BaseModel item0 = new BaseModel();
        item0.put("position", 0);
        item0.put("icon", Util.getIcon(R.string.icon_chat));
        item0.put("text", "Tùy chọn");
        list.add(0, item0);

        BaseModel item1 = new BaseModel();
        item1.put("position", 1);
        item1.put("icon", Util.getIcon(R.string.icon_chat_noname));
        item1.put("text", "Zalo");
        list.add(1, item1);

        BaseModel item2 = new BaseModel();
        item2.put("position", 2);
        item2.put("icon", Util.getIcon(R.string.icon_chat_viber));
        item2.put("text", "Viber");
        list.add(2, item2);

        BaseModel item3 = new BaseModel();
        item3.put("position", 3);
        item3.put("icon", Util.getIcon(R.string.icon_chat_messenger));
        item3.put("text", "Messenger");
        list.add(3, item3);

        return list;
    }

    public static List<BaseModel> listCashFlowKind(boolean addDefault){
        List<BaseModel> list = new ArrayList<>();

        BaseModel item = new BaseModel();
        item.put("kind", 0);
        item.put("icon", Util.getIcon(R.string.icon_money_check));
        item.put("text", "Trả tiền hàng NPP");
        if (User.getId() == 2 && addDefault){
            list.add(0, item);
        }

        BaseModel item0 = new BaseModel();
        item0.put("kind", 1);
        item0.put("icon", Util.getIcon(R.string.icon_bill));
        item0.put("text", "Khách trả tiền");
        if (User.getId() == 2 && addDefault){
            list.add(item0);
        }


        BaseModel item1 = new BaseModel();
        item1.put("kind", 2);
        item1.put("icon", Util.getIcon(R.string.icon_hand_on_money));
        item1.put("text", "Trả chiết khấu");
        if (User.getId() ==  2 && addDefault){
            list.add(item1);
        }



        BaseModel item2 = new BaseModel();
        item2.put("kind", 3);
        item2.put("icon", Util.getIcon(R.string.icon_protect));
        item2.put("text", "Mặc định");
        if (User.getId() ==  2 && addDefault){
            list.add(item2);
        }


        BaseModel item3 = new BaseModel();
        item3.put("kind", 4);
        item3.put("icon", Util.getIcon(R.string.icon_detail));
        item3.put("text", "Khác");
        list.add(item3);



        return list;
    }

    public static BaseModel cashFlowKind(int kind){
        List<BaseModel> list = listCashFlowKind(true);
        BaseModel model = new BaseModel();
        for (int i = 0; i<list.size(); i++){
            if (list.get(i).getInt("kind") == kind){
                model = list.get(i);
                break;
            }
        }
        return model;
    }

    public static String[] warehouseOptions = new String[]{
            Util.getIconString(R.string.icon_info, "    ", "Thông tin"),
            Util.getIconString(R.string.icon_depot, "   ", "Tồn kho"),
            Util.getIconString(R.string.icon_reply, "    ", "Trả hàng")};

}