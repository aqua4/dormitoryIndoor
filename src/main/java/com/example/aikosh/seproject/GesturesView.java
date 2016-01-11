package com.example.aikosh.seproject;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

public class GesturesView extends View
        implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener,
        ScaleGestureDetector.OnScaleGestureListener
{
    static double[][] w;
    String[] imgs;
    public double actualx;
    public double actualy;
    private GestureDetector mDetector;
    private ScaleGestureDetector scaleDetector;
    private Paint p;
    public double circlex = 0;
    public double circley = 0;
    public double x;
    public double y;
    private double bex;
    private double bey;
    public int putPin=0;
    public int putPin2=0;
    public int putPinf2=0;
    public int putPin2f2=0;
    private double scalefactorX=1.0;
    private double scalefactorY=1.0;
    public double pinX1=0.0;
    public double pinY1=0.0;
    public double pin2X=0.0;
    private double pin2Y=0.0;
    public boolean puted=false;
    public boolean putedf2=false;
    public String[] last2pins= {"",""};
    public int maxN;
    public int[] imgs_x;
    public int[] imgs_y;
    public String[] imgsID;
    public Bitmap bitmaphall;
    public Bitmap bitmapall;
    public Bitmap bitmaphall2;
    public Bitmap bitmapall2;
    private int screenWidth;
    private int screenHeight;
    private int[] lineX;
    private int[] lineY;
    public String[] lt;
    private int length;
    public int[] num;
    public double pinX=0.0;
    public double pinY=0.0;
    public boolean pinputed=false;
    public boolean pinputedf2=false;
    public String lastputed="";
    public int[]path;
    public int lastpinid;
    public int Searchpincounter=0;
    public double spinX;
    public double spinY;


    public GesturesView(Context context, AttributeSet attrs) {

        super(context, attrs);

        mDetector = new GestureDetector( context, this );

        scaleDetector = new ScaleGestureDetector( context, this );

        mDetector.setOnDoubleTapListener(this);

        p = new Paint();
        p.setColor(Color.RED);
        p.setStrokeWidth(7);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        maxN=MainActivity.dbHelper.getSize()+1;
        dijkstraheap.n=maxN-1;
        dijkstraheap.a=new int[maxN][];
        imgs = new String[maxN];
        lineX = new int[maxN];
        imgs_x = new int[maxN];
        imgs_y = new int[maxN];
        lt=new String[maxN];
        imgsID = new String[maxN];
        lineY = new int[maxN];
        int ma=0,id=0;
        for (int count=0;count<maxN-1;count++) {
            Cursor cursor=MainActivity.dbHelper.getData(count+1);
            imgs[count] = cursor.getString(cursor.getColumnIndex("Type"));
            imgs_x[count] = cursor.getInt(cursor.getColumnIndex("x_cordinate"));
            imgs_y[count] = cursor.getInt(cursor.getColumnIndex("y_cordinate"));
            imgsID[count] = cursor.getString(cursor.getColumnIndex("ID"));
            if (!imgs[count].equals("navigation")) lt[count]=cursor.getString(cursor.getColumnIndex("LongText"));
            if (id<Integer.parseInt(imgsID[count])) id=Integer.parseInt(imgsID[count]);
            String stringFromDB=cursor.getString(cursor.getColumnIndex("Relations"));
            String[] s = stringFromDB.split(",");
            int[] array = new int[s.length+2];
            array[0]=Integer.parseInt(imgsID[count]);
            array[1]=s.length-1;
            for (int curr = 0; curr < s.length; curr++){
                if (s[curr]==null || s[curr].equals(" ")) continue;
                if (!Character.isDigit(s[curr].charAt(0))) s[curr]=s[curr].substring(1);
                if (!Character.isDigit(s[curr].charAt(s[curr].length()-1))) s[curr]=s[curr].substring(0,s[curr].length()-1);
                if (s[curr].equals("0"))continue;
                array[curr+2] = Integer.parseInt(s[curr]);
            }
            if (ma<s.length) ma=s.length;
            dijkstraheap.a[count+1] = array;
        }
        id++;
        num=new int[id];
        dijkstraheap.num=new int[id];
        dijkstraheap.p=new int[id];
        dijkstraheap.par=new int[id];
        w=new double[maxN][ma];
        maxN--;
        for(int i=0;i<maxN;i++) {
            num[Integer.parseInt(imgsID[i])]=i; }

        for(int i=1;i<=maxN;i++){
            double dx1=imgs_x[num[dijkstraheap.a[i][0]]]+.0;
            double dy1=imgs_y[num[dijkstraheap.a[i][0]]]+.0;
            for (int j = 2; j <= dijkstraheap.a[i][1] + 1; j++) {
                double dx2=imgs_x[num[dijkstraheap.a[i][j]]]+.0;
                double dy2=imgs_y[num[dijkstraheap.a[i][j]]]+.0;
                w[i][j - 2] = Math.sqrt((dx1-dx2) * (dx1-dx2) + (dy1-dy2) * (dy1-dy2));
                if (w[i][j-2]==0.0) w[i][j-2]=1.0;
            }
        }

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.fp1, o);
        actualx = bmp.getWidth();
        actualy = bmp.getHeight();
        x = actualx; y = actualy;

        int resIdbalcony = this.getResources().getIdentifier("balcony", "drawable", this.getContext().getPackageName());
        Bitmap balcony = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resIdbalcony), 18, 18, true);
        int resIdbathroom = this.getResources().getIdentifier("bathroom", "drawable", this.getContext().getPackageName());
        Bitmap bathroom = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resIdbathroom), 18, 18, true);
        int resIddoor = this.getResources().getIdentifier("door", "drawable", this.getContext().getPackageName());
        Bitmap door = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resIddoor), 18, 18, true);
        int resIdelevator = this.getResources().getIdentifier("elevator", "drawable", this.getContext().getPackageName());
        Bitmap elevator = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resIdelevator), 18, 18, true);
        int resIdhall = this.getResources().getIdentifier("hall", "drawable", this.getContext().getPackageName());
        Bitmap hall = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resIdhall), 18, 18, true);
        int resIdcorridor = this.getResources().getIdentifier("hallway", "drawable", this.getContext().getPackageName());
        Bitmap hallway = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resIdcorridor), 18, 18, true);
        int resIdstairs = this.getResources().getIdentifier("stairs", "drawable", this.getContext().getPackageName());
        Bitmap stairs = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resIdstairs), 18, 18, true);
        Map<String,Bitmap> bmps = new HashMap<>();
        bmps.put("balcony", balcony);
        bmps.put("bathroom", bathroom);
        bmps.put("door", door);
        bmps.put("elevator", elevator);
        bmps.put("hall", hall);
        bmps.put("hallway", hallway);
        bmps.put("stairs", stairs);

        bitmapall = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fp1), (int) x, (int) y, true);
        Canvas can = new Canvas(bitmapall);
        double sx=(actualx/x);
        double sy=(actualy/y);
        for(int i=0;i<maxN;i++){
            if(!imgs[i].equals("navigation") && Integer.parseInt(imgsID[i])<1500){
                double imgX1= (imgs_x[i]+.0)/sx+circlex;
                double imgY1 = (imgs_y[i]+.0)/sy+circley;
                can.drawBitmap(bmps.get(imgs[i]), (int) imgX1 - 9, (int) imgY1 - 9, null);
            }
        }

        bitmaphall = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fp1), (int) x, (int) y, true);
        can =new Canvas(bitmaphall);
        for(int i=0;i<maxN;i++) {
            if (imgs[i].equals("hall") && Integer.parseInt(imgsID[i]) < 1500) {
                double imgX1 = (imgs_x[i] + .0) / sx + circlex;
                double imgY1 = (imgs_y[i] + .0) / sy + circley;
                can.drawBitmap(bmps.get(imgs[i]), (int) imgX1 - 9, (int) imgY1 - 9, null);
            }
        }
        bitmapall2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fp2), (int) x, (int) y, true);
        can =new Canvas(bitmapall2);
        for(int i=0;i<maxN;i++){
            if(!imgs[i].equals("navigation") && Integer.parseInt(imgsID[i])>1500){
                double imgX1= (imgs_x[i]+.0)/sx+circlex;
                double imgY1 = (imgs_y[i]+.0)/sy+circley;
                can.drawBitmap(bmps.get(imgs[i]), (int) imgX1 - 9, (int) imgY1 - 9, null);
            }
        }
        bitmaphall2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fp2), (int) x, (int) y, true);
        can =new Canvas(bitmaphall2);
        for(int i=0;i<maxN;i++) {
            if (imgs[i].equals("hall") && Integer.parseInt(imgsID[i])>1500) {
                double imgX1 = (imgs_x[i] + .0) / sx + circlex;
                double imgY1 = (imgs_y[i] + .0) / sy + circley;
                can.drawBitmap(bmps.get(imgs[i]), (int) imgX1 - 9, (int) imgY1 - 9, null);
            }
        }

            y = y*((screenWidth+.0)/x);
            x = screenWidth+.0;
            circley=(screenHeight-y)/3;
        MainActivity.bitmapallforpins = Bitmap.createBitmap(bitmapall);
        MainActivity.bitmaphallforpins = Bitmap.createBitmap(bitmaphall);
        MainActivity.bitmapallforpins2 = Bitmap.createBitmap(bitmapall2);
        MainActivity.bitmaphallforpins2 = Bitmap.createBitmap(bitmaphall2);
        //ShowPopUp pop=new ShowPopUp();
        //pop.show();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        MainActivity.diss();
        super.onDraw(canvas);
        scalefactorX=actualx/x;
        scalefactorY=actualy/y;

        double bsx;
        double bx;
        if(circlex<0) {bsx =-circlex;bx =0;}else{bsx =0;bx =circlex;}
        double bsy;
        double by;
        if(circley<0) {bsy =-circley;by =0;}else{bsy =0;by =circley;}
        if(x+circlex>screenWidth && circlex<0) {bex=screenWidth;}
        if(x+circlex>screenWidth && circlex>=0) {bex=screenWidth-circlex;}
        if(x+circlex<=screenWidth && circlex<0) {bex=x+circlex;}
        if(x+circlex<=screenWidth && circlex>=0) {bex=x;}
        if(y+circley>screenHeight && circley<0) {bey=screenHeight;}
        if(y+circley>screenHeight && circley>=0) {bey=screenHeight-circley;}
        if(y+circley<=screenHeight && circley<0) {bey=y+circley;}
        if(y+circley<=screenHeight && circley>=0) {bey=y;}

        if ((actualx/ x) < 0.59)
        {
            if(MainActivity.floorNumber==2){
                Bitmap croppedBmp = Bitmap.createBitmap(Bitmap.createScaledBitmap(MainActivity.bitmapallforpins, (int) x, (int) y, true), (int) bsx, (int) bsy, (int)bex, (int)bey);
                canvas.drawBitmap(croppedBmp, (int) bx, (int) by, p);}
            else{
                Bitmap croppedBmp = Bitmap.createBitmap(Bitmap.createScaledBitmap(MainActivity.bitmapallforpins2, (int) x, (int) y, true), (int) bsx, (int) bsy, (int)bex, (int)bey);
                canvas.drawBitmap(croppedBmp, (int) bx, (int) by, p);}
        }
        else
        {
            if(MainActivity.floorNumber==2) {
                Bitmap croppedBmp = Bitmap.createBitmap(Bitmap.createScaledBitmap(MainActivity.bitmaphallforpins, (int) x, (int) y, true), (int) bsx, (int) bsy, (int) bex, (int) bey);
                canvas.drawBitmap(croppedBmp, (int) bx, (int) by, p);}
            else {
                Bitmap croppedBmp = Bitmap.createBitmap(Bitmap.createScaledBitmap(MainActivity.bitmaphallforpins2, (int) x, (int) y, true), (int) bsx, (int) bsy, (int) bex, (int) bey);
                canvas.drawBitmap(croppedBmp, (int) bx, (int) by, p);}
        }

        if(MainActivity.floorNumber==2) {
            if ((putPin == 1 && putPin2 == 1) || (putPin == 1 && putPinf2 == 1)) {
                for (int i = 0; i < length - 1; i++) {
                    int x11 = (int) ((lineX[i] + .0) / scalefactorX + circlex);
                    int y11 = (int) ((lineY[i] + .0) / scalefactorY + circley);
                    int x21 = (int) ((lineX[i + 1] + .0) / scalefactorX + circlex);
                    int y21 = (int) ((lineY[i + 1] + .0) / scalefactorY + circley);
                    if ((x11 >= 0 && x11 <= screenWidth && y11 >= 0 && y11 <= screenHeight) || (x21 >= 0 && x21 <= screenWidth && y21 >= 0 && y21 <= screenHeight)) {
                        if (path[i + 1] < 1500) {
                            canvas.drawLine(x11, y11, x21, y21, p);
                        }
                    }
                }
            }
            if (!last2pins[0].equals("") && Integer.parseInt(last2pins[0]) < 1500) {
                if (pinX1 >= 0 && pinX1 <= screenWidth && pinY1 >= 0 && pinY1 <= screenHeight) {
                    Bitmap marker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pin), 30, 30, true);
                    canvas.drawBitmap(marker, (int) pinX1 - 15, (int) pinY1 - 30, null);
                }
            }
            if (!last2pins[1].equals("") && Integer.parseInt(last2pins[1]) < 1500) {
                if (pin2X >= 0 && pin2X <= screenWidth && pin2Y >= 0 && pin2Y <= screenHeight) {
                    Bitmap marker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pin), 30, 30, true);
                    canvas.drawBitmap(marker, (int) pin2X - 15, (int) pin2Y - 30, null);
                }
            }
        }
        else if (MainActivity.floorNumber==3){
            if ((putPinf2 == 1 && putPin2f2 == 1) || (putPin == 1 && putPinf2 == 1)) {
                for (int i = 0; i < length - 1; i++) {
                    int x11 = (int) ((lineX[i] + .0) / scalefactorX + circlex);
                    int y11 = (int) ((lineY[i] + .0) / scalefactorY + circley);
                    int x21 = (int) ((lineX[i + 1] + .0) / scalefactorX + circlex);
                    int y21 = (int) ((lineY[i + 1] + .0) / scalefactorY + circley);
                    if ((x11 >= 0 && x11 <= screenWidth && y11 >= 0 && y11 <= screenHeight) || (x21 >= 0 && x21 <= screenWidth && y21 >= 0 && y21 <= screenHeight)) {
                        if (path[i + 1] > 1500) {
                            canvas.drawLine(x11, y11, x21, y21, p);
                        }
                    }
                }
            }
            if (!last2pins[0].equals("") && Integer.parseInt(last2pins[0]) > 1500) {
                if (pinX1 >= 0 && pinX1 <= screenWidth && pinY1 >= 0 && pinY1 <= screenHeight) {
                    Bitmap marker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pin), 30, 30, true);
                    canvas.drawBitmap(marker, (int) pinX1 - 15, (int) pinY1 - 30, null);
                }
            }
            if (!last2pins[1].equals("") && Integer.parseInt(last2pins[1]) > 1500) {
                if ( pin2X >= 0 && pin2X <= screenWidth && pin2Y >= 0 && pin2Y <= screenHeight) {
                    Bitmap marker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pin), 30, 30, true);
                    canvas.drawBitmap(marker, (int) pin2X - 15, (int) pin2Y - 30, null);
                }
            }
        }
        if (Searchpincounter!=0) {
            Bitmap marker = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pin), 30, 30, true);
            canvas.drawBitmap(marker, (int) spinX - 15, (int) spinY - 30, null);
            Searchpincounter--;
            MainActivity.pop(MainActivity.toPop,(int)spinX,(int)spinY);
        }
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        MainActivity.diss();

        if(MainActivity.floorNumber==2){
            if(!puted){
                pinX =Math.round(event.getX());
                pinY =Math.round(event.getY());
                pinX = pinX -circlex;
                pinY = pinY -circley;
                scalefactorX=actualx/x;
                scalefactorY=actualy/y;
                pinX = pinX *scalefactorX;
                pinY = pinY *scalefactorY;
                pinputed = false;

                for(int i=0;i<maxN;i++)
                    if (imgs_x[i]+.0> pinX -22.0 && imgs_x[i]+.0< pinX +22.0 && imgs_y[i]+.0> pinY -22.0 && imgs_y[i]+.0< pinY +22.0)
                    {
                        putPin=1;
                        if(last2pins[0].equals("")){
                            last2pins[0]=imgsID[i];
                            pinX1 = pinX;
                            pinY1 = pinY;
                            pinX1=(imgs_x[i]+.0)/scalefactorX+circlex;
                            pinY1=(imgs_y[i]+.0)/scalefactorY+circley;
                        }else{
                            if(last2pins[1].equals("")){
                                last2pins[1]=imgsID[i];
                                pin2X = pinX;
                                pin2Y = pinY;
                                pin2X=(imgs_x[i]+.0)/scalefactorX + circlex;
                                pin2Y=(imgs_y[i]+.0)/scalefactorY + circley;
                            }else {
                                if(lastpinid==Integer.parseInt(last2pins[0])){
                                    last2pins[1]=imgsID[i];
                                    pin2X = pinX;
                                    pin2Y = pinY;
                                    pin2X=(imgs_x[i]+.0)/scalefactorX + circlex;
                                    pin2Y=(imgs_y[i]+.0)/scalefactorY + circley;}
                                else{
                                    last2pins[0]=imgsID[i];
                                    pinX1 = pinX;
                                    pinY1 = pinY;
                                    pinX1=(imgs_x[i]+.0)/scalefactorX+circlex;
                                    pinY1=(imgs_y[i]+.0)/scalefactorY+circley;}
                            }
                        }
                        lastpinid = Integer.parseInt(imgsID[i]);
                        lastputed=imgsID[i];
                        puted=true;
                        pinputed=true;
                        break;
                    }
            }
            else{
                pinX =Math.round(event.getX());
                pinY =Math.round(event.getY());
                pinX = pinX -circlex;
                pinY = pinY -circley;
                scalefactorX=actualx/x;
                scalefactorY=actualy/y;
                pinX = pinX *scalefactorX;
                pinY = pinY *scalefactorY;
                pinputed=false;

                for(int i=0;i<maxN;i++)
                    if (imgs_x[i]+.0> pinX -22.0 && imgs_x[i]+.0< pinX +22.0 && imgs_y[i]+.0> pinY -22.0 && imgs_y[i]+.0< pinY +22.0)
                    {
                        putPin2=1;
                        if(last2pins[0].equals("")){
                            last2pins[0]=imgsID[i];
                            pinX1 = pinX;
                            pinY1 = pinY;
                            pinX1=(imgs_x[i]+.0)/scalefactorX+circlex;
                            pinY1=(imgs_y[i]+.0)/scalefactorY+circley;
                        }else{
                            if(last2pins[1].equals("")){
                                last2pins[1]=imgsID[i];
                                pin2X = pinX;
                                pin2Y = pinY;
                                pin2X=(imgs_x[i]+.0)/scalefactorX + circlex;
                                pin2Y=(imgs_y[i]+.0)/scalefactorY + circley;
                            }else {
                                if(lastpinid==Integer.parseInt(last2pins[0])){
                                    last2pins[1]=imgsID[i];
                                    pin2X = pinX;
                                    pin2Y = pinY;
                                    pin2X=(imgs_x[i]+.0)/scalefactorX + circlex;
                                    pin2Y=(imgs_y[i]+.0)/scalefactorY + circley;
                            }
                            else{
                                last2pins[0]=imgsID[i];
                                pinX1 = pinX;
                                pinY1 = pinY;
                                pinX1=(imgs_x[i]+.0)/scalefactorX+circlex;
                                pinY1=(imgs_y[i]+.0)/scalefactorY+circley;}}
                        }
                        lastpinid = Integer.parseInt(imgsID[i]);
                        lastputed=imgsID[i];
                        puted=false;
                        pinputed=true;
                        break;
                    }
            }

            if((putPin==1 && putPin2==1) || (putPin==1 && putPinf2==1)) {
                dijkstraheap dijk = new dijkstraheap();
                path = new int[maxN+1];
                path = dijk.getPath(Integer.parseInt(last2pins[0]), Integer.parseInt(last2pins[1]));
                length = 1;
                while (path[length] != 0) {
                    length++;
                }
                length--;
                for (int temp = 0; temp < length; temp++) {
                    lineX[temp] = imgs_x[num[path[temp+1]]];
                    lineY[temp] = imgs_y[num[path[temp+1]]];
                }
            }
            invalidate();
        }
        else{
            if(!putedf2){
                pinX =Math.round(event.getX());
                pinY =Math.round(event.getY());
                pinX = pinX -circlex;
                pinY = pinY -circley;
                scalefactorX=actualx/x;
                scalefactorY=actualy/y;
                pinX = pinX *scalefactorX;
                pinY = pinY *scalefactorY;
                pinputedf2 = false;

                for(int i=0;i<maxN;i++)
                    if (imgs_x[i]+.0> pinX -22.0 && imgs_x[i]+.0< pinX +22.0 && imgs_y[i]+.0> pinY -22.0 && imgs_y[i]+.0< pinY +22.0)
                    {
                        putPinf2=1;
                        if(last2pins[0].equals("")){
                            if(Integer.parseInt(imgsID[i])<1500){last2pins[0]= String.valueOf(Integer.parseInt(imgsID[i])+1000);}
                            else{last2pins[0]=imgsID[i];}
                            pinX1 = pinX;
                            pinY1 = pinY;
                            pinX1=(imgs_x[i]+.0)/scalefactorX+circlex;
                            pinY1=(imgs_y[i]+.0)/scalefactorY+circley;
                        }else{
                            if(last2pins[1].equals("")){
                                if(Integer.parseInt(imgsID[i])<1500){last2pins[1]= String.valueOf(Integer.parseInt(imgsID[i])+1000);}
                                else{last2pins[1]=imgsID[i];}
                                pin2X = pinX;
                                pin2Y = pinY;
                                pin2X = (imgs_x[i]+.0)/scalefactorX+circlex;
                                pin2Y = (imgs_y[i]+.0)/scalefactorY+circley;
                            }else {
                                if(lastpinid==Integer.parseInt(last2pins[0])){
                                    if(Integer.parseInt(imgsID[i])<1500){last2pins[1]= String.valueOf(Integer.parseInt(imgsID[i])+1000);
                                    }
                                    else{
                                        last2pins[1]=imgsID[i];
                                    }
                                    pin2X = pinX;
                                    pin2Y = pinY;
                                    pin2X=(imgs_x[i]+.0)/scalefactorX+circlex;
                                    pin2Y=(imgs_y[i]+.0)/scalefactorY+circley;
                                }
                                else{
                                    if(Integer.parseInt(imgsID[i])<1500){last2pins[0]= String.valueOf(Integer.parseInt(imgsID[i])+1000);}
                                    else{last2pins[0]=imgsID[i];
                                    }
                                    pinX1 = pinX;
                                    pinY1 = pinY;
                                    pinX1=(imgs_x[i]+.0)/scalefactorX+circlex;
                                    pinY1=(imgs_y[i]+.0)/scalefactorY+circley;
                                }
                            }
                        }
                        if(Integer.parseInt(imgsID[i])<1500){lastpinid=Integer.parseInt(imgsID[i])+1000;}
                        else{lastpinid=Integer.parseInt(imgsID[i]);
                        }
                        if(Integer.parseInt(imgsID[i])<1500){lastputed=String.valueOf(Integer.parseInt(imgsID[i])+1000);}
                        else{lastputed=imgsID[i];
                        }
                        putedf2=true;
                        pinputedf2=true;
                        break;
                    }
            }
            else{
                pinX =Math.round(event.getX());
                pinY =Math.round(event.getY());
                pinX = pinX -circlex;
                pinY = pinY -circley;
                scalefactorX=actualx/x;
                scalefactorY=actualy/y;
                pinX = pinX *scalefactorX;
                pinY = pinY *scalefactorY;
                pinputedf2=false;

                for(int i=0;i<maxN;i++)
                    if (imgs_x[i]+.0> pinX -22.0 && imgs_x[i]+.0< pinX +22.0 && imgs_y[i]+.0> pinY -22.0 && imgs_y[i]+.0< pinY +22.0)
                    {
                        putPin2f2=1;
                        if(last2pins[0].equals("")){
                            if(Integer.parseInt(imgsID[i])<1500){last2pins[0]= String.valueOf(Integer.parseInt(imgsID[i])+1000);}
                            else{last2pins[0]=imgsID[i];}
                            pinX1 = pinX;
                            pinY1 = pinY;
                            pinX1=(imgs_x[i]+.0)/scalefactorX+circlex;
                            pinY1=(imgs_y[i]+.0)/scalefactorY+circley;
                        }else{
                            if(last2pins[1].equals("")){
                                if(Integer.parseInt(imgsID[i])<1500){last2pins[1]= String.valueOf(Integer.parseInt(imgsID[i])+1000);}
                                else{last2pins[1]=imgsID[i];}
                                pin2X = pinX;
                                pin2Y = pinY;
                                pin2X =(imgs_x[i]+.0)/scalefactorX+circlex;
                                pin2Y =(imgs_y[i]+.0)/scalefactorY+circley;
                            }else
                            {
                                if(lastpinid==Integer.parseInt(last2pins[0])){
                                    if(Integer.parseInt(imgsID[i])<1500){
                                        last2pins[1]= String.valueOf(Integer.parseInt(imgsID[i])+1000);}
                                    else{last2pins[1]=imgsID[i];}
                                    pin2X = pinX;
                                    pin2Y = pinY;
                                    pin2X=(imgs_x[i]+.0)/scalefactorX+circlex;
                                    pin2Y=(imgs_y[i]+.0)/scalefactorY+circley;
                                }
                            else{
                                    if(Integer.parseInt(imgsID[i])<1500){
                                        last2pins[0]= String.valueOf(Integer.parseInt(imgsID[i])+1000);}
                                    else{last2pins[0]=imgsID[i];}
                                    pinX1 = pinX;
                                    pinY1 = pinY;
                                    pinX1=(imgs_x[i]+.0)/scalefactorX+circlex;
                                    pinY1=(imgs_y[i]+.0)/scalefactorY+circley;
                                }
                            }
                        }
                        if(Integer.parseInt(imgsID[i])<1500){lastpinid=Integer.parseInt(imgsID[i])+1000;}
                        else{lastpinid=Integer.parseInt(imgsID[i]);
                        }
                        if(Integer.parseInt(imgsID[i])<1500){lastputed=String.valueOf(Integer.parseInt(imgsID[i])+1000);}
                        else{lastputed=imgsID[i];
                        }
                        putedf2=false;
                        pinputedf2=true;
                        break;
                    }
            }
            if((putPinf2==1 && putPin2f2==1) || (putPin==1 && putPinf2==1)) {
                dijkstraheap dijk = new dijkstraheap();
                path = new int[maxN];
                path = dijk.getPath(Integer.parseInt(last2pins[0]), Integer.parseInt(last2pins[1]));
                length = 1;
                while (path[length] != 0) {
                    length++;
                }
                length--;
                for (int temp = 0; temp < length; temp++) {
                    lineX[temp] = imgs_x[num[path[temp+1]]];
                    lineY[temp] = imgs_y[num[path[temp+1]]];
                }
            }
            invalidate();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        this.mDetector.onTouchEvent(event);
        this.scaleDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        //Log.d(TAG, "onDown: " + event.toString());
        return false;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        //Log.d(TAG, "onLongPress: " + event.toString());
    }

    @Override
    public void onShowPress(MotionEvent event) {
        //Log.d(TAG, "onShowPress: " + event.toString());
    }


    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d("111", "onSingleTapUp: ");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        MainActivity.diss();
        //Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        invalidate();
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        double x2=event.getX();
        double y2=event.getY();
        x2 = x2 -circlex;
        y2 = y2 -circley;
        scalefactorX=actualx/x;
        scalefactorY=actualy/y;
        x2 = x2 *scalefactorX;
        y2 = y2 *scalefactorY;
        boolean bo=false;
        for(int i=0;i<maxN;i++)
        {
            if (imgs_x[i]+.0> x2 -22.0 && imgs_x[i]+.0< x2 +22.0 && imgs_y[i]+.0> y2 -22.0 && imgs_y[i]+.0< y2 +22.0)
            {
                if ((actualx/x <0.59 && !imgs[i].equals("navigation")) || (actualx/x >=0.59 && imgs[i].equals("hall"))) {
                int x1=(int)Math.round((imgs_x[i]+.0)/scalefactorX+circlex);
                int y1=(int)Math.round((imgs_y[i]+.0)/scalefactorY+circley);
                if (MainActivity.floorNumber==2){MainActivity.pop(lt[i],x1,y1);} else MainActivity.pop(lt[num[Integer.parseInt(imgsID[i])+1000]],x1,y1);
                bo=true;
                break;}
            }
        }
        if (!bo) MainActivity.diss();
        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        MainActivity.diss();
        //Log.d(TAG, "onScale: " + detector.getScaleFactor() );
        double checkx = x;
        checkx *=detector.getScaleFactor();
        if(actualx/checkx >0.35 && checkx >screenWidth){
            x *=detector.getScaleFactor();
            y *=detector.getScaleFactor();
            circlex=(circlex -detector.getFocusX())*detector.getScaleFactor() +detector.getFocusX();
            circley=(circley -detector.getFocusY())*detector.getScaleFactor() +detector.getFocusY();
            pinX1=(pinX1 -detector.getFocusX())*detector.getScaleFactor() +detector.getFocusX();
            pinY1=(pinY1-detector.getFocusY())*detector.getScaleFactor()+detector.getFocusY();
            pin2X=(pin2X -detector.getFocusX())*detector.getScaleFactor() +detector.getFocusX();
            pin2Y=(pin2Y-detector.getFocusY())*detector.getScaleFactor()+detector.getFocusY();
            invalidate();
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        //Log.d(TAG, "onScaleBegin: " + detector.getScaleFactor() );
        // return true if you want to collect the related Scale and ScaleEnd events
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        //Log.d(TAG, "onScaleEnd: " + detector.getScaleFactor() );

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        double limitx;
        double limity;
        boolean changed=false;
        if(x/2<screenWidth && y/2<screenHeight){
            if(circlex+x/2<0){
                limitx = circlex+x/2;
                if(circley+y/2<0){
                    limity = circley+y/2;
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+x/2<screenWidth && circley+y/2<screenHeight && circlex+x/2> limitx && circley+y/2> limity) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
                else if(circley+y/2>screenHeight){
                    limity = circley+y/2;
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+x/2<screenWidth && circley+y/2>0 && circlex+x/2> limitx && circley+y/2< limity) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
                else{
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+x/2<screenWidth && circley+y/2>0 &&
                            circley+y/2<screenHeight && circlex+x/2> limitx) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
            }
            else if(circlex+x/2>screenWidth){
                limitx = circlex+x/2;
                if(circley+y/2<0){
                    limity = circley+y/2;
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+x/2>0 && circley+y/2<screenHeight && circlex+x/2< limitx && circley+y/2> limity) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
                else if(circley+y/2>screenHeight){
                    limity = circley+y/2;
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+x/2>0 && circley+y/2>0 && circlex+x/2< limitx && circley+y/2< limity) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
                else{
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+x/2>0 && circley+y/2>0 && circley+y/2<screenHeight && circlex+x/2< limitx) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
            }
            else if(circley+y/2<0){
                limity = circley+y/2;
                circlex = circlex-Math.round(distanceX);
                circley = circley-Math.round(distanceY);
                if(circlex+x/2>0 && circlex+x/2<screenWidth &&
                        circley+y/2<screenHeight && circley+y/2> limity) changed=true;
                else{
                    circlex = circlex+Math.round(distanceX);
                    circley = circley+Math.round(distanceY);
                }
            }
            else if(circley+y/2>screenHeight){
                limity = circley+y/2;
                circlex = circlex-Math.round(distanceX);
                circley = circley-Math.round(distanceY);
                if(circlex+x/2>0 && circlex+x/2<screenWidth && circley+y/2>0 && circley+y/2< limity) changed=true;
                else{
                    circlex = circlex+Math.round(distanceX);
                    circley = circley+Math.round(distanceY);
                }
            }
            else{
                circlex = circlex-Math.round(distanceX);
                circley = circley-Math.round(distanceY);
                if(circlex+x/2>0 && circlex+x/2<screenWidth && circley+y/2>0 &&
                        circley+y/2<screenHeight) changed=true;
                else{
                    circlex = circlex+Math.round(distanceX);
                    circley = circley+Math.round(distanceY);
                }
            }
        }
        else{
            if(circlex>0){
                limitx = circlex;
                if(circley>0){
                    limity = circley;
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+x-screenWidth>0 && y+circley-screenHeight>0 && circley< limity && circlex< limitx) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
                else if(circley+y<screenHeight){
                    limity = circley+y;
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+x-screenWidth>0 && circley+screenHeight<screenHeight && circley+y> limity && circlex< limitx) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
                else{
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+x-screenWidth>0 && y+circley-screenHeight>0 && circley+screenHeight<screenHeight && circlex< limitx) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
            }
            else if(circley>0){
                limity = circley;
                circlex = circlex-Math.round(distanceX);
                circley = circley-Math.round(distanceY);
                if(circlex+x-screenWidth>0 && circlex+screenWidth<screenWidth && y+circley-screenHeight>0 && circley< limity) changed=true;
                else{
                    circlex = circlex+Math.round(distanceX);
                    circley = circley+Math.round(distanceY);
                }
            }
            else if(circlex+x<screenWidth){
                limitx = circlex+x;
                if(circley>0){
                    limity = circley;
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+screenWidth<screenWidth && y+circley-screenHeight>0 && circley< limity && circlex+x> limitx) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
                else if(circley+y<screenHeight){
                    limity = circley+y;
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+screenWidth<screenWidth && circley+screenHeight<screenHeight  && circley+y> limity && circlex+x> limitx) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
                else {
                    circlex = circlex-Math.round(distanceX);
                    circley = circley-Math.round(distanceY);
                    if(circlex+screenWidth<screenWidth && y+circley-screenHeight>0 && circley+screenHeight<screenHeight
                            && circlex+x> limitx) changed=true;
                    else{
                        circlex = circlex+Math.round(distanceX);
                        circley = circley+Math.round(distanceY);
                    }
                }
            }
            else if(circley+y<screenHeight){
                limity = circley+y;
                circlex = circlex-Math.round(distanceX);
                circley = circley-Math.round(distanceY);
                if(circlex+x-screenWidth>0 && circlex+screenWidth<screenWidth && circley+screenHeight<screenHeight &&
                        circley+y> limity) changed=true;
                else{
                    circlex = circlex+Math.round(distanceX);
                    circley = circley+Math.round(distanceY);
                }
            }
            else {
                circlex = circlex-Math.round(distanceX);
                circley = circley-Math.round(distanceY);
                if(circlex+x-screenWidth>0 && circlex+screenWidth<screenWidth &&
                        y+circley-screenHeight>0 && circley+screenHeight<screenHeight)
                {changed=true;}
                else if(circlex+x-screenWidth<0 && circlex+screenWidth<screenWidth &&
                        y+circley-screenHeight>0 && circley+screenHeight<screenHeight){
                    circlex = circlex+Math.round(distanceX);
                    pinY1 = pinY1 - Math.round(distanceY);
                    pin2Y = pin2Y - Math.round(distanceY);
                    invalidate();
                }
                else if(circlex+x-screenWidth>0 && circlex+screenWidth<screenWidth &&
                        y+circley-screenHeight<0 && circley+screenHeight<screenHeight){
                    circley = circley+Math.round(distanceY);
                    pinX1 = pinX1 - Math.round(distanceX);
                    pin2X = pin2X - Math.round(distanceX);
                    invalidate();
                }
                else if(circlex+x-screenWidth>0 && circlex+screenWidth>screenWidth &&
                        y+circley-screenHeight>0 && circley+screenHeight<screenHeight){
                    circlex = circlex+Math.round(distanceX);
                    pinY1 = pinY1 - Math.round(distanceY);
                    pin2Y = pin2Y - Math.round(distanceY);
                    invalidate();
                }
                else if(circlex+x-screenWidth>0 && circlex+screenWidth<screenWidth &&
                        y+circley-screenHeight>0 && circley+screenHeight>screenHeight){
                    circley = circley+Math.round(distanceY);
                    pinX1 = pinX1 - Math.round(distanceX);
                    pin2X = pin2X - Math.round(distanceX);
                    invalidate();
                }
                else{
                    circlex = circlex+Math.round(distanceX);
                    circley = circley+Math.round(distanceY);
                }
            }
        }
        if (changed)
        {
            pinX1 = pinX1 - Math.round(distanceX);
            pinY1 = pinY1 - Math.round(distanceY);
            pin2X = pin2X - Math.round(distanceX);
            pin2Y = pin2Y - Math.round(distanceY);
            invalidate();
        }
        return false;
    }
}