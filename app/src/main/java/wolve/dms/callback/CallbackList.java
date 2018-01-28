package wolve.dms.callback;

/**
 * Created by Engine on 12/27/2016.
 */

import java.util.List;

/**
 * Created by Engine on 12/26/2016.
 */
public abstract class CallbackList {

    public abstract void onResponse(List result);

    public abstract void onError(String error);

}
