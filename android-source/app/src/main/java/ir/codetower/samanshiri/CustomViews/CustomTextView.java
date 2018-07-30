package ir.codetower.samanshiri.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.R;


public class CustomTextView
  extends AppCompatTextView
{
  private Context context;
  
  public CustomTextView(Context paramContext)
  {
    super(paramContext);
    this.context = paramContext;
    if (!isInEditMode()) {
      initialize(null);
    }
  }
  
  public CustomTextView(Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.context = paramContext;
    if (!isInEditMode()) {
      initialize(paramAttributeSet);
    }
  }
  
  public CustomTextView(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.context = paramContext;
    if (!isInEditMode()) {
      initialize(paramAttributeSet);
    }
  }
  
  private void initialize(AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet != null)
    {
      TypedArray typedArray = this.context.obtainStyledAttributes(paramAttributeSet, R.styleable.CustomTextView, 0, 0);
      setTypeface(App.getFontWithName(typedArray.getString(0)));
      typedArray.recycle();
    }
  }
}


/* Location:              C:\Users\Mr R00t\Desktop\dec-dex2jar.jar!\biz\ejahan\lifestories\Views\Controls\CustomTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */