package com.dailymeditation.android.activities;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dailymeditation.android.R;
import com.dailymeditation.android.utils.LogUtils;
import com.squareup.seismic.ShakeDetector;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.SINGLE;

public class TestActivity extends AppCompatActivity implements ShakeDetector.Listener {

    @BindView(R.id.calendar_view)
    CalendarPickerView calendarPickerView;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mUnbinder = ButterKnife.bind(this);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        Date today = new Date();
//        calendarPickerView.init(today, nextYear.getTime()).withSelectedDate(today);

        calendarPickerView.init(today, nextYear.getTime())
                .inMode(SINGLE);

        calendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Toast.makeText(TestActivity.this, date.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
    }

    @Override
    protected void onStop() {
        super.onStop();

        LogUtils.logI("TAG", calendarPickerView.getSelectedDate().toString());

        for (Date date : calendarPickerView.getSelectedDates()) {
            LogUtils.logI("TAG", date.toString());
        }

    }

    @Override
    public void hearShake() {
        Toast.makeText(this, "Don't shake me, bro!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
