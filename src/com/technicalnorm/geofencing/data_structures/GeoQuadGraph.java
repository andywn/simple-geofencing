package com.technicalnorm.geofencing.data_structures;

import java.util.Map;
import java.util.Set;

import com.technicalnorm.geofencing.entities.Fence;

/**
 *
 * Geo fences - we break up into even squares, choosing a minimum size, and boundaries.
 * 
 * We know a quadrant is wholy within a fence if all four points are within the fence, and none of the four sides crosses any polygon edge.
 * We also know it's wholy within the fence if all four points lie within the fences inner circle.
 * 
 * How we're going to do this:
 * <ol>
 * <li>Fences added one at a time.  We look for nodes in the tree </li>
 * <li>Add a fence, and </li>
 * </ol>
 *
 */
public class GeoQuadGraph {
	
	private Map<Quadrant, Set<GeoQuadGraph>> subnodes;

	public GeoQuadGraph()
	{
		
	}
	
	public void addNode(GeoQuadGraph node)
	{
	}
	
	public void addFence(Fence fence)
	{
		
	}
	
}

enum Quadrant {
	ABOVE, BELOW, RIGHT, LEFT
}
