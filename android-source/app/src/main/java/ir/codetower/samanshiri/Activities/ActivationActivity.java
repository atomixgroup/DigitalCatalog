package ir.codetower.samanshiri.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.HashMap;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.R;

public class ActivationActivity extends AppCompatActivity {
    EditText code;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        code= (EditText) findViewById(R.id.code);
        progressBar= (ProgressBar) findViewById(R.id.progress);

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(code.getText().length()==4){
                    progressBar.setVisibility(View.VISIBLE);
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put("code",code.getText()+"");
                    params.put("sh2", Configurations.sh2);
                    params.put("token",App.prefManager.getPreference("token"));
                    App.webService.postRequest(params, Configurations.apiUrl + "activation", new WebService.OnPostReceived() {
                        @Override
                        public void onReceived(String message) {
                            if(!message.equals("ERROR:0")){
                                App.prefManager.savePreference("sh1",message);
                                finish();
                            }
                            else{
                                App.showToast("کد وارد شده اشتباه می باشد");
                            }
                            code.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onReceivedError(String message) {
                                App.showToast("مشکل در شبکه بوجود آمده است.");
                            code.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);


                        }
                    });
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(code.getWindowToken(), 0);
                    code.setEnabled(false);
                }
            }
        });
    }
}
