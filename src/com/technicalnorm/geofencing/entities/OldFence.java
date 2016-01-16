package com.technicalnorm.geofencing.entities;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Deprecated
public class OldFence implements Shape {
	
	private Coordinate center;
	private String id;
	private Rectangle2D.Double boundingBox;
	
	private List<Coordinate> points = new ArrayList<>();
	
	public OldFence(List<Coordinate> points, String name, Coordinate center) throws FenceValidationException
	{
		this.id = name;
		this.points = points;
		this.center = center;
		calculateBoundingBox();
		validate();
	}
	
	public OldFence(List<Coordinate> points, String name) throws FenceValidationException
	{
		this.id = name;
		this.points = points;
		calculateBoundingBox();
		validate();
	}
	
	private void calculateBoundingBox() {
		double maxX = points.parallelStream().mapToDouble(Coordinate::getX).max().getAsDouble();
		double minX = points.parallelStream().mapToDouble(Coordinate::getX).min().getAsDouble();
		double maxY = points.parallelStream().mapToDouble(Coordinate::getY).max().getAsDouble();
		double minY = points.parallelStream().mapToDouble(Coordinate::getY).min().getAsDouble();
		this.boundingBox = new Rectangle2D.Double(minX, maxY, Math.abs(maxX - minX), Math.abs(maxY - minY));
	}
	
	public Coordinate getCenter()
	{
		return this.center;
	}
	
	public void validate() throws FenceValidationException
	{
		
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Iterator<Line2D> buildLineIterator() {
		return new Iterator<Line2D>() {
			
			private int index = 0;

			@Override
			public boolean hasNext() {
				return points.size() > 1 && index >= points.size();
			}

			@Override
			public Line2D next() {
				if (index >= points.size()) {
					throw new NoSuchElementException();
				}
				Coordinate first = points.get(index);
				Coordinate second = points.get((index < points.size()-1)?index+1:0);
				index++;
				// Increment index for second point.
				return new Line2D.Double(first.getX(), first.getY(), second.getX(), second.getY());
			}
			
		};
	}
	
	@Override
	public Rectangle getBounds() {
		return null;
	}

	/**
	 *  Rectangle that completely encloses the shape.
	 */
	@Override
	public Rectangle2D getBounds2D() {
		return boundingBox;
	}

	@Override
	public boolean contains(double x, double y) {
		if (!boundingBox.contains(x, y)) {
			return false;
		}
		Line2D.Double ray = new Line2D.Double(x, y, boundingBox.getMaxX() + 1.0, boundingBox.getMaxY() + 1.0);
		long count = StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.buildLineIterator(), Spliterator.IMMUTABLE), true)
			.filter(ray::intersectsLine)
			.count();
		if (count % 2 == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return contains(new Rectangle2D.Double(x, y, w, h));
	}

	@Override
	public boolean contains(Rectangle2D r) {
		double x = r.getX();
		double y = r.getY();
		double h = r.getHeight();
		double w = r.getWidth();
		if (contains(x, y) // Upper left
				&& contains(x + w, y) // Upper right
				&& contains(x, y-h) // Lower left
				&& contains(x+w, y-h)) { // Lower right
			// All four points.  Check the edges.
			if (StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.buildLineIterator(), Spliterator.IMMUTABLE), true)
				.anyMatch(r::intersectsLine)) {
				// Line crosses over.
				return false;
			} else {
				return true;
			}
		}
		return false; // Line outside
		
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return null;
	}

}
