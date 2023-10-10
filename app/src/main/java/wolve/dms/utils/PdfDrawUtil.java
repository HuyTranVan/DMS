package wolve.dms.utils;

import static wolve.dms.utils.PagerInfo.ISOA5Width;
import static wolve.dms.utils.PagerInfo.LOGOHEIGHT;
import static wolve.dms.utils.PagerInfo.MARGINBOTTOM;
import static wolve.dms.utils.PagerInfo.MARGINLEFT;
import static wolve.dms.utils.PagerInfo.MARGINRIGHT;
import static wolve.dms.utils.PagerInfo.MARGINTOP;
import static wolve.dms.utils.PagerInfo.ROWHEIGHTDEFAULT;
import static wolve.dms.utils.PagerInfo.ROWHIGHER;
import static wolve.dms.utils.PagerInfo.ROWHIGHMIN;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import wolve.dms.R;
import wolve.dms.libraries.BitmapView;
import wolve.dms.models.BaseModel;
import wolve.dms.models.Distributor;
import wolve.dms.models.User;

public class PdfDrawUtil {
    public static void drawText(Canvas canvas,
                                String text,
                                int color,
                                int textSize,
                                boolean isBold,
                                boolean isIcon,
                                int position,  // 0: left  1: center  2:right
                                int startX,
                                int endX,
                                int startY,
                                int margin){
        Paint paint = new Paint();
        paint.setColor(Util.getInstance().getCurrentActivity().getResources().getColor(color, null));
        paint.setTextSize(textSize);
        Typeface customTypeface =   isIcon ?
                Typeface.createFromAsset( Util.getInstance().getCurrentActivity().getAssets(), "font_awesome.ttf"):
                Typeface.createFromAsset(Util.getInstance().getCurrentActivity().getAssets(), "opensans_regular.ttf");
        paint.setTypeface(customTypeface);

        paint.setFakeBoldText(isBold);

        if (position == Constants.LEFTPOSITION ){
            canvas.drawText(text,
                    startX + margin,
                    startY,
                    paint);

        }else if (position == Constants.CENTERPOSITION){
            canvas.drawText(text,
                    startX + (endX - startX - paint.measureText(text)) / 2,
                    startY,
                    paint);

        }else if (position ==  Constants.RIGHTPOSITION){
            canvas.drawText(text,
                    endX - paint.measureText(text) - margin,
                    startY,
                    paint);
        }

    }

    public static int drawDrawable(Canvas canvas,
                                   int drawbleImage,
                                   int startX,
                                   int startY,
                                   int height){
        Drawable drawable = ContextCompat.getDrawable(Util.getInstance().getCurrentActivity(), drawbleImage);
        drawable.setBounds(startX,
                startY,
                startX +  drawable.getIntrinsicWidth() * height / drawable.getIntrinsicHeight(),
                startY + height );
        drawable.draw(canvas);

        return drawable.getBounds().bottom;
    }

    public static int drawImage(Canvas canvas,
                                   Bitmap bm,
                                   int startX,
                                   int startY,
                                   int imageHeight){
        Bitmap bitmap  = BitmapView.ResizeBitMapDependHeight(bm, imageHeight);
        Drawable drawable = new BitmapDrawable(Util.getInstance().getCurrentActivity().getResources(), bitmap);
        drawable.setBounds(startX,
                startY,
                startX + drawable.getIntrinsicWidth() ,
                startY + imageHeight );
        drawable.draw(canvas);

        return drawable.getBounds().bottom;

    }

    public static int drawHorizontalLine(Canvas canvas,
                                         int color,
                                         int strokewidth,
                                         int startX,
                                         int endX,
                                         int startY
                                         ){
        Paint paint = new Paint();
        paint.setColor(Util.getInstance().getCurrentActivity().getColor(color));
        paint.setStrokeWidth(strokewidth);
        canvas.drawLine(startX,
                startY,
                endX,
                startY,
                paint);

        return startY;
    }

    public static int drawRetangle(Canvas canvas,
                                         int color,
                                         int strokewidth,
                                         int startX,
                                         int endX,
                                         int startY,
                                        int height){
        Paint paint = new Paint();
        paint.setColor(Util.getInstance().getCurrentActivity().getColor(color));
        paint.setStrokeWidth(strokewidth);
        canvas.drawRect(startX,
                startY,
                endX,
                startY+ height,
                paint);

        return startY;
    }

    public static int drawVerticalLine(Canvas canvas,
                                         int color,
                                         int strokewidth,
                                         int startX,
                                         int startY,
                                         int length){
        Paint paint = new Paint();
        paint.setColor(Util.getInstance().getCurrentActivity().getColor(color));
        paint.setStrokeWidth(1);
        canvas.drawLine(startX,
                startY,
                startX,
                startY + length,
                paint);

        return startY + strokewidth;
    }


    public static int drawHeader(Canvas canvas,
                                 Bitmap bitmapLogo,
                                 BaseModel distributor){
        int logobottom = 0;
        if (bitmapLogo != null){
            logobottom = drawImage(canvas,
                    bitmapLogo,
                    MARGINLEFT,
                    MARGINTOP,
                    LOGOHEIGHT);

        }else {
            logobottom = drawDrawable(canvas,
                    R.drawable.lub_logo_red,
                    MARGINLEFT ,
                    MARGINTOP,
                    45);

        }

        drawText(canvas,
                Util.getStringIcon(distributor.getString("company"),"     ", R.string.icon_building ),
                R.color.black_text_color,
                9,
                true,
                true,
                Constants.RIGHTPOSITION,
                0,
                ISOA5Width - MARGINRIGHT,
                MARGINTOP + 8,
                0);

        drawText(canvas,
                Util.getStringIcon(distributor.getString("address"), "     ",(R.string.icon_district)),
                R.color.black_text_color,
                9,
                false,
                true,
                Constants.RIGHTPOSITION,
                0,
                ISOA5Width - MARGINRIGHT,
                MARGINTOP + 20,
                0);

        drawText(canvas,
                Util.getStringIcon(Util.FormatPhone(distributor.getString("phone")), "     ",R.string.icon_phone ),
                R.color.black_text_color,
                9,
                false,
                true,
                Constants.RIGHTPOSITION,
                0,
                ISOA5Width - MARGINRIGHT,
                MARGINTOP + 32,
                0);
        drawText(canvas,
                Util.getStringIcon(distributor.getString("website"), "     ",R.string.icon_global),
                R.color.black_text_color,
                9,
                false,
                true,
                Constants.RIGHTPOSITION,
                0,
                ISOA5Width - MARGINRIGHT,
                MARGINTOP + 44,
                0);


        drawHorizontalLine(canvas,
                R.color.black_text_color_hint,
                1,
                MARGINLEFT,
                ISOA5Width - MARGINRIGHT,
                logobottom + 5
        );

        return logobottom + 5;

    }

    public static void drawCopyrightApp(Canvas canvas, int pageheight){
        drawText(canvas,
                "export by Lubsolution DMS",
                R.color.colorGrey,
                6,
                false,
                false,
                2,
                0,
                ISOA5Width - MARGINRIGHT,
                pageheight - MARGINBOTTOM/2,
                0);

    }

    public static void drawCustomerInfo(Canvas canvas,
                                       BaseModel customer,
                                       BaseModel bill,
                                       int startY,
                                        boolean showdeliver){
        drawText(canvas,
                "Khách hàng",
                R.color.black_text_color,
                10,
                false,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT,
                0,
                startY + 40,
                0);
        drawText(canvas,
                "Liên hệ",
                R.color.black_text_color,
                10,
                false,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT,
                0,
                startY + 55,
                0);
        drawText(canvas,
                "Địa chỉ",
                R.color.black_text_color,
                10,
                false,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT,
                0,
                startY + 70,
                0);

        drawText(canvas,
                String.format(": %s %s", Constants.shopName[customer.getInt("shopType")].toUpperCase(), customer.getString("signBoard").toUpperCase()),
                R.color.black_text_color,
                10,
                true,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT + 60,
                0,
                startY + 40,
                0);
        drawText(canvas,
                String.format(": %s - %s",
                        customer.getString("name"),
                        customer.getString("phone").equals("") ? "--" : Util.FormatPhone(customer.getString("phone"))),
                R.color.black_text_color,
                10,
                false,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT + 60,
                0,
                startY + 55,
                0);
        drawText(canvas,
                String.format(": %s, %s" , customer.getString("district"), customer.getString("province")),
                R.color.black_text_color,
                10,
                false,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT + 60,
                0,
                startY + 70,
                0);


        drawText(canvas,
                "Ngày",
                R.color.black_text_color,
                10,
                false,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT + 350,
                0,
                startY + 40,
                0);
        drawText(canvas,
                "Bán hàng",
                R.color.black_text_color,
                10,
                false,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT + 350,
                0,
                startY + 55,
                0);

        if (showdeliver){
            drawText(canvas,
                    "Giao hàng",
                    R.color.black_text_color,
                    10,
                    false,
                    false,
                    Constants.LEFTPOSITION,
                    MARGINLEFT + 350,
                    0,
                    startY + 70,
                    0);
        }


        drawText(canvas,
                String.format(": %s" , Util.CurrentMonthYearHour()),
                R.color.black_text_color,
                10,
                true,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT + 350 + 60,
                0,
                startY + 40,
                0);
        drawText(canvas,
                String.format(": %s" , bill != null?  bill.getBaseModel("user").getString("displayName") : User.getFullName()),
                R.color.black_text_color,
                10,
                false,
                false,
                Constants.LEFTPOSITION,
                MARGINLEFT + 350 + 60,
                0,
                startY + 55,
                0);
        if (showdeliver){
            drawText(canvas,
                    String.format(": %s" , User.getFullName()),
                    R.color.black_text_color,
                    10,
                    false,
                    false,
                    Constants.LEFTPOSITION,
                    MARGINLEFT + 350 + 60,
                    0,
                    startY + 70,
                    0);
        }

    }
    public static int drawSignature(Canvas canvas,
                                    int startY,
                                    int type,
                                    BaseModel user){     // 0: temp     1: newbill     2: debt
        drawText(canvas,
                Distributor.getThanks(),
                R.color.black_text_color,
                10,
                false,
                false,
                0,
                MARGINLEFT,
                ISOA5Width /2,
                startY + 15,
                0);

        drawText(canvas,
                String.format("Số điện thoại đặt hàng: %s" ,Util.FormatPhone(User.getContact())),
                R.color.black_text_color,
                12,
                true,
                false,
                0,
                MARGINLEFT,
                ISOA5Width /2,
                startY + 30,
                0);

        if (type != 0) {
            drawText(canvas,
                    "Khách hàng",
                    R.color.black_text_color,
                    10,
                    true,
                    false,
                    1,
                    0,
                    ISOA5Width / 2,
                    startY + 45,
                    0);
            drawText(canvas,
                    "(Ký & Họ tên)",
                    R.color.black_text_color,
                    8,
                    false,
                    false,
                    1,
                    0,
                    ISOA5Width / 2,
                    startY + 55,
                    0);

        }


        drawText(canvas,
                "Nhân viên bán hàng",
                R.color.black_text_color,
                10,
                true,
                false,
                1,
                ISOA5Width /2,
                ISOA5Width,
                startY + 45,
                0);

        drawText(canvas,
                "(Ký & Họ tên)",
                R.color.black_text_color,
                8,
                false,
                false,
                1,
                ISOA5Width /2,
                ISOA5Width,
                startY + 55,
                0);

        if (type == 0 || type == 2){
            drawText(canvas,
                    user.getString("displayName"),
                    R.color.colorRed,
                    10,
                    true,
                    false,
                    1,
                    ISOA5Width /2,
                    ISOA5Width,
                    startY + 85,
                    0);

        }else if (type == 1){
            drawSign(canvas, startY + 55);

        }






        return 0;
    }

    public static void drawSign(Canvas canvas, int startY){
        drawRetangle(canvas,
                R.color.colorGreenLight,
                60,
                ISOA5Width * 3/4 - 60,
                ISOA5Width * 3/4 + 60,
                startY + 10,
                60
        );

        drawText(canvas,
                Util.getIcon(R.string.icon_check),
                R.color.lightGreenDark,
                40,
                true,
                true,
                1,
                ISOA5Width * 3/4 - 60,
                ISOA5Width * 3/4 + 60,
                startY + 50,
                10);

        drawText(canvas,
                "Được ký:",
                R.color.colorRed,
                8,
                false,
                false,
                0,
                ISOA5Width * 3/4 - 60,
                ISOA5Width * 3/4 + 60,
                startY + 20,
                5);

        drawText(canvas,
                User.getFullName(),
                R.color.colorRed,
                10,
                true,
                false,
                0,
                ISOA5Width * 3/4 - 60,
                ISOA5Width * 3/4 + 60,
                startY + 35,
                10);

        drawText(canvas,
                "Ngày phát hành:",
                R.color.colorRed,
                8,
                false,
                false,
                0,
                ISOA5Width * 3/4 - 60,
                ISOA5Width * 3/4 + 60,
                startY + 50,
                5);

        drawText(canvas,
                Util.CurrentMonthYearHour(),
                R.color.colorRed,
                10,
                true,
                false,
                0,
                ISOA5Width * 3/4 - 60,
                ISOA5Width * 3/4 + 60,
                startY + 65,
                10);




    }

    public static int drawRow(Canvas canvas,
                              int startX,
                              int endX,
                              int startY,
                              BaseModel item){
        String[] text = item.getSTRINGArray("text");
        int[] columX = item.getINTArray("columx");   //size = textsize + 1
        int[] position = item.getINTArray("position");

        int bottomline = drawHorizontalLine(canvas,
                item.getInt("linecolor"),
                item.getInt("linestrokewidth"),
                startX,
                endX,
                startY+item.getInt("rowheight"));

        for (int i =0; i < text.length; i++) {
            drawText(canvas,
                    text[i],
                    item.getInt("textcolor"),
                    item.getInt("textsize"),
                    item.getBoolean("isbold"),
                    false,
                    position[i],
                    columX[i],
                    columX[i+1],
                    bottomline - item.getInt("margin_bottom"),
                    item.getInt("margin_left"));



            drawVerticalLine(canvas,
                    item.getInt("linecolor"),
                    item.getInt("linestrokewidth"),
                    columX[i],
                    startY ,
                    item.getInt("rowheight"));
        }

        drawVerticalLine(canvas,
                item.getInt("linecolor"),
                item.getInt("linestrokewidth"),
                columX[columX.length-1],
                startY ,
                item.getInt("rowheight"));


        return startY+item.getInt("rowheight");
    }

    public static List<BaseModel> createPayableRowData(List<BaseModel> debts){
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
        titleItem.put("textcolor", R.color.black_text_color);
        titleItem.put("textsize", 10);
        titleItem.put("isbold", true);
        titleItem.put("margin_bottom", 4);
        titleItem.put("margin_left", 5);
        titleItem.put("rowheight", ROWHIGHMIN);
        results.add(0, titleItem);

        for (int i=0; i<debts.size(); i++){
            List<BaseModel> details = debts.get(i).getList("billDetails");
            List<BaseModel> payments = debts.get(i).getList("payments");

            for (int ii =0; ii<details.size(); ii++){
                sum += (details.get(ii).getDouble("unitPrice") - details.get(ii).getDouble("discount"))*details.get(ii).getInt("quantity");
                String[] texts = new String[]{
                        Util.DateMonthYearString(details.get(ii).getLong("createAt")),
                        details.get(ii).getString("productName"),
                        Util.isEmpty(details.get(i).getString("unit")) ? "--" : details.get(i).getString("unit"),
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
                item.put("rowheight", ROWHEIGHTDEFAULT);
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
            titleSum.put("rowheight", ROWHEIGHTDEFAULT);
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
                itempa.put("rowheight", ROWHEIGHTDEFAULT);
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
        itemtotal.put("rowheight", ROWHIGHER);
        results.add(itemtotal);


        return results;


    }
    public static List<BaseModel> createBillRowData(BaseModel bill, List<BaseModel> details, List<BaseModel> debts, double paid){
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
        titleItem.put("textcolor", R.color.black_text_color);
        titleItem.put("textsize", 10);
        titleItem.put("isbold", true);
        titleItem.put("margin_bottom", 4);
        titleItem.put("margin_left", 5);
        titleItem.put("rowheight", ROWHIGHMIN);
        results.add(0, titleItem);

        for (int i=0; i<details.size(); i++){
            String[] texts = new String[]{
                    String.valueOf(i+1),
                    details.get(i).getString("productName"),
                    Util.isEmpty(details.get(i).getString("unit")) ? "--" : details.get(i).getString("unit"),
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
            item.put("rowheight", ROWHEIGHTDEFAULT);
            results.add(item);

        }

        if (debts.size()>0){
            String[] textsum = new String[]{
                    "Hoá đơn hiện tại",
                    Util.FormatMoney(bill.getDouble("total"))};

            BaseModel titleSum = new BaseModel();
            titleSum.put("text", textsum);
            titleSum.put("columx", colum_bill_2);
            titleSum.put("position", postotal);
            titleSum.put("linecolor", R.color.black_text_color_hint);
            titleSum.put("linestrokewidth", 1);
            titleSum.put("textcolor", R.color.colorRed);
            titleSum.put("textsize", 10);
            titleSum.put("isbold", true);
            titleSum.put("margin_bottom", 7);
            titleSum.put("margin_left", 5);
            titleSum.put("rowheight", ROWHIGHER);
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
            titleDebt.put("margin_bottom", 7);
            titleDebt.put("margin_left", 5);
            titleDebt.put("rowheight", ROWHIGHER);
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
        itemtotal.put("rowheight", ROWHIGHER);
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
        itempaid.put("rowheight", ROWHIGHER);
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
        itemremain.put("rowheight", ROWHIGHER);
        results.add(itemremain);

        return results;
    }

    public static List<BaseModel> createTempBillRowData(BaseModel bill,  List<BaseModel> details){
        int[] colum_bill_lengths = new int[]{25, 50, 300, 350, 400, 480, ISOA5Width - MARGINRIGHT};
        int[] colum_bill_2 = new int[]{25, 480, ISOA5Width - MARGINRIGHT};
        String[] colum_bill_titles = new String[]{"STT", "Tên sản phẩm", "ĐVT", "SL", "Đơn giá", "Thành tiền"};
        int[] position_titles= new int[]{1, 1, 1, 1, 1, 1};
        int[] position_detail= new int[]{1, 0, 1, 1, 2, 2};
        int[] postotal = new int[]{2, 2};
        List<BaseModel> results = new ArrayList<>();

        BaseModel titleItem = new BaseModel();
        titleItem.put("text", colum_bill_titles);
        titleItem.put("columx", colum_bill_lengths);
        titleItem.put("position", position_titles);
        titleItem.put("linecolor", R.color.black_text_color_hint);
        titleItem.put("linestrokewidth", 1);
        titleItem.put("textcolor", R.color.black_text_color);
        titleItem.put("textsize", 10);
        titleItem.put("isbold", true);
        titleItem.put("margin_bottom", 4);
        titleItem.put("margin_left", 5);
        titleItem.put("rowheight", ROWHIGHMIN);
        results.add(0, titleItem);

        for (int i=0; i<details.size(); i++){
            String[] texts = new String[]{
                    String.valueOf(i+1),
                    details.get(i).getString("productName"),
                    details.get(i).getString("unit"),
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
            item.put("rowheight", ROWHEIGHTDEFAULT);
            results.add(item);

        }

        String[] texttotal = new String[]{
                "Tổng hóa đơn",
                Util.FormatMoney(bill.getDouble("total"))};

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
        itemtotal.put("rowheight", ROWHIGHER);
        results.add(itemtotal);

        return results;
    }



}

//    public static int  drawBillTable(Canvas canvas,
//                                     int strokewidth,
//                                     BaseModel bill,
//                                     List<BaseModel> billdetails,
//                                     List<BaseModel> debts,
//                                     Double paid,
//                                     String[] columtitles,
//                                     int[] columlength,
//                                     int startX,
//                                     int endX,
//                                     int startY,
//                                     int rowheight,
//                                     int rowdebtheight){
//        Paint paint = new Paint();
//        paint.setStrokeWidth(strokewidth);
//
//        drawHorizontalLine(canvas,
//                R.color.black_text_color_hint,
//                1,
//                startX,
//                endX,
//                startY);
//        for (int x =0; x<columtitles.length; x++){
//            drawText(canvas,
//                    columtitles[x],
//                    R.color.black_text_color,
//                    10,
//                    false,
//                    false,
//                    1,
//                    columlength[x],
//                    columlength[x+1],
//                    startY + 15 - 5,
//                    columlength[x]);
//            drawVerticalLine(canvas,
//                    R.color.black_text_color_hint,
//                    1,
//                    columlength[x],
//                    startY,
//                    15
//            );
//
//        }
//        int lineX = drawHorizontalLine(canvas,
//                R.color.black_text_color_hint,
//                1,
//                startX,
//                endX,
//                startY + 15);
//
//
//        for (int i = 0; i < billdetails.size(); i++){
//            BaseModel detail = billdetails.get(i);
//            drawHorizontalLine(canvas,
//                    R.color.black_text_color_hint,
//                    strokewidth,
//                    startX,
//                    endX,
//                    lineX + (i+1) * rowheight);
//
//            String[] text = new String[]{
//                    String.valueOf(i+1),
//                    detail.getString("productName"),
//                    "--",
//                    detail.getString("quantity"),
//                    Util.FormatMoney(detail.getDouble("unitPrice") - detail.getDouble("discount") ),
//                    Util.FormatMoney((detail.getDouble("unitPrice") - detail.getDouble("discount"))*detail.getInt("quantity"))
//                    };
//            int[] pos= new int[]{1, 0, 1, 2, 2, 2};
//
//            for (int ii =0; ii<text.length; ii++){
//                drawText(canvas,
//                        text[ii],
//                        R.color.black_text_color,
//                        10,
//                        false,
//                        false,
//                        pos[ii],
//                        columlength[ii],
//                        columlength[ii+1],
//                        lineX + (i+1) * rowheight - 6,
//                        5);
//
//                drawVerticalLine(canvas,
//                        R.color.black_text_color_hint,
//                        1,
//                        columlength[ii],
//                        lineX + i * rowheight -1 ,
//                        rowheight + 1);
//            }
//
//        }
//        int lineXX = startY + 15 + rowheight*billdetails.size();
//        if (debts.size()>0){
//            lineXX = drawDebtTable(canvas,
//                    strokewidth,
//                    bill.getDouble("total"),
//                    debts,
//                    columlength,
//                    startX,
//                    endX,
//                    startY + 15 + rowheight*billdetails.size(),
//                    rowheight,
//                    rowdebtheight);
//        }
//
//        String[] sumtitle = new String[]{"Tổng" , " Thanh toán", "Còn lại"};
//        double total = bill.getDouble("total") + DataUtil.sumValueFromList(debts, "debt");
//        String[] sumvalue = new String[]{Util.FormatMoney(total),
//                                            Util.FormatMoney(paid),
//                                            Util.FormatMoney(total - paid)};
//
//        for (int a = 0; a < sumtitle.length; a++){
//            drawHorizontalLine(canvas,
//                    R.color.black_text_color_hint,
//                    strokewidth,
//                    startX,
//                    endX,
//                     lineXX + (a+1) * rowheight);
//
//            for (int b=0; b<2; b++){
//                if (b ==0){
//                    drawText(canvas,
//                            sumtitle[a],
//                            R.color.black_text_color,
//                            10,
//                            true,
//                            false,
//                            2,
//                            columlength[0],
//                            columlength[5],
//                            lineXX + (a+1) * rowheight - 6,
//                            10);
//
//                    drawVerticalLine(canvas,
//                            R.color.black_text_color_hint,
//                            1,
//                            columlength[0],
//                            lineXX + a * rowheight ,
//                            rowheight);
//
//                } else if (b == 1) {
//                    drawText(canvas,
//                            sumvalue[a],
//                            R.color.black_text_color,
//                            12,
//                            true,
//                            false,
//                            2,
//                            columlength[5],
//                            columlength[6],
//                            lineXX + (a+1) * rowheight - 5,
//                            5);
//
//                    drawVerticalLine(canvas,
//                            R.color.black_text_color_hint,
//                            1,
//                            columlength[5],
//                            lineXX + a * rowheight ,
//                            rowheight);
//                }
//
//
//            }
//
//        }
//        int currentbillheight = debts.size()>0?rowheight* (billdetails.size() +4) : rowheight* (billdetails.size() +3);
//        int YEnd = startY + 15 + currentbillheight+ rowdebtheight * debts.size();
//
//        drawVerticalLine(canvas,
//                R.color.black_text_color_hint,
//                1,
//                endX,
//                startY,
//                YEnd - startY );
//
//        return YEnd;
//    }
//
//    public static int  drawDebtTable(Canvas canvas,
//                                     int strokewidth,
//                                     double billtotal,
//                                     List<BaseModel> debts,
//                                     int[] columlength,
//                                     int startX,
//                                     int endX,
//                                     int startYY,
//                                     int rowheight,
//                                     int rowdebtheight){
//        DataUtil.sortbyStringKey("createAt", debts, true);
//        int startY = startYY + rowheight;
//        drawHorizontalLine(canvas,
//                R.color.black_text_color_hint,
//                strokewidth,
//                startX,
//                endX,
//                startY);
//        drawText(canvas,
//                "Hoá đơn hiện tại",
//                R.color.black_text_color,
//                10,
//                true,
//                false,
//                2,
//                columlength[0],
//                columlength[5],
//                startYY + rowheight - 6,
//                5);
//        drawVerticalLine(canvas,
//                R.color.black_text_color_hint,
//                1,
//                columlength[0],
//                startYY,
//                rowdebtheight);
//        drawText(canvas,
//                Util.FormatMoney(billtotal),
//                R.color.black_text_color,
//                12,
//                true,
//                false,
//                2,
//                columlength[5],
//                columlength[6],
//                startYY + rowheight - 5,
//                5);
//
//        drawVerticalLine(canvas,
//                R.color.black_text_color_hint,
//                1,
//                columlength[5],
//                startYY  ,
//                rowdebtheight);
//
//        for (int i = 0; i < debts.size(); i++){
//            drawHorizontalLine(canvas,
//                    R.color.black_text_color_hint,
//                    strokewidth,
//                    startX,
//                    endX,
//                    startY + (i+1) * rowdebtheight);
//
//
//            for (int b=0; b<2; b++){
//                if (b ==0){
//                    drawText(canvas,
//                            String.format("Hoá đơn %s", Util.DateHourString(debts.get(i).getLong("createAt"))),
//                            R.color.black_text_color_hint,
//                            8,
//                            false,
//                            false,
//                            0,
//                            columlength[0],
//                            columlength[5],
//                            startY + (i+1) * rowdebtheight - 15,
//                            5);
//
//                    drawText(canvas,
//                            String.format("Tổng: %s", Util.FormatMoney(debts.get(i).getDouble("total"))),
//                            R.color.black_text_color,
//                            10,
//                            false,
//                            false,
//                            0,
//                            columlength[0],
//                            columlength[5],
//                            startY + (i+1) * rowdebtheight - 4,
//                            5);
//
//                    drawText(canvas,
//                            String.format("Đã thanh toán: %s", Util.FormatMoney(debts.get(i).getDouble("paid"))),
//                            R.color.black_text_color,
//                            10,
//                            false,
//                            false,
//                            0,
//                            columlength[0] + 150,
//                            columlength[5],
//                            startY + (i+1) * rowdebtheight - 4,
//                            5);
//
//                    drawVerticalLine(canvas,
//                            R.color.black_text_color_hint,
//                            1,
//                            columlength[0],
//                            startY + i * rowdebtheight ,
//                            rowdebtheight);
//
//                } else if (b == 1) {
//                    drawText(canvas,
//                            Util.FormatMoney(debts.get(i).getDouble("debt")),
//                            R.color.black_text_color,
//                            10,
//                            true,
//                            false,
//                            2,
//                            columlength[5],
//                            columlength[6],
//                            startY + (i+1) * rowdebtheight - 8,
//                            5);
//
//                    drawVerticalLine(canvas,
//                            R.color.black_text_color_hint,
//                            1,
//                            columlength[5],
//                            startY + i * rowdebtheight ,
//                            rowdebtheight);
//                }
//
//
//            }
//
//
//
//
//
//        }
//
//        return startY + debts.size() * rowdebtheight;
//    }

