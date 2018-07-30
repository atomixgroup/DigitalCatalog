package ir.codetower.samanshiri.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ir.codetower.samanshiri.Adapters.MessageAdapter;
import ir.codetower.samanshiri.Adapters.ProductListAdapter;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.DeleteItemListener;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Message;
import ir.codetower.samanshiri.Models.Order;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.R;
import ru.rambler.libs.swipe_layout.SwipeLayout;

public class MessageActivity extends AppCompatActivity {
    private CustomButton send_notif;
    private File uploadFile;
    private AppCompatImageView selectedImage;
    private static final int FILEMANAGER_RESULT_CODE=3211;
    private static final int SELECT_PICTURE = 1889;
    private ArrayList<Product> products;
    private ProductListAdapter productListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        final View notif_dialog_view = LayoutInflater.from(this).inflate(R.layout.notif_dialog, null);
        final AlertDialog notif_dialog = new AlertDialog.Builder(MessageActivity.this).create();
        final RadioButton rdb_choose,rdb_no_event,rdb_only_app;
        final Spinner spinner= (Spinner) notif_dialog_view.findViewById(R.id.spinner);
        products=App.dbHelper.getProducts();
        if(products==null){
            App.showToast("پیغامی ثبت نشده است");
            finish();
        }
        else{
            String[] spinArray=new String[products.size()];
            for (int i=0;i<products.size();i++) {
                spinArray[i]=products.get(i).getTitle();
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item,spinArray );
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spinner.setAdapter(spinnerArrayAdapter);

            rdb_choose= (RadioButton) notif_dialog_view.findViewById(R.id.rdb_choose);
            rdb_choose.setChecked(true);
            rdb_no_event= (RadioButton) notif_dialog_view.findViewById(R.id.rdb_no_event);
            rdb_only_app= (RadioButton) notif_dialog_view.findViewById(R.id.rdb_only_app);
            rdb_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        rdb_no_event.setChecked(false);
                        rdb_only_app.setChecked(false);
                        spinner.setEnabled(true);
                    }
                }
            });
            rdb_no_event.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        rdb_choose.setChecked(false);
                        rdb_only_app.setChecked(false);
                        spinner.setEnabled(false);
                    }
                }
            });
            rdb_only_app.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        rdb_no_event.setChecked(false);
                        rdb_choose.setChecked(false);
                        spinner.setEnabled(false);
                    }
                }
            });



            final EditText edt_message= (EditText) notif_dialog_view.findViewById(R.id.edt_message);
            final EditText edt_title= (EditText) notif_dialog_view.findViewById(R.id.edt_title);
            CustomButton btn_browse= (CustomButton) notif_dialog_view.findViewById(R.id.btn_browse);
            btn_browse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);
                }
            });
            selectedImage= (AppCompatImageView) notif_dialog_view.findViewById(R.id.selected_image);
            CustomButton btn_send= (CustomButton) notif_dialog_view.findViewById(R.id.btn_send);
            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notif_dialog.dismiss();
                    HashMap<String,String > params=new HashMap<>();
                    params.put("message",edt_message.getText()+"");
                    params.put("title",edt_title.getText()+"");
                    params.put("username", App.prefManager.getPreference("username"));
                    params.put("password", App.prefManager.getPreference("password"));
                    params.put("sh2", Configurations.sh2);
                    params.put("sh1", App.prefManager.getPreference("sh1"));
                    if(rdb_choose.isChecked()){
                        params.put("type","product");
                        params.put("value",products.get((int) spinner.getSelectedItemId()).getId()+"");
                    }
                    else if(rdb_no_event.isChecked()){
                        params.put("type","no.event");
                        params.put("value","");
                    }
                    else if(rdb_only_app.isChecked()){
                        params.put("type","only.app");
                        params.put("value","");
                    }
                    if(uploadFile!=null && uploadFile.exists()){
                        params.put("file",Util.getStringFile(uploadFile));
                    }
                    App.webService.postRequest(params, Configurations.apiUrl + "sendNotif", new WebService.OnPostReceived() {
                        @Override
                        public void onReceived(String message) {
                            App.showToast("عملیات با موفقیت انجام شد.");
                        }

                        @Override
                        public void onReceivedError(String message) {
                            App.showToast("عملیات به مشکل بر خورد.");
                        }
                    });
                }
            });

            notif_dialog.setView(notif_dialog_view);
            send_notif= (CustomButton) findViewById(R.id.send_notif);
            send_notif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notif_dialog.show();
                }
            });




            HashMap<String,String> params=new HashMap<>();
            params.put("username", App.prefManager.getPreference("username"));
            params.put("password", App.prefManager.getPreference("password"));
            params.put("sh2", Configurations.sh2);
            params.put("sh1", App.prefManager.getPreference("sh1"));
            final RecyclerView recyclerView= (RecyclerView) findViewById(R.id.message_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            final ProgressBar loading= (ProgressBar) findViewById(R.id.loading);
            App.webService.postRequest(params, Configurations.apiUrl + "getMessages", new WebService.OnPostReceived() {
                @Override
                public void onReceived(final String message) {
                    loading.setVisibility(View.GONE);
                    ArrayList<Message> messages=Message.jsonArrayToMessage(message);
                    if(!messages.isEmpty()){
                        MessageAdapter adapter=new MessageAdapter(messages, new MessageAdapter.OnGetMessageDetailListener() {
                            @Override
                            public void onGet(Order order, final int message_id) {
                                final View dialogView = LayoutInflater.from(MessageActivity.this).inflate(R.layout.message_detail_dialog, null);
                                final CustomTextView amount,ref,status,postal,address,phone;
                                final RecyclerView product_list;
                                final Button dismiss,check;

                                product_list= (RecyclerView) dialogView.findViewById(R.id.product_list);
                                product_list.setLayoutManager(new LinearLayoutManager(MessageActivity.this));

                                amount= (CustomTextView) dialogView.findViewById(R.id.amount);
                                ref= (CustomTextView) dialogView.findViewById(R.id.ref);
                                postal= (CustomTextView) dialogView.findViewById(R.id.postal);
                                address= (CustomTextView) dialogView.findViewById(R.id.address);
                                phone= (CustomTextView) dialogView.findViewById(R.id.phone);
                                dismiss= (Button) dialogView.findViewById(R.id.btnDisMiss);
                                check= (Button) dialogView.findViewById(R.id.btnCheck);
                                final AlertDialog dialog = new AlertDialog.Builder(MessageActivity.this).create();
                                dialog.setView(dialogView);
                                dismiss.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                check.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        HashMap<String ,String > params=new HashMap<>();
                                        params.put("username", App.prefManager.getPreference("username"));
                                        params.put("password", App.prefManager.getPreference("password"));
                                        params.put("sh2", Configurations.sh2);
                                        params.put("sh1", App.prefManager.getPreference("sh1"));
                                        params.put("id",message_id+"");
                                        App.webService.postRequest(params, Configurations.apiUrl + "setStatusToChecked", new WebService.OnPostReceived() {
                                            @Override
                                            public void onReceived(String message) {
                                                //todo write this section and server side codes
                                                ValueAnimator anim = new ValueAnimator();
                                                anim.setIntValues(Color.parseColor("#95a5a6"), Color.parseColor("#8e44ad"));
                                                anim.setEvaluator(new ArgbEvaluator());
                                                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                    @Override
                                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                        check.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
                                                    }
                                                });
                                                anim.setDuration(300);
                                                anim.start();
                                            }

                                            @Override
                                            public void onReceivedError(String message) {

                                            }
                                        });
                                    }
                                });
                                if(order.getStatus().equals("check")){
                                    check.setText("بررسی شده");
                                    check.setBackgroundColor(Color.parseColor("#8e44ad"));
                                }
                                else {
                                    check.setText("بررسی نشده");
                                    check.setBackgroundColor(Color.parseColor("#95a5a6"));
                                }
                                amount.setText("مبلغ پرداخت شده:"+order.getAmount()+"");
                                ref.setText("کد رهگیری تراکنش: "+((order.getRef_id().equals("NULL"))?"ثبت نشده است":order.getRef_id()));
                                address.setText("آدرس گیرنده: "+order.getAddress());
                                postal.setText("کد پستی گیرنده: "+order.getPostal());
                                phone.setText("شماره تماس: "+order.getPhone());
                                final View confirmDialogView = LayoutInflater.from(App.context).inflate(R.layout.confirm_dialog, null);
                                final AlertDialog confirmDialog = new AlertDialog.Builder(MessageActivity.this).create();
                                final CustomButton btn_accept= (CustomButton) confirmDialogView.findViewById(R.id.btn_accept);
                                final CustomButton btn_decline= (CustomButton) confirmDialogView.findViewById(R.id.btn_decline);

                                dialog.setView(confirmDialogView);
                                productListAdapter = new ProductListAdapter(products, new DeleteItemListener() {
                                    @Override
                                    public void OnDelete(final int itemId, final int pos, final SwipeLayout swipeLayout) {
                                        confirmDialog.show();
                                        btn_decline.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                swipeLayout.animateReset();
                                                confirmDialog.dismiss();
                                            }
                                        });
                                        btn_accept.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                confirmDialog.dismiss();
                                                productListAdapter.deleteProductItem(itemId);
                                                productListAdapter.notifyItemRemoved(pos);
                                                HashMap<String,String> params=new HashMap<>();
                                                params.put("id", itemId+"");
                                                params.put("username", App.prefManager.getPreference("username"));
                                                params.put("password", App.prefManager.getPreference("password"));
                                                params.put("sh2", Configurations.sh2);
                                                params.put("sh1", App.prefManager.getPreference("sh1"));
                                                App.webService.postRequest(params, Configurations.apiUrl + "removeProduct", new WebService.OnPostReceived() {
                                                    @Override
                                                    public void onReceived(String message) {
                                                        if(message.equals("OK:1")){
                                                            App.showToast("عملیات با موفقیت انجام شد.");
                                                            App.dbHelper.delete(App.dbHelper.product_table,"id",itemId+"");
                                                        }
                                                        else{
                                                            App.showToast("عملیات با مشکل مواجه شد.");
                                                        }
                                                    }
                                                    @Override
                                                    public void onReceivedError(String message) {
                                                        App.showToast("عملیات با مشکل مواجه شد.");
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                                product_list.setAdapter(productListAdapter);
                                productListAdapter.notifyDataSetChanged();
                                dialog.show();

                            }
                        });
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    else{
                        App.showToast("در حال حاضر هیچ پیغامی ثبت نشده است");
                    }

                }

                @Override
                public void onReceivedError(String message) {

                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                selectedImage.setImageBitmap(photo);
                uploadFile = new File(Configurations.sdTempFolderAddress + "notif_image");
                Util.writeToFile(photo, uploadFile, 100, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
