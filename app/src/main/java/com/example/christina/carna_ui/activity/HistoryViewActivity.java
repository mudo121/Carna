package com.example.christina.carna_ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.christina.carna_ui.R;
import com.example.christina.carna_ui.customview.BatteryLevelGraphView;
import com.example.christina.carna_ui.customview.OpticalGraphView;
import com.example.christina.carna_ui.database.AngelMemoDataSource;
import com.example.christina.carna_ui.database.AngelMemoWerte;
import com.example.christina.carna_ui.enumclass.BroadcastIntentType;
import com.example.christina.carna_ui.enumclass.BroadcastIntentValueType;
import com.example.christina.carna_ui.enumclass.IntentValueType;
import com.example.christina.carna_ui.enumclass.SensorType;
import com.example.christina.carna_ui.listviewadapter.ListHistoryItem;
import com.example.christina.carna_ui.listviewadapter.ListHistoryItemsAdapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class HistoryViewActivity extends AppCompatActivity {

    ImageView imageView;
    ListView listView;
    BroadcastReceiver receiver;
    OpticalGraphView greenGraph;
    OpticalGraphView blueGraph;
    BatteryLevelGraphView batteryGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        SensorType type = (SensorType)getIntent().getSerializableExtra(IntentValueType.SENSORTYPE.toString());

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // nothing
            }
        };

        if(type == SensorType.HEARTRATE) {
            setContentView(R.layout.activity_history_heartrate);
            greenGraph = (OpticalGraphView) findViewById(R.id.graph_blue);
            blueGraph = (OpticalGraphView) findViewById(R.id.graph_green);
            blueGraph.setStrokeColor(Color.BLUE);
            greenGraph.setStrokeColor(Color.GREEN);
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    BroadcastIntentValueType waveform = BroadcastIntentValueType.valueOf(intent.getStringExtra(BroadcastIntentValueType.OPTICAL_SENSOR_COLOR.toString()));
                    OpticalGraphView o = null;
                    switch (waveform) {
                        case OPTICAL_BLUE:
                            o = blueGraph;
                            break;
                        case OPTICAL_GREEN:
                            o = greenGraph;
                            break;
                    }
                    o.addValue((float) intent.getIntExtra(BroadcastIntentValueType.OPTICAL_VALUE.toString(), 0));
                }
            };
        } else if(type == SensorType.BATTERY) {
            setContentView(R.layout.activity_history_battery);
            batteryGraph = (BatteryLevelGraphView) findViewById(R.id.graph_battery);

        }else {
            setContentView(R.layout.activity_history);
        }

        imageView = (ImageView)findViewById(R.id.historyImageView);
        listView = (ListView)findViewById(R.id.historyListView);

        ListHistoryItemsAdapter adapter = new ListHistoryItemsAdapter(this,R.id.historyListView);
        AngelMemoDataSource source = new AngelMemoDataSource(this);
        source.open();
        List<AngelMemoWerte> values = null;

        switch (type) {
            case TEMPERATURE:
                //Get Values
                setTitle("Carna - Temperatur Overview");
                imageView.setImageResource(R.drawable.temperatur);
                values = source.getWerte(b.getInt(IntentValueType.USER_ID.toString()),source.getSensorByName(SensorType.TEMPERATURE.toString()));
                break;
            case STEPCOUNTER:
                //Get Values
                setTitle("Carna - Stepcounter Overview");
                imageView.setImageResource(R.drawable.stepcounter);
                values =source.getWerte(b.getInt(IntentValueType.USER_ID.toString()),source.getSensorByName(SensorType.STEPCOUNTER.toString()));
                break;
            case HEARTRATE:
                //Get Values
                setTitle("Carna - Heartrate Overview");
                imageView.setImageResource(R.drawable.heart);
                values = source.getWerte(b.getInt(IntentValueType.USER_ID.toString()),source.getSensorByName(SensorType.HEARTRATE.toString()));
                break;
            case BATTERY:
                //Get Values
                setTitle("Carna - Battery Level Overview");
                imageView.setImageResource(R.drawable.battery);
                values = source.getWerte(b.getInt(IntentValueType.USER_ID.toString()),source.getSensorByName(SensorType.BATTERY.toString()));
                break;
            default:
                    break;
            }
            if(values != null){
                if(type == SensorType.BATTERY) {
                    if(values.size() < 100) {
                        batteryGraph.setMaximumNumberOfValues(100);
                    } else {
                        batteryGraph.setMaximumNumberOfValues(values.size());
                    }
                    for(int i = values.size()-1; i >= 0; i--){
                        batteryGraph.addValue(Float.parseFloat(values.get(i).getWert()));
                    }
                }
                for (int i = 0; i < values.size(); i++) {
                    Timestamp ts = Timestamp.valueOf(values.get(i).getDatum());

                    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = s.format(ts);

                    adapter.addItem(new ListHistoryItem(values.get(i).getWert(), dateString));
            }
        }
        listView.setAdapter(adapter);
    }

    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(BroadcastIntentType.OPTICAL_WAVE.toString())
        );
    }
}
