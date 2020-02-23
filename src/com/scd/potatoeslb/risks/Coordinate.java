package com.scd.potatoeslb.risks;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.Range;

public class Coordinate implements Cloneable {

	public static Map<Range<Double>, Integer> riskLevels;
	final static double earthRadius = 6371e3; // meters
	private Point2D.Double point;
	private Double risk = 0d; 
	private int riskLevel = 0;
	private boolean edge;
	private boolean classified = false;

	static {
		initRiskLevels();
	}

	// constructors

	Coordinate(Point2D.Double point) {
		this.point = point;
	}

	Coordinate(Double latitude, Double longitude) {
		this.point = new Point2D.Double(latitude, longitude);
	}

	public Point2D.Double getPoint() {
		return this.point;
	}

	public double getLatitude() {
		return this.point.x;
	}

	public void setLatitude(double lat) {
		this.point.x = lat;
		//this.point.setLocation(lat, getLongitude());
	}

	public double getLongitude() {
		return this.point.y;
	}

	public void setLongitude(double lon) {
		this.point.y = lon;
		//this.point.setLocation(getLatitude(), lon);
	}

	public Double getRisk() {
		return risk;
	}
	
	public int getRiskLevel() {
		return riskLevel;
	}

	public boolean isEdge() {
		return edge;
	}

	public void setEdge(boolean edge) {
		this.edge = edge;
	}
	
	public boolean isClassified() {
		return classified;
	}
	
	public void setClassified( boolean classified ) {
		this.classified = classified;
	}

	public void addLatitude(double meters) {
		// add meters to latitude: lat1 = lat0 + meters/(111111*COS(lat0*PI/180))
		setLatitude(this.getLatitude() + meters / (111111 * Math.cos(this.getLatitude() * Math.PI / 180)));
	}
	
	public void addLongitude(double meters) {
		// add meters to longitude: lon1 = lon0 + meters/111111
		setLongitude(this.getLongitude() + meters / 111111);
	}

	public double getDistanceFromLatitude(double meters) {
		return meters / (111111 * Math.cos(this.getLatitude() * Math.PI / 180));
	}

	public double getDistanceFromLongitude(double meters) {
		return meters / 111111 ;
	}
	
	public double getDistance(Coordinate coordinate) {
		if (this.point == null || coordinate.point == null) { // TODO: VECTOR
			return 0;
		}

		double latitudeDiff = Math.toRadians(coordinate.point.getX() - this.point.getX());
		double longitudeDiff = Math.toRadians(coordinate.point.getY() - this.point.getY());

		double a = Math.pow(Math.sin(latitudeDiff / 2), 2)
				+ Math.cos(Math.toRadians(this.point.getX())) * Math.cos(Math.toRadians(coordinate.point.getX())) * Math.pow(Math.sin(longitudeDiff / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return earthRadius * c;
	}

	public double calculateRisk(List<Coordinate> infectedCoordinates, Vector windVector) {
		risk = 0d;
		for (Coordinate infectedCoordinate : infectedCoordinates) {
			// calculate the vector between this coordinate and the infected coordinate => infectedVector
			Vector infectedVector = new Vector(this.point, infectedCoordinate.point);
			// calculate the angel between windVector and infectedVector
			double angel = windVector.getAngelwithVector(infectedVector);
			// calculate the distance in meters between this coordinate and the infected coordinate
			double distance = getDistance(infectedCoordinate);
			// calculate the risk caused by the infected coordinate
			double windRisk = 0.3 * Math.pow(angel, -0.47);
			double distanceRisk = 0;
			if ( distance > RiskMap.SQUARE_SIZE ) {
				distanceRisk = 990.8 * Math.pow(distance, -1.33); // risk by distance of this coordinate from the infected coordinate
			}
			double currentInfectedcoordinateRisk = (6 * windRisk + distanceRisk) / 7;
			// sum the risks of all infected coordinates
			risk = risk + currentInfectedcoordinateRisk - (risk * currentInfectedcoordinateRisk);
//			setRiskLevel();
		}
		if (Double.isNaN(risk)) {
			System.err.println("risk is NaN. windVector is " + windVector );
		} else if ( risk > 2 ) {
			System.err.println("risk is " + risk + ". windVector is " + windVector );		
		}
		setRiskLevel();
		return risk;
	}

	@Override
	public String toString() {
		return "Coordinate [latitude=" + point.getX() + " longitude=" + point.getY() + "]";
	}

	@Override
	public Object clone() {
		Coordinate coordinateClone = null;
		try {
			coordinateClone = (Coordinate) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		coordinateClone.point = new Point2D.Double(point.x, point.y);
		return coordinateClone;
	}

	public void calculateEdge(Coordinate east, Coordinate west, Coordinate south, Coordinate north) {
		edge = false;
		setEdgeTrue(east);
		setEdgeTrue(west);
		setEdgeTrue(south);
		setEdgeTrue(north);
	}
	
	private void setEdgeTrue(Coordinate neighbor) {
		if (neighbor == null || riskLevel != neighbor.getRiskLevel()) { // neighbor == null means that this coordinate is on the edge of the polygon
			edge = true;
		}
	}

	private void setRiskLevel() {
		Iterator<Entry<Range<Double>, Integer>> it = riskLevels.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Range<Double>, Integer> pair = (Map.Entry<Range<Double>, Integer>) it.next();
			Range<Double> range = pair.getKey();
			if (range.contains(risk)) {
				riskLevel = pair.getValue();
				return;
			}
		}
		System.err.println("risk level of coordinate " + getLatitude() +", " + getLongitude() + " is out of range with value=" + risk);
	}

	private static void initRiskLevels() {
		riskLevels = new HashMap<Range<Double>, Integer>();
		// TODO: from properties file
		riskLevels.put(Range.between(0d, 0.0500), 0);
		riskLevels.put(Range.between(0.0500, 0.1000), 1);
		riskLevels.put(Range.between(0.1000, 0.1500), 2);
		riskLevels.put(Range.between(0.1500, 0.2500), 3);
		riskLevels.put(Range.between(0.2500, 0.4000), 4);
		riskLevels.put(Range.between(0.4000, 0.5000), 5);
		riskLevels.put(Range.between(0.5000, 0.8000), 6);
		riskLevels.put(Range.between(0.8000, 2.0000), 7);
		
		
//		riskLevels.put(Range.between(0d, 0.060), 0);
//		riskLevels.put(Range.between(0.060, 0.070), 1);
//		riskLevels.put(Range.between(0.070, 0.07400), 2);
//		riskLevels.put(Range.between(0.07400, 0.07500), 3);
//		riskLevels.put(Range.between(0.07500, 0.0800), 4);
//		riskLevels.put(Range.between(0.0800, 0.0830), 5);
//		riskLevels.put(Range.between(0.0830, 0.0850), 6);
//		riskLevels.put(Range.between(0.0850, 2.0000), 7);

	}
}
