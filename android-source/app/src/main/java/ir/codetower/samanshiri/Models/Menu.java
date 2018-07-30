package ir.codetower.samanshiri.Models;

import java.util.ArrayList;

import ir.codetower.samanshiri.R;


public class Menu {
    private int id;
    private String title;
    private int icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
    public static ArrayList<Menu> getFakeMenus(){
        ArrayList<Menu> menus=new ArrayList<>();
        for (int i=0;i<4;i++){
            Menu menu=new Menu();
            menu.setId(i+1);
            menu.setTitle("تایتل"+" "+i);
            menu.setIcon(R.drawable.splash_bach);
            menus.add(menu);
        }
        return menus;
    }
}

