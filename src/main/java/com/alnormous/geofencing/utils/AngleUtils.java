package com.alnormous.geofencing.utils;

import java.util.List;
import java.util.function.ToDoubleBiFunction;

import com.alnormous.geofencing.entities.Coordinate;

public class AngleUtils {
	
	@FunctionalInterface
	interface ToDoubleTriFunction<T, U, V> {
	    public Double applyAsDouble(T t, U u, V v);
	}
	
	/**
	 * Calculate the length between two points
	 */
	public static ToDoubleBiFunction<Coordinate, Coordinate> calculateLength = 
			(Coordinate a, Coordinate b) -> Math.sqrt((Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2)));
			
	//arcos((P122 + P132 - P232) / (2 * P12 * P13))
	public static ToDoubleTriFunction<Coordinate, Coordinate, Coordinate> calculateMinAngle = (Coordinate a, Coordinate b, Coordinate c) -> {
		return Math.acos(
				(Math.pow(calculateLength.applyAsDouble(a, b), 2) + Math.pow(calculateLength.applyAsDouble(a, c), 2) - Math.pow(calculateLength.applyAsDouble(c, b), 2))
				/
				(2 * calculateLength.applyAsDouble(a, b) * calculateLength.applyAsDouble(a, c)));
		
	};
	
	public static ToDoubleBiFunction<List<Coordinate>, Integer> calculateMinAngleFromList = (List<Coordinate> list, Integer index) ->  {
		if (list.size() < 3)
		{
			return -1.0D;
		} else {
			return calculateMinAngle.applyAsDouble(
					list.get((index == 0)?list.size()-1:index-1),
					list.get(index),
					list.get((index == list.size()-1)?0:index));
		}
	};

}
