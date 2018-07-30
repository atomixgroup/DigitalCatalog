package ir.codetower.samanshiri;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

/**
 * Created by Mr-R00t on 9/13/2017.
 */

public class AnimThread extends Thread {
    private int time;
    private boolean animCancel=false;
    private int sliderCurrentPage=0;
    private int slideSize=0;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    @Override
    public void run() {
        while (true) {
            if(animCancel) break;
            try {
                Thread.sleep(this.time);
                int nextPage = viewPager.getCurrentItem() + 1;
                if (nextPage >= slideSize) {
                    nextPage=0;
                }
                final int finalNextPage = nextPage;
                App.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TabLayout.Tab tab = tabLayout.getTabAt(finalNextPage);
                        tab.select();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void cancel(){
        animCancel=true;
    }
    public void setSlideDelayTime(int time){
        this.time=time;
    }
    public void setSlideSize(int size){
        slideSize=size;
    }
    public void setViewPager(ViewPager viewPager, TabLayout tabLayout){
        this.viewPager=viewPager;
        this.tabLayout=tabLayout;
    }
}
