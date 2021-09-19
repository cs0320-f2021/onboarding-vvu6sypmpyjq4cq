package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  // use port 4567 by default when running server
  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // set up parsing of command line flags
    OptionParser parser = new OptionParser();

    // "./run --gui" will start a web server
    parser.accepts("gui");

    // use "--port <n>" to specify what port on which the server runs
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;
      ArrayList<String[]> starData = new ArrayList<>();
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          String[] arguments = input.split(" ");
          if (arguments[0].equals("add")) {
            double sum = Double.parseDouble(arguments[1]) + Double.parseDouble(arguments[2]);
            System.out.println(sum);
          } else if (arguments[0].equals("subtract")) {
            double difference = Double.parseDouble(arguments[1]) - Double.parseDouble(arguments[2]);
            System.out.println(difference);
          } else if (arguments[0].equals("stars")) {
            starData = new ArrayList<>();
            BufferedReader br2 = new BufferedReader(new FileReader("" + arguments[1]));
            br2.readLine();
            String line;
            while ((line = br2.readLine()) != null) {
              starData.add(line.split(","));
            }
            System.out.println("Read " + starData.size() + " stars from " + arguments[1]);
          } else if (arguments[0].equals("naive_neighbors")) {
            double x, y, z;
            int k = Integer.parseInt(arguments[1]);
            if (arguments[2].substring(0, 1).equals("\"")) {
              int index  = 0;
              while (!("\"" + starData.get(index)[1] + "\"").equals(arguments[2])) {
                index++;
              }
              x = Double.parseDouble(starData.get(index)[2]);
              y = Double.parseDouble(starData.get(index)[3]);
              z = Double.parseDouble(starData.get(index)[4]);
            } else {
              x = Double.parseDouble(arguments[2]);
              y = Double.parseDouble(arguments[3]);
              z = Double.parseDouble(arguments[4]);
            }
            if (k != 0) {
              findNeighbors(k, x, y, z, starData);
            }
          } else {
            System.out.println(arguments[0]);
          }
        } catch (Exception e) {
          // e.printStackTrace(); // <-- bad practice
          System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
    }

  }

  private void findNeighbors(int k, double x, double y, double z, ArrayList<String[]> allData) {
    // TODO: order ID printing, remove the star itself from closest list
    Hashtable<Double, Integer> stars = new Hashtable<>();
    for (int i = 0; i < k; i++) {
      stars.put(distanceTo(x, y, z, allData.get(i)), Integer.parseInt(allData.get(i)[0]));
    }
    for (int i = k; i < allData.size(); i++) {
      double currDistance = distanceTo(x, y, z, allData.get(i));
      double maxInHash = Collections.max(stars.keySet());
      if (currDistance < maxInHash) {
        stars.remove(maxInHash);
        stars.put(currDistance, Integer.parseInt(allData.get(i)[0]));
      }
    }
    for (Integer id : stars.values()) {
      System.out.println(id);
    }
  }

  private double distanceTo(double x, double y, double z, String[] star2) {
    return Math.sqrt(Math.pow(Double.parseDouble(star2[2]) - x, 2)
        + Math.pow(Double.parseDouble(star2[3]) - y, 2)
        + Math.pow(Double.parseDouble(star2[4]) - z, 2));
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_0);

    // this is the directory where FreeMarker templates are placed
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    // set port to run the server on
    Spark.port(port);

    // specify location of static resources (HTML, CSS, JS, images, etc.)
    Spark.externalStaticFileLocation("src/main/resources/static");

    // when there's a server error, use ExceptionPrinter to display error on GUI
    Spark.exception(Exception.class, new ExceptionPrinter());

    // initialize FreeMarker template engine (converts .ftl templates to HTML)
    FreeMarkerEngine freeMarker = createEngine();

    // setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      // status 500 generally means there was an internal server error
      res.status(500);

      // write stack trace to GUI
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * A handler to serve the site's main page.
   *
   * @return ModelAndView to render.
   * (main.ftl).
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // this is a map of variables that are used in the FreeMarker template
      Map<String, Object> variables = ImmutableMap.of("title",
          "Go go GUI");

      return new ModelAndView(variables, "main.ftl");
    }
  }
}
