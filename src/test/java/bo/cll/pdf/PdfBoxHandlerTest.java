package bo.cll.pdf;

import bo.cll.pdf.templates.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.testng.annotations.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

public class PdfBoxHandlerTest {

    public static final String JSON_FILE = /*"C:\\Workspace\\bozka-excel\\src\\test\\resources\\" + */"template-config-v1.json";
    private static final String TEMPLATE_FILE = /*"C:\\Workspace\\bozka-excel\\src\\test\\resources\\*/"test.pdf";
    private static final String DESTINATION_FILE = /*"C:\\Workspace\\bozka-excel\\src\\test\\resources\\*/"result.pdf";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @SneakyThrows
    private Path getResourcePath(String resourceName) {
        Path resourcePath = Paths.get("./src/test/resources/" + resourceName);
        //assertThat(Files.exists(resourcePath)).isTrue();
        return resourcePath;
    }

    @Test
    public void testName() {
        Path resourcePath = getResourcePath(JSON_FILE);
        assertThat(resourcePath).isNotNull();
        try (FileReader fileReader = new FileReader(resourcePath.toString())) {
            assertThat(fileReader).isNotNull();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.out);
            fail("Resource should exist!");
        }
    }

    @Test
    public void givenTemplate_whenAddField_thenProducePdf() {
        try (FileReader fileReader = new FileReader(getResourcePath(JSON_FILE).toString())) {
            CustomTemplate customTemplate = GSON.fromJson(fileReader, CustomTemplate.class);

            PdfBoxHandler handler = new PdfBoxHandler(
                    getResourcePath(TEMPLATE_FILE),
                    getResourcePath(DESTINATION_FILE),
                    customTemplate
            );
            handler.addField();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    @Test
    public void givenCustomTemplate_whenAddFields_thenCreateProperPdf() {
        try (FileReader fileReader = new FileReader(getResourcePath(JSON_FILE).toString())) {
            CustomTemplate customTemplate = GSON.fromJson(fileReader, CustomTemplate.class);
            PdfBoxHandler handler = new PdfBoxHandler(
                    getResourcePath(TEMPLATE_FILE),
                    getResourcePath(DESTINATION_FILE),
                    customTemplate
            );
            Map<String, String> nameValueMap = Map.of(
                    "excelHeader1", "to-be-set-1",
                    "excelHeader2", "to-be-set-2",
                    "excelHeader3", "to-be-set-3",
                    "thisDoesNotExist", "nothing"
            );

            handler.addFields(nameValueMap);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.out);
            fail("Should not throw any exception");
        }
    }

    @Test
    public void generateJson() {
        ColorRGB black = new ColorRGB(0, 0, 0);
        ColorRGB white = new ColorRGB(255, 255, 255);
        // /Helv 9 Tf 0 0 0 rg
        CustomTemplate customTemplate = new CustomTemplate(
                JSON_FILE,
                new Appearance("Helv", 9, black),
                white,
                white,
                List.of(new Field("excelHeader1", "to-be-set", 25f, 350f, 70f, 13f, 1, true),
                        new Field("excelHeader2", "to-be-set", 100f, 350f, 70f, 13f, 1, true),
                        new Field("excelHeader3", "to-be-set", 175f, 350f, 70f, 13f, 1, true)
                )
        );

        try (Writer writer = new FileWriter(getResourcePath(JSON_FILE).toString())) {
            GSON.toJson(customTemplate, writer);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.out);
        }
    }
}