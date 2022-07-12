package com.example.cryptotracker;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.binance.api.client.domain.market.TickerStatistics;

import java.util.List;
import java.util.Random;

public class AlertInsertionPopup {

    private MainDatabase db;
    private List<PriceAlert> list;
    public AlertInsertionPopup(MainDatabase db, List<PriceAlert> list)
    {
        this.db = db;
        this.list = list;
    }

    public void showPopupWindow(final View view) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.alert_creation_popup, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler

        EditText ticker = popupView.findViewById(R.id.popup_ticker_textview);
        EditText price = popupView.findViewById(R.id.popup_price_textview);

        Button addButton = popupView.findViewById(R.id.popup_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //As an example, display the message

                String ticker_input = ticker.getText().toString();
                String price_input = price.getText().toString();
                ticker_input = ticker_input.toUpperCase();
                double price_double;
                try {
                    price_double = Double.parseDouble(price_input);
                }
                catch (NumberFormatException e)
                {
                    Toast.makeText(view.getContext(), "INVALID PRICE", Toast.LENGTH_LONG).show();
                    return;
                }
                String finalTicker_input = ticker_input;
                final double[] curr_price = {-1};
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TickerStatistics tickerStatistics = PriceDiscoveryService.client.get24HrPriceStatistics(finalTicker_input);
                            curr_price[0] = Double.parseDouble(tickerStatistics.getLastPrice());
                        }
                        catch (Exception e)
                        {
                            Log.e("BINANCE", e.toString());
                        }
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(curr_price[0] == -1)
                {
                    Toast.makeText(view.getContext(), "INVALID TICKER PAIR", Toast.LENGTH_LONG).show();
                    return;
                }


                PriceAlertDAO dao = db.priceAlertDAO();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PriceDirection direction = curr_price[0] > price_double ? PriceDirection.DOWN : PriceDirection.UP;
                        dao.insertAll(new PriceAlert(finalTicker_input, price_double, direction));
                        list.add(new PriceAlert(finalTicker_input, price_double, direction));
                    }
                }).start();

                popupWindow.dismiss();

            }
        });

        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
