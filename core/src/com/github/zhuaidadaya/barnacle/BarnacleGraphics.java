package com.github.zhuaidadaya.barnacle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.github.zhuaidadaya.barnacle.option.SimpleOption;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static com.github.zhuaidadaya.barnacle.storage.Variables.*;

public class BarnacleGraphics extends ApplicationAdapter {
    Stage stage;
    SpriteBatch batch;
    //    Texture img;
    //    Texture img2;
    //    Texture button_default;
    //    Texture button_normal;
    //    Texture button_down;
    BitmapFont messageDrawer;
    BitmapFont titleDrawer;
    BitmapFont tipDrawer;
    FreeTypeFontGenerator generator;
    LinkedHashSet<TextButton> buttons;
    ProgressBar waitProgress;

    long lastOption = - 1;

    @Override
    public void create() {
        batch = new SpriteBatch();
        stage = new Stage(new StretchViewport(frameWidth, frameHeight), batch);

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = resourceJson.toString();
        parameter.size = 16;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("format/font/font.ttf"));

        messageDrawer = generator.generateFont(parameter);
        messageDrawer.getData().setScale(1.1f);
        messageDrawer.setColor(new Color(0, 0, 0, 100));

        titleDrawer = generator.generateFont(parameter);
        titleDrawer.getData().setScale(1.3f);
        titleDrawer.setColor(new Color(0, 0, 0, 100));

        tipDrawer = generator.generateFont(parameter);
        tipDrawer.setColor(new Color(1, 1, 1, 100));

        textureAtlas.put("background_default", new Texture(drawBackground));
        textureAtlas.put("background_dialog", new Texture("textures/bg/background2.png"));
        Gdx.graphics.setForegroundFPS(360);
        Gdx.graphics.setVSync(false);
        Gdx.input.setInputProcessor(stage);

        textureAtlas.put("button_default", new Texture("textures/button/button_default.png"));
        textureAtlas.put("button_normal", new Texture("textures/button/button.png"));
        textureAtlas.put("button_down", new Texture("textures/button/button_down.png"));

        ProgressBar.ProgressBarStyle progressStyle = new ProgressBar.ProgressBarStyle();

        progressStyle.background = new TextureRegionDrawable(new Texture("textures/progress/waiting.png"));

        waitProgress = new ProgressBar(0, 10000, 1, false, progressStyle);
    }

    public void applyButtons(boolean update) {
        buttons = new LinkedHashSet<>();

        int y = 180 + 50;

        if(update) {
            LinkedHashMap<TextButton, SimpleOption> clone = optionsButtonMap;
            optionsButtonMap = new LinkedHashMap<>();

            for(TextButton button : clone.keySet()) {
                TextButton.TextButtonStyle style = button.getStyle();
                style.up = new TextureRegionDrawable(new TextureRegion(! clone.get(button).equals(willOption) ? getTexture("button_normal") : getTexture("button_default")));

                button.setStyle(style);

                buttons.add(button);
                optionsButtonMap.put(button, clone.get(button));
            }
        } else {
            for(SimpleOption option : optionsButton) {
                TextButton button;
                y -= 50;
                TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();

                //            style.up = new TextureRegionDrawable(new TextureRegion(new Texture("textures/button/button.png")));
                style.up = new TextureRegionDrawable(new TextureRegion(! option.equals(willOption) ? getTexture("button_normal") : getTexture("button_default")));
                style.down = new TextureRegionDrawable(new TextureRegion(getTexture("button_down")));
                style.font = tipDrawer;

                button = new TextButton(formatConstant(option.formatInvoke()), style);

                button.setPosition(frameWidth - 360, y);
                button.setSize(240, 40);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        SimpleOption option1 = optionsButtonMap.get(button);
                        trend.initTrend(option1.trendName(), option1.trend());
                        optionsButtonMap = new LinkedHashMap<>();
                    }
                });
                buttons.add(button);
                optionsButtonMap.put(button, option);
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 0);

        try {
            if(trend.optionWaiting()) {
                if(optionsButton.size() != optionsButtonMap.size()) {
                    applyButtons(false);
                }

                for(TextButton b : buttons) {
                    stage.addActor(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(trend.optionWaiting()) {
            stage.draw();
            stage.act();
        } else {
            stage.clear();
        }

        batch.begin();
        batch.draw(getTexture("background_default"), 0, 50, frameWidth, frameHeight - (50 * 2));
        batch.draw(getTexture("background_dialog"), 0, 50, frameWidth, 150);
        try {
            messageDrawer.draw(batch, formatConstant(drawPlotMessage), 30, 80 + 50);
            titleDrawer.draw(batch, formatConstant(drawPlotTitle), 10, 120 + 50);
            tipDrawer.draw(batch, drawPlotTip, frameWidth - 120, 20);
        } catch (Exception e) {

        }

        try {
            if(trend.optionWaiting()) {
                for(TextButton b : buttons) {
                    b.draw(batch, 100);
                }
            }
        } catch (Exception e) {

        }
        //        if(img != null)
        //            batch.draw(img, 0, 0);

        if(trend.optionWaiting()) {
            if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if(System.currentTimeMillis() - lastOption > 300) {
                    lastOption = System.currentTimeMillis();
                    Object[] options = optionsButton.toArray();
                    if((willOptionIndex + 1) < options.length) {
                        willOptionIndex++;
                    } else {
                        willOptionIndex = 0;
                    }
                    willOption = (SimpleOption) options[willOptionIndex];

                    applyButtons(true);

                    System.gc();
                }
            } else {
                if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    if(System.currentTimeMillis() - lastOption > 300) {
                        lastOption = System.currentTimeMillis();
                        Object[] options = optionsButton.toArray();
                        if((willOptionIndex + 1) > options.length - 1) {
                            willOptionIndex = 0;
                        } else {
                            willOptionIndex++;
                        }
                        willOption = (SimpleOption) options[willOptionIndex];

                        applyButtons(true);

                        System.gc();
                    }
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                trendWillOption();
            }
        } else {
            if(Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    trend.trendSkip();
                } else {
                    trend.trendContinue();
                }
            }
        }

        batch.end();

        System.gc();
    }

    @Override
    public void dispose() {
        batch.dispose();
        //        img.dispose();
        System.exit(0);
    }
}
