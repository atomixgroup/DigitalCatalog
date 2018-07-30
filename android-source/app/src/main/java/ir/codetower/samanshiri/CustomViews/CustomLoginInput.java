package ir.codetower.samanshiri.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import ir.codetower.samanshiri.R;


public class CustomLoginInput extends LinearLayout {
    private EditText editText;
    public CustomLoginInput(Context context) {
        super(context);
        initialized(context,null);
    }

    public CustomLoginInput(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialized(context,attrs);
    }

    public CustomLoginInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialized(context,attrs);

    }
    private void initialized(Context context, AttributeSet attrib){
        View view= LayoutInflater.from(context).inflate(R.layout.custom_login_input,this,true);
        if (attrib != null)
        {
            TypedArray typedArray = context.obtainStyledAttributes(attrib, R.styleable.CustomLoginInput, 0, 0);
            int icon=typedArray.getResourceId(R.styleable.CustomLoginInput_iconDr,0);
            String hintText=typedArray.getString(R.styleable.CustomLoginInput_hintText);
            String type=typedArray.getString(R.styleable.CustomLoginInput_edtType);
            typedArray.recycle();
            AppCompatImageView iconImage= (AppCompatImageView) view.findViewById(R.id.icon);
            editText= (EditText) view.findViewById(R.id.editText);
            if(type.equals("password")){
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            else{
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            switch (icon){
                case 0:iconImage.setImageResource(R.drawable.ic_user_icon);
                    break;
                default:iconImage.setImageResource(icon);
            }
            editText.setHint(hintText);
        }
    }
    public String getText(){
        return editText.getText()+"";
    }
    public void setText(String text){
        editText.setText(text);
    }
}
