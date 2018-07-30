package ir.codetower.samanshiri.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mr-R00t on 11/23/2017.
 */

public class Message {
    private int id;
    private String type;
    private String text;
    private String status;
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public static ArrayList<Message> jsonArrayToMessage(String json) {
        ArrayList<Message> messages = new ArrayList<>();
        JSONArray array = null;
        try {
            array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonItem = array.getJSONObject(i);
                Message message = new Message();
                message.setId(jsonItem.getInt("id"));
                message.setText(jsonItem.getString("message"));
                message.setStatus(jsonItem.getString("status"));
                message.setType(jsonItem.getString("type"));
                message.setValue(jsonItem.getString("value"));
                messages.add(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
