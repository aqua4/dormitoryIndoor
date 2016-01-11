package com.example.aikosh.seproject;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ImageButton floorOne;
    private ImageButton floorTwo;
    private ImageButton saveButton;
    private Button clear;
    public static final int MAXPins=10;
    public static int indexOfPinF1=0;
    public static String[] pinsF1ID = new String[MAXPins];
    public static double[] pinsF1x = new double[MAXPins];
    public static double[] pinsF1y = new double[MAXPins];
    public static int floorNumber=2;
    private GesturesView gestureView;
    public static Bitmap bitmaphallforpins;
    public static Bitmap bitmapallforpins;
    public static Bitmap bitmaphallforpins2;
    public static Bitmap bitmapallforpins2;
    private Bitmap bitmapstar;
    static SqliteHelper dbHelper;
    static SharedPreferences prefs = null;
    static PopupWindow popUp;
    static LinearLayout layout;
    static TextView tv;
    private RelativeLayout container;
    static ViewGroup.LayoutParams params;
    static String toPop;

    private ListView lv;
    private SearchAdapter exhibits;
    private SearchView searchView;
    final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("com.example.amanbek.DormitoryIndoor", MODE_PRIVATE);
        dbHelper = new SqliteHelper(MainActivity.this);
        // Parse.enableLocalDatastore(this);
        Log.d("wtf",""+prefs.getBoolean("firstrun", true));
        if (!prefs.getBoolean("firstrun", true)) {

            gestureView = new GesturesView(getApplicationContext(), null);
            container = (RelativeLayout) findViewById(R.id.container);
            container.addView(gestureView);
            getWindow().setBackgroundDrawableResource(R.drawable.white);

            Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.star);
            bitmapstar = Bitmap.createScaledBitmap(marker, 20, 20, true);

            floorOne = (ImageButton) findViewById(R.id.firstfloor);
            floorOne.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        floorOne.setImageResource(R.drawable.f1down);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        floorOne.setImageResource(R.drawable.f1up);
                        floorNumber = 2;
                        gestureView.invalidate();
                    }
                    return true;
                }
            });

            floorTwo = (ImageButton) findViewById(R.id.secondfloor);
            floorTwo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        floorTwo.setImageResource(R.drawable.f2down);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        floorTwo.setImageResource(R.drawable.f2up);
                        floorNumber = 3;
                        gestureView.invalidate();
                    }
                    return true;
                }
            });

            clear = (Button) findViewById(R.id.clear);
            clear.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureView.last2pins[0]="";
                    gestureView.last2pins[1]="";
                    gestureView.path=null;
                    gestureView.putPin =0;
                    gestureView.putPin2 =0;
                    gestureView.putPin2f2 =0;
                    gestureView.putPinf2 =0;
                    gestureView.puted =false;
                    gestureView.putedf2 =false;
                    gestureView.lastpinid =0;
                    gestureView.pinputed=false;
                    gestureView.pinputedf2=false;
                    gestureView.lastputed="";
                    indexOfPinF1=0;
                    pinsF1ID = new String[MAXPins];
                    pinsF1x = new double[MAXPins];
                    pinsF1y = new double[MAXPins];
                    bitmapallforpins = Bitmap.createBitmap(gestureView.bitmapall);
                    bitmaphallforpins = Bitmap.createBitmap(gestureView.bitmaphall);
                    bitmapallforpins2 = Bitmap.createBitmap(gestureView.bitmapall2);
                    bitmaphallforpins2 = Bitmap.createBitmap(gestureView.bitmaphall2);
                    gestureView.invalidate();
                    return true;
                }
            });

            saveButton = (ImageButton) findViewById(R.id.saveButton);
            saveButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        saveButton.setImageResource(R.drawable.savebuttondown);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        saveButton.setImageResource(R.drawable.savebuttonup);
                        boolean isitthere = false;
                        String ID = gestureView.lastputed;
                        for (int i = 0; i < indexOfPinF1; i++) {
                            if (ID.equals(pinsF1ID[i])) {
                                isitthere = true;
                            }
                        }
                        if (!isitthere && gestureView.pinputed) {
                            if (floorNumber == 2) {
                                if (indexOfPinF1 < MAXPins) {
                                    pinsF1ID[indexOfPinF1] = ID;
                                    pinsF1x[indexOfPinF1] = (gestureView.imgs_x[gestureView.num[Integer.parseInt(ID)]] + .0);
                                    pinsF1y[indexOfPinF1] = (gestureView.imgs_y[gestureView.num[Integer.parseInt(ID)]] + .0);
                                    indexOfPinF1++;

                                    bitmapallforpins = Bitmap.createBitmap(gestureView.bitmapall);
                                    bitmaphallforpins = Bitmap.createBitmap(gestureView.bitmaphall);
                                    Canvas can = new Canvas(bitmapallforpins);
                                    Canvas can2 = new Canvas(bitmaphallforpins);
                                    for (int z = 0; z < MainActivity.indexOfPinF1; z++) {
                                        if(Integer.parseInt(pinsF1ID[z])<1500){
                                            double putParseX = MainActivity.pinsF1x[z];
                                            double putParseY = MainActivity.pinsF1y[z];
                                            can.drawBitmap(bitmapstar, (int) putParseX - 10, (int) putParseY - 10, null);
                                            can2.drawBitmap(bitmapstar, (int) putParseX - 10, (int) putParseY - 10, null);
                                        }
                                    }
                                    gestureView.invalidate();
                                }
                            } else {
                                if (indexOfPinF1 < MAXPins) {
                                    pinsF1ID[indexOfPinF1] = ID;
                                    pinsF1x[indexOfPinF1] = gestureView.imgs_x[gestureView.num[Integer.parseInt(ID)]];
                                    pinsF1y[indexOfPinF1] = gestureView.imgs_y[gestureView.num[Integer.parseInt(ID)]];
                                    indexOfPinF1++;

                                    bitmapallforpins2 = Bitmap.createBitmap(gestureView.bitmapall2);
                                    bitmaphallforpins2 = Bitmap.createBitmap(gestureView.bitmaphall2);
                                    Canvas can = new Canvas(bitmapallforpins2);
                                    Canvas can2 = new Canvas(bitmaphallforpins2);
                                    for (int z = 0; z < MainActivity.indexOfPinF1; z++) {
                                        if(Integer.parseInt(pinsF1ID[z])>1500){
                                            double putParseX = MainActivity.pinsF1x[z];
                                            double putParseY = MainActivity.pinsF1y[z];
                                            can.drawBitmap(bitmapstar, (int) putParseX - 10, (int) putParseY - 10, null);
                                            can2.drawBitmap(bitmapstar, (int) putParseX - 10, (int) putParseY - 10, null);
                                        }
                                    }
                                    gestureView.invalidate();
                                }
                            }
                        }
                    }
                    return true;
                }
            });
            popUp = new PopupWindow(this);
            layout = new LinearLayout(this);
            tv = new TextView(this);
            tv.setTextColor(Color.YELLOW);
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(tv, params);
            popUp.setContentView(layout);
            gestureView.post(new Runnable() {
                public void run() {
                    popUp.showAtLocation(gestureView, Gravity.NO_GRAVITY, 0,0);
                }
            });

            lv = (ListView)findViewById(R.id.ListView);
            ArrayList<String> items = new ArrayList<String>();

            for(int count=0;count<gestureView.lt.length-1;count++){
                if (!gestureView.imgs[count].equals("navigation")){
                    items.add(gestureView.lt[count]);
                }
            }

            exhibits = new SearchAdapter(MainActivity.this, items);
            lv.setVisibility(View.GONE);
            lv.setAdapter(exhibits);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = lv.getItemAtPosition(position);

                int count;
                for(count=0;count<gestureView.lt.length-1;count++){
                        if(listItem.toString().equals(gestureView.lt[count])){break;}
                }
                if(Integer.parseInt(gestureView.imgsID[count])>1500){floorNumber=3;}else{floorNumber=2;}
                gestureView.spinX = gestureView.imgs_x[count]/(gestureView.actualx/gestureView.x)+gestureView.circlex;
                gestureView.spinY = gestureView.imgs_y[count]/(gestureView.actualy/gestureView.y)+gestureView.circley;
                gestureView.Searchpincounter=3;
                toPop = listItem.toString();
                gestureView.lastputed =gestureView.imgsID[count];
                gestureView.pinputed=true;
                gestureView.invalidate();

                MenuItem searchItem = menu.findItem(R.id.action_search);
                searchItem.collapseActionView();
            }
        });

        // Define the listener
        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG,"View collapsed");
                lv.setVisibility(View.GONE);
                floorOne.setVisibility(View.VISIBLE);
                floorTwo.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                gestureView.setVisibility(View.VISIBLE);
                clear.setVisibility(View.VISIBLE);
                container.setBackgroundResource(R.drawable.background);
                // Do something when action item collapses
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                Log.d(TAG,"View expanded");
                lv.setVisibility(View.VISIBLE);
                floorOne.setVisibility(View.GONE);
                floorTwo.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                gestureView.setVisibility(View.GONE);
                clear.setVisibility(View.GONE);
                diss();
                container.setBackgroundResource(0);
                return true;  // Return true to expand action view
            }
        };

        // Get the MenuItem for the action item
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();

        // Assign the listener to that action item
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    exhibits.getFilter().filter("");
                    Log.i("Nomad", "onQueryTextChange Empty String");
                    lv.clearTextFilter();
                } else {
                    Log.i("Nomad", "onQueryTextChange " + newText.toString());
                    exhibits.getFilter().filter(newText.toString());
                }
                return true;
            }
        });

        // Any other things you have to do when creating the options menu…

        return true;


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_search:

                return true;
            default:
                return false;
        }
    }
    public static void diss()
    {
        tv.setText("");
        popUp.update(10000,10000,0,0);
    }
    public static void pop(String s,int x,int y) {
        Log.d("fqw",""+x+" "+y);
        tv.setText(s);
        popUp.update(x,y,300,80);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("prefs", "" + prefs.getBoolean("firstrun", true));
        if (prefs.getBoolean("firstrun", true)) {

            //Log.d("asdf", "123");
            Parse.initialize(this, "X3EZl59cHlAXRMNqTmqSBomOm78wxGXWssutP3hd", "WTk7hgFvwM41AU0QCfHUBJ6JlizRDu8nT2fGgFwM");

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Scannable");
            query.setLimit(999);
            query.whereExists("Floor");
            List<ParseObject> results;
            try {
                results = query.find();
                for (ParseObject result : results) {
                    List<Integer> list = result.getList("Relations");

                    dbHelper.putInfo(dbHelper, result.getString("ID"), list, result.getInt("x_cordinate"),
                            result.getInt("y_cordinate"), result.getInt("Floor"), result.getString("Type"),result.getString("LongText"));
                }
                prefs.edit().putBoolean("firstrun", false).apply();
            } catch (ParseException e) {
                int close = 3000;
                Toast.makeText(getApplicationContext(), "Can not connect to Parse. Please check your Internet connection and relaunch the app. It will be closed in " + (close / 1000) + " seconds",
                        Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(close);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                finish();
                System.exit(0);
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "First time app run synchronization complete. Restart the app please. ",
                    Toast.LENGTH_LONG).show();
            finish();
            System.exit(0);
        }
    }
}
