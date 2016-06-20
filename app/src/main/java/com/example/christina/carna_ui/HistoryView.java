package com.example.christina.carna_ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.christina.carna_ui.overview.SensorType;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class HistoryView extends AppCompatActivity {

    ImageView imageView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        imageView = (ImageView)findViewById(R.id.historyImageView);
        listView = (ListView)findViewById(R.id.historyListView);

        SensorType type = (SensorType)getIntent().getSerializableExtra("Type");
        ListHistoryItemsAdapter adapter = new ListHistoryItemsAdapter(this,R.id.historyListView);
        AngelMemoDataSource source = new AngelMemoDataSource(this);
        source.open();

        switch (type) {
            case TEMPERATURE:
                //Get Values
                imageView.setImageResource(R.drawable.temperatur);
                break;
            case STEPCOUNTER:
                //Get Values
                imageView.setImageResource(R.drawable.stepcounter);
                break;
            case HEARTRATE:
                //Get Values
                imageView.setImageResource(R.drawable.heart);
                break;
            default:
                // TODO: 20.06.2016
        }

        listView.setAdapter(adapter);

    }
}
