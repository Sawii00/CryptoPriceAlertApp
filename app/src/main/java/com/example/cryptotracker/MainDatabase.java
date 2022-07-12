package com.example.cryptotracker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PriceAlert.class}, version = 5)
public abstract class MainDatabase extends RoomDatabase {
    public abstract PriceAlertDAO priceAlertDAO();
}