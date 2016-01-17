package com.alnormous.geofencing.test.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.alnormous.geofencing.entities.Coordinate;
import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.entities.FenceValidationException;
import com.alnormous.geofencing.selection.QuadTreeFenceSelector;

public class QuadTreeTest {

	@Test
	public void testQuadTree() throws FenceValidationException {
		
		/* -2   -1   0    1   2
		 * .    .    .___________
		 *            |         |
		 * .    .     |         |
		 *      /\__  |         |
		 * .   /    \_|         |
		 *    /       |     2   |
		 * . |    1   |         |
		 *   /        |         |
		 * ./_________|_________|
		 */
		List<Coordinate> points = new ArrayList<>();
		points.add(new Coordinate(-1, 1));
		points.add(new Coordinate(0,0));
		points.add(new Coordinate(0,-2));
		points.add(new Coordinate(-2, -2));
		
		Fence fence = new Fence(points, "fence1");
		
		List<Coordinate> points2 = new ArrayList<>();
		points2.add(new Coordinate(0,2));
		points2.add(new Coordinate(0, -2));
		points2.add(new Coordinate(2, -2));
		points2.add(new Coordinate(2, 2));
		Fence fence2 = new Fence(points2, "fence2");
		
		Set<Fence> fences = new HashSet<>();
		fences.add(fence);
		fences.add(fence2);
		
		QuadTreeFenceSelector selector = new QuadTreeFenceSelector(fences, new Coordinate(-2,2), new Coordinate(2,-2));
		
		Assert.assertEquals("fence2", selector.selectFence(new Coordinate(1,0)).get().getId());
		Assert.assertEquals("fence1", selector.selectFence(new Coordinate(-1,-1)).get().getId());
		Assert.assertEquals("fence2", selector.selectFence(new Coordinate(1, 1)).get().getId()); // On the fence
		Assert.assertNull("", selector.selectFence(new Coordinate(-1, 2))); // No boundary available
		
	}
	
	
}
