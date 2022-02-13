package bo.cll.pdf;

import bo.cll.pdf.templates.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testng.annotations.Test;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class PdfBoxHandlerTest {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void run() {
        String templatePath = "C:\\Workspace\\bozka-excel\\src\\test\\resources\\test.pdf";
        String destinationPath = "C:\\Workspace\\bozka-excel\\src\\test\\resources\\result.pdf";

        try (FileReader fileReader = new FileReader("C:\\Workspace\\bozka-excel\\src\\test\\resources\\template-config-v1.json")) {
            CustomTemplate customTemplate = gson.fromJson(fileReader, CustomTemplate.class);

            PdfBoxHandler handler = new PdfBoxHandler(Path.of(templatePath), destinationPath, customTemplate);
            handler.addField();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    @Test
    public void generateJson() {
        ColorRGB black = new ColorRGB(0, 0, 0);
        ColorRGB white = new ColorRGB(255, 255, 255);
        // /Helv 9 Tf 0 0 0 rg
        CustomTemplate customTemplate = new CustomTemplate(
                new Appearance("Helv", 9, black),
                white,
                white,
                List.of(new Field("excelHeader1", "UserName", 25f, 350f, 70f, 13f, 1, true),
                        new Field("excelHeader2", "UserName2", 100f, 350f, 70f, 13f, 1, true),
                        new Field("excelHeader3", "UserName3", 175f, 350f, 70f, 13f, 1, true)
                )
        );

        try (Writer writer = new FileWriter("C:\\Workspace\\bozka-excel\\src\\test\\resources\\template-config-v1.json")) {
            gson.toJson(customTemplate, writer);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.out);
        }
    }
}