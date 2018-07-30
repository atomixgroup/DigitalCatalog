package ir.codetower.samanshiri.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;


public class AboutDesignerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_designer);
        ImageView primary = (ImageView) findViewById(R.id.primary);
        final CustomTextView description = (CustomTextView) findViewById(R.id.description);
        setPicasso(primary);
        Setting setting=App.prefManager.getSettings();
        App.webService.postRequest("about", "text", "http://www.codetower.ir/service/api/Catalog/v1/getAboutDesigner", new WebService.OnPostReceived() {
            @Override
            public void onReceived(String message) {
                description.setText(message);
            }

            @Override
            public void onReceivedError(String message) {

            }
        });

        CustomButton telegram,call;
        telegram= (CustomButton) findViewById(R.id.telegram);
        call= (CustomButton) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+989372289526"));
                startActivity(intent);
            }
        });
        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse("https://telegram.me/najvacou"));
                startActivity(telegram);
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(setting.getPrimary_color()));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(setting.getPrimary_color()));

        setSupportActionBar(toolbar);
        AppCompatImageView back = (AppCompatImageView) findViewById(R.id.back);
        back.setColorFilter(Color.parseColor(Setting.getSColor()));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setPicasso(final ImageView targetView) {
            Picasso.with(AboutDesignerActivity.this).load("http://www.codetower.ir/service/api/Catalog/v1/getAboutDesignerImage").placeholder(R.drawable.placeholder).into(targetView);
    }


}
