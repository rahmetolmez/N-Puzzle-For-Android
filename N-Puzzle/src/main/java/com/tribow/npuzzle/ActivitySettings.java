package com.tribow.npuzzle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

public class ActivitySettings extends Activity
{

    public static final String PACK = "com.tribow.npuzzle.vibration";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedEditor;
    private boolean shouldVibrate = true;
    private boolean optionDarkMode = false;
    private boolean optionMusic = true;
    private ToggleButton tbVibration;
    private ToggleButton tbDarkMode;
    private ToggleButton tbMusic;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tbVibration = findViewById(R.id.tb_vibration);
        tbDarkMode = findViewById(R.id.tb_darkMode);
        tbMusic = findViewById(R.id.tb_music);

        LinearLayout as_layout = findViewById(R.id.as_layout); // layout for dark mode

        // setting dark theme
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedEditor = sharedPreferences.edit();
        shouldVibrate = sharedPreferences.getBoolean("com.tribow.npuzzle.vibration", true);
        optionDarkMode = sharedPreferences.getBoolean("com.tribow.npuzzle.darkMode", false);
        optionMusic = sharedPreferences.getBoolean("com.tribow.npuzzle.music", true);

        if(optionDarkMode)
        {
            setTheme(R.style.DarkTheme);
            as_layout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPDark, null));
        }

        checkShared(shouldVibrate, tbVibration);
        checkShared(optionDarkMode, tbDarkMode);
        checkShared(optionMusic, tbMusic);

        tbVibration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (tbVibration.isChecked())
                {
                    shouldVibrate = true;
                    sharedEditor.putBoolean("com.tribow.npuzzle.vibration", true);
                    sharedEditor.commit();
                }
                else
                {
                    shouldVibrate = false;
                    sharedEditor.putBoolean("com.tribow.npuzzle.vibration", false);
                    sharedEditor.commit();
                }

                getIntent().putExtra("com.tribow.npuzzle.shouldVibrate", shouldVibrate);
            }
        });

        tbDarkMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (tbDarkMode.isChecked())
                {
                    optionDarkMode = true;
                    sharedEditor.putBoolean("com.tribow.npuzzle.darkMode", true);
                    sharedEditor.commit();
                    recreateActivity();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else
                {
                    optionDarkMode = false;
                    sharedEditor.putBoolean("com.tribow.npuzzle.darkMode", false);
                    sharedEditor.commit();
                    recreateActivity();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

                getIntent().putExtra("com.tribow.npuzzle.darkMode", optionDarkMode);
            }
        });

        tbMusic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (tbMusic.isChecked())
                {
                    optionMusic = true;
                    sharedEditor.putBoolean("com.tribow.npuzzle.music", true);
                    sharedEditor.commit();
                }
                else
                {
                    optionMusic= false;
                    sharedEditor.putBoolean("com.tribow.npuzzle.music", false);
                    sharedEditor.commit();
                }

                getIntent().putExtra("com.tribow.npuzzle.music", optionMusic);
            }
        });
    }

    private void recreateActivity()
    {
        Intent newIntent = new Intent(getApplicationContext(), ActivitySettings.class);
        startActivity(newIntent);
        finish();
    }

    public boolean getOptionDarkMode()
    {
        return optionDarkMode;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if(!optionMusic)
        {
            stopMusicPlayer();
        }
        // to start dark mode on main activity
        Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
        //newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(newIntent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void playMusic()
    {
        if(mediaPlayer == null)
        {
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
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

    private void checkShared(boolean is, ToggleButton tb)
    {
        if (is)
        {
            tb.setChecked(true);
        }
        else
        {
            tb.setChecked(false);
        }
    }
}
