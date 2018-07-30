package ir.codetower.samanshiri.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ir.codetower.samanshiri.Adapters.CartAdapter;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.CartItem;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;

public class CartActivity extends AppCompatActivity {
    private CustomTextView cart_sum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        if(App.shoppingCart==null){
            App.showToast("هیچ محصول در سبد خرید موجود نیست");
            finish();
        }
        final LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.cart_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(CartActivity.this).create();
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

                Setting setting=App.prefManager.getSettings();


                App.prefManager.savePreferences(params);
                App.prefManager.savePreference("number",number.getText()+"");
                params.put("phone", number.getText() + "");
                params.put("code",setting.getTitle());
                params.put("sh2", Configurations.sh2);
                JSONArray jsonArray=new JSONArray();
                for (CartItem cartitem :
                        App.shoppingCart) {
                    JSONObject ob = new JSONObject();
                    try {
                        ob.put("title",cartitem.getTitle());
                        ob.put("id",cartitem.getpId());
                        ob.put("price",cartitem.getPrice());
                        ob.put("count",cartitem.getCount());
                        jsonArray.put(ob);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                params.put("carts",jsonArray.toString());
                App.webService.postRequest(params, Configurations.apiUrl + "saveCart", new WebService.OnPostReceived() {
                    @Override
                    public void onReceived(String message) {
                        String url = Configurations.apiUrl+"request?sh2="+Configurations.sh2+"&id="+message;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                    @Override
                    public void onReceivedError(String message) {
                        App.showToast("خطا در شبکه بوجود آمده لطفا بعدا امتحان فرمایید");

                    }
                });
            }
        });
         cart_sum= (CustomTextView) findViewById(R.id.cart_sum);
        CustomButton cart_finish= (CustomButton) findViewById(R.id.cart_finish);
        cart_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    name.setText(App.prefManager.getPreference2("name"));
                    address.setText(App.prefManager.getPreference2("address"));
                    postal_code.setText(App.prefManager.getPreference2("postal"));
                    number.setText(App.prefManager.getPreference2("number"));
                    dialog.show();
            }
        });
        CustomButton cart_clear= (CustomButton) findViewById(R.id.cart_clear);
        cart_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.dbHelper.clearCart();
                App.updateCart();
                finish();
            }
        });
        CartAdapter adapter=new CartAdapter(App.shoppingCart, new CartAdapter.OnIncDecListener() {
            @Override
            public void onChange() {
                double price=0d;
                if(App.shoppingCart!=null){
                    for (CartItem item:App.shoppingCart){
                        price+=(item.getPrice()*item.getCount());
                    }
                    cart_sum.setText(price+"");
                }

            }
        });
        RecyclerView cart_list= (RecyclerView) findViewById(R.id.cart_list);
        cart_list.setLayoutManager(new LinearLayoutManager(this));
        cart_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        double price=0d;
        if(App.shoppingCart!=null){
            for (CartItem item:App.shoppingCart){
                price+=(item.getPrice()*item.getCount());
            }
            cart_sum.setText(price+"");
        }
    }
}
