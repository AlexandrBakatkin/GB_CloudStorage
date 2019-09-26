package com.geerkbrains.netty.example.client;

import com.geerkbrains.netty.example.common.FileListMessage;
import com.geerkbrains.netty.example.common.FileMessage;
import com.geerkbrains.netty.example.common.FileRequest;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    public static ArrayList<String> arrayList;
    private MainController mainController;

    public ClientHandler(MainController mainController){
        this.mainController = mainController;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        if (msg instanceof FileMessage) {
            FileMessage fm = (FileMessage) msg;
            Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
        }
        if (msg instanceof String){
            String str = (String) msg;
            System.out.println(str);
        }
        if (msg instanceof FileListMessage){
            FileListMessage fileListMessage = (FileListMessage) msg;
            arrayList = fileListMessage.getFileList();
            mainController.setList(arrayList);
        }
    }
}