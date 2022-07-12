package com.example.cryptotracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.cryptotracker.email.EmailSender;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.cryptotracker.databinding.ActivityMainBinding;
import com.example.cryptotracker.keys;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    public static MainDatabase db = null;
    public static RecyclerView mainRecyclerView;
    public static EmailSender emailSender = new EmailSender(MAIL_SENDER, MAIL_SENDER_PWD);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //we initialize the database
        db = Room.databaseBuilder(getApplicationContext(),
                MainDatabase.class, "main_db").fallbackToDestructiveMigration().build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "main_channel";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        com.example.cryptotracker.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //popup for ticker insertion
                AlertInsertionPopup popup = new AlertInsertionPopup(db, ((PriceAlertAdapter)mainRecyclerView.getAdapter()).list);
                popup.showPopupWindow(view);
            }
        });
        mainRecyclerView = (RecyclerView)findViewById(R.id.main_recyclerview);
        PriceAlertAdapter adapter = new PriceAlertAdapter();
        mainRecyclerView.setAdapter(adapter);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        populateAlertList();

        Intent intent = new Intent(this, PriceDiscoveryService.class);
        startService(intent);
    }

    public static void populateAlertList()
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                PriceAlertDAO dao = db.priceAlertDAO();
                ((PriceAlertAdapter)mainRecyclerView.getAdapter()).list = dao.getAll();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}