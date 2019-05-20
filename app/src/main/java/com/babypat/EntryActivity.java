package com.babypat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * CreateAt : 2019/5/18
 * Describe :
 *
 * @author chendong
 */
public class EntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        findViewById(R.id.btn).setOnClickListener(v -> {
            startActivity(new Intent(this, TestActivity.class));
        });
    }
}
