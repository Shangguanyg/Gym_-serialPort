package com.ganainy.FishingGame;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FishingActivity extends AppCompatActivity{

    private FishingGameView gameView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new FishingGameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理游戏视图资源
        if (gameView != null) {
            // 如果需要，可以在这里添加清理逻辑
        }
    }

}
