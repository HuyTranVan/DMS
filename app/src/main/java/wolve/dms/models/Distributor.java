package wolve.dms.models;

import org.json.JSONObject;

import wolve.dms.callback.CallbackBoolean;
import wolve.dms.callback.CallbackLong;
import wolve.dms.callback.CallbackObject;
import wolve.dms.utils.Constants;
import wolve.dms.utils.CustomBottomDialog;
import wolve.dms.utils.CustomCenterDialog;
import wolve.dms.utils.CustomSQL;
import wolve.dms.utils.Util;

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

    public static int getProvinceId() {
        int province_id = 0;
        BaseModel currentDistributor = CustomSQL.getBaseModel(Constants.DISTRIBUTOR);

        if (currentDistributor != null) {
            province_id = currentDistributor.getInt("province_id");
//            location = 79;
        }
        return province_id;

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

    public static int getImportFunction(){
        int importFunction =0;
        BaseModel currentDistributor = CustomSQL.getBaseModel(Constants.DISTRIBUTOR);
        if (currentDistributor != null) {
            importFunction = currentDistributor.getInt("importFunction");
//            location = 79;
        }
        return importFunction;
    }


}
