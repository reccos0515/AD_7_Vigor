package com.example.vigor.vigor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This activity allows users of every type to create plans
 * of any length of days.
 *
 * @author Adrian Hamill
 */
public class PlanCreatorActivity extends AppCompatActivity {

    private EditText activity;
    private EditText sets;
    private EditText reps;
    private Button savePlan;
    private Button addToPlan;
    private Button prev;
    private Button next;
    private TextView Day;
    private ListView listView;

    private static CustomAdapter adapter;
    public static int index = 0;
    public static String PlanName;
    private boolean fromTrainerTools = false;

    private String TAG = PlanCreatorActivity.class.getSimpleName();
    private SessionController session;

    static ArrayList<ArrayList> days;
    static ArrayList<DataModel> dataModels;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_creator);

        //Initialize things for the activity
        session = new SessionController(getApplicationContext());

        activity = (EditText) findViewById(R.id.PlanCreatorEtActivity);
        sets = (EditText) findViewById(R.id.PlanCreatorEtSets);
        reps = (EditText) findViewById(R.id.PlanCreatorEtReps);

        Day = (TextView) findViewById(R.id.PlanCreatorEtDay);

        listView = (ListView) findViewById(R.id.list);

        savePlan = (Button) findViewById(R.id.PlanCreatorBtnsavePlan);
        addToPlan = (Button) findViewById(R.id.PlanCreatorBtnaddToPlan);
        prev = (Button) findViewById(R.id.PlanCreatorBtnPrev);
        next = (Button) findViewById(R.id.PlanCreatorBtnNext);

        days = new ArrayList<>();
        dataModels = new ArrayList<>();

        Bundle receivedData = getIntent().getExtras();
        if (receivedData != null){
            if (receivedData.getBoolean("fromTrainerTools"))
                fromTrainerTools = true;
            else
                fromTrainerTools = false;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(
                PlanCreatorActivity.this);
        alert.setTitle("What would you like to name this plan?");
        final EditText alertInput = new EditText(PlanCreatorActivity.this);
        alert.setView(alertInput);
        alertInput.setInputType(InputType.TYPE_CLASS_TEXT);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PlanName = alertInput.getText().toString();
                dialog.dismiss();
            }
        });
        alert.show();

        Day.setText("Day: 1");

        adapter = new CustomAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);

        //Listen fo ran activity to be added to a day of the plan.
        addToPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toAddActivity = activity.getText().toString();
                String toAddSets = sets.getText().toString();
                String toAddReps = reps.getText().toString();
                //make sure the activity is entered correctly
                if (toAddActivity.equals("")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            PlanCreatorActivity.this);
                    alert.setTitle("No activity entered.");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                } else if (toAddSets.equals("") || toAddReps.equals("") ||
                        isInt(sets.getText().toString()) || isInt(reps.getText().toString())) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            PlanCreatorActivity.this);
                    alert.setTitle("Amount entered isn't a number.");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                } else {
                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                            "http://proj309-ad-07.misc.iastate.edu:8080/exercise/check/" + toAddActivity, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("exists")){
                                    dataModels.add(new DataModel(
                                            UserTableActivity.UserEmailString,
                                            PlanName,
                                            activity.getText().toString(),
                                            sets.getText().toString(),
                                            reps.getText().toString()));
                                    activity.setText("");
                                    sets.setText("");
                                    reps.setText("");
                                    adapter.notifyDataSetChanged();
                                } else {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(
                                            PlanCreatorActivity.this);
                                    alert.setTitle("Activity does not exist in our list, would you like to add it?");
                                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            JSONObject tosend = new JSONObject();
                                            try {
                                                tosend.put("name", activity.getText().toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                                                    "http://proj309-ad-07.misc.iastate.edu:8080/exercise/add", tosend, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.d(TAG, response.toString());
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    VolleyLog.d(TAG, "Error:" + error.getMessage());
                                                }
                                            });
                                            VolleySingleton.getInstance().addToRequestQueue(jsonRequest, "json_req");
                                            dataModels.add(new DataModel(
                                                    UserTableActivity.UserEmailString,
                                                    PlanName,
                                                    activity.getText().toString(),
                                                    sets.getText().toString(),
                                                    reps.getText().toString()));
                                            activity.setText("");
                                            sets.setText("");
                                            reps.setText("");
                                            adapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                    });
                                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alert.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, response.toString());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error:" + error.getMessage());
                        }
                    });
                    VolleySingleton.getInstance().addToRequestQueue(jsonRequest, "json_req");
                }
            }
        });

        //increment the day the user is editing
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (days.size() <= PlanCreatorActivity.index) {
                    days.add(dataModels);
                } else {
                    days.set(PlanCreatorActivity.index, dataModels);
                }
                PlanCreatorActivity.index++;
                Day.setText("Day: " + (PlanCreatorActivity.index + 1));
                if (days.size() - 1 < index) {
                    dataModels = new ArrayList<>();
//                    days.add(dataModels);
                } else {
                    dataModels = days.get(PlanCreatorActivity.index);
                }
                adapter = new CustomAdapter(dataModels, getApplicationContext());
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        //Decrement the day the user is editing
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (days.size() <= PlanCreatorActivity.index)
                    days.add(dataModels);
                else
                    days.set(PlanCreatorActivity.index, dataModels);
                if (PlanCreatorActivity.index > 0) {
                    PlanCreatorActivity.index--;
                    dataModels = days.get(PlanCreatorActivity.index);
                    Day.setText("Day: " + (PlanCreatorActivity.index + 1));
                    adapter = new CustomAdapter(dataModels, getApplicationContext());
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            PlanCreatorActivity.this);
                    alert.setTitle("Already on first day.");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            }
        });

        //listen for the user to delete an item from the current day
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        PlanCreatorActivity.this);
                alert.setTitle("Are you sure about that?");
                alert.setMessage("Are you sure to delete record?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataModels.remove(position);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return false;
            }
        });

        //save the plan to the server.
        savePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ask user for a plan name
                savePlan.setClickable(false);
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        PlanCreatorActivity.this);
                alert.setTitle("Are you sure about that");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get the plan name
                        //make sure the plan name is correctly entered
                        if (PlanName.equals("")) {
                            AlertDialog.Builder alert2 = new AlertDialog.Builder(
                                    PlanCreatorActivity.this);
                            alert2.setTitle("No Plan Name entered.");
                            alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert2.show();
                        } else {
                            //add the last day entered to the plan name
                            if (days.size() <= PlanCreatorActivity.index) {
                                days.add(dataModels);
                            } else {
                                days.set(PlanCreatorActivity.index, dataModels);
                            }
                            JSONArray toSend = new JSONArray();
                            String planURL = "";
                            for (int i = 0; i < days.size(); i++) {
                                ArrayList temp = days.get(i);
                                for (int j = 0; j < temp.size(); j++) {
                                    DataModel tempActivity = (DataModel) temp.get(j);
                                    JSONObject toPut = new JSONObject();
                                    try {
                                        if (!fromTrainerTools) {
                                            toPut.put("userEmail", session.returnEmail());
                                            planURL = "http://proj309-ad-07.misc.iastate.edu:8080/userPlan/add";
                                        } else {
                                            toPut.put("trainerId", session.returnUserID());
                                            toPut.put("userEmail", UserTableActivity.UserEmailString);
                                            planURL = "http://proj309-ad-07.misc.iastate.edu:8080/trainerPlan/add";
                                        }
                                        toPut.put("planName", PlanName);
                                        toPut.put("day", (i + 1));
                                        toPut.put("exercise", tempActivity.getexercise());
                                        toPut.put("sets", Integer.parseInt(tempActivity.getsets()));
                                        toPut.put("reps", Integer.parseInt(tempActivity.getreps()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    toSend.put(toPut);
                                }
                            }
                            //send the plan to the server
                            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                                    planURL, toSend, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.d(TAG, response.toString());
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "Error; " + error.toString());
                                }
                            });
                            VolleySingleton.getInstance().addToRequestQueue(jsonArrayRequest, "jsonArray_req");
                            dialog.dismiss();
                            finish();
                            //clear all the data from the current plan and reset the data the user sees
                            dataModels = new ArrayList<>();
                            days = new ArrayList<>();
                            PlanCreatorActivity.index = 0;
                            Day.setText("Day: " + (PlanCreatorActivity.index + 1));
                            adapter = new CustomAdapter(dataModels, getApplicationContext());
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savePlan.setClickable(true);
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
    }

    /**
     * Helper method used to determine whether a string
     * has only integers or not.
     * @param name
     * @return
     */
    private boolean isInt(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars)
            if (Character.isLetter(c))
                return true;
        return false;
    }
}
