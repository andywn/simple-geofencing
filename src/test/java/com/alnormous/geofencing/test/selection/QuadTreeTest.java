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
		points.add(new Coordinate(-1.001, 1.001));
		points.add(new Coordinate(0.001,0.001));
		points.add(new Coordinate(0.001,-2.001));
		points.add(new Coordinate(-2.001, -2.001));
		
		Fence fence = new Fence(points, "fence1", "fence1");
		
		List<Coordinate> points2 = new ArrayList<>();
		points2.add(new Coordinate(0.001,2.001));
		points2.add(new Coordinate(0.001, -2.001));
		points2.add(new Coordinate(2.001, -2.001));
		points2.add(new Coordinate(2.001, 2.001));
		Fence fence2 = new Fence(points2, "fence2", "fence2");
		
		Set<Fence> fences = new HashSet<>();
		fences.add(fence);
		fences.add(fence2);
		
		QuadTreeFenceSelector selector = new QuadTreeFenceSelector(fences, new Coordinate(-3.000,3.000), new Coordinate(3.000,-3.000));
		Assert.assertEquals("fence2", selector.selectFence(new Coordinate(1.000,0.000)).get().getName());
		Assert.assertEquals("fence1", selector.selectFence(new Coordinate(-1.000,-1.000)).get().getName());
		Assert.assertEquals("fence2", selector.selectFence(new Coordinate(1.000, 1.000)).get().getName()); // On the fence
		Assert.assertFalse(selector.selectFence(new Coordinate(-1.000, 2.000)).isPresent()); // No boundary available
		
	}
	
	
}
