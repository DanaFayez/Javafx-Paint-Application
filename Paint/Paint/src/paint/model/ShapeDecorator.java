package paint.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import java.util.Map;

public abstract class ShapeDecorator implements iShape{
  protected iShape decoratedShape;

public ShapeDecorator(iShape decoratedShape) {
    this.decoratedShape = decoratedShape;
}

    // Getter for decorated shape
    public iShape getDecoratedShape() {
        return this.decoratedShape;
    }

    @Override
    public void draw(Canvas canvas) {
        decoratedShape.draw(canvas);
    }

    // Delegate all other methods to the decorated shape
    @Override
    public void setPosition(Point2D position) {
        decoratedShape.setPosition(position);
    }

    @Override
    public Point2D getPosition() {
        return decoratedShape.getPosition();
    }

    @Override
    public void setTopLeft(Point2D position) {
        decoratedShape.setTopLeft(position);
    }

    @Override
    public Point2D getTopLeft() {
        return decoratedShape.getTopLeft();
    }

    @Override
    public void setProperties(Map<String, Double> properties) {
        decoratedShape.setProperties(properties);
    }

    @Override
    public Map<String, Double> getProperties() {
        return decoratedShape.getProperties();
    }

    @Override
    public void setColor(Color color) {
        decoratedShape.setColor(color);
    }

    @Override
    public Color getColor() {
        return decoratedShape.getColor();
    }

    @Override
    public void setFillColor(Color color) {
        decoratedShape.setFillColor(color);
    }

    @Override
    public Color getFillColor() {
        return decoratedShape.getFillColor();
    }

    @Override
    public iShape clone() throws CloneNotSupportedException {
        // Default: clone the decorated shape
        // Subclasses should override this to preserve decorator properties
        return decoratedShape.clone();
    }
}
