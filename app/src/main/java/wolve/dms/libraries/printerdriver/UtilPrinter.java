package wolve.dms.libraries.printerdriver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import wolve.dms.utils.StringUtil;
import wolve.dms.utils.Util;

/**
 * Created by imrankst1221@gmail.com
 *
 */

public class UtilPrinter {
    // UNICODE 0x23 = #
    public static final byte[] UNICODE_TEXT = new byte[] {0x23, 0x23, 0x23,
            0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,
            0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,0x23, 0x23, 0x23,
            0x23, 0x23, 0x23};

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };

    public static byte[] decodeBitmap(Bitmap bmp){
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;


        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString+widthHexString+heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    public static String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static void printCustomText(OutputStream outputStream, String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x05};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x00};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x33}; // 3- bold with large text
        byte[] bb4 = new byte[]{0x1B,0x21,0x23};
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
                case 4:
                    outputStream.write(bb4);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(StringUtil.unAccent(msg).getBytes());
//            outputStream.write(msg.getBytes("Windows-1258"));
            outputStream.write(PrinterCommands.FEED_LINE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printCustom2Text(OutputStream outputStream, String msg1,String msg2, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x05};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x00};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x30}; // 3- bold with large text
        byte[] bb4 = new byte[]{0x1B,0x21,0x23};

        int width =0;
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    width = 30;
                    break;
                case 1:
                    outputStream.write(bb);
                    width = 30;
                    break;
                case 2:
                    outputStream.write(bb2);
                    width = 30;
                    break;
                case 3:
                    outputStream.write(bb3);
                    width = 30;
                    break;
                case 4:
                    outputStream.write(bb4);
                    width = 19;
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }

            String space = " ";
            int l = msg1.length() + msg2.length();

            if(l < width){
                for(int x = width-l; x >= 0; x--) {
                    space = space+" ";
                }
            }
            outputStream.write((StringUtil.unAccent(msg1) + space + StringUtil.unAccent(msg2)).getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void printPhoto(OutputStream outputStream,int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(Util.getInstance().getCurrentActivity().getResources(), img);
            if(bmp!=null){
                byte[] command = UtilPrinter.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                outputStream.write(command);


            }else{
                Util.showToast("The file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.showToast("The file isn't exists");
        }
    }

    public static void printPhoto(OutputStream outputStream,String path) {
        try {
            //Bitmap bmp = BitmapFactory.decodeResource(getResources(), img);
            Bitmap bmp = BitmapFactory.decodeFile(path);
            if(bmp!=null){
                byte[] command = UtilPrinter.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                outputStream.write(command);


            }else{
                Log.e("file", "The file isn't exists");
                //Util.showToast("The file isn't exists");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("file", "The file isn't exists");
            //Util.showToast("The file isn't exists");
        }
    }

    //outputStream.write(0x1C); outputStream.write(0x2E); // Cancels Chinese  character mode (FS .)
    //outputStream.write(0x1B); outputStream.write(0x74); outputStream.write(0x10); // Select character code table (ESC t n) - n = 16(0x10) for WPC1252

    public static byte[] convertExtendedAscii(String input)
    {
        int length = input.length();
        byte[] retVal = new byte[length];

        for(int i=0; i<length; i++)
        {
            char c = input.charAt(i);

            if (c < 127)
            {
                retVal[i] = (byte)c;
            }
            else
            {
                retVal[i] = (byte)(c - 256);
            }
        }

        return retVal;
    }
}
