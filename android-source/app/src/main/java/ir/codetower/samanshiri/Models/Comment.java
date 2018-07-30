package ir.codetower.samanshiri.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mr-R00t on 12/5/2017.
 */

public class Comment {
    private int id;
    private String text;
    private String owner_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }
    public static ArrayList<Comment> jsonArrayToCommentItem(String text) throws JSONException {
        ArrayList<Comment> comments = new ArrayList<>();
        JSONArray array = null;
        try {
            array = new JSONArray(text);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonItem = array.getJSONObject(i);
                Comment comment = new Comment();

                comment.setId(jsonItem.getInt("id"));
                comment.setOwner_name(jsonItem.getString("owner_name"));
                comment.setText(jsonItem.getString("text"));

               comments.add(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return comments;
    }
}
