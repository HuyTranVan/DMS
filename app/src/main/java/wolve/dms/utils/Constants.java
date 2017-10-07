package wolve.dms.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import wolve.dms.R;

public class Constants {
    public final static String DMS_LOGS = "DMS_LOGS";
    public final static String PRODUCTGROUP = "productgroup";
    public final static String PRODUCT = "product";
    public final static String STATUS = "status";
    public final static String CUSTOMER = "customer";
    public final static String CUSTOMER_CART = "customer";
    public final static String USER = "user";
    public final static String DISTRIBUTOR = "distributor";
    public final static String USER_USERNAME = "username";
    public final static String USER_PASSWORD = "password";
    public final static String IMAGES_DIRECTORY = "Wolver_DMS";

    public final static String LOGIN_PARAM ="phone=%s&password=%s";

    public final static String PRODUCTGROUPS_PARAM ="?page=%d&size=%d";
    public final static String PRODUCTGROUP_CREATE_PARAM ="%sname=%s";

    public final static String PRODUCTS_PARAM ="?page=%d&size=%d";
    public final static String PRODUCT_CREATE_PARAM ="%sname=%s&promotion=%s&unitPrice=%d&purchasePrice=%d&volume=%d&productGroup.id=%d";

    public final static String STATUS_PARAM ="?page=%d&size=%d";
    public final static String STATUS_CREATE_PARAM ="%sname=%s&color=%s&defaultStatus=%s";

    public final static String CUSTOMER_PARAM ="?page=%d&size=%d";
    public final static String CUSTOMER_NEAREST_PARAM ="?lat=%s&lng=%s&page=%d&size=%d";
    public final static String CUSTOMER_CREATE_PARAM ="%sname=%s&signBoard=%s&address=%s&phone=%s&street=%s&note=%s&district=%s&province=%s&lat=%s&lng=%s&volumeEstimate=%s&shopType=%s&status.id=%d";

    public final static String SCHECKIN_CREATE_PARAM ="customer.id=%d&status.id=%d&note=%s&user.id=%s";

    public final static String IS_PROMOTION = "BÁN VÀ KHUYẾN MÃI";
    public final static String NO_PROMOTION = "CHỈ BÁN";

    public final static int RESULT_CUSTOMER_ACTIVITY = 1000;
    public final static int RESULT_SHOPCART_ACTIVITY = 1001;
    public final static int REQUEST_CHOOSE_IMAGE = 1002;
    public final static int REQUEST_READ_PERMISSION = 1003;
    public final static int REQUEST_PERMISSION_LOCATION = 2;
    public final static int REQUEST_PHONE_PERMISSION = 8;

    public final static String BADGE = "badge";

    public final static String RESULT_ERROR_TITLE = "Có lỗi xảy ra";
    public final static String RESULT = "result";
    public final static boolean RESULT_FALSE = false;
    public final static String RESULT_MESSAGE = "message";

    //Home constant
    public static ArrayList<JSONObject> HomeItemList(){
        String[] icons = new String[]{
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_home),
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_chart),
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_group),
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_product_group),
                Util.getInstance().getCurrentActivity().getResources().getString(R.string.icon_status)};
        String[] texts = new String[]{
                "Bán hàng",
                "Doanh thu",
                "Nhân viên",
                "Sản phẩm",
                "Trạng thái"};
        String[] colors = new String[]{
                "Bán hàng",
                "Doanh thu",
                "Nhân viên",
                "Sản phẩm",
                "Trạng thái"};
        String[] roles = new String[]{
                "SALE",
                "SALE",
                "MANAGER",
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
    public static String getShopInfo(String type, String name){
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



}