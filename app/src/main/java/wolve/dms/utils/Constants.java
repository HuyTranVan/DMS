package wolve.dms.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import wolve.dms.R;
import wolve.dms.models.User;

public class Constants {
    public final static String DMS_NAME = "DMS_NAME";
    public final static String DMS_LOGS = "DMS_LOGS";
    public final static String PRODUCTGROUP = "productgroup";
    public final static String PRODUCT = "product";
    public final static String STATUS = "status";
    public final static String CUSTOMER = "customer";
    public final static String BILLS = "bills";
    public final static String BILL = "bill";
    public final static String PAYMENT = "payment";
    public final static String SHOP_CART_ACTIVITY = "shopcart";
    public final static String PRINT_BILL_ACTIVITY = "printbill";
    public final static String USER = "user";
    public final static String DISTRIBUTOR = "distributor";
    public final static String USER_USERNAME = "username";
    public final static String USER_PASSWORD = "password";
    public final static String IMAGES_DIRECTORY = "Lubsolution_DMS";
    public final static String APP_DIRECTORY = "Lubsolution_DMS";
    public final static String ROLE_ADMIN = "MANAGER";
    public final static String ROLE_WAREHOUSE = "WAREHOUSE";
    public final static String ROLE_SALE = "SALE";
    public final static String IS_ADMIN = "is_admin";
    public final static String PRINTER_SIZE = "printer_size";
    public static final String CHECKIN_FLAG = "check_in";
    public static final long CHECKIN_DISTANCE = 200;
    public static final String CHECKIN_TIME = "check_in_time";
    public static final String RELOAD_DATA = "reload_data";
    public static final String RE_PRINT = "reprint";
    public static final String BARCODE = "code";

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
    public final static String RESULT_ERROR_TITLE = "Có lỗi xảy ra";
    public final static String RESULT = "result";
    public final static boolean RESULT_FALSE = false;
    public final static String RESULT_MESSAGE = "message";

    public static final int REQUEST_ENABLE_BT = 0*1000;

    public final static String BADGE = "badge";

    public final static String MARKER_ALL = "all";
    public final static String MARKER_INTERESTED = "interested";
    public final static String MARKER_ORDERED = "ordered";
    public static final String BLUETOOTH_DEVICE ="bluetooth_device" ;
    public static final String STATUS_LIST = "status_list";
    public static final String DISTRICT_LIST = "district_list";
    public static final String PRODUCTGROUP_LIST = "productgroup_list";
    public static final String PRODUCT_LIST = "product_list";
    public static final String PROVINCE_LIST = "province_list";

    public static final String CONNECTING_PRINTER = "Đang kết nối máy in ....";
    public static final String CONNECTED_PRINTER = "Đã kết nối máy in: %s";
    public static final String CONNECTED_PRINTER_ERROR = "Không thể kết nối tới máy in";

    public static final String ON_MAP_SCREEN = "on_map";

    public final static String COMPANY_NAME = "CTY TNHH MTV TM DV XNK TRẦN VŨ ANH";
    public final static String COMPANY_ADDRESS = "MST: 3702405542";
    public final static String COMPANY_HOTLINE = "ĐT:0931.07.22.23";
    public final static String COMPANY_WEBSITE = "Website: www.wolver.vn";
    public final static String COMPANY_ORDERPHONE = String.format("Đặt hàng: %s", Util.PhoneFormat(User.getPhone()));
    public final static String COMPANY_THANKS = "Trân trọng cảm ơn quý khách hàng";

    public final static String PRINTER_57 = "57mm";
    public final static String PRINTER_80 = "80mm";
    public final static String currentEmulatorDevice = "Google Android SDK built for x86";

    public final static String ONSTART = "on_start";
    public final static String ONFAIL = "on_fail";
    public final static String ONSUCCESS = "on_success";

    public final static String ALL_FILTER ="TẤT CẢ";
    public static final String USER_LIST = "user_list";


    //Home constant
    public static ArrayList<JSONObject> HomeItemList(){
        String[] icons = new String[]{
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_edit_map),
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_chart),
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_import),
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_barcode),
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_setting),
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_username)};
        String[] texts = new String[]{
                "Bán hàng",
                "Thống kê",
                "Xuất nhập tồn",
                "Quét mã",
                "Cài đặt",
                "Tài khoản"};
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
        for (int i=0; i< icons.length; i++){
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

    //Shoptype constant
    public static String[] shopType = new String[]{
            "SUA_XE",
            "RUA_XE",
            "PHU_TUNG",
            "BAO_TRI"};
    public static String[] shopName = new String[]{
            "SỬA XE",
            "RỬA XE",
            "PHỤ TÙNG",
            "BẢO TRÌ"};
    public static String getShopTitle(String type, String name){
        String result ="";
        if (type == null && name != null){
            for (int i=0; i<shopName.length; i++){
                if (shopName[i].equals(name)){
                    result = shopType[i];
                    break;
                }
            }
        }else if (type != null && name == null){
            for (int i=0; i<shopType.length; i++){
                if (shopType[i].equals(type)){
                    result = shopName[i];
                    break;
                }
            }
        }

        return result;
    }

    public static String getShopType( String name){
        String result ="";
        for (int i=0; i<shopName.length; i++){
            if (shopName[i].equals(name)){
                result = shopType[i];
                break;
            }
        }



        return result;
    }

}