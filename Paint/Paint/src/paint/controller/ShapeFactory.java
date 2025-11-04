
package paint.controller;

import java.util.HashMap;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import paint.model.*;

//Factory DP
public class ShapeFactory {

    public ShapeFactory() {
    }

 
    private static final java.util.Map<String, Shape> PROTOTYPES = new java.util.HashMap<>();

    static {
        PROTOTYPES.put("Circle", new Circle());
        PROTOTYPES.put("Ellipse", new Ellipse());
        PROTOTYPES.put("Rectangle", new Rectangle());
        PROTOTYPES.put("Square", new Square());
        PROTOTYPES.put("Line", new Line());
        PROTOTYPES.put("Triangle", new Triangle());
    }

    private Shape cloneOf(String type) {
        Shape proto = PROTOTYPES.get(type);
        if (proto == null)
            throw new IllegalArgumentException("Unknown shape type: " + type);
        try {
            return (Shape) proto.clone(); 
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    // Refactored: Removed Bridge Pattern - now uses composition directly
    public Shape createShape(String type, Point2D start, Point2D end, Color strokeColor) {
        Shape s = cloneOf(type);
        
        double x1 = start.getX(), y1 = start.getY();
        double x2 = end.getX(), y2 = end.getY();
        double tlx = Math.min(x1, x2), tly = Math.min(y1, y2);

        HashMap<String, Double> m = new HashMap<>();
        m.put("startPositionX", x1);
        m.put("startPositionY", y1);
        m.put("endPositionX", x2);
        m.put("endPositionY", y2);
        m.put("topLeftX", tlx);
        m.put("topLeftY", tly);
        
        // Set color properties using composition
        m.put("strockR", strokeColor.getRed());
        m.put("strockG", strokeColor.getGreen());
        m.put("strockB", strokeColor.getBlue());
        
        m.put("fillR", 0.0);
        m.put("fillG", 0.0);
        m.put("fillB", 0.0);

        // Shape-specific properties
        switch (type) {
            case "Rectangle":
                m.put("width", Math.abs(x2 - x1));
                m.put("height", Math.abs(y2 - y1));
                break;
            case "Square":
                double side = Math.min(Math.abs(x2 - x1), Math.abs(y2 - y1));
                m.put("width", side);
                m.put("height", side);
                break;
            case "Ellipse":
                m.put("hRadius", Math.abs(x2 - x1) / 2.0);
                m.put("vRadius", Math.abs(y2 - y1) / 2.0);
                break;
            case "Circle":
                double r = Math.min(Math.abs(x2 - x1), Math.abs(y2 - y1)) / 2.0;
                m.put("hRadius", r);
                m.put("vRadius", r);
                break;
            case "Triangle":
                double x3 = (x1 + x2) / 2.0;
                double y3 = tly - Math.abs(x2 - x1);
                m.put("thirdPointX", x3);
                m.put("thirdPointY", y3);
                break;
        }

        s.setProperties(m);
        s.setColor(strokeColor);  // Use composition directly
        
        if (!"Line".equalsIgnoreCase(type)) {
            s.setFillColor(Color.TRANSPARENT);  // Use composition directly
        }

        return s;
    }
   
    // Old overloaded method for loading from properties map
    public Shape createShape(String type, HashMap<String, Double> m) {
        Shape s = cloneOf(type); // Prototype
        s.setProperties(m); 
        return s;
    }
   
}