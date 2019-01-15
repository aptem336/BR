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
            BR.reset();
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            length += 0.5d;
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            length -= 0.5d;
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
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            BR.move = !BR.move;
        }
        if (e.getKeyCode() == KeyEvent.VK_1) {
            BR.changeModel(1);
        }
        if (e.getKeyCode() == KeyEvent.VK_2) {
            BR.changeModel(2);
        }
        if (e.getKeyCode() == KeyEvent.VK_3) {
            BR.changeModel(3);
        }
        if (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET) {
            Body.resist -= 0.025;
            Body.resist = Math.max(0.5f, Body.resist);
        }
        if (e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
            Body.resist += 0.025;
            Body.resist = Math.min(1.0f, Body.resist);
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
        BR.addMomentum(BR.camera.getInvert(), new Vector(0.0f, (e.getY() - CLICK.y), (e.getX() - CLICK.x)));
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
        Body.mass -= 0.5 * e.getWheelRotation();
        Body.mass = Math.max(1.0f, Body.mass);
    }

}
