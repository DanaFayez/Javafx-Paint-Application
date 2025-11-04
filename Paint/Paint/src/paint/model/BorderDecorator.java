package paint.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BorderDecorator extends ShapeDecorator {

    private double borderWidth;
    private Color borderColor;

    public BorderDecorator(iShape decoratedShape, double borderWidth, Color borderColor) {
        super(decoratedShape);
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }

    @Override
    public void draw(Canvas canvas) {
        // Save original color and line width
        Color originalColor = decoratedShape.getColor();
        double originalLineWidth = canvas.getGraphicsContext2D().getLineWidth();

        // Apply new border properties
        decoratedShape.setColor(borderColor);
        canvas.getGraphicsContext2D().setLineWidth(borderWidth);

        // Draw the shape with new border
        decoratedShape.draw(canvas);

        // Restore original properties
        decoratedShape.setColor(originalColor);
        canvas.getGraphicsContext2D().setLineWidth(originalLineWidth);
    }
}
