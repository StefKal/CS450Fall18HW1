package edu.stlawu.stopwatch;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Define variable for our views
    private TextView tv_count = null;
    private Button bt_start = null;
    private Button bt_stop = null;
    private Button bt_reset = null;
    private Button bt_resume = null;
    private Timer t = new Timer();
    private Counter ctr = new Counter();  // TimerTask
    private int count;
    private String time;

    public AudioAttributes  aa = null;
    private SoundPool soundPool = null;
    private int bloopSound = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        this.tv_count = findViewById(R.id.tv_count);
        this.bt_start = findViewById(R.id.bt_start);
        this.bt_stop = findViewById(R.id.bt_stop);
        this.bt_reset = findViewById(R.id.bt_reset);
        this.bt_resume = findViewById(R.id.bt_resume);

        int count = getPreferences(MODE_PRIVATE).getInt("Count", 0);
        this.count = count;
        this.tv_count.setText(this.time);
        ctr.run();

        if (this.count == 0){
            bt_start.setEnabled(true);
            bt_resume.setEnabled(false);
            bt_stop.setEnabled(false);
            bt_reset.setEnabled(false);
        }else{
            bt_start.setEnabled(false);
            bt_stop.setEnabled(false);
            bt_reset.setEnabled(true);
            bt_resume.setEnabled(true);
        }

        this.bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_start.setEnabled(false);
                bt_stop.setEnabled(true);
                bt_reset.setEnabled(true);
                bt_resume.setEnabled(false);
                ctr = new Counter();
                t = new Timer();

                t.scheduleAtFixedRate(ctr, 0, 100);

            }

        });


        this.bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                t.cancel();

                bt_reset.setEnabled(true);
                bt_resume.setEnabled(true);
                bt_stop.setEnabled(false);
            }
        });

        this.bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.count = 0;
                MainActivity.this.tv_count.setText(MainActivity.this.time);
                ctr.run();
                t.cancel();

                bt_resume.setEnabled(false);
                bt_start.setEnabled(true);
                bt_stop.setEnabled(false);
            }
        });

        this.bt_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_stop.setEnabled(true);
                bt_reset.setEnabled(false);
                bt_resume.setEnabled(false);
                ctr = new Counter();
                t = new Timer();

                t.scheduleAtFixedRate(ctr, 0, 100);
            }
        });



        this.aa = new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(aa)
                .build();
        this.bloopSound = this.soundPool.load(
                this, R.raw.bloop, 1);

        this.tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(bloopSound, 1f,
                        1f, 1, 0, 1f);
                Animator anim = AnimatorInflater
                        .loadAnimator(MainActivity.this,
                                R.animator.counter);
                anim.setTarget(tv_count);
                anim.start();
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();



        // factory method - design pattern
        Toast.makeText(this, "Stopwatch is started",
                Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("Count", this.count)
                .apply();
    }

    class Counter extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {


                            int tens = count % 10;
                            int seconds = (count / 10) % 60;
                            int minutes = (count / 600) % 60;

                            MainActivity.this.time = String.format("%02d:%02d:%d",minutes, seconds, tens);
                            MainActivity.this.tv_count.setText(MainActivity.this.time);
                            count++;
                        }
                    }
            );
        }

    }
}