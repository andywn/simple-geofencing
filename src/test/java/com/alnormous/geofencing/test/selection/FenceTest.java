package com.alnormous.geofencing.test.selection;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alnormous.geofencing.entities.Coordinate;
import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.entities.FenceValidationException;
import com.vividsolutions.jts.geom.Envelope;

public class FenceTest {
	
	private Fence generateFence() throws FenceValidationException {
		
		/* -2   -1   0     1     2
		 * .    .    .     .     . 2
		 *                /|     
		 * .    .        / |     . 1
		 *      /\__    /  |   
		 * .   /    \_ /   |     . 0
		 *    /            |   
		 * . |    1        |     . -1
		 *   /             |     
		 * ./______________|     . -2
		 */
		List<Coordinate> points = new ArrayList<>();
		points.add(new Coordinate(-1.000, 1.000));
		points.add(new Coordinate(0.000,0.000));
		points.add(new Coordinate(1.000, 2.000));
		points.add(new Coordinate(1.000,-2.000));
		points.add(new Coordinate(-2.000, -2.000));
		
		Fence fence = new Fence(points, "fence1", "fence1");
		
		return fence;
		
	}
	
	@Test
	public void testPointsIn() throws Exception {
		Fence f = generateFence();
		Assert.assertTrue(f.contains(new Coordinate(0.000, -1.100))); // Inside shape
		Assert.assertTrue(f.contains(new Coordinate(-1.000, 0.999))); // Just inside
		Assert.assertTrue(f.contains(new Coordinate(0.499, -0.500))); // Just inside
	}
	
	@Test
	public void testPointsOut() throws Exception {
		Fence f = generateFence();
		Assert.assertFalse(f.contains(new Coordinate(0.000, 1.000))); // Definitely outside.
		Assert.assertFalse(f.contains(new Coordinate(-1.000, -2.010))); // Outside.
	}
	
	@Test
	public void testEnvelopeIn() throws Exception {
		Fence f = generateFence();
		Envelope envelope = new Envelope(new Coordinate(-1.000, -1.000), new Coordinate(-1.500, -1.500));
		Assert.assertTrue(f.contains(envelope)); // In
		Envelope envelope2 = new Envelope(new Coordinate(-2.000, 2.000), new Coordinate(-1.500, -1.800));
		Assert.assertFalse(f.contains(envelope2)); // Out
		Envelope envelope3 = new Envelope(new Coordinate(-2.000, 2.000), new Coordinate(0.000, 0.000));
		Assert.assertFalse(f.contains(envelope3)); // Overlaps, but doesn't contain.
		
	}
	
	@Test
	public void testEnvelopeOverlapping() throws Exception {
		Fence f = generateFence();
		Envelope envelope = new Envelope(new Coordinate(-1.000, -1.000), new Coordinate(-1.500, -1.500));
		Assert.assertTrue(f.overlapsOrContains(envelope)); // In
		Envelope envelope2 = new Envelope(new Coordinate(-2.000, 2.000), new Coordinate(-1.500, 1.800));
		Assert.assertFalse(f.overlapsOrContains(envelope2)); // Out
		Envelope envelope3 = new Envelope(new Coordinate(-2.000, 2.000), new Coordinate(0.000, 0.000));
		Assert.assertTrue(f.overlapsOrContains(envelope3)); // Overlaps, but doesn't contain.
	}
	
	@Test
	public void fenceTestOverlappingContainedWithin() throws Exception {
		List<Coordinate> points2 = new ArrayList<>();
		points2.add(new Coordinate(0.000,2.000));
		points2.add(new Coordinate(0.000, -2.000));
		points2.add(new Coordinate(2.000, -2.000));
		points2.add(new Coordinate(2.000, 2.000));
		Fence fence = new Fence(points2, "fence2", "fence2");
		Envelope envelope3 = new Envelope(new Coordinate(-3.000, 3.000), new Coordinate(3.000, -3.000));
		Assert.assertTrue(fence.overlapsOrContains(envelope3)); // Contained by envelope
		
	}

}
