package ir.codetower.samanshiri.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.BuildConfig;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;

public class AboutEditorActivity extends AppCompatActivity implements View.OnClickListener {
    private int id;
    private String image_url;
    private AppCompatImageView primary_image;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 1889;
    private Bitmap photo;
    private String selectedName;
    private Setting setting;
    private String primaryUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_editor);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Setting.getPColor()));
        }
        setting = App.prefManager.getSettings();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(Setting.getPColor()));

        setSupportActionBar(toolbar);
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setColorFilter(Color.parseColor(setting.getPrimary_color()));


        primary_image = (AppCompatImageView) findViewById(R.id.primary_image);


        final EditText description = (EditText) findViewById(R.id.description);
        ImageView save = (ImageView) findViewById(R.id.save);
        save.setColorFilter(Color.parseColor(Setting.getSColor()));


        setPicasso(primary_image);

        description.setText(setting.getAbout());


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(description.getWindowToken(), 0);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo show waiting dialog

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        HashMap<String, String> params = new HashMap<>();

                        setting.setAbout(description.getText() + "");
                        params.put("description", description.getText() + "");
                        params.put("username", App.prefManager.getPreference("username"));
                        params.put("password", App.prefManager.getPreference("password"));
                        params.put("sh2", Configurations.sh2);
                        params.put("sh1", App.prefManager.getPreference("sh1"));

                        if (primary_image.getTag() != null && primary_image.getTag().equals("edited")) {
                            params.put("primary", Util.getStringFile(new File(primaryUrl)));
                        }

                        App.webService.postRequest(params, Configurations.apiUrl + "saveAbout", new WebService.OnPostReceived() {
                            @Override
                            public void onReceived(String message) {
                                App.showToast("عملیات با موفقیت انجام شد.");
                                setting.setAbout(description.getText() + "");
                                App.prefManager.saveSettings(setting);

                            }

                            @Override
                            public void onReceivedError(String message) {
                                App.showToast("عملیات به مشکل برخورد کرد.");

                            }
                        });
                    }
                }).start();
                finish();


            }
        });
        primary_image.setOnClickListener(this);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void showChooseDialog() {
        final LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.choose_pic_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(AboutEditorActivity.this).create();

        dialog.setView(dialogView);
        dialog.setCancelable(true);
        CustomButton camera = (CustomButton) dialogView.findViewById(R.id.camera);
        CustomButton gallery = (CustomButton) dialogView.findViewById(R.id.sd);

        camera.setText("دوربین");
        gallery.setText("گالری");

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,   FileProvider.getUriForFile(AboutEditorActivity.this, BuildConfig.APPLICATION_ID + ".provider",photoFile));
                        }
                        else{
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
                dialog.dismiss();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
                App.prefManager.savePreference("temp_image_which", selectedName);

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "digital_catalog_temp_photo";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        image_url = "file:" + image.getAbsolutePath();

        App.prefManager.savePreference("temp_image", image_url);
        App.prefManager.savePreference("temp_image_which", selectedName);
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            String image_url = App.prefManager.getPreference("temp_image");
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image_url));
                File temp = new File(Configurations.sdTempFolderAddress + "about");
                Util.writeToFile(photo, temp, 80, true);
                setBitmapToViewWithName("about", temp.getAbsolutePath());
                primary_image.setTag("edited");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                File temp = new File(Configurations.sdTempFolderAddress + "about");
                Util.writeToFile(photo, temp, 80, true);
                setBitmapToViewWithName("about", temp.getAbsolutePath());
                primary_image.setTag("edited");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setBitmapToViewWithName(String name, String url) {
        switch (name) {
            case "about":
                primary_image.setImageBitmap(Util.getResizedBitmap(photo));
                primary_image.setTag("edited");
                primaryUrl = url;
                break;

        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.primary_image:
                selectedName = "about";
                showChooseDialog();
                break;


        }
    }

    private void setPicasso(final AppCompatImageView targetImageView) {


        String path = Configurations.sdTempFolderAddress + "about";
        primaryUrl = path;
        File temp = new File(path);
        if (temp.exists()) {
//            if (album) {
            Bitmap bitmap = BitmapFactory.decodeFile(temp.getAbsolutePath());


            targetImageView.setImageBitmap(bitmap);
//                Picasso.with(App.context).load(temp).placeholder(R.drawable.placeholder).memoryPolicy(MemoryPolicy.NO_CACHE).resize(100, 150).into(targetView);
//            }else {
//                Picasso.with(App.context).load(temp).placeholder(R.drawable.placeholder).memoryPolicy(MemoryPolicy.NO_CACHE).into(targetView);
//            }
        } else {
            ImageDownloader.downloadImage(Configurations.apiUrl + "getAboutImage?sh2=" + Configurations.sh2, new ImageDownloader.OnDownloadImageCompleteListener() {
                @Override
                public void OnDownloadComplete(Bitmap bitmap) {
//                        if(album){
//                            targetView.setImageBitmap(Util.resizeBitmap(bitmap,100));
//                        }
//                        else{
                    targetImageView.setImageBitmap(bitmap);


//                        }

                    File temp = new File(Configurations.sdTempFolderAddress + "about");
                    try {
                        Util.writeToFile(bitmap, temp, 100, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnDownloadError() {
                    targetImageView.setImageResource(R.drawable.placeholder);
                }
            });

        }
    }

}
