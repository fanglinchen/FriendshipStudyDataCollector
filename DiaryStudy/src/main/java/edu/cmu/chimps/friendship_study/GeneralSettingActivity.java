package edu.cmu.chimps.friendship_study;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

// General set up what user would like to store.

public class GeneralSettingActivity extends PreferenceActivity {

    private Context context;
    private static boolean tracking_clicked;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this)); // Set up the default exception handler for unexpected exception
        context = this;
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            Preference trackingServicePreference =findPreference("collectDataButton");
            trackingServicePreference
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Utils.startTracking(MyApplication.getContext());
                    Toast.makeText(MyApplication.getContext(),"Tracking Started!", Toast.LENGTH_LONG).show();

                    return false;
                }
            });

            final Preference participantIdPreference = findPreference("participantId");
            participantIdPreference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.shared_preference_key_participant_id),
                                    newValue.toString());
                            editor.apply();
                            participantIdPreference.setEnabled(false);
                            return true;
                        }
            });


            final Preference f1Preference = findPreference("REUFriend1");
            f1Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                            Set<String> set = sharedPref.getStringSet(getResources().getString(R.string.reu_friends_key), null);

                            if(set==null)
                                set=new HashSet<String>();

                            set.add(newValue.toString());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putStringSet(getResources().getString(R.string.reu_friends_key), set);

                            editor.apply();
                            f1Preference.setEnabled(false);
                            return true;
                        }
                    });

            final Preference f2Preference = findPreference("REUFriend2");
            f2Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                            Set<String> set = sharedPref.getStringSet(getResources().getString(R.string.reu_friends_key), null);

                            if(set==null)
                                set=new HashSet<String>();

                            set.add(newValue.toString());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putStringSet(getResources().getString(R.string.reu_friends_key), set);

                            editor.apply();
                            f2Preference.setEnabled(false);
                            return true;
                        }
                    });

            final Preference f3Preference = findPreference("REUFriend3");
            f3Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                            Set<String> set = sharedPref.getStringSet(getResources().getString(R.string.reu_friends_key), null);

                            if(set==null)
                                set=new HashSet<String>();

                            set.add(newValue.toString());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putStringSet(getResources().getString(R.string.reu_friends_key), set);

                            editor.apply();
                            f3Preference.setEnabled(false);
                            return true;
                        }
                    });

            final Preference f4Preference = findPreference("NREUFriend1");
            f4Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                            Set<String> set = sharedPref.getStringSet(getResources().getString(R.string.nreu_friends_key), null);

                            if(set==null)
                                set=new HashSet<String>();

                            set.add(newValue.toString());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putStringSet(getResources().getString(R.string.nreu_friends_key), set);

                            editor.apply();
                            f4Preference.setEnabled(false);
                            return true;
                        }
            });

            final Preference f5Preference = findPreference("NREUFriend2");
            f5Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                            Set<String> set = sharedPref.getStringSet(getResources().getString(R.string.nreu_friends_key), null);

                            if(set==null)
                                set=new HashSet<String>();

                            set.add(newValue.toString());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putStringSet(getResources().getString(R.string.nreu_friends_key), set);

                            editor.apply();
                            f5Preference.setEnabled(false);
                            return true;
                        }
                    });
            final Preference f6Preference = findPreference("NREUFriend3");
            f6Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                            Set<String> set = sharedPref.getStringSet(getResources().getString(R.string.nreu_friends_key), null);

                            if(set==null)
                                set=new HashSet<>();

                            set.add(newValue.toString());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putStringSet(getResources().getString(R.string.nreu_friends_key), set);

                            editor.apply();
                            f6Preference.setEnabled(false);
                            return true;
                        }
                    });
        };
    }
}