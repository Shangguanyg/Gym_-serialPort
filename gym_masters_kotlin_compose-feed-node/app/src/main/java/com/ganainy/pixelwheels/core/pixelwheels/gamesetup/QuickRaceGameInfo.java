package com.ganainy.pixelwheels.core.pixelwheels.gamesetup;

import com.ganainy.pixelwheels.core.pixelwheels.GameConfig;
import com.ganainy.pixelwheels.core.pixelwheels.map.Track;
import com.ganainy.pixelwheels.core.pixelwheels.vehicledef.VehicleDef;
import com.badlogic.gdx.utils.Array;

public class QuickRaceGameInfo extends GameInfo {
    private Track mTrack;

    public QuickRaceGameInfo() {
        super(GameType.QUICK_RACE);
    }

    public static class Builder extends GameInfo.Builder<QuickRaceGameInfo> {
        private Track mTrack;

        public Builder(Array<VehicleDef> vehicleDefs, GameConfig gameConfig) {
            super(vehicleDefs, gameConfig);
        }

        public void setTrack(Track track) {
            mTrack = track;
            mGameConfig.track = mTrack.getId();
            mGameConfig.flush(GameConfig.ConfigGroup.OTHER);
        }

        @Override
        public QuickRaceGameInfo build() {
            QuickRaceGameInfo gameInfo = new QuickRaceGameInfo();
            gameInfo.mTrack = mTrack;
            createEntrants(gameInfo);
            return gameInfo;
        }
    }

    @Override
    public Track getTrack() {
        return mTrack;
    }
}
