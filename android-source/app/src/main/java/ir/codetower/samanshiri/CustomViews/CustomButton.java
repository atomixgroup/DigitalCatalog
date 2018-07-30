package ir.codetower.samanshiri.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.R;


public class CustomButton
  extends AppCompatButton
{
  private Context context;
  
  public CustomButton(Context paramContext)
  {
    super(paramContext);
    this.context = paramContext;
    if (!isInEditMode()) {
      initialize(null);
    }
  }
  
  public CustomButton(Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.context = paramContext;
    if (!isInEditMode()) {
      initialize(paramAttributeSet);
    }
  }
  
  public CustomButton(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt)
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
      TypedArray typedArray = this.context.obtainStyledAttributes(paramAttributeSet, R.styleable.CustomButton, 0, 0);
      setTypeface(App.getFontWithName(typedArray.getString(0)));
      typedArray.recycle();
    }
  }


}

