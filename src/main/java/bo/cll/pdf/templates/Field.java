package bo.cll.pdf.templates;

import lombok.Data;

@Data
public class Field {
    private final String name;
    private final String value;
    private final float xPos;
    private final float yPos;
    private final float width;
    private final float height;
    private final int pageNumber;
    private final boolean printable;
}
