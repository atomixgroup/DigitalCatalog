package ir.codetower.samanshiri.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import ir.codetower.samanshiri.R;

/**
 * Created by Mr-R00t on 12/27/2017.
 */

public class CustomCircleButton extends RelativeLayout {
    AppCompatImageView circle,icon;
    public CustomCircleButton(Context context) {
        super(context);
        Initialize(context,null);
    }

    public CustomCircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initialize(context,attrs);
    }

    public CustomCircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initialize(context,attrs);
    }
    private void Initialize(Context context, AttributeSet attributeSet){
        View view= LayoutInflater.from(context).inflate(R.layout.custom_circle_button,this,true);
        circle=view.findViewById(R.id.circle);
        icon=view.findViewById(R.id.icon);
    }
    public void setIconImage(int id){
        icon.setImageResource(id);
    }
    public void setCircleColor(String colorCode){
        circle.setColorFilter(Color.parseColor(colorCode));
    }


}
