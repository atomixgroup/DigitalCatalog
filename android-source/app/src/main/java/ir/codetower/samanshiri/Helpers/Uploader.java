package ir.codetower.samanshiri.Helpers;

import android.os.AsyncTask;

import java.util.HashMap;


/**
 * Created by GunAy-PC17 on 10/18/2016.
 */

public class Uploader extends AsyncTask<String, String, String> {

    WebService.OnPostReceived onPostReceived;

    public Uploader(String source, HashMap<String,String> params, String uploadUrl, WebService.OnPostReceived onPostReceived) {
        this.onPostReceived=onPostReceived;
        UploaderManager.source = source;
        UploaderManager.uploadUrl=uploadUrl;
        UploaderManager.params=params;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... strings) {
        UploaderManager uploadManager = new UploaderManager();

        UploaderManager.timeOutCount=10;
        uploadManager.onProgress = new UploaderManager.OnProgress() {
            @Override
            public void onProgress(int percent) {

            }
        };
        return uploadManager.upload();

    }

    @Override
    protected void onPostExecute(String responseMessage) {
        super.onPostExecute(responseMessage);
        onPostReceived.onReceived(responseMessage);
    }


}
