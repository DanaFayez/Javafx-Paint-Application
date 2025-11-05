package paint.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 * CompositeShape - Implements the Composite Pattern
 * Allows grouping of multiple shapes as a single unit
 * Can be nested (shapes within groups within groups)
 */
public class CompositeShape implements iShape, java.lang.Cloneable {
    
    private List<iShape> children = new ArrayList<>();
    private String groupName;
    private Point2D position;
    private Color color = Color.BLACK;
    private Color fillColor = Color.TRANSPARENT;
    
    public CompositeShape(String groupName) {
        this.groupName = groupName;
        this.position = new Point2D(0, 0);
    }
    
    /**
     * Add a shape to this composite group(Groupe Button)
     */

    public void addShape(iShape shape) {
        if (shape != null) {
            children.add(shape);
            // Update group position to track the first child's position
            if (children.size() == 1) {
                this.position = shape.getPosition();
            }
        }
    }
    
    /**
     * Remove a shape from this composite group
     */
    public void removeShape(iShape shape) {
        children.remove(shape);
    }
    
    /**
     * Get all shapes in this group
     */
    public List<iShape> getChildren() {
        return children;
    }
    
    /**
     * Get the number of shapes in this group
     */
    public int getShapeCount() {
        return children.size();
    }
    
    /**
     * Check if this group contains any shapes
     */
    public boolean isEmpty() {
        return children.isEmpty();
    }
    
    /**
     * Draw all shapes in the group
     */
    @Override
    public void draw(Canvas canvas) {
        // Draw all child shapes
        for (iShape shape : children) {
            shape.draw(canvas);
        }
    }
    
    /**
     * Set position for all shapes in the group
     * Moves all children by the same offset to maintain group structure
     */
    @Override
    public void setPosition(Point2D newPosition) {
        // If group is empty, just set position
        if (children.isEmpty()) {
            this.position = newPosition;
            return;
        }
        
        // Get the current position of the first child as the actual group position
        Point2D currentGroupPosition = children.get(0).getPosition();
        
        // Calculate the offset to move
        Point2D offset = newPosition.subtract(currentGroupPosition);
        
        // Move all children by the same offset
        for (iShape shape : children) {
            Point2D currentPos = shape.getPosition();
            Point2D newPos = currentPos.add(offset);
            shape.setPosition(newPos);
        }
        
        // Update group position to match the first child's new position
        this.position = children.get(0).getPosition();
    }
    
    @Override
    public Point2D getPosition() {
        return this.position;
    }
    
    /**
     * Get the top-left position of the group (for compatibility with Shape)
     */
    public Point2D getTopLeft() {
        return getPosition();
    }
    
    /**
     * Set the top-left position of the group (for compatibility with Shape)
     * This moves all children by the offset
     */
    public void setTopLeft(Point2D position) {
        setPosition(position);
    }

    /**
     * Set properties for all shapes in the group
     */
    @Override
    public void setProperties(Map<String, Double> properties) {
        // Apply properties to all children
        for (iShape shape : children) {
            shape.setProperties(properties);
        }
    }
    
    @Override
    public Map<String, Double> getProperties() {
        // Return empty map for composite (properties are managed by children)
        return new java.util.HashMap<>();
    }
    
    /**
     * Set color for all shapes in the group
     */
    @Override
    public void setColor(Color color) {
        this.color = color;
        for (iShape shape : children) {
            shape.setColor(color);
        }
    }
    
    @Override
    public Color getColor() {
        return this.color;
    }
    
    /**
     * Set fill color for all shapes in the group
     */
    @Override
    public void setFillColor(Color color) {
        this.fillColor = color;
        for (iShape shape : children) {
            shape.setFillColor(color);
        }
    }
    
    @Override
    public Color getFillColor() {
        return this.fillColor;
    }
    
    /**
     * Clone the composite shape and all its children
     */
    @Override
    public CompositeShape clone() throws CloneNotSupportedException {
        CompositeShape copy = (CompositeShape) super.clone();
        copy.children = new ArrayList<>();
        
        // Clone all children
        for (iShape child : children) {
            copy.children.add(child.clone());
        }
        
        return copy;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String name) {
        this.groupName = name;
    }
    
    @Override
    public String toString() {
        return "Group: " + groupName + " (" + children.size() + " shapes)";
    }
}
