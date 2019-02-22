package me.jatinsoni.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // set up the credentials and notification constants
    private static final String USERNAME = "edureka";
    private static final String PASSWORD = "edureka123";
    private static final String CHANNEL_ID = "personal_notifications";
    private static final int NOTIFICATION_ID = 1;
    private static int numMessages;
    private static int bigNumber;

    // widget members
    EditText username, password;
    TextView feedbackMessage;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get all widgets
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.sign_in_button);
        feedbackMessage = findViewById(R.id.feedback_message);

        // hide and clear feedback message onCreate
        feedbackMessage.setText("");
        feedbackMessage.setVisibility(View.GONE);

        // set the button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // show a toast message on button click, just to cross check the click
                Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();

                // convert credentials value to string
                String currentUsername = username.getText().toString();
                String currentPassword = password.getText().toString();

                // check for empty fields. If any than show the message
                if (currentUsername.trim().length() == 0 || currentPassword.trim().length() == 0) {

                    feedbackMessage.setText(getResources().getString(R.string.missing_field_message));
                    feedbackMessage.setTextColor(getResources().getColor(R.color.colorWarning));
                    feedbackMessage.setVisibility(View.VISIBLE);

                    // shoot typical notification on empty fields
                    displaySimpleNotification(getResources().getString(R.string.missing_field_message));

                }
                // if both fields has value than move on
                else {

                    //verify the credentials
                    if (currentUsername.equals(USERNAME) && currentPassword.equals(PASSWORD)) {

                        // set message text
                        feedbackMessage.setText(getResources().getString(R.string.success_message));
                        // set the success color
                        feedbackMessage.setTextColor(getResources().getColor(R.color.colorSuccess));
                        // make it visible
                        feedbackMessage.setVisibility(View.VISIBLE);
                        // set button state to disabled
                        loginButton.setEnabled(false);

                        // shoot big style notification with home and profile actions
                        scheduleNotification(getResources().getString(R.string.success_message));

                    }
                    // display message if wrong credentials
                    else {

                        // set message text
                        feedbackMessage.setText(getResources().getString(R.string.failure_message));
                        // set the warning color
                        feedbackMessage.setTextColor(getResources().getColor(R.color.colorWarning));
                        // make it visible
                        feedbackMessage.setVisibility(View.VISIBLE);

                        //clear the input values
                        username.setText("");
                        password.setText("");

                        // shoot typical notification on wrong credentials
                        displaySimpleNotification(getResources().getString(R.string.failure_message));

                    }

                }

            }
        });

    }

    /**
     * Shoot notification with required message
     *
     * @param text {string} - notification text
     * @return void
     */
    private void displaySimpleNotification(String text) {

        //create notification channel
        createNotificationChannel();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        mBuilder.setSmallIcon(R.drawable.ic_notifications);
        mBuilder.setContentTitle(getResources().getString(R.string.authentication));
        mBuilder.setContentText(text);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setNumber(++numMessages);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    /**
     * Create notification channel for the app
     *
     * @return void
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notifications";
            String description = "Include all the personal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }

    /**
     * Create scheduled notification. This is the big type
     * notification display with the bitmap (picture) and
     * having multiple actions
     *
     * @param message {string} - notification message
     */
    private void scheduleNotification(String message) {

        // create notification channel
        createNotificationChannel();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notification_image);

        Intent intent = new Intent(this, Home.class);
        Intent intent1 = new Intent(this, Profile.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, NOTIFICATION_ID, intent1, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_home, "Home", pendingIntent).build();
        NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(R.drawable.ic_profile, "Profile", pendingIntent2).build();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentText(getResources().getString(R.string.success_message))
                .setNumber(++bigNumber)
                .setContentTitle(getResources().getString(R.string.authentication))
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(message))
                .addAction(action)
                .addAction(action2)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notification);

    }
}
