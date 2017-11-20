package wolve.dms.libraries.printerdriver;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by TOAN on 6/27/2016.
 */
public class LinePrinter {
    private Canvas canvas = null;
    private Paint paint = null;
    private Bitmap bm = null;
    private int width;
    private float length = 0.0F;
    private byte[] bitbuf = null;

    public LinePrinter() {
    }

    public int getLength() {
        return (int)this.length;
    }

    public int getWidth() {
        return this.width;
    }

    public void initCanvas(int w) {
        int h = 10 * w;
        this.bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(this.bm);
        this.canvas.drawColor(-1);
        this.width = w;
        this.bitbuf = new byte[this.width / 8];
    }

    public void initPaint() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        //this.paint.setColor(Color.BLACK);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }


    public void drawImage(float x, float y, Bitmap bitmap) {
        try {
            Bitmap e = bitmap;
            this.canvas.drawBitmap(e, x, y, (Paint)null);
            if(this.length < y + (float)e.getHeight()) {
                this.length = y + (float)e.getHeight();
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }


    public byte[] printDraw() {
        if(this.getLength() == 0) {
            return null;
        } else {
            Bitmap nbm = Bitmap.createBitmap(this.bm, 0, 0, this.width, this.getLength());
            byte[] imgbuf = new byte[this.width / 8 * this.getLength()];
            int s = 0;

            for(int i = 0; i < this.getLength(); ++i) {
                int t;
                for(t = 0; t < this.width / 8; ++t) {
                    int c0 = nbm.getPixel(t * 8 + 0, i);
                    byte p0;
                    if(c0 == -1) {
                        p0 = 0;
                    } else {
                        p0 = 1;
                    }

                    int c1 = nbm.getPixel(t * 8 + 1, i);
                    byte p1;
                    if(c1 == -1) {
                        p1 = 0;
                    } else {
                        p1 = 1;
                    }

                    int c2 = nbm.getPixel(t * 8 + 2, i);
                    byte p2;
                    if(c2 == -1) {
                        p2 = 0;
                    } else {
                        p2 = 1;
                    }

                    int c3 = nbm.getPixel(t * 8 + 3, i);
                    byte p3;
                    if(c3 == -1) {
                        p3 = 0;
                    } else {
                        p3 = 1;
                    }

                    int c4 = nbm.getPixel(t * 8 + 4, i);
                    byte p4;
                    if(c4 == -1) {
                        p4 = 0;
                    } else {
                        p4 = 1;
                    }

                    int c5 = nbm.getPixel(t * 8 + 5, i);
                    byte p5;
                    if(c5 == -1) {
                        p5 = 0;
                    } else {
                        p5 = 1;
                    }

                    int c6 = nbm.getPixel(t * 8 + 6, i);
                    byte p6;
                    if(c6 == -1) {
                        p6 = 0;
                    } else {
                        p6 = 1;
                    }

                    int c7 = nbm.getPixel(t * 8 + 7, i);
                    byte p7;
                    if(c7 == -1) {
                        p7 = 0;
                    } else {
                        p7 = 1;
                    }

                    int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 + p5 * 4 + p6 * 2 + p7;
                    this.bitbuf[t] = (byte)value;
                }

                for(t = 0; t < this.width / 8; ++t) {
                    imgbuf[s] = this.bitbuf[t];
                    ++s;
                }
            }

            return imgbuf;
        }
    }
}
