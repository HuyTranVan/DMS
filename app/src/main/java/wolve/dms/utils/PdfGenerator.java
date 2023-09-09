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

public class PdfGenerator {

    //        int pageHeight = 5000;

    public static PdfDocument createPdfBill(BaseModel customer, BaseModel bill, List<BaseModel> debts, Bitmap bitmapLogo, double paid) {
        Uri uri =null;
        List<BaseModel> billdetails = bill.getList(Constants.BILL_DETAIL);
        BaseModel distributor = Distributor.getObject();

        int pageHeight = billdetails.size() * ROWHEIGHTDEFAULT + debts.size()* ROWHIGHER > 120 ? ISOA4Height : ISOA5Height;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(ISOA5Width, pageHeight, 1).create();
        PdfDocument.Page currentPage = document.startPage(pageInfo1);
        Canvas canvas = currentPage.getCanvas();
        drawCopyrightApp(canvas, pageHeight);


        int bottomheader = drawHeader(canvas, bitmapLogo, distributor, pageHeight);

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



        drawSignature(canvas, line, distributor);


        document.finishPage(currentPage);

        return document;

    }

    public static PdfDocument createPdfOldBill(BaseModel customer, List<BaseModel> debts,  Bitmap bitmapLogo) {
        int pageHeight = ISOA4Height;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(ISOA5Width, pageHeight, 1).create();
        PdfDocument.Page currentPage = document.startPage(pageInfo1);
        Canvas canvas = currentPage.getCanvas();
        drawCopyrightApp(canvas, pageHeight);


        BaseModel distributor = Distributor.getObject();
        int bottomheader = drawHeader(canvas, bitmapLogo, distributor, pageHeight);

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

        drawSignature(canvas, line, distributor);

        document.finishPage(currentPage);

        return document;

    }



    private static List<BaseModel> createBillRowData(BaseModel bill, List<BaseModel> details, List<BaseModel> debts, double paid){
        int[] colum_bill_lengths = new int[]{25, 50, 300, 350, 400, 480, ISOA5Width - MARGINRIGHT};
        int[] colum_bill_2 = new int[]{25, 480, ISOA5Width - MARGINRIGHT};
        String[] colum_bill_titles = new String[]{"STT", "Tên sản phẩm", "ĐVT", "SL", "Đơn giá", "Thành tiền"};
        int[] position_titles= new int[]{1, 1, 1, 1, 1, 1};
        int[] position_detail= new int[]{1, 0, 1, 1, 2, 2};
        int[] possum = new int[]{0, 2};
        int[] postotal = new int[]{2, 2};
        List<BaseModel> results = new ArrayList<>();

        BaseModel titleItem = new BaseModel();
        titleItem.put("text", colum_bill_titles);
        titleItem.put("columx", colum_bill_lengths);
        titleItem.put("position", position_titles);
        titleItem.put("linecolor", R.color.black_text_color_hint);
        titleItem.put("linestrokewidth", 1);
        titleItem.put("textcolor", R.color.black_text_disable);
        titleItem.put("textsize", 10);
        titleItem.put("isbold", false);
        titleItem.put("margin_bottom", 5);
        titleItem.put("margin_left", 5);
        titleItem.put("rowheight", 15);
        results.add(0, titleItem);

        for (int i=0; i<details.size(); i++){
            String[] texts = new String[]{
                    String.valueOf(i+1),
                    details.get(i).getString("productName"),
                    "--",
                    details.get(i).getString("quantity"),
                    Util.FormatMoney(details.get(i).getDouble("unitPrice") - details.get(i).getDouble("discount") ),
                    Util.FormatMoney((details.get(i).getDouble("unitPrice") - details.get(i).getDouble("discount"))*details.get(i).getInt("quantity"))
            };

            BaseModel item = new BaseModel();
            item.put("text", texts);
            item.put("columx", colum_bill_lengths);
            item.put("position", position_detail);
            item.put("linecolor", R.color.black_text_color_hint);
            item.put("linestrokewidth", 1);
            item.put("textcolor", R.color.black_text_color);
            item.put("textsize", 10);
            item.put("isbold", false);
            item.put("margin_bottom", 6);
            item.put("margin_left", 5);
            item.put("rowheight", 18);
            results.add(item);

        }

        if (debts.size()>0){
            String[] textsum = new String[]{
                    "Hoá đơn hiện tại",
                    Util.FormatMoney(bill.getDouble("total"))};

            BaseModel titleSum = new BaseModel();
            titleSum.put("text", textsum);
            titleSum.put("columx", colum_bill_2);
            titleSum.put("position", possum);
            titleSum.put("linecolor", R.color.black_text_color_hint);
            titleSum.put("linestrokewidth", 1);
            titleSum.put("textcolor", R.color.colorRed);
            titleSum.put("textsize", 10);
            titleSum.put("isbold", true);
            titleSum.put("margin_bottom", 6);
            titleSum.put("margin_left", 5);
            titleSum.put("rowheight", 18);
            results.add(titleSum);
        }

        for (int ii=0; ii<debts.size(); ii++){
            String pay =  debts.get(ii).getDouble("paid")>0.0? String.format("[Đã thanh toán -%s]", Util.FormatMoney(debts.get(ii).getDouble("paid"))) :"";
            String[] textdebt = new String[]{
                    String.format("Hoá đơn %s   + %s   %s",Util.DateHourString(debts.get(ii).getLong("createAt")) ,  Util.FormatMoney(debts.get(ii).getDouble("total")) , pay),
                    Util.FormatMoney(debts.get(ii).getDouble("debt"))};

            BaseModel titleDebt = new BaseModel();
            titleDebt.put("text", textdebt);
            titleDebt.put("columx", colum_bill_2);
            titleDebt.put("position", possum);
            titleDebt.put("linecolor", R.color.black_text_color_hint);
            titleDebt.put("linestrokewidth", 1);
            titleDebt.put("textcolor", R.color.black_text_color);
            titleDebt.put("textsize", 10);
            titleDebt.put("isbold", true);
            titleDebt.put("margin_bottom", 9);
            titleDebt.put("margin_left", 5);
            titleDebt.put("rowheight", 25);
            results.add(titleDebt);

        }

        double total = bill.getDouble("total") + DataUtil.sumValueFromList(debts, "debt");
        String[] texttotal = new String[]{
                "Tổng",
                Util.FormatMoney(total)};

        BaseModel itemtotal = new BaseModel();
        itemtotal.put("text", texttotal);
        itemtotal.put("columx", colum_bill_2);
        itemtotal.put("position", postotal);
        itemtotal.put("linecolor", R.color.black_text_color_hint);
        itemtotal.put("linestrokewidth", 1);
        itemtotal.put("textcolor", R.color.colorRed);
        itemtotal.put("textsize", 12);
        itemtotal.put("isbold", true);
        itemtotal.put("margin_bottom", 6);
        itemtotal.put("margin_left", 5);
        itemtotal.put("rowheight", 20);
        results.add(itemtotal);

        String[] textpaid = new String[]{
                "Thanh toán",
                Util.FormatMoney(paid)};

        BaseModel itempaid = new BaseModel();
        itempaid.put("text", textpaid);
        itempaid.put("columx", colum_bill_2);
        itempaid.put("position", postotal);
        itempaid.put("linecolor", R.color.black_text_color_hint);
        itempaid.put("linestrokewidth", 1);
        itempaid.put("textcolor", R.color.colorBlue);
        itempaid.put("textsize", 12);
        itempaid.put("isbold", true);
        itempaid.put("margin_bottom", 6);
        itempaid.put("margin_left", 5);
        itempaid.put("rowheight", 20);
        results.add(itempaid);

        String[] textremain = new String[]{
                "Còn lại",
                Util.FormatMoney(total - paid)};

        BaseModel itemremain = new BaseModel();
        itemremain.put("text", textremain);
        itemremain.put("columx", colum_bill_2);
        itemremain.put("position", postotal);
        itemremain.put("linecolor", R.color.black_text_color_hint);
        itemremain.put("linestrokewidth", 1);
        itemremain.put("textcolor", R.color.colorRed);
        itemremain.put("textsize", 12);
        itemremain.put("isbold", true);
        itemremain.put("margin_bottom", 6);
        itemremain.put("margin_left", 5);
        itemremain.put("rowheight", 20);
        results.add(itemremain);

        return results;
    }

    private static List<BaseModel> createPayableRowData(List<BaseModel> debts){
        int[] colum_payable_x = new int[]{25, 75, 280, 315, 350, 415, 485, ISOA5Width - MARGINRIGHT};
        int[] colum_payable_x3 = new int[]{25, colum_payable_x[5], colum_payable_x[6], ISOA5Width - MARGINRIGHT};
        int[] colum_payable_x2 = new int[]{25, colum_payable_x[6], ISOA5Width - MARGINRIGHT};
        String[] colum_payable_titles = new String[]{"Ngày", "Tên sản phẩm", "ĐVT", "SL", "Đơn giá", "Thành tiền", "Tổng"};
        int[] position_titles= new int[]{1, 1, 1, 1, 1, 1, 1};
        int[] position_debt= new int[]{1, 0, 1, 1, 2, 2, 2};
        int[] possum = new int[]{0, 2, 2};
        int[] postotal = new int[]{2, 2};
        double sum = 0.0;

        List<BaseModel> results = new ArrayList<>();

        //Title
        BaseModel titleItem = new BaseModel();
        titleItem.put("text", colum_payable_titles);
        titleItem.put("columx", colum_payable_x);
        titleItem.put("position", position_titles);
        titleItem.put("linecolor", R.color.black_text_color_hint);
        titleItem.put("linestrokewidth", 1);
        titleItem.put("textcolor", R.color.black_text_disable);
        titleItem.put("textsize", 10);
        titleItem.put("isbold", false);
        titleItem.put("margin_bottom", 5);
        titleItem.put("margin_left", 5);
        titleItem.put("rowheight", 15);
        results.add(0, titleItem);

        for (int i=0; i<debts.size(); i++){
            List<BaseModel> details = debts.get(i).getList("billDetails");
            List<BaseModel> payments = debts.get(i).getList("payments");

            for (int ii =0; ii<details.size(); ii++){
                sum += (details.get(ii).getDouble("unitPrice") - details.get(ii).getDouble("discount"))*details.get(ii).getInt("quantity");
                String[] texts = new String[]{
                        Util.DateMonthYearString(details.get(ii).getLong("createAt")),
                        details.get(ii).getString("productName"),
                        "--",
                        details.get(ii).getString("quantity"),
                        Util.FormatMoney(details.get(ii).getDouble("unitPrice") -  details.get(ii).getDouble("discount")),
                        Util.FormatMoney((details.get(ii).getDouble("unitPrice") - details.get(ii).getDouble("discount"))*details.get(ii).getInt("quantity")),
                        Util.FormatMoney(sum)};

                BaseModel item = new BaseModel();
                item.put("text", texts);
                item.put("columx", colum_payable_x);
                item.put("position", position_debt);
                item.put("linecolor", R.color.black_text_color_hint);
                item.put("linestrokewidth", 1);
                item.put("textcolor", R.color.black_text_color);
                item.put("textsize", 10);
                item.put("isbold", false);
                item.put("margin_bottom", 6);
                item.put("margin_left", 5);
                item.put("rowheight", 18);
                results.add(item);

            }

            String[] textsum = new String[]{
                    String.format("%s Tổng hoá đơn", Util.DateMonthYearString(debts.get(i).getLong("createAt"))),
                    Util.FormatMoney(debts.get(i).getDouble("total")),
                    Util.FormatMoney(sum)};


            BaseModel titleSum = new BaseModel();
            titleSum.put("text", textsum);
            titleSum.put("columx", colum_payable_x3);
            titleSum.put("position", possum);
            titleSum.put("linecolor", R.color.black_text_color_hint);
            titleSum.put("linestrokewidth", 1);
            titleSum.put("textcolor", R.color.colorRed);
            titleSum.put("textsize", 10);
            titleSum.put("isbold", false);
            titleSum.put("margin_bottom", 6);
            titleSum.put("margin_left", 5);
            titleSum.put("rowheight", 18);
            results.add(titleSum);

            for (int z =0; z<payments.size(); z++){
                sum -=payments.get(z).getDouble("paid");
                String[] textpa = new String[]{
                        String.format("%s Thanh toán", Util.DateString(payments.get(z).getLong("createAt"))),
                        "-" + Util.FormatMoney(payments.get(z).getDouble("paid")),
                        Util.FormatMoney(sum)};

                BaseModel itempa = new BaseModel();
                itempa.put("text", textpa);
                itempa.put("columx", colum_payable_x3);
                itempa.put("position", possum);
                itempa.put("linecolor", R.color.black_text_color_hint);
                itempa.put("linestrokewidth", 1);
                itempa.put("textcolor", R.color.colorBlue);
                itempa.put("textsize", 10);
                itempa.put("isbold", false);
                itempa.put("margin_bottom", 6);
                itempa.put("margin_left", 5);
                itempa.put("rowheight", 18);
                results.add(itempa);

            }

        }

        String[] texttotal = new String[]{
                String.format("Tổng công nợ đến %s", Util.CurrentDayMonthYear()),
                Util.FormatMoney(sum)};

        BaseModel itemtotal = new BaseModel();
        itemtotal.put("text", texttotal);
        itemtotal.put("columx", colum_payable_x2);
        itemtotal.put("position", postotal);
        itemtotal.put("linecolor", R.color.black_text_color_hint);
        itemtotal.put("linestrokewidth", 1);
        itemtotal.put("textcolor", R.color.black_text_color);
        itemtotal.put("textsize", 12);
        itemtotal.put("isbold", true);
        itemtotal.put("margin_bottom", 6);
        itemtotal.put("margin_left", 5);
        itemtotal.put("rowheight", 20);
        results.add(itemtotal);


        return results;


    }

}
