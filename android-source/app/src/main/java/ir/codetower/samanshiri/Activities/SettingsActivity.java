package ir.codetower.samanshiri.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;

import java.io.File;
import java.util.HashMap;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.CustomViews.CustomInput;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;


public class SettingsActivity extends AppCompatActivity
        implements View.OnClickListener {
    private AlertDialog dialog;
    private AlertDialog alertDialog;
    private int color_pick = 0;
    private CustomInput phone;
    private int primary_picked_color, second_picked_color;
    private ImageView selectedImageView, primary_color, second_color;
    private AppCompatImageView back, save;
    private File selectedPdfFile;
    private static final int FILE_MANAGER_REQUEST_CODE = 567;
    private CustomTextView filename;
    private Setting setting;
    private EditText title,description,instagram,telegram,linkedin;
    private Switch switch_cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Setting.getPColor()));
        }

        setting=App.prefManager.getSettings();
        back = (AppCompatImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        back.setColorFilter(Color.parseColor(Setting.getSColor()));
        save = (AppCompatImageView) findViewById(R.id.save);
        save.setColorFilter(Color.parseColor(Setting.getSColor()));

        save.setOnClickListener(this);
//        filename=(CustomTextView) findViewById(R.id.file_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(Setting.getPColor()));
        setSupportActionBar(toolbar);
        phone = (CustomInput) findViewById(R.id.phone);
        primary_color = (ImageView) findViewById(R.id.primary_color);
        second_color = (ImageView) findViewById(R.id.second_color);
//        Button btn_file_manager = (Button) findViewById(R.id.btn_file_manager);

        primary_color.setOnClickListener(this);
        second_color.setOnClickListener(this);
//        btn_file_manager.setOnClickListener(this);
        primary_color.setBackgroundColor(Color.parseColor(setting.getPrimary_color()));
        second_color.setBackgroundColor(Color.parseColor(setting.getSecond_color()));

        final LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.color_picker_dialog, null);
        dialog = new AlertDialog.Builder(SettingsActivity.this).create();
        dialog.setView(dialogView);
        final ImageView colorp = (ImageView) dialogView.findViewById(R.id.color);
        final HSLColorPicker colorPicker = (HSLColorPicker) dialogView.findViewById(R.id.color_picker);
        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(final int color) {
                // Do whatever you want with the color
                color_pick = color;
                colorp.setBackgroundColor(color);
            }
        });
        CustomButton choose_color = (CustomButton) dialogView.findViewById(R.id.choose_color);
        choose_color.setOnClickListener(this);

        phone.editText.setOnClickListener(this);
        phone.setText(setting.getPhone());
        primary_picked_color= Color.parseColor(setting.getPrimary_color());
        second_picked_color= Color.parseColor(setting.getSecond_color());

        title=(EditText) findViewById(R.id.title);
        description=(EditText) findViewById(R.id.description);

        title.setText(setting.getTitle());
        description.setText(setting.getDescription());

        instagram= (EditText) findViewById(R.id.instagram);
        telegram= (EditText) findViewById(R.id.telegram);
        linkedin= (EditText) findViewById(R.id.linkedin);
        instagram.setText(setting.getInstagram());
        telegram.setText(setting.getTelegram());
        linkedin.setText(setting.getLinkedin());
        switch_cart= (Switch) findViewById(R.id.switch_cart);
        switch_cart.setChecked(setting.isCartSwitch());

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.primary_color:
                selectedImageView = primary_color;
                selectedImageView.setTag("primary");

                dialog.show();
                break;
            case R.id.editText:
                phone.editText.setFocusableInTouchMode(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                break;
            case R.id.second_color:
                selectedImageView = second_color;
                selectedImageView.setTag("second");
                dialog.show();
                break;
            case R.id.btn_file_manager:
                Intent intent =new Intent(SettingsActivity.this,FileManagerActivity.class);
                startActivityForResult(intent,FILE_MANAGER_REQUEST_CODE);

                break;
            case R.id.choose_color:
                dialog.dismiss();
                selectedImageView.setBackgroundColor(color_pick);
                switch (selectedImageView.getTag()+""){
                    case "second":
                        second_picked_color=color_pick;
                        break;
                    case "primary":
                        primary_picked_color=color_pick;
                        break;
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.save:
                HashMap<String, String> params = new HashMap<>();
                params.put("title", title.getText()+"");
                params.put("description", description.getText()+"");
                params.put("primary_color", String.format("#%06X", (0xFFFFFF & primary_picked_color)));
                params.put("second_color", String.format("#%06X", (0xFFFFFF & second_picked_color)));
                params.put("phone", phone.getText() + "");
                params.put("instagram", instagram.getText() + "");
                params.put("telegram", telegram.getText() + "");
                params.put("linkedin", linkedin.getText() + "");
                params.put("is_cart", (switch_cart.isChecked())?"true":"false");
                if (selectedPdfFile != null) {
                    params.put("pdf", Util.getStringFile(selectedPdfFile));
                }
                Setting newSettings=new Setting();
                newSettings.setTitle(title.getText()+"");
                newSettings.setDescription(description.getText()+"");
                newSettings.setPhone(phone.getText()+"");
                newSettings.setPrimary_color(String.format("#%06X", (0xFFFFFF & primary_picked_color)));
                newSettings.setSecond_color(String.format("#%06X", (0xFFFFFF & second_picked_color)));
                newSettings.setInstagram(instagram.getText()+"");
                newSettings.setTelegram(telegram.getText()+"");
                newSettings.setLinkedin(linkedin.getText()+"");
                newSettings.setCartSwitch(switch_cart.isChecked());
                App.prefManager.saveSettings(newSettings);
                params.put("username", App.prefManager.getPreference("username"));
                params.put("password", App.prefManager.getPreference("password"));
                params.put("sh2", Configurations.sh2);
                params.put("sh1", App.prefManager.getPreference("sh1"));
                App.webService.postRequest(params, Configurations.apiUrl + "saveSettings", new WebService.OnPostReceived() {
                    @Override
                    public void onReceived(String message) {
                        App.showToast("عملیات با موفقیت انجام شد.");

                    }

                    @Override
                    public void onReceivedError(String message) {
                        App.showToast("عملیات با مشکل مواجه شد.");

                    }
                });
                finish();
                break;

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_MANAGER_REQUEST_CODE && resultCode == RESULT_OK) {
            String file_uri = data.getExtras().getString("file_url");
            if(file_uri!=null) {
                File targetFile = new File(file_uri);
                filename.setText(targetFile.getName());
                HashMap<String, String> params = new HashMap<>();
                params.put("username", App.prefManager.getPreference("username"));
                params.put("password", App.prefManager.getPreference("password"));
                params.put("sh2", Configurations.sh2);
                params.put("sh1", App.prefManager.getPreference("sh1"));
                params.put("pdf", Util.getStringFile(targetFile));
                App.webService.postRequest(params, Configurations.apiUrl + "savePdf", new WebService.OnPostReceived() {
                    @Override
                    public void onReceived(String message) {
                        App.showToast("عملیات با موفقیت انجام شد.");
                    }
                    @Override
                    public void onReceivedError(String message) {
                        App.showToast("عملیات با مشکل مواجه شد.");

                    }
                });
            }
        }
    }

}
