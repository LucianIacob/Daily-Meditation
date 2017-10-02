package com.dailymeditation.android.storage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.dailymeditation.android.models.Passage;

import java.util.List;

/**
 * Created with <3 by liacob & <Pi> on 29-Sep-17.
 */

@Dao
public interface PassageDao {

    @Query("SELECT * FROM passage")
    List<Passage> getAll();

    @Query("SELECT COUNT(*) FROM passage")
    int countPassages();

    @Insert
    void insertAll(Passage... passages);

    @Query("SELECT * FROM passage WHERE date LIKE :date")
    List<Passage> findByDate(String date);

    @Delete
    void delete(Passage passage);

}
