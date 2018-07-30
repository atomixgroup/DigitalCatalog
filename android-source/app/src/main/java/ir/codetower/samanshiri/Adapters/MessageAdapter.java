package ir.codetower.samanshiri.Adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Message;
import ir.codetower.samanshiri.Models.Order;
import ir.codetower.samanshiri.R;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ArrayList<Message> messages;
    private OnGetMessageDetailListener onGetMessageDetailListener;

    public MessageAdapter(ArrayList<Message> messages, OnGetMessageDetailListener onGetMessageDetailListener) {
        this.messages = messages;
        this.onGetMessageDetailListener=onGetMessageDetailListener;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(App.context).inflate(R.layout.message_adapter, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private CustomButton btn_check,btn_detail;
        private CustomTextView item_message;

        public MessageViewHolder(View itemView) {
            super(itemView);
            btn_check= (CustomButton) itemView.findViewById(R.id.btn_check);
            btn_detail= (CustomButton) itemView.findViewById(R.id.btn_detail);
            btn_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String ,String > params=new HashMap<>();
                    params.put("username", App.prefManager.getPreference("username"));
                    params.put("password", App.prefManager.getPreference("password"));
                    params.put("sh2", Configurations.sh2);
                    params.put("sh1", App.prefManager.getPreference("sh1"));
                    params.put("id",messages.get(getLayoutPosition()).getId()+"");
                    App.webService.postRequest(params, Configurations.apiUrl + "getMessageDetail", new WebService.OnPostReceived() {
                        @Override
                        public void onReceived(String message) {
                            try {
                                onGetMessageDetailListener.onGet(Order.jsonArrayToOrderItem(message),messages.get(getLayoutPosition()).getId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                App.showToast("مشکل در دریافت اطلاعات از طرف سرور بوجود آمده است .");
                            }
                        }

                        @Override
                        public void onReceivedError(String message) {

                        }
                    });
                }
            });
            item_message= (CustomTextView) itemView.findViewById(R.id.item_message);
            btn_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int mes_id=messages.get(getLayoutPosition()).getId();
                    HashMap<String ,String > params=new HashMap<>();
                    params.put("username", App.prefManager.getPreference("username"));
                    params.put("password", App.prefManager.getPreference("password"));
                    params.put("sh2", Configurations.sh2);
                    params.put("sh1", App.prefManager.getPreference("sh1"));
                    params.put("id",mes_id+"");
                    App.webService.postRequest(params, Configurations.apiUrl + "setStatusToChecked", new WebService.OnPostReceived() {
                        @Override
                        public void onReceived(String message) {
                            Message temp=messages.get(getLayoutPosition());
                            temp.setStatus("checked");

                            messages.set(getLayoutPosition(),temp);
                            notifyItemChanged(getLayoutPosition());
                            //todo write this section and server side codes
                            ValueAnimator anim = new ValueAnimator();
                            anim.setIntValues(Color.parseColor("#95a5a6"), Color.parseColor("#8e44ad"));
                            anim.setEvaluator(new ArgbEvaluator());
                            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    btn_check.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
                                }
                            });

                            anim.setDuration(300);
                            anim.start();
                            btn_check.setText("بررسی شد");
                        }

                        @Override
                        public void onReceivedError(String message) {

                        }
                    });
                }
            });

        }

        public void bind(Message message) {
            item_message.setText(message.getText());
            if(message.getStatus().equals("checked")){
                ValueAnimator anim = new ValueAnimator();
                anim.setIntValues(Color.parseColor("#95a5a6"), Color.parseColor("#8e44ad"));
                anim.setEvaluator(new ArgbEvaluator());
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        btn_check.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
                    }
                });

                anim.setDuration(300);
                anim.start();
                btn_check.setText("بررسی شد");
                btn_check.setEnabled(false);
            }
            else{
                btn_check.setBackgroundColor(Color.parseColor("#95a5a6"));
                btn_check.setText("بررسی نشده");
                btn_check.setEnabled(true);

            }
        }


    }
    public interface OnGetMessageDetailListener{
        public void onGet(Order order, int message_id);
    }

}
