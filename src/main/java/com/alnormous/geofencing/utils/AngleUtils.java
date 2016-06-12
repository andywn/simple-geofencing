package com.alnormous.geofencing.utils;

import java.util.List;
import java.util.Optional;

import com.alnormous.geofencing.entities.Coordinate;

public class AngleUtils {
	
	/**
	 * Calculate the length between two points
	 */
	public static Double calculateLength(Coordinate a, Coordinate b) { 
		return Math.sqrt((Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2)));
	}

	/**
	 * Calculate the inside angle made by three points a -> b -> c
	 * 
	 * arcos((P122 + P132 - P232) / (2 * P12 * P13))
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static Double calculateMinAngle(Coordinate a, Coordinate b, Coordinate c) {
		return Math.acos(
				(Math.pow(calculateLength(a, b), 2) + Math.pow(calculateLength(a, c), 2) - Math.pow(calculateLength(c, b), 2))
				/
				(2 * calculateLength(a, b) * calculateLength(a, c)));
		
	};
	
	public static Optional<Double> calculateMinAngleFromList(List<Coordinate> list, Integer index) {
		if (list.size() < 3)
		{
			return Optional.empty();
		} else {
			return Optional.of(calculateMinAngle(
					list.get((index == 0)?list.size()-1:index-1),
					list.get(index),
					list.get((index == list.size()-1)?0:index)));
		}
	};

}
