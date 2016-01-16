package com.technicalnorm.geofencing.selection;

import java.util.Optional;

import com.technicalnorm.geofencing.entities.Coordinate;
import com.technicalnorm.geofencing.entities.Fence;

public interface FenceSelector {
	
	public Optional<Fence> selectFence(Coordinate coord);

}
