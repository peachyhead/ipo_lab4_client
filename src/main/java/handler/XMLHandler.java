package handler;

import data.VisualObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class XMLHandler {
    
    public static String serializeVisualObject(VisualObject visualObject) {
        DocumentBuilder dBuilder;
        var dbFactory = DocumentBuilderFactory.newInstance();
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            var doc = dBuilder.newDocument();

            // Создаем корневой элемент <GraphicObject>
            var graphicObjectElement = doc.createElement("GraphicObject");
            doc.appendChild(graphicObjectElement);
            
            var shapeTypeElement = doc.createElement("ShapeType");
            shapeTypeElement.appendChild(doc.createTextNode(getShapeType(visualObject.shape())));
            graphicObjectElement.appendChild(shapeTypeElement);

            handleShape(visualObject, doc, graphicObjectElement);
            handleColor(visualObject, doc, graphicObjectElement);
            handleStroke(visualObject, doc, graphicObjectElement);

            // Преобразуем документ в строку XML
            var xmlOutputStream = getXML(doc);
            return xmlOutputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static ByteArrayOutputStream getXML(Document doc) throws TransformerException {
        var transformerFactory = TransformerFactory.newInstance();
        var transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        var domSource = new DOMSource(doc);
        var xmlOutputStream = new ByteArrayOutputStream();
        var result = new StreamResult(xmlOutputStream);
        transformer.transform(domSource, result);
        return xmlOutputStream;
    }

    // Добавляем элемент для стиля линии (Stroke)
    private static void handleStroke(VisualObject visualObject, Document doc, 
                                     Element graphicObjectElement) {
        var strokeElement = doc.createElement("Stroke");
        if (visualObject.stroke() instanceof BasicStroke basicStroke) {
            strokeElement.setAttribute("width", String.valueOf(basicStroke.getLineWidth()));
            strokeElement.setAttribute("cap", String.valueOf(basicStroke.getEndCap()));
            strokeElement.setAttribute("join", String.valueOf(basicStroke.getLineJoin()));
        }
        graphicObjectElement.appendChild(strokeElement);
    }

    // Добавляем элемент для цвета
    private static void handleColor(VisualObject visualObject, Document doc, 
                                    Element graphicObjectElement) {
        var colorElement = doc.createElement("Color");
        colorElement.setAttribute("r", String.valueOf(visualObject.color().getRed()));
        colorElement.setAttribute("g", String.valueOf(visualObject.color().getGreen()));
        colorElement.setAttribute("b", String.valueOf(visualObject.color().getBlue()));
        graphicObjectElement.appendChild(colorElement);
    }

    // Добавляем элемент для типа формы
    private static void handleShape(VisualObject visualObject, Document doc, 
                                    Element graphicObjectElement) {
        // Добавляем элемент для координат и размеров
        if (visualObject.shape() instanceof RectangularShape rectShape) {
            var boundsElement = doc.createElement("Bounds");
            boundsElement.setAttribute("x", String.valueOf(rectShape.getX()));
            boundsElement.setAttribute("y", String.valueOf(rectShape.getY()));
            boundsElement.setAttribute("width", String.valueOf(rectShape.getWidth()));
            boundsElement.setAttribute("height", String.valueOf(rectShape.getHeight()));
            graphicObjectElement.appendChild(boundsElement);
        } else if (visualObject.shape() instanceof Line2D line) {
            var boundsElement = doc.createElement("Line");
            boundsElement.setAttribute("x1", String.valueOf(line.getX1()));
            boundsElement.setAttribute("y1", String.valueOf(line.getY1()));
            boundsElement.setAttribute("x2", String.valueOf(line.getX2()));
            boundsElement.setAttribute("y2", String.valueOf(line.getY2()));
            graphicObjectElement.appendChild(boundsElement);
        }
    }

    // Метод для определения типа графического объекта
    private static String getShapeType(Shape shape) {
        if (shape instanceof Rectangle2D) {
            return "Rectangle";
        } else if (shape instanceof Ellipse2D) {
            return "Ellipse";
        } else if (shape instanceof Line2D) {
            return "Line";
        }
        return "Unknown";
    }

    public static VisualObject deserializeVisualObject(String xmlData) {
        Shape shape = null;
        Color color = null;
        Stroke stroke = null;
        try {
            // Парсинг XML строки
            var dbFactory = DocumentBuilderFactory.newInstance();
            var dBuilder = dbFactory.newDocumentBuilder();
            var inputStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
            var doc = dBuilder.parse(inputStream);

            // Извлекаем тип формы
            var shapeTypeNode = doc.getElementsByTagName("ShapeType").item(0);
            var shapeType = shapeTypeNode.getTextContent();

            // Извлекаем координаты и размеры
            if ("Rectangle".equals(shapeType) || "Ellipse".equals(shapeType)) {
                var boundsNode = doc.getElementsByTagName("Bounds").item(0);
                var boundsAttrs = boundsNode.getAttributes();
                var x = Double.parseDouble(boundsAttrs.getNamedItem("x").getNodeValue());
                var y = Double.parseDouble(boundsAttrs.getNamedItem("y").getNodeValue());
                var width = Double.parseDouble(boundsAttrs.getNamedItem("width").getNodeValue());
                var height = Double.parseDouble(boundsAttrs.getNamedItem("height").getNodeValue());

                // Восстанавливаем объект Shape
                if ("Rectangle".equals(shapeType)) {
                    shape = new Rectangle2D.Double(x, y, width, height);
                } else if ("Ellipse".equals(shapeType)) {
                    shape = new Ellipse2D.Double(x, y, width, height);
                }
            } else if ("Line".equals(shapeType)) {
                var lineNode = doc.getElementsByTagName("Line").item(0);
                var lineAttrs = lineNode.getAttributes();
                var x1 = Double.parseDouble(lineAttrs.getNamedItem("x1").getNodeValue());
                var y1 = Double.parseDouble(lineAttrs.getNamedItem("y1").getNodeValue());
                var x2 = Double.parseDouble(lineAttrs.getNamedItem("x2").getNodeValue());
                var y2 = Double.parseDouble(lineAttrs.getNamedItem("y2").getNodeValue());

                shape = new Line2D.Double(x1, y1, x2, y2);
            }

            // Извлекаем цвет
            var colorNode = doc.getElementsByTagName("Color").item(0);
            var colorAttrs = colorNode.getAttributes();
            var r = Integer.parseInt(colorAttrs.getNamedItem("r").getNodeValue());
            var g = Integer.parseInt(colorAttrs.getNamedItem("g").getNodeValue());
            var b = Integer.parseInt(colorAttrs.getNamedItem("b").getNodeValue());
            color = new Color(r, g, b);

            // Извлекаем стиль линии (Stroke)
            var strokeNode = doc.getElementsByTagName("Stroke").item(0);
            if (strokeNode != null) {
                var strokeAttrs = strokeNode.getAttributes();
                var strokeWidth = Float.parseFloat(strokeAttrs.getNamedItem("width").getNodeValue());
                stroke = new BasicStroke(strokeWidth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new VisualObject(shape, color, stroke);
    }
}