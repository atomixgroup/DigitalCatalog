package ir.codetower.samanshiri.Models;

import java.util.ArrayList;

/**
 * Created by Mr-R00t on 7/31/2017.
 */

public class Slide {
    private int id;
    private String image;
    private String link;
    private String update_token;
    private String tabTitle;
    private String title;
    private String description;

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
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

    public String getUpdate_token() {
        return update_token;
    }

    public void setUpdate_token(String update_token) {
        this.update_token = update_token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public static ArrayList<Slide> getFakeSlides(){
        ArrayList<Slide> slides=new ArrayList<>();


        for (int i=1;i<=5;i++){
            Slide slide=new Slide();
            slide.setId(i);
            slides.add(slide);

        }
        return slides;
    }
}
