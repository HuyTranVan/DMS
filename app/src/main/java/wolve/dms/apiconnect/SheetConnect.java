package wolve.dms.apiconnect;

import java.util.List;

import wolve.dms.libraries.connectapi.GoogleSheetGet;
import wolve.dms.libraries.connectapi.GoogleSheetPost;
import wolve.dms.utils.Util;

public class SheetConnect {

    public static void getALlValue(final GoogleSheetGet.CallbackListList callback, final Boolean stoploading){
        Util.getInstance().showLoading();
        new GoogleSheetGet(new GoogleSheetGet.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                callback.onRespone(results);
                Util.getInstance().stopLoading(stoploading);
            }
        }).execute();
    }

    public static void postValue(String range, List<List<Object>> params, final Boolean stopLoadung){
        Util.getInstance().showLoading();
        new GoogleSheetPost(range, params, new GoogleSheetPost.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                Util.getInstance().stopLoading(stopLoadung);

            }
        }).execute();
    }



}
