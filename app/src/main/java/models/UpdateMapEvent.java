package models;

import java.util.ArrayList;

/**
 * Created by haseeb on 25/12/16.
 */

public class UpdateMapEvent {
    public int position;

    public UpdateMapEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
