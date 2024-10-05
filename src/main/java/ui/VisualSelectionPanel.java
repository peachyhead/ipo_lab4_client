package ui;

import base.SignalBus;
import base.SignalListener;
import data.ClientCode;
import data.VisualObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import static handler.XMLHandler.serializeVisualObject;

public class VisualSelectionPanel extends JPanel {
    
    private final ArrayList<VisualTemplateView> templates = new ArrayList<>();
    
    private final Action selectAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    
    public VisualSelectionPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setupTemplates();

        SignalBus.subscribe(new SignalListener<String>(ClientCode.visualSelect, value -> {
            var index = Integer.parseInt(value);
            selectAction.putValue("select", serializeVisualObject(templates
                    .get(index).getVisualObject()));
        }));
    }
    
    public void setupTemplates() {
        var rectangle = new VisualTemplateView(0, setupRectangle());
        add(rectangle);
        templates.add(rectangle);
        
        var ellipse = new VisualTemplateView(1, setupEllipse());
        add(ellipse);
        templates.add(ellipse);
        
        var line = new VisualTemplateView(2, setupLine());
        add(line);
        templates.add(line);
    }
    
    private VisualObject setupRectangle() {
        return new VisualObject(
                new Rectangle2D.Double(0, 0, 200, 100),
                Color.GREEN,
                new BasicStroke(3.0f)
        );
    }
    
    private VisualObject setupEllipse(){
        return new VisualObject(
                new Ellipse2D.Double(0, 0, 150, 100),
                Color.RED,
                null
        );
    }
    
    private VisualObject setupLine() {
        return new VisualObject(
                new Line2D.Double(10, 10, 200, 200), 
                Color.BLUE,
                new BasicStroke(5.0f) 
        );
    }
    
    public void subscribeOnSelection(PropertyChangeListener listener) {
        selectAction.addPropertyChangeListener(listener);
    }
}
