package com.geerkbrains.netty.example.common;

import java.util.ArrayList;

public class FileListMessage extends AbstractMessage {

    private ArrayList<String> fileList;

    public ArrayList<String> getFileList() {
        return fileList;
    }

    public FileListMessage(ArrayList<String> list){
        this.fileList = list;
    }
}