package com.example.cryptotracker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

enum PriceDirection
{
    UP, DOWN
}

@Entity
public class PriceAlert {

    public PriceAlert(String pair_ticker, double price, PriceDirection direction)
    {
        this.price = price;
        this.pair_ticker = pair_ticker;
        this.direction = direction;
    }

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "pair_ticker")
    public String pair_ticker;

    @ColumnInfo(name = "price")
    public double price;

    //1 : down
    //0 : up
    @ColumnInfo(name = "direction")
    public PriceDirection direction;
}
