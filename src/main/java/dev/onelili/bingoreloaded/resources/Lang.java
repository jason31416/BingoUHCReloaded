package dev.onelili.bingoreloaded.resources;

import cn.jason31416.planetlib.message.Message;
import cn.jason31416.planetlib.message.MessageList;
import cn.jason31416.planetlib.message.MessageLoader;

import java.util.ArrayList;

public class Lang {
    public static MessageLoader loader;
    public static Message getMessage(String path){
        return loader.getMessage(path, "Missing message: "+path);
    }
    public static MessageList getMessageList(String path){
        return new MessageList(loader.getList(path, new ArrayList<>()));
    }
}
