package wolve.dms.models;

import org.json.JSONObject;

import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomSQL;

/**
 * Created by macos on 9/16/17.
 */

public class Distributor extends BaseModel {

    public Distributor() {
        jsonObject = null;
    }

    public Distributor(JSONObject objOrder) {
        jsonObject = objOrder;
    }


    public static String getDistributorId() {
        String id_distributor = "";
        BaseModel currentDistributor = CustomSQL.getBaseModel(Constants.DISTRIBUTOR);

        if (currentDistributor != null) {
            id_distributor = String.valueOf(currentDistributor.getInt("id"));
        }
        return id_distributor;
    }

    public static int getId() {
        int id_distributor = 0;
        BaseModel currentDistributor = CustomSQL.getBaseModel(Constants.DISTRIBUTOR);

        if (currentDistributor != null) {
            id_distributor = currentDistributor.getInt("id");
        }
        return id_distributor;
    }

    public static String getName() {
        String name = "";
        BaseModel currentDistributor = CustomSQL.getBaseModel(Constants.DISTRIBUTOR);

        if (currentDistributor != null) {
            name = currentDistributor.getString("name");
        }
        return name;

    }

    public static String getImage() {
        BaseModel currentDistributor = CustomSQL.getBaseModel(Constants.DISTRIBUTOR);

        return currentDistributor.getString("image");
    }

    public static int getLocationId() {
        int location = 0;
        BaseModel currentDistributor = CustomSQL.getBaseModel(Constants.DISTRIBUTOR);

        if (currentDistributor != null) {
            location = currentDistributor.getInt("location");
//            location = 79;
        }
        return location;

    }

    public static BaseModel getObject() {
        String name = "";
        BaseModel currentDistributor = CustomSQL.getBaseModel(Constants.DISTRIBUTOR);

        if (currentDistributor != null) {
            return currentDistributor;
        }
        return new BaseModel();

    }

    public static String getObjectString() {
        return CustomSQL.getString(Constants.DISTRIBUTOR);

    }


}
