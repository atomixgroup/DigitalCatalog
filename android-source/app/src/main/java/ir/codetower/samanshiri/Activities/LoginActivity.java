package ir.codetower.samanshiri.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.HashMap;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomLoginInput;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;

public class LoginActivity extends AppCompatActivity {
    private CustomLoginInput username,password;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Setting.getPColor()));
        }
        username= (CustomLoginInput) findViewById(R.id.username);
        password= (CustomLoginInput) findViewById(R.id.password);
        login= (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> params=new HashMap<>();
                params.put("username",username.getText());
                params.put("password",password.getText());
                params.put("sh2", Configurations.sh2);
                App.webService.postRequest(params, Configurations.apiUrl + "login", new WebService.OnPostReceived() {
                    @Override
                    public void onReceived(String message) {
                       if(message.equals("OK:1")){
                           Intent intent=new Intent(LoginActivity.this,ActivationActivity.class);
                           startActivity(intent);
                           App.prefManager.savePreference("username",username.getText()+"");
                           App.prefManager.savePreference("password",password.getText()+"");
                           finish();

                       }
                    }
                    @Override
                    public void onReceivedError(String message) {

                    }
                });
            }
        });


    }


}
