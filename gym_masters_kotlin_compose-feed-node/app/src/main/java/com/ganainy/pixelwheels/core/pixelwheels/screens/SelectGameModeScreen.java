/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ganainy.pixelwheels.core.pixelwheels.screens;

import com.ganainy.pixelwheels.core.pixelwheels.GameConfig;
import com.ganainy.pixelwheels.core.pixelwheels.PwGame;
import com.ganainy.pixelwheels.core.pixelwheels.PwRefreshHelper;
import com.ganainy.pixelwheels.core.pixelwheels.gamesetup.GameMode;
import com.ganainy.pixelwheels.core.ui.anchor.AnchorGroup;
import com.ganainy.pixelwheels.core.ui.menu.Menu;
import com.ganainy.pixelwheels.core.ui.menu.MenuItem;
import com.ganainy.pixelwheels.core.ui.menu.MenuItemListener;
import com.ganainy.pixelwheels.core.ui.uibuilder.UiBuilder;
import com.ganainy.pixelwheels.core.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/** Select between quick race, championship... */
public class SelectGameModeScreen extends PwStageScreen {
    private final PwGame mGame;
    private final int mPlayerCount;

    public SelectGameModeScreen(PwGame game, int playerCount) {
        super(game.getAssets().ui);
        mGame = game;
        mPlayerCount = playerCount;
        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectGameModeScreen(mGame, mPlayerCount));
            }
        };
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().ui.atlas, mGame.getAssets().ui.skin);
        DifficultySelectorController.registerFactory(builder, mGame.getConfig());

        AnchorGroup root =
                (AnchorGroup) builder.build(FileUtils.assets("screens/selectgamemode.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");
        builder.getActor("quickRaceButton")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                mGame.getConfig().gameMode = GameMode.QUICK_RACE;
                                mGame.getConfig().flush(GameConfig.ConfigGroup.OTHER);
                                mGame.showQuickRace(mPlayerCount);
                            }
                        });
        MenuItem championshipItem = builder.getMenuItem("championshipButton");
        championshipItem.addListener(
                new MenuItemListener() {
                    @Override
                    public void triggered() {
                        mGame.getConfig().gameMode = GameMode.CHAMPIONSHIP;
                        mGame.getConfig().flush(GameConfig.ConfigGroup.OTHER);
                        mGame.showChampionship(mPlayerCount);
                    }
                });
        if (mGame.getConfig().gameMode == GameMode.CHAMPIONSHIP) {
            menu.setCurrentItem(championshipItem);
        }

        menu.addBackButton()
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }
}
