package paint.model;

import javafx.scene.paint.Color;

public class SolidColor implements ColorAPI {
    private Color color;
    
    public SolidColor(Color color) {
        this.color = color;
    }
    
    @Override
    public Color getColor() {
        return color;
    }
    
    @Override
    public void applyStyle() {
        System.out.println("Applying solid color: " + color);
    }
}
