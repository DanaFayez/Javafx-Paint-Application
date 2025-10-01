package paint.model;

// أبقِ على extends Cloneable
public interface iShape extends Cloneable { 

    public void setPosition(javafx.geometry.Point2D position);

    public javafx.geometry.Point2D getPosition();

    /* update shape specific properties (e.g., radius) */
    public void setProperties(java.util.Map<String, Double> properties);

    public java.util.Map<String, Double> getProperties();

    public void setColor(javafx.scene.paint.Color color);

    public javafx.scene.paint.Color getColor();

    public void setFillColor(javafx.scene.paint.Color color);

    public javafx.scene.paint.Color getFillColor();

    /*
     * redraw the shape on the canvas,
     * for swing, you will cast canvas to java.awt.Graphics
     */
    public void draw(javafx.scene.canvas.Canvas canvas);

    // **احذف هذا السطر:**
    // public iShape clone() throws CloneNotSupportedException; 

}