package wolve.dms.models;

import org.json.JSONObject;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;

/**
 * Created by macos on 9/16/17.
 */

public class Distributor extends BaseModel{

    public Distributor() {
        jsonObject = null;
    }

    public Distributor(JSONObject objOrder) {
        jsonObject = objOrder;
    }


    public static String getDistributorId(){
        int id_distributor = 0;
        Distributor currentDistributor = CustomSQL.getObject(Constants.DISTRIBUTOR, Distributor.class);

        if (currentDistributor != null) {
            id_distributor = currentDistributor.getInt("id");
        }
        return String.valueOf(id_distributor);
    }


    public static String getName(){
        String name = "";
        Distributor currentDistributor = CustomSQL.getObject(Constants.DISTRIBUTOR, Distributor.class);

        if (currentDistributor != null) {
            name = currentDistributor.getString("name");
        }
        return name;

    }

    public static String getLocation(){
        String location = "";
        Distributor currentDistributor = CustomSQL.getObject(Constants.DISTRIBUTOR, Distributor.class);

        if (currentDistributor != null) {
            location = currentDistributor.getString("location");
        }
        return location;

    }


}
