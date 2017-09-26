package wolve.dms.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tranhuy on 6/5/17.
 */

public class ListingUtil {

    public static int statusId(JSONObject listingObject){
        int Id =0;
        JSONObject object = new JSONObject();
        if (!listingObject.isNull("transactionStatus")){
            try {
                Id = listingObject.getJSONObject("transactionStatus").getInt("id");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return Id;
    }

    public static String defineListingType(JSONObject listingObject){
        String type="bán";
        try {
            if (!listingObject.getJSONObject("listing").isNull("listingTypeId")){
                if (listingObject.getJSONObject("listing").getInt("listingTypeId") ==1 ){
                    type = "bán";

                }else if (listingObject.getJSONObject("listing").getInt("listingTypeId") ==2){
                    type = "cho thuê";
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return type;
    }

    public static JSONObject defineStatusListing(JSONObject listingObject){
        JSONObject object = new JSONObject();
        if (!listingObject.isNull("transactionStatus")){
            try {
                int statusId = listingObject.getJSONObject("transactionStatus").getInt("id");
                String statusText = listingObject.getJSONObject("transactionStatus").getString("name");
                switch (statusId){
                    case 1:
                        //gray color
                        object.put("status", statusText);
                        object.put("color", "#666464");
                        break;

                    case 2:
                        //orange color
                        object.put("status", statusText);
                        object.put("color", "#ff9f35");
                        break;

                    case 3:
                        //green color
                        object.put("status", statusText);
                        object.put("color", "#8fd811");
                        break;

                    case 4:
                        //gray color
                        object.put("color", "#666464");
                        if (!listingObject.isNull("listingTypeId")){
                            if (listingObject.getInt("listingTypeId") ==1 ){
                                object.put("status", "Đã bán");
                            }else if (listingObject.getInt("listingTypeId") ==2){
                                object.put("status", "Đã cho thuê");
                            }
                        }else {
                            object.put("status", statusText);
                        }

                        break;

                    case 5:
                        //gray color
                        object.put("status", statusText);
                        object.put("color", "#666464");
                        break;

                    case 6:
                        //gray color
                        object.put("status", statusText);
                        object.put("color", "#666464");
                        break;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public static JSONObject defineStatusListingDetail(JSONObject listingDetailObject){
        JSONObject object = new JSONObject();
        if (!listingDetailObject.isNull("transactionStatus")){
            try {
                int statusId = listingDetailObject.getJSONObject("transactionStatus").getInt("id");
                String statusText = listingDetailObject.getJSONObject("transactionStatus").getString("name");
                switch (statusId){
                    case 1:
                        //chờ xét duyệt
                        //gray color
                        object.put("status", statusText);
                        object.put("color", "#666464");
                        break;

                    case 2:
                        //cần bổ sung
                        //orange color
                        object.put("status", statusText);
                        object.put("color", "#ff9f35");
                        break;

                    case 3:
                        //đang rao
                        //green color
                        object.put("status", statusText);
                        object.put("color", "#8fd811");
                        break;

                    case 4:
                        //đã bán/ đã cho thuê
                        //gray color
                        object.put("color", "#666464");
                        if (listingDetailObject.isNull("listing")){
                            object.put("status", statusText);
                        }else {
                            if (!listingDetailObject.getJSONObject("listing").isNull("listingTypeId")){
                                if (listingDetailObject.getJSONObject("listing").getInt("listingTypeId") ==1 ){
                                    object.put("status", "Đã bán");
                                }else if (listingDetailObject.getJSONObject("listing").getInt("listingTypeId") ==2){
                                    object.put("status", "Đã cho thuê");
                                }
                            }else {
                                object.put("status", statusText);
                            }
                        }


                        break;

                    case 5:
                        //ngừng đăng tin
                        //gray color
                        object.put("status", statusText);
                        object.put("color", "#666464");
                        break;

                    case 6:
                        //Từ chối
                        //gray color
                        object.put("status", statusText);
                        object.put("color", "#666464");
                        break;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

}
