import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XMLEditorRWUIController extends Application {

    // variables

    public String currentFileType;

    public String ThingDef  = "\\thingDef_Base.xml";

    public String ThingDef_Item_Base  = "\\thingDef_Item_Base.xml";

    public String ThingDef_Item  = "\\thingDef_Item_Generic.xml";

    public String RecipeDef;

    // fxml variables

    @FXML
    public Stage primaryStage;

    @FXML
    public Label dialog;

    @FXML
    public TextArea textArea;

    @FXML
    public DirectoryChooser directoryChooser;

    @FXML
    public FileChooser fileChooser;

    @FXML
    public ChoiceBox<String> cbTD;

    @FXML
    public ChoiceBox<String> cbTDI;

    @FXML
    public ChoiceBox<String> cbTDIG;

    @FXML
    public void initialize() {

        // header message
        System.out.println("----- BEGINNING TO INITIALIZE -----");

        // set up the choice boxes
        cbTD.getItems().addAll("No Values", "Default Values");
        cbTDI.getItems().addAll("No Values", "Default Values");
        cbTDIG.getItems().addAll("No Values", "Default Values");



        System.out.println("Choice boxes Initialized!");

        // footer message
        System.out.println("----- END INITIALIZE -----");
    }

    public static void main(String[] args) {

        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {

        // creates the root, sets it equal to the .fxml file and then sets the stage
        Parent root = FXMLLoader.load(getClass().getResource("XMLEditorRWUI.fxml"));
        primaryStage.setTitle("XML Editor RW v0.0.1");
        primaryStage.setScene(new Scene(root, 1000, 1000));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public void save(ActionEvent actionEvent) throws IOException {

        // messages
        dialog.setText("Saving...");

        // set up directory chooser, selectedDirectory and a new file to save to
        directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        File f = new File(selectedDirectory + currentFileType);

        // write to file
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(f,true);
            fw.write(textArea.getText());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (fw!=null)
            {
                fw.close();
            }
        }

        //
        dialog.setText("Saved");
    }

    public void about(ActionEvent actionEvent) {

        // creates a new alert popup box when the about button is clicked
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("XML Editor RW v0.0.1 help");
        alert.setHeaderText(null);
        alert.setContentText("XML Editor RW v0.0.1 was written in java 8 for Rimworld 1.3");
        alert.showAndWait();
    }

    public void close(ActionEvent actionEvent) {

        System.exit(0);
    }

    public void openFile() throws IOException {

        // messages
        dialog.setText("Opening file...");

        // set up directory chooser, selectedDirectory and a new file to save to
        fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {

            // set up the buffered reader
            BufferedReader br = new BufferedReader(new FileReader(selectedFile));

            // clear the text area
            textArea.clear();

            // while br is ready do...
            while (br.ready()){
                // set the text area
                textArea.appendText(br.readLine());
                textArea.appendText(System.getProperty("line.separator"));
            }
            // close the br
            br.close();

            // messages
            System.out.println("File opened");
        }
    }

    public void generateThingDefBase(ActionEvent actionEvent) throws ParserConfigurationException {

        // messages
        dialog.setText("Generating ThingDef (base)...");

        // currentFileType
        currentFileType = ThingDef;

        // instantiate the document builder, the factory and document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // create the root element node
        Element defs = doc.createElement("Defs");
        doc.appendChild(defs);

        // create the textures element
        Element thingDef = doc.createElement("ThingDef");
        defs.appendChild(thingDef);





        if (cbTD.getSelectionModel().isSelected(0)) {

            //
            System.out.println("No values selected");



        } else if (cbTD.getSelectionModel().isSelected(1)){

            //
            System.out.println("Default values selected");




        }

        // messages
        dialog.setText("ThingDef (base) generated");
    }

    public void GenerateThingDef_Item_Base(ActionEvent actionEvent) throws ParserConfigurationException, TransformerException {

        // messages
        dialog.setText("Generating ThingDef_Item_Base (generic)...");

        // currentFileType
        currentFileType = ThingDef_Item_Base;

        // instantiate the document builder, the factory and document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // create the Def element
        Element defs = doc.createElement("Defs");
        doc.appendChild(defs);

        // create the ThingDef element
        Element thingDef = doc.createElement("ThingDef");
        defs.appendChild(thingDef);

        // set the ThingDef attributes
        thingDef.setAttribute("Abstract", " ");
        thingDef.setAttribute("Name", " ");
        thingDef.setAttribute("ParentName", " ");

        // create the thingClass element
        Element thingClass = doc.createElement("thingClass");
        thingDef.appendChild(thingClass);
        thingClass.setTextContent(" ");

        // create the thingClass element
        Element stackCount = doc.createElement("stackCount");
        thingDef.appendChild(stackCount);
        stackCount.setTextContent(" ");

        // TODO finish resource base item set up






        if (cbTDI.getSelectionModel().isSelected(0)) {

            // messages
            System.out.println("No values selected");

        } else if (cbTDI.getSelectionModel().isSelected(1)) {

            // messages
            System.out.println("Default values selected");

            // set the Def attributes
            thingDef.setAttribute("Abstract", "true");
            thingDef.setAttribute("Name", "GenericItemBase");
            thingDef.setAttribute("ParentName", "ResourceBase");

            // TODO finish resource base item setters





        }

        // create the transformer to output the document
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(doc), new StreamResult(out));

        // set to text area
        textArea.clear();
        textArea.setText(out.toString());

        // messages
        dialog.setText("ThingDef_Item_Base generated");
    }

    public void GenerateThingDef_Item_Generic(ActionEvent actionEvent) throws ParserConfigurationException, TransformerException {

        // messages
        dialog.setText("Generating ThingDef_Item_Generic...");

        // currentFileType
        currentFileType = ThingDef_Item;

        // instantiate the document builder, the factory and document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // create the Def element
        Element defs = doc.createElement("Defs");
        doc.appendChild(defs);

        // create the ThingDef element
        Element thingDef = doc.createElement("ThingDef");
        defs.appendChild(thingDef);

        // set the ThingDef attributes
        thingDef.setAttribute("ParentName", " ");

        // create the defName element
        Element defName = doc.createElement("defName");
        thingDef.appendChild(defName);
        defName.setTextContent(" ");

        // create the label element
        Element label = doc.createElement("label");
        thingDef.appendChild(label);
        label.setTextContent(" ");

        // create the description element
        Element description = doc.createElement("description");
        thingDef.appendChild(description);
        description.setTextContent(" ");

        //





        if (cbTDIG.getSelectionModel().isSelected(0)) {

            // messages
            System.out.println("No values selected");

        } else if (cbTDIG.getSelectionModel().isSelected(1)) {

            // messages
            System.out.println("Default values selected");

            // set the Def attributes
            thingDef.setAttribute("ParentName", "ResourceBase");

            //



        }

        // create the transformer to output the document
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(doc), new StreamResult(out));

        // set to text area
        textArea.clear();
        textArea.setText(out.toString());

        // messages
        dialog.setText("ThingDef_Item_Generic generated");
    }
}

