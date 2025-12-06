package paint.model;

import paint.controller.FXMLDocumentController;

public class ListObserver implements Observer {

    private FXMLDocumentController controller;

    public ListObserver(FXMLDocumentController controller) {
        this.controller = controller;
    }

    @Override
    public void update() {
        controller.updateShapeList();
    }
}
