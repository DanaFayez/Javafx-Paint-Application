package paint.model;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

public abstract class Shape implements iShape, java.lang.Cloneable {

    private Point2D startPosition;
    private Point2D endPosition;
    private Point2D topLeft;
    private Color strokeColor;
    private Color fillColor;
    private LinearGradient strokeGradient;  // Composition for gradient support
    private LinearGradient fillGradient;    // Composition for gradient support
    private Map<String, Double> properties = new HashMap<>();

    public Shape() {
        // Variables will be set by the Properties map.
    }

    public Shape(Point2D startPos, Point2D endPos, Color strockColor) {
        this.strokeColor = strockColor;
        this.startPosition = startPos;
        this.endPosition = endPos;
        this.fillColor = Color.TRANSPARENT;
        this.topLeft = calculateTopLeft();
    }

    /*
     * public Shape(Point2D startPos, Point2D endPos, Color strockColor, Color
     * fillColor){
     * this.color = strockColor;
     * this.startPosition = startPos;
     * this.endPosition = endPos;
     * this.fillColor = fillColor;
     * }
     */
    @Override
    public void setPosition(Point2D position) {
        this.startPosition = position;
    }

    public void setEndPosition(Point2D position) {
        this.endPosition = position;
    }

    @Override
    public Point2D getPosition() {
        return this.startPosition;
    }

    public Point2D getEndPosition() {
        return this.endPosition;
    }

    @Override
    public void setProperties(Map<String, Double> properties) {
        this.properties = properties;
        setPropertiesToVariables();
    }

    protected void setPropertiesToVariables() {
        double startX, startY, endX, endY, topLeftX, topLeftY;
        startX = (double) properties.get("startPositionX");
        startY = (double) properties.get("startPositionY");

        endX = (double) properties.get("endPositionX");
        endY = (double) properties.get("endPositionY");

        topLeftX = (double) properties.get("topLeftX");
        topLeftY = (double) properties.get("topLeftY");

        startPosition = new Point2D(startX, startY);
        endPosition = new Point2D(endX, endY);
        topLeft = new Point2D(topLeftX, topLeftY);

        Double strockR, strockG, strockB, fillR, fillG, fillB;
        strockR = (Double) properties.get("strockR");
        strockG = (Double) properties.get("strockG");
        strockB = (Double) properties.get("strockB");

        fillR = (Double) properties.get("fillR");
        fillG = (Double) properties.get("fillG");
        fillB = (Double) properties.get("fillB");

        strokeColor = Color.color(strockR, strockG, strockB);

        fillColor = Color.color(fillR, fillG, fillB);

    }

    protected double getFromMap(String s) {
        try {
            return (double) properties.get(s);
        } catch (Exception e) {
            System.out.println("Error, can't find this property.");
        }
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public Map<String, Double> getProperties() {
        getPropertiesToMap();
        return this.properties;
    }

    protected void getPropertiesToMap() {
        properties.put("startPositionX", startPosition.getX());
        properties.put("startPositionY", startPosition.getY());

        properties.put("endPositionX", endPosition.getX());
        properties.put("endPositionY", endPosition.getY());

        properties.put("topLeftX", topLeft.getX());
        properties.put("topLeftY", topLeft.getY());

        properties.put("strockR", strokeColor.getRed());
        properties.put("strockG", strokeColor.getGreen());
        properties.put("strockB", strokeColor.getBlue());

        properties.put("fillR", fillColor.getRed());
        properties.put("fillG", fillColor.getGreen());
        properties.put("fillB", fillColor.getBlue());
    }

    public void addToProperties(String s, Double x) {
        properties.put(s, x);
    }

    @Override
    public void setColor(Color color) {
        this.strokeColor = color;
    }

    @Override
    public Color getColor() {
        return this.strokeColor;
    }

    @Override
    public void setFillColor(Color color) {
        this.fillColor = color;
    }

    @Override
    public Color getFillColor() {
        return this.fillColor;
    }

    // Composition: Gradient support methods
    public void setStrokeGradient(LinearGradient gradient) {
        this.strokeGradient = gradient;
    }

    public LinearGradient getStrokeGradient() {
        return this.strokeGradient;
    }

    public void setFillGradient(LinearGradient gradient) {
        this.fillGradient = gradient;
    }

    public LinearGradient getFillGradient() {
        return this.fillGradient;
    }

    @Override
   public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Use composition for gradient support
        if (strokeGradient != null) {
            gc.setStroke(strokeGradient);
        } else {
            gc.setStroke(strokeColor);
        }
        
        if (fillGradient != null) {
            gc.setFill(fillGradient);
        } else {
            gc.setFill(fillColor);
        }
    }
    
    @Override
    public Shape clone() throws CloneNotSupportedException {
        Shape copy = (Shape) super.clone();

        if (this.properties != null && !this.properties.isEmpty()) {
            copy.properties = new java.util.HashMap<>(this.properties);
            copy.setPropertiesToVariables();
        } else {

            copy.properties = new java.util.HashMap<>();
        }

        return copy;
    }

    public Point2D calculateTopLeft() {
        double x = Math.min(this.getPosition().getX(), this.getEndPosition().getX());
        double y = Math.min(this.getPosition().getY(), this.getEndPosition().getY());
        return new Point2D(x, y);
    }

    public Point2D getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(Point2D pos) {
        this.topLeft = pos;
    }

}
