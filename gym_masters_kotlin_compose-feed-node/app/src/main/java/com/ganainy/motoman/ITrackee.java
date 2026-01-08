package com.ganainy.motoman;

import com.badlogic.gdx.math.Vector3;
import com.ganainy.motoman.track.TrackSegment;

public interface ITrackee {
	public void getTrackeePos(Vector3 vec);
	public void setLastTrackSegment(TrackSegment ts);
	public TrackSegment getLastTrackSegment();
}
