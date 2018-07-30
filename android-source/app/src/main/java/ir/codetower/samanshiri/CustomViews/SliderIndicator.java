package ir.codetower.samanshiri.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by Mr-R00t on 7/31/2017.
 */

public class SliderIndicator extends android.support.v7.widget.AppCompatImageView {
    private Paint paint ,stroke,blank,full,shadow;
    private int count;
    private int radius;
    private int space;
    private int indicatorWidth;
    private int currentPage=0;
    public SliderIndicator(Context context) {
        super(context);
        initialize();
    }

    public SliderIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();

    }

    public SliderIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();

    }
    private void initialize(){
        stroke=new Paint();
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setAntiAlias(true);
        stroke.setColor(Color.parseColor("#e2e2e2"));
        stroke.setStrokeWidth(3);

        blank=new Paint();
        blank.setStyle(Paint.Style.FILL);
        blank.setAntiAlias(true);
        blank.setColor(Color.WHITE);


        full=new Paint();
        full.setStyle(Paint.Style.FILL);
        full.setAntiAlias(true);
        full.setColor(Color.parseColor("#e2e2e2"));

        shadow=new Paint();
        shadow.setStyle(Paint.Style.FILL);
        shadow.setAntiAlias(true);
        shadow.setColor(Color.parseColor("#33000000"));



    }
    public void setIndicatorCount(int count){
        this.count=count;
        invalidate();
    }
    public void setRadius(int radius){
        this.radius=radius;
    }
    public void setSpace(int space){
        this.space=space;
    }
    public void setCurrentPage(int current){
        currentPage=current;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        computeIndicatorWidth();

        float offsetX=(getWidth()-indicatorWidth)/2;

        for (int i=0;i<count;i++){
            paint=blank;
            if(currentPage==i){
                paint=full;
            }
            canvas.drawCircle(offsetX+i*(radius+space)+5,15,radius,shadow);
            canvas.drawCircle(offsetX+i*(radius+space),12,radius,paint);
            canvas.drawCircle(offsetX+i*(radius+space),12,radius+1,stroke);
        }

    }
    private void computeIndicatorWidth(){
        indicatorWidth=count*(space+radius);
    }
}
