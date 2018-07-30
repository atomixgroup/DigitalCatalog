package ir.codetower.samanshiri.CustomViews;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import ir.codetower.samanshiri.R;


public class ShoppingCartView extends RelativeLayout {
    private AppCompatTextView cart_notif_number;
    private RelativeLayout cart_notif_root;
    public ShoppingCartView(Context context) {
        super(context);
        Initialize(context,null);
    }

    public ShoppingCartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initialize(context,attrs);
    }

    public ShoppingCartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initialize(context,attrs);
    }
    private void Initialize(Context context, AttributeSet attributeSet){
        View view= LayoutInflater.from(context).inflate(R.layout.shoping_cart_view,this,true);
         cart_notif_root= (RelativeLayout) view.findViewById(R.id.cart_notif);
         cart_notif_number= (AppCompatTextView) view.findViewById(R.id.cart_notif_number);
    }
    public void setNumber(int number){
        cart_notif_number.setText(number+"");
    }
    public void setNotifVisibility(boolean visibility){
        cart_notif_root.setVisibility((visibility)?VISIBLE:INVISIBLE);
    }

}
