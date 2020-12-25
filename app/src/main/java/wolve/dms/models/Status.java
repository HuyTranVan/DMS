//package wolve.dms.models;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import wolve.dms.utils.Constants;
//import wolve.dms.utils.CustomSQL;
//
///**
// * Created by macos on 9/16/17.
// */
//
//public class Status extends BaseModel {
//    public Status() {
//        jsonObject = null;
//    }
//
//    public Status(JSONObject objOrder) {
//        jsonObject = objOrder;
//    }
//
//    public String StatustoString() {
//        return jsonObject.toString();
//    }
//
//    public static void saveStatusList(JSONArray status) {
//        CustomSQL.setString(Constants.STATUS_LIST, status.toString());
//    }
//
//    public static List<Status> getStatusList() {
//        List<Status> mListStatus = new ArrayList<>();
//
//        try {
//            JSONArray array = new JSONArray(CustomSQL.getString(Constants.STATUS_LIST));
//            for (int i = 0; i < array.length(); i++) {
//                Status status = new Status(array.getJSONObject(i));
//                mListStatus.add(status);
//            }
//        } catch (JSONException e) {
////            e.printStackTrace();
//            return new ArrayList<Status>();
//        }
//
//        return mListStatus;
//    }
//
//}
