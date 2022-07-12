package com.example.cryptotracker;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerStatistics;
import com.example.cryptotracker.email.Email;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

import com.example.cryptotracker.keys;

public class PriceDiscoveryService extends Service
{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean run = true;
    public static BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(TOKEN1, TOKEN2);
    public static BinanceApiRestClient client = factory.newRestClient();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent open_activity = new Intent(this, MainActivity.class);
        open_activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, open_activity, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.crypto)
                .setContentTitle("Price Alert!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                PriceAlertDAO dao = MainActivity.db.priceAlertDAO();
                long serverTime = client.getServerTime();
                Log.d("BINANCE", ""+serverTime);


                while(run)
                {
                    List<PriceAlert> alerts = dao.getAll();

                    for (PriceAlert p: alerts)
                    {
                        TickerStatistics tickerStatistics = client.get24HrPriceStatistics(p.pair_ticker);
                        double last_price = Double.parseDouble(tickerStatistics.getLastPrice());
                        Log.d("BINANCE", p.pair_ticker+": "+last_price);

                        if(p.direction == PriceDirection.DOWN)
                        {
                            if(last_price < p.price)
                            {
                                Log.d("ALARM", p.pair_ticker+" REACHED "+p.price);
                                builder.setContentText(p.pair_ticker+" REACHED "+p.price);
                                String sha256hex = DigestUtils.sha256Hex(p.pair_ticker + p.price + last_price);
                                sha256hex = sha256hex.substring(0, 3);
                                notificationManager.notify(Integer.decode("0x"+sha256hex), builder.build());

                                List<PriceAlert> alert_list = dao.findByTicker(p.pair_ticker);
                                for(PriceAlert al: alert_list)
                                {
                                    if (al.price == p.price)
                                    {
                                        Log.w("ALARM_RESET", "Deleting alarm entry!!");
                                        dao.delete(al);
                                        break;
                                    }
                                }
                                MainActivity.populateAlertList();

                                boolean res = MainActivity.emailSender.sendEmail(new Email(MAIL2, "PRICE ALERT", p.pair_ticker+" REACHED "+p.price, ""));
                                if (!res)
                                    Log.e("EMAIL", "CANNOT SEND EMAIL");
                            }
                        }
                        else
                        {
                            if(last_price > p.price)
                            {
                                Log.d("ALARM", p.pair_ticker+" REACHED "+p.price);
                                builder.setContentText(p.pair_ticker+" REACHED "+p.price);
                                String sha256hex = DigestUtils.sha256Hex(p.pair_ticker + p.price + last_price);
                                sha256hex = sha256hex.substring(0, 3);
                                notificationManager.notify(Integer.decode("0x"+sha256hex), builder.build());

                                List<PriceAlert> alert_list = dao.findByTicker(p.pair_ticker);
                                for(PriceAlert al: alert_list)
                                {
                                    if (al.price == p.price)
                                    {
                                        Log.w("ALARM_RESET", "Deleting alarm entry!!");
                                        dao.delete(al);
                                        break;
                                    }
                                }
                                MainActivity.populateAlertList();

                                boolean res = MainActivity.emailSender.sendEmail(new Email(MAIL, "PRICE ALERT", p.pair_ticker+" REACHED "+p.price, ""));
                                if (!res)
                                    Log.e("EMAIL", "CANNOT SEND EMAIL");
                            }
                        }

                    }


                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        t.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        run = false;
        super.onDestroy();
    }
}
