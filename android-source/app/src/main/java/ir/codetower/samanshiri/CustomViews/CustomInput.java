package ir.codetower.samanshiri.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import ir.codetower.samanshiri.R;


public class CustomInput extends LinearLayout {
    public EditText editText;
    public CustomInput(Context context) {
        super(context);
        initialized(context,null);
    }

    public CustomInput(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialized(context,attrs);
    }

    public CustomInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialized(context,attrs);

    }
    private void initialized(Context context, AttributeSet attrib){
        View view= LayoutInflater.from(context).inflate(R.layout.custom_input,this,true);

        if (attrib != null)
        {
            TypedArray typedArray = context.obtainStyledAttributes(attrib, R.styleable.CustomInput, 0, 0);
            String labelText=typedArray.getString(R.styleable.CustomInput_inputLabelText);
            String type=typedArray.getString(R.styleable.CustomInput_inputEdtType);
            typedArray.recycle();
            CustomTextView label= (CustomTextView) view.findViewById(R.id.label);
            editText= (EditText) view.findViewById(R.id.editText);
            if(type.equals("password")){
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            else if(type.equals("phone")){
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
            }
            else{
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            label.setText(labelText);

        }
    }
    public String getText(){
        return editText.getText()+"";
    }
    public void setText(String text){
        editText.setText(text);
    }
}
