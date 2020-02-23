package com.scd.potatoeslb.risks;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.scd.potatoeslb.ApplicationContextProvider;
import com.scd.potatoeslb.data.Meteorology;
import com.scd.potatoeslb.data.Report;
import com.scd.potatoeslb.spring.dao.IJsonRiskMapDAO;
import com.scd.potatoeslb.spring.dao.IMeteorologyDAO;
import com.scd.potatoeslb.spring.dao.IReportDAO;

@PropertySource("classpath:potatoeslb.properties")
public class RiskMap {

	@Autowired
	Environment environment;

	// TODO: to properties file
	public static final double SQUARE_SIZE = 300;
	// TODO: end of : to properties file

	private Path2D.Float polygon; // map boundaries
	List<List<Coordinate>> grid; // grid of the bounding box of the map boundaries
	private List<Coordinate> infectedCoordinates;
	private Map<Integer, Area> polygons = new HashMap<>(); // key=risk level, value=Area that contains all the polygons with this risk level
	private JSONArray jsonRiskMap; // risk map represented in JSON
	private AnnotationConfigApplicationContext context = ApplicationContextProvider.getApplicationContext();
	private IReportDAO reportDAO = context.getBean(IReportDAO.class);
	private IMeteorologyDAO meteorologyDAO = context.getBean(IMeteorologyDAO.class);
	private IJsonRiskMapDAO jsonRiskMapDAO = context.getBean(IJsonRiskMapDAO.class);

	// Constructor
	public RiskMap() {
		// int x = Integer.valueOf(environment.getProperty("FIRST_DAY_OF_SEASON1")); TODO: it doesn't work. find why and if it
		// will work on Heroku
		buildPolygon();
		buildGrid();
		loadStoredJsonRiskMap();
		if (jsonRiskMap == null) {
			System.out.println("jsonRiskMap failed to load from storage. Generating empty riskMap.");
			generateEmptyJsonRiskMap();
		} else {
			System.out.println("jsonRiskMap was loaded susccessfully from persistent storage.");
		}

	}

	public void generateEmptyJsonRiskMap() {
		generateRiskPolygons();
		generateJsonRiskMap();
		if (!jsonRiskMapDAO.createRiskMap(jsonRiskMap.toString())) { // store new risk map in database
			System.err.println("Failed to save empty jsonRiskMap in persistent storage.");
		} else {
			System.out.println("jsonRiskMap was saved susccessfully to persistent storage.");
		}
	}

	public JSONArray getJsonRiskMap() {
		return jsonRiskMap;
	}

	public void updateRisks(LocalDateTime windDirectionSamplePeriodStart) {
		calculateRisks(windDirectionSamplePeriodStart);
		long time1 = System.currentTimeMillis();
		generateRiskPolygons();
		long time2 = System.currentTimeMillis();
		long diff = (time2 - time1);
		System.out.println("generateRiskPolygons took " + diff + " msec.");
		generateJsonRiskMap();
		long time3 = System.currentTimeMillis();
		diff = (time3 - time2);
		System.out.println("generateJsonRiskMap took " + diff + " msec.");
		if (!jsonRiskMapDAO.createRiskMap(jsonRiskMap.toString())) { // store new risk map in database
			System.err.println("Failed to save jsonRiskMap in persistent storage");
		}
	}

	private void calculateRisks(LocalDateTime windDirectionSamplePeriodStart) {
		// get the final wind direction
		Vector windVector = calculateWindVector(windDirectionSamplePeriodStart);

		// get infected coordinates list out of reports
		List<Report> reportsList = reportDAO.getLatestReports(RiskMapManager.getReportsStartDate());
		infectedCoordinates = extractInfectedCoordinates(reportsList);
		System.out.println( "infected coordinates count: "  + infectedCoordinates.size() );// TODO: remove after debug

		// calculate risk for each coordinate in the map
		for (int i = 0; i < grid.size(); i++) {
			// System.out.print(" i=" + i);
			List<Coordinate> line = grid.get(i);
			for (int j = 0; j < line.size(); j++) {
				Coordinate coordinate = line.get(j); // coordinates and neighbors
				if (coordinate != null) {
					coordinate.calculateRisk(infectedCoordinates, windVector);
				}
			}
		}
	}

	private void generateRiskPolygons() {
		Area area = null;
		polygons.clear();
		for (List<Coordinate> col : grid) {
			for (Coordinate coordinate : col) {
				if (coordinate != null) {
					area = polygons.get(coordinate.getRiskLevel());
					if (area == null) { // new
						polygons.put(coordinate.getRiskLevel(), new Area(new Rectangle2D.Double(coordinate.getLatitude(), coordinate.getLongitude(),
								coordinate.getDistanceFromLatitude(SQUARE_SIZE), coordinate.getDistanceFromLongitude(SQUARE_SIZE))));
					} else {
						area.add(new Area(new Rectangle2D.Double(coordinate.getLatitude(), coordinate.getLongitude(), coordinate.getDistanceFromLatitude(SQUARE_SIZE),
								coordinate.getDistanceFromLongitude(SQUARE_SIZE))));
					}
				}
			}
		}
	}

	private void generateJsonRiskMap() {
		JSONArray jsonRiskMapTmp = new JSONArray(); // To avoid read-write concurrency problems, use temp object to build the map, then set jsonRiskMap reference.
		JSONObject jsonPolygon = null;
		JSONArray jsonCoordinates = null;
		JSONObject jsonCoordinate = null;

		Iterator<Entry<Integer, Area>> riskLevelIterator = polygons.entrySet().iterator();
		while (riskLevelIterator.hasNext()) { // iterate on risk levels
			Entry<Integer, Area> entry = (Entry<Integer, Area>) riskLevelIterator.next();
			int riskLevel = entry.getKey();
			Area area = entry.getValue();
			PathIterator polygonsIterator = area.getPathIterator(null);
			double[] coord = new double[6];
			while (!polygonsIterator.isDone()) { // iterate on all polygons of the same risk level
				int type = polygonsIterator.currentSegment(coord);
				switch (type) {
				case PathIterator.SEG_MOVETO:
					jsonPolygon = new JSONObject();
					jsonPolygon.put("risk_level", riskLevel); // getRiskLevelOf(coord[0], coord[1]));
					jsonCoordinates = new JSONArray();
					jsonCoordinate = new JSONObject();
					jsonCoordinate.put("lat", coord[0]);
					jsonCoordinate.put("lng", coord[1]);
					jsonCoordinates.put(jsonCoordinate);
					break;
				case PathIterator.SEG_LINETO:
					jsonCoordinate = new JSONObject();
					jsonCoordinate.put("lat", coord[0]);
					jsonCoordinate.put("lng", coord[1]);
					if (jsonCoordinates != null) {
						jsonCoordinates.put(jsonCoordinate);
					} else {
						System.err.println("Error in the Area object structure: SEG_LINETO before SEG_MOVETO!");
					}
					break;
				case PathIterator.SEG_CLOSE:
					jsonPolygon.put("paths", jsonCoordinates);
					jsonPolygon.put("risk_level", entry.getKey());
					jsonRiskMapTmp.put(jsonPolygon);

					break;
				default:
					System.err.println("Unexpected segment type in the Area object. SegmentType=" + type);
				}
				polygonsIterator.next();
			}
			riskLevelIterator.remove(); // avoids a ConcurrentModificationException
		}
		jsonRiskMap = jsonRiskMapTmp;
	}

	private void buildGrid() {
		Rectangle2D boundingBox = polygon.getBounds2D();
		int i = 0;
		int j = 0;

		grid = new ArrayList<List<Coordinate>>();
		Coordinate coordinate = new Coordinate(boundingBox.getMinX(), boundingBox.getMinY());
		while (coordinate.getLatitude() <= boundingBox.getMaxX()) {
			grid.add(i, new ArrayList<Coordinate>());
			while (coordinate.getLongitude() <= boundingBox.getMaxY()) {
				grid.get(i).add(j, null);
				if (polygon.contains(coordinate.getPoint())) {
					Coordinate newCoordinate = (Coordinate) coordinate.clone();
					grid.get(i).set(j, newCoordinate);
				}
				coordinate.addLongitude(SQUARE_SIZE);
				j++;
			}
			coordinate.addLatitude(SQUARE_SIZE);
			coordinate.setLongitude(boundingBox.getMinY());
			j = 0;
			i++;
		}
	}

	private void buildPolygon() {
		List<Coordinate> verticesList = createVerticesList();
		polygon = new Path2D.Float();
		Coordinate vertex = verticesList.get(0);
		polygon.moveTo(vertex.getLatitude(), vertex.getLongitude());
		for (int i = 1; i < verticesList.size(); i++) {
			vertex = verticesList.get(i);
			polygon.lineTo(vertex.getLatitude(), vertex.getLongitude());
		}
		polygon.closePath();
	}

	private List<Coordinate> createVerticesList() {
		// TODO: get from properties
		List<Coordinate> verticesList = new ArrayList<Coordinate>();
		
//		verticesList.add(new Coordinate(31.541743, 34.574145)); // sederot
//		verticesList.add(new Coordinate(31.516077, 35.130235)); // Hebron
//		verticesList.add(new Coordinate(30.762078, 35.280489)); // Hazeva
//		verticesList.add(new Coordinate(30.8001393, 34.2227325)); // Sinai 2
//		verticesList.add(new Coordinate(31.374159, 34.319267)); // Khan Yunis

		verticesList.add(new Coordinate(31.4051361, 34.2703391)); // gaza sea (north-west)
		verticesList.add(new Coordinate(31.3861394, 34.456639)); // kibbutz Reim (north-east)
		verticesList.add(new Coordinate(31.303992, 34.5183511)); // kibbutz Urim (east)
		verticesList.add(new Coordinate(31.210295, 34.4632533)); // Kibbutz Gvulot (south-east)
		verticesList.add(new Coordinate(31.183820, 34.212045)); // Rafah, Egypt (south-west)
		//verticesList.add(new Coordinate(31.2967792, 34.2347272)); // Rafah (west)

		return verticesList;
	}

	private List<Double> extractWindDirections(List<Meteorology> meteorologyList) {
		List<Double> windDirectionList = new ArrayList<Double>();
		for (Meteorology meteorology : meteorologyList) {
			windDirectionList.add((double) meteorology.getWindDirection());
		}
		return windDirectionList;
	}

	private List<Coordinate> extractInfectedCoordinates(List<Report> reportsList) {
		List<Coordinate> infectedCoordinates = new ArrayList<Coordinate>();
		for (Report report : reportsList) {
			Coordinate infectedCoordinate = new Coordinate(Double.valueOf(report.getLatitude()), Double.valueOf(report.getLongitude()));
			if (polygon.contains(infectedCoordinate.getPoint())) {
				infectedCoordinates.add(infectedCoordinate);
			}
		}
		return infectedCoordinates;
	}

	private Vector calculateWindVector(LocalDateTime from) {
		// get the final wind direction
		LocalDateTime to = from.plusHours(Meteorology.WIND_SAMPLE_HOURS);
		List<Meteorology> meteorologyList = meteorologyDAO.getMeteorologiesByTimeInterval(from, to);
		// List<Meteorology> meteorologyList = meteorologyDAO.getAllMeteorologies(); // TODO: remove this after debug
		return Vector.sumVectors(extractWindDirections(meteorologyList));
	}

	private void loadStoredJsonRiskMap() {
		String jsonRiskMapStr = jsonRiskMapDAO.getLatestRiskMap();
		if (jsonRiskMapStr != null) {
			jsonRiskMap = new JSONArray(jsonRiskMapStr);
		}
//		JSONObject jsnobject = new JSONObject(jsonRiskMapStr);
//		jsonRiskMap = jsnobject.getJSONArray("risk_level");
	}

//	public static final String ANSI_RESET = "\u001B[0m";
//	public static final String ANSI_WHITE = "\u001B[37m";
//	public static final String ANSI_BLACK = "\u001B[30m";
//	public static final String ANSI_RED = "\u001B[31m";
//	public static final String ANSI_GREEN = "\u001B[32m";
//	public static final String ANSI_YELLOW = "\u001B[33m";
//	public static final String ANSI_BLUE = "\u001B[34m";
//	public static final String ANSI_PURPLE = "\u001B[35m";
//	public static final String ANSI_CYAN = "\u001B[36m";
//

}
