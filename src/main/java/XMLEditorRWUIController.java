import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;

import javafx.scene.input.Clipboard;

public class XMLEditorRWUIController extends Application {

    // program variables

    // primtive variables

    public String currentFileType;

    public File selectedDirectory;

    public String ThingDef_Base  = "\\thingDef_Base.xml";

    public String ThingDef_Item_Base  = "\\thingDef_Item_Base.xml";

    public String ThingDef_Item_Generic  = "\\thingDef_Item_Generic.xml";

    // fxml variables

    @FXML
    public Stage primaryStage;

    @FXML
    public Label dialog;

    @FXML
    public TextArea textArea;

    @FXML
    public DirectoryChooser directoryChooserS;

    @FXML
    public FileChooser fileChooserOF;

    @FXML
    public DirectoryChooser directoryChooserMT;

    @FXML
    public ChoiceBox<String> cbTD;

    @FXML
    public ChoiceBox<String> cbTDI;

    @FXML
    public ChoiceBox<String> cbTDIG;

    // program methods

    // all the bits to launch the program
    // and initialize the used variables

    @FXML
    public void initialize() {

        // header message
        System.out.println("----- BEGINNING TO INITIALIZE -----");

        // set up the choice boxes
        cbTD.getItems().addAll("No Values", "Default Values");
        cbTDI.getItems().addAll("No Values", "Default Values");
        cbTDIG.getItems().addAll("No Values", "Default Values");

        System.out.println("Choice boxes initialized!");

        // footer message
        System.out.println("----- END INITIALIZE -----");
    }

    public static void main(String[] args) {

        // launches the program
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {

        // creates the root, sets it equal to the .fxml file and then sets the stage
        Parent root = FXMLLoader.load(getClass().getResource("XMLEditorRWUI.fxml"));
        primaryStage.setTitle("XML Editor RW v0.0.3");
        primaryStage.setScene(new Scene(root, 1024, 1024));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    // top menu stuff

    public void save(ActionEvent actionEvent) throws IOException {

        // messages
        dialog.setText("Saving...");

        // set up directory chooser, selectedDirectory and a new file to save to
        directoryChooserS = new DirectoryChooser();
        File selectedDirectory = directoryChooserS.showDialog(primaryStage);
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
        alert.setTitle("XML Editor RW v0.0.3 help");
        alert.setHeaderText(null);
        alert.setContentText("XML Editor RW v0.0.3 was written in java 8 for Rimworld 1.3");
        alert.showAndWait();
    }

    public void close(ActionEvent actionEvent) {

        System.exit(0);
    }

    public void openFile() throws IOException {

        // messages
        dialog.setText("Opening file...");

        // set up fileChooser, selectedFile and a new file to save to
        fileChooserOF = new FileChooser();
        File selectedFile = fileChooserOF.showOpenDialog(primaryStage);

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
            dialog.setText("File opened");
        }
    }

    // top menu 2 bar

    public void generateModTemplate(ActionEvent actionEvent) throws IOException, ParserConfigurationException, TransformerException {

        // messages
        dialog.setText("Generating mod template in chosen directory");

        // initialize the directory chooser
        directoryChooserMT = new DirectoryChooser();
        selectedDirectory = directoryChooserMT.showDialog(primaryStage);

        // creates the base directory
        File baseDirectory = new File(selectedDirectory + "\\Mod Template\\1.3");
        Files.createDirectories(baseDirectory.toPath());

        // creates the About directory
        File aboutDirectory = new File(selectedDirectory + "\\Mod Template\\About");
        Files.createDirectory(aboutDirectory.toPath());


        // creates the Defs directory
        File defsDirectory = new File(selectedDirectory + "\\Mod Template\\1.3\\Defs");
        Files.createDirectory(defsDirectory.toPath());

        // creates the Patches directory
        File patchesDirectory = new File(selectedDirectory + "\\Mod Template\\1.3\\Patches");
        Files.createDirectory(patchesDirectory.toPath());

        // creates the Textures directory
        File texturesDirectory = new File(selectedDirectory + "\\Mod Template\\1.3\\Textures");
        Files.createDirectory(texturesDirectory.toPath());


        // creates the Sounds directory
        File soundsDirectory = new File(selectedDirectory + "\\Mod Template\\1.3\\Sounds");
        Files.createDirectory(soundsDirectory.toPath());

        // create the LoadFolders.xml file
        generateLoadFolders();

        // create the about.xml file
        generateAbout();

        // messages
        dialog.setText("Mod template generated in chosen directory");

    }

    // bottom menu bar

    public void copyToClipboard(ActionEvent actionEvent) {

        // set up the clipboard content
        ClipboardContent content = new ClipboardContent();

        // set the content to the textArea
        content.putString(textArea.getText());

        // set up the clipboard and get the system clipboard
        Clipboard clipboard = Clipboard.getSystemClipboard();

        // set the clipboard equal to content
        clipboard.setContent(content);

        // copied to clipboard
        dialog.setText("Copied to clipboard");
    }

    // methods for mod template generation

    public void generateLoadFolders() throws IOException, ParserConfigurationException, TransformerException {

        // instantiate the document builder, the factory and document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // create the loadFolders element node
        Element loadFolders = doc.createElement("loadFolders");
        doc.appendChild(loadFolders);

        // create the v1point3 element node
        Element v1point3 = doc.createElement("v1.3");
        loadFolders.appendChild(v1point3);

        // create the li element node
        Element li = doc.createElement("li");
        v1point3.appendChild(li);
        li.setTextContent("1.3");

        // transform the document
        StringWriter sw = transform(doc);

        // create the file writer
        FileWriter fw = new FileWriter(selectedDirectory + "\\Mod Template\\LoadFolders.xml");

        // write to file
        fw.write(sw.toString());

        // close the stream to
        // prevent memory leaks
        fw.close();
    }

    public void generateAbout() throws IOException, ParserConfigurationException, TransformerException {

        // creates the LoadFolders.xml file
        File About = new File(selectedDirectory + "\\Mod Template\\About\\About.xml");
        Files.createFile(About.toPath());

        // instantiate the document builder, the factory and document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // create the modMetaData element node
        Element modMetaData = doc.createElement("ModMetaData");
        doc.appendChild(modMetaData);

        // create the name element node
        Element name  = doc.createElement("name");
        modMetaData.appendChild(name);
        name.setTextContent(" ");

        // create the author element node
        Element author  = doc.createElement("author");
        modMetaData.appendChild(author);
        author.setTextContent(" ");

        // create the supportedVersions element node
        Element supportedVersions = doc.createElement("supportedVersions");
        modMetaData.appendChild(supportedVersions);

        // create the supportVersions li element node
        Element li = doc.createElement("li");
        supportedVersions.appendChild(li);
        li.setTextContent("1.3");

        // create the packageId element node
        Element packageId  = doc.createElement("packageId");
        modMetaData.appendChild(packageId);
        packageId.setTextContent(" ");

        // create the modDependencies element node
        Element modDependencies  = doc.createElement("modDependencies");
        modMetaData.appendChild(modDependencies);

        // create the modDependencies li element node
        Element li2 = doc.createElement("li");
        modDependencies.appendChild(li2);
        li2.setTextContent(" ");

        // create the description element node
        Element description  = doc.createElement("description");
        modMetaData.appendChild(description);
        description.setTextContent(" ");

        // transform the document
        StringWriter sw = transform(doc);

        // create the file writer
        FileWriter fw = new FileWriter(selectedDirectory + "\\Mod Template\\About\\About.xml");

        // write to file
        fw.write(sw.toString());

        // close the stream to
        // prevent memory leaks
        fw.close();
    }

    // methods for def template generation

    public StringWriter transform (Document doc) throws TransformerException {
        // create the transformer to output the document
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "6");
        StringWriter out = new StringWriter();
        tf.transform(new DOMSource(doc), new StreamResult(out));
        return out;
    }

    // generation method for thingDefs

    public void generateThingDefBase(ActionEvent actionEvent) throws ParserConfigurationException, TransformerException {

        // messages
        dialog.setText("Generating ThingDef (base)...");

        // set currentFileType
        currentFileType = ThingDef_Base;

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

        // TODO finish this section of elements




        if (cbTD.getSelectionModel().isSelected(0)) {

            // messages
            System.out.println("No values selected");

        } else if (cbTD.getSelectionModel().isSelected(1)){

            // messages
            System.out.println("Default values selected");

            // TODO finish this section of setters



        }

        // transform the document
        // and output to a string writer
        StringWriter sw = transform(doc);

        // set to text area
        textArea.clear();
        textArea.setText(String.valueOf(sw));

        // messages
        dialog.setText("ThingDef (base) generated");
    }

    public void generateThingDef_Item_Base(ActionEvent actionEvent) throws ParserConfigurationException, TransformerException {

        // messages
        dialog.setText("Generating ThingDef_Item_Base (generic)...");

        // set currentFileType
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

        // transform the document
        // and output to a string writer
        StringWriter sw = transform(doc);

        // set to text area
        textArea.clear();
        textArea.setText(String.valueOf(sw));

        // messages
        dialog.setText("ThingDef_Item_Base generated");
    }

    public void generateThingDef_Item_Generic(ActionEvent actionEvent) throws ParserConfigurationException, TransformerException {

        // messages
        dialog.setText("Generating ThingDef_Item_Generic...");

        // set currentFileType
        currentFileType = ThingDef_Item_Generic;

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

        // create the graphicData element
        Element graphicData = doc.createElement("graphicData");
        thingDef.appendChild(graphicData);
        graphicData.setTextContent(System.getProperty(System.lineSeparator()));

        // create the graphicClass element
        Element graphicClass = doc.createElement("graphicClass");
        graphicData.appendChild(graphicClass);
        graphicClass.setTextContent(" ");

        // create the texPath element
        Element texPath = doc.createElement("texPath");
        graphicData.appendChild(texPath);
        texPath.setTextContent(" ");

        // create the statBase element
        Element statBase = doc.createElement("statBases");
        thingDef.appendChild(statBase);
        statBase.setTextContent(System.getProperty(System.lineSeparator()));

        // create the Mass element
        Element Mass = doc.createElement("Mass");
        statBase.appendChild(Mass);
        Mass.setTextContent(" ");

        // create the MarketValue element
        Element MarketValue = doc.createElement("MarketValue");
        statBase.appendChild(MarketValue);
        MarketValue.setTextContent(" ");

        // create the stuffProps element
        Element stuffProps = doc.createElement("stuffProps");
        thingDef.appendChild(stuffProps);
        stuffProps.setTextContent(System.getProperty(System.lineSeparator()));

        // create the thingCategories element
        Element thingCategories = doc.createElement("thingCategories");
        thingDef.appendChild(thingCategories);
        thingCategories.setTextContent(System.getProperty(System.lineSeparator()));

        // create the li 1 element
        Element li1 = doc.createElement("li");
        thingCategories.appendChild(li1);
        li1.setTextContent(" ");

        if (cbTDIG.getSelectionModel().isSelected(0)) {

            // messages
            System.out.println("No values selected");

        } else if (cbTDIG.getSelectionModel().isSelected(1)) {

            // messages
            System.out.println("Default values selected");

            // set the Def attributes
            thingDef.setAttribute("ParentName", "ResourceBase");

            // set the graphicClass element
            graphicClass.setTextContent("Graphic_Single");

            // set the texPath element
            texPath.setTextContent("Things/Item");

            // set the li1 element
            li1.setTextContent("Item");

            //


            //



        }

        // transform the document
        // and output to a string writer
        StringWriter sw = transform(doc);

        // set to text area
        textArea.clear();
        textArea.setText(String.valueOf(sw));

        // messages
        dialog.setText("thingDef_Item_Generic generated");
    }

    // TODO more templates





    // bonus template to speed up coding workflow

    public void generateTemplate() throws ParserConfigurationException, TransformerException {

        // messages
        dialog.setText("Generating X...");

        // set currentFileType
        currentFileType = ThingDef_Item_Generic;

        // instantiate the document builder, the factory and document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // createElements and setAttributes here

        // create the Defs element
        //Element defs = doc.createElement("Defs");
        //doc.appendChild(defs);

        //
        //
        //

        // remember to change choice box
        if (cbTDIG.getSelectionModel().isSelected(0)) {

            // messages
            System.out.println("No values selected");

            // leave this empty

        } else if (cbTDIG.getSelectionModel().isSelected(1)) {

            // messages
            System.out.println("Default values selected");

            // set default values here with setters

        }

        // transform the document
        // and output to a string writer
        StringWriter sw = transform(doc);

        // set to text area
        textArea.clear();
        textArea.setText(String.valueOf(sw));

        // messages
        dialog.setText("X generated");
    }
}

