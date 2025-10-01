
package paint.controller;

import java.util.HashMap;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import paint.model.*;

//Factory DP
public class ShapeFactory {

    public ShapeFactory() {
    }

    // تعديل
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
            return (Shape) proto.clone(); // يستدعي clone() العميق الذي عدّلناه في Shape
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    // تعديل
    public Shape createShape(String type, Point2D start, Point2D end, Color stroke) {
        Shape s = cloneOf(type); // Prototype

        double x1 = start.getX(), y1 = start.getY();
        double x2 = end.getX(), y2 = end.getY();

        double tlx = Math.min(x1, x2), tly = Math.min(y1, y2);
        double width = Math.abs(x2 - x1);
        double height = Math.abs(y2 - y1);

        HashMap<String, Double> m = new HashMap<>();
        // العامة
        m.put("startPositionX", x1);
        m.put("startPositionY", y1);
        m.put("endPositionX", x2);
        m.put("endPositionY", y2);
        m.put("topLeftX", tlx);
        m.put("topLeftY", tly);
        // لون الحدود
        m.put("strockR", stroke.getRed());
        m.put("strockG", stroke.getGreen());
        m.put("strockB", stroke.getBlue());
        // تعبئة مبدئية (الشفافية تُضبط لاحقاً)
        m.put("fillR", 0.0);
        m.put("fillG", 0.0);
        m.put("fillB", 0.0);

        // خصائص خاصة بكل نوع
        switch (type) {
            case "Rectangle":
                m.put("width", width);
                m.put("height", height);
                break;
            case "Square":
                double side = Math.min(width, height);
                m.put("width", side);
                m.put("height", side);
                break;
            case "Ellipse":
                m.put("hRadius", width / 2.0);
                m.put("vRadius", height / 2.0);
                break;
            case "Circle":
                double r = Math.min(width, height) / 2.0;
                m.put("hRadius", r);
                m.put("vRadius", r);
                break;
            case "Triangle":
                // نقطة ثالثة افتراضية (تقدير بسيط)
                double x3 = (x1 + x2) / 2.0;
                double y3 = tly - Math.abs(x2 - x1);
                m.put("thirdPointX", x3);
                m.put("thirdPointY", y3);
                break;
            case "Line":
                // يعتمد على start/end فقط
                break;
        }

        // فكّ الخريطة إلى حقول
        s.setProperties(m);

        // اجعل التعبئة شفافة لكل الأشكال ما عدا Line (لأن Line.setFillColor تغيّر
        // stroke)
        if (!"Line".equalsIgnoreCase(type)) {
            s.setFillColor(Color.TRANSPARENT);
        }

        return s;
    }


    // تعديل

    // تعديل
    public Shape createShape(String type, HashMap<String, Double> m) {
        Shape s = cloneOf(type); // Prototype
        s.setProperties(m); // يفك القيم عبر setPropertiesToVariables() في Shape
        return s;
    }
    // تعديل

}