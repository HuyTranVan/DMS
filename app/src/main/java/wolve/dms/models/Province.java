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

public class Province extends BaseModel{
    static List<Province> mListProvinces = null;

    public Province() {
        jsonObject = null;
    }

    public Province(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String ProvincetoString(){
        return jsonObject.toString();
    }

    public static void saveProvinceList(JSONArray province){
        CustomSQL.setString(Constants.PROVINCE_LIST, province.toString());
        mListProvinces = null;
    }

    public static List<Province> getProvincetList(){
        if (mListProvinces == null){
            mListProvinces = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(CustomSQL.getString(Constants.PROVINCE_LIST));
                for (int i=0; i<array.length(); i++){
                    Province province = new Province(array.getJSONObject(i));
                    mListProvinces.add(province);
                }

            } catch (JSONException e) {
                return mListProvinces;
            }
        }

        return mListProvinces;
    }

    public static List<String> getListProvince(){
        List<String> list = new ArrayList<>();

            try {
                JSONArray array = new JSONArray(CustomSQL.getString(Constants.PROVINCE_LIST));
                for (int i=0; i<array.length(); i++){
                    JSONObject object =array.getJSONObject(i);
                    list.add(object.getString("name"));
                }

            } catch (JSONException e) {
                return list;
            }

            Collections.sort(list);
        return list;
    }

    public static int  getDistrictId(String district){
        int id = 0;

        try {
            JSONArray array = new JSONArray(CustomSQL.getString(Constants.PROVINCE_LIST));
            for (int i=0; i<array.length(); i++){
                JSONObject object =array.getJSONObject(i);
                if (object.getString("name").equals(district)){
                    id = object.getInt("provinceid");
                    break;
                }
            }

        } catch (JSONException e) {
            return id;
        }

        return id;
    }
}
