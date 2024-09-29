package handler;

import base.SignalBus;
import base.SignalListener;
import data.ClientCode;
import ui.MainFrame;

public class UIService {
    
    private final MainFrame mainFrame;
    
    public UIService(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    public void initialize() {
        SignalBus.subscribe(new SignalListener<String>(ClientCode.openConversationPanel, 
                evt -> mainFrame.setupConversationPanel()));
        
        SignalBus.subscribe(new SignalListener<>(ClientCode.openMediaPanel,
                mainFrame::setupMediaPanel));
    }
}
