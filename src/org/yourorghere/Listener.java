package org.yourorghere;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import static org.yourorghere.BR.vertical;
import static org.yourorghere.BR.horizontal;
import static org.yourorghere.BR.length;

public class Listener implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private static final Vector CLICK = new Vector();

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            BR.resetRotate();
            BR.reserCam();
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            length += 0.1d;
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            length -= 0.1d;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            vertical -= 0.1d;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            vertical += 0.1d;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            horizontal += 0.1d;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            horizontal -= 0.1d;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            Body.solid = !Body.solid;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            BR.move = !BR.move;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        CLICK.set(e.getX(), e.getY(), 0.0f);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        BR.twist(new Vector((CLICK.x - e.getX()) / 500.0f, (e.getY() - CLICK.y) / 500.0f, 0.0f));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Body.RC += e.getWheelRotation() * 0.01;
        Body.RC = Math.max(0.5f, Body.RC);
        Body.RC = Math.min(1.0f, Body.RC);
    }

}
