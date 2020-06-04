package wolve.dms.callback;

import wolve.dms.models.BaseModel;

/**
 * Created by macos on 7/17/17.
 */

public interface CallbackClickProduct {
    void ProductChoice(BaseModel product);

    void ProductAdded(BaseModel newProduct);
}
