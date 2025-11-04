package paint.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

public class ShadowDecorator extends ShapeDecorator {
    private static final double SHADOW_OFFSET_X = 8.0;
    private static final double SHADOW_OFFSET_Y = 8.0;
    private static final Color SHADOW_COLOR = Color.GRAY;  // Full solid gray color

    public ShadowDecorator(iShape decoratedShape, double shadowRadius, Color shadowColor) {
        super(decoratedShape);
    }

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Get the base shape (unwrap decorators)
        iShape baseShape = decoratedShape;
        while (baseShape instanceof ShapeDecorator) {
            baseShape = ((ShapeDecorator) baseShape).decoratedShape;
        }
        
        // Cast to Shape to access drawing properties
        if (baseShape instanceof Shape) {
            Shape shapeObj = (Shape) baseShape;
            
            // Save original colors
            Color originalStroke = shapeObj.getColor();
            Color originalFill = shapeObj.getFillColor();
            
            // Draw shadow layers around the shape (creates blurred shadow effect)
            // Draw multiple concentric copies with decreasing opacity
            shapeObj.setColor(Color.color(0.5, 0.5, 0.5, 0.2));  // Light gray, very transparent
            shapeObj.setFillColor(Color.color(0.5, 0.5, 0.5, 0.2));
            
            // Layer 1: Largest shadow (farthest from shape)
            Point2D originalPos = shapeObj.getPosition();
            for (int i = 6; i >= 1; i--) {
                double offset = i * 1.5;
                
                // Draw in all 8 directions to create outline shadow
                for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 4) {
                    double offsetX = Math.cos(angle) * offset;
                    double offsetY = Math.sin(angle) * offset;
                    Point2D shadowPos = new Point2D(originalPos.getX() + offsetX,
                                                   originalPos.getY() + offsetY);
                    shapeObj.setPosition(shadowPos);
                    baseShape.draw(canvas);
                }
            }
            
            // Restore original position and colors
            shapeObj.setPosition(originalPos);
            shapeObj.setColor(originalStroke);
            shapeObj.setFillColor(originalFill);
        }
        
        // Draw the original shape on top with full opacity
        decoratedShape.draw(canvas);
    }
}



