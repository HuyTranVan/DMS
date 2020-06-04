package wolve.dms.callback;

import java.util.List;

public interface CallbackListBaseModel {
    void onResponse(List object);

    void onError();
}
