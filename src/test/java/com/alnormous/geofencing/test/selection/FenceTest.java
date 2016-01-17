package com.alnormous.geofencing.test.selection;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alnormous.geofencing.entities.Coordinate;
import com.alnormous.geofencing.entities.Fence;
import com.alnormous.geofencing.entities.FenceValidationException;

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
		points.add(new Coordinate(-1.0, 1.0));
		points.add(new Coordinate(0.0,0));
		points.add(new Coordinate(1.0, 2.0));
		points.add(new Coordinate(1.0,-2.0));
		points.add(new Coordinate(-2.0, -2.0));
		
		Fence fence = new Fence(points, "fence1");
		
		return fence;
		
	}
	
//	@Test
//	public void testBoudingBox() throws Exception {
//		Fence f = generateFence();
//		Assert.assertEquals(-2.00, f.getEnvelope()., 0.1);
//		Assert.assertEquals(2.00, f.getBounds2D().getY(), 0.1);
//		Assert.assertEquals(4.00, f.getBounds2D().getHeight(), 0.1);
//		Assert.assertEquals(3.00, f.getBounds2D().getWidth(), 0.1);
//	}
//	
//	@Test
//	public void testBoudingBox2() throws Exception {
//		Fence f = generateFence();
//		Assert.assertEquals(-2.00, f.getBounds2D().getX(), 0.1);
//		Assert.assertEquals(2.00, f.getBounds2D().getY(), 0.1);
//		Assert.assertEquals(4.00, f.getBounds2D().getHeight(), 0.1);
//		Assert.assertEquals(3.00, f.getBounds2D().getWidth(), 0.1);
//		Assert.assertTrue(f.getBounds2D().contains(new Coordinate(0.0, 0.0)));
//	}
	
	@Test
	public void testPointsIn() throws Exception {
		
		Fence f = generateFence();
		//Assert.assertTrue(f.contains(new Coordinate(0.0, 0.0))); // Point on boundary - known
		Assert.assertTrue(f.contains(new Coordinate(0.0, -1.1))); // Inside shape
		Assert.assertFalse(f.contains(new Coordinate(0.0, 1.0))); // Definitely outside.
		//Assert.assertTrue(f.contains(new Coordinate(-1.0, -2.0))); // On line, but not on point.  Should be in.
		Assert.assertFalse(f.contains(new Coordinate(-1.0, -2.01))); // Outside.
		//Assert.assertTrue(f.contains(new Coordinate(0.5, 1.0))); // On line on diagonal.
		
		
	}
	
//	@Test
//	public void testRectangleIn() throws Exception {
//		
//		Fence f = generateFence();
//		Assert.assertTrue(f.contains(new Rectangle2D.Double(-1.0, -1.0, 1.0, 0.5))); // All inside
//		Assert.assertTrue(f.contains(new Rectangle2D.Double(-1.0, -1.0, 1.0, 1.0))); // Touches bottom line.
//		Assert.assertFalse(f.contains(new Rectangle2D.Double(-2.0, -1.0, 1.0, 1.0))); // One point out
//		Assert.assertFalse(f.contains(new Rectangle2D.Double(-1.0, -0.1, 1.9, 2.0))); // All points in, but line goes out of shape
//		
//	}

}
