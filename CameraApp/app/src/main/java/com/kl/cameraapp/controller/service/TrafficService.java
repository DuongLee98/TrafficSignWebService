package com.kl.cameraapp.controller.service;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class TrafficService extends AsyncTask<Void, Void, JSONObject> {
    String URL;
    JSONObject data;
    public TrafficService(String url, JSONObject data){
        this.URL=url;
        this.data = data;
    }
    @Override
    protected JSONObject doInBackground(Void... voids) {
        JSONObject res = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            StringEntity se = new StringEntity(data.toString());
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(se);


            HttpResponse response = (HttpResponse) httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();

                // convert content stream to a String
                String resultString= convertStreamToString(instream);
                instream.close();
                resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

                res = new JSONObject(resultString);

                // Raw DEBUG output of our received JSON object:
                Log.i("res","<JSONObject>\n"+res.toString()+"\n</JSONObject>");


            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
    }
}
