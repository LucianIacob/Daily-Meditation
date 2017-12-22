package com.dailymeditation.android.storage;

import android.content.Context;

import com.dailymeditation.android.model.Passage;

import java.util.List;

/**
 * Created with <3 by liacob & <Pi> on 29-Sep-17.
 */

public class DatabaseController {

    private final AppDatabase mAppDatabase;

    public DatabaseController(Context context) {
        mAppDatabase = AppDatabase.getAppDatabase(context);
    }

    public void addPassage(Passage passage) {
        if (mAppDatabase.userDao().findByDate(passage.getDate()).size() == 0) {
            mAppDatabase.userDao().insertAll(passage);
        }
    }

    public List<Passage> getAll() {
        return mAppDatabase.userDao().getAll();
    }
}
