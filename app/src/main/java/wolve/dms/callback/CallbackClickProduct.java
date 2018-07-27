package wolve.dms.callback;

import org.json.JSONObject;

import java.util.List;

import wolve.dms.models.Product;

/**
 * Created by macos on 7/17/17.
 */

public interface CallbackClickProduct {
    void ProductChoice(Product product);
    void ProductAdded(Product newProduct);
}
