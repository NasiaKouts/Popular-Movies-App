package aueb.gr.nasiakouts.popularmovies.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import aueb.gr.nasiakouts.popularmovies.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
