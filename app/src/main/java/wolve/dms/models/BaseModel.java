package wolve.dms.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Engine on 12/27/2016.
 */

public class BaseModel implements Serializable {
    JSONObject jsonObject;

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

    public void put(String key, Object value) throws JSONException {
        jsonObject.put(key, value);
    }

    public String getString(String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        try {
            return jsonObject.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getInt(String key) {
        try {
            return jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double getDouble(String key) {
        try {
            return jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
            e.printStackTrace();
        }
        return null;
    }


    public JSONArray getJSONArray(String key) {
        try {
            return jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean has(String key) {
        return jsonObject.has(key);
    }

    public String getOrginalJson() {
        return jsonObject.toString();
    }
}