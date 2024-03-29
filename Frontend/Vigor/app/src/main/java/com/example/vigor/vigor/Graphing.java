package com.example.vigor.vigor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This activity allows the user to request a graphical representatiuon
 * of the step data they've saved withihn the last week.
 *
 * @author Adrian Hamill
 */
public class Graphing extends AppCompatActivity {
    private int i = 0;
    private int data[] = new int[7];
    private int datsrecieved[] = new int[7];
    public static int j = 0;
    private String TAG = Graphing.class.getSimpleName();

    TextView jsonresults;
//    JSONObject data;

    RequestQueue requestQue;
    private SessionController session;
    private DateController dateController;
    private Date currentDate;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphing);

        jsonresults = (TextView) findViewById(R.id.jsonData);
        //initialize session controller
        session = new SessionController(getApplicationContext());

        //initialize date controller
        dateController = new DateController();
        currentDate = dateController.dateOfToday();

        Button results = (Button) findViewById(R.id.averageBTN);
        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] ids = new int[]{R.id.numEditText1, R.id.numEditText2, R.id.numEditText3,
                        R.id.numEditText4, R.id.numEditText5, R.id.numEditText6, R.id.numEditText7};

                //Read in Values
                int j = 0;
                for (int id : ids) {
                    EditText t = (EditText) findViewById(id);
                    if (t.getText().toString().equals("")) {
                        ids[j] = 0;
                    } else {
                        ids[j] = Integer.parseInt(t.getText().toString());
                    }
                }
                Graph(ids, null);
            }
        });

        Button populate = (Button) findViewById(R.id.popButton);
        populate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hardcoded for testing
                String jsonUrl = "http://proj309-ad-07.misc.iastate.edu:8080/steps/multiple/" +
                        session.returnUserID() + "/7";
                JsonArrayRequest jsonArrRequest = new JsonArrayRequest(Request.Method.GET, jsonUrl,
                        null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject element = (JSONObject) response.getJSONObject(i);
                                        data[i] = element.getInt("steps");
                                        datsrecieved[i] = Integer.parseInt(element.getString("date"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //Graph data
                                Graph(data, datsrecieved);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });

                VolleySingleton.getInstance().addToRequestQueue(jsonArrRequest, "json_req");
            }
        });
    }

    /**
     * Graph the given data
     * @param days
     * @param dates
     */
    public void Graph(int days[], int dates[]) {
        double resultNum = 0, temp = 0;

        for (int i = 0; i < 7; i++) {
            if (days[i] > temp) {
                temp = days[i];
            }
            resultNum += days[i];
        }

        //Print Average
        TextView result = (TextView) findViewById(R.id.resultTextView);
        resultNum = resultNum / 7;
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        result.setText(numberFormat.format(resultNum) + "");

        //Initialize Graph
        GraphView revGraph = (GraphView) findViewById(R.id.avgPlot);
        revGraph.removeAllSeries();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMinimumIntegerDigits(0);

        revGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));

        //Display Graph
        BarGraphSeries<DataPoint> weekRev = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(1, days[0]),
                new DataPoint(2, days[1]),
                new DataPoint(3, days[2]),
                new DataPoint(4, days[3]),
                new DataPoint(5, days[4]),
                new DataPoint(6, days[5]),
                new DataPoint(7, days[6])
        });
        revGraph.addSeries(weekRev);

        //Post Configurations
        weekRev.setDrawValuesOnTop(true);
        weekRev.setValuesOnTopColor(Color.BLACK);
        Viewport view1 = revGraph.getViewport();
        view1.setMinY(0);
        weekRev.setSpacing(5);

        revGraph.getGridLabelRenderer().setNumVerticalLabels(5);
        revGraph.getViewport().setMinX(0.5);
        revGraph.getViewport().setMaxX(7.5);

        revGraph.getViewport().setXAxisBoundsManual(true);
    }
}
