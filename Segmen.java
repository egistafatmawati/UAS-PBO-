package aplikasi;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Segmen extends GameObject {
    public Segmen(int x, int y) {
        super(x, y);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(getX() * 25, getY() * 25, 24, 24);
        gc.setFill(Color.GREEN);
        gc.fillRect(getX() * 25 + 1, getY() * 25 + 1, 22, 22);
    }
}
