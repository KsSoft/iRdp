/*
   Application Settings Activity

   Copyright 2013 Thincast Technologies GmbH, Author: Martin Fleisz

   This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
   If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

package com.freerdp.freerdpcore.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.widget.Toast;

import com.freerdp.freerdpcore.R;

import java.io.File;
import java.io.IOException;

public class ApplicationSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    Activity self = this;
    private Preference prefEraseAllCertificates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("SettingsActivity", "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.application_settings);
        this.downloadPreference();
        this.getListView().setDivider(new ColorDrawable(R.color.divider_border_color));
        prefEraseAllCertificates = (Preference) findPreference("security.clear_certificate_cache");

        // erase certificate cache button
        prefEraseAllCertificates.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
                builder.setTitle(R.string.dlg_title_clear_cert_cache)
                        .setMessage(R.string.dlg_msg_clear_cert_cache)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                clearCertificateCache();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });

        // register for preferences changed notification
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(getPreferenceManager().getSharedPreferences(), "power.disconnect_timeout");

        if (com.mk.iRdp.billingUtil.CheckTrial.isLicensePurchased(com.mk.iRdp.billingUtil.CheckTrial.LICENSE_URI, getApplicationContext())) {
            PreferenceCategory mCategory = (PreferenceCategory) findPreference("General_Pref");
            Preference mPreference = (Preference) findPreference("download");
            mCategory.removePreference(mPreference);
        }

    }

    private void downloadPreference() {
        try {
            com.mk.iRdp.billingUtil.CheckTrial.saveStartTimeStamp(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Preference download = (Preference) findPreference("download");
        download.setSummary(com.mk.iRdp.billingUtil.CheckTrial.timeRemaining(getApplicationContext()));
        download.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                com.mk.iRdp.billingUtil.CheckTrial.startInAppBilling(getApplicationContext(), self, com.mk.iRdp.billingUtil.CheckTrial.PURCHASE_LICENSE, false);
                return true;
            }
        });

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("power.disconnect_timeout")) {
            int val = sharedPreferences.getInt(key, 5);
            Preference pref = findPreference(key);
            if (pref != null) {
                if (val == 0)
                    pref.setSummary(getResources().getString(R.string.settings_description_disabled));
                else
                    pref.setSummary(String.format(getResources().getString(R.string.settings_description_after_minutes), val));
            }
        }
    }

    private boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String file : children) {
                if (!deleteDirectory(new File(dir, file)))
                    return false;
            }
        }
        return dir.delete();
    }

    private void clearCertificateCache() {
        if ((new File(getFilesDir() + "/.freerdp")).exists()) {
            if (deleteDirectory(new File(getFilesDir() + "/.freerdp")))
                Toast.makeText(this, R.string.info_reset_success, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, R.string.info_reset_failed, Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, R.string.info_reset_success, Toast.LENGTH_LONG).show();
    }
}
