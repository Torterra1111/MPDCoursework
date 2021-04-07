package org.me.gcu.equakestartercode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Locale;
import java.time.Month;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

//Kellan brosnan-S1828587
public class MainActivity extends AppCompatActivity
{
    private Button DateStart;
    private Button DateEnd;
    private Button ExtraInfo;
    private Button StartSearch;
    private String result;
    private String url1="";
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    private DatePickerDialog datePickerDialog;

    private ListView EQlist;
    //List of Strings from XML
    ArrayList<String> QuakeTitle;
    ArrayList<String> QuakeDesc;
    ArrayList<String> QuakeLink;
    ArrayList<String> QuakeDate;
    ArrayList<String> QuakeCata;
    ArrayList<String> QuakeeLat;
    ArrayList<String> QuakeLong;
    //Data storage for Parsed info
    ArrayList<String> QuakeDisplayTitle;
    ArrayList<Double> QuakeMag;
    ArrayList<Integer> QuakeDepth;
    ArrayList<Double> QuakeNS;
    ArrayList<Double> QuakeEW;
    ArrayList<String> QuakeSortedDate;
    //Infomation temparty
    ArrayList<String> Displaylol = new ArrayList<>();
    ArrayList<String> ResetDisplay = new ArrayList<>();
    ArrayList<String> DesctLol = new ArrayList<>();
    ArrayList<Integer> TempSortShDp;
    ArrayList<Double> TempSortNS;
    ArrayList<Double> TempSortEW;
    ArrayList<Double> TempSortMag;
    //Pass through data for
    ArrayList<Double> Latitude;
    ArrayList<Double> Longitude;
    ArrayList<Double> Magnitude;
    ArrayList<String> Descript;
    //Adaptor storage~
    ArrayAdapter<String> adapt;
    //To sort date
    Boolean SSDate;
    Boolean SEDate;
    String StartDate;
    String EndDate;
    int Choice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Datapull
        QuakeTitle = new ArrayList<>();
        QuakeDesc = new ArrayList<>();
        QuakeLink = new ArrayList<>();
        QuakeDate = new ArrayList<>();
        QuakeCata = new ArrayList<>();
        QuakeeLat = new ArrayList<>();
        QuakeLong = new ArrayList<>();
        //Data storage for pulled info
        QuakeDisplayTitle = new ArrayList<>();
        QuakeDepth = new ArrayList<>();
        QuakeMag = new ArrayList<>();
        QuakeNS = new ArrayList<>();
        QuakeEW = new ArrayList<>();
        //Sorting
        TempSortShDp = new ArrayList<>();
        TempSortNS = new ArrayList<>();
        TempSortEW = new ArrayList<>();
        TempSortMag = new ArrayList<>();
        //Sorting storage
        Latitude= new ArrayList<>();
        Longitude= new ArrayList<>();
        Magnitude= new ArrayList<>();
        Descript= new ArrayList<>();
        //To allow date sorting
        SSDate = false;
        SEDate = false;

        QuakeSortedDate = new ArrayList<String>();

        //StartDate
        DateStart = (Button) findViewById(R.id.DateSearch);
        DateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                SSDate = true;
                StartDatePick();
                datePickerDialog.show();
            }
        });

        //End date
        DateEnd = (Button) findViewById(R.id.EndDateSearch);
        DateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                SEDate = true;
                EndDatePick();
                datePickerDialog.show();
            }
        });

        //start sort
        StartSearch = (Button) findViewById(R.id.StartSearch);
        StartSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //SORT!
                DateEnd.setText("Date end");
                DateStart.setText("Date start");
                BeginSort();

            }
        });

        //Resort
        ExtraInfo = findViewById(R.id.ExtraSearch);
        ExtraInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Test();
                SortList(QuakeDisplayTitle,Magnitude,true);
            }
        });

        //List
        EQlist = (ListView) findViewById(R.id.EQList);
        EQlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Passing Colours
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                i.putExtra("Luuat", Latitude.get(position));
                i.putExtra("Loonge",Longitude.get(position));
                i.putExtra("Magnitude", Magnitude.get(position));
                i.putExtra("QuakInfo", Descript.get(position));
                startActivity(i);
            }
        });
        new ProcessinBackground().execute();

    }

    private void StartDatePick() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day,month,year);
                StartDate = date;
                DateStart.setText(+day + "/" + month + "\n" + year);
            }

        };
        Calendar cal = Calendar.getInstance();
        int DisplayYear = cal.get(Calendar.YEAR);
        int DisplayMonth = cal.get(Calendar.MONTH);
        int Displayday = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this,style,dateSetListener,DisplayYear,DisplayMonth,Displayday);
    }

    private void EndDatePick() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                //Calendar TestCal = new Cal
                month = month + 1;
                String date = makeDateString(day,month,year);
                EndDate = date;
                DateEnd.setText(day + "/" + month + "\n" + year);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this,style,dateSetListener,year,month,day);
    }

    private String makeDateString(int day, int month, int year) {
        if(day <= 9)
        {
            return ("0" + day + " " + month + " " + year);
        }
        else
        {
            return (day + " " + month + " " + year);
        }

    }

    private int MakeMonthInt(String Month){

        int t = 0;
        switch (Month)
        {
            case "Jan":
                t = 1;
                break;
            case "Feb":
                t = 2;
                break;
            case "Mar":
                t = 3;
                break;
            case "Apr":
                t = 4;
                break;
            case "May":
                t = 5;
                break;
            case "Jun":
                t = 6;
                break;
            case "Jul":
                t = 7;
                break;
            case "Aug":
                t = 8;
                break;
            case "Sep":
                t = 9;
                break;
            case "Oct":
                t = 10;
                break;
            case "Nov":
                t = 11;
                break;
            case "Dec":
                t = 12;
                break;

        }
        return t;
    }

    public InputStream getInputStream(URL url) //Connecting to website.
    {
        try
        {
            return  url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return  null;
        }
    }

    public class  ProcessinBackground extends AsyncTask<Integer, Void, Exception>
    {
        ProgressDialog progressdialouge = new ProgressDialog(MainActivity.this);
        Exception problem = null;

        @Override
        protected void onPreExecute() { //Loading
            super.onPreExecute();
            progressdialouge.setMessage("Hold on im loading the feed!");
        }

        @Override
        protected Exception doInBackground(Integer... params) {

            try {
                URL XMLData = new URL("http://quakes.bgs.ac.uk/feeds/MhSeismology.xml");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);
                XmlPullParser XMLPull = factory.newPullParser();
                XMLPull.setInput(getInputStream(XMLData), "UTF_8");

                boolean InItem = false; //if we have reached an item we want to pull from
                int eventType = XMLPull.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (XMLPull.getName().equalsIgnoreCase("item")) { //If its an item we start pulling
                            InItem = true;
                        } else if (XMLPull.getName().equalsIgnoreCase("title") && InItem) {
                            QuakeTitle.add(XMLPull.nextText());
                        } else if (XMLPull.getName().equalsIgnoreCase("description") && InItem) {
                            QuakeDesc.add(XMLPull.nextText());
                        } else if (XMLPull.getName().equalsIgnoreCase("link") && InItem) {
                            QuakeLink.add(XMLPull.nextText());
                        } else if (XMLPull.getName().equalsIgnoreCase("pubDate") && InItem) {
                            QuakeDate.add(XMLPull.nextText());
                        } else if (XMLPull.getName().equalsIgnoreCase("category") && InItem) {
                            QuakeCata.add(XMLPull.nextText());
                        } else if (XMLPull.getName().equalsIgnoreCase("geo:lat") && InItem) {
                            QuakeeLat.add(XMLPull.nextText());
                        } else if (XMLPull.getName().equalsIgnoreCase("geo:long") && InItem) {
                            QuakeLong.add(XMLPull.nextText());
                        }
                    } else if (eventType == XmlPullParser.END_TAG && XMLPull.getName().equalsIgnoreCase("item")) {
                        InItem = false; //Stop searching
                    }
                    eventType = XMLPull.next(); //next line
                }
            }
            catch (MalformedURLException e){ //Bad URL
                problem = e;
            }
            catch (XmlPullParserException e){ //bad data
                problem = e;
            }
            catch (IOException e){ //Bad ???
                problem = e;
            }
            //Or here?
            return  problem;
        }

        @Override
        protected  void onPostExecute(Exception s) { //Finsihed loading
            super.onPostExecute(s);
            PullInfo();
            Log.i("PULLINFO", "onPostExecute: " + QuakeNS.get(0));
            for(int Xi = 0; Xi < QuakeTitle.size(); Xi++)
            {
                String Xe = QuakeTitle.get(Xi);
                String[] separated = Xe.split(",");
                String trans =  separated[0].trim();
                QuakeDisplayTitle.add(trans);
            }
            //Create adaptor
            Test();
            SortList(QuakeDisplayTitle,Magnitude,true);
            progressdialouge.dismiss();
        }

    }



    public void SortList(ArrayList<String> ArrayInfo, ArrayList<Double> QuakeStrength, boolean Do)
    {
            adapt = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, ArrayInfo){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                //ColorSort.
                if(Do)
                {
                    if(QuakeStrength.get(position) <= 0.6) {
                        view.setBackgroundColor(Color.GREEN);
                    }
                    else if (QuakeStrength.get(position) > 0.6 && QuakeMag.get(position) <= 1.2) {
                        view.setBackgroundColor(Color.YELLOW);
                    }
                    else {
                        view.setBackgroundColor(Color.RED);
                    }
                }

                return view;
            }
        };
        EQlist.setAdapter(adapt);
    }
    public void PullInfo()
    {
        String e;
        String ea;
        //Sort Size pog
        for (int i = 0; i < QuakeDesc.size(); i++) {
            //Mag
            e = QuakeDesc.get(i);
            String[] separated = e.split("Magnitude:");
            String trans =  separated[1].trim();
            Double d = Double.parseDouble(trans);
            QuakeMag.add(d);

            //Depth
            String Sort1 = separated[0].trim();
            String[] TempSplit = Sort1.split("Depth:");
            String SortDepth = TempSplit[1].trim();
            String[] Depthsplit = SortDepth.split("km");
            String Depth = Depthsplit[0].trim();
            int dep = Integer.parseInt(Depth);
            QuakeDepth.add(dep);

            //Lat:Long
            String Sort2 = TempSplit[0].trim();
            String[] Direct1 = Sort2.split("Lat/long:");
            //Get only noombors
            String LatSplit =  Direct1[1].trim();
            String[] Direct2 = LatSplit.split(";");
            //Finnaly sort by long
            String se = Direct2[0].trim();
            String[] Sort3 = se.split(",");
            String tra = Sort3[0].trim();
            String ns = Sort3[1].trim();
            double lat = Double.parseDouble(tra);
            double longe = Double.parseDouble(ns);
            QuakeNS.add(lat);
            QuakeEW.add(longe);

            //date
            ea = QuakeDate.get(i);
            //Date = Values on 6,7 char
            String DatePull = ea.substring(5, 7);
            int Date = Integer.parseInt(DatePull);
            //Month = 9,10,11 char
            int Month = MakeMonthInt(ea.substring(8, 11));
            //Year == 13,14,15,16 char
            String YearPull = ea.substring(12, 16);
            int Year = Integer.parseInt(YearPull);
            String date = makeDateString(Date,Month,Year);
            QuakeSortedDate.add(date);

        }
    }



    public void BeginSort() {
        //Temp storage and bubblesort code
        int i, j;
        Double temp;
        Integer Depthtemp;
        String Tempu;
        boolean swapped;
        //Temp storage of varibles
        ArrayList<String> ViewListDisplay = new ArrayList<>();
        ArrayList<String> TempDisplayBest = new ArrayList<>();
        //Date storage
        String DateBegin;
        String DateEnd;
        //Temp storage for
        //Date infomation sort
        if (SEDate && SSDate) {
            //Clear info for storage
            Displaylol.clear();
            ResetDisplay.clear();
            TempSortShDp.clear();
            TempSortNS.clear();
            TempSortEW.clear();
            TempSortMag.clear();
            Latitude.clear();
            Longitude.clear();
            Magnitude.clear();
            Descript.clear();
            //BEGIN THE DATE SORT IF BOTH DATES ARE SET!
            String Dp = StartDate.substring(0, 2);
            int Ds = Integer.parseInt(Dp.trim()); //DAY START
            String Dep = EndDate.substring(0, 2);
            int De = Integer.parseInt(Dep.trim()); //DAY END
            String Mp = StartDate.substring(3, 5);

            int Ms = Integer.parseInt(Mp.trim()); //MONTH START
            String Mep = EndDate.substring(3, 5);
            int Me = Integer.parseInt(Mep.trim()); //MONTH END

            String Yp = StartDate.substring(5, 9);
            int Ys = Integer.parseInt(Yp.trim()); //YEAR START
            String Yep = EndDate.substring(5, 9);
            int Ye = Integer.parseInt(Yep.trim()); //YEAR END
            LocalDate DayStart = LocalDate.of(Ys, Ms, Ds);
            LocalDate DayEnd = LocalDate.of(Ye, Me, De);
            //START CHECKING
            for (int v = 0; v < QuakeTitle.size(); v++) {
                String DateArrayPull = QuakeSortedDate.get(v);
                String Dcp = DateArrayPull.substring(0, 2);
                int Dc = Integer.parseInt(Dcp.trim()); //YEAR CHECK

                String Mcp = DateArrayPull.substring(3, 5);
                int Mc = Integer.parseInt(Mcp.trim()); //MONTH CHECK

                String Ycp = DateArrayPull.substring(5, 9);
                int Yc = Integer.parseInt(Ycp.trim()); //DAY CHECK
                LocalDate DayCheck = LocalDate.of(Yc, Mc, Dc);

                if(DayEnd.compareTo(DayCheck) >= 0 && DayStart.compareTo(DayCheck) <= 0)
                {
                    Displaylol.add(QuakeTitle.get(v));
                    ResetDisplay.add(QuakeTitle.get(v));
                    PutInfo(v);
                }
            }
            if(Displaylol.size() >= 1) //Catch for null
            {
                //NORTH/SOUTH
                int n = Displaylol.size();
                for (i = 0; i < n - 1; i++) {
                    swapped = false;
                    for (j = 0; j < n - i - 1; j++) {
                        if (TempSortNS.get(j) < TempSortNS.get(j + 1)) {
                            // swap arr[j] and arr[j+1]
                            temp = TempSortNS.get(j);
                            TempSortNS.set(j, TempSortNS.get(j + 1));
                            TempSortNS.set(j + 1, temp);

                            Tempu = Displaylol.get(j);
                            Displaylol.set(j, Displaylol.get(j + 1));
                            Displaylol.set(j + 1, Tempu);
                            swapped = true;
                        }
                    }
                    // IF no two elements were
                    // swapped by inner loop, then break
                    if (swapped == false)
                        break;
                }
                TempDisplayBest.add("Most northern: "+Displaylol.get(0));
                PassInfo(0);
                TempDisplayBest.add("Most Southern: "+Displaylol.get(Displaylol.size() - 1));
                PassInfo(Displaylol.size() - 1);
                ResetList();
                //EAST/WEST
                n = Displaylol.size();
                for (i = 0; i < n - 1; i++) {
                    swapped = false;
                    for (j = 0; j < n - i - 1; j++) {
                        if (TempSortEW.get(j) < TempSortEW.get(j + 1)) {
                            // swap arr[j] and arr[j+1]
                            temp = TempSortEW.get(j);
                            TempSortEW.set(j, TempSortEW.get(j + 1));
                            TempSortEW.set(j + 1, temp);

                            Tempu = Displaylol.get(j);
                            Displaylol.set(j, Displaylol.get(j + 1));
                            Displaylol.set(j + 1, Tempu);

                            swapped = true;
                        }
                    }

                    // IF no two elements were
                    // swapped by inner loop, then break
                    if (swapped == false)
                        break;
                }
                TempDisplayBest.add("Most Eastern: "+Displaylol.get(0));
                PassInfo(0);
                TempDisplayBest.add("Most Western: "+Displaylol.get(Displaylol.size() - 1));
                PassInfo(Displaylol.size() - 1);
                ResetList();
                //DEEP/SHALLOW
                n = Displaylol.size();
                for (i = 0; i < n - 1; i++) {
                    swapped = false;
                    for (j = 0; j < n - i - 1; j++) {
                        if (TempSortShDp.get(j) < TempSortShDp.get(j + 1)) {
                            // swap arr[j] and arr[j+1]
                            Depthtemp = TempSortShDp.get(j);
                            TempSortShDp.set(j, TempSortShDp.get(j + 1));
                            TempSortShDp.set(j + 1, Depthtemp);

                            Tempu = Displaylol.get(j);
                            Displaylol.set(j, Displaylol.get(j + 1));
                            Displaylol.set(j + 1, Tempu);

                            swapped = true;
                        }
                    }

                    // IF no two elements were
                    // swapped by inner loop, then break
                    if (swapped == false)
                        break;
                }
                TempDisplayBest.add("Deepest: "+Displaylol.get(0));
                PassInfo(0);
                TempDisplayBest.add("Shallowest: "+Displaylol.get(Displaylol.size() - 1));
                PassInfo(Displaylol.size() - 1);
                ResetList();
                //STRONK
                n = Displaylol.size();
                for (i = 0; i < n - 1; i++) {
                    swapped = false;
                    for (j = 0; j < n - i - 1; j++) {
                        if (TempSortMag.get(j) < TempSortMag.get(j + 1)) {
                            // swap arr[j] and arr[j+1]
                            temp = TempSortMag.get(j);
                            TempSortMag.set(j, TempSortMag.get(j + 1));
                            TempSortMag.set(j + 1, temp);

                            Tempu = Displaylol.get(j);
                            Displaylol.set(j, Displaylol.get(j + 1));
                            Displaylol.set(j + 1, Tempu);
                            swapped = true;
                        }
                    }

                    // IF no two elements were
                    // swapped by inner loop, then break
                    if (swapped == false)
                        break;
                }
                TempDisplayBest.add("Strongest: "+Displaylol.get(0));
                PassInfo(0);
                //Display infomation
                Toast Aeoi = Toast.makeText(MainActivity.this,"Feed sorted.",Toast.LENGTH_SHORT);
                Aeoi.setGravity(Gravity.CENTER, 0 ,0);
                Aeoi.show();
                for (int Xi = 0; Xi < TempDisplayBest.size(); Xi++) {
                    String Xe = TempDisplayBest.get(Xi);
                    String[] separated = Xe.split(",");
                    String trans = separated[0].trim();
                    ViewListDisplay.add(trans);
                }
                SortList(ViewListDisplay,Magnitude,false);
            }
            else {
                Toast Aeoi = Toast.makeText(MainActivity.this,"No earthquakes between dates! Displaying normal feed.",Toast.LENGTH_SHORT);
                Aeoi.setGravity(Gravity.CENTER, 0 ,0);
                Aeoi.show();
                Test();
                for (int Xi = 0; Xi < QuakeTitle.size(); Xi++) {
                    String Xe = QuakeTitle.get(Xi);
                    String[] separated = Xe.split(",");
                    String trans = separated[0].trim();
                    ViewListDisplay.add(trans);
                }
                SortList(ViewListDisplay,Magnitude,true);
            }
            SEDate = false;
            SSDate = false;
        }
        else
        {
           Toast Aeoi = Toast.makeText(MainActivity.this,"Start/End date not set!",Toast.LENGTH_SHORT);
           Aeoi.setGravity(Gravity.CENTER, 0 ,0);
           Aeoi.show();
        }
    }

    public void PutInfo(int arrayput) {

        TempSortShDp.add(QuakeDepth.get(arrayput));
        TempSortNS.add(QuakeNS.get(arrayput));
        TempSortEW.add(QuakeEW.get(arrayput));
        TempSortMag.add(QuakeMag.get(arrayput));
    }
    public void PassInfo(int ech)
    {
        for(int i = 0; i < QuakeTitle.size(); i++)
        {
            if(Displaylol.get(ech) == QuakeTitle.get(i)) {
                Latitude.add(QuakeNS.get(i));
                Longitude.add(QuakeEW.get(i));
                Magnitude.add(QuakeMag.get(i));
                Descript.add(QuakeDesc.get(i));
                break;
            }
        }
    }
    public void ResetList()
    {
        Displaylol.clear();
        for(int i = 0; i < ResetDisplay.size(); i++)
        {
            Displaylol.add(ResetDisplay.get(i));
        }

    }
    public void Test()
    {
        Latitude.clear();
        Longitude.clear();
        Magnitude.clear();
        Descript.clear();
        for(int i = 0; i < QuakeDisplayTitle.size(); i++)
        {
            Descript.add(QuakeDesc.get(i));
            Latitude.add(QuakeNS.get(i));
            Longitude.add(QuakeEW.get(i));
            Magnitude.add(QuakeMag.get(i));
        }
    }

}

//Graveyard of old code, (F)
//Uri uri = Uri.parse(QuakeLink.get(position));
//Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//startActivity(intent);
//    public void showOptionsDialog() {
//        String[] Sortby = {"Most northerly", "Most southerly", "Most easterly ", "Most westerly", "Deepest earthquake", "Shallowest earthquake", "Largest magnitude", "None"};
//        String choice;
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setTitle("Choose sort by");
//        builder.setSingleChoiceItems(Sortby, Choice, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Choice = which;
//                switch (which) {
//                    case 0:
//                        ExtraInfo.setText("North");
//                        dialog.dismiss();
//                        break;
//                    case 1:
//                        ExtraInfo.setText("South");
//                        dialog.dismiss();
//                        break;
//                    case 2:
//                        ExtraInfo.setText("East");
//                        dialog.dismiss();
//                        break;
//                    case 3:
//                        ExtraInfo.setText("West");
//                        dialog.dismiss();
//                        break;
//                    case 4:
//                        ExtraInfo.setText("Deep");
//                        dialog.dismiss();
//                        break;
//                    case 5:
//                        ExtraInfo.setText("Shallow");
//                        dialog.dismiss();
//                        break;
//                    case 6:
//                        ExtraInfo.setText("Strong");
//                        dialog.dismiss();
//                        break;
//                    case 7:
//                        ExtraInfo.setText("None");
//                        dialog.dismiss();
//                        break;
//                }
//            }
//        });
//        builder.show();
//    }