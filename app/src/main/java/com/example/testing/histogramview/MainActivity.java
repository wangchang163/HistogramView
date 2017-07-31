

package com.example.testing.histogramview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.library.HistogramView;


public class MainActivity extends AppCompatActivity {

    private String[] str = {"AA", "BB", "CC", "DD", "EE", "FF", "GG","HH","II","JJ","KK"};
    private int[] pro={100,90,80,70,60,50,40,30,20,10,5};
    private int[] progress={90,80,70,60,50,40,30,20,10};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HistogramView histogramView= (HistogramView) findViewById(R.id.histogramView);
        histogramView.setData(str,pro,progress,100);
        histogramView.startAnim();

    }

}
