package th.ac.tu.siit.its333.lab7exercise1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    double tbkk = -1 ,tnon = -1 ,tpathum = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WeatherTask w = new WeatherTask();
        w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
    }

    public void buttonClicked(View v) {
        int id = v.getId();
        WeatherTask w = new WeatherTask();
        switch (id) {
            case R.id.btBangkok:
                if(tbkk < 0) {
                    w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
                    tbkk = System.currentTimeMillis();
                }
                else{
                    if(System.currentTimeMillis() - tbkk >= 60000){
                        w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
                        tbkk = System.currentTimeMillis();
                    }
                    else{
                        Toast t = Toast.makeText(this,"Not reload the data",Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
                break;
            case R.id.btNon:
                if(tnon < 0) {
                w.execute("http://ict.siit.tu.ac.th/~cholwich/nonthaburi.json", "Nonthaburi Weather");
                    tnon = System.currentTimeMillis();
                }
                else {
                    if (System.currentTimeMillis() - tnon >= 60000) {
                        w.execute("http://ict.siit.tu.ac.th/~cholwich/nonthaburi.json", "Nonthaburi Weather");
                        tnon = System.currentTimeMillis();
                    } else {
                        Toast t = Toast.makeText(this, "Not reload the data.", Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
                break;
            case R.id.btPathum:
                if(tpathum < 0) {
                w.execute("http://ict.siit.tu.ac.th/~cholwich/pathumthani.json", "Pathumthani Weather");
                    tpathum = System.currentTimeMillis();
                }
                else {
                    if (System.currentTimeMillis() - tpathum >= 60000) {
                        w.execute("http://ict.siit.tu.ac.th/~cholwich/pathumthani.json", "pathumthani Weather");
                        tpathum = System.currentTimeMillis();
                    } else {
                        Toast t = Toast.makeText(this, "Not reload the data", Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
                    break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class WeatherTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        ProgressDialog pDialog;
        String title;

        double windSpeed;
        double maintemp,maintempmin,maintempmax,mainhumidity;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading weather data ...");
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                title = params[1];
                URL u = new URL(params[0]);
                HttpURLConnection h = (HttpURLConnection)u.openConnection();
                h.setRequestMethod("GET");
                h.setDoInput(true);
                h.connect();

                int response = h.getResponseCode();
                if (response == 200) {
                    reader = new BufferedReader(new InputStreamReader(h.getInputStream()));
                    while((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    //Start parsing JSON
                    JSONObject jWeather = new JSONObject(buffer.toString());
                    JSONObject jWind = jWeather.getJSONObject("wind");
                    windSpeed = jWind.getDouble("speed");


                    JSONObject jMain = jWeather.getJSONObject("main");
                    maintemp = jMain.getDouble("temp");
                    maintempmin = jMain.getDouble("temp_min");
                    maintempmax = jMain.getDouble("temp_max");
                    mainhumidity = jMain.getDouble("humidity");

                    errorMsg = "";
                    return true;
                }
                else {
                    errorMsg = "HTTP Error";
                }
            } catch (MalformedURLException e) {
                Log.e("WeatherTask", "URL Error");
                errorMsg = "URL Error";
            } catch (IOException e) {
                Log.e("WeatherTask", "I/O Error");
                errorMsg = "I/O Error";
            } catch (JSONException e) {
                Log.e("WeatherTask", "JSON Error");
                errorMsg = "JSON Error";
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            TextView tvTitle, tvWeather, tvWind, tvTemp , tvHumid;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            tvTitle = (TextView)findViewById(R.id.tvTitle);
            tvWeather = (TextView)findViewById(R.id.tvWeather);
            tvTemp = (TextView)findViewById(R.id.tvTemp);
            tvHumid = (TextView)findViewById(R.id.tvHumid);
            tvWind = (TextView)findViewById(R.id.tvWind);

            if (result) {
                tvTitle.setText(title);
                tvTemp.setText(String.format("%.1f ( max = %.1f, min = %.1f) " ,maintemp,maintempmax,maintempmin));
                tvHumid.setText(String.format("%.1f", mainhumidity));
                tvWind.setText(String.format("%.1f", windSpeed));

            }
            else {
                tvTitle.setText(errorMsg);
                tvWeather.setText("");
                tvWind.setText("");
            }
        }
    }
}
