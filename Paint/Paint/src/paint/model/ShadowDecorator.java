package paint.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class ShadowDecorator extends ShapeDecorator {
    private double shadowRadius;
    private Color shadowColor;

    public ShadowDecorator(iShape  decoratedShape, double shadowRadius, Color shadowColor) {
        super(decoratedShape);
        this.shadowRadius = shadowRadius;
        this.shadowColor = shadowColor;
    }

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Apply shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(shadowRadius);
        shadow.setColor(shadowColor);
        gc.applyEffect(shadow);
        
        // Draw the original shape
        decoratedShape.draw(canvas);
        
        // Remove effect for next drawings
        gc.setEffect(null);
    }
}
