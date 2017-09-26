package wolve.dms.callback;

/**
 * Created by Engine on 12/27/2016.
 */

import java.util.List;

/**
 * Created by Engine on 12/26/2016.
 */
public abstract class CallbackList {
    private boolean showLoading = true;

    public boolean isShowLoading() {
        return this.showLoading;
    }

    public CallbackList isShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        return this;
    }

    public abstract void onResponse(List result);
}
