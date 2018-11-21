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
        String id_distributor = "";
        Distributor currentDistributor = CustomSQL.getObject(Constants.DISTRIBUTOR, Distributor.class);

        if (currentDistributor != null) {
            id_distributor = String.valueOf(currentDistributor.getInt("id"));
        }
        return id_distributor;
    }

    public static int getId(){
        int id_distributor = 0;
        Distributor currentDistributor = CustomSQL.getObject(Constants.DISTRIBUTOR, Distributor.class);

        if (currentDistributor != null) {
            id_distributor = currentDistributor.getInt("id");
        }
        return id_distributor;
    }

    public static String getName(){
        String name = "";
        Distributor currentDistributor = CustomSQL.getObject(Constants.DISTRIBUTOR, Distributor.class);

        if (currentDistributor != null) {
            name = currentDistributor.getString("name");
        }
        return name;

    }

    public static int getLocationId(){
        int location = 0;
        Distributor currentDistributor = CustomSQL.getObject(Constants.DISTRIBUTOR, Distributor.class);

        if (currentDistributor != null) {
//            location = currentDistributor.getInt("location");
            location = 79;
        }
        return location;

    }


}
