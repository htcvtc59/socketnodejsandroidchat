package com.os.nodejs_socketio_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by MacOS on 19/01/2018.
 */

public class RoomsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Rooms> roomsList;

    public RoomsAdapter(Context context, int layout, List<Rooms> roomsList) {
        this.context = context;
        this.layout = layout;
        this.roomsList = roomsList;
    }

    @Override
    public int getCount() {
        return roomsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        ImageView imgRoomDis;
        TextView txtNameRoom, txtidRoom;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder = new ViewHolder();
            holder.imgRoomDis = (ImageView) view.findViewById(R.id.imgRoomDis);
            holder.txtNameRoom = (TextView) view.findViewById(R.id.txtNameRoom);
            holder.txtidRoom = (TextView)view.findViewById(R.id.txtidRoom);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        Rooms rooms = roomsList.get(i);

        holder.imgRoomDis.setImageResource(rooms.getImageroom());
        holder.txtNameRoom.setText(rooms.getNameroom());
        holder.txtidRoom.setText(rooms.getIdroom());

        return view;
    }
}
