package wolve.dms.callback;

import org.json.JSONObject;

import wolve.dms.models.BaseModel;

public interface CallbackObject {
    void onResponse(BaseModel object);
}
