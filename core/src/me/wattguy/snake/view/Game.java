package me.wattguy.snake.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.wattguy.snake.Config;
import me.wattguy.snake.Info;
import me.wattguy.snake.Main;
import me.wattguy.snake.enums.Control;
import me.wattguy.snake.enums.Direction;
import me.wattguy.snake.enums.Reason;
import me.wattguy.snake.infos.MoveResponse;
import me.wattguy.snake.objects.Apple;
import me.wattguy.snake.objects.Coin;
import me.wattguy.snake.objects.Dot;
import me.wattguy.snake.objects.Snake;
import me.wattguy.snake.utils.TouchHandler;
import me.wattguy.snake.utils.buttons.GButtons;
import me.wattguy.snake.utils.RoundedShapeRenderer;
import me.wattguy.snake.utils.Pair;
import me.wattguy.snake.utils.ui.GUI;
import me.wattguy.snake.utils.Utils;

public class Game implements Screen {

    public RoundedShapeRenderer srender;
    public SpriteBatch batch;
    public GUI GUI;

    public static Texture coin;

    //public static Color LIGHT_GREEN = Color.BLACK;
   // public static Color GREEN = Utils.toRGB(15, 15, 15)/*Utils.toRGB(162, 209, 73)*/;

    public static Boolean DIED = false;
    public static Boolean WON = false;
    public static Boolean PAUSED = false;

    public static List<Dot> bricks = new ArrayList<>();
    public static Snake s;
    public static Apple a;
    public static Coin c;

    public static HashMap<String, Dot> dots = new HashMap<>();

    private static Game instance;

    @Override
    public void show() {
        instance = this;

        GButtons.initialize();

        c = null;
        bricks.clear();

        srender = new RoundedShapeRenderer();
        coin = new Texture(Gdx.files.internal("sprites/coin.png"));
        batch = new SpriteBatch();
        GUI = new GUI();

        if (dots.size() == 0){
            long time = System.currentTimeMillis();

            for (int x = 0; x <= Info.WIDTH_SIZE - 1; x++) {

                for (int y = 0; y <= Info.HEIGHT_SIZE - 1; y++) {

                    dots.put(get(x, y), new Dot((Integer) x, (Integer) y));

                }

            }

            System.out.println(dots.size() + " blocks made by " + (System.currentTimeMillis() - time) + "ms");
        }

        Gdx.input.setInputProcessor(new TouchHandler(new TouchHandler.DListener() {

                    @Override
                    public void onSwipeLeft() {
                        if (Config.CONTROL != Control.SWIPES) return;

                        s.setDirection(Direction.LEFT);
                    }

                    @Override
                    public void onSwipeRight() {
                        if (Config.CONTROL != Control.SWIPES) return;

                        s.setDirection(Direction.RIGHT);
                    }

                    @Override
                    public void onSwipeUp() {
                        if (Config.CONTROL != Control.SWIPES) return;

                        s.setDirection(Direction.UP);
                    }

                    @Override
                    public void onSwipeDown() {
                        if (Config.CONTROL != Control.SWIPES) return;

                        s.setDirection(Direction.DOWN);
                    }

                    @Override
                    public void onTouchMenu() {
                        Main.buttonSound();
                        Main.getInstance().setScreen(Main.menu);
                    }

                    @Override
                    public void onTouchPause() {
                        Main.buttonSound();
                        Game.PAUSED = true;
                    }

                    @Override
                    public void onTouchLeft() {
                        if (Config.CONTROL != Control.RELATIVITY) return;

                        s.leftTurn();
                    }

                    @Override
                    public void onTouchRight() {
                        if (Config.CONTROL != Control.RELATIVITY) return;

                        s.rightTurn();

                    }

                    @Override
                    public Boolean onTouch() {

                        if (DIED || WON){

                            DIED = false;
                            WON = false;

                            renewGame();

                            return true;

                        }else if (PAUSED){

                            PAUSED = false;
                            return true;

                        }

                        return false;

                    }

                })
        );

    }

    public static Game getInstance() {
        return instance;
    }

    public static String get(int x, int y){
        return x + ":" + y;
    }

    public void renewGame(){

        Game.s = new Snake(4, 13);
        spawnBricks();
        Game.c = new Coin();
        Game.a = new Apple();

    }

    /*private void drawBackground(){

        for(int x = 1; x <= Info.WIDTH_SIZE; x++){
            boolean evenx = (x % 2 == 0);

            for(int y = 1; y <= Info.HEIGHT_SIZE; y++){
                System.out.println((x - 1) + ":" + (y - 1));
                Pair p = Utils.crdsToReal(x - 1, y - 1);
                boolean eveny = (y % 2 == 0);

                Color c = GREEN;
                if (evenx && eveny){

                    c = LIGHT_GREEN;

                }else if (evenx && !eveny){

                    c = GREEN;

                }else if (!evenx && eveny){

                    c = GREEN;

                }else if (!evenx && !eveny){

                    c = LIGHT_GREEN;

                }

                srender.setColor(c);
                srender.rect((Float) p.first(), (Float) p.second(), Info.BLOCK_WIDTH, Info.BLOCK_HEIGHT);

            }

        }

    }*/

    private void spawnBricks(){
        bricks.clear();
        List<Dot> dots = Utils.getWithout();
        dots.remove(Game.dots.get(get(5, 13)));
        dots.remove(Game.dots.get(get(6, 13)));
        dots.remove(Game.dots.get(get(7, 13)));
        dots.remove(Game.dots.get(get(8, 13)));

        for (int i = 0; i <= Utils.randInt(5, 12); i++){
            Dot d = dots.get(Utils.randInt(0, dots.size() - 1));

            dots.remove(d);
            dots.remove(Game.dots.get(get(d.getXCoordinate() - 1, d.getYCoordinate())));
            dots.remove(Game.dots.get(get(d.getXCoordinate() + 1, d.getYCoordinate())));

            bricks.add(d);

        }

    }

    public void update(float delta){

        MoveResponse r = s.move(delta);

        if (r.isMoved() && r.getReason() == Reason.APPLE){

            if (!a.random()){

                WON = true;

            }

        }else if (!r.isMoved()){

            if (r.getReason() == Reason.DIED){

                if (s.dots.size() > Config.RECORD){

                    Config.setRecord(s.dots.size());

                }

                DIED = true;

            }

        }

    }

    public void drawBorders(){

        Pair right_bottom = Utils.modify(Utils.crdsToReal(Info.WIDTH_SIZE, 0), 0, -3);
        Pair left_bottom = Utils.modify(Utils.crdsToReal(0, 0), -3, -3);

        Pair right_top = Utils.crdsToReal(Info.WIDTH_SIZE, Info.HEIGHT_SIZE);
        Pair left_top = Utils.modify(Utils.crdsToReal(0, Info.HEIGHT_SIZE), -3, 0);


        srender.setColor(Color.RED);
        Utils.border(left_top, right_top, 3, Direction.UP);
        Utils.border(right_top, right_bottom, 3, Direction.RIGHT);
        Utils.border(right_bottom, left_bottom, 3, Direction.DOWN);
        Utils.border(left_bottom, left_top, 3, Direction.LEFT);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (!GUI.COUNTING && !WON){

            update(delta);

        }

        batch.begin();
        try {
            Game.c.draw(delta);
        }catch(Exception ignored){}
        batch.end();

        srender.begin(ShapeRenderer.ShapeType.Filled);
        //drawBackground();
        drawBorders();

        if (!GUI.COUNTING){


            Game.a.draw(delta);

            for (Dot d : Game.bricks){

                d.drawBrick();

            }

            Game.s.draw();

        }

        GButtons.draw();
        srender.end();

        GUI.draw(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        PAUSED = true;
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Game.DIED = false;
        Game.PAUSED = false;

        Game.s = null;
        Game.a = null;
    }

    @Override
    public void dispose() {
        coin.dispose();
        srender.dispose();
        batch.dispose();
        GButtons.dispose();
        GUI.dispose();
    }

}
