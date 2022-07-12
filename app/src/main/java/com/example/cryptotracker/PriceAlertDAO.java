package com.example.cryptotracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PriceAlertDAO {
    @Query("SELECT * FROM pricealert")
    List<PriceAlert> getAll();

    @Query("SELECT * FROM pricealert WHERE pair_ticker == (:ticker)")
    List<PriceAlert> findByTicker(String ticker);

    @Insert
    void insertAll(PriceAlert... users);

    @Delete
    void delete(PriceAlert user);


}


