package ir.codetower.samanshiri.Activities;

import android.content.Intent;
import android.graphics.Color;
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
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ImageView primary = (ImageView) findViewById(R.id.primary);
        CustomTextView description = (CustomTextView) findViewById(R.id.description);
        Picasso.with(this).load(Configurations.apiUrl + "getAboutImage?sh2=" + Configurations.sh2).into(primary);
        Setting setting=App.prefManager.getSettings();
        description.setText(setting.getAbout());
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
        AppCompatImageView edit = (AppCompatImageView) findViewById(R.id.edit);
        edit.setColorFilter(Color.parseColor(setting.getSecond_color()));
        if(App.prefManager.isAdmin()){
            edit.setVisibility(View.VISIBLE);

        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this, AboutEditorActivity.class);
                startActivity(intent);
            }
        });

    }


}
