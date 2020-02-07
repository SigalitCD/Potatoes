package com.scd.potatoeslb.risks;

import java.awt.geom.Point2D;
import java.util.List;

//import com.scd.potatoeslb.data.Meteorology;

public class Vector {
	private Double angel = null;
	//private Double length = null;
	private Point2D.Double point1 = null; // x
	private Point2D.Double point2 = null; // y

	// constructors
	public Vector(double angel) {
		this.angel = angel;
	}

	public Vector(Point2D.Double point1, Point2D.Double point2) {
		this.point1 = point1;
		this.point2 = point2;
	}
	

	// getters and setters

	public Point2D.Double getPoint1() {
		return point1;
	}

	public Point2D.Double getPoint2() {
		return point2;
	}

	public double getAngel() {
		if( angel != null ) {
			return angel;
		}
		double slope = getSlope();
		angel = Math.toDegrees(Math.atan(slope));
		return angel;
	}

	// Calculate the angel between this vector and the argument vector v.
	public double getAngelwithVector( Vector v ) { // todo: consult if this is right to do the abs and 180- manipulations
		double angel = Math.abs(this.getAngel() - v.getAngel());
		while ( angel > 180 ) {
			angel = angel - 180;
		}
		angel = angel + 180;
		return angel;
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
