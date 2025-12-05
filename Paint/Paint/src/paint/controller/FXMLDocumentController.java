package paint.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import paint.model.*;

public class FXMLDocumentController implements Initializable, DrawingEngine {

    /**
     * *FXML VARIABLES**
     */
    @FXML
    private Button DeleteBtn;

    @FXML
    private ComboBox<String> ShapeBox;

    @FXML
    private Button UndoBtn;

    @FXML
    private Button RedoBtn;

    @FXML
    private ColorPicker ColorBox;

    @FXML
    private Button SaveBtn;

    @FXML
    private Button MoveBtn;

    @FXML
    private Button RecolorBtn;

    @FXML
    private Button LoadBtn;

    @FXML
    private GridPane After;

    @FXML
    private Pane Before;

    @FXML
    private Pane PathPane;

    @FXML
    private TextField PathText;

    @FXML
    private Button StartBtn;

    @FXML
    private Button ResizeBtn;

    @FXML
    private Button GroupBtn;

    @FXML
    private Button UngroupBtn;

    @FXML
    private Button ImportBtn;

    @FXML
    private Button PathBtn;

    @FXML
    private Canvas CanvasBox;

    @FXML
    private Button CopyBtn;

    @FXML
    private Label Message;

    @FXML
    private ListView ShapeList;

    /**
     * *CLASS VARIABLES**
     */
    private Point2D start;
    private Point2D end;
    // STAGE 2: Composite/Decorator Implementation
    // Composition: Current stroke color for creating shapes
    private Color currentStrokeColor;
    private boolean useShadow = false;
    private boolean useBorder = false;

    // Singleton, memento Pattern
    private static ArrayList<iShape> shapeList = new ArrayList<>();

    // memento Pattern
    private CanvasCaretaker caretaker = new CanvasCaretaker();

    // Observer Pattern
    private List<Observer> observers = new ArrayList<>();

    private boolean move = false;
    private boolean copy = false;
    private boolean resize = false;
    private boolean save = false;
    private boolean load = false;
    private boolean importt = false;

    // handleButtonAction added
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == StartBtn) {
            Before.setVisible(false);
            After.setVisible(true);
        }

        Message.setText("");
        if (event.getSource() == DeleteBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                int index = ShapeList.getSelectionModel().getSelectedIndex();
                removeShape(shapeList.get(index));
            } else {
                Message.setText("You need to pick a shape first to delete it.");
            }
        }

        if (event.getSource() == RecolorBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                int index = ShapeList.getSelectionModel().getSelectedIndex();
                shapeList.get(index).setFillColor(ColorBox.getValue());
                refresh(CanvasBox);
            } else {
                Message.setText("You need to pick a shape first to recolor it.");
            }
        }

        if (event.getSource() == MoveBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                move = true;
                Message.setText("Click on the new top-left position below to move the selected shape.");
            } else {
                Message.setText("You need to pick a shape first to move it.");
            }
        }

        if (event.getSource() == CopyBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                copy = true;
                Message.setText("Click on the new top-left position below to copy the selected shape.");
            } else {
                Message.setText("You need to pick a shape first to copy it.");
            }
        }

        if (event.getSource() == ResizeBtn) {
            if (!ShapeList.getSelectionModel().isEmpty()) {
                resize = true;
                Message.setText("Click on the new right-button position below to resize the selected shape.");
            } else {
                Message.setText("You need to pick a shape first to copy it.");
            }
        }

        if (event.getSource() == UndoBtn) {
            if (primary.empty()) {
                Message.setText("We are back to zero point! .. Can Undo nothing more!");
                return;
            }
            undo();
        }

        if (event.getSource() == RedoBtn) {
            if (secondary.empty()) {
                Message.setText("There is no more history for me to get .. Go search history books.");
                return;
            }
            redo();
        }

        if (event.getSource() == SaveBtn) {
            showPathPane();
            save = true;
        }

        if (event.getSource() == LoadBtn) {
            showPathPane();
            load = true;
        }

        if (event.getSource() == ImportBtn) {
            showPathPane();
            importt = true;
        }

        if (event.getSource() == PathBtn) {
            if (PathText.getText().isEmpty()) {
                PathText.setText("You need to set the path of the file.");
                return;
            }
            if (save) {
                save = false;
                save(PathText.getText());
            } else if (load) {
                load = false;
                load(PathText.getText());
            } else if (importt) {
                importt = false;
                installPluginShape(PathText.getText());
            }
            hidePathPane();
        }
    }

    public void showPathPane() {
        Message.setVisible(false);
        PathPane.setVisible(true);
    }

    public void hidePathPane() {
        PathPane.setVisible(false);
        Message.setVisible(true);
    }

    @FXML
    public void startDrag(MouseEvent event) {
        start = new Point2D(event.getX(), event.getY());
        Message.setText("");
    }

    @FXML
    public void endDrag(MouseEvent event) throws CloneNotSupportedException {
        end = new Point2D(event.getX(), event.getY());
        if (end.equals(start)) {
            clickFunction();
        } else {
            dragFunction();
        }
    }

    public void clickFunction() throws CloneNotSupportedException {
        if (move) {
            move = false;
            moveFunction();
        } else if (copy) {
            copy = false;
            copyFunction();
        } else if (resize) {
            resize = false;
            resizeFunction();
        }
    }

    public void moveFunction() {
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        shapeList.get(index).setTopLeft(start);
        refresh(CanvasBox);

    }
    // Prototype Pattern (Cloning)

    public void copyFunction() throws CloneNotSupportedException {
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        iShape temp = shapeList.get(index).clone();
        if (temp == null) {
            System.out.println("Error cloning failed!");
        } else {
            shapeList.add(temp);
            shapeList.get(shapeList.size() - 1).setTopLeft(start);
            refresh(CanvasBox);
        }
    }

    public void resizeFunction() {
        int index = ShapeList.getSelectionModel().getSelectedIndex();
        Color c = shapeList.get(index).getFillColor();
        start = shapeList.get(index).getTopLeft();
        // Factory DP with Composition pattern
        Shape temp = new ShapeFactory().createShape(shapeList.get(index).getClass().getSimpleName(), start, end,
                currentStrokeColor);
        if (temp.getClass().getSimpleName().equals("Line")) {
            Message.setText("Line doesn't support this command. Sorry :(");
            return;
        }
        shapeList.remove(index);
        temp.setFillColor(c);
        shapeList.add(index, temp);
        refresh(CanvasBox);

    }
    // STAGE 2: Composite/Decorator Implementation

    @FXML
    public void dragFunction() {
        String type = ShapeBox.getValue();
        if (type == null || type.isEmpty()) {
            Message.setText("Please select a shape first!");
            return;
        }

        iShape sh;
        Shape baseShape = null;

        try {
            // 1.ShapeFactory (Composition pattern)
            baseShape = new ShapeFactory().createShape(type, start, end, currentStrokeColor);
            sh = baseShape;

            // 2. Decorator pattern
            if (useShadow) {
                // ShadowDecorator
                sh = new ShadowDecorator(sh, 10.0, Color.GRAY);
            }
            if (useBorder) {
                // BorderDecorator
                sh = new BorderDecorator(sh, 3.0, Color.BLACK);
            }
        } catch (Exception e) {
            Message.setText("Error creating shape: Check if ShapeFactory supports '" + type + "'");
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "Error creating shape", e);
            return;
        }

        // Adding the shape will lead to creating a new Memento in refresh().
        if (baseShape != null) {
            addShape(baseShape);
        } else {
            Message.setText("Could not add shape to list. Drawing only.");
            sh.draw(CanvasBox);
            return;
        }
    }

    // Observer Pattern (Helper)
    public ObservableList getStringList() {
        ObservableList l = FXCollections.observableArrayList(new ArrayList());
        try {
            for (int i = 0; i < shapeList.size(); i++) {
                String temp = shapeList.get(i).getClass().getSimpleName() + "  ("
                        + (int) shapeList.get(i).getTopLeft().getX() + "," + (int) shapeList.get(i).getTopLeft().getY()
                        + ")";
                l.add(temp);
            }
        } catch (Exception e) {
        }
        return l;
    }

    public ArrayList<iShape> cloneList(ArrayList<iShape> l) throws CloneNotSupportedException {
        ArrayList<iShape> temp = new ArrayList<iShape>();
        for (int i = 0; i < l.size(); i++) {
            temp.add(l.get(i).clone());

        }
        return temp;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList shapeList = FXCollections.observableArrayList();
        shapeList.add("Circle");
        shapeList.add("Ellipse");
        shapeList.add("Rectangle");
        shapeList.add("Square");
        shapeList.add("Triangle");
        shapeList.add("Line");
        ShapeBox.setItems(shapeList);

        ColorBox.setValue(Color.BLACK);
        // Initialize with composition: use Color directly
        currentStrokeColor = Color.BLACK;

        // Enable multiple selection for GROUP functionality
        ShapeList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        if (CanvasBox != null) {
            CanvasBox.getGraphicsContext2D().setStroke(Color.BLACK);
        }
        attach(canvasObserver);
        attach(listObserver);

    }

    @Override
    public void refresh(Object canvas) {
        try {
            CanvasMemento m = storeInMemento();
            caretaker.addMemento(m);
        } catch (CloneNotSupportedException ex) {
            // ... logging
        }
        redraw((Canvas) canvas);
        ShapeList.setItems(getStringList());
        // Observer Pattern (Notification)
        notifyObservers();
    }

    // paint.controller.FXMLDocumentController.redraw()
    // Decorator .
    public void redraw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        try {
            for (int i = 0; i < shapeList.size(); i++) {
                iShape shToDraw = shapeList.get(i);
                shToDraw.draw(canvas);
            }
        } catch (Exception e) {
            Message.setText("Error during redraw process: " + e.getMessage());
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "Redraw Error", e);
        }
    }
    // DrawingEngine Method

    @Override
    public void addShape(Shape shape) {
        shapeList.add(shape);
        refresh(CanvasBox);

    }

    @Override
    public void removeShape(iShape shape) {
        shapeList.remove(shape);
        refresh(CanvasBox);

    }

    // Observer Pattern (Concrete Observer: List)
    private Observer listObserver = new Observer() {
        @Override
        public void update() {
            ShapeList.setItems(getStringList());
        }
    };

    // Observer Pattern (Concrete Observer: Canvas)
    private Observer canvasObserver = new Observer() {
        @Override
        public void update() {
            redraw(CanvasBox);
        }

    };

    @Override
    public Shape[] getShapes() {
        return shapeList.toArray(new Shape[0]);
    }

    @Override
    public void undo() {
        try {
            CanvasMemento previous = caretaker.undo();

            if (previous == null) {
                Message.setText("No more undo steps available.");
                return;
            }

            restoreFromMemento(previous);

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        redraw(CanvasBox);
        ShapeList.setItems(getStringList());
        notifyObservers(); // Observer pattern
    }

    @Override
    public void redo() {
        try {
            CanvasMemento next = caretaker.redo();

            if (next == null) {
                Message.setText("No more redo steps available.");
                return;
            }

            restoreFromMemento(next);

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        redraw(CanvasBox);
        ShapeList.setItems(getStringList());
        notifyObservers();
    }

    @Override
    public void save(String path) {
        if (path.length() >= 4 && path.substring(path.length() - 4).equals(".xml")) {
            SaveToXML x = new SaveToXML(path, shapeList);
            if (x.checkSuccess()) {
                Message.setText("File Saved Successfully");
            } else {
                Message.setText("Error happened while saving, please check the path and try again!");
            }
        } else if (path.length() >= 5 && path.substring(path.length() - 5).equals(".json")) {
            Message.setText("Sorry, Json is not supported :(");
        } else {
            Message.setText("Wrong file format .. save to either .xml or .json");
        }

    }

    @Override
    public void load(String path) {
        if (path.length() >= 4 && path.substring(path.length() - 4).equals(".xml")) {
            try {
                LoadFromXML l = new LoadFromXML(path);
                if (l.checkSuccess()) {
                    shapeList = l.getList();
                    refresh(CanvasBox);
                    Message.setText("File loaded successfully");
                } else {
                    Message.setText("Error loading the file .. check the file path and try again!");
                }
            } catch (SAXException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (path.length() >= 5 && path.substring(path.length() - 5).equals(".json")) {
            Message.setText("Sorry, Json is not supported :(");
        } else {
            Message.setText("Wrong file format .. load from either .xml or .json");
        }
    }

    @Override
    public List<Class<? extends Shape>> getSupportedShapes() {
        return null;
    }

    @Override
    public void installPluginShape(String jarPath) {
        Message.setText("Not supported yet.");
    }
    // STAGE 2: Composite/Decorator Implementation

    // ==================== DECORATOR PATTERN METHODS ====================
    @FXML
    public void handleShadowEffect(ActionEvent event) {
        int selectedIndex = ShapeList.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0 || selectedIndex >= shapeList.size()) {
            Message.setText("Please select a shape first!");
            return;
        }

        iShape selectedShape = shapeList.get(selectedIndex);

        // Check if shadow is already applied
        boolean hasShadow = selectedShape instanceof ShadowDecorator;

        if (hasShadow) {
            // Remove shadow decorator by unwrapping it
            iShape unwrapped = ((ShadowDecorator) selectedShape).getDecoratedShape();
            shapeList.set(selectedIndex, unwrapped);
            Message.setText("Shadow removed from shape");
        } else {
            // Apply shadow decorator
            iShape decoratedShape = new ShadowDecorator(selectedShape, 10.0, Color.GRAY);
            shapeList.set(selectedIndex, decoratedShape);
            Message.setText("Shadow applied to shape");
        }

        redraw(CanvasBox);
    }

    @FXML
    public void handleBorderEffect(ActionEvent event) {
        int selectedIndex = ShapeList.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0 || selectedIndex >= shapeList.size()) {
            Message.setText("Please select a shape first!");
            return;
        }

        iShape selectedShape = shapeList.get(selectedIndex);

        // Check if border is already applied
        boolean hasBorder = selectedShape instanceof BorderDecorator;

        if (hasBorder) {
            // Remove border decorator by unwrapping it
            iShape unwrapped = ((BorderDecorator) selectedShape).getDecoratedShape();
            shapeList.set(selectedIndex, unwrapped);
            Message.setText("Border removed from shape");
        } else {
            // Apply border decorator
            iShape decoratedShape = new BorderDecorator(selectedShape, 3.0, Color.BLACK);
            shapeList.set(selectedIndex, decoratedShape);
            Message.setText("Border applied to shape");
        }

        redraw(CanvasBox);
    }
    // STAGE 2: Composite/Decorator Implementation
    // ==================== COMPOSITE PATTERN METHODS ====================

    /**
     * Composite Pattern (Group)
     */
    @FXML
    public void handleGroupShapes(ActionEvent event) {
        ObservableList<?> selectedIndices = ShapeList.getSelectionModel().getSelectedIndices();

        if (selectedIndices == null || selectedIndices.isEmpty() || selectedIndices.size() < 2) {
            Message.setText("Select at least 2 shapes to group!");
            return;
        }

        // Create a new composite shape
        CompositeShape group = new CompositeShape("Group_" + System.currentTimeMillis());

        // Add selected shapes to the group
        java.util.List<iShape> selectedShapes = new java.util.ArrayList<>();
        for (Object index : selectedIndices) {
            if (index instanceof Integer) {
                selectedShapes.add(shapeList.get((Integer) index));
            }
        }

        // Remove selected shapes from main list (in reverse order)
        for (int i = selectedIndices.size() - 1; i >= 0; i--) {
            Integer index = (Integer) selectedIndices.get(i);
            shapeList.remove((int) index);
        }

        // Add all selected shapes to the group
        for (iShape shape : selectedShapes) {
            group.addShape(shape);
        }

        // Add the composite group to the main shape list
        shapeList.add(group);

        Message.setText("Grouped " + selectedShapes.size() + " shapes!");
        refresh(CanvasBox);
        ShapeList.setItems(getStringList());
    }

    /**
     * Composite Pattern (Ungroup)
     */
    @FXML
    public void handleUngroupShapes(ActionEvent event) {
        int selectedIndex = ShapeList.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) {
            Message.setText("Select a group to ungroup!");
            return;
        }

        iShape selected = shapeList.get(selectedIndex);

        // Unwrap decorators to get the actual shape
        iShape actualShape = selected;
        while (actualShape instanceof ShapeDecorator) {
            actualShape = ((ShapeDecorator) actualShape).getDecoratedShape();
        }

        if (!(actualShape instanceof CompositeShape)) {
            Message.setText("Selected shape is not a group!");
            return;
        }

        CompositeShape group = (CompositeShape) actualShape;

        if (group.isEmpty()) {
            Message.setText("Group is empty!");
            return;
        }

        // Remove the group (or its decorator)
        shapeList.remove(selectedIndex);

        // Add all child shapes back to the main list
        for (iShape child : group.getChildren()) {
            shapeList.add(child);
        }

        Message.setText("Ungrouped " + group.getShapeCount() + " shapes!");
        refresh(CanvasBox);
        ShapeList.setItems(getStringList());
    }
    // ==================== OBSERVER PATTERN (SUBJECT) IMPLEMENTATION
    // ====================

    /**
     * Observer Pattern (Attach)
     */
    @Override
    public void attach(Observer o) {
        observers.add(o);
    }

    /**
     * Observer Pattern (Detach)
     */
    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    /**
     * Observer Pattern (Notify)
     */
    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }

    // ====== Memento pattern ========

    // Originator
    public CanvasMemento storeInMemento() throws CloneNotSupportedException {
        return new CanvasMemento(shapeList);
    }

    // Originator
    public void restoreFromMemento(CanvasMemento m) throws CloneNotSupportedException {
        shapeList = m.getState();
    }

}
