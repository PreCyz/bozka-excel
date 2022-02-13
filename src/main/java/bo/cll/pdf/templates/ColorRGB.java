package bo.cll.pdf.templates;

import lombok.Data;

@Data
public class ColorRGB {
    private final int red;
    private final int green;
    private final int blue;

    public float[] array() {
        return new float[]{red, green, blue};
    }
}
