package paint.model;

import java.util.ArrayList;

public class CanvasMemento {

    private final ArrayList<iShape> snapshot;

    public CanvasMemento(ArrayList<iShape> shapes) throws CloneNotSupportedException {
        snapshot = new ArrayList<>();
        for (iShape s : shapes) {
            snapshot.add(s.clone());   
        }
    }

    public ArrayList<iShape> getState() throws CloneNotSupportedException {
        ArrayList<iShape> copy = new ArrayList<>();
        for (iShape s : snapshot) {
            copy.add(s.clone());
        }
        return copy;
    }
}
    

