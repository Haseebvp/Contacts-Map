package models;

import java.util.ArrayList;

/**
 * Created by haseeb on 23/12/16.
 */

public class MessageEvent {
    public final ArrayList<ContactModel> data;

    public MessageEvent(ArrayList<ContactModel> data) {
        this.data = data;
    }

    public ArrayList<ContactModel> getData() {
        return data;
    }
}
