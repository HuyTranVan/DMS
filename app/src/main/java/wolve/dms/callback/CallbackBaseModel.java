package wolve.dms.callback;

import wolve.dms.models.BaseModel;

public interface CallbackBaseModel {
    void onResponse(BaseModel object);

    void onError();
}
