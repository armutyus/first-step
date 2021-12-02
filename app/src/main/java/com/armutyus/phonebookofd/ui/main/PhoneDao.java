package com.armutyus.phonebookofd.ui.main;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface PhoneDao {

    @Query("SELECT name,id FROM PhoneRoom")
    Flowable<List<PhoneRoom>> getPhoneRoomWithNameAndId();

    @Query("SELECT * FROM PhoneRoom WHERE id = :id")
    Flowable<PhoneRoom> getPhoneRoomById(int id);

    @Insert
    Completable insert(PhoneRoom phoneRoom);

    @Delete
    Completable delete(PhoneRoom phoneRoom);
}
