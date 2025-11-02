package paint.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BorderDecorator extends ShapeDecorator {

    private double borderWidth;
    private Color borderColor;

    public BorderDecorator(iShape decoratedShape, double borderWidth, Color borderColor) {
        super(decoratedShape);
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }

// paint.model.BorderDecorator.draw() - الحل المقترح
    @Override
    public void draw(Canvas canvas) {
        // 1. حفظ خصائص الحد الأصلية (قبل التغيير)
        Color originalColor = decoratedShape.getColor(); // لون الحد الأصلي
        double originalLineWidth = canvas.getGraphicsContext2D().getLineWidth(); // سمك الحد الأصلي

        // 2. تطبيق خصائص الحد الجديدة على الشكل المزين
        // نغير لون الحد في الكائن المزين مؤقتاً
        decoratedShape.setColor(borderColor);
        canvas.getGraphicsContext2D().setLineWidth(borderWidth);

        // 3. نرسم الشكل مرة واحدة. (يفترض أن الشكل الأساسي سيستخدم اللون والسمك الجديدين)
        decoratedShape.draw(canvas);

        // 4. إعادة الخصائص الأصلية للرسم التالي
        decoratedShape.setColor(originalColor);
        canvas.getGraphicsContext2D().setLineWidth(originalLineWidth);
    }
}
