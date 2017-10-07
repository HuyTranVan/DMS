package wolve.dms.models;

import org.json.JSONException;
import org.json.JSONObject;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSharedPrefer;
import wolve.dms.utils.Util;

/**
 * Created by macos on 9/16/17.
 */

public class Distributor extends BaseModel{
    String id_distributor;
    static Distributor currentDistributor;

    public String getId_distributor() {
        return id_distributor;
    }

    public void setId_distributor(String id_distributor) {
        this.id_distributor = id_distributor;
    }

    public static void setCurrentDistributor(Distributor currentDistributor) {
        Distributor.currentDistributor = currentDistributor;
    }

    public Distributor() {
        jsonObject = null;
    }

    public Distributor(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String DistributortoString(){
        return jsonObject.toString();
    }

    public JSONObject DistributorJSONObject(){
        return jsonObject;
    }

    public static Distributor getCurrentDistributor(){
        CustomSharedPrefer customSharedPrefer = new CustomSharedPrefer(Util.getInstance().getCurrentActivity());
        if(customSharedPrefer.getObject(Constants.DISTRIBUTOR, User.class) == null){
            currentDistributor = new Distributor();
        }else {
            currentDistributor = customSharedPrefer.getObject(Constants.DISTRIBUTOR, Distributor.class);
        }
        return currentDistributor;
    }

    public static JSONObject getCurrentDistributorInfo() {
        currentDistributor = getCurrentDistributor();
        return currentDistributor.jsonObject;
    }

    public static int getCurrentDistributorId(){
        int id_distributor = 0;
        Distributor currentDistributor = Distributor.getCurrentDistributor();
        if (currentDistributor != null) {
            id_distributor = currentDistributor.getInt("id");
        }
        return id_distributor;
    }


}
