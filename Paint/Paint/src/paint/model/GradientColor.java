package paint.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

public class GradientColor implements ColorAPI {
    private Color startColor;
    private Color endColor;
    
    public GradientColor(Color start, Color end) {
        this.startColor = start;
        this.endColor = end;
    }
    
    @Override
    public Color getColor() {
        return startColor;
    }
    
    public LinearGradient getGradient() {
        return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, startColor), new Stop(1, endColor));
    }
    
    @Override
    public void applyStyle() {
        System.out.println("Applying gradient from " + startColor + " to " + endColor);
    }
}
