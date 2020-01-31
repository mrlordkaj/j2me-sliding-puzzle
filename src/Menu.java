
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import util.GraphicButton;
import util.ImageHelper;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thinh
 */
public class Menu extends GamePage implements CommandListener {
    private static final byte COMMAND_NONE = 0;
    private static final byte COMMAND_LEFT = 1;
    private static final byte COMMAND_RIGHT = 2;
    
    private Image backgroundImage, pictureImage, leftButtonImage;
    private GraphicButton[] arrowButton;
    private int activeCommand = COMMAND_NONE;
    private boolean touching = false;
    private int pictureId = 1;
    private int sizeId = 1, numCol = 3, numRow = 3;
    private String strSize = "3x3";
    private boolean isMainMenu = true;
    
    private Main parent;
    private Alert alert;
    
    public Menu(boolean _isMain, Main _parent) {
        parent = _parent;
        isMainMenu = _isMain;
        
        schedule = 40;
        new Thread(this).start();
        
        prepareResource();
        
        switchPicture();
        switchSize();
    }
    
    private void prepareResource() {
        backgroundImage = ImageHelper.loadImage("/images/menubg.png");
        
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_SETTING, false);
            pictureId = Integer.parseInt(new String(rs.getRecord(1)));
            sizeId = Integer.parseInt(new String(rs.getRecord(2)));
            rs.closeRecordStore();
        } catch (RecordStoreException ex) {
            sizeId = 0;
        }
        
        if(isMainMenu) leftButtonImage = ImageHelper.loadImage("/images/btnabout.png");
        else leftButtonImage = ImageHelper.loadImage("/images/btnresume.png");
        
        arrowButton = new GraphicButton[] {
            new GraphicButton("/images/arrowleft.png", COMMAND_LEFT, 6, 148, 54, 43),
            new GraphicButton("/images/arrowright.png", COMMAND_RIGHT, 180, 148, 54, 43)
        };
        
        isLoading = false;
    }
    
    private void switchPicture() {
        pictureImage = Image.createImage(110, 110);
        Graphics g = pictureImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/picture" + pictureId + "_small.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        g.drawImage(Picture.createPeaceMask(110, 110), 0, 0, Graphics.LEFT | Graphics.TOP);
        g = null;
    }
    
    private void switchSize() {
        int numPeace = Picture.RECORD_ROW[sizeId];
        switch(numPeace) {
            case 9:
                numCol = 3;
                numRow = 3;
                break;
                
            case 12:
                numCol = 3;
                numRow = 4;
                break;
                
            case 16:
                numCol = 4;
                numRow = 4;
                break;
                
            case 20:
                numCol = 4;
                numRow = 5;
                break;
                
            case 25:
                numCol = 5;
                numRow = 5;
                break;
                
            case 30:
                numCol = 5;
                numRow = 6;
                break;
                
            case 36:
                numCol = 6;
                numRow = 6;
                break;
        }
        strSize = numRow + "x" + numCol;
    }

    protected void update() {
        
    }
    
    public void paint(Graphics g) {
        if(isLoading) {
            g.setColor(0x000000);
            g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
            g.setColor(0xffffff);
            g.drawString("loading...", Main.SCREENSIZE_WIDTH / 2, Main.SCREENSIZE_HEIGHT / 2, Graphics.HCENTER | Graphics.BASELINE);
        } else {
            g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
            arrowButton[0].paint(g);
            arrowButton[1].paint(g);
            if(pictureImage != null) g.drawImage(pictureImage, 65, 111, Graphics.LEFT | Graphics.TOP);
            g.setColor(0x00ff00);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
            g.drawString(strSize, Main.SCREENSIZE_WIDTH / 2, 264, Graphics.HCENTER | Graphics.BASELINE);
            g.drawImage(leftButtonImage, 6, 396, Graphics.LEFT | Graphics.BOTTOM);
        }
    }
    
    protected void pointerPressed(int x, int y) {
        if(setActiveCommand(x, y)) touching = true;
    }
    
    protected void pointerDragged(int x, int y) {
        setActiveCommand(x, y);
    }
    
    private boolean setActiveCommand(int x, int y) {
        arrowButton[0].active = false;
        arrowButton[1].active = false;
        activeCommand = COMMAND_NONE;
        if(arrowButton[0].contains(x, y)) {
            arrowButton[0].active = true;
            activeCommand = arrowButton[0].getCommand();
            return true;
        } else if(arrowButton[1].contains(x, y)) {
            arrowButton[1].active = true;
            activeCommand = arrowButton[1].getCommand();
            return true;
        }
        return false;
    }
    
    protected void pointerReleased(int x, int y) {
        arrowButton[0].active = false;
        arrowButton[1].active = false;
        switch(activeCommand) {
            case COMMAND_LEFT:
                if(--pictureId < 1) pictureId = 10;
                switchPicture();
                break;
                
            case COMMAND_RIGHT:
                if(++pictureId > 10) pictureId = 1;
                switchPicture();
                break;
                
            case COMMAND_NONE:
                if(!touching) {
                    //nút thoát
                    if(x > 160 && y > 350) {
                        alert = new Alert("Quit", "Are you sure you want to quit?", null, AlertType.WARNING);
                        alert.setTimeout(Alert.FOREVER);
                        alert.addCommand(new Command("Okie", Command.EXIT, 1));
                        alert.addCommand(new Command("Cancel", Command.CANCEL, 1));
                        alert.setCommandListener(this);
                        parent.display.setCurrent(alert);
                    } else if(x > 70 && x < 169 && y > 248 && y < 268) { //nút chuyển kích thước
                        if(++sizeId > 7) sizeId = 1;
                        switchSize();
                    } else if(x > 40 && x < 199 && y > 282 && y < 323) { //nút start
//                        if(isMainMenu) {
                            startNewGame();
//                        } else {
//                            alert = new Alert("New Game", "Do you want to start a new puzzle? This will kill your current process!", null, AlertType.WARNING);
////                            alert.setTimeout(Alert.FOREVER);
//                            alert.addCommand(new Command("Start", Command.OK, 1));
//                            alert.addCommand(new Command("Cancel", Command.CANCEL, 1));
//                            alert.setCommandListener(this);
//                            parent.display.setCurrent(alert);
//                        }
                    } else if(x > 40 && x < 199 && y > 330 && y < 371) { //nút help
                        parent.showHelp();
                    } else if(x < 80 && y > 350) { //Nút resume hoặc about
                        if(isMainMenu) { //Nút about
                            alert = new Alert("About", "Dragon Ball Puzzle\r\nVersion 1.0\r\n\r\nDeveloped by Openitvn Forum", null, AlertType.INFO);
                            alert.setTimeout(Alert.FOREVER);
                            alert.addCommand(new Command("Close", Command.CANCEL, 1));
                            alert.setCommandListener(this);
                            parent.display.setCurrent(alert);
                        } else { //Nút Resume
                            parent.resume();
                        }
                    }
                }
                break;
        }
        touching = false;
        activeCommand = COMMAND_NONE;
    }
    
    private void startNewGame() {
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_SETTING, false);
            byte[] writer = Integer.toString(pictureId).getBytes();
            rs.setRecord(1, writer, 0, writer.length);
            writer = Integer.toString(sizeId).getBytes();
            rs.setRecord(2, writer, 0, writer.length);
            rs.closeRecordStore();
        } catch (RecordStoreException ex) {
        }
        parent.startGame(pictureId, numCol, numRow);
    }

    public void dispose() {
        isLoading = true;
        pageLooping = false;
    }

    public void commandAction(Command c, Displayable d) {
        switch(c.getCommandType()) {
            case Command.EXIT:
                parent.notifyDestroyed();
                break;
                
            case Command.OK:
                //parent.display.setCurrent(this);
                startNewGame();
                break;
                
            case Command.CANCEL:
                parent.display.setCurrent(this);
                break;
        }
        alert = null;
    }
}
