package com.os.nodejs_socketio_android;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
import java.util.Collections;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    ListView list_item_rooms, list_item_user_online;
    ArrayList<Rooms> roomsArr;
    RoomsAdapter roomsAdapter;
    public static ArrayList<String> arrBird;

    ImageButton main_btn_add_room, btnAdd;
    EditText edtUserName;

    public static Socket mSocket;

    String usernameGlobal;

    ArrayList<String> arrUser;
    ArrayAdapter adapterUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findbyid();

        try {
            mSocket = IO.socket("http://192.168.1.129:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtUserName.getText().toString().trim().length() > 0) {
                    mSocket.emit("client-register-user", edtUserName.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Please input user", Toast.LENGTH_SHORT).show();
                }

            }
        });


        mSocket.on("server-send-userfail", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Username exists", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSocket.on("server-send-loginsuccess", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usernameGlobal = (String) args[0];
                        main_btn_add_room.setVisibility(View.VISIBLE);
                        btnAdd.setVisibility(View.INVISIBLE);
                        edtUserName.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });


        arrUser = new ArrayList<>();
        adapterUser = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrUser);
        list_item_user_online.setAdapter(adapterUser);

        mSocket.on("server-send-arr-user", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonArray = (JSONArray) args[0];
                        arrUser.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                String user = jsonArray.getString(i);
                                arrUser.add(user + " Online");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapterUser.notifyDataSetChanged();
                    }
                });
            }
        });

        roomsArr = new ArrayList<>();
        roomsAdapter = new RoomsAdapter(MainActivity.this, R.layout.per_rooms, roomsArr);
        list_item_rooms.setAdapter(roomsAdapter);

        mSocket.on("server-send-roomsforuser", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonArray = (JSONArray) args[0];
                        roomsArr.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject object = new JSONObject(jsonArray.getString(i));
                                String name = object.getString("name");
                                String user = object.getString("user");
                                int img = object.getInt("img");

                                roomsArr.add(new Rooms(name,user,img));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        roomsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        mSocket.on("server-send-show-rooms", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject((String) args[0]);

                            roomsArr.add(new Rooms(object.getString("name")
                                    , object.getString("user")
                                    , object.getInt("img")));


                            roomsAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });



        String[] nameBird = getResources().getStringArray(R.array.list_name_birds);
        arrBird = new ArrayList<>(Arrays.asList(nameBird));

        main_btn_add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.create_rooms);
                dialog.setCanceledOnTouchOutside(false);

                final EditText edtname = (EditText) dialog.findViewById(R.id.edt_room_name);
                Button btncreate = (Button) dialog.findViewById(R.id.btn_room_create_name);
                Button btncancel = (Button) dialog.findViewById(R.id.btn_room_cancel_name);

                btncreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.shuffle(arrBird);
                        Random random = new Random();
                        int min = 1;
                        int max = 8;
                        int i = random.nextInt(max - min + 1) + min;
                        int imgBird = getResources().getIdentifier(arrBird.get(i), "mipmap", getPackageName());

                        String nameroom = edtname.getText().toString();


                        JSONObject object = new JSONObject();
                        try {
                            object.put("name", nameroom);
                            object.put("img", imgBird);
                            object.put("user", usernameGlobal);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mSocket.emit("client_show_rooms", object.toString());

                        roomsArr.add(new Rooms(nameroom, usernameGlobal, imgBird));
                        roomsAdapter.notifyDataSetChanged();

                        dialog.dismiss();
                    }
                });


                btncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        dialog.dismiss();
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });


        list_item_rooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSocket.emit("client_create_rooms", roomsArr.get(i).getNameroom());

                Intent intent = new Intent(MainActivity.this, RoomsActivity.class);
                intent.putExtra("roomsobj", roomsArr.get(i));
                intent.putExtra("username", usernameGlobal);
                startActivity(intent);

            }
        });

    }

    private void findbyid() {
        list_item_rooms = (ListView) findViewById(R.id.list_item_rooms);
        list_item_user_online = (ListView) findViewById(R.id.list_item_user_online);
        main_btn_add_room = (ImageButton) findViewById(R.id.main_btn_add_room);
        btnAdd = (ImageButton) findViewById(R.id.imgbuttonAdd);
        edtUserName = (EditText) findViewById(R.id.editTextUserName);
    }
}
