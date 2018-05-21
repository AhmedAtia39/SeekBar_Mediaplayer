package com.example.ahmed.seek_bar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class FromPhoneActivity extends AppCompatActivity {
    ArrayList<listitemFromPhone> list = new ArrayList<>();
    ListView listView;
    MediaPlayer mediaPlayer = new MediaPlayer();
    Button btn_play, btn_pause, btn_stop;
    TextView tvTitle, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_phone);

        listView = (ListView) findViewById(R.id.listView2);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_stop = (Button) findViewById(R.id.btn_stop);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvCurrentTime = (TextView) findViewById(R.id.tvCurrentTime);
        tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);

        permission_Check();

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_play.setVisibility(View.INVISIBLE);
                btn_pause.setVisibility(View.VISIBLE);
                btn_stop.setVisibility(View.VISIBLE);

                if (!mediaPlayer.isPlaying()) {
                    Thread updateSeekBar;
                    updateSeekBar = new Thread() {
                        @Override
                        public void run() {
                            int SoundDuration = mediaPlayer.getDuration();
                            int currentPostion = 0;
                            seekBar.setMax(SoundDuration);
                            while (currentPostion < SoundDuration) {
                                try {
                                    sleep(50);
                                    currentPostion = mediaPlayer.getCurrentPosition();
                                    seekBar.setProgress(currentPostion);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    mediaPlayer.start();
                    updateSeekBar.start();
                }
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_play.setVisibility(View.INVISIBLE);
                btn_pause.setVisibility(View.INVISIBLE);
                btn_stop.setVisibility(View.INVISIBLE);
                seekBar.setVisibility(View.INVISIBLE);
                tvTotalTime.setVisibility(View.INVISIBLE);
                tvCurrentTime.setVisibility(View.INVISIBLE);
                tvTitle.setText("");
                mediaPlayer.stop();
            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_play.setVisibility(View.VISIBLE);
                btn_pause.setVisibility(View.INVISIBLE);
                btn_stop.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) mediaPlayer.seekTo(i);
                SoundTime();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                btn_play.setVisibility(View.VISIBLE);
                seekBar.setVisibility(View.VISIBLE);
                tvTotalTime.setVisibility(View.VISIBLE);
                tvCurrentTime.setVisibility(View.VISIBLE);
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(FromPhoneActivity.this, Uri.parse(list.get(i).data));
                tvTitle.setText(list.get(i).Title);
                SoundTime();
            }
        });

    }


    private void SoundTime() {
        seekBar.setMax(mediaPlayer.getDuration());
        int tim = (seekBar.getMax() / 1000);
        int m = tim / 60;
        int s = tim % 60;
        //////
        int tim0 = (seekBar.getProgress() / 1000);
        int m0 = tim0 / 60;
        int s0 = tim0 % 60;

        tvTotalTime.setText(s + " : " + m);
        tvCurrentTime.setText(s0 + " : " + m0);
    }


    public void permission_Check() {
        //////////////////
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }

        }
        GetAllSound();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            GetAllSound();
        } else {
            permission_Check();
        }
    }

    public void GetAllSound() {
        Cursor cursor;
        Uri Allsounduri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0 ";
        cursor = managedQuery(Allsounduri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String Title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    list.add(new listitemFromPhone(name, data, album, artist, Title));

                } while (cursor.moveToNext());
            }
        }


        Adapter adapter = new Adapter(list, this);
        listView.setAdapter(adapter);

        if (list.size() > 0) {
            mediaPlayer = MediaPlayer.create(FromPhoneActivity.this, Uri.parse(list.get(0).data));
        }
    }
}
