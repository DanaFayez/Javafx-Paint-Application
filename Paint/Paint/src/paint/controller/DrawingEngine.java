package paint.controller;


import paint.model.Observer;
import paint.model.Shape;
import paint.model.iShape;

public interface DrawingEngine {


    
    /* Redraw canvas */
    public void refresh(Object canvas);
    
    /* Add shape */
    public void addShape(Shape shape);
    
    /* Remove shape */
    public void removeShape(iShape shape);
    
   
    /* Return shapes */
    public Shape[] getShapes();
    
    /* Undo action */
    public void undo();
    
    /* Redo action */
    public void redo();
    
    /* Save shapes */
    public void save(String path);
    
    /* Load shapes */
    public void load(String path);
    
    /* Supported shapes */
    public java.util.List<Class<? extends Shape>> getSupportedShapes();
    
    /* Install plugin */
    public void installPluginShape(String jarPath);
    
    /* Attach observer */
    public void attach(Observer o);
    
    /* Detach observer */
    public void detach(Observer o);
    
    /* Notify observers */
    public void notifyObservers();
}