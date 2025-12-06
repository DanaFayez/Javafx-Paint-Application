package paint.model;

import paint.controller.FXMLDocumentController;

public class CanvasObserver implements Observer {

    private FXMLDocumentController controller;

    public CanvasObserver(FXMLDocumentController controller) {
        this.controller = controller;
    }

    @Override
    public void update() {
        controller.redrawCanvas();
    }
}
