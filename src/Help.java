
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thinh
 */
public class Help extends Form implements CommandListener {
    private Main parent;
    
    public Help(Main _parent) {
        super("Game Help");
        parent = _parent;
        
        addCommand(new Command("Back", Command.BACK, 1));
        setCommandListener(this);
        
        append("Dragon Ball Puzzle\r\nVerion 1.0\r\nDeveloped by Openitvn Forum.\r\n\r\nINSTRUCTION:\r\n\r\n" +
                "Dragon Ball Puzzle is a funny sliding puzzles (also called 15-puzzle) with dragonball's posters and many difficulties. Let's play and take your record!\r\n\r\n" +
                "The game consists of a frame of numbered square tiles in random order with one tile missing. There are many sizes, from 3x3 to 6x6, for the number of tiles and the number of spaces. The object of the puzzle is to place the tiles in order by making sliding moves that use the empty space.\r\n\r\n" +
                "While playing, you can pause the game by pressing the Menu button. Then, press Resume to continue the current game.\r\n\r\n" +
                "For more infomation and games, please visit us at http://openitvn.net\r\n\r\n" + 
                "THANKS FOR YOUR PLAYING!");
    }
    
    public void commandAction(Command c, Displayable d) {
        switch(c.getCommandType()) {
            case Command.BACK:
                parent.backToMenu();
                break;
        }
    }
}
