package aplikasi;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class Makanan extends GameObject {
    private static Random rand = new Random();
    private Color warna;

    public Makanan() {
        super(rand.nextInt(20), rand.nextInt(20));  
        this.warna = pilihWarna();
    }

    public Color getWarna() { return warna; }

    private Color pilihWarna() {
        switch (rand.nextInt(5)) {
            case 0: return Color.PURPLE;
            case 1: return Color.LIGHTBLUE;
            case 2: return Color.YELLOW;
            case 3: return Color.PINK;
            case 4: return Color.ORANGE;
            default: return Color.WHITE;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(warna);
        gc.fillOval(getX() * 25, getY() * 25, 25, 25);
    }
}
