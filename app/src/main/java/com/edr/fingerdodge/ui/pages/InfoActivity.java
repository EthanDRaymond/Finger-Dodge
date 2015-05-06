package com.edr.fingerdodge.ui.pages;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.edr.fingerdodge.R;

/**
 * @author Ethan Raymond
 */
public class InfoActivity extends ActionBarActivity {

    private TextView blurb;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setTitle("App Information");
        email = (TextView) findViewById(R.id.info_email);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                        getBaseContext().getResources().getString(R.string.info_email) });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Something about Finger Dodge...");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
    }

}
