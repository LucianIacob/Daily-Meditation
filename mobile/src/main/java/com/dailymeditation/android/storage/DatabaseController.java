package com.dailymeditation.android.storage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dailymeditation.android.model.Passage;

import java.util.List;

/**
 * Created with <3 by liacob & <Pi> on 29-Sep-17.
 */

public class DatabaseController {

    @Nullable private final AppDatabase mAppDatabase;

    public DatabaseController(@NonNull Context context) {
        mAppDatabase = AppDatabase.getAppDatabase(context);
    }

    public void addPassage(@NonNull Passage passage) {
        if (mAppDatabase != null && mAppDatabase.userDao().findByDate(passage.getDate()).size() == 0) {
            mAppDatabase.userDao().insertAll(passage);
        }
    }

    public List<Passage> getAll() {
        return mAppDatabase != null ? mAppDatabase.userDao().getAll() : null;
    }
}
