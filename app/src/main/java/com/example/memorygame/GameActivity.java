package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import java.util.stream.IntStream;

import static com.example.memorygame.GameViewAdaptor.DERP;

public class GameActivity extends AppCompatActivity {
    private GridView mGv;
    private GameViewAdaptor adaptor;
    private List<Icon> icons = null;
    private List<Icon> shows = null;

    public GameViewAdaptor gameViewAdaptor() {
        return this.adaptor;
    }

    public List<Icon> getAnswer() {
        return this.shows;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.mGv = (GridView) findViewById(R.id.gridview);
        Initial();

        SharedPreferences shared = getSharedPreferences("status", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();


        this.mGv.setAdapter(new GameViewAdaptor(GameActivity.this, this.shows));
        this.adaptor = (GameViewAdaptor) this.mGv.getAdapter();
        this.mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                GridView gridView = findViewById(R.id.gridview);

                SharedPreferences shared = getSharedPreferences("status", MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                int cnt = shared.getInt("currnum", 0);

                if (!shared.getBoolean(Integer.toString(position), false) && cnt <= 1) {
                    ImageView itemView = gridView.getAdapter().getView(position, view, parent).findViewById(R.id.iv_grid);
                    Glide.with(GameActivity.this).load(getAnswer().get(position).getAddress()).into(itemView);
                    editor.putBoolean(Integer.toString(position), true);
                    editor.putInt("currnum", cnt + 1);
                    editor.commit();
                }
                if (shared.getInt("currnum", 0) == 2) {
                    revertImg_(gridView, shared, editor);
                }
            }
        });
    }
    private Handler hd;
    private void revertImg_(GridView gridView, SharedPreferences shared, SharedPreferences.Editor editor) {
        hd = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int num = shared.getInt("currnum",0);
                if(num == 2){
                    for(int i = 0 ; i < mGv.getChildCount();i++){
                        if(shared.getBoolean(Integer.toString(i),false)){
                            ImageView item = gridView.getChildAt(i).findViewById(R.id.iv_grid);
                            Glide.with(GameActivity.this).load(DERP).into(item);
                            int nums = shared.getInt("currnum",0);
                            nums -= 1;
                            editor.putInt("currnum",nums);
                            editor.putBoolean(Integer.toString(i),false);
                            editor.commit();
                        }
                    }
                }
            }
        };
        hd.postDelayed(runnable,500);
    }
    private void Initial() {
        this.icons = new ArrayList<>();
        this.icons.add(new Icon(0, "https://image.shutterstock.com/image-photo/image-great-egret-ardea-alba-600w-1968431356.jpg"));
        this.icons.add(new Icon(1, "https://image.shutterstock.com/image-photo/large-beautiful-drops-transparent-rain-600w-668593321.jpg"));
        this.icons.add(new Icon(2, "https://image.shutterstock.com/image-photo/european-british-robin-erithacus-rubecula-600w-1273577122.jpg"));
        this.icons.add(new Icon(3, "https://image.shutterstock.com/image-photo/red-avadavat-munia-strawberry-finch-600w-1862440066.jpg"));
        this.icons.add(new Icon(4, "https://image.shutterstock.com/image-photo/cockatoo-bird-perched-tree-branch-600w-1919227724.jpg"));
        this.icons.add(new Icon(5, "https://image.shutterstock.com/image-photo/seagull-perched-on-rock-during-600w-1887747280.jpg"));
        List<Icon> showIcon = new ArrayList<>();
        int[] seq = randomArray(0, 11, 12);
        Arrays.stream(seq).forEach(x -> showIcon.add(this.icons.get(x % 6)));

        this.shows = showIcon;
        SharedPreferences shared = getSharedPreferences("status", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        IntStream.range(0, 12).forEach(x -> editor.putBoolean(Integer.toString(x), false));
        editor.putInt("currnum", 0);
        editor.commit();


    }

    private int[] randomArray(int min, int max, int n) {
        int len = max - min + 1;
        if (max < min || n > len) {
            return null;
        }
        int[] source = new int[len];
        for (int i = min; i < min + len; i++) {
            source[i - min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            index = Math.abs(rd.nextInt() % len--);
            result[i] = source[index];
            source[index] = source[len];
        }
        return result;
    }

}