package com.armutyus.phonebookofd.ui.main;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PhoneRoom.class}, version = 1)
public abstract class PhoneDb extends RoomDatabase {
    public abstract PhoneDao phoneDao();
}