package wolve.dms.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;

/**
 * Created by macos on 9/16/17.
 */

public class District extends BaseModel{
    static List<String> mListDistricts = null;

    public District() {
        jsonObject = null;
    }

    public District(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String DistricttoString(){
        return jsonObject.toString();
    }

    public static void saveDistrictList(JSONArray district){
        CustomSQL.setString(Constants.DISTRICT_LIST, district.toString());
        mListDistricts = null;
    }

    public static List<String> getDistrictList(){
        if (mListDistricts == null){
            mListDistricts = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(CustomSQL.getString(Constants.DISTRICT_LIST));
                for (int i=0; i<array.length(); i++){
                    JSONObject object = array.getJSONObject(i);
                    if (!object.getString("name").contains(" ")){
                        mListDistricts.add(object.getString("type") + " " + object.getString("name"));
                    }else {
                        mListDistricts.add(object.getString("name"));
                    }
                }

                Collections.sort(mListDistricts, String.CASE_INSENSITIVE_ORDER);
//                mListDistricts.add(0, "Chọn quận");
            } catch (JSONException e) {
                return mListDistricts;
            }
        }

        return mListDistricts;
    }
}
