package wolve.dms.callback;

import java.util.List;

import wolve.dms.models.BaseModel;

/**
 * Created by Engine on 12/26/2016.
 */
public interface NewCallbackCustom {

    void onResponse(BaseModel result, List<BaseModel> list);

    void onError(String error);
}