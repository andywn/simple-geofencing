package com.alnormous.geofencing.entities;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Point;

public class Coordinate extends com.vividsolutions.jts.geom.Coordinate {
	
	private static final long serialVersionUID = 1107521497407106487L;

	private Point point;
	
	private static Double EPISLON = 0.0001; // 11.13m on equator
	
	public Coordinate(double x, double y)
	{
		super(x, y);
		
	}
	
	private void updatePoint() {
		JTSFactoryFinder.getGeometryFactory().createPoint(this);
	}
	
	
	public double getX() {
		return this.getOrdinate(0);
	}
	public void setX(double x) {
		this.setOrdinate(0, x);
		updatePoint();
	}
	
	public double getY() {
		return this.getOrdinate(1);
	}
	public void setY(double y) {
		this.setOrdinate(1, y);
		updatePoint();
	}

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		updatePoint();
	}
	
	/**
	 * Center of a rectangle formed by two points.
	 * @param otherCorner
	 * @return
	 */
	public Coordinate getCenter(Coordinate otherCorner) {
		// X coord.
		double xdiff = Math.abs(otherCorner.x - this.x)/2.0D;
		double x;
		if (otherCorner.x > this.x) {
			x = this.x + xdiff;
		} else {
			x = otherCorner.x + xdiff;
		}
		double ydiff = Math.abs(otherCorner.y - this.y)/2.0D;
		double y;
		if (otherCorner.y > this.y) {
			y = this.y + ydiff;
		} else {
			y = otherCorner.y + ydiff;
		}
		return new Coordinate(x, y);
	}
	
	/**
	 * What direction is this coordinate from a centre coordinate's perspective?
	 * @param centre
	 * @return
	 */
	public Quadrant directionFrom(Coordinate centre) {
		if (this.x >= centre.x) {
			if (this.y >= centre.y) {
				return Quadrant.ABOVE_RIGHT;
			} else {
				return Quadrant.BELOW_RIGHT;
			}
		} else {
			if (this.y >= centre.y) {
				return Quadrant.ABOVE_LEFT;
			} else {
				return Quadrant.BELOW_LEFT;
			}
		}
	}
	
	public boolean isPointInFence(Fence fence) {
		return fence.getPolygon().contains(this.point);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Coordinate)) {
			return false;
		}
		Coordinate coord = (Coordinate)other;
		return Math.abs(this.getX() - coord.getX()) < EPISLON
				&& Math.abs(this.getY() - coord.getY()) < EPISLON;
	}

	/**
	 * This could be improved
	 * @return
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = (int)Math.round(getX());
		result = prime * result + (int)Math.round(getY());
		return result;
	}
	
	
	
}
