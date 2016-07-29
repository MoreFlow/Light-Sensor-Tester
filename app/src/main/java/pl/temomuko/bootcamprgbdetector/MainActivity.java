package pl.temomuko.bootcamprgbdetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.TextView_name)
    TextView sensorNameView;

    @BindView(R.id.TextView_vendor)
    TextView vendorNameView;

    @BindView(R.id.TextView_version)
    TextView versionView;

    @BindView(R.id.TextView_power)
    TextView powerView;

    @BindView(R.id.TextView_resolution)
    TextView resolutionView;

    @BindView(R.id.TextView_delay)
    TextView delayView;

    @BindView(R.id.ImageView_image)
    ImageView imageView;

    @BindView(R.id.TextView_lux)
    TextView luxView;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private float lightSensorMaxRange;
    private Long previousStamp;
    final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            luxView.setText(String.format(Locale.getDefault(), "%.2f\nLUX", event.values[0]));
            float percent = event.values[0] / lightSensorMaxRange;
            imageView.setAlpha(percent);
            Long delay = event.timestamp / 1000000 - previousStamp;
            delayView.setText(String.format(getString(R.string.delay_text), delay));
            previousStamp = event.timestamp / 1000000;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupSensor();
        setupLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    private void setupSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null) {
            setErrorLayout();
        }
        lightSensorMaxRange = 1000;
        previousStamp = System.currentTimeMillis();
    }

    private void setupLayout() {
        sensorNameView.setText(lightSensor.getName());
        vendorNameView.setText(String.format("%s%s", getString(R.string.vendor_text), lightSensor.getVendor()));
        versionView.setText(String.format(Locale.getDefault(), "%s%d", getString(R.string.version_text), lightSensor.getVersion()));
        powerView.setText(String.format("%s%s mA", getString(R.string.power_text), lightSensor.getPower()));
        resolutionView.setText(String.format("%s%s L", getString(R.string.resolution_text), lightSensor.getResolution()));
    }

    private void setErrorLayout() {
        sensorNameView.setText(R.string.sensor_detect_error);
        imageView.setImageResource(R.drawable.ic_warning_black_48dp);
    }
}
