package wolve.dms.utils;

import static wolve.dms.utils.PagerInfo.ISOA4Height;
import static wolve.dms.utils.PagerInfo.ISOA5Height;
import static wolve.dms.utils.PagerInfo.ISOA5Width;
import static wolve.dms.utils.PagerInfo.MARGINBOTTOM;
import static wolve.dms.utils.PagerInfo.MARGINLEFT;
import static wolve.dms.utils.PagerInfo.MARGINRIGHT;
import static wolve.dms.utils.PagerInfo.MARGINTOP;
import static wolve.dms.utils.PagerInfo.ROWHEIGHTDEFAULT;
import static wolve.dms.utils.PagerInfo.ROWHIGHER;
import static wolve.dms.utils.PdfDrawUtil.createBillRowData;
import static wolve.dms.utils.PdfDrawUtil.createPayableRowData;
import static wolve.dms.utils.PdfDrawUtil.createTempBillRowData;
import static wolve.dms.utils.PdfDrawUtil.drawCopyrightApp;
import static wolve.dms.utils.PdfDrawUtil.drawCustomerInfo;
import static wolve.dms.utils.PdfDrawUtil.drawHeader;
import static wolve.dms.utils.PdfDrawUtil.drawHorizontalLine;
import static wolve.dms.utils.PdfDrawUtil.drawRow;
import static wolve.dms.utils.PdfDrawUtil.drawSignature;
import static wolve.dms.utils.PdfDrawUtil.drawText;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;

public class PdfGenerator {

    public static PdfDocument createPdfOldBill(BaseModel customer, List<BaseModel> debts,  Bitmap bitmapLogo) {
        int pageHeight = ISOA4Height;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(ISOA5Width, pageHeight, 1).create();
        PdfDocument.Page currentPage = document.startPage(pageInfo1);
        Canvas canvas = currentPage.getCanvas();
        drawCopyrightApp(canvas, pageHeight);


        BaseModel distributor = Distributor.getObject();
        int bottomheader = drawHeader(canvas, bitmapLogo, distributor);

        drawText(canvas,
                String.format("CÔNG NỢ ĐẾN %s", Util.CurrentDayMonthYear()),
                R.color.black_text_color,
                16,
                true,
                false,
                Constants.CENTERPOSITION,
                MARGINLEFT,
                ISOA5Width - MARGINRIGHT,
                bottomheader + 20,
                0);

        drawCustomerInfo(canvas, customer, null, bottomheader, false);

        int line = drawHorizontalLine(canvas,
                R.color.black_text_color_hint,
                1,
                MARGINLEFT,
                ISOA5Width - MARGINRIGHT,
                bottomheader + 80);

        List<BaseModel> createDebts = createPayableRowData(debts);
        for (int i=0; i<createDebts.size(); i++){
            line = drawRow(canvas,
                    MARGINLEFT,
                    ISOA5Width - MARGINRIGHT,
                    line,
                    createDebts.get(i));

            if (line > pageHeight - 2 *MARGINBOTTOM  ){

                document.finishPage(currentPage);
                currentPage = document.startPage(pageInfo1);
                canvas = currentPage.getCanvas();
                drawCopyrightApp(canvas, pageHeight);

                line = drawHorizontalLine(canvas,
                        R.color.black_text_color_hint,
                        1,
                        MARGINLEFT,
                        ISOA5Width - MARGINRIGHT,
                        2* MARGINTOP);


            }

        }

        drawSignature(canvas, line, 2, User.getCurrentUser());

        document.finishPage(currentPage);

        return document;

    }
    public static PdfDocument createPdfBill(BaseModel customer, BaseModel bill, List<BaseModel> debts, Bitmap bitmapLogo, double paid) {
        List<BaseModel> billdetails = bill.getList(Constants.BILL_DETAIL);
        BaseModel distributor = Distributor.getObject();
        int debtheight = debts.size()> 0 ? (debts.size() + 1)* ROWHIGHER : 0;
        int pageHeight = billdetails.size() * ROWHEIGHTDEFAULT + debtheight > 78 ? ISOA4Height : ISOA5Height;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(ISOA5Width, pageHeight, 1).create();
        PdfDocument.Page currentPage = document.startPage(pageInfo1);
        Canvas canvas = currentPage.getCanvas();
        drawCopyrightApp(canvas, pageHeight);


        int bottomheader = drawHeader(canvas, bitmapLogo, distributor);

        drawText(canvas,
                "HÓA ĐƠN BÁN HÀNG",
                R.color.black_text_color,
                16,
                true,
                false,
                Constants.CENTERPOSITION,
                MARGINLEFT,
                ISOA5Width - MARGINRIGHT,
                bottomheader + 20,
                0);

        drawCustomerInfo(canvas, customer, bill, bottomheader, true);

        int line = drawHorizontalLine(canvas,
                R.color.black_text_color_hint,
                1,
                MARGINLEFT,
                ISOA5Width - MARGINRIGHT,
                bottomheader + 80);

        List<BaseModel> createDetails = createBillRowData(bill, billdetails, debts, paid);
        for (int i=0; i<createDetails.size(); i++){
            line = drawRow(canvas,
                    MARGINLEFT,
                    ISOA5Width - MARGINRIGHT,
                    line,
                    createDetails.get(i));

            if (line > ISOA4Height - 2 *MARGINBOTTOM - 80 ){

                document.finishPage(currentPage);
                currentPage = document.startPage(pageInfo1);
                canvas = currentPage.getCanvas();
                drawCopyrightApp(canvas, pageHeight);

                if (i < createDetails.size()-1){
                    line = drawHorizontalLine(canvas,
                            R.color.black_text_color_hint,
                            1,
                            MARGINLEFT,
                            ISOA5Width - MARGINRIGHT,
                            2* MARGINTOP);

                }else {
                    line = 2*MARGINTOP;
                }



            }

        }



        drawSignature(canvas, line, 1, User.getCurrentUser());


        document.finishPage(currentPage);

        return document;

    }

    public static PdfDocument createPdfTempBill(BaseModel tempbill, Bitmap bitmapLogo) {
        BaseModel bill = tempbill.getBaseModel(Constants.BILL);
        List<BaseModel> billdetails = bill.getList(Constants.BILL_DETAIL);
        BaseModel customer = tempbill.getBaseModel(Constants.CUSTOMER);
        BaseModel user = tempbill.getBaseModel(Constants.USER);
        BaseModel distributor = Distributor.getObject();

        int pageHeight = billdetails.size() * ROWHEIGHTDEFAULT > 120 ? ISOA4Height : ISOA5Height;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(ISOA5Width, pageHeight, 1).create();
        PdfDocument.Page currentPage = document.startPage(pageInfo1);
        Canvas canvas = currentPage.getCanvas();
        drawCopyrightApp(canvas, pageHeight);


        int bottomheader = drawHeader(canvas, bitmapLogo, distributor);

        drawText(canvas,
                String.format("HÓA ĐƠN TẠM %s", Util.CurrentDayMonthYear()),
                R.color.black_text_color,
                16,
                true,
                false,
                Constants.CENTERPOSITION,
                MARGINLEFT,
                ISOA5Width - MARGINRIGHT,
                bottomheader + 20,
                0);

        drawCustomerInfo(canvas, customer, bill, bottomheader, false);

        int line = drawHorizontalLine(canvas,
                R.color.black_text_color_hint,
                1,
                MARGINLEFT,
                ISOA5Width - MARGINRIGHT,
                bottomheader + 80);

        List<BaseModel> createDetails = createTempBillRowData(bill, billdetails);
        for (int i=0; i<createDetails.size(); i++){
            line = drawRow(canvas,
                    MARGINLEFT,
                    ISOA5Width - MARGINRIGHT,
                    line,
                    createDetails.get(i));

            if (line > ISOA4Height - 2 *MARGINBOTTOM - 80 ){

                document.finishPage(currentPage);
                currentPage = document.startPage(pageInfo1);
                canvas = currentPage.getCanvas();
                drawCopyrightApp(canvas, pageHeight);

                if (i < createDetails.size()-1){
                    line = drawHorizontalLine(canvas,
                            R.color.black_text_color_hint,
                            1,
                            MARGINLEFT,
                            ISOA5Width - MARGINRIGHT,
                            2* MARGINTOP);

                }else {
                    line = 2*MARGINTOP;
                }



            }

        }



        drawSignature(canvas, line, 0, user);


        document.finishPage(currentPage);

        return document;

    }




}
