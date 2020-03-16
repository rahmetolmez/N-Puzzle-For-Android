package com.tribow.npuzzle;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.preference.PreferenceManager;

public class ActivityGameplay extends Activity //AppCompatActivity for menu bar
{
    LayoutActivityGameplay layoutActivityGameplay;
    private int[][] boardMap;
    private int sizeX = 0;
    private int sizeY = 0;
    private boolean shouldVibrate = true;
    private boolean optionMusic = true;
    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(layoutActivityGameplay.getClose())
                    onBackPressed();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_gameplay);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setTheme(R.style.DarkTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        layoutActivityGameplay = new LayoutActivityGameplay(this);

        setContentView(layoutActivityGameplay);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        layoutActivityGameplay.setOptionVibrate(sharedPreferences.getBoolean("com.tribow.npuzzle.vibration", true));

        if(sharedPreferences.getBoolean("com.tribow.npuzzle.darkMode", false))
            layoutActivityGameplay.setDarkMode(sharedPreferences.getBoolean("com.tribow.npuzzle.darkMode", false));

        if(sharedPreferences.getBoolean("com.tribow.npuzzle.music", true))
            playMusic();
        else
            stopMusicPlayer();

        //getting size of board from user
        if (getIntent().hasExtra("com.tribow.npuzzle.sizeX") && getIntent().hasExtra("com.tribow.npuzzle.sizeY"))
        {
            sizeX = getIntent().getExtras().getInt("com.tribow.npuzzle.sizeX");
            sizeY = getIntent().getExtras().getInt("com.tribow.npuzzle.sizeY");

            createBoard(sizeX, sizeY);
            layoutActivityGameplay.setXY(sizeX, sizeY);
            reset();
            layoutActivityGameplay.setBoardArray(boardMap);
            layoutActivityGameplay.shuffleBoard(sizeX * sizeY * (sizeX + sizeY));

            if(layoutActivityGameplay.getClose())
            {
                super.onBackPressed();
            }



            //System.out.println(shouldVibrate);
        }

    }

    public void createBoard(int X, int Y)
    {
        boardMap = new int[Y][X];
    }
    public void reset()
    {
        int count = 1;

        for (int i = 0; i < sizeY; i++)
        {
            for (int j = 0; j < sizeX; j++)
            {
                if (boardMap[i][j] != -1) // -1 represents the border tiles (00)
                {
                    boardMap[i][j] = count;
                    count++;
                }
            }
        }
        boardMap[(sizeY)-1][(sizeX)-1] = 0;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
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
}
