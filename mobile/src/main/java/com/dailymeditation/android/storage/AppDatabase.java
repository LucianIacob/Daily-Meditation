package com.dailymeditation.android.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dailymeditation.android.model.Passage;

/**
 * Created with <3 by liacob & <Pi> on 29-Sep-17.
 */

@Database(entities = {Passage.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    @Nullable private static AppDatabase INSTANCE;

    @NonNull
    static AppDatabase getAppDatabase(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "passage-database")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract PassageDao userDao();
}
