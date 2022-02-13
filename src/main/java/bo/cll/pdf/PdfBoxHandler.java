package bo.cll.pdf;

import bo.cll.pdf.templates.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class PdfBoxHandler {

    private final Path pathToPdfTemplate;
    private final Path destinationPath;
    private final CustomTemplate customTemplate;
    private final Map<String, Field> nameFieldMap;

    public PdfBoxHandler(Path pathToPdfTemplate, Path destinationPath, CustomTemplate customTemplate) {
        this.pathToPdfTemplate = pathToPdfTemplate;
        this.destinationPath = destinationPath;
        this.customTemplate = customTemplate;
        nameFieldMap = Optional.ofNullable(customTemplate).map(
                ct -> ct.getFields().stream()
                        .collect(toMap(Field::getName, field -> field, (v1, v2) -> v1))
        ).orElseGet(HashMap::new);
    }

    public void addField() {
        try (PDDocument pdDocument = PDDocument.load(Files.newInputStream(pathToPdfTemplate))) {
            Appearance appearance = customTemplate.getAppearance();

            for (int pageNumber = 0; pageNumber < pdDocument.getNumberOfPages(); ++pageNumber) {

                PDPage page = pdDocument.getPage(pageNumber);

                PDAcroForm form = new PDAcroForm(pdDocument);
                pdDocument.getDocumentCatalog().setAcroForm(form);

                PDFont font = PDType1Font.HELVETICA;
                PDResources resources = new PDResources();
                //resources.put(COSName.getPDFName("Helv"), font);
                resources.put(COSName.getPDFName(appearance.getFontName()), font);
                form.setDefaultResources(resources);

                for (Field field : customTemplate.getFields()) {
                    if (field.getPageNumber() == pageNumber + 1) {
                        PDTextField textField = new PDTextField(form);
                        textField.setPartialName(field.getName());

                        //String defaultAppearance = "/Helv 9 Tf 0 0 0 rg";
                        textField.setDefaultAppearance(appearance.generate());

                        form.getFields().add(textField);

                        PDAnnotationWidget widget = textField.getWidgets().get(0);
                        PDRectangle rect = new PDRectangle(field.getXPos(), field.getYPos(), field.getWidth(), field.getHeight());
                        widget.setRectangle(rect);
                        widget.setPage(page);

                        PDAppearanceCharacteristicsDictionary fieldAppearance = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
                        fieldAppearance.setBorderColour(new PDColor(customTemplate.getBorderColor().array(), PDDeviceRGB.INSTANCE));
                        fieldAppearance.setBackground(new PDColor(customTemplate.getBackgroundColor().array(), PDDeviceRGB.INSTANCE));
                        widget.setAppearanceCharacteristics(fieldAppearance);

                        widget.setPrinted(field.isPrintable());

                        page.getAnnotations().add(widget);

                        textField.setValue(field.getValue());
                        log.debug("Field [{}] created.", field.getName());
                    }
                }
            }

            pdDocument.save(destinationPath.toString());
        } catch (IOException e) {
            log.error("Something ent wrong", e);
        }
    }

    public void addFields(Map<String, String> nameValueMap) {
        log.info("Processing excel values for pdf template [{}].", customTemplate.getVersion());
        try (PDDocument pdDocument = PDDocument.load(Files.newInputStream(pathToPdfTemplate))) {
            Appearance appearance = customTemplate.getAppearance();

            for (Map.Entry<String, String> entry : nameValueMap.entrySet()) {
                Optional<Field> fieldOpt = Optional.ofNullable(nameFieldMap.get(entry.getKey()));
                if (fieldOpt.isPresent()) {
                    Field field = fieldOpt.get();

                    PDPage page = pdDocument.getPage(field.getPageNumber() - 1);
                    PDAcroForm form = new PDAcroForm(pdDocument);
                    pdDocument.getDocumentCatalog().setAcroForm(form);

                    PDFont font = PDType1Font.HELVETICA;
                    PDResources resources = new PDResources();
                    resources.put(COSName.getPDFName(appearance.getFontName()), font);
                    form.setDefaultResources(resources);

                    PDTextField textField = new PDTextField(form);
                    textField.setPartialName(field.getName());
                    textField.setDefaultAppearance(appearance.generate());

                    form.getFields().add(textField);

                    PDAnnotationWidget widget = textField.getWidgets().get(0);
                    PDRectangle rect = new PDRectangle(field.getXPos(), field.getYPos(), field.getWidth(), field.getHeight());
                    widget.setRectangle(rect);
                    widget.setPage(page);

                    PDAppearanceCharacteristicsDictionary fieldAppearance = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
                    fieldAppearance.setBorderColour(new PDColor(customTemplate.getBorderColor().array(), PDDeviceRGB.INSTANCE));
                    fieldAppearance.setBackground(new PDColor(customTemplate.getBackgroundColor().array(), PDDeviceRGB.INSTANCE));
                    widget.setAppearanceCharacteristics(fieldAppearance);

                    widget.setPrinted(field.isPrintable());

                    page.getAnnotations().add(widget);

                    textField.setValue(Optional.ofNullable(entry.getValue()).orElseGet(field::getValue));
                    log.info("Field [{}] created and value [{}] is set.", entry.getKey(), entry.getValue());
                } else {
                    log.info("Field [{}] with value [{}] is not defined in the template [{}].",
                            entry.getKey(), entry.getValue(), customTemplate.getVersion());
                }
            }

            pdDocument.save(destinationPath.toString());
        } catch (IOException e) {
            log.error("Something ent wrong", e);
        }
    }

}
