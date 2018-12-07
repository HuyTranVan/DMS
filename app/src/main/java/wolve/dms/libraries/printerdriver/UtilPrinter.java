package wolve.dms.libraries.printerdriver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import wolve.dms.utils.Constants;
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

    public static final int SMALLTEXT = 0;
    public static final int MEDIUMTEXT = 1;
    public static final int BIGTEXT = 2;
    public static final int LARGETEXT = 3;
    public static final int BOLTSMALLTEXT = 4;
    public static final int BOLTMEDIUMTEXT = 5;
    public static final int BOLTBIGTEXT = 6;
//    public static final int BOLTLARGETEXT = 7;

    public static final byte[] alignLeft = PrinterCommands.ESC_ALIGN_LEFT;
    public static final byte[] alignCenter = PrinterCommands.ESC_ALIGN_CENTER;
    public static final byte[] alignRight = PrinterCommands.ESC_ALIGN_RIGHT;



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
//        byte[] bb3 = new byte[]{0x1a,0x21,0x32}; // 3- bold with large text
        byte[] bb3 = new byte[]{29,33,33};
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
            outputStream.write(Util.unAccent(msg).getBytes());
//            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printBytes(OutputStream outputStream, String msg, int size, int align) {
//        //Print config "mode"
//        byte[] cc = new byte[]{0x1B,0x21,0x05};  // 0- normal size text
//        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
//        byte[] bb = new byte[]{0x1B,0x21,0x00};  // 1- only bold text
//        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
////        byte[] bb3 = new byte[]{0x1a,0x21,0x32}; // 3- bold with large text
//        byte[] bb3 = new byte[]{29,33,33};
//        byte[] bb4 = new byte[]{0x1B,0x21,0x23};
        try {
//            switch (size){
//                case 0:
//                    outputStream.write(cc);
//                    break;
//                case 1:
//                    outputStream.write(bb);
//                    break;
//                case 2:
//                    outputStream.write(bb2);
//                    break;
//                case 3:
//                    outputStream.write(bb3);
//                    break;
//                case 4:
//                    outputStream.write(bb4);
//                    break;
//            }

            outputStream.write(getAlign(align));
            outputStream.write(toQrCode(msg));
//            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printCustom2Text(OutputStream outputStream,String printerSize, String msg1,String msg2, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x10};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x21};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x30}; // 3- bold with large text
        byte[] bb4 = new byte[]{0x1B,0x21,0x23};


        int width =0;
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    width = printerSize.equals(Constants.PRINTER_57)?30:42;
                    break;
                case 1:
                    outputStream.write(bb);
                    width = printerSize.equals(Constants.PRINTER_57)?30:42;
                    break;
                case 2:
                    outputStream.write(bb2);
                    width = printerSize.equals(Constants.PRINTER_57)?30:42;
                    break;
                case 3:
                    outputStream.write(bb3);
                    width = printerSize.equals(Constants.PRINTER_57)?30:42;
                    break;
                case 4:
                    outputStream.write(bb4);
                    width = printerSize.equals(Constants.PRINTER_57)?19:24;
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
            outputStream.write((Util.unAccent(msg1) + space + Util.unAccent(msg2)).getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printCustomTextNew(OutputStream outputStream, String msg, int size, int align) {
        try {
            outputStream.write(getTextSize(size));
            outputStream.write(getAlign(align));

            outputStream.write(Util.unAccent(msg).getBytes());
//            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printCustom2TextNew(OutputStream outputStream,String printerSize, String msg1,String msg2, int size, int align) {
        try {
            outputStream.write(getTextSize(size));
            outputStream.write(getAlign(align));


            String space = " ";
            int l = msg1.length() + msg2.length();

            int width = printerSize.equals(Constants.PRINTER_57)?30:30;
            if(l < width){
                for(int x = width-l; x >= 0; x--) {
                    space = space+" ";
                }
            }
            outputStream.write((Util.unAccent(msg1) + space + Util.unAccent(msg2)).getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);

        } catch (IOException e) {
            //e.printStackTrace();
        }

    }


    public static void printDrawablePhoto(OutputStream outputStream,Drawable img) {
        try {
//            Bitmap bmp = BitmapFactory.decodeResource(Util.getInstance().getCurrentActivity().getResources(), img);
            Bitmap bmp = drawableToBitmap(img);
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
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER123);
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

    public static byte[] convertExtendedAscii(String input) {
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

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    private static byte[] getTextSize(int size){
        byte[] byteReturn = null;
        switch (size){
            case 1:
                byteReturn = new byte[]{0x1B,0x21,0x05};
                break;

            case 2:
                byteReturn = new byte[]{0x1B,0x21,0x06};
                break;

            case 3:
                byteReturn = new byte[]{0x1B,0x21,0x15};
                break;

            case 4:
                byteReturn = new byte[]{0x1B,0x21,0x16};
                break;

            case 11:
                byteReturn = new byte[]{0x1B,0x21,0x69};
                break;

            case 22:
                byteReturn = new byte[]{0x1B,0x21,0x71};
                break;

            case 33:
                byteReturn = new byte[]{0x1B,0x21,0x72};;

                break;

        }

        return byteReturn;
    }

    private static byte[] getAlign(int align){
        byte[] alignReturn = null;
        switch (align){
            case 0:
                alignReturn = PrinterCommands.ESC_ALIGN_LEFT;
                break;
            case 1:
                alignReturn = PrinterCommands.ESC_ALIGN_CENTER;
                break;

            case 2:
                alignReturn = PrinterCommands.ESC_ALIGN_RIGHT;
                break;
        }
        return alignReturn;
    }

    //*******************************//

//    public static void main(String args[]) {
//        String content = "Test Content";
//        String printerIp = "192.168.1.123";
//        int printerPort = 9100;
//
//        try (
//                Socket socket = new Socket(printerIp, printerPort);
//                DataInputStream in = new DataInputStream(new ByteArrayInputStream(toQrCode(content)));
//                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
//        ) {
//            while (in.available() != 0) {
//                out.write(in.readByte());
//            }
//            out.writeByte(0x00);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    public static byte[] toQrCode(String content) {
        return QrCodeBuilder.create()
                .withModel((byte) 0x32, (byte) 0x00)
                .withSize((byte) 0x06)
                .withErrorCorrection((byte) 0x33)
                .withContent(content, (byte) 0x30)
                .toBytes();
    }

    enum QrCommand {
        MODEL, SIZE, ERROR_CORRECTION, STORE, CONTENT, PRINT
    }

    private static final class QrCodeBuilder {

        private final Map commands = new EnumMap<>(QrCommand.class);

        private QrCodeBuilder() {
            // empty
        }

        /**
         * Creates a new builder.
         *
         * @return a builder
         */
        public static QrCodeBuilder create() {
            return new QrCodeBuilder();
        }

        /**
         * Generates a {@code byte[]} array appended with the two {@code byte} arguments.
         *
         * @param penultimate the penultimate byte
         * @param last        the last byte
         * @return a {@code byte[]} array starting with (@code 0x1d 0x28 0x6b 0x03 0x00 0x31)
         */
        private static byte[] append(byte penultimate, byte last) {
            return new byte[]{0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, penultimate, last};
        }

        /**
         * Sets the model with the following bytes: {@code 0x1d 0x28 0x6b 0x04 0x00 0x31 0x41 n1(x32) n2(x00)}.
         *
         * @param model [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
         * @param size  (x00 size of model)
         * @return this builder
         */
        public QrCodeBuilder withModel(byte model, byte size) {
            commands.put(QrCommand.MODEL, new byte[]{0x1d, 0x28, 0x6b, 0x04, 0x00, 0x31, 0x41, model, size});

            return this;
        }

        /**
         * Sets the size with the following bytes: {@code 0x1d 0x28 0x6b 0x03 0x00 0x31 0x43 n}.
         *
         * @param size depends on the printer
         * @return this builder
         */
        public QrCodeBuilder withSize(byte size) {
            commands.put(QrCommand.SIZE, append((byte) 0x43, size));

            return this;
        }

        /**
         * Sets the error correction with the following bytes: {@code 0x1d 0x28 0x6b 0x03 0x00 0x31 0x43 n}.
         *
         * @param errorCorrection [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
         * @return this builder
         */
        public QrCodeBuilder withErrorCorrection(byte errorCorrection) {
            commands.put(QrCommand.ERROR_CORRECTION, append((byte) 0x45, errorCorrection));

            return this;
        }

        /**
         * Sets the content.
         *
         * @param content the content to set
         * @param print   the print byte
         * @return this builder
         */
        public QrCodeBuilder withContent(String content, byte print) {
            // QR Code: Store the data in the symbol storage area
            // Hex      1D    28    6B    pL    pH    31            50            30            d1...dk
            // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
            //          1D    28    6B    pL    pH    cn(49->x31)   fn(80->x50)   m(48->x30)    d1...dk
            int contentLength = content.length() + 3;
            byte length = (byte) (contentLength % 256);
            byte height = (byte) (contentLength / 256);

            commands.put(QrCommand.STORE, new byte[]{0x1d, 0x28, 0x6b, length, height, 0x31, 0x50, print});
            commands.put(QrCommand.CONTENT, content.getBytes());
            commands.put(QrCommand.PRINT, append((byte) 0x51, print));

            return this;
        }

        public byte[] toBytes() {
            if (commands.size() != QrCommand.values().length) {
                throw new IllegalStateException("Not all commands given.");
            }

            byte[] result = new byte[0];
            for (Object current : commands.values()) {
                result = Arrays.copyOf(result, result.length + current.toString().length());
                System.arraycopy(current, 0, result, result.length, current.toString().length());
            }

            return result;
        }
    }

}
