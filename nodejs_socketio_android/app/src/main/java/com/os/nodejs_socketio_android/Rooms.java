package com.os.nodejs_socketio_android;

import java.io.Serializable;

/**
 * Created by MacOS on 19/01/2018.
 */

public class Rooms implements Serializable{
   private String nameroom;
   private String idroom;
   private int imageroom;

    public Rooms() {
    }

    public Rooms(String nameroom, String idroom, int imageroom) {
        this.nameroom = nameroom;
        this.idroom = idroom;
        this.imageroom = imageroom;
    }

    public String getNameroom() {
        return nameroom;
    }

    public void setNameroom(String nameroom) {
        this.nameroom = nameroom;
    }

    public String getIdroom() {
        return idroom;
    }

    public void setIdroom(String idroom) {
        this.idroom = idroom;
    }

    public int getImageroom() {
        return imageroom;
    }

    public void setImageroom(int imageroom) {
        this.imageroom = imageroom;
    }
}
