package uskysd.smartvolley.graphics;

public class RotationBoard extends RectangleArea {

    private int rotation = 1;

    public RotationBoard(int x, int y, int width, int height, int color) {
        super(x, y, width, height, color);

        // Initialize rotation
        setRotation(1);
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
        //setText(Integer.toString(rotation));
        //use roman numerals for rotation numbers
        switch (rotation) {
            case 1:
                setText("\u2160");
                break;
            case 2:
                setText("\u2161");
                break;
            case 3:
                setText("\u2162");
                break;
            case 4:
                setText("\u2163");
                break;
            case 5:
                setText("\u2164");
                break;
            case 6:
                setText("\u2165");
                break;
            default:
                setText("");
        }
    }

    public int getRotation() {
        return rotation;
    }
}
