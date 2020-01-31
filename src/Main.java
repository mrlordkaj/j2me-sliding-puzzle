/*
 * Copyright (C) 2012 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * @author Thinh Pham
 */
public class Main extends MIDlet {
    public static final int SCREENSIZE_WIDTH = 240;
    public static final int SCREENSIZE_HEIGHT = 400;
    public static final String RMS_RECORD = "record";
    public static final String RMS_SETTING = "setting";
    
    private Game game;
    private Menu menu;
    private Help help;
    public Display display;

    public void startApp() {
        try {
            RecordStore rs = RecordStore.openRecordStore(RMS_RECORD, true);
            if (rs.getNumRecords() != 7) {
                rs.closeRecordStore();
                RecordStore.deleteRecordStore(RMS_RECORD);

                rs = RecordStore.openRecordStore(RMS_RECORD, true);
                byte[] writer = "0".getBytes();
                for(int i = 0; i < 7; i++) {
                    rs.addRecord(writer, 0, writer.length);
                }
                rs.closeRecordStore();
            }
        } catch (RecordStoreException ex) {
        }
        
        try {
            RecordStore rs = RecordStore.openRecordStore(RMS_SETTING, true);
            if (rs.getNumRecords() != 2) {
                rs.closeRecordStore();
                RecordStore.deleteRecordStore(RMS_SETTING);

                rs = RecordStore.openRecordStore(RMS_SETTING, true);
                byte[] writer = "1".getBytes();
                rs.addRecord(writer, 0, writer.length);
                rs.addRecord(writer, 0, writer.length);
                rs.closeRecordStore();
            }
        } catch (RecordStoreException ex) {
        }
        
        display = Display.getDisplay(this);
        
        menu = new Menu(true, this);
        display.setCurrent(menu);
        //game = new Game(this);
        //display.setCurrent(game);
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    public void startGame(int pictureId, int numCol, int numRow) {
        menu.dispose();
        menu = null;
        game = new Game(pictureId, numCol, numRow, this);
        display.setCurrent(game);
    }
    
    public void gotoMenu() {
        if(game.state == Game.STATE_PLAY) {
            menu = new Menu(false, this);
            display.setCurrent(menu);
            game.state = Game.STATE_PAUSE;
        } else if(game.state == Game.STATE_DONE) {
            game.dispose();
            game = null;
            menu = new Menu(true, this);
            display.setCurrent(menu);
        }
    }
    
    public void showHelp() {
        help = new Help(this);
        display.setCurrent(help);
    }
    
    public void backToMenu() {
        display.setCurrent(menu);
        help = null;
    }
    
    public void resume() {
        menu.dispose();
        menu = null;
        display.setCurrent(game);
        game.state = Game.STATE_PLAY;
    }
}
