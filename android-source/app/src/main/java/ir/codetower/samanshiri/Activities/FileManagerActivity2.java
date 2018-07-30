package ir.codetower.samanshiri.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;

import ir.codetower.samanshiri.Adapters.AdapterFileManager;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Models.FileManagerItem;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;

public class FileManagerActivity2 extends AppCompatActivity implements AdapterFileManager.ListItemClickListener {
    ArrayList<FileManagerItem> startDialogList = new ArrayList<>();
    ArrayList<FileManagerItem> tempArray=new ArrayList<>();
    private FileManagerItem backItem=new FileManagerItem();
    private AdapterFileManager adapter;
    private int depth=0;
    private boolean finishFlag=true;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Setting.getPColor()));
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        FileManagerItem temp;
        String primary_sd = System.getenv("EXTERNAL_STORAGE");
        if (primary_sd != null) {
            try {
                if (new File(primary_sd).isDirectory()) {
                    temp = new FileManagerItem();
                    temp.setFileUrl(primary_sd);
                    temp.setFileName("Primary SD");
                    temp.setDirectory(true);
                    startDialogList.add(temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        String secondary_sd = System.getenv("SECONDARY_STORAGE");
        if (secondary_sd != null) {
            try {
                File tempFile=new File(secondary_sd);
                boolean thereFlag=false;
                if (tempFile.isDirectory()) {
                    for (File single:tempFile.listFiles()){
                        thereFlag=true;
                        break;
                    }
                    if(thereFlag){
                        temp = new FileManagerItem();
                        temp.setFileUrl(secondary_sd);
                        temp.setFileName("Secondary SD");
                        temp.setDirectory(true);
                        startDialogList.add(temp);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        tempArray.addAll(startDialogList);
        adapter = new AdapterFileManager(startDialogList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    public ArrayList<FileManagerItem> findFiles(File root) {
        FileManagerItem temp;
        ArrayList<FileManagerItem> allFiles = new ArrayList<>();
        File[] files = root.listFiles();
        temp=new FileManagerItem();
        temp.setFileName("../");
        temp.setFileUrl(root.getParent());
        temp.setDirectory(true);
        temp.setBack(true);
        backItem=temp;
        allFiles.add(temp);
        if(files!=null) {
            for (File singleFile : files) {
                if (singleFile.isHidden()) {
                    continue;
                }
                temp = new FileManagerItem();
                temp.setFileName(App.getExcerpt(singleFile.getName(), 12, 8));
                temp.setFileUrl(singleFile.getAbsolutePath());
                if (singleFile.isDirectory()) {
                    temp.setDirectory(true);
                } else {
                    temp.setDirectory(false);
                }
                allFiles.add(temp);
            }
        }
        return allFiles;
    }
    @Override
    public void listItemClick(FileManagerItem fileManagerItem) {
        processItems(fileManagerItem);
    }

    @Override
    public void onBackPressed() {
        if(finishFlag){
            finish();
        }
        processItems(backItem);

    }

    private void processItems(FileManagerItem fileManagerItem){
        if(fileManagerItem.isDirectory()){
            if(fileManagerItem.isBack()){
                depth--;
                if(depth<=0) {
                    adapter.swap(tempArray);
                    finishFlag=true;
                    depth=0;
                }
                else{
                    ArrayList<FileManagerItem> fileManagerItems = findFiles(new File(fileManagerItem.getFileUrl()));
                    adapter.swap(fileManagerItems);
                    recyclerView.scrollToPosition(0);
                }
            }
            else {
                ArrayList<FileManagerItem> fileManagerItems = findFiles(new File(fileManagerItem.getFileUrl()));
                adapter.swap(fileManagerItems);
                recyclerView.scrollToPosition(0);
                depth++;
                finishFlag=false;
            }
        }
        else{
            Intent returnIntent = new Intent();
            returnIntent.putExtra("file_url",fileManagerItem.getFileUrl());
            setResult(Activity.RESULT_OK,returnIntent);
            finish();

        }

    }

}
