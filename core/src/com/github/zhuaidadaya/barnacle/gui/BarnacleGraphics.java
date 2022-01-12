package com.github.zhuaidadaya.barnacle.gui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.github.zhuaidadaya.barnacle.option.SimpleOption;
import org.json.JSONObject;

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
    BitmapFont pauseDrawer;
    BitmapFont shutdownDrawer;
    FreeTypeFontGenerator textGenerator;
    LinkedHashSet<TextButton> buttons;
    ProgressBar bar;

    long lastOption = - 1;
    long lastPause = - 1;
    long lastSwitchSlot = - 1;
    long lastSaveLevel = - 1;
    long lastViewLog = - 1;
    long rendingStart = - 1;

    @Override
    public void create() {
        loadTextures();

        batch = new SpriteBatch();
        stage = new Stage(new StretchViewport(frameWidth, frameHeight), batch);

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = resourceJson.toString();
        parameter.size = 16;

        textGenerator = new FreeTypeFontGenerator(Gdx.files.internal("format/font/bold.ttf"));

        messageDrawer = textGenerator.generateFont(parameter);
        messageDrawer.getData().setScale(1.1f);
        messageDrawer.setColor(new Color(0, 0, 0, 100));

        pauseDrawer = textGenerator.generateFont(parameter);
        pauseDrawer.getData().setScale(1.4f);
        pauseDrawer.setColor(new Color(1, 1, 1, 100));

        titleDrawer = textGenerator.generateFont(parameter);
        titleDrawer.getData().setScale(1.3f);
        titleDrawer.setColor(new Color(0, 0, 0, 100));

        shutdownDrawer = textGenerator.generateFont(parameter);
        shutdownDrawer.getData().setScale(1.5f);
        shutdownDrawer.setColor(new Color(1, 1, 1, 100));

        tipDrawer = textGenerator.generateFont(parameter);
        tipDrawer.setColor(new Color(1, 1, 1, 100));

        Gdx.graphics.setForegroundFPS(360);
        Gdx.graphics.setVSync(false);
        Gdx.input.setInputProcessor(stage);

        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = new TextureRegionDrawable(new Texture("textures/progress/waiting_background.png"));
        //        progressBarStyle.knob = new TextureRegionDrawable(new Texture("textures/progress/waiting.png"));
        progressBarStyle.knobBefore = new TextureRegionDrawable(new Texture("textures/progress/waiting.png"));
        bar = new ProgressBar(0f, 5000, 1f, false, progressBarStyle);
        bar.setPosition(0, 50);
        bar.setSize(frameWidth, 3);
        //        bar.setVisualInterpolation(Interpolation.exp10Out);
        stage.addActor(bar);

        launched = true;
    }

    public void loadTextures() {
        for(String s : texturesJson.keySet()) {
            textureAtlas.put(s, new Texture(texturesJson.getString(s)));
        }
    }

    public void applyButtons(boolean update) {
        buttons = new LinkedHashSet<>();

        int y = 180 + 50;

        if(update) {
            LinkedHashMap<TextButton, SimpleOption> clone = optionsButtonMap;
            optionsButtonMap = new LinkedHashMap<>();

            for(TextButton button : clone.keySet()) {
                TextButton.TextButtonStyle style = button.getStyle();
                style.up = new TextureRegionDrawable(new TextureRegion(getTexture(! clone.get(button).equals(willOption) ? "button_normal" : "button_default")));

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

                button.setPosition(frameWidth - 400, y);
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

        if(! shuttingDown) {
            if(! pause & ! rendingSaveLevel)
                preRendingGame();

            batch.begin();

            if(! pause & ! rendingSaveLevel)
                rendingGame();
            else
                rendingPause();
        } else {
            batch.begin();

            rendingShuttingDown();
        }

        batch.end();
    }

    public void rendingShuttingDown() {
        batch.draw(getTexture("black"), 0, 50, frameWidth, frameHeight - (50 * 2));
        shutdownDrawer.draw(batch, getFormat("rending.shutting.down").toString(), (frameWidth / 2f) - 100, frameHeight / 2f);
    }

    public void preRendingGame() {
        try {
            if(trend.optionWaiting()) {
                if(optionsButton.size() != optionsButtonMap.size()) {
                    applyButtons(false);
                }

                for(TextButton b : buttons) {
                    stage.addActor(b);
                }

                stage.act();
                stage.draw();
            } else {
                stage.clear();
            }
        } catch (Exception e) {

        }
    }

    public void rendingGame() {
        batch.draw(getTexture(drawBackground), 0, 50, frameWidth, frameHeight - (50 * 2));

        if(! viewingLog) {
            if(drawDialogBox) {
                batch.draw(getTexture("background_dialog"), 0, 50, frameWidth, 150);
                titleDrawer.draw(batch, drawPlotTitle, 10, 120 + 50);
            }
        } else {
            batch.draw(getTexture("background_dialog"), 0, 50, frameWidth, frameHeight - (50 * 2));

            int y = 80 + 50 + 50;
            for(int plotIndex = recodingPlots.size(); plotIndex > 0 & y < frameHeight; plotIndex--) {
                String plot = recodingPlots.get(plotIndex);
                if(! drawPlotMessage.equals(plot)) {
                    messageDrawer.draw(batch, plot, 30, y);
                    y += 50;
                }
            }
        }

        try {
            messageDrawer.draw(batch, drawPlotMessage, 30, 80 + 50);
            tipDrawer.draw(batch, drawPlotTip, 5, 20);
        } catch (Exception e) {

        }

        bar.setRange(0, waitingProgressBarMax);
        bar.setValue(waitingProgressBarValue);
        bar.draw(batch, 100);

        if(trend.optionWaiting()) {
            for(TextButton b : buttons) {
                b.draw(batch, 100);
            }

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
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                trendWillOption();
            }
        } else {
            if(Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    trend.trendSkip();
                } else {
                    trend.trendContinue();
                }
            } else {
                if(Gdx.input.isTouched()) {
                    trend.trendContinue();
                } else {
                    if(Gdx.input.isKeyPressed(Input.Keys.G)) {
                        if(System.currentTimeMillis() - lastViewLog > 300) {
                            lastViewLog = System.currentTimeMillis();
                            viewingLog = ! viewingLog;
                        }
                    }
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            if(System.currentTimeMillis() - lastPause > 300) {
                lastPause = System.currentTimeMillis();
                pause = true;
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.L)) {
            if(System.currentTimeMillis() - lastPause > 300) {
                lastPause = System.currentTimeMillis();
                lastSaveLevel = System.currentTimeMillis();
                rendingSaveLevel = true;
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            exit();
        }
    }

    public void rendingPause() {
        batch.draw(getTexture("pause"), 0, 50, frameWidth, frameHeight - (50 * 2));

        if(rendingSaveLevel) {
            try {
                pauseDrawer.draw(batch, formatLevels(getFormat("rending.save.options").toString()), 20, frameHeight - 55);
            } catch (Exception e) {

            }

            if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                if(System.currentTimeMillis() - lastPause > 300) {
                    lastPause = System.currentTimeMillis();
                    rendingSaveLevel = false;
                }
            }

            if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if(System.currentTimeMillis() - lastSwitchSlot > 200) {
                    lastSwitchSlot = System.currentTimeMillis();
                    if(selectedSlot > 0)
                        selectedSlot--;
                    else
                        selectedSlot = 9;
                }
            }

            if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if(System.currentTimeMillis() - lastSwitchSlot > 200) {
                    lastSwitchSlot = System.currentTimeMillis();
                    if(selectedSlot < 9)
                        selectedSlot++;
                    else
                        selectedSlot = 0;
                }
            }

            if(Gdx.input.isKeyPressed(Input.Keys.L)) {
                if(System.currentTimeMillis() - lastSaveLevel > 500) {
                    if(!savingSlot.contains(selectedSlot)) {
                        new Thread(() -> {
                            int selectedSlotCache = selectedSlot;
                            savingSlot.add(selectedSlotCache);
                            lastSaveLevel = System.currentTimeMillis();
                            levels.createLevel(String.valueOf(selectedSlotCache));
                            config.set("levels", levels.toJSONObject());
                            savingSlot.remove(selectedSlotCache);
                        }).start();
                    }
                }
            }

            if(Gdx.input.isKeyPressed(Input.Keys.R)) {
                jumpTrend(levels.getLevel(String.valueOf(selectedSlot)));
                rendingSaveLevel = false;
                pause = false;
            }
        } else {
            try {
                pauseDrawer.draw(batch, getFormat("rending.pause.options").toString(), 20, frameHeight - 55);
            } catch (Exception e) {

            }

            if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                if(System.currentTimeMillis() - lastPause > 300) {
                    lastPause = System.currentTimeMillis();
                    pause = false;
                }
            }

            if(Gdx.input.isKeyPressed(Input.Keys.E)) {
                exit();
            }

            if(Gdx.input.isKeyPressed(Input.Keys.L)) {
                lastSaveLevel = System.currentTimeMillis();
                pause = true;
                rendingSaveLevel = true;
            }
        }
    }

    @Override
    public void dispose() {
        config.shutdown();
        logger.info("stopping game");
        batch.dispose();
        stage.dispose();
        titleDrawer.dispose();
        tipDrawer.dispose();
        pauseDrawer.dispose();
        messageDrawer.dispose();
    }

    public void exit() {
        shuttingDown = true;
        running = false;
    }
}
