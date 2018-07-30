package ir.codetower.samanshiri.Models;

import ir.codetower.samanshiri.App;

/**
 * Created by Mr-R00t on 8/15/2017.
 */

public class Setting {
private String title;
private String description;

    private String primary_color;
    private String second_color;
    private String phone;
    private String about;
    private String instagram;
    private String telegram;
    private String linkedin;
    private boolean cartSwitch;

    public boolean isCartSwitch() {
        return cartSwitch;
    }

    public void setCartSwitch(boolean cartSwitch) {
        this.cartSwitch = cartSwitch;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrimary_color() {
        return primary_color;
    }

    public void setPrimary_color(String primary_color) {
        this.primary_color = primary_color;
    }

    public String getSecond_color() {
        return second_color;
    }

    public void setSecond_color(String second_color) {
        this.second_color = second_color;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static String getPColor(){
        Setting setting= App.prefManager.getSettings();
        return setting.primary_color;
    }
    public static String getSColor(){
        Setting setting= App.prefManager.getSettings();
        return setting.second_color;
    }


}
