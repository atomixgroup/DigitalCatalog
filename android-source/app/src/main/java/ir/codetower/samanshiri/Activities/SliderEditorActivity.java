package ir.codetower.samanshiri.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.BuildConfig;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Slide;
import ir.codetower.samanshiri.R;

public class SliderEditorActivity extends AppCompatActivity implements View.OnClickListener {
    private int id;
    private String image_url;
    private AppCompatImageView primary_image;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 1889;
    private Bitmap photo;
    private String selectedName;
    private Slide slide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_editor);
        Intent data = getIntent();
        ImageView back = (ImageView) findViewById(R.id.back);
        if (data.hasExtra("id") && App.prefManager.isAdmin()) {
            id = data.getExtras().getInt("id");
            slide = App.dbHelper.getSlide(id);
            primary_image = (AppCompatImageView) findViewById(R.id.primary_image);


            setPicasso("primary", slide.getId(), primary_image);



            final EditText p_name = (EditText) findViewById(R.id.p_name);
            final EditText tab_title = (EditText) findViewById(R.id.tab_title);
            final EditText slide_title = (EditText) findViewById(R.id.slide_title);
            ImageView save = (ImageView) findViewById(R.id.save);



            p_name.setText(slide.getLink());

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo show waiting dialog

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            slide.setLink(p_name.getText() + "");
                            slide.setTabTitle(tab_title.getText()+"");
                            slide.setTitle(slide_title.getText()+"");
                            HashMap<String, String> params = new HashMap<>();
                            params.put("id", slide.getId() + "");
                            params.put("link", slide.getLink());
                            params.put("tab_title", slide.getTabTitle());
                            params.put("title", slide.getTitle());
                            params.put("link", slide.getLink());
                            params.put("username", App.prefManager.getPreference("username"));
                            params.put("password", App.prefManager.getPreference("password"));
                            params.put("sh2", Configurations.sh2);
                            params.put("sh1", App.prefManager.getPreference("sh1"));
                            if(primary_image.getTag()!= null && primary_image.getTag().equals("edited")){
                                params.put("image", Util.getStringFile(new File(slide.getImage())));
                            }
                            App.webService.postRequest(params, Configurations.apiUrl + "saveSlide", new WebService.OnPostReceived() {
                                @Override
                                public void onReceived(String message) {
                                    App.showToast("عملیات با موفقیت انجام شد.");
                                    App.dbHelper.update(slide);
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
        } else {
            finish();
        }


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
        final AlertDialog dialog = new AlertDialog.Builder(SliderEditorActivity.this).create();

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
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,   FileProvider.getUriForFile(SliderEditorActivity.this, BuildConfig.APPLICATION_ID + ".provider",photoFile));
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
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
                File temp = new File(Configurations.sdTempFolderAddress+"slider/"+slide.getId());
                Util.writeToFile(photo, temp, 80, true);
                primary_image.setImageBitmap(Util.getResizedBitmap(photo));
                primary_image.setTag("edited");
                slide.setImage(temp.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                File temp = new File(Configurations.sdTempFolderAddress +"slider/"+ slide.getId());
                Util.writeToFile(photo, temp, 80, true);
                primary_image.setImageBitmap(Util.getResizedBitmap(photo));
                primary_image.setTag("edited");
                slide.setImage(temp.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.primary_image:
                selectedName = "primary";
                showChooseDialog();
                break;
        }
    }

    private void setPicasso(final String name, int id, final AppCompatImageView targetImageView) {
        String path = Configurations.sdTempFolderAddress + "slider" ;
        File temp = new File(path,  slide.getId()+"");
        if (temp.exists()) {
            Picasso.with(App.context).load(temp).placeholder(R.drawable.placeholder).into(targetImageView);
        } else {
            Picasso.with(App.context).load(Configurations.apiUrl + "getSlideImage?id=" + id).placeholder(R.drawable.placeholder).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    targetImageView.setImageBitmap(bitmap);
                    File targetPath = new File(Configurations.sdTempFolderAddress + "slider");
                    targetPath.mkdirs();
                    File target = new File(targetPath, slide.getId()+"");
                    try {
                        Util.writeToFile(bitmap, target, 100, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

}
