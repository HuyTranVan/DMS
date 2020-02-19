package wolve.dms.callback;

import java.util.List;

import wolve.dms.models.BaseModel;

public interface CallbackListObject {
    void onResponse(List<BaseModel> list);
}
