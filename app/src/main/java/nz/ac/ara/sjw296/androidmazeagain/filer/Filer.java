package nz.ac.ara.sjw296.androidmazeagain.filer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import nz.ac.ara.sjw296.androidmazeagain.communal.MazePoint;
import nz.ac.ara.sjw296.androidmazeagain.communal.Point;
import nz.ac.ara.sjw296.androidmazeagain.communal.Wall;
import nz.ac.ara.sjw296.androidmazeagain.game.Loadable;
import nz.ac.ara.sjw296.androidmazeagain.game.MazeGame;
import nz.ac.ara.sjw296.androidmazeagain.game.Savable;

/**
 * Created by Sim on 22/06/2017.
 */

public class Filer implements Loader, Saver {
    protected final String SAVE_FILE = "saves.xml";
    protected int currentLevel = -1;
    protected final char SOMETHING = 'x';
    protected final char NOTHING = 'o';

    @Override
    public void save(Savable game) {
        this.saveToXmlFile(game, this.SAVE_FILE, game.getLevelName());
    }

    @Override
    public void save(Savable game, String fileName) {
        this.saveToXmlFile(game, fileName, game.getLevelName());
    }

    @Override
    public void save(Savable game, String fileName, String levelName) {
        this.saveToXmlFile(game, fileName, levelName);
    }

    protected void saveToXmlFile(Savable game, String fileName, String level) {
        this.saveGame(game, this.haveFileWantDoc(fileName), level, fileName);
    }

    protected boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    protected Document createXmlFile() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;

            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element root = doc.createElement("mazes");
            doc.appendChild(root);
            return doc;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Document haveFileWantDoc(String fileName) {
        try {
            return this.getXmlDoc(new FileInputStream(fileName));
        }
        catch (IOException e) {
            return this.createXmlFile();
        }
    }

    protected void saveGame(Savable game, Document doc, String levelName, String fileName) {
        Element rootElement = doc.getDocumentElement();
        Element mazeElement = doc.createElement("maze");
        NodeList nodes = doc.getElementsByTagName("maze");

        if (nodes.getLength() > 0) {
            nodes.item(0).getParentNode().insertBefore(mazeElement, nodes.item(0));
        }
        else {
            rootElement.appendChild(mazeElement);
        }

        Element nameElement = this.makeNode(doc, mazeElement, "name");
        this.addTextToNode(doc, nameElement, levelName);
        Element wallsElement = this.makeNode(doc, mazeElement, "walls");
        Element hWallsElement = this.makeNode(doc, wallsElement, "horizontal");
        Element vWallsElement = this.makeNode(doc, wallsElement, "vertical");

        this.addWallsNodes(doc, hWallsElement,"whatsAbove", game);
        this.addWallsNodes(doc, vWallsElement, "whatsLeft", game);

        Element positionsElement = this.makeNode(doc, mazeElement, "positions");
        this.addPositionNode(doc, positionsElement, "theseus", game.wheresTheseus());
        this.addPositionNode(doc, positionsElement, "minotaur", game.wheresMinotaur());
        this.addPositionNode(doc, positionsElement, "exit", game.wheresExit());

        this.saveXmlToFile(doc, fileName);
    }

    protected void saveXmlToFile(Document doc, String fileName) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new StringWriter());

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //android
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, result);

            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);

            String xmlString = result.getWriter().toString();
            fos.write(xmlString.getBytes());
            fos.flush();
            fos.close();
        }
        catch (IOException |TransformerException e) {
            e.printStackTrace();
        }
    }

    protected Element makeNode(Document doc, Element parent, String child) {
        Element thisElement = doc.createElement(child);
        parent.appendChild(thisElement);
        return thisElement;
    }

    protected void addTextToNode(Document doc, Element parent, String text) {
        Text textNode = doc.createTextNode(text);
        parent.appendChild(textNode);
    }

    protected String makeWallText(String wallMethod, Savable game, int row) {
        int cols = game.getWidthAcross();
        StringBuilder rowText = new StringBuilder();

        for (int c = 0; c < cols; c++) {
            try {
                Method m = MazeGame.class.getDeclaredMethod(wallMethod, Point.class);

                if (m.invoke(game, new MazePoint(row, c)) == Wall.SOMETHING) {
                    rowText.append(this.SOMETHING);
                }
                else {
                    rowText.append(this.NOTHING);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rowText.toString();
    }

    protected void addWallsNodes(Document doc, Element parent, String wallMethod, Savable game) {
        int rows = game.getDepthDown();

        for (int r = 0; r < rows; r++) {
            Element wallRow = this.makeNode(doc, parent, "row");
            String wallText = this.makeWallText(wallMethod, game, r);
            this.addTextToNode(doc, wallRow, wallText);
        }
    }

    protected void addPositionNode(Document doc, Element pos, String thing, Point p) {
        Element characterElement = this.makeNode(doc, pos, thing);
        Element xElement = this.makeNode(doc, characterElement, "x");
        this.addTextToNode(doc, xElement, String.valueOf(p.getCol()));
        Element yElement = this.makeNode(doc, characterElement, "y");
        this.addTextToNode(doc, yElement, String.valueOf(p.getRow()));
    }

    @Override
    public void loadNextLevel(Loadable game, InputStream inputStream) {
        this.loadLevel(game, ++this.currentLevel, inputStream);
    }

    @Override
    public void loadLevel(Loadable game, int level, InputStream inputStream) {
        this.loadLevelOrLoadSaveByNumber(game, level, getXmlDoc(inputStream));
    }

    protected NodeList getMazeList(Document doc) {
        try {
            XPath xPath =  XPathFactory.newInstance().newXPath();
            String expression = "/mazes/maze";
            return (NodeList)xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void loadLevelOrSaveIntoGame(Loadable game, Element mazeElement) {
        //get name
        game.setName(mazeElement.getElementsByTagName("name").item(0).getTextContent());
        //get rows
        NodeList hRows = getWallsList(mazeElement, "horizontal");
        NodeList vRows = getWallsList(mazeElement, "vertical");

        game.setDepthDown(hRows.getLength());
        game.setWidthAcross(vRows.item(0).getTextContent().length());

        addWalls(game, "addWallLeft", vRows);
        addWalls(game, "addWallAbove", hRows);

        Element positions = (Element)mazeElement.getElementsByTagName("positions").item(0);
        //NodeList positions = mazeElement.getElementsByTagName("positions").item(0).getChildNodes();
        //get minotaur
        game.addMinotaur(this.getPosition(positions, "minotaur"));
        //get theseus
        game.addTheseus(this.getPosition(positions, "theseus"));
        //get exit
        game.addExit(this.getPosition(positions, "exit"));
    }

    protected void loadLevelOrLoadSaveByName(Loadable game, String level, Document doc) {
        NodeList mazeList = this.getMazeList(doc);

        for (int i = 0; i < mazeList.getLength(); i++) {
            Element currentElement = (Element)mazeList.item(i);
            if (currentElement.getElementsByTagName("name").item(0).getTextContent().equals(level)) {
                this.loadLevelOrSaveIntoGame(game, currentElement);
            }
        }
    }

    protected void loadLevelOrLoadSaveByNumber(Loadable game, int level, Document doc) {
//        Document doc = this.getXmlDoc(fileName);
        NodeList mazeList = this.getMazeList(doc);
        int levelToLoad = level % mazeList.getLength();
        Node mazeNode = mazeList.item(levelToLoad);
        Element mazeElement = (Element)mazeNode;
        this.loadLevelOrSaveIntoGame(game, mazeElement);
    }

    protected NodeList getWallsList(Element maze, String type) {
        Node walls = maze.getElementsByTagName("walls").item(0);
        Element wallsE = ((Element)walls);
        Node hWalls = wallsE.getElementsByTagName(type).item(0);
        Element hWallsE = (Element)hWalls;
        return hWallsE.getElementsByTagName("row");
    }

    protected Point getPosition(Element pos, String thing) {
        Element thingPos = (Element)pos.getElementsByTagName(thing).item(0);
        int row = Integer.parseInt(thingPos.getElementsByTagName("y").item(0).getTextContent());
        int col = Integer.parseInt(thingPos.getElementsByTagName("x").item(0).getTextContent());
        return new MazePoint(row, col);
    }

    protected void addWalls(Loadable game, String wallMethod, NodeList wallRows) {
        for (int i = 0; i < wallRows.getLength(); i++) {
            String row = wallRows.item(i).getTextContent();
            for (int j = 0; j < row.length(); j++) {
                if (row.charAt(j) == this.SOMETHING) {
                    try {
                        Method c = MazeGame.class.getDeclaredMethod(wallMethod, Point.class);
                        c.invoke(game, new MazePoint(i, j));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected Document getXmlDoc(InputStream inputStream) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;

            dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            inputStream.close();
            return doc;
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            e.getCause();
        }
        return null;
    }

    @Override
    public void loadSave(Loadable game, String fileName) {
        try {
            InputStream inputStream = new FileInputStream(fileName);
            this.loadLevelOrLoadSaveByNumber(game, 0, getXmlDoc(inputStream));
        }
        catch (IOException e) {
            e.getCause();
        }
    }

    @Override
    public void loadSave(Loadable game) {
        loadSave(game, this.SAVE_FILE);
    }

    @Override
    public void loadSave(Loadable game, String fileName, String level) {
        try {
            InputStream inputStream = new FileInputStream(fileName);
            this.loadLevelOrLoadSaveByName(game, level, getXmlDoc(inputStream));
        }
        catch (IOException e) {
            e.getCause();
        }
    }

    @Override
    public String[] getLevelNamesFromFile(InputStream inputStream) {
        Document doc = getXmlDoc(inputStream);
        NodeList mazeList = this.getMazeList(doc);
        List<String> levelNames = new ArrayList<>();

        for (int i = 0; i < mazeList.getLength(); i++) {
            Element currentElement = (Element)mazeList.item(i);
            levelNames.add(currentElement.getElementsByTagName("name").item(0).getTextContent());
        }

        return levelNames.toArray(new String[levelNames.size()]);
    }

    @Override
    public int getCurrentLevel() {
        return currentLevel;
    }
}
