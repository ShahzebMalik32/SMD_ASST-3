package com.example.taskflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(v -> openHomeScreen());
    }

    private void openHomeScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.EXTRA_USER_NAME, "Hari");
        startActivity(intent);
        finish();
    }
}
