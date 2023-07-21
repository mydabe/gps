import java.io.FileInputStream;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

/*
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Hadi Chaudhri, Lasal Mapitagama
 */
public class GraphDemo {
    public static void main(String[] args) throws Exception {
        FileInputStream fStream = new FileInputStream("../data/usa.graph");
        GraphProcessor graphProcessor = new GraphProcessor();
        graphProcessor.initialize(fStream);

        Map<String, Point> cities = readCities("../data/uscities.csv");
        //System.out.println(cities);

        Scanner inputReader = new Scanner(System.in);

        Point startPoint, endPoint;

        // --- Force user to retry if input is invalid.
        String startLocation = "";
        while (true) {
            System.out.print("Enter your starting location: ");
            startLocation = inputReader.nextLine();
            
            if (!cities.containsKey(startLocation)) {
                System.out.println("Starting location is invalid: " + startLocation);
                continue;
            }

            startPoint = cities.get(startLocation);

            break;
        }
        String endLocation = "";
        while (true) {
            System.out.print("Enter your ending location: ");
            endLocation = inputReader.nextLine();

            if (!cities.containsKey(endLocation)) {
                System.out.println("Ending location is invalid: " + endLocation);
                continue;
            }

            endPoint = cities.get(endLocation);

            break;
        }

        long startTime = System.nanoTime();
        Point start = graphProcessor.nearestPoint(startPoint);
        Point end = graphProcessor.nearestPoint(endPoint);
        List<Point> route = graphProcessor.route(start, end);
        double routeDist = graphProcessor.routeDistance(route);
        long elapsedNanos = System.nanoTime() - startTime;

        System.out.println(String.format("Trip distance: %,.2f miles | Calculation time: %d ms", routeDist, elapsedNanos));
        System.out.println("Visualizing graph...");
        System.out.println("Nearest Point to " + startLocation + ": " + start);
        System.out.println("Nearest Point to " + endLocation + ": "  + end);

        Visualize viz = new Visualize("../data/usa.vis", "../images/usa.png");
        viz.drawPoint(startPoint);
        viz.drawPoint(endPoint);
        viz.drawRoute(route);
    }

    private static Map<String, Point> readCities(String fileName) throws FileNotFoundException {
        Scanner reader = new Scanner(new File(fileName));
        Map<String, Point> cityLookup = new HashMap<>();
        while (reader.hasNextLine()) {
            try {
                String[] info = reader.nextLine().split(",");
                cityLookup.put(info[0] + " " + info[1],
                        new Point(Double.parseDouble(info[2]),
                                Double.parseDouble(info[3])));
            } catch (Exception e) {
                continue;
            }
        }
        return cityLookup;
    }
}