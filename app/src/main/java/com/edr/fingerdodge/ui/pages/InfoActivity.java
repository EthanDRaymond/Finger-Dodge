package com.edr.fingerdodge.ui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.edr.fingerdodge.R;

/**
 * @author Ethan Raymond
 */
public class InfoActivity extends StatisticsTrackingActivity {

    /**
     * This text view contains the information blurb.
     */
    //private TextView blurb;
    /**
     * This text view contains the contact email address.
     */
    private TextView email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        email = (TextView) findViewById(R.id.info_email);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{
                        getBaseContext().getResources().getString(R.string.info_email)});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Something about Finger Dodge...");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
    }

}
