package ir.codetower.samanshiri.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.BuildConfig;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Uploader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Helpers.ZipManager;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;

public class ProductEditorActivity extends AppCompatActivity implements View.OnClickListener {
    private int id;
    private String image_url;
    private AppCompatImageView primary_image;
    private AppCompatImageView p1, p2, p3, p4, p5, p6;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 1889;
    private Bitmap photo;
    private String selectedName;
    private AppCompatRadioButton rdb_product,rdb_news;
    private AppCompatCheckBox chk_download;
    private AppCompatTextView download_link;
    private Product product;
    private Button fileManager;
    private static final int FILE_MANAGER_REQUEST_CODE = 567;
    private File uploadFile;
    private LinearLayout file_section;
    private AppCompatEditText price;
    private RelativeLayout video_layout;
    private VideoView videoView;

    private Timer timer;
    private TextView videoCurrentDurationTextView;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_editor);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(Setting.getPColor()));
        setSupportActionBar(toolbar);
        fileManager= (Button) findViewById(R.id.btn_file_manager);
        fileManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ProductEditorActivity.this,FileManagerActivity2.class);
                startActivityForResult(intent,FILE_MANAGER_REQUEST_CODE);
            }
        });
        price= (AppCompatEditText) findViewById(R.id.price);
        Intent data = getIntent();
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Setting.getPColor()));
        }
        file_section= (LinearLayout) findViewById(R.id.file_section);

        ImageView back = (ImageView) findViewById(R.id.back);
        back.setColorFilter(Color.parseColor(Setting.getSColor()));
        if (data.hasExtra("id") && App.prefManager.isAdmin()) {
            id = data.getExtras().getInt("id");
            primary_image = (AppCompatImageView) findViewById(R.id.primary_image);
            video_layout = (RelativeLayout) findViewById(R.id.video_layout);
            p1 = (AppCompatImageView) findViewById(R.id.p1);
            p2 = (AppCompatImageView) findViewById(R.id.p2);
            p3 = (AppCompatImageView) findViewById(R.id.p3);
            p4 = (AppCompatImageView) findViewById(R.id.p4);
            p5 = (AppCompatImageView) findViewById(R.id.p5);
            p6 = (AppCompatImageView) findViewById(R.id.p6);
            if (id == -1) {
                product = new Product();
                product.setId(-1);
                product.setCat_id(data.getExtras().getInt("cat_id"));
                primary_image.setImageResource(R.drawable.placeholder);
                p1.setImageResource(R.drawable.placeholder);
                p2.setImageResource(R.drawable.placeholder);
                p3.setImageResource(R.drawable.placeholder);
                p4.setImageResource(R.drawable.placeholder);
                p5.setImageResource(R.drawable.placeholder);
                p6.setImageResource(R.drawable.placeholder);
            } else {
                product = App.dbHelper.getProduct(id);
                if(product.isVideo()){
                    video_layout.setVisibility(View.VISIBLE);
                    setupVideo(product.getVideo_url(),product.getVideoThumb());
                    primary_image.setImageResource(R.drawable.placeholder);
                }
                else{
                    setPicasso("primary", product.getId(), primary_image);
                }
                setPicasso("p1", product.getId(), p1);
                setPicasso("p2", product.getId(), p2);
                setPicasso("p3", product.getId(), p3);
                setPicasso("p4", product.getId(), p4);
                setPicasso("p5", product.getId(), p5);
                setPicasso("p6", product.getId(), p6);
            }


            p1.setOnClickListener(this);
            p2.setOnClickListener(this);
            p3.setOnClickListener(this);
            p4.setOnClickListener(this);
            p5.setOnClickListener(this);
            p6.setOnClickListener(this);
            setupRadio();

            final EditText p_name = (EditText) findViewById(R.id.p_name);
            final EditText description = (EditText) findViewById(R.id.description);

            ImageView save = (ImageView) findViewById(R.id.save);
            final AppCompatCheckBox checkBox= (AppCompatCheckBox) findViewById(R.id.checkBox);
            checkBox.setChecked(product.isTop());
            p_name.setText(product.getTitle());

            IBinder token = p_name.getWindowToken();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(token, 0);

            description.setText(product.getDescription());
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo show waiting dialog

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            product.setTitle(p_name.getText() + "");
                            product.setDescription(description.getText() + "");
                            HashMap<String, String> edited = new HashMap<String, String>();
                            if (primary_image.getTag() != null && primary_image.getTag().equals("edited"))
                                edited.put("primary", product.getImage());
                            if (p1.getTag() != null && p1.getTag().equals("edited"))
                                edited.put("p1", product.getA_image1());
                            if (p2.getTag() != null && p2.getTag().equals("edited"))
                                edited.put("p2", product.getA_image2());
                            if (p3.getTag() != null && p3.getTag().equals("edited"))
                                edited.put("p3", product.getA_image3());
                            if (p4.getTag() != null && p4.getTag().equals("edited"))
                                edited.put("p4", product.getA_image4());
                            if (p5.getTag() != null && p5.getTag().equals("edited"))
                                edited.put("p5", product.getA_image5());
                            if (p6.getTag() != null && p6.getTag().equals("edited"))
                                edited.put("p6", product.getA_image6());
                            if(chk_download.isChecked() && uploadFile!=null){
                                edited.put(download_link.getText()+"",uploadFile.getAbsolutePath());
                            }
                            ZipManager zipManager = new ZipManager();
                            App.makeDirectories();
                            final File sourceFile = new File(Configurations.sdTempFolderAddress + "temp-" + product.getId() + ".zip");
                            zipManager.zip(edited, sourceFile.getAbsolutePath());
                            product.setZipFile(sourceFile.getAbsolutePath());
                            HashMap<String, String> params = new HashMap<>();
                            params.put("id", product.getId() + "");
                            if(checkBox.isChecked()){
                                params.put("top", "yes");
                                product.setTop(true);
                            }
                            else{
                                params.put("top", "no");
                                product.setTop(false);

                            }
                            if(chk_download.isChecked()){
                                params.put("download", "true");
                                params.put("link",download_link.getText()+"");
                            }
                            else{
                                params.put("download", "false");
                                params.put("link","");
                            }
                            if(rdb_news.isChecked()){
                                params.put("type", "news");
                                params.put("price","");
                            }
                            else if(rdb_product.isChecked()){
                                params.put("type", "product");
                                params.put("price",price.getText()+"");
                            }

                            params.put("title", Util.getBase64String(product.getTitle()));
                            params.put("description", Util.getBase64String(product.getDescription()));
                            params.put("cat_id", product.getCat_id() + "");
                            if(product.isVideo()){
                                params.put("video","yes");
                                params.put("video_url",product.getVideo_url());
                                params.put("video_thumb",product.getVideoThumb());
                            }
                            else{
                                params.put("video","no");
                                params.put("video_url","");
                                params.put("video_thumb","");
                            }
                            Uploader uploader=new Uploader(sourceFile.getAbsolutePath(),params,Configurations.apiUrl+"saveProduct", new WebService.OnPostReceived() {
                                @Override
                                public void onReceived(String message) {
                                    App.showToast("عملیات با موفقیت انجام شد.");
                                    App.getDataFromServerAndSaveToDataBase(new WebService.OnGetDataCompleteListener() {
                                        @Override
                                        public void onGetDataCompleted(String info) {

                                        }

                                        @Override
                                        public void onGetDataError(String info) {

                                        }
                                    });
                                }

                                @Override
                                public void onReceivedError(String message) {
                                    App.showToast("عملیات به مشکل برخورد کرد.");
                                }
                            });
                            uploader.execute();

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
    private void setupVideo(final String url, final String thumb) {
        videoView=(VideoView)findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse(url));


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                setupVideoControlersViews();
                product.setVideo_url(url);
                product.setVideo(true);
                product.setVideoThumb(thumb);
            }
        });

    }

    private void setupVideoControlersViews() {
        final ImageView playButton = (ImageView) findViewById(R.id.button_play);
        playButton.setVisibility(View.VISIBLE);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    Picasso.with(App.context).load(R.drawable.ic_play).into(playButton);
                } else {
                    videoView.start();
                    Picasso.with(App.context).load(R.drawable.ic_pause).into(playButton);
                }
            }
        });


        TextView videoDurationTextView = (TextView) findViewById(R.id.text_video_duration);
        videoDurationTextView.setVisibility(View.VISIBLE);
        videoDurationTextView.setText(Util.formatDuration(videoView.getDuration()));

        videoCurrentDurationTextView = (TextView) findViewById(R.id.text_video_current_duration);
        videoCurrentDurationTextView.setText(Util.formatDuration(0));
        videoCurrentDurationTextView.setVisibility(View.VISIBLE);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setMax(videoView.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        videoCurrentDurationTextView.setText(Util.formatDuration(videoView.getCurrentPosition()));
                        seekBar.setProgress(videoView.getCurrentPosition());
                        seekBar.setSecondaryProgress((videoView.getBufferPercentage() * videoView.getDuration()) / 100);
                    }
                });
            }
        }, 0, 1000);
    }

    private void showChooseDialog() {
        final LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.choose_pic_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(ProductEditorActivity.this).create();

        dialog.setView(dialogView);
        dialog.setCancelable(true);
        CustomButton camera = (CustomButton) dialogView.findViewById(R.id.camera);
        CustomButton gallery = (CustomButton) dialogView.findViewById(R.id.sd);
        CustomButton aparat = (CustomButton) dialogView.findViewById(R.id.aparat);
        aparat.setVisibility(View.VISIBLE);
        final EditText aparat_url= (EditText) dialogView.findViewById(R.id.edt_aparat);
        aparat_url.setVisibility(View.VISIBLE);
        aparat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String > params=new HashMap<>();
                params.put("sh2",Configurations.sh2);
                params.put("url",aparat_url.getText()+"");
                App.webService.postRequest(params, "http://codetower.ir/api/Catalog/v1/" + "processVideo", new WebService.OnPostReceived() {
                    @Override
                    public void onReceived(final String message) {
                        String[] data=message.split(",");
                        video_layout.setVisibility(View.VISIBLE);
                        setupVideo(data[0],data[1]);
                    }

                    @Override
                    public void onReceivedError(String message) {
                        App.showToast("لینک ویدیو آپارات وارد شده اشتباه است.");
                    }
                });
                dialog.dismiss();

            }
        });
        camera.setText("دوربین");
        gallery.setText("گالری");
        aparat.setText("ویدیو آپارات");
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
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,   FileProvider.getUriForFile(ProductEditorActivity.this, BuildConfig.APPLICATION_ID + ".provider",photoFile));
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
                File temp = new File(Configurations.sdTempFolderAddress + product.getCat_id() + "/" + product.getId() + "/" + App.prefManager.getPreference("temp_image_which"));
                Util.writeToFile(photo, temp, 80, true);
                setBitmapToViewWithName(App.prefManager.getPreference("temp_image_which"), temp.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                File temp = new File(Configurations.sdTempFolderAddress + product.getCat_id() + "/" + product.getId() + "/" + App.prefManager.getPreference("temp_image_which"));
                Util.writeToFile(photo, temp, 80, true);
                setBitmapToViewWithName(App.prefManager.getPreference("temp_image_which"), temp.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == FILE_MANAGER_REQUEST_CODE && resultCode == RESULT_OK) {
            String file_uri = data.getExtras().getString("file_url");
            if(file_uri!=null) {
                uploadFile = new File(file_uri);
                download_link.setText(uploadFile.getName());
            }
        }
    }

    private void setBitmapToViewWithName(String name, String url) {
        switch (name) {
            case "primary":
                primary_image.setImageBitmap(Util.getResizedBitmap(photo));
                primary_image.setTag("edited");
                video_layout.setVisibility(View.GONE);
                product.setVideo(false);
                product.setVideo_url("");
                product.setImage(url);
                break;
            case "p1":
                p1.setImageBitmap(Util.getResizedBitmap(photo));
                p1.setTag("edited");
                product.setA_image1(url);
                break;
            case "p2":
                p2.setImageBitmap(Util.getResizedBitmap(photo));
                p2.setTag("edited");
                product.setA_image2(url);
                break;
            case "p3":
                p3.setImageBitmap(Util.getResizedBitmap(photo));
                p3.setTag("edited");
                product.setA_image3(url);
                break;
            case "p4":
                p4.setImageBitmap(Util.getResizedBitmap(photo));
                p4.setTag("edited");
                product.setA_image4(url);
                break;
            case "p5":
                p5.setImageBitmap(Util.getResizedBitmap(photo));
                p5.setTag("edited");
                product.setA_image5(url);
                break;
            case "p6":
                p6.setImageBitmap(Util.getResizedBitmap(photo));
                p6.setTag("edited");
                product.setA_image6(url);
                break;
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
            case R.id.p1:
                selectedName = "p1";
                showChooseDialog();
                break;
            case R.id.p2:
                selectedName = "p2";
                showChooseDialog();
                break;
            case R.id.p3:
                selectedName = "p3";
                showChooseDialog();
                break;
            case R.id.p4:
                selectedName = "p4";
                showChooseDialog();
                break;
            case R.id.p5:
                selectedName = "p5";
                showChooseDialog();
                break;
            case R.id.p6:
                selectedName = "p6";
                showChooseDialog();
                break;

        }
    }

    private void setPicasso(final String name, int id, final AppCompatImageView targetView) {
        String path = Configurations.sdTempFolderAddress + product.getCat_id() + "/" + product.getId();
        File temp = new File(path, "/" + name);
        if (temp.exists()) {

            Bitmap bitmap = BitmapFactory.decodeFile(temp.getAbsolutePath());


            targetView.setImageBitmap(bitmap);

        } else {
            ImageDownloader.downloadImage(Configurations.apiUrl + "getImage?id=" + product.getId() + "&name=" + name, new ImageDownloader.OnDownloadImageCompleteListener() {
                @Override
                public void OnDownloadComplete(Bitmap bitmap) {
                    targetView.setImageBitmap(bitmap);
                    File path = new File(Configurations.sdTempFolderAddress + product.getCat_id() + "/" + product.getId());
                    path.mkdirs();
                    File temp = new File(path, name);
                    try {
                        Util.writeToFile(bitmap, temp, 100, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnDownloadError() {
                    targetView.setImageResource(R.drawable.placeholder);
                }
            });

        }


    }
    private void setupRadio(){
        chk_download = (AppCompatCheckBox) findViewById(R.id.chk_download);
        rdb_news= (AppCompatRadioButton) findViewById(R.id.rdb_news);
        rdb_product= (AppCompatRadioButton) findViewById(R.id.rdb_product);
        download_link= (AppCompatTextView) findViewById(R.id.download_link);

        rdb_product.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rdb_news.setChecked(false);
                    price.setEnabled(true);
                }
            }
        });
        rdb_news.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rdb_product.setChecked(false);
                    price.setEnabled(false);

                }
            }
        });
        chk_download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    fileManager.setEnabled(true);
                }
                else{
                    fileManager.setEnabled(false);
                }
            }
        });
        if(id!=-1){
            if(product.isDownload()){
                chk_download.setChecked(true);
                fileManager.setEnabled(true);
                download_link.setText(product.getDownloadLink());
            }
            else{
                chk_download.setChecked(false);
                fileManager.setEnabled(false);
                download_link.setText("");
            }
            if(product.getType()==Product.TYPE_NEWS){
                chk_download.setChecked(false);
                rdb_product.setChecked(false);
                rdb_news.setChecked(true);
                price.setEnabled(false);
                price.setText("");

            }
            else if(product.getType()==Product.TYPE_PRODUCT){
                chk_download.setChecked(false);
                rdb_product.setChecked(true);
                price.setEnabled(true);
                rdb_news.setChecked(false);
                price.setText(product.getPrice()+"");
            }
        }


    }
    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
        super.onDestroy();
    }


}
