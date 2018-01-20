package com.os.nodejs_socketio_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomsActivity extends AppCompatActivity {


    ListView lvUser, lvChat;
    EditText edtContent;
    ImageButton btnSend;

    ArrayList<String> arrUserRoom;
    ArrayAdapter adapterUserRoom;
    Rooms rooms;
    String usernameGlobal;

    ArrayList<String> arrChatMes;
    ArrayAdapter adapterMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        findbyid();


        Intent intent = getIntent();
        rooms = (Rooms) intent.getSerializableExtra("roomsobj");
        usernameGlobal = intent.getStringExtra("username");


        JSONObject object = new JSONObject();
        try {
            object.put("name", usernameGlobal);
            object.put("room", rooms.getNameroom());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        MainActivity.mSocket.emit("client-in-room-user", object.toString());


        arrUserRoom = new ArrayList<>();
        adapterUserRoom = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrUserRoom);
        lvUser.setAdapter(adapterUserRoom);

        MainActivity.mSocket.on("server-in-room-user", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonArray = (JSONArray) args[0];
                        arrUserRoom.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                String user = jsonArray.getString(i);
                                arrUserRoom.add(user + " Online");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapterUserRoom.notifyDataSetChanged();

                    }
                });
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = edtContent.getText().toString();
                JSONObject object = new JSONObject();
                try {
                    object.put("content", content);
                    object.put("user", usernameGlobal);
                    object.put("room", rooms.getNameroom());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MainActivity.mSocket.emit("client-chat", object.toString());


            }
        });

        arrChatMes = new ArrayList<>();

        adapterMes = new ArrayAdapter(RoomsActivity.this, android.R.layout.simple_list_item_1, arrChatMes);
        adapterMes.notifyDataSetChanged();
        lvChat.setAdapter(adapterMes);

        MainActivity.mSocket.on("server-res-chat", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject((String) args[0]);
                            String content = object.getString("content");
                            String user = object.getString("user");
                            String room = object.getString("room");

                            arrChatMes.add("User: " + user + "\n" + content);

                            adapterMes.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        MainActivity.mSocket.on("server-getmes-inroom", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray = new JSONArray(args[0]);

                            Log.d("arr", jsonArray.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        JSONObject object = new JSONObject();
        try {
            object.put("name", usernameGlobal);
            object.put("room", rooms.getNameroom());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MainActivity.mSocket.emit("client-leave-room", object.toString());

        super.onDestroy();
    }

    private void findbyid() {
        btnSend = (ImageButton) findViewById(R.id.imgbuttonSend);
        edtContent = (EditText) findViewById(R.id.editTextContent);
        lvUser = (ListView) findViewById(R.id.listviewUser);
        lvChat = (ListView) findViewById(R.id.listviewChat);
    }
}
