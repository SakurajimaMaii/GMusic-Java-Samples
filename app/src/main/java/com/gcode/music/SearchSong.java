package com.gcode.music;

import com.gcode.music.model.LocalMusicBean;
import com.gcode.vastadapter.interfaces.VastBindAdapterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * create by liu
 * on 2020/5/21 9:24 AM
 * 搜索实现
 **/
public class SearchSong {
    public static List<VastBindAdapterItem> searchSongByName(List<VastBindAdapterItem> musicBeans, String searchName) {
        List<VastBindAdapterItem> searchResult = new ArrayList<>();
        for (VastBindAdapterItem bean:musicBeans){
            if (((LocalMusicBean) bean).getSong().contains(searchName)){
                searchResult.add(bean);
            }
        }
        return searchResult;
    }
}
