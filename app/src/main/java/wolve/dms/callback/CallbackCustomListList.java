package wolve.dms.callback;

import java.util.List;

import wolve.dms.models.BaseModel;

/**
 * Created by Engine on 12/26/2016.
 */
public interface CallbackCustomListList {

    void onResponse(List<List<BaseModel>> results);

    void onError(String error);
}