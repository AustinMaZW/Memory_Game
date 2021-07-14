package com.example.memorygame;

import android.content.Context;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.example.memorygame.GameViewAdaptor.DERP;

public class FlipRunnable implements Runnable {
    private List<Boolean> isFlip;
    private Map<Integer,Icon> answer;
    private GridView gridView;
    private Context context;
    private List<Boolean> isRight;
    private Integer currImageNum;
    private ICurrImageNum iCurrImageNum;
    public interface ICurrImageNum{
        int getCurrImageNum();
        void setCurrImageNum(int num);
    }


    public FlipRunnable(List<Boolean> isFlip,List<Boolean> isRight, Map<Integer,Icon> answer, GridView gridView, Context context){
        this.answer = answer;
        this.isFlip = isFlip;
        this.gridView = gridView;
        this.context = context;
        this.isRight = isRight;
        this.iCurrImageNum = (ICurrImageNum) context;
    }
    @Override
    public void run() {
        List<Integer> curr = new ArrayList<>();
        IntStream.range(0,this.isFlip.size()).filter(x -> this.isFlip.get(x) && !this.isRight.get(x)).forEach(x->curr.add(x));
        if(curr.size() % 2 == 0){
            if(this.answer.get(curr.get(0)).getId() != this.answer.get(curr.get(1)).getId()){
                curr.stream().forEach(x->{
                    ImageView imageView = this.gridView.getChildAt(x).findViewById(R.id.iv_grid);
                    Glide.with(context).load(DERP).into(imageView);
                });
                curr.stream().forEach(x->this.isFlip.set(x,false));
            }else {
                curr.stream().forEach(x->this.isRight.set(x,true));
            }
        }
        iCurrImageNum.setCurrImageNum(0);
    }
}
