var express = require('express');
var app = express();
var server = require('http').createServer(app);
var io = require('socket.io').listen(server);
var fs = require('fs');
server.listen(process.env.PORT || 3000);

app.get('/', (req, res) => {
    res.sendFile(__dirname + "/index.html");
});

var arrUser = [];
var arrRoom = [];

var userRoom = [];

io.sockets.on("connection", function (socket) {
    // console.log(socket.id);
    // console.log(socket.adapter.rooms);

    socket.on("client-register-user", (data) => {
        if (arrUser.indexOf(data) >= 0) {
            socket.emit("server-send-userfail");
        } else {
            arrUser.push(data);
            socket.username = data;
            socket.emit("server-send-loginsuccess", data);
            io.sockets.emit("server-send-arr-user", arrUser);
            socket.emit("server-send-roomsforuser", arrRoom);
        }
    });

    socket.on("client_create_rooms", (data) => {
        socket.join(data);
    });

    socket.on("client-in-room-user", (data) => {
        var objroom = {
            room: String,
            name: []
        }
        if (userRoom.findIndex(u => u.room === JSON.parse(data).room) >= 0) {
            var i = userRoom.findIndex(u => u.room === JSON.parse(data).room);
            if (userRoom.findIndex(u => u.name.findIndex(n => n !== JSON.parse(data).name))) {
                userRoom[i].name.push(JSON.parse(data).name);
            }
        } else {
            objroom.room = JSON.parse(data).room;
            objroom.name.push(JSON.parse(data).name);
            userRoom.push(objroom);
        }
        console.log(userRoom);
        var result = userRoom[userRoom.findIndex(u => u.room === JSON.parse(data).room)].name;
        io.sockets.in(JSON.parse(data).room).emit("server-in-room-user", result);

    });

    socket.on("client-leave-room", (data) => {
        var i = userRoom.findIndex(u => u.room === JSON.parse(data).room);
        if (userRoom.findIndex(u => u.name.findIndex(n => n === JSON.parse(data).name)) >= 0
            && userRoom.findIndex(r => r.room === JSON.parse(data).room >= 0)
        ) {
            userRoom[i].name.splice(JSON.parse(data).name, 1);
        }

        var result = userRoom[userRoom.findIndex(u => u.room === JSON.parse(data).room)].name;

        console.log(result);

        socket.broadcast.in(JSON.parse(data).room).emit("server-in-room-user", result);

        socket.leaveAll();
    });

    socket.on("client-chat", (data) => {
        io.sockets.in(JSON.parse(data).room).emit("server-res-chat", data);
    });



    socket.on("client_show_rooms", (data) => {
        arrRoom.push(data);
        socket.broadcast.emit("server-send-show-rooms", data);
    });




    socket.on('disconnect', () => {
        // console.log(socket.id);
        arrUser.splice(
            arrUser.indexOf(socket.username), 1
        );

        socket.broadcast.emit("server-send-arr-user", arrUser);
    });

});