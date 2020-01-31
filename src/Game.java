/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
import util.ImageHelper;
import util.StringHelper;

/**
 *
 * @author Thinh
 */
public class Game extends GamePage implements CommandListener {
    public static final int STATE_PLAY = 0;
    public static final int STATE_PAUSE = 1;
    public static final int STATE_DONE = 2;
    
    private Image backgroundImage;
    private Picture picture;
    
    private int recordRow = 1;
    private int numCol = 3, numRow = 3;
    private int second = 0, timeline = 0, record = 0;
    private String time = "00:00", timeRecord = "00:00";
    private int pictureId;
    public int state = STATE_PAUSE;
    
    private Main parent;
    private Alert alert;
    
    public Game(int _pictureId, int _numCol, int _numRow, Main _parent) {
        parent = _parent;
        pictureId = _pictureId;
        numCol = _numCol;
        numRow = _numRow;
        
        schedule = 20;
        new Thread(this).start();
        
        prepareResource();
    }
    
    private void prepareResource() {
        isLoading = true;
        
        //kỷ lục
        int numPeace = numCol*numRow;
        for(int i = 0; i < Picture.RECORD_ROW.length; i++) {
            if(Picture.RECORD_ROW[i] == numPeace) {
                recordRow = i;
                break;
            }
        }
        
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_RECORD, false);
            record = Integer.parseInt(new String(rs.getRecord(recordRow)));
            rs.closeRecordStore();
        } catch (RecordStoreException ex) {
            record = 0;
        }
        
        //vẽ nền
        backgroundImage = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = backgroundImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/background.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        
        timeRecord = StringHelper.timeParse(record);
        
        //chuẩn bị hình ảnh
        preparePicture();

        isLoading = false;
    }
    
    private void preparePicture() {
        picture = new Picture("/images/picture" + pictureId + ".png", numCol, numRow, this);
        Graphics g = backgroundImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/picture" + pictureId + "_small.png"), 126, 262, Graphics.LEFT | Graphics.TOP);
        g.drawImage(Picture.createPeaceMask(110, 110), 126, 262, Graphics.LEFT | Graphics.TOP);
        picture.shuffle();
        state = STATE_PLAY;
    }
    
    protected void hideNotify() {
        if(state == STATE_PLAY) state = STATE_PAUSE;
    }
    
    protected void update() {
        if(isLoading) return;
        
        if(state == Game.STATE_PLAY) {
            picture.update();
            
            //tính thời gian
            if(++timeline == 50) {
                timeline = 0;
                time = StringHelper.timeParse(++second);
            }
        }
    }
    
    public void paint(Graphics g) {
        if(isLoading) {
            g.setColor(0x000000);
            g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
            g.setColor(0xffffff);
            g.drawString("loading...", Main.SCREENSIZE_WIDTH / 2, Main.SCREENSIZE_HEIGHT / 2, Graphics.HCENTER | Graphics.BASELINE);
        } else {
            if(state != STATE_PAUSE) {
                g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
                picture.paint(g);
                g.setColor(0x00ff00);
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
                g.drawString(numRow + "x" + numCol, 61, 273, Graphics.HCENTER | Graphics.BASELINE);
                g.drawString(time, 61, 320, Graphics.HCENTER | Graphics.BASELINE);
                g.drawString(timeRecord, 61, 367, Graphics.HCENTER | Graphics.BASELINE);
            } else {
                g.setColor(0x000000);
                g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
                g.setColor(0xffffff);
                g.drawString("PAUSED", Main.SCREENSIZE_WIDTH / 2, Main.SCREENSIZE_HEIGHT / 2, Graphics.HCENTER | Graphics.BASELINE);
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                g.drawString("Tap to continue.", Main.SCREENSIZE_WIDTH / 2, Main.SCREENSIZE_HEIGHT / 2 + 16, Graphics.HCENTER | Graphics.BASELINE);
            }
        }
    }
    
    public void dispose() {
        isLoading = true;
        pageLooping = false;
    }
    
    protected void pointerPressed(int x, int y) {
        if(state == STATE_PLAY) {
            picture.click(x, y);
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if(state != STATE_PAUSE) {
            if(x > 160 && y > 350) { //Nút Shuffle
                alert = new Alert("Shuffle the Puzzle", "Are you sure you want to Shuffle the puzzle?", null, AlertType.WARNING);
                alert.setTimeout(Alert.FOREVER);
                alert.addCommand(new Command("Shuffle", Command.OK, 1));
                alert.addCommand(new Command("Cancel", Command.CANCEL, 1));
                alert.setCommandListener(this);
                parent.display.setCurrent(alert);
            } else if(x < 80 && y > 350) { //Nút Menu
                parent.gotoMenu();
            }
        } else {
            state = STATE_PLAY;
        }
    }
    
    public void gameOver() {
        StringBuffer congratulation = new StringBuffer("Congratulation! You done with ");
        if(second < record || record == 0) {
            congratulation.append("NEW RECORD: ");
            try {
                RecordStore rs = RecordStore.openRecordStore(Main.RMS_RECORD, false);
                byte[] writer = Integer.toString(second).getBytes();
                rs.setRecord(recordRow, writer, 0, writer.length);
                rs.closeRecordStore();
            } catch (RecordStoreException ex) {
            }
            record = second;
            timeRecord = StringHelper.timeParse(record);
        }
        congratulation.append(second + " seconds!");
        alert = new Alert("Done!", congratulation.toString(), null, AlertType.CONFIRMATION);
        alert.setTimeout(Alert.FOREVER);
        alert.addCommand(new Command("Close", Command.CANCEL, 1));
        alert.setCommandListener(this);
        parent.display.setCurrent(alert);
        state = STATE_DONE;
    }

    public void commandAction(Command c, Displayable d) {
        switch(c.getCommandType()) {
            case Command.OK:
                picture.shuffle();
                state = STATE_PLAY;
                second = 0;
                time = "00:00";
                timeline = 0;
                
            default:
                parent.display.setCurrent(this);
                break;
        }
        alert = null;
    }
}
