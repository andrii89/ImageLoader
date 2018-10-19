package com.test.android.applicationa.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface LinkDao {

    @Query("SELECT * FROM link")
    LiveData<List<LinkData>> loadLinksSync();

    @Query("SELECT * FROM link")
    List<LinkData> loadLinks();

    @Query("SELECT * FROM link WHERE link.id = :id")
    List<LinkData> selectById(long id);

    @Query("SELECT * FROM link ORDER BY open_time DESC")
    LiveData<List<LinkData>> loadLinksSortedByTimeSync();

    @Query("SELECT * FROM link ORDER BY status")
    LiveData<List<LinkData>> loadLinksSortedByStatusSync();

    @Insert
    long insertLink(LinkData linkData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateLink(LinkData linkData);

    @Query("DELETE FROM link WHERE link.id = :id")
    int deleteById(long id);
}
