package wolve.dms.apiconnect;

import com.google.api.services.sheets.v4.model.AddConditionalFormatRuleRequest;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BooleanCondition;
import com.google.api.services.sheets.v4.model.BooleanRule;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.ConditionValue;
import com.google.api.services.sheets.v4.model.ConditionalFormatRule;
import com.google.api.services.sheets.v4.model.GradientRule;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.InterpolationPoint;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.TextFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import wolve.dms.libraries.connectapi.sheetapi.GoogleSheetCreateUpdateTab;
import wolve.dms.libraries.connectapi.sheetapi.GoogleSheetGetAllTab;
import wolve.dms.libraries.connectapi.sheetapi.GoogleSheetGetData;
import wolve.dms.libraries.connectapi.sheetapi.GoogleSheetPostData;
import wolve.dms.utils.Util;

public class SheetConnect {

    public static void getALlValue(String sheetId, String sheetTab, final GoogleSheetGetData.CallbackListList callback, final Boolean stoploading){
        Util.getInstance().showLoading();
        new GoogleSheetGetData(sheetId,sheetTab, new GoogleSheetGetData.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                callback.onRespone(results);
                Util.getInstance().stopLoading(stoploading);
            }
        }).execute();
    }

    public static void postValue(String sheetId, String range, List<List<Object>> params,String majorDimension, final GoogleSheetGetData.CallbackListList callback, final Boolean stopLoadung){
        Util.getInstance().showLoading();
        new GoogleSheetPostData(sheetId, range, params, majorDimension, new GoogleSheetPostData.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                callback.onRespone(results);
                Util.getInstance().stopLoading(stopLoadung);

            }
        }).execute();
    }

    public static void getALlTab(String sheetId, final GoogleSheetGetAllTab.CallbackListSheet callback, final Boolean stoploading){
        Util.getInstance().showLoading();
        new GoogleSheetGetAllTab(sheetId, new GoogleSheetGetAllTab.CallbackListSheet() {
            @Override
            public void onRespone(List<Sheet> results) {
                callback.onRespone(results);
                Util.getInstance().stopLoading(stoploading);

            }
        }).execute();
    }

    public static void createNewTab(String sheetId, String title, final GoogleSheetGetData.CallbackListList callback, final Boolean stopLoadung){
        Util.getInstance().showLoading();

        List<Request> requests = new ArrayList<>();
        Request request = new Request();
        request.setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle(title)));

        requests.add(request);

        new GoogleSheetCreateUpdateTab(sheetId, requests, new GoogleSheetCreateUpdateTab.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                callback.onRespone(results);
                Util.getInstance().stopLoading(stopLoadung);

            }
        }).execute();
    }

    public static void updateCurrentTab(String sheetId, final GoogleSheetGetData.CallbackListList callback, final Boolean stopLoadung){
        Util.getInstance().showLoading();

        List<GridRange> ranges = Collections.singletonList(new GridRange()
                .setSheetId(266674131)
//                .setSheetId(0)
                .setStartRowIndex(0)
                .setEndRowIndex(20)
                .setStartColumnIndex(0)
                .setEndColumnIndex(4)
        );

        List<Request> requests = new ArrayList<>();

        Request request = new Request();
        request.setAddConditionalFormatRule(new AddConditionalFormatRuleRequest()
                        .setIndex(1)
                        .setRule(new ConditionalFormatRule()
                                .setRanges(ranges)
                                .setBooleanRule(new BooleanRule()
                                        .setCondition(new BooleanCondition()
                                                .setType("CUSTOM_FORMULA")
                                                .setValues(Collections.singletonList(
                                                        new ConditionValue().setUserEnteredValue(
                                                                "=GT($b2,median($b$2:$b$11))")

                                                ))
                                        )
                                        .setFormat(new CellFormat()
                                                .setTextFormat(new TextFormat()
                                                        .setForegroundColor(new Color()
                                                                .setRed(1.0f))
                                                        .setBold(true)
                                        ))

                                )
                        )

                );
//                new Request().setAddConditionalFormatRule(new AddConditionalFormatRuleRequest()
//                        .setRule(new ConditionalFormatRule()
//                                .setRanges(ranges)
//                                .setBooleanRule(new BooleanRule()
//                                        .setCondition(new BooleanCondition()
//                                                .setType("CUSTOM_FORMULA")
//                                                .setValues(Collections.singletonList(
//                                                        new ConditionValue().setUserEnteredValue(
//                                                                "=LT($D2,median($D$2:$D$11))")
//                                                ))
//                                        )
//                                        .setFormat(new CellFormat().setBackgroundColor(
//                                                new Color().setRed(1f).setGreen(0.4f).setBlue(0.4f)
//                                        ))
//                                )
//                        )
//                        .setIndex(0)
//                )
//        );





//        Request request = new Request();
//        request.setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle(title)));

        requests.add(request);

        new GoogleSheetCreateUpdateTab(sheetId, requests, new GoogleSheetCreateUpdateTab.CallbackListList() {
            @Override
            public void onRespone(List<List<Object>> results) {
                callback.onRespone(results);
                Util.getInstance().stopLoading(stopLoadung);

            }
        }).execute();
    }

}
