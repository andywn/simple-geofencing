package com.alnormous.geofencing.selection;

import java.util.Optional;

import com.alnormous.geofencing.entities.Coordinate;
import com.alnormous.geofencing.entities.Fence;

public interface FenceSelector {
	
	public Optional<Fence> selectFence(Coordinate coord);

}
