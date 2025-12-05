package paint.model;

import java.util.ArrayList;

public class CanvasCaretaker {

    private ArrayList<CanvasMemento> history = new ArrayList<>();
    private int currentIndex = -1;

    public void addMemento(CanvasMemento m) {
        while (history.size() > currentIndex + 1) {
            history.remove(history.size() - 1);
        }
        history.add(m);
        currentIndex++;
    }

    public boolean canUndo() {
        return currentIndex > 0;
    }

    public boolean canRedo() {
        return currentIndex < history.size() - 1;
    }

    public CanvasMemento undo() {
        if (!canUndo())
            return null;
        currentIndex--;
        return history.get(currentIndex);
    }

    public CanvasMemento redo() {
        if (!canRedo())
            return null;
        currentIndex++;
        return history.get(currentIndex);
    }
}
