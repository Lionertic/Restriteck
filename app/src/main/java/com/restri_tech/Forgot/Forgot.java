package com.restri_tech.Forgot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.restri_tech.HomeActivity;
import com.restri_tech.Fragments.Main;
import com.restri_tech.MainActivity;
import com.restri_tech.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Forgot extends Fragment {
    EditText ed1;
    TextView t;
    SharedPreferences sd;
    String otp;
    boolean o = false;
    public String USGS_REQUEST_URL = "http://control.msg91.com/api/sendotp.php?authkey=240230AiJftvlVu5bb12192&sender=RSTRTK";


    public Forgot() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_forgot, container, false);
        sd = getActivity().getSharedPreferences("Forgot",0);
        t=v.findViewById(R.id.textView);
        ed1=v.findViewById(R.id.editText);

        if(sd.getBoolean("FirstN",true)){
        ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() == 10){
                    ed1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    sd.edit().putString("Ph", ed1.getText().toString()).commit();
                    sd.edit().putBoolean("FirstN", false).commit();
                    Intent i = new Intent(getContext(), HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getActivity().startActivity(i);

                }
            }
        });
        }
        else{
            if(sd.getBoolean("Finish",false)){
                getActivity().startActivity(new Intent(getContext(),HomeActivity.class));
            }
            sd.edit().putBoolean("Finish",true).commit();
            String p = sd.getString("Ph",null);
            t.setText(" Enter the coressponding phone no *******"+p.charAt(7)+p.charAt(8)+p.charAt(9));
            ed1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().length() == 10){
                        if(s.toString().equals(sd.getString("Ph",null))){
                            ed1.setText("");
                            t.setText("Enter OTP");
                            otp = Integer.toString(generateMyNumber());
                            String number ="+91"+sd.getString("Ph", null);
                            USGS_REQUEST_URL=USGS_REQUEST_URL+"&mobile="+number+"&otp="+otp+"&message=YOUR OTP "+otp;
                            SMS task = new SMS();
                            task.execute();
                            Toast.makeText(getContext(),"Otp Sent",Toast.LENGTH_LONG).show();
                            o = true;
                        }
                        else {
                            Toast.makeText(getContext(),"Wrong No",Toast.LENGTH_LONG).show();;
                        }
                    }
                    else if(o && s.toString().length() == 6){
                        if(s.toString().equals(otp)){
                            o = false;
                            SharedPreferences sd1 ;
                            sd1 = getActivity().getSharedPreferences("Pin",0);
                            sd1.edit().putBoolean("First",true).commit();
                            sd1 = getActivity().getSharedPreferences("Pattern",0);
                            sd1.edit().putBoolean("First",true).commit();
                            sd1 = getActivity().getSharedPreferences("Pass",0);
                            sd1.edit().putBoolean("First",true).commit();
                            sd.edit().putBoolean("Finish",true).commit();
                            getActivity().setTitle("Change Password");
                            Main m = new Main();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            fm.beginTransaction().replace(R.id.fragment, m).commit();
                        }
                        else {
                            Toast.makeText(getContext(),"Wrong OTP",Toast.LENGTH_LONG).show();;
                        }
                    }
                }
            });
        }

        return v;
    }
    public int generateMyNumber()
    {
        int aNumber;
        aNumber = (int)((Math.random() * 900000)+100000);
        return aNumber;
    }

    private class SMS extends AsyncTask<URL, Void, String> {

        private final String LOG_TAG = MainActivity.class.getSimpleName();

        @Override
        protected String doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(USGS_REQUEST_URL);

            try {
                makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(String earthquake) {

        }

        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }


        private void makeHttpRequest(URL url) throws IOException {

            if (url == null) {
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
    }


}
