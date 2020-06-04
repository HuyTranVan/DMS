package wolve.dms.callback;

import wolve.dms.models.BaseModel;

/**
 * Created by Engine on 12/26/2016.
 */
public interface CallbackCustom {

    void onResponse(BaseModel result);

    void onError(String error);
}