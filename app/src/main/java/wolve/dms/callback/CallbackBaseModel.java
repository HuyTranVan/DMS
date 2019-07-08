package wolve.dms.callback;

import org.json.JSONObject;

import wolve.dms.models.BaseModel;

public interface CallbackBaseModel {
    void onResponse(BaseModel object);
    void onError();
}
