package com.gcode.music;

import static com.gcode.vasttools.extension.CastExtension.cast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gcode.music.adapter.LocalMusicAdapter;
import com.gcode.music.databinding.ActivityMainBinding;
import com.gcode.music.model.LocalMusicBean;
import com.gcode.vastadapter.base.VastBindAdapter;
import com.gcode.vastadapter.interfaces.VastBindAdapterItem;
import com.gcode.vasttools.activity.VastVbActivity;
import com.gcode.vasttools.utils.LogUtils;
import com.gcode.vasttools.utils.ToastUtils;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2020/11/17 19:06
// Description:
// Documentation:

public class MainActivity extends VastVbActivity<ActivityMainBinding> implements View.OnClickListener {

    TextView singerTv, songTv;

    //搜索到到数据
    List<VastBindAdapterItem> searchResultMusic = new ArrayList<>();
    //做个数据缓存
    private final List<VastBindAdapterItem> cacheMusicData = new ArrayList<>();

    //数据源
    List<VastBindAdapterItem> mData;
    private LocalMusicAdapter adapter;
    //记录当前正在播放的音乐的位置
    int currentPlayPosition = -1;
    //记录暂停音乐时进度条的位置
    int currentPausePositionInSong = 0;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getBaseContext();

        PermissionX.init(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            ToastUtils.showShortMsg(context, "所有权限已经授权,如果没有歌曲显示请重启应用");
                        } else {
                            ToastUtils.showShortMsg(context, "以下权限未被授予$deniedList");
                        }
                    }
                });

        initView();
        mediaPlayer = new MediaPlayer();
        mData = new ArrayList<>();
        //创建适配器对象
        adapter = new LocalMusicAdapter(mData,getContext());
        adapter.setOnItemClickListener(new VastBindAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int i) {
                currentPlayPosition = i;
                LocalMusicBean musicBean = null;
                try {
                    musicBean = cast(mData.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                playMusicInMusicBean(musicBean);
            }
        });
        getBinding().localMusicRv.setAdapter(adapter);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        getBinding().localMusicRv.setLayoutManager(layoutManager);
        //加载本地数据源
        loadLocalMusicData();
        //设置每一项的点击事件
    }

    private void playMusicInMusicBean(LocalMusicBean musicBean) {
        /*根据传入对象播放音乐*/
        //设置底部显示的歌手名称和歌曲名
        singerTv.setText(musicBean.getSinger());
        songTv.setText(musicBean.getSong());
        stopMusic();
        //重置多媒体播放器
        mediaPlayer.reset();
        //设置新的播放路径
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            String albumArt = musicBean.getAlbumArt();
            LogUtils.INSTANCE.i(getDefaultTag(), "playMusicInMusicBean: album==" + albumArt);
            Bitmap bm = BitmapFactory.decodeFile(albumArt);
            LogUtils.INSTANCE.i(getDefaultTag(), "playMusicInMusicBean: bm==" + bm);
            getBinding().localMusicBottomIvIcon.setImageBitmap(bm);
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {
        //播放音乐的函数
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            if (currentPausePositionInSong == 0) {
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //从暂停到播放
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
            }
            getBinding().localMusicBottomIvPlay.setImageResource(R.drawable.pause);
        }
    }

    private void pauseMusic() {
        /* 暂停音乐的函数*/
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            getBinding().localMusicBottomIvPlay.setImageResource(R.drawable.play);
        }
    }

    private void stopMusic() {
        //停止音乐的函数
        if (mediaPlayer != null) {
            currentPausePositionInSong = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            getBinding().localMusicBottomIvPlay.setImageResource(R.drawable.play);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    @SuppressLint({"NotifyDataSetChanged","Range"})
    private void loadLocalMusicData() {
        Cursor cursor = null;
        try {
            //加载本地文件到集合中
            //1.获取ContentResolver对象
            ContentResolver resolver = getContentResolver();
            //1.获取本地存储的Uri地址
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            //开始查询地址
            cursor = resolver.query(uri, null, MediaStore.Audio.Media.ARTIST+"!=?", new String[]{"<unknown>"}, null);
            //遍历cursor
            int id = 0;
            while (cursor.moveToNext()) {
                String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                @SuppressLint("InlinedApi") String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                @SuppressLint("InlinedApi") String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                id++;
                String sid = String.valueOf(id);
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                String time = sdf.format(new Date(duration));
                String album_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String albumArt = getAlbumArt(album_id);
                //将一行当中的对象封装到数据当中
                LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time, path, albumArt);
                mData.add(bean);
            }
            //做数据缓存，提升效率
            cacheMusicData.addAll(mData);
            //数据源变化，提示适配器更新
            adapter.notifyDataSetChanged();
        } finally {
            assert cursor != null;
            cursor.close();
        }
    }

    private String getAlbumArt(String album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = this.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + album_id),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        return album_art;
    }

    private void initView() {
        /*初始化控件函数*/
        singerTv = findViewById(R.id.local_music_bottom_tv_songer);
        songTv = findViewById(R.id.local_music_bottom_tv_song);

        getBinding().searchView.setSubmitButtonEnabled(true);
        getBinding().searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResultMusic = SearchSong.searchSongByName(mData, query);
                mData.clear();
                mData.addAll(searchResultMusic);
                adapter.notifyDataSetChanged();
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                mData.clear();
                if (cacheMusicData.size() < 1) {
                    loadLocalMusicData();
                    return false;
                }
                mData.addAll(cacheMusicData);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        getBinding().localMusicBottomIvNext.setOnClickListener(this); /*设置点击事件*/
        getBinding().localMusicBottomIvLast.setOnClickListener(this); /*设置点击事件*/
        getBinding().localMusicBottomIvPlay.setOnClickListener(this); /*设置点击事件*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.local_music_bottom_iv_last):
                if (currentPlayPosition == 0) {
                    Toast.makeText(this, "已经是第一首了，没有上一曲！", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition - 1;
                LocalMusicBean lastBean = null;
                try {
                    lastBean = cast(mData.get(currentPlayPosition));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                playMusicInMusicBean(lastBean);
                break;

            case (R.id.local_music_bottom_iv_next):
                if (currentPlayPosition == mData.size() - 1) {
                    Toast.makeText(this, "已经是最后一首了，没有下一曲！", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition + 1;
                LocalMusicBean nextBean = null;
                try {
                    nextBean = cast(mData.get(currentPlayPosition));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                playMusicInMusicBean(nextBean);
                break;

            case (R.id.local_music_bottom_iv_play):
                if (currentPlayPosition == -1) {
                    //并没有选中要播放的音乐
                    Toast.makeText(this, "请选择想要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    //此时处于播放状态，需要暂停音乐
                    pauseMusic();
                } else {
                    //此时没有播放音乐，点击开始播放音乐
                    playMusic();
                }
                break;
        }
    }
}