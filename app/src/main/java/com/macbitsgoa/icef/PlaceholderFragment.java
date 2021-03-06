package com.macbitsgoa.icef;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.macbitsgoa.icef.Lists.SpeakersList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    static Bundle args;
    TextView name;
    TextView venue;
    TextView timing;
    TextView desc;
    SimpleDraweeView image;
    private ProgressDialog mProgressDialog;
    int position;
    Button button;
    Vector<SpeakersList> vector = new Vector<>();
    DownloadTask downloadTask;
    public PlaceholderFragment() {

    }

    @SuppressLint("ValidFragment")
    public PlaceholderFragment(int position, Vector<SpeakersList> vector) {
        this.vector = vector;
        this.position = position;
    }


    public static PlaceholderFragment newInstance(int sectionNumber, Vector<SpeakersList> vector) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_speakers, container, false);

        name = rootView.findViewById(R.id.Name);
        venue = rootView.findViewById(R.id.Venue);
        desc = rootView.findViewById(R.id.Description);
        timing = rootView.findViewById(R.id.Timing);
        image = rootView.findViewById(R.id.image);
        button=rootView.findViewById(R.id.download);


        image.setImageURI(vector.get(position).getImageurl());
        name.setText(vector.get(position).getName());
        venue.setText(vector.get(position).getVenue());
        timing.setText(vector.get(position).getTimings());
        desc.setText(vector.get(position).getDescription());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ICEF/Presentations/"+vector.get(position).getName()+".pdf");
                 if(file.exists()) {

                    Uri path = Uri.fromFile(file);
                    Intent objIntent = new Intent(Intent.ACTION_VIEW);
                    objIntent.setDataAndType(path, "application/pdf");
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent intent1 = Intent.createChooser(objIntent, "Open PDF with..");
                    try {
                        getActivity().startActivity(intent1);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "Activity Not Found Exception ", Toast.LENGTH_SHORT).show();
                    }
                }else {

                     mProgressDialog = new ProgressDialog(getContext());
                     mProgressDialog.setMessage("Downloading");
                     mProgressDialog.setIndeterminate(true);
                     mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                     mProgressDialog.setCancelable(true);
                     downloadTask = new DownloadTask(getContext(), vector.get(position).getUrl(), vector.get(position).getName());
                     downloadTask.execute();
                     mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                         @Override
                         public void onCancel(DialogInterface dialog) {
                             downloadTask.cancel(true);

                         }
                     });

                 }


            }
        });
        return rootView;
    }


 private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private String URL;
        private String name;

        public DownloadTask(Context context, String URL,String name) {
            this.context = context;
            this.URL = URL;
            this.name=name;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                Log.e("path",Environment.getExternalStorageDirectory() + "/ICEF/Presentations/"+name+".pdf");
                File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICEF/Presentations/");
                if(! file.exists()){
                    file.mkdirs();}
                output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICEF/Presentations/"+name+".pdf");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();

            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            Log.e("path",Environment.getExternalStorageDirectory() + "/ICEF/Presentations/"+name+".pdf");
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICEF/Presentations/"+name+".pdf");


            Uri path = Uri.fromFile(file);
            Intent objIntent = new Intent(Intent.ACTION_VIEW);
            objIntent.setDataAndType(path, "application/pdf");
            objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Intent intent1 = Intent.createChooser(objIntent, "Open PDF with..");
            try {
                context.startActivity(intent1);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Activity Not Found Exception ", Toast.LENGTH_SHORT).show();
            }

        }
    }
}



class SectionsPagerAdapter extends FragmentPagerAdapter {

    Vector<SpeakersList> vector = new Vector<>();

    public SectionsPagerAdapter(FragmentManager fm, Vector<SpeakersList> vector) {
        super(fm);
        this.vector = vector;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.e("ABC", String.valueOf(position));

        return new PlaceholderFragment(position, vector);
    }

    @Override
    public int getCount() {

        return vector.size();
    }


}


