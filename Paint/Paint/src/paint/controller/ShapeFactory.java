
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

    // تعديل
    public Shape createShape(String type, Point2D start, Point2D end, Color color) {
        Shape s = cloneOf(type); // Prototype: نسخة من البروتوتايب
        s.setPosition(start); // نضبط الإحداثيات مباشرة
        s.setEndPosition(end);
        s.setColor(color); // لون الحدود
        s.setFillColor(Color.TRANSPARENT);// تعبئة افتراضية (تقدر تغيّرها لاحقاً)
        s.setTopLeft(s.calculateTopLeft());// احسب المربع المحيط العلوي الأيسر
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