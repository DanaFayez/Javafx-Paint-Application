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
            
            // Save original stroke width
            double originalStrokeWidth = gc.getLineWidth();
            
            // Save original colors
            Color originalStroke = shapeObj.getColor();
            Color originalFill = shapeObj.getFillColor();
            
            // Draw smooth, blurred shadow with multiple layers and decreasing opacity
            // This creates a professional drop shadow effect
            shapeObj.setColor(Color.color(0, 0, 0, 0));  // No stroke for shadow
            shapeObj.setFillColor(Color.TRANSPARENT);
            
            // Draw shadow layers from thick to thin (outside to inside)
            // Each layer gets progressively more transparent
            for (int layer = 1; layer <= 8; layer++) {
                double opacity = 0.15 * (1.0 - (layer / 8.0));  // Decreasing opacity
                double lineWidth = (9 - layer) * 1.5;  // Decreasing line width
                
                Color shadowColor = Color.color(0, 0, 0, opacity);
                shapeObj.setColor(shadowColor);
                gc.setLineWidth(lineWidth);
                
                baseShape.draw(canvas);
            }
            
            // Restore original stroke width
            gc.setLineWidth(originalStrokeWidth);
            
            // Restore original colors
            shapeObj.setColor(originalStroke);
            shapeObj.setFillColor(originalFill);
        }
        
        // Draw the original shape on top with full appearance
        decoratedShape.draw(canvas);
    }
}



