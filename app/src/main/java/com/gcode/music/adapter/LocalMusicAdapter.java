package com.gcode.music.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.gcode.music.BR;
import com.gcode.vastadapter.base.VastBindAdapter;
import com.gcode.vastadapter.interfaces.VastBindAdapterItem;

import java.util.List;

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2021/9/10 10:39
// Description:
// Documentation:

public class LocalMusicAdapter extends VastBindAdapter {

    public LocalMusicAdapter(@NonNull List<VastBindAdapterItem> dataSource, @NonNull Context mContext) {
        super(dataSource, mContext);
    }

    @Override
    public int setVariableId() {
        return BR.item;
    }
}
