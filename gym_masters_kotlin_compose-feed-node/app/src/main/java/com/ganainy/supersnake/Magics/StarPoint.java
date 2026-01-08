package com.ganainy.supersnake.Magics;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ganainy.supersnake.Colors;
import com.ganainy.supersnake.Magic;
import com.ganainy.supersnake.Snake;

public class StarPoint extends Magic {
    public StarPoint(float x, float y) {
        super(x, y, "star.png");
    }

    public boolean draw() {
        rotation = drawn % 360;

        return super.draw();
    }

    public void action(Snake snake, Array<Magic> magics) {
        snake.point++;

        if (snake.color != Colors.snake) {
            snake.point++;
        }

        snake.color = Colors.snake;
        snake.addTail();
    }

    public boolean iter() {
        return true;
    }
}
