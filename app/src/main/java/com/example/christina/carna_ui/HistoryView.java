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

        imageView = (ImageView)findViewById(R.id.imageView);
        listView = (ListView)findViewById(R.id.listView);

        SensorType type = (SensorType)getIntent().getSerializableExtra("Type");

        switch (type) {
            case TEMPERATURE:
                imageView.setImageDrawable(getDrawable(R.drawable.temperatur));

                break;
            case STEPCOUNTER:
                imageView.setImageDrawable(getDrawable(R.drawable.stepcounter));
                break;
            case HEARTRATE:
                imageView.setImageDrawable(getDrawable(R.drawable.heart));
                break;

            default:
                // TODO: 20.06.2016
        }

    }
}
