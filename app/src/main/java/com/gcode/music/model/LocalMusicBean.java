package com.gcode.music.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.gcode.music.BR;
import com.gcode.music.R;
import com.gcode.vastadapter.interfaces.VAapClickEventListener;
import com.gcode.vastadapter.interfaces.VAdpLongClickEventListener;
import com.gcode.vastadapter.interfaces.VastBindAdapterItem;

import org.jetbrains.annotations.Nullable;

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2020/11/17 19:06
// Description:
// Documentation:

public class LocalMusicBean extends BaseObservable implements VastBindAdapterItem {
    private String id; //歌曲id
    private String song; //歌曲
    private String singer; //歌手名称
    private String album; //专辑名称
    private String duration; //歌曲时长
    private String path; //歌曲路径
    private String albumArt;  //专辑地址

    public LocalMusicBean(String id, String song, String singer, String album, String duration, String path, String albumArt) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.duration = duration;
        this.path = path;
        this.albumArt = albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
        notifyPropertyChanged(BR.albumArt);
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    public void setSong(String song) {
        this.song = song;
        notifyPropertyChanged(BR.song);
    }

    public void setSinger(String singer) {
        this.singer = singer;
        notifyPropertyChanged(BR.singer);
    }

    public void setAlbum(String album) {
        this.album = album;
        notifyPropertyChanged(BR.album);
    }

    public void setDuration(String duration) {
        this.duration = duration;
        notifyPropertyChanged(BR.duration);
    }

    public void setPath(String path) {
        this.path = path;
        notifyPropertyChanged(BR.path);
    }

    @Bindable
    public String getAlbumArt() {
        return albumArt;
    }

    @Bindable
    public String getId() {
        return id;
    }

    @Bindable
    public String getSong() {
        return song;
    }

    @Bindable
    public String getSinger() {
        return singer;
    }

    @Bindable
    public String getAlbum() {
        return album;
    }

    @Bindable
    public String getDuration() {
        return duration;
    }

    @Bindable
    public String getPath() {
        return path;
    }

    @Nullable
    @Override
    public VAapClickEventListener getVbAapClickEventListener() {
        return null;
    }

    @Override
    public void setVbAapClickEventListener(@Nullable VAapClickEventListener vAapClickEventListener) {

    }

    @Nullable
    @Override
    public VAdpLongClickEventListener getVbAdpLongClickEventListener() {
        return null;
    }

    @Override
    public void setVbAdpLongClickEventListener(@Nullable VAdpLongClickEventListener vAdpLongClickEventListener) {

    }

    @Override
    public int getVBAdpItemType() {
        return R.layout.item_local_music;
    }
}
