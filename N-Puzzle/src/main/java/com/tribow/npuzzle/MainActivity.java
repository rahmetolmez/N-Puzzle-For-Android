package com.tribow.npuzzle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener
{
    private int size_X = 3;
    private int size_Y = 3;
    private boolean optionDarkMode = false;
    private boolean optionMusic = true;
    private RelativeLayout relativeLayout;
    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        handleMusic();
        handleWindow();
        setContentView(R.layout.activity_main);

        LinearLayout am_layout = findViewById(R.id.am_layout);
        handleDarkMode(am_layout);

        // creating number pickers for sizeX & sizeY input
        NumberPicker numberPickerX = findViewById(R.id.numberPickerX);
        setNumberPicker(numberPickerX, 3, 9);

        NumberPicker numberPickerY = findViewById(R.id.numberPickerY);
        setNumberPicker(numberPickerY, 3, 9);

        // setting sizeX & sizeY from input
        size_X = numberPickerX.getValue();
        size_Y = numberPickerY.getValue();

        NumberPicker.OnValueChangeListener newlistener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){

                size_Y = newVal;

            }
        };
        numberPickerY.setOnValueChangedListener(newlistener);

        // creating button "Play"
        Button button_play = (Button) findViewById(R.id.button_play);

        button_play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopMusic();
                Intent gameplayIntent = new Intent(getApplicationContext(), ActivityGameplay.class);
                gameplayIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                gameplayIntent.putExtra("com.tribow.npuzzle.sizeX", size_X);
                gameplayIntent.putExtra("com.tribow.npuzzle.sizeY", size_Y);

                startActivity(gameplayIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // creating button "Settings"
        Button button_settings = (Button) findViewById(R.id.button_settings);

        button_settings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                stopMusic();
                Intent settingsIntent = new Intent(getApplicationContext(), ActivitySettings.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(settingsIntent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
    {
        size_X = newVal;
    }

    private void recreateActivity()
    {
        Intent newIntent = new Intent(getApplicationContext(), ActivitySettings.class);
        startActivity(newIntent);
        finish();
    }

    public void playMusic()
    {
        if(mediaPlayer == null)
        {
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music_elise);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    stopMusicPlayer();
                }
            });
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void pauseMusic()
    {
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }

    public void stopMusic()
    {
        stopMusicPlayer();
    }

    private void stopMusicPlayer()
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        stopMusicPlayer();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(sharedPreferences.getBoolean("com.tribow.npuzzle.music", true))
            playMusic();
    }

    private void handleWindow()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("");
    }

    private void setNumberPicker(NumberPicker numberPicker, int min, int max)
    {
        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(max);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private void handleDarkMode(LinearLayout linearLayout)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        optionDarkMode = sharedPreferences.getBoolean("com.tribow.npuzzle.darkMode", false);

        if(optionDarkMode)
        {
            setTheme(R.style.DarkTheme);
            linearLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPDark, null));
        }
    }

    private void handleMusic()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        optionMusic= sharedPreferences.getBoolean("com.tribow.npuzzle.music", true);

        if(optionMusic)
            playMusic();
        else
            stopMusicPlayer();
    }
}
