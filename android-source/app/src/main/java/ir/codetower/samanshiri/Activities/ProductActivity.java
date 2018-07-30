package ir.codetower.samanshiri.Activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ir.codetower.samanshiri.Adapters.AlbumAdapter;
import ir.codetower.samanshiri.Adapters.CommentAdapter;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.CustomViews.ShoppingCartView;
import ir.codetower.samanshiri.CustomViews.TouchImageView;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.CartItem;
import ir.codetower.samanshiri.Models.Comment;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView image;
    private CustomTextView head_name, body, name;
    private LinearLayout share;
    private AppCompatImageView back, edit, remove;
    private boolean backFlag = false;
    private AppBarLayout appBarLayout;
    private Product product;
    private CollapsingToolbarLayout collapsing_toolbar;
    private int id;
    private AlertDialog dialog;
    private LinearLayout touchLayout;
    private TouchImageView touchImageView;
    private VideoView videoView;
    private Timer timer;
    private TextView videoCurrentDurationTextView,price;
    private SeekBar seekBar;
    private ShoppingCartView btn_cart;
    private RelativeLayout video_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Setting.getPColor()));
        }




        video_layout= (RelativeLayout) findViewById(R.id.video_layout);
        image = (ImageView) findViewById(R.id.image);
        price= (TextView) findViewById(R.id.price);
        edit = (AppCompatImageView) findViewById(R.id.edit);
        edit.setColorFilter(Color.parseColor(Setting.getSColor()));
        remove = (AppCompatImageView) findViewById(R.id.remove);
        remove.setColorFilter(Color.parseColor(Setting.getSColor()));

        if(!getIntent().hasExtra("id")) finish();
        id = getIntent().getExtras().getInt("id");
        HashMap<String,String> params=new HashMap<>();
        params.put("token",App.prefManager.getPreference("token"));
        params.put("sh2",Configurations.sh2);
        params.put("p_id",id+"");
        App.webService.postRequest(params, Configurations.apiUrl + "saveSeen", new WebService.OnPostReceived() {
            @Override
            public void onReceived(String message) {

            }

            @Override
            public void onReceivedError(String message) {

            }
        });



        product = App.dbHelper.getProduct(id);
        if(product.isVideo()){
            video_layout.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            setupVideo(product.getVideo_url());
        }
        CustomTextView seenCount=findViewById(R.id.seenCount);
        seenCount.setText("آمار بازدید :"+product.getSeen());
        getAvrage();
        AppCompatImageView touchBack = (AppCompatImageView) findViewById(R.id.touchBack);
        touchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                animation.setDuration(500);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        touchLayout.clearAnimation();
                        touchLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                touchLayout.startAnimation(animation);
                backFlag = false;
            }
        });
        if (product == null) finish();

        if (App.prefManager.isAdmin()) {
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductActivity.this, ProductEditorActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });
            remove.setVisibility(View.VISIBLE);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View dialogView = LayoutInflater.from(ProductActivity.this).inflate(R.layout.confirm_dialog, null);
                    final AlertDialog dialog = new AlertDialog.Builder(ProductActivity.this).create();
                    final CustomButton btn_accept= (CustomButton) dialogView.findViewById(R.id.btn_accept);
                    CustomButton btn_decline= (CustomButton) dialogView.findViewById(R.id.btn_decline);
                    dialog.setView(dialogView);
                    dialog.show();
                    btn_decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    btn_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("id", product.getId() + "");
                                    params.put("username", App.prefManager.getPreference("username"));
                                    params.put("password", App.prefManager.getPreference("password"));
                                    params.put("sh2", Configurations.sh2);
                                    params.put("sh1", App.prefManager.getPreference("sh1"));
                                    App.webService.postRequest(params, Configurations.apiUrl + "removeProduct", new WebService.OnPostReceived() {
                                        @Override
                                        public void onReceived(String message) {
                                            if (message.equals("OK:1")) {
                                                App.showToast("عملیات با موفقیت انجام شد.");
                                                App.dbHelper.delete(App.dbHelper.product_table, "id", product.getId() + "");
                                            } else {
                                                App.showToast("عملیات با مشکل مواجه شد.");
                                            }
                                        }

                                        @Override
                                        public void onReceivedError(String message) {
                                            App.showToast("عملیات با مشکل مواجه شد.");

                                        }
                                    });
                                }
                            }).start();
                            dialog.dismiss();
                            finish();
                        }
                    });



                }
            });

        }

        setupViews();
        updateUI();

//        setupVideo();
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (collapsing_toolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsing_toolbar)) {
                    head_name.setVisibility(View.VISIBLE);
                } else {
                    head_name.setVisibility(View.INVISIBLE);
                }
            }
        });
        if(product.getType()==Product.TYPE_NEWS) {
            price.setVisibility(View.INVISIBLE);
        }
        else{
            price.setVisibility(View.VISIBLE);

            price.setText("قیمت : " + product.getPrice() + " ریال ");

        }

    }

    private void setupVideo(String url) {
        videoView=(VideoView)findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse(url));


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                video_layout.setLayoutParams(rlp);
                setupVideoControlersViews();
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



    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
        }
        super.onDestroy();
    }

    private void setupViews() {
        final FloatingActionButton favorite=findViewById(R.id.btn_favorite);

        if(product.isFavorite()){
            favorite.setColorFilter(Color.parseColor("#e74c3c"));
        }
        else{
            favorite.setColorFilter(Color.parseColor("#ecf0f1"));
        }
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product.setFavorite(!product.isFavorite());
                if(product.isFavorite()){
                    favorite.setColorFilter(Color.parseColor("#e74c3c"));
                }
                else{
                    favorite.setColorFilter(Color.parseColor("#ecf0f1"));

                }
                String fav=App.prefManager.getPreference("favorites");
                if(!fav.equals("-1")){
                    App.prefManager.savePreference("favorites",fav+"-"+product.getId());
                }
                else{
                    App.prefManager.savePreference("favorites",product.getId()+"");
                }
                App.dbHelper.update(product);

            }
        });
        RecyclerView albumList=findViewById(R.id.album_list);
        albumList.setLayoutManager(new LinearLayoutManager(ProductActivity.this, LinearLayoutManager.HORIZONTAL, false));
        AlbumAdapter adapter=new AlbumAdapter(product, new AlbumAdapter.ClickOnAlbumImageListener() {
            @Override
            public void onClick(File image) {
                touchEvent(image);
            }
        });
        albumList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btn_cart= (ShoppingCartView) findViewById(R.id.btn_cart);
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(App.context,CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        if(App.shoppingCart==null){
            btn_cart.setNumber(0);
        }
        else{
            int count=0;
            for (CartItem item : App.shoppingCart) {
                count+=item.getCount();
            }
            btn_cart.setNumber(count);
        }
        appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setContentScrimColor(Color.parseColor(Setting.getPColor()));
        touchLayout = (LinearLayout) findViewById(R.id.touchLayout);

        image.setOnClickListener(this);
//        Picasso.with(App.context).load(Configurations.apiUrl+"getImage/"+product.getA_image1()).into();
        back = (AppCompatImageView) findViewById(R.id.back);
        back.setColorFilter(Color.parseColor(Setting.getSColor()));

        share = (LinearLayout) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAddress();
            }
        });
        head_name = (CustomTextView) findViewById(R.id.head_name);
        head_name.setText(product.getTitle());
        name = (CustomTextView) findViewById(R.id.name);
        name.setText(product.getTitle());
        body = (CustomTextView) findViewById(R.id.body);
        body.setText(product.getDescription());
        touchImageView = (TouchImageView) findViewById(R.id.touchImage);
        touchImageView.setImageResource(R.drawable.placeholder);
        setPicasso("primary", image, false);



        final LayoutInflater factory = LayoutInflater.from(this);

        final View dialogView = factory.inflate(R.layout.cart_dialog, null);
        dialog = new AlertDialog.Builder(ProductActivity.this).create();
        dialog.setView(dialogView);
        final EditText name = (EditText) dialogView.findViewById(R.id.edtName);
        final EditText address = (EditText) dialogView.findViewById(R.id.edtAddress);
        final EditText postal_code = (EditText) dialogView.findViewById(R.id.postal_code);
        final EditText number = (EditText) dialogView.findViewById(R.id.edtNum);

        Button dismiss = (Button) dialogView.findViewById(R.id.btnDisMiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button send = (Button) dialogView.findViewById(R.id.btnSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                HashMap<String, String> params = new HashMap<>();
                params.put("name", name.getText() + "");
                params.put("address", address.getText() + "");
                params.put("postal", postal_code.getText() + "");
                params.put("number", number.getText() + "");
                App.prefManager.savePreferences(params);
                params.put("sh2", Configurations.sh2);
                params.put("cat_id", product.getCat_id() + "");
                params.put("id", product.getId() + "");

                App.webService.postRequest(params, Configurations.apiUrl + "saveOrder", new WebService.OnPostReceived() {
                    @Override
                    public void onReceived(String message) {
                        if (message.equals("OK:1")) {
                            App.showToast("بزودی یکی از کارشناسان ما با شما در ارتباط خواهد بود");
                        } else {
                            App.showToast("عملیات با شکست مواجه گردید");
                            Log.e("response", message);
                        }
                    }

                    @Override
                    public void onReceivedError(String message) {
                        App.showToast("خطا در شبکه بوجود آمده لطفا بعدا امتحان فرمایید");

                    }
                });
            }
        });
        CustomButton btn_order = (CustomButton) findViewById(R.id.btn_order);
        CustomButton btn_comments = (CustomButton) findViewById(R.id.btn_comments);

        final View commentsDialogView = factory.inflate(R.layout.comments_dialog, null);
        final AlertDialog commentsDialog = new AlertDialog.Builder(ProductActivity.this).create();
        final RecyclerView comments_list= (RecyclerView) commentsDialogView.findViewById(R.id.comments_list);
        final EditText comment_owner= (EditText) commentsDialogView.findViewById(R.id.comment_owner);
        final EditText comment_text= (EditText) commentsDialogView.findViewById(R.id.comment_text);
        Button btnSend= (Button) commentsDialogView.findViewById(R.id.btnSend);
        final RelativeLayout load_view= (RelativeLayout) commentsDialogView.findViewById(R.id.loading);
        final CustomTextView no_comments= (CustomTextView) commentsDialogView.findViewById(R.id.no_comments);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentOwner=comment_owner.getText()+"";
                String commentText=comment_text.getText()+"";
                if(!commentOwner.equals("") && !commentText.equals("")){
                    HashMap<String,String > params=new HashMap<>();
                    params.put("sh2", Configurations.sh2);
                    params.put("id", product.getId()+"");
                    params.put("text",commentText);
                    params.put("owner_name",commentOwner);
                    commentsDialog.dismiss();

                    App.webService.postRequest(params, Configurations.apiUrl + "saveComment", new WebService.OnPostReceived() {
                        @Override
                        public void onReceived(String message) {
                            App.showToast("عملیات با موفقیت انجام شد.");
                            comment_owner.setText("");
                            comment_text.setText("");
                        }

                        @Override
                        public void onReceivedError(String message) {
                            App.showToast("عملیات به مشکل برخورد.");


                        }
                    });
                }
                else{
                    App.showToast("فیلدها را با دقت پر کنید.");
                }
            }
        });
        Button btnDisMiss= (Button) commentsDialogView.findViewById(R.id.btnDisMiss);
        btnDisMiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentsDialog.dismiss();
            }
        });

        comments_list.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        commentsDialog.setView(commentsDialogView);

        btn_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentsDialog.show();
                HashMap<String,String > params=new HashMap<>();
                params.put("sh2", Configurations.sh2);
                params.put("id", product.getId()+"");
                App.webService.postRequest(params, Configurations.apiUrl + "getComments", new WebService.OnPostReceived() {
                    @Override
                    public void onReceived(String message) {
                        if(message.equals("no.record")){
                            comments_list.setVisibility(View.GONE);
                            no_comments.setVisibility(View.VISIBLE);
                        }
                        else{
                            try {
                                CommentAdapter adapter=new CommentAdapter(Comment.jsonArrayToCommentItem(message));
                                comments_list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                comments_list.setVisibility(View.VISIBLE);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                comments_list.setVisibility(View.GONE);
                                no_comments.setVisibility(View.VISIBLE);
                            }
                        }
                        load_view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onReceivedError(String message) {
                        load_view.setVisibility(View.GONE);
                    }
                });
            }
        });

        if (product.getType() == Product.TYPE_PRODUCT ) {
            btn_order.setVisibility(View.VISIBLE);
            btn_order.setText("اضافه کردن به سبد خرید");

            btn_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    product_name.setText("نام محصول : " + product.getTitle());
//                    name.setText(App.prefManager.getPreference2("name"));
//                    address.setText(App.prefManager.getPreference2("address"));
//                    postal_code.setText(App.prefManager.getPreference2("postal"));
//                    number.setText(App.prefManager.getPreference2("number"));
//                    dialog.show();
                    CartItem cartItem=new CartItem();
                    cartItem.setPrice(product.getPrice());
                    cartItem.setCount(1);
                    cartItem.setpId(product.getId());
                    cartItem.setTitle(product.getTitle());
                    App.dbHelper.insert(cartItem);
                    App.updateCart();
                    int count=0;
                    for (CartItem item :
                            App.shoppingCart) {
                        count+=item.getCount();
                    }
                    btn_cart.setNumber(count);
                }
            });
        }
        else if(product.getType() == Product.TYPE_NEWS && product.isDownload()) {
            btn_order.setVisibility(View.VISIBLE);
            btn_order.setText("دانلود");
            btn_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(Configurations.apiUrl + "getFile?sh2=" + Configurations.sh2+"&id="+product.getId());
                    DownloadManager mgr = (DownloadManager) App.context.getSystemService(DOWNLOAD_SERVICE);
                    try{
                        mgr.enqueue(new DownloadManager.Request(uri)
                                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                        DownloadManager.Request.NETWORK_MOBILE)
                                .setAllowedOverRoaming(false)
                                .setTitle(product.getTitle())
                                .setDescription(product.getTitle())
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                        product.getDownloadLink()));
                        registerReceiver(onComplete,
                                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(uri);
                        startActivity(i);
                    }


                }
            });
        }
        else{
            btn_order.setVisibility(View.GONE);
        }

        final SmileRating smileRating = (SmileRating) findViewById(R.id.smile_rating);
        smileRating.setSelectedSmile(BaseRating.GOOD);

        CustomButton btn_rate = (CustomButton) findViewById(R.id.btn_rate);
        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                int rate = smileRating.getRating();
                params.put("sh2", Configurations.sh2);
                params.put("IMEI", App.IMEI + "");
                params.put("rate", rate + "");
                params.put("id", product.getId() + "");

                App.webService.postRequest(params, Configurations.apiUrl + "saveRate", new WebService.OnPostReceived() {
                    @Override
                    public void onReceived(String message) {
                        App.showToast("امتیاز با موفقیت ثبت شد");
                    }

                    @Override
                    public void onReceivedError(String message) {
                        App.showToast("مشکل در شبکه بوجود آمده است");
                    }
                });
            }
        });
    }


    private void updateUI() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void shareAddress() {
        String shareBody = "http://www.arabiacademy.ir/dl/program.apk";
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        Setting setting=App.prefManager.getSettings();
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "دانلود اپلیکیشن"+" "+setting.getTitle()+" "+" از لینک زیر امکانپذیر می باشد:");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "ارسال به دوستان جهت دانلود"));
    }

    @Override
    public void onBackPressed() {
        if (backFlag) {
            AlphaAnimation animation = new AlphaAnimation(1, 0);
            animation.setDuration(500);

            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    touchLayout.clearAnimation();
                    touchLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            touchLayout.startAnimation(animation);
            backFlag = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String name = "";
        switch (id) {
            case R.id.image:
                String path = Configurations.sdTempFolderAddress + product.getCat_id()+"-"+product.getCat_update_token() + "/" + product.getId()+"-"+product.getUpdate_token();
                File temp = new File(path, "/primary");
                touchEvent(temp);
                break;
        }





    }
    private void touchEvent(File imageFile){
        Picasso.with(this).load(imageFile).into(touchImageView);
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(200);
        animation.setFillAfter(true);
        touchLayout.setVisibility(View.VISIBLE);
        touchLayout.startAnimation(animation);
        backFlag = true;
    }
    private void setPicasso(final String name, final ImageView targetView, final boolean album) {
        String path = Configurations.sdTempFolderAddress + product.getCat_id() + "/" + product.getId();
        File temp = new File(path, "/" + name);
        final SpinKitView spin = (SpinKitView) findViewById(App.getResourceId(name + "_spin"));
        spin.setColor(Color.parseColor(Setting.getPColor()));
        if (temp.exists()) {
//            if (album) {
            Bitmap bitmap = BitmapFactory.decodeFile(temp.getAbsolutePath());
            spin.setVisibility(View.GONE);

            targetView.setImageBitmap(bitmap);

//                Picasso.with(App.context).load(temp).placeholder(R.drawable.placeholder).memoryPolicy(MemoryPolicy.NO_CACHE).resize(100, 150).into(targetView);
//            }else {
//                Picasso.with(App.context).load(temp).placeholder(R.drawable.placeholder).memoryPolicy(MemoryPolicy.NO_CACHE).into(targetView);
//            }
        } else {
            ImageDownloader.downloadImage(Configurations.apiUrl + "getImage?id=" + product.getId() + "&name=" + name, new ImageDownloader.OnDownloadImageCompleteListener() {
                @Override
                public void OnDownloadComplete(Bitmap bitmap) {
//                        if(album){
//                            targetView.setImageBitmap(Util.resizeBitmap(bitmap,100));
//                        }
//                        else{
                    targetView.setImageBitmap(bitmap);

                    spin.setVisibility(View.GONE);
//                        }
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
                    spin.setVisibility(View.GONE);
                    targetView.setImageResource(R.drawable.placeholder);

                }
            });

        }

    }
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "دانلود با موقیت به پایان رسید.در صورت باز نشدن خودکار مکان دانلود شما میتوانید در پوشه دانلود پیدا کنید.", Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
            startActivity(i);
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if(App.shoppingCart==null){
            btn_cart.setNumber(0);
        }
        else{
            int count=0;
            for (CartItem item : App.shoppingCart) {
                count+=item.getCount();
            }
            btn_cart.setNumber(count);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                    App.getXy().y,
                    App.getXy().x);
            video_layout.setLayoutParams(rlp);


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            video_layout.setLayoutParams(rlp);
        }
    }
    private void getAvrage(){
        final CustomTextView average = (CustomTextView) findViewById(R.id.average);
        HashMap<String, String> params = new HashMap<>();
        params.put("sh2", Configurations.sh2);
        params.put("id", product.getId() + "");
        App.webService.postRequest(params, Configurations.apiUrl + "getAverage", new WebService.OnPostReceived() {
            @Override
            public void onReceived(String message) {
                average.setText("میانگین امتیازات : " + message);
            }

            @Override
            public void onReceivedError(String message) {

            }
        });
    }
}
