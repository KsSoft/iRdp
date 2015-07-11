package com.affmobapp.zrdp.licence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

    private static final String mainVersionUri = "com.affmobapp.zrdp";
    private final String marketLink = "market://details?id=com.affmobapp.zrdp&c=apps";

    public static boolean fullVersionInstalled(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    //public static final String license = "alexander.frizler.besh@gmail.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        if (fullVersionInstalled(mainVersionUri, getApplicationContext())) {
            Intent secondIntent = new Intent();
            secondIntent.setAction(Intent.ACTION_MAIN);
            secondIntent.setClassName(mainVersionUri, "com.affmobapp.zrdp");
            startActivity(secondIntent);
            finish();
        } else {
            getMainApplication();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_main, menu);

        return true;
    }

    private void getMainApplication() {
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("iRemote Not installed");
        ad.setIcon(R.drawable.market);
        ad.setMessage("This is iRemote Licence. Please download iRemote main Applicaion from Android Market.");
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        ad.setPositiveButton("Get from Market", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(marketLink));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        }).show();
        //ad.show();

    }

}
