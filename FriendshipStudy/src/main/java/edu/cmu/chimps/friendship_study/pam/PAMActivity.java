package edu.cmu.chimps.friendship_study.pam;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.privacystreams.core.UQI;
import com.github.privacystreams.storage.DropboxOperators;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import edu.cmu.chimps.friendship_study.ExceptionHandler;
import edu.cmu.chimps.friendship_study.GeneralSettingActivity;
import edu.cmu.chimps.friendship_study.R;
import edu.cmu.chimps.friendship_study.Utils;
//Photographic Affect Meter measure user's atitude 4x4 galery the best matched photo to receive his mood

public class PAMActivity extends AppCompatActivity {

    Bitmap[] images;
    int[] imageIds;
    private final Random random = new Random();
    private static String pam_photo_id;

    private int selection = GridView.INVALID_POSITION;
    private GridView gridview;

    public static final String[] IMAGE_FOLDERS = new String[]{
            "1_afraid",
            "2_tense",
            "3_excited",
            "4_delighted",
            "5_frustrated",
            "6_angry",
            "7_happy",
            "8_glad",
            "9_miserable",
            "10_sad",
            "11_calm",
            "12_satisfied",
            "13_gloomy",
            "14_tired",
            "15_sleepy",
            "16_serene"
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTitle("Select a Photo");
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this)); // Set up the default exception handler for unexpected exception
        setContentView(R.layout.activity_pam);


        gridview = (GridView) this.findViewById(R.id.pam_grid);
        Button submit = (Button) this.findViewById(R.id.post_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check PAM input
                if (null == pam_photo_id) {
                    Toast toast = Toast.makeText(PAMActivity.this, "Please select a picture!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    onSubmit();
                }
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        loadImages();
        setupPAM();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if(Utils.hasStoredPreferences(this) && Utils.isTrackingEnabled(this)) {
            menu.findItem(R.id.general_config).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.general_config:
                startActivity(new Intent(this,GeneralSettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MyAsyncTask extends AsyncTask<String, Object, Object> {
        @Override
        protected Object doInBackground(String[] strings) {
            String participantId = Utils.getParticipantID(PAMActivity.this);
            DropboxOperators.uploadTo(participantId+"/PAM.txt",true).apply(new UQI(PAMActivity.this), strings[0]);
            return null;
        }
    }


    private String onSubmit() {
        try {

            int idx = Integer.valueOf(pam_photo_id.split("_")[0]);
            DateTime dt = new DateTime();

            PamSchema pamSchema = new PamSchema(idx, dt);
            JSONObject body = pamSchema.toJSON();

            new MyAsyncTask().execute(body.toString());
            Toast.makeText(PAMActivity.this,
                    "Thank you. Your response is being saved.",
                    Toast.LENGTH_LONG).show();

            // clear selection
            pam_photo_id = null;

            PAMActivity.this.finish();
        } catch (Exception e) {
            Log.e("e",e.toString());
            Toast.makeText(PAMActivity.this,
                    "Submission failed. Please contact study coordinator",
                    Toast.LENGTH_LONG).show();
        }
        return null;
    }


    private void loadImages() {
        images = new Bitmap[IMAGE_FOLDERS.length];
        imageIds = new int[IMAGE_FOLDERS.length];

        AssetManager assets = getResources().getAssets();
        String subFolder;
        for (int i = 0; i < IMAGE_FOLDERS.length; i++) {
            subFolder = "pam_images/" + IMAGE_FOLDERS[i];
            try {
                String filename = assets.list(subFolder)[random.nextInt(3)];
                images[i] = BitmapFactory.decodeStream(assets.open(subFolder + "/" + filename));
                imageIds[i] = filename.split("_")[1].charAt(0) - '0';
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void setupPAM() {
        // Start PAM
        gridview.setAdapter(new BaseAdapter() {

            private final int width = PAMActivity.this.getWindowManager().getDefaultDisplay()
                    .getWidth();

            @Override
            public int getCount() {
                return images.length;
            }

            @Override
            public Object getItem(int arg0) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView;
                if (null == convertView) {
                    imageView = new ImageView(PAMActivity.this);
                    imageView.setLayoutParams(new AbsListView.LayoutParams(width / 4, width / 4));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setColorFilter(null);
                } else {
                    imageView = (ImageView) convertView;
                }
                imageView.setImageBitmap(images[position]);

                if (position == selection)
                    highlightSelection(imageView);

                return imageView;
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (selection != gridview.INVALID_POSITION)
                    ((ImageView) parent.getChildAt(selection)).setColorFilter(null);
                highlightSelection(v);
                selection = position;
                pam_photo_id = IMAGE_FOLDERS[position];
            }
        });
    }

    private void highlightSelection(View v) {
        ((ImageView) v).setColorFilter(0xffff9933, PorterDuff.Mode.MULTIPLY);
    }


}
