package com.scd.potatoeslb.risks;

import java.awt.geom.Point2D;
import java.util.List;

//import com.scd.potatoeslb.data.Meteorology;

public class Vector {
	private Double angel = null;
	//private Double length = null;
	private Point2D.Double point1 = null; // TODO: decide which is point1 and which is point2
	private Point2D.Double point2 = null; 

	// constructors
	public Vector(double angel) {
		this.angel = angel;
	}

//	public Vector(double angel, double length) {
//		this.angel = angel;
//		this.length = length;
//	}

	public Vector(Point2D.Double point1, Point2D.Double point2) {
		this.point1 = point1;
		this.point2 = point2;
	}
	

	// getters and setters

	public Point2D.Double getPoint1() {
		return point1;
	}

//	public void setPoint1(Point point1) {
//		this.point1 = point1;
//	}

	public Point2D.Double getPoint2() {
		return point2;
	}

//	public void setPoint2(Point point2) {
//		this.point2 = point2;
//	}

	public double getAngel() {
		if( angel != null ) {
			return angel;
		}
		double slope = getSlope();
		angel = Math.toDegrees(Math.atan(slope));
		return angel;
	}

//	public void setAngel(double angel) {
//		this.angel = angel;
//	}

//	public Double getLength() {
//		if (length != null ) {
//			return length;
//		}
//		if ( point1 != null && point2 != null ) { // TODO: VECTOR
//			final double earthRadius = 6371e3; // meters
//			double latitudeDiff = Math.toRadians(point2.getX()-point1.getX());
//			double longitudeDiff = Math.toRadians(point2.getY()-point1.getY());
//			
//			double a =	Math.pow(Math.sin(latitudeDiff/2), 2) +
//						Math.cos(Math.toRadians(point1.getX())) * Math.cos(Math.toRadians(point2.getX())) * 
//						Math.pow(Math.sin(longitudeDiff/2), 2);
//			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//			length = earthRadius * c;
//			
//			return length;
//		};
//		return null;
//	}

//	public void setLength(double length) { // TODO is this needed? is this coherent?
//		this.length = length;
//	}

	// Calculate the angel between this vector and the argument vector v.
	public double getAngelwithVector( Vector v ) {
		return this.getAngel() - v.getAngel();
	}
	
	private double getSlope() {
		return ( point1.getY() - point2.getY() ) / (point1.getX() - point2.getX() );
	}
	
	public static Vector sumVectors( List<Double> angelsList ) {
		//note that we use only angels without length, because we assume that each vector in the list has the length=1
		double x = 0;
		double y = 0;
		for ( double angel : angelsList ) {
			x = x + Math.cos(Math.toRadians(angel));
			y = y + Math.sin(Math.toRadians(angel));
		}
		
		double summedAngel = Math.toDegrees(Math.atan(y/x));
		if ( summedAngel < 0 ) {
			summedAngel = 360 + summedAngel;
		}
		if ( summedAngel > 360 ) {
			summedAngel = summedAngel % 360; 
		}
		//return new Vector(summedAngel, Math.sqrt(x*x + y*y));
		return new Vector(summedAngel);
	}


}
