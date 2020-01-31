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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import util.Rectangle;

/**
 *
 * @author Thinh Pham
 */
public class Peace {
    public static final int STATE_STAY = 0;
    public static final int STATE_UP = 1;
    public static final int STATE_LEFT = 2;
    public static final int STATE_RIGHT = 3;
    public static final int STATE_DOWN = 4;
    
    private Image peaceImage;
    
    public Rectangle touchArea = new Rectangle();
    public int index, x, y;
    private int desX, desY, stepX, stepY;
    public int state = STATE_STAY;
    
    private Picture parent;

    public Peace(int _index, Image _peaceImage, Picture _parent) {
        index = _index;
        peaceImage = _peaceImage;
        parent = _parent;
        stepX = parent.getPeaceWidth() / 4;
        stepY = parent.getPeaceHeight() / 4;
        touchArea.width = parent.getPeaceWidth();
        touchArea.height = parent.getPeaceHeight();
    }

    public void update() {
        switch (state) {
            case STATE_UP:
                if (y > desY + stepY) {
                    y -= stepY;
                } else {
                    y = desY;
                    index -= parent.getNumCol();
                    parent.blankId += parent.getNumCol();
                    state = STATE_STAY;
                    parent.checkDone();
                }
                break;

            case STATE_LEFT:
                if (x > desX + stepX) {
                    x -= stepX;
                } else {
                    x = desX;
                    index -= 1;
                    parent.blankId += 1;
                    state = STATE_STAY;
                    parent.checkDone();
                }
                break;

            case STATE_RIGHT:
                if (x < desX - stepX) {
                    x += stepX;
                } else {
                    x = desX;
                    index += 1;
                    parent.blankId -= 1;
                    state = STATE_STAY;
                    parent.checkDone();
                }
                break;

            case STATE_DOWN:
                if (y < desY - stepY) {
                    y += stepY;
                } else {
                    y = desY;
                    index += parent.getNumCol();
                    parent.blankId -= parent.getNumCol();
                    state = STATE_STAY;
                    parent.checkDone();
                }
                break;
        }

        updateTouchArea();
    }

    public void paint(Graphics g) {
        g.drawImage(peaceImage, x, y, Graphics.LEFT | Graphics.TOP);
    }

    public void moveUp() {
        desY = y - parent.getPeaceHeight();
        state = STATE_UP;
    }

    public void moveLeft() {
        desX = x - parent.getPeaceWidth();
        state = STATE_LEFT;
    }

    public void moveRight() {
        desX = x + parent.getPeaceWidth();
        state = STATE_RIGHT;
    }

    public void moveDown() {
        desY = y + parent.getPeaceHeight();
        state = STATE_DOWN;
    }

    public void updateTouchArea() {
        touchArea.x = x;
        touchArea.y = y;
    }
}
