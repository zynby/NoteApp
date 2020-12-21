package com.shariati.notes;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.shariati.notes.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(MainActivity.this, HomePage.class));
    }
    //https://stackoverflow.com/questions/10378764/is-there-any-library-or-algorithm-for-persian-shamsi-or-jalali-calendar-in-and/16411897
}
