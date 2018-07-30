package ir.codetower.samanshiri.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.BuildConfig;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.Helpers.DownloadTask;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        App.setXy(size);
        App.webService.postRequest("sh2", Configurations.sh2, "http://www.codetower.ir/service/api/Catalog/v1/getAboutDesigner", new WebService.OnPostReceived() {
            @Override
            public void onReceived(String message) {
                App.aboutDesigner = message;
            }

            @Override
            public void onReceivedError(String message) {

            }
        });
        if (checkPermissions()) {
            App.webService.postRequest("sh2", Configurations.sh2, Configurations.apiUrl + "checkUpdate", new WebService.OnPostReceived() {
                @Override
                public void onReceived(String message) {
                    int verNum=0;
                    try
                    {
                        verNum= Integer.parseInt(message);

                    } catch (NumberFormatException ex)
                    {

                    }
                    final int num = verNum;
                    if (num > BuildConfig.VERSION_CODE) {
                        Uri updateApk = null;
                        File fileApkAddress= new File(Configurations.sdTempFolderAddress + "update.apk");

                        if (Build.VERSION.SDK_INT >= 24) {
                            updateApk=FileProvider.getUriForFile(SplashScreenActivity.this, BuildConfig.APPLICATION_ID + ".provider",fileApkAddress);
                        }
                        else{
                            updateApk=Uri.fromFile(fileApkAddress);
                        }
                        if (fileApkAddress.exists()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(updateApk, "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        } else {
                            final Uri finalUpdateApk = updateApk;
                            DownloadTask downloadTask = new DownloadTask(new DownloadTask.ProgressPercentListener() {
                                @Override
                                public void onListen(int percent) {

                                }

                                @Override
                                public void onEnd(String result) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(finalUpdateApk, "application/vnd.android.package-archive");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(intent);
                                }
                            });
                            downloadTask.execute(Configurations.apiUrl + "getUpdate?sh2=" + Configurations.sh2, Configurations.sdTempFolderAddress + "update.apk");
                        }
                    } else {
                        new File(Configurations.sdTempFolderAddress + "update.apk").delete();

                    }
                    getServerData();

                }

                @Override
                public void onReceivedError(String message) {
                    if (checkPermissions()) {
                        getServerData();

                    }
                }
            });
        }

    }


    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE
    };

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull final String[] permissions, @NonNull int[] grantResults) {
        boolean flag = false;
        for (int grant : grantResults) {
            if (grant != 0) {
                flag = true;
            }
        }
        if (flag) {
            checkPermissions();
        } else {
            getServerData();


        }

    }

    private void getServerData() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        App.IMEI = telephonyManager.getDeviceId();
        App.getDataFromServerAndSaveToDataBase(new WebService.OnGetDataCompleteListener() {
            @Override
            public void onGetDataCompleted(String info) {
                App.prefManager.savePreference("firstrun", "no");
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onGetDataError(String info) {
                if (info.equals("E1")) {
                    if (App.prefManager.getPreference("firstrun").equals("no")) {
                        App.showToast("برای بروز بودن اطلاعات باید گوشی تان را به اینترنت وصل کنید");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        App.showToast("برای بروز بودن اطلاعات باید گوشی تان را به اینترنت وصل کنید");
                        finish();
                    }
                    Log.e("error", "onGetDataError:E1 ");
                } else {
                    getServerData();
                }
            }
        });
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "دانلود با موقیت به پایان رسید.در صورت باز نشدن خودکار مکان دانلود شما میتوانید در پوشه دانلود پیدا کنید.", Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
            startActivity(i);
        }
    };
}
