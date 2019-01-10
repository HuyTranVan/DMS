package wolve.dms.apiconnect;

import java.util.List;

import wolve.dms.libraries.connectapi.GoogleSheetGet;
import wolve.dms.libraries.connectapi.GoogleSheetPost;
import wolve.dms.utils.Util;

public class SheetConnect {

    public static void getALlValue(String sheetId,String sheetTab, final GoogleSheetGet.CallbackListList callback, final Boolean stoploading){
        Util.getInstance().showLoading();
        new GoogleSheetGet(sheetId,sheetTab, new GoogleSheetGet.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                callback.onRespone(results);
                Util.getInstance().stopLoading(stoploading);
            }
        }).execute();
    }

    public static void postValue(String sheetId, String range, List<List<Object>> params, final GoogleSheetGet.CallbackListList callback, final Boolean stopLoadung){
        Util.getInstance().showLoading();
        new GoogleSheetPost(sheetId, range, params, new GoogleSheetPost.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                callback.onRespone(results);
                Util.getInstance().stopLoading(stopLoadung);

            }
        }).execute();
    }



}
