package bo.cll.pdf.templates;

import lombok.Data;

@Data
public class Appearance {
    private final String fontName;
    private final int fontSize;
    private final ColorRGB fontColor;

    public String generate() {
        // /Helv 9 Tf 0 0 0 rg
        return "/" + fontName + " " +
                fontSize + " " +
                "Tf " +
                fontColor.getRed() + " " +
                fontColor.getGreen() + " " +
                fontColor.getBlue() + " " +
                "rg";
    }
}
