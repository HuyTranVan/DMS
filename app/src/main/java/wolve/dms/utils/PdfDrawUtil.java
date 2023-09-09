package wolve.dms.utils;

import static wolve.dms.utils.PagerInfo.ISOA5Width;
import static wolve.dms.utils.PagerInfo.LOGOHEIGHT;
import static wolve.dms.utils.PagerInfo.MARGINBOTTOM;
import static wolve.dms.utils.PagerInfo.MARGINLEFT;
import static wolve.dms.utils.PagerInfo.MARGINRIGHT;
import static wolve.dms.utils.PagerInfo.MARGINTOP;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.List;

import wolve.dms.R;
import wolve.dms.libraries.BitmapView;
import wolve.dms.models.BaseModel;
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


//        Rect bounds = new Rect();
//        paint.getTextBounds(text, 0, text.length(), bounds);
//        int height = bounds.height();
//        startY = startY + ()

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

    public static int  drawBillTable(Canvas canvas,
                                     int strokewidth,
                                     BaseModel bill,
                                     List<BaseModel> billdetails,
                                     List<BaseModel> debts,
                                     Double paid,
                                     String[] columtitles,
                                     int[] columlength,
                                     int startX,
                                     int endX,
                                     int startY,
                                     int rowheight,
                                     int rowdebtheight){
        Paint paint = new Paint();
        paint.setStrokeWidth(strokewidth);

        drawHorizontalLine(canvas,
                R.color.black_text_color_hint,
                1,
                startX,
                endX,
                startY);
        for (int x =0; x<columtitles.length; x++){
            drawText(canvas,
                    columtitles[x],
                    R.color.black_text_color,
                    10,
                    false,
                    false,
                    1,
                    columlength[x],
                    columlength[x+1],
                    startY + 15 - 5,
                    columlength[x]);
            drawVerticalLine(canvas,
                    R.color.black_text_color_hint,
                    1,
                    columlength[x],
                    startY,
                    15
            );

        }
        int lineX = drawHorizontalLine(canvas,
                R.color.black_text_color_hint,
                1,
                startX,
                endX,
                startY + 15);


        for (int i = 0; i < billdetails.size(); i++){
            BaseModel detail = billdetails.get(i);
            drawHorizontalLine(canvas,
                    R.color.black_text_color_hint,
                    strokewidth,
                    startX,
                    endX,
                    lineX + (i+1) * rowheight);

            String[] text = new String[]{
                    String.valueOf(i+1),
                    detail.getString("productName"),
                    "--",
                    detail.getString("quantity"),
                    Util.FormatMoney(detail.getDouble("unitPrice") - detail.getDouble("discount") ),
                    Util.FormatMoney((detail.getDouble("unitPrice") - detail.getDouble("discount"))*detail.getInt("quantity"))
                    };
            int[] pos= new int[]{1, 0, 1, 2, 2, 2};

            for (int ii =0; ii<text.length; ii++){
                drawText(canvas,
                        text[ii],
                        R.color.black_text_color,
                        10,
                        false,
                        false,
                        pos[ii],
                        columlength[ii],
                        columlength[ii+1],
                        lineX + (i+1) * rowheight - 6,
                        5);

                drawVerticalLine(canvas,
                        R.color.black_text_color_hint,
                        1,
                        columlength[ii],
                        lineX + i * rowheight -1 ,
                        rowheight + 1);
            }

        }
        int lineXX = startY + 15 + rowheight*billdetails.size();
        if (debts.size()>0){
            lineXX = drawDebtTable(canvas,
                    strokewidth,
                    bill.getDouble("total"),
                    debts,
                    columlength,
                    startX,
                    endX,
                    startY + 15 + rowheight*billdetails.size(),
                    rowheight,
                    rowdebtheight);
        }

        String[] sumtitle = new String[]{"Tổng" , " Thanh toán", "Còn lại"};
        double total = bill.getDouble("total") + DataUtil.sumValueFromList(debts, "debt");
        String[] sumvalue = new String[]{Util.FormatMoney(total),
                                            Util.FormatMoney(paid),
                                            Util.FormatMoney(total - paid)};

        for (int a = 0; a < sumtitle.length; a++){
            drawHorizontalLine(canvas,
                    R.color.black_text_color_hint,
                    strokewidth,
                    startX,
                    endX,
                     lineXX + (a+1) * rowheight);

            for (int b=0; b<2; b++){
                if (b ==0){
                    drawText(canvas,
                            sumtitle[a],
                            R.color.black_text_color,
                            10,
                            true,
                            false,
                            2,
                            columlength[0],
                            columlength[5],
                            lineXX + (a+1) * rowheight - 6,
                            10);

                    drawVerticalLine(canvas,
                            R.color.black_text_color_hint,
                            1,
                            columlength[0],
                            lineXX + a * rowheight ,
                            rowheight);

                } else if (b == 1) {
                    drawText(canvas,
                            sumvalue[a],
                            R.color.black_text_color,
                            12,
                            true,
                            false,
                            2,
                            columlength[5],
                            columlength[6],
                            lineXX + (a+1) * rowheight - 5,
                            5);

                    drawVerticalLine(canvas,
                            R.color.black_text_color_hint,
                            1,
                            columlength[5],
                            lineXX + a * rowheight ,
                            rowheight);
                }


            }

        }
        int currentbillheight = debts.size()>0?rowheight* (billdetails.size() +4) : rowheight* (billdetails.size() +3);
        int YEnd = startY + 15 + currentbillheight+ rowdebtheight * debts.size();

        drawVerticalLine(canvas,
                R.color.black_text_color_hint,
                1,
                endX,
                startY,
                YEnd - startY );

        return YEnd;
    }

    public static int  drawDebtTable(Canvas canvas,
                                     int strokewidth,
                                     double billtotal,
                                     List<BaseModel> debts,
                                     int[] columlength,
                                     int startX,
                                     int endX,
                                     int startYY,
                                     int rowheight,
                                     int rowdebtheight){
        DataUtil.sortbyStringKey("createAt", debts, true);
        int startY = startYY + rowheight;
        drawHorizontalLine(canvas,
                R.color.black_text_color_hint,
                strokewidth,
                startX,
                endX,
                startY);
        drawText(canvas,
                "Hoá đơn hiện tại",
                R.color.black_text_color,
                10,
                true,
                false,
                2,
                columlength[0],
                columlength[5],
                startYY + rowheight - 6,
                5);
        drawVerticalLine(canvas,
                R.color.black_text_color_hint,
                1,
                columlength[0],
                startYY,
                rowdebtheight);
        drawText(canvas,
                Util.FormatMoney(billtotal),
                R.color.black_text_color,
                12,
                true,
                false,
                2,
                columlength[5],
                columlength[6],
                startYY + rowheight - 5,
                5);

        drawVerticalLine(canvas,
                R.color.black_text_color_hint,
                1,
                columlength[5],
                startYY  ,
                rowdebtheight);

        for (int i = 0; i < debts.size(); i++){
            drawHorizontalLine(canvas,
                    R.color.black_text_color_hint,
                    strokewidth,
                    startX,
                    endX,
                    startY + (i+1) * rowdebtheight);


            for (int b=0; b<2; b++){
                if (b ==0){
                    drawText(canvas,
                            String.format("Hoá đơn %s", Util.DateHourString(debts.get(i).getLong("createAt"))),
                            R.color.black_text_color_hint,
                            8,
                            false,
                            false,
                            0,
                            columlength[0],
                            columlength[5],
                            startY + (i+1) * rowdebtheight - 15,
                            5);

                    drawText(canvas,
                            String.format("Tổng: %s", Util.FormatMoney(debts.get(i).getDouble("total"))),
                            R.color.black_text_color,
                            10,
                            false,
                            false,
                            0,
                            columlength[0],
                            columlength[5],
                            startY + (i+1) * rowdebtheight - 4,
                            5);

                    drawText(canvas,
                            String.format("Đã thanh toán: %s", Util.FormatMoney(debts.get(i).getDouble("paid"))),
                            R.color.black_text_color,
                            10,
                            false,
                            false,
                            0,
                            columlength[0] + 150,
                            columlength[5],
                            startY + (i+1) * rowdebtheight - 4,
                            5);

                    drawVerticalLine(canvas,
                            R.color.black_text_color_hint,
                            1,
                            columlength[0],
                            startY + i * rowdebtheight ,
                            rowdebtheight);

                } else if (b == 1) {
                    drawText(canvas,
                            Util.FormatMoney(debts.get(i).getDouble("debt")),
                            R.color.black_text_color,
                            10,
                            true,
                            false,
                            2,
                            columlength[5],
                            columlength[6],
                            startY + (i+1) * rowdebtheight - 8,
                            5);

                    drawVerticalLine(canvas,
                            R.color.black_text_color_hint,
                            1,
                            columlength[5],
                            startY + i * rowdebtheight ,
                            rowdebtheight);
                }


            }





        }

        return startY + debts.size() * rowdebtheight;
    }

    public static int drawHeader(Canvas canvas,
                                 Bitmap bitmapLogo,
                                 BaseModel distributor,
                                 int pageheight){
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
    public static int drawSignature(Canvas canvas, int startY, BaseModel distributor){
        drawText(canvas,
                distributor.getString("thanks"),
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

        drawText(canvas,
                "Nhân viên bán hàng",
                R.color.black_text_color,
                10,
                true,
                false,
                1,
                0,
                ISOA5Width /2,
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
                ISOA5Width /2,
                startY + 55,
                0);
        drawText(canvas,
                "Khách hàng",
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

        return 0;
    }

//    public static int  drawPayableTable(Canvas canvas,
//                                        int strokewidth,
//                                        List<BaseModel> debts,
//                                        String[] columtitles,
//                                        int[] columlength,
//                                        int startX,
//                                        int endX,
//                                        int startY,
//                                        int rowheight){
//        Paint paint = new Paint();
//        paint.setStrokeWidth(strokewidth);
//
//        int currentEndY = startY;
//        int[] columsumlength = new int[]{columlength[0], columlength[5], columlength[6], columlength[7]};
//        double sum = 0.0;
//
//
//        drawHorizontalLine(canvas,
//                R.color.black_text_color_hint,
//                1,
//                startX,
//                endX,
//                currentEndY);
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
//                    currentEndY + 15 - 5,
//                    columlength[x]);
//            drawVerticalLine(canvas,
//                    R.color.black_text_color_hint,
//                    1,
//                    columlength[x],
//                    currentEndY,
//                    15
//            );
//
//        }
//        currentEndY = drawHorizontalLine(canvas,
//                R.color.black_text_color_hint,
//                1,
//                startX,
//                endX,
//                currentEndY + 15);
//// END TITLE
//
//        for (int i = 0; i < debts.size(); i++){
//            //BaseModel debt = debts.get(i);
//            List<BaseModel> details = debts.get(i).getList("billDetails");
//            List<BaseModel> payments = debts.get(i).getList("payments");
//// draw detail line
//            for (int y =0; y<details.size(); y++){
//                currentEndY = drawHorizontalLine(canvas,
//                        R.color.black_text_color_hint,
//                        strokewidth,
//                        startX,
//                        endX,
//                        currentEndY + rowheight);
//
//
//                sum += (details.get(y).getDouble("unitPrice") - details.get(y).getDouble("discount"))*details.get(y).getInt("quantity");
//                String[] text = new String[]{
//                        Util.DateMonthYearString(details.get(y).getLong("createAt")),
//                        details.get(y).getString("productName"),
//                        "--",
//                        details.get(y).getString("quantity"),
//                        Util.FormatMoney(details.get(y).getDouble("unitPrice") -  details.get(y).getDouble("discount")),
//                        Util.FormatMoney((details.get(y).getDouble("unitPrice") - details.get(y).getDouble("discount"))*details.get(y).getInt("quantity")),
//                        Util.FormatMoney(sum)};
//                int[] pos= new int[]{1, 0, 1, 2, 2, 2, 2};
//
//
//                for (int yy =0; yy<text.length; yy++){
//                    drawText(canvas,
//                            text[yy],
//                            R.color.black_text_color,
//                            10,
//                            false,
//                            false,
//                            pos[yy],
//                            columlength[yy],
//                            columlength[yy+1],
//                            currentEndY - 6,
//                            5);
//
//                    drawVerticalLine(canvas,
//                            R.color.black_text_color_hint,
//                            1,
//                            columlength[yy],
//                            currentEndY - rowheight,
//                            rowheight );
//                }
//
//            }
////draw sum
//            currentEndY = drawHorizontalLine(canvas,
//                    R.color.black_text_color_hint,
//                    strokewidth,
//                    startX,
//                    endX,
//                    currentEndY + rowheight);
//            String[] textsum = new String[]{
//                    String.format("Tổng hoá đơn ngày %s", Util.DateMonthYearString(debts.get(i).getLong("createAt"))),
//                    Util.FormatMoney(debts.get(i).getDouble("total")),
//                    Util.FormatMoney(sum)};
//            int[] possum = new int[]{0, 2, 2};
//
//
//            for (int s =0; s<textsum.length; s++) {
//                drawText(canvas,
//                        textsum[s],
//                        R.color.colorRed,
//                        10,
//                        true,
//                        false,
//                        possum[s],
//                        columsumlength[s],
//                        columsumlength[s+1],
//                        currentEndY - 6,
//                        5);
//
//                drawVerticalLine(canvas,
//                        R.color.black_text_color_hint,
//                        1,
//                        columsumlength[s],
//                        currentEndY  - rowheight ,
//                        rowheight);
//            }
//
//            for (int pa =0; pa < payments.size(); pa++){
//                currentEndY = drawHorizontalLine(canvas,
//                        R.color.black_text_color_hint,
//                        strokewidth,
//                        startX,
//                        endX,
//                        currentEndY + rowheight);
//                sum -=payments.get(pa).getDouble("paid");
//                String[] textpa = new String[]{
//                        String.format("%s Thanh toán", Util.DateString(payments.get(pa).getLong("createAt"))),
//                        Util.FormatMoney(payments.get(pa).getDouble("paid")),
//                        Util.FormatMoney(sum)};
//                for (int pp =0; pp<textsum.length; pp++){
//                    drawText(canvas,
//                            textpa[pp],
//                            R.color.colorBlue,
//                            10,
//                            true,
//                            false,
//                            possum[pp],
//                            columsumlength[pp],
//                            columsumlength[pp+1],
//                            currentEndY - 6,
//                            5);
//
//                    drawVerticalLine(canvas,
//                            R.color.black_text_color_hint,
//                            1,
//                            columsumlength[pp],
//                            currentEndY  - rowheight ,
//                            rowheight);
//
//                }
//
//
//            }
//
//        }
//
//        drawVerticalLine(canvas,
//                R.color.black_text_color_hint,
//                1,
//                endX,
//                startY,
//                currentEndY - startY );
//
//        return currentEndY;
//    }

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

}
