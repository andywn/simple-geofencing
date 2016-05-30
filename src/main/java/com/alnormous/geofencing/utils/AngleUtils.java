package com.alnormous.geofencing.utils;

import java.util.List;

import com.alnormous.geofencing.entities.Coordinate;

public class AngleUtils {
	
	/**
	 * Calculate the length between two points
	 */
	public static Double calculateLength(Coordinate a, Coordinate b) { 
		return Math.sqrt((Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2)));
	}
			
	//arcos((P122 + P132 - P232) / (2 * P12 * P13))
	public static Double calculateMinAngle(Coordinate a, Coordinate b, Coordinate c) {
		return Math.acos(
				(Math.pow(calculateLength(a, b), 2) + Math.pow(calculateLength(a, c), 2) - Math.pow(calculateLength(c, b), 2))
				/
				(2 * calculateLength(a, b) * calculateLength(a, c)));
		
	};
	
	public static Double calculateMinAngleFromList(List<Coordinate> list, Integer index) {
		if (list.size() < 3)
		{
			return -1.0D;
		} else {
			return calculateMinAngle(
					list.get((index == 0)?list.size()-1:index-1),
					list.get(index),
					list.get((index == list.size()-1)?0:index));
		}
	};

}
