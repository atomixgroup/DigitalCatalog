package ir.codetower.samanshiri.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Models.FileManagerItem;
import ir.codetower.samanshiri.R;


public class AdapterFileManager extends RecyclerView.Adapter<AdapterFileManager.ContactViewHolder> {
    ListItemClickListener listItemClickListener;
    public void swap(ArrayList<FileManagerItem> files){
        this.files.clear();
        this.files.addAll(files);
        notifyDataSetChanged();
    }
    private List<FileManagerItem> files;
    public AdapterFileManager(List<FileManagerItem> files, ListItemClickListener listItemClickListener){
        this.listItemClickListener=listItemClickListener;
        this.files = files;
    }
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(App.context).inflate(R.layout.filemanager_layout_adapter,parent,false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        FileManagerItem file=files.get(position);
        holder.name.setText(file.getFileName());
        if(file.isDirectory()){
            if(file.isBack()){
                holder.image.setImageResource(R.drawable.up);
            }
            else{
                holder.image.setImageResource(R.drawable.folder);
            }
        }
        else{
            holder.image.setImageResource(R.drawable.document);
        }

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;


        public ContactViewHolder(View itemView) {
            super(itemView);

            image= (ImageView) itemView.findViewById(R.id.item_image);
            name= (TextView) itemView.findViewById(R.id.item_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listItemClickListener.listItemClick(files.get(getLayoutPosition()));

                }
            });
        }


    }
    public interface ListItemClickListener{
        public void listItemClick(FileManagerItem fileManagerItem);
    }

}
