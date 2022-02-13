package bo.cll.pdf.templates;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class CustomTemplate {
    private final String version;
    private final Appearance appearance;
    private final ColorRGB borderColor;
    private final ColorRGB backgroundColor;
    private final Collection<Field> fields;

    public Collection<Field> getFields() {
        return new ArrayList<>(fields);
    }
}
