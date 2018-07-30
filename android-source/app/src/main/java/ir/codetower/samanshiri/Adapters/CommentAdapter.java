package ir.codetower.samanshiri.Adapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Comment;
import ir.codetower.samanshiri.R;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private ArrayList<Comment> comments;


    public CommentAdapter(ArrayList<Comment> comments) {
        this.comments = comments;

    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(App.context).inflate(R.layout.comments_adapter, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatImageView item_delete;
        private AppCompatTextView item_title, item_owner;

        public CommentViewHolder(View itemView) {
            super(itemView);
            if(App.prefManager.isAdmin()) {
                item_delete = (AppCompatImageView) itemView.findViewById(R.id.item_delete);
                item_delete.setOnClickListener(this);
                item_delete.setVisibility(View.VISIBLE);
            }
            item_title = (AppCompatTextView) itemView.findViewById(R.id.item_title);
            item_owner = (AppCompatTextView) itemView.findViewById(R.id.item_owner);


        }

        public void bind(Comment comment) {

            item_title.setText(comment.getText());
            item_owner.setText(comment.getOwner_name());
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Comment comment = comments.get(getLayoutPosition());
            switch (id) {
                case R.id.item_delete:
                    if(App.prefManager.isAdmin()) {

                        HashMap<String, String> params = new HashMap<>();
                        params.put("username", App.prefManager.getPreference("username"));
                        params.put("password", App.prefManager.getPreference("password"));
                        params.put("sh2", Configurations.sh2);
                        params.put("sh1", App.prefManager.getPreference("sh1"));
                        params.put("id", comment.getId() + "");

                        App.webService.postRequest(params, Configurations.apiUrl + "deleteComment", new WebService.OnPostReceived() {
                            @Override
                            public void onReceived(String message) {
                                App.showToast("عملیات با موفقیت انجام شد.");
                            }

                            @Override
                            public void onReceivedError(String message) {
                                App.showToast("عملیات به مشکل برخورد.");

                            }
                        });
                        comments.remove(getLayoutPosition());
                        notifyItemRemoved(getLayoutPosition());
                    }
                    break;
            }

        }
    }
}
