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
import com.example.christina.carna_ui.customview.OpticalGraphView;
import com.example.christina.carna_ui.database.AngelMemoDataSource;
import com.example.christina.carna_ui.database.AngelMemoWerte;
import com.example.christina.carna_ui.enumclass.BroadcastIntentType;
import com.example.christina.carna_ui.enumclass.BroadcastIntentValueType;
import com.example.christina.carna_ui.enumclass.IntentValueType;
import com.example.christina.carna_ui.enumclass.SensorType;
import com.example.christina.carna_ui.listviewadapter.ListHistoryItem;
import com.example.christina.carna_ui.listviewadapter.ListHistoryItemsAdapter;

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

        if(type == SensorType.HEARTRATE){
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
                    switch(waveform){
                        case OPTICAL_BLUE:
                            o = blueGraph;
                            break;
                        case OPTICAL_GREEN:
                            o = greenGraph;
                            break;
                    }
                    o.addValue((float)intent.getIntExtra(BroadcastIntentValueType.OPTICAL_VALUE.toString(),0));
                }
            };
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
                imageView.setImageResource(R.drawable.temperatur);
                values = source.getWerte(b.getInt(IntentValueType.USER_ID.toString()),source.getSensorByName(SensorType.TEMPERATURE.toString()));
                break;
            case STEPCOUNTER:
                //Get Values
                imageView.setImageResource(R.drawable.stepcounter);
                values =source.getWerte(b.getInt(IntentValueType.USER_ID.toString()),source.getSensorByName(SensorType.STEPCOUNTER.toString()));
                break;
            case HEARTRATE:
                //Get Values
                imageView.setImageResource(R.drawable.heart);
                values = source.getWerte(b.getInt(IntentValueType.USER_ID.toString()),source.getSensorByName(SensorType.HEARTRATE.toString()));
                break;
            case BATTERY:
                //Get Values
                imageView.setImageResource(R.drawable.battery);
                values = source.getWerte(b.getInt(IntentValueType.USER_ID.toString()),source.getSensorByName(SensorType.BATTERY.toString()));
                break;
            default:
                break;
        }
        if(values != null){
            for(int i = 0; i < values.size(); i++){
                adapter.addItem(new ListHistoryItem(values.get(i).getWert(),values.get(i).getDatum()));
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
