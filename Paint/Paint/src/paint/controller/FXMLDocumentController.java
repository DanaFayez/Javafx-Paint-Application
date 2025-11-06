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

    //SINGLETON DP - Changed to iShape to support Composite Pattern
    private static ArrayList<iShape> shapeList = new ArrayList<>();

    private boolean move = false;
    private boolean copy = false;
    private boolean resize = false;
    private boolean save = false;
    private boolean load = false;
    private boolean importt = false;

    //MEMENTO DP
    private Stack primary = new Stack<ArrayList<Shape>>();
    private Stack secondary = new Stack<ArrayList<Shape>>();

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
        //Factory DP with Composition pattern
        Shape temp = new ShapeFactory().createShape(shapeList.get(index).getClass().getSimpleName(), start, end, currentStrokeColor);
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

            // 2.  Decorator pattern 
            if (useShadow) {
                // ShadowDecorator
                sh = new ShadowDecorator(sh, 10.0, Color.GRAY);
            }
            if (useBorder) {
                //BorderDecorator
                sh = new BorderDecorator(sh, 3.0, Color.BLACK);
            }
        } catch (Exception e) {
            Message.setText("Error creating shape: Check if ShapeFactory supports '" + type + "'");
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, "Error creating shape", e);
            return;
        }

        //Memento
        if (baseShape != null) {
            addShape(baseShape);
        } else {
            Message.setText("Could not add shape to list. Drawing only.");
            sh.draw(CanvasBox);
            return;
        }
    }

    //Observer DP
    public ObservableList getStringList() {
        ObservableList l = FXCollections.observableArrayList(new ArrayList());
        try {
            for (int i = 0; i < shapeList.size(); i++) {
                String temp = shapeList.get(i).getClass().getSimpleName() + "  (" + (int) shapeList.get(i).getTopLeft().getX() + "," + (int) shapeList.get(i).getTopLeft().getY() + ")";
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
    }

    @Override
    public void refresh(Object canvas) {
        try {
            primary.push(new ArrayList(cloneList(shapeList)));

            secondary.clear();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        redraw((Canvas) canvas);
        ShapeList.setItems((getStringList()));
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

    @Override
    public void updateShape(Shape oldShape, Shape newShape) {
        int index = shapeList.indexOf((Object) oldShape);
        if (index >= 0) {
            shapeList.set(index, (iShape) newShape);
        }
        shapeList.add(newShape);
        refresh(CanvasBox);
    }

    @Override
    public Shape[] getShapes() {
        return shapeList.toArray(new Shape[0]);
    }

    @Override
    public void undo() {
        if (primary.empty()) {
            Message.setText("We are back to zero point! .. Can Undo nothing more!");
            return;
        }

        if (primary.size() > 1) { 
            ArrayList temp = (ArrayList) primary.pop();
            secondary.push(temp);

            temp = (ArrayList) primary.peek();

            try {
                shapeList = cloneList(temp);
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        } else if (primary.size() == 1) {
            ArrayList temp = (ArrayList) primary.pop();
            secondary.push(temp);
            shapeList = new ArrayList<iShape>();
        }

        redraw(CanvasBox);
        ShapeList.setItems((getStringList()));
    }

    @Override
    public void redo() {
        if (secondary.empty()) {
            Message.setText("There is no more history for me to get .. Go search history books.");
            return;
        }

        ArrayList temp = (ArrayList) secondary.pop();

        primary.push(temp);

        try {
            shapeList = cloneList(temp);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        redraw(CanvasBox);
        ShapeList.setItems((getStringList()));
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
    // Decorator pattern methods
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
     * Group selected shapes into a CompositeShape
     * 
     * LINE-BY-LINE EXPLANATION:
     * This method handles the grouping functionality that allows users to combine
     * multiple shapes into a single composite group using the Composite design pattern.
     * 
     * @param event The ActionEvent triggered when the Group button is clicked
     */
    @FXML
    public void handleGroupShapes(ActionEvent event) {
        // LINE 1: Get the list of selected indices from the ShapeList ListView
        // The ShapeList is configured for MULTIPLE selection mode (line 390)
        // This returns an ObservableList containing Integer indices of selected items
        ObservableList<?> selectedIndices = ShapeList.getSelectionModel().getSelectedIndices();

        // LINES 2-5: Validate that at least 2 shapes are selected for grouping
        // Check if: (1) selectedIndices is null, (2) list is empty, or (3) less than 2 items selected
        // Grouping requires a minimum of 2 shapes to make sense
        if (selectedIndices == null || selectedIndices.isEmpty() || selectedIndices.size() < 2) {
            // Display error message to user via the Message Label
            Message.setText("Select at least 2 shapes to group!");
            // Exit the method early - no grouping performed
            return;
        }

        // LINE 6: Create a new CompositeShape object to hold the grouped shapes
        // The group is given a unique name using current timestamp: "Group_1234567890"
        // CompositeShape implements the Composite pattern to treat groups as single shapes
        CompositeShape group = new CompositeShape("Group_" + System.currentTimeMillis());

        // LINES 7-12: Collect the actual shape objects from the selected indices
        // Create a temporary ArrayList to store references to the selected iShape objects
        java.util.List<iShape> selectedShapes = new java.util.ArrayList<>();
        // Iterate through each selected index from the ListView
        for (Object index : selectedIndices) {
            // Type-check to ensure the index is an Integer (defensive programming)
            if (index instanceof Integer) {
                // Get the shape at this index from the main shapeList and add to selectedShapes
                selectedShapes.add(shapeList.get((Integer) index));
            }
        }

        // LINES 13-16: Remove the selected shapes from the main shapeList
        // IMPORTANT: Iterate in REVERSE order (from highest index to lowest)
        // Reason: Removing items changes indices - removing from end preserves lower indices
        // Example: If removing indices [1,3,5], remove 5 first, then 3, then 1
        for (int i = selectedIndices.size() - 1; i >= 0; i--) {
            // Get the index value at position i in the selectedIndices list
            Integer index = (Integer) selectedIndices.get(i);
            // Remove the shape at this index from the main shapeList
            shapeList.remove((int) index);
        }

        // LINES 17-19: Add each selected shape to the new CompositeShape group
        // This builds the parent-child relationship in the Composite pattern
        for (iShape shape : selectedShapes) {
            // Call addShape() on the CompositeShape to add this child shape
            group.addShape(shape);
        }

        // LINE 20: Add the newly created group to the main shapeList
        // The group is now treated as a single shape in the list
        // All children are contained within this composite and no longer appear individually
        shapeList.add(group);

        // LINE 21: Display success message showing how many shapes were grouped
        Message.setText("Grouped " + selectedShapes.size() + " shapes!");
        
        // LINE 22: Refresh the canvas to redraw all shapes with the new grouping
        // This clears the canvas and redraws all shapes including the new group
        refresh(CanvasBox);
        
        // LINE 23: Update the ShapeList ListView to reflect the changes
        // The grouped shapes are replaced by a single "Group_..." entry
        ShapeList.setItems(getStringList());
    }

    /**
     * Ungroup a composite shape into individual shapes
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
}
