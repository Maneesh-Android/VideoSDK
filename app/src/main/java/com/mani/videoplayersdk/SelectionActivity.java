package com.mani.videoplayersdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SelectionActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_view);

        Button manifest = findViewById(R.id.manifest);
        Button m3u8 = findViewById(R.id.m3u8);
        Button mp4 = findViewById(R.id.mp4);
        Button mp4Encripted = findViewById(R.id.mp4encripted);
        Button selection = findViewById(R.id.select);
        EditText videoUrl = findViewById(R.id.videoUrl);

        selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
                intent.putExtra("videoType", 1);
                intent.putExtra("videoUrl", videoUrl.getText().toString());
                startActivity(intent);
            }
        });

        manifest.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            intent.putExtra("videoType", 0);
            startActivity(intent);
        });

        m3u8.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            intent.putExtra("videoType", 1);
            startActivity(intent);
        });

        mp4.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            intent.putExtra("videoType", 2);
            startActivity(intent);
        });

        mp4Encripted.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, MainActivity.class);
            intent.putExtra("videoType", 3);
            startActivity(intent);
//            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }
}
