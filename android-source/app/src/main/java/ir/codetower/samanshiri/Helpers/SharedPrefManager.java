package ir.codetower.samanshiri.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Models.Setting;

public class SharedPrefManager {

    private SharedPreferences sharedPreferences;
    public SharedPrefManager(String sharedPrefName) {
        sharedPreferences= App.context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
    }
    public void savePreferences(HashMap<String,String> params){
        if(params!=null){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            for ( Map.Entry<String, String> entry : params.entrySet()) {
                editor.putString(entry.getKey(),entry.getValue());
            }
            editor.apply();
        }
    }
    public void savePreference(String key, String value){
        if(key!=null && value!=null){
            SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(key,value);
            editor.apply();
        }
    }
    public void removePreference(String key){
        if(key!=null){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }
    public HashMap<String,String> getPreferences(ArrayList<String> params){
            HashMap<String, String> temp = new HashMap<>();
            for (String entry : params) {
                temp.put(entry, sharedPreferences.getString(entry, null));
            }
            return temp;
    }
    public String getPreference(String param){
        return sharedPreferences.getString(param,"-1");
    }
    public String getPreference2(String param){
        return sharedPreferences.getString(param,"");
    }
    public boolean isSetPereference(){
        if((sharedPreferences.getString("initialized","false")).equals("false")){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean isAdmin() {
        if((sharedPreferences.getString("sh1","false")).equals("false")){
            return false;
        }
        else {
            return true;
        }

    }

    public void saveSettings(Setting setting) {
        savePreference("title",setting.getTitle());
        savePreference("description",setting.getDescription());
        savePreference("primary_color",setting.getPrimary_color());
        savePreference("second_color",setting.getSecond_color());
        savePreference("phone",setting.getPhone());
        savePreference("about",setting.getAbout());
        savePreference("instagram",setting.getInstagram());
        savePreference("telegram",setting.getTelegram());
        savePreference("linkedin",setting.getLinkedin());
        savePreference("is_cart",(setting.isCartSwitch())?"true":"false");
    }
    public Setting getSettings(){
        Setting setting=new Setting();
        setting.setTitle(sharedPreferences.getString("title","شرکت اتمیکس"));
        setting.setDescription(sharedPreferences.getString("description","طراحی و توسعه وب و اپلیکیشن"));
        setting.setPrimary_color(sharedPreferences.getString("primary_color","#000000"));
        setting.setPrimary_color(sharedPreferences.getString("primary_color","#000000"));
        setting.setSecond_color(sharedPreferences.getString("second_color","#000000"));
        setting.setPhone(sharedPreferences.getString("phone","09308144474"));
        setting.setAbout(sharedPreferences.getString("about","در حال حاضر توضیحاتی ثبت نشده است"));
        setting.setTelegram(sharedPreferences.getString("telegram",""));
        setting.setInstagram(sharedPreferences.getString("instagram",""));
        setting.setLinkedin(sharedPreferences.getString("linkedin",""));
        setting.setCartSwitch(sharedPreferences.getString("is_cart", "false").equals("true"));
        return setting;
    }
}
