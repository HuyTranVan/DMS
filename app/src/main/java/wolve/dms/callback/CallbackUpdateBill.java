package wolve.dms.callback;

import wolve.dms.models.Bill;

/**
 * Created by macos on 7/18/17.
 */

public interface CallbackUpdateBill {
    void onUpdate(Bill bill, int position);
}
