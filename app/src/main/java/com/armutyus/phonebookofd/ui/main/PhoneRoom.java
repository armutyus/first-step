package com.armutyus.phonebookofd.ui.main;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import io.reactivex.rxjava3.annotations.Nullable;

@Entity
public class PhoneRoom {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @Nullable
    @ColumnInfo(name = "phone")
    public String phone;

    @Nullable
    @ColumnInfo(name = "personImage")
    public byte[] personImage;

    public PhoneRoom(String name, String phone, byte[] personImage) {
        this.name = name;
        this.phone = phone;
        this.personImage = personImage;
    }
}
