package wolve.dms.callback;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by macos on 7/17/17.
 */

public interface CallbackReason {
    void Result(List<JSONObject> list);
}
