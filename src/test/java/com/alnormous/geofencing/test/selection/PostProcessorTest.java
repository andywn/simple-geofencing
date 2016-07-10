package com.alnormous.geofencing.test.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.alnormous.geofencing.entities.Coordinate;
import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.selection.QuadTreeFenceSelector;

public class PostProcessorTest {
	
	@Test
	public void testContiguosFences()  throws Exception {
		/* -2   -1   0    1   2
		 * ______________________
		 * |    1     |         |
		 * |    .     |         |
		 * |    /\__  |         |
		 * |   /    \_|         |
		 * |  /       |     1   |
		 * | |    1   |         |
		 * | /        |         |
		 * |/_________|_________|
		 */
		// Slighly off exact numbers to avoid bad distance calculations in geotools.
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
		Fence fence2 = new Fence(points2, "fence1", "fence1");
		
		List<Coordinate> points3 = new ArrayList<>();
		points3.add(new Coordinate(-2.001, 2.001));
		points3.add(new Coordinate(0.001, 2.001));
		points3.add(new Coordinate(0.001, 0.001));
		points3.add(new Coordinate(-1.001, 1.001));
		points3.add(new Coordinate(-2.001, -2.001));
		Fence fence3 = new Fence(points3, "fence1", "fence1");
		
		Set<Fence> fences = new HashSet<>();
		fences.add(fence);
		fences.add(fence2);
		fences.add(fence3);
		
		QuadTreeFenceSelector selector = new QuadTreeFenceSelector(fences, new Coordinate(-2.000,2.000), new Coordinate(2.000,-2.000), 1);
		Fence f = selector.selectFence(new Coordinate(0.0,  0.0)).get();
		Assert.assertEquals(5, f.getPolygon().getCoordinates().length);
		for (int i = 0; i < f.getPolygon().getCoordinates().length; i++) {
			Assert.assertTrue(Math.abs(Math.abs(f.getPolygon().getCoordinates()[i].x) - 2.0) < 0.01);
			Assert.assertTrue(Math.abs(Math.abs(f.getPolygon().getCoordinates()[i].y) - 2.0) < 0.01);
		}
	}
	
	@Test
	public void testContiguosFencesOutsideQuadrant()  throws Exception {
		/* -3   -2   -1   0    1   2
		 * __________________________
		 * |        1     |         |
		 * |        .     |         |
		 * |        /\__  |         |
		 * |       /    \_|         |
		 * |      /       |     1   |
		 * |     |    1   |         |
		 * |     /        |         |
		 * |____/_________|_________|
		 */
		// Slighly off exact numbers to avoid bad distance calculations in geotools.
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
		Fence fence2 = new Fence(points2, "fence1", "fence1");
		
		List<Coordinate> points3 = new ArrayList<>();
		points3.add(new Coordinate(-3.001, 2.001));
		points3.add(new Coordinate(0.001, 2.001));
		points3.add(new Coordinate(0.001, 0.001));
		points3.add(new Coordinate(-1.001, 1.001));
		points3.add(new Coordinate(-2.001, -2.001));
		points3.add(new Coordinate(-3.001, -2.001));
		Fence fence3 = new Fence(points3, "fence1", "fence1");
		
		Set<Fence> fences = new HashSet<>();
		fences.add(fence);
		fences.add(fence2);
		fences.add(fence3);
		
		QuadTreeFenceSelector selector = new QuadTreeFenceSelector(fences, new Coordinate(-2.000,2.000), new Coordinate(2.000,-2.000), 1);
		Fence f = selector.selectFence(new Coordinate(0.0,  0.0)).get();
		Assert.assertEquals(5, f.getPolygon().getCoordinates().length);
		for (int i = 0; i < f.getPolygon().getCoordinates().length; i++) {
			System.out.println(f.getPolygon().getCoordinates()[i]);
			Assert.assertTrue(Math.abs(Math.abs(f.getPolygon().getCoordinates()[i].x) - 2.0) < 0.01);
			Assert.assertTrue(Math.abs(Math.abs(f.getPolygon().getCoordinates()[i].y) - 2.0) < 0.01);
		}
	}

}
