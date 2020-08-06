package wolve.dms.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wolve.dms.utils.DataUtil;
import wolve.dms.utils.Util;

/**
 * Created by Engine on 12/27/2016.
 */

public class BaseModel implements Serializable {
    JSONObject jsonObject;

    public BaseModel() {
        jsonObject = new JSONObject();
    }

    public BaseModel(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public BaseModel(String objOrder) {
        try {
            jsonObject = new JSONObject(objOrder);
        } catch (JSONException e) {
            jsonObject = new JSONObject();
        }
    }

    public String BaseModelstoString() {
        return jsonObject.toString();
    }

    public JSONObject BaseModelJSONObject() {
        return jsonObject;
    }

    public boolean hasKey(String key) {
        return jsonObject.has(key);
    }

    public Object get(String key) {
        try {
            return jsonObject.get(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void put(String key, Object value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            Util.showToast(e.toString());
//            e.printStackTrace();
        }
    }

    public static BaseModel putValueToNewObject(String key, Object value) {
        BaseModel model = new BaseModel();
        model.put(key, value);
        return model;
    }

    public static BaseModel put2ValueToNewObject(String key, Object value, String key2, Object value2) {
        BaseModel model = new BaseModel();
        model.put(key, value);
        model.put(key2, value2);
        return model;
    }

    public void putBaseModel(String key, BaseModel value) {
        try {
            jsonObject.put(key, value.BaseModelJSONObject());
        } catch (JSONException e) {
            Util.showToast(e.toString());
        }
    }

    public void putList(String key, List<BaseModel> list) {
        try {
            jsonObject.put(key, DataUtil.convertListObject2Array(list));
        } catch (JSONException e) {
            Util.showToast(e.toString());
        }

    }

    public List<BaseModel> getList(String key) {
        try {
            return DataUtil.array2ListObject(jsonObject.getString(key));
        } catch (JSONException e) {
            return new ArrayList<>();
        }

    }

    public String getString(String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Boolean getBoolean(String key) {
        try {
            return jsonObject.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getInt(String key) {
        try {
            return jsonObject.getInt(key);
        } catch (JSONException e) {
            return 0;

        }

    }

    public Double getDouble(String key) {
        try {
            return jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getDoubleValue(String key) {
        try {
            return jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Long getLong(String key) {
        try {
            return jsonObject.getLong(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public JSONObject getJsonObject(String key) {
        try {
            return jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            return new JSONObject();
//            e.printStackTrace();
        }
//        return null;
    }


    public JSONArray getJSONArray(String key) {
        try {
            return jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            return new JSONArray();
        }

    }

    public BaseModel getBaseModel(String key) {
        try {
            return new BaseModel(jsonObject.getJSONObject(key));
        } catch (JSONException e) {
            return new BaseModel();
        }
    }

//    public BaseModel getObject(String key) {
//        try {
//            return jsonObject.getO(key);
//        } catch (JSONException e) {
//            return new BaseModel();
//        }
//    }

    public boolean has(String key) {
        return jsonObject.has(key);
    }

    public String getOrginalJson() {
        return jsonObject.toString();
    }

    public JSONObject convertJsonObject() {
        return jsonObject;
    }

    public boolean isNull(String key) {
        return jsonObject.isNull(key);
    }

    public void removeKey(String key) {
        jsonObject.remove(key);

    }


}