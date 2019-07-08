package wolve.dms.callback;

import java.util.List;

import wolve.dms.models.BaseModel;

public interface CallbackListBaseModel {
    void onResponse(List object);
    void onError();
}
