package me.wattguy.snake.objects;

import java.util.ArrayList;
import java.util.List;

import me.wattguy.snake.Config;
import me.wattguy.snake.Main;
import me.wattguy.snake.enums.Direction;
import me.wattguy.snake.enums.Reason;
import me.wattguy.snake.infos.MoveResponse;
import me.wattguy.snake.utils.buttons.GButtons;
import me.wattguy.snake.utils.Utils;
import me.wattguy.snake.view.Game;

public class Snake {

    public List<Dot> dots = new ArrayList<>();

    public Direction d = Direction.RIGHT;
    private Game game = Game.getInstance();

    private float time;
    private float every;

    public Snake(int x, int y){

        time = 0f;
        every = 0.194f;

        for (int i = x; i >= x - 2; i--) {

            dots.add(Game.dots.get(Game.get(i, y)));

        }

        System.out.println(dots.size() + " blocks snake");

        GButtons.rectangleUpdate(this);

    }

    public void leftTurn(){

        if (d == Direction.UP){

            d = Direction.LEFT;

        }else if (d == Direction.DOWN){

            d = Direction.RIGHT;

        }else if (d == Direction.LEFT){

            d = Direction.DOWN;

        }else {

            d = Direction.UP;

        }

    }

    public void rightTurn(){

        if (d == Direction.UP){

            d = Direction.RIGHT;

        }else if (d == Direction.DOWN){

            d = Direction.LEFT;

        }else if (d == Direction.LEFT){

            d = Direction.UP;

        }else /*right*/{

            d = Direction.DOWN;

        }

    }

    public void setDirection(Direction d){

        if (dots.size() > 1){

            if ((this.d == Direction.UP && d == Direction.DOWN) || (this.d == Direction.DOWN && d == Direction.UP)){

                return;

            }else if ((this.d == Direction.RIGHT && d == Direction.LEFT) || (this.d == Direction.LEFT && d == Direction.RIGHT)){

                return;

            }

        }

        this.d = d;

    }

    public void draw() {
        for (int i = 0; i <= this.dots.size() - 1; i++) {
            Dot d = (Dot) this.dots.get(i);
            if (d != null) {
                if (i == 0) {
                    d.drawHead();
                } else if (i != this.dots.size() - 1) {
                    d.drawBody(i);
                } else {
                    d.drawTail();
                }
            }
        }
    }

    public MoveResponse move(float delta) {

        if (!Game.PAUSED && !Game.DIED) {
            time += delta;

            if (time < every) {
                return new MoveResponse(false, null);
            }

            time = 0.0f;
            Dot g = Utils.next(dots.get(0), d);

            if (g == null) {
                return new MoveResponse(false, Reason.DIED);
            }

            if (dots.contains(g) || Game.bricks.contains(g)) {
                return new MoveResponse(false, Reason.DIED);
            }

            dots.add(0, g);

            GButtons.rectangleUpdate(this);

            Dot c = Game.c.get();
            if (c != null && c.equals(g)){

                Config.setCoins(Config.COINS + 1);
                Main.actionSound();
                Game.c.set();

            } else if (Game.a.get().equals(g)) {

                every = Math.round(200 - (dots.size() * 1.3f)) * 0.001f;
                Main.actionSound();
                Game.c.check();

                return new MoveResponse(true, Reason.APPLE);
            }

            dots.remove(dots.get(dots.size() - 1));
            return new MoveResponse(true, null);

        } else if (Game.DIED) {

            return new MoveResponse(false, Reason.DIED);

        } else {

            return new MoveResponse(false, Reason.PAUSED);

        }
    }

}
