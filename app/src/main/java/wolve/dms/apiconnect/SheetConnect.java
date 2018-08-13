package wolve.dms.apiconnect;

import java.util.List;

import wolve.dms.libraries.connectapi.GoogleSheetGet;
import wolve.dms.utils.Util;

public class SheetConnect {

    public static void getALlValue(){
        Util.getInstance().showLoading();
        new GoogleSheetGet(new GoogleSheetGet.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                Util.getInstance().stopLoading(true);
            }
        }).execute();
    }


}
