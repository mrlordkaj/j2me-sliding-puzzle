/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Random;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import util.ImageHelper;

/**
 *
 * @author Thinh
 */
public class Picture {
    public static final int PADDING_TOP = 4;
    public static final int PADDING_LEFT = 4;
    public static final int PICTURE_WIDTH = 232;
    public static final int PICTURE_HEIGHT = 232;
    public static final int[] RECORD_ROW = new int[] {0, 9, 12, 16, 20, 25, 30, 36};
    
    private int numCol, numRow, numPeace;
    private int peaceWidth, peaceHeight;
    private Image pictureImage;
    private Peace[] peace;
    private String picturePath;
    public int blankId;
    private int[][] neighbour;
    
    private int time = 0, timeline = 0;
    private Game parent;
    
    public int getNumCol() { return numCol; }
    public int getPeaceWidth() { return peaceWidth; }
    public int getPeaceHeight() { return peaceHeight; }

    public Picture(String _picturePath, int _numCol, int _numRow, Game _parent) {
        picturePath = _picturePath;
        numCol = _numCol;
        numRow = _numRow;
        numPeace = numCol * numRow;
        parent = _parent;
        
        //tinh chieu rong va chieu cao moi manh ghep
        peaceWidth = PICTURE_WIDTH / numCol;
        peaceHeight = PICTURE_HEIGHT / numRow;
        
        prepareResource();
        setNeighbour();
    }

    private void prepareResource() {
        //tạo bóng cho mỗi mảnh ghép
        Image peaceMaskImage = createPeaceMask(peaceWidth, peaceHeight);
        
        //tao cac manh ghep
        Image picture = ImageHelper.loadImage(picturePath);
        peace = new Peace[numPeace];
        for (int i = 0; i < numPeace; i++) {
            Image peaceImage = Image.createImage(peaceWidth, peaceHeight);
            Graphics g = peaceImage.getGraphics();
            int col = i % numCol;
            int row = i / numCol;
            int srcX = col * peaceWidth;
            int srcY = row * peaceHeight;
            g.drawImage(picture, -srcX, -srcY, Graphics.LEFT | Graphics.TOP);
            g.drawImage(peaceMaskImage, 0, 0, Graphics.LEFT | Graphics.TOP);
            peace[i] = new Peace(i, peaceImage, this);
            g = null;
            peaceImage = null;
            //peace[i].x = col * peaceWidth + PADDING_LEFT;
            //peace[i].y = row * peaceHeight + PADDING_TOP;
        }
        peaceMaskImage = null;
        //Tạo ảnh đầy đủ
        pictureImage = Image.createImage(PICTURE_WIDTH, PICTURE_HEIGHT);
        Graphics g = pictureImage.getGraphics();
        g.drawImage(picture, 0, 0, Graphics.LEFT | Graphics.TOP);
        g.drawImage(createPeaceMask(PICTURE_WIDTH, PICTURE_HEIGHT), 0, 0, Graphics.LEFT | Graphics.TOP);
        g = null;
        picture = null;
    }

    public void update() {
        if (parent.state == Game.STATE_PLAY) {
            for (int i = 0; i < numPeace - 1; i++) {
                peace[i].update();
            }
            if (timeline < 50) {
                timeline++;
            } else {
                timeline = 0;
                time++;
            }
        }
    }

    public void paint(Graphics g) {
        if(parent.state == Game.STATE_DONE) {
            g.drawImage(pictureImage, PADDING_LEFT, PADDING_TOP, Graphics.LEFT | Graphics.TOP);
            return;
        }
        
        for (int i = 0; i < numPeace - 1; i++) {
            peace[i].paint(g);
        }
    }

    private void setNeighbour() {
        neighbour = new int[numPeace][4];
        for (int i = 0; i < numPeace; i++) {
            //phan tu lan can phia tren
            if (i >= numCol) neighbour[i][0] = i - numCol;
            else neighbour[i][0] = -1;
            
            //phan tu lan can phia ben trai
            if (i % numCol != 0) neighbour[i][1] = i - 1;
            else neighbour[i][1] = -1;
            
            //phan tu lan can phia ben phai
            if ((i + 1) % numCol != 0) neighbour[i][2] = i + 1;
            else neighbour[i][2] = -1;
            
            //phan tu lan can phia duoi
            if (i < numPeace - numCol) neighbour[i][3] = i + numCol;
            else neighbour[i][3] = -1;
        }
    }

    /*private void shuffle() {
        Random rand = new Random();
        int[] randNum = new int[numPeace];

        for (int i = 0; i < numPeace; i++) {
            peace[i].index = 0;
            randNum[i] = rand.nextInt(numPeace);
            for (int j = 0; j < i; j++) {
                if (i == 0) {
                    break;
                }
                while (randNum[i] == randNum[j]) {
                    randNum[i] = rand.nextInt(numPeace);
                    j = 0;
                }
            }

            peace[i].index = randNum[i];
            if (i == numPeace - 1) {
                blankId = randNum[i];
            }

            int col = randNum[i] % numCol;
            int row = (randNum[i] - col) / numCol;
            peace[i].x = col * peaceWidth + PADDING_LEFT;
            peace[i].y = row * peaceHeight + PADDING_TOP;
            //peace[i].UpdateTouchArea();
        }
    }*/

    public void shuffle() {
        int numTurn = numRow * numCol * 17;
        Random rand = new Random();

        for (int i = 0; i < numPeace; i++) {
            peace[i].index = i;
        }
        blankId = numPeace - 1;

        int k = 0;
        while (k < numTurn) {
            int direction = rand.nextInt(4);
            if (neighbour[blankId][direction] != -1) {
               int from = -1, to = -1;
                for (int i = 0; i < numPeace; i++) {
                    if (peace[i].index == blankId) {
                        from = i;
                    } else if (peace[i].index == neighbour[blankId][direction]) to = i;
                }
                if (from != -1 && to != -1) {
                    int swap = peace[to].index;
                    peace[to].index = peace[from].index;
                    peace[from].index = swap;
                    blankId = neighbour[blankId][direction];
               }
               k++;
            }
        }

        for (int i = 0; i < numPeace; i++) {
            int col = peace[i].index % numCol;
            int row = (peace[i].index - col) / numCol;
            peace[i].x = col * peaceWidth + PADDING_LEFT;
            peace[i].y = row * peaceHeight + PADDING_TOP;
            peace[i].updateTouchArea();
        }
    }
    
    protected void click(int x, int y) {
        for (int i = 0; i < numPeace; i++) {
            if (peace[i].touchArea.contains(x, y) && peace[i].state == Peace.STATE_STAY) {
                int id = peace[i].index;
                if (neighbour[id][0] == blankId) peace[i].moveUp();
                if (neighbour[id][1] == blankId) peace[i].moveLeft();
                if (neighbour[id][2] == blankId) peace[i].moveRight();
                if (neighbour[id][3] == blankId) peace[i].moveDown();
                break;
            }
        }
    }

    public void checkDone() {
        boolean done = true;
        for (int i = 0; i < numPeace - 1; i++) {
            if (peace[i].index != i) {
                done = false;
                break;
            }
        }

        if (done) {
            parent.gameOver();
        }
    }
    
    public static Image createPeaceMask(int width, int height) {
        Image mask = Image.createImage(width, height);
        Graphics g = mask.getGraphics();
        
        g.setColor(0xeeeeee);
        g.drawLine(0, 0, width-1, 0);
        g.drawLine(0, 1, width-2, 1);
        g.drawLine(0, 2, 0, height-2);
        g.drawLine(1, 2, 1, height-3);
        g.setColor(0x111111);
        g.drawLine(width-1, 0, width-1, height);
        g.drawLine(width-2, 1, width-2, height-1);
        g.drawLine(0, height-1, width-2, height-1);
        g.drawLine(1, height-2, width-3, height-2);
        
        int[] rgb = new int[width*height];
        mask.getRGB(rgb, 0, width, 0, 0, width, height);
        for (int i = 0; i < rgb.length; ++i) {
            if (rgb[i] == 0xffffffff) {
                rgb[i] = 0x00ffffff;
            } else {
                rgb[i] &= 0x99ffffff;
            }
        }
        mask = Image.createRGBImage(rgb, width, height, true);
        
        return mask;
    }
}
