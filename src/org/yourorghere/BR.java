package org.yourorghere;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class BR implements GLEventListener {

    public static int SIZE;
    public static int[] VIEWPORT = new int[4];

    public static void main(String[] args) {
        Frame frame = new Frame("BR");
        SIZE = Toolkit.getDefaultToolkit().getScreenSize().height - 30;
        frame.setSize(SIZE, SIZE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        GLCanvas canvas = new GLCanvas();
        Listener listener = new Listener();
        canvas.addKeyListener(listener);
        canvas.addMouseListener(listener);
        canvas.addMouseMotionListener(listener);
        canvas.addMouseWheelListener(listener);
        canvas.addGLEventListener(new BR());
        canvas.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        Animator animator = new Animator(canvas);
        frame.add(canvas);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(() -> {
                    animator.stop();
                    System.exit(0);
                }).start();
            }
        });
        animator.start();
    }

    private static GL gl;
    private static GLU glu;
    private static GLUT glut;

    //������ ��������
    private static Body[] bodyes = new Body[0];

    public static void resetRotate() {
        for (Body body : bodyes) {
            body.w.set(Vector.NULL);
            body.fi.set(Vector.NULL);
        }
    }

    public static void twist(Vector impulse) {
        if (move) {
            for (Body body : bodyes) {
                body.twist(impulse);
            }
        }
    }

    private static void loadBody(String path, float scale) {
        //����������� ������
        bodyes = Arrays.copyOf(bodyes, bodyes.length + 1);
        //��������� ����� ������ � �����
        bodyes[bodyes.length - 1] = new Body(path, scale);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL();
        glu = new GLU();
        glut = new GLUT();

        reserCam();

        //������ �������� �� ��������, ������ �������� - �������
        loadBody("suzanne.obj", 1);
        loadBody("elephan.obj", 4);
        loadBody("tree.obj", 1);

        //��������� ���������
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glEnable(GL.GL_NORMALIZE);
        gl.glAlphaFunc(GL.GL_GREATER, 0);
        gl.glEnable(GL.GL_BLEND);
        //��������� ����� ������
        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        //������ �����
        gl.glShadeModel(GL.GL_SMOOTH);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glGetIntegerv(GL.GL_VIEWPORT, VIEWPORT, 0);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 500.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    //���������� �� 0,0,0 �� ������
    public static double length;
    //����������� � �������������� ���� � ��������������� ��������� ����������� (������ �������� �� ���������� ������ 0,0,0
    public static double vertical, horizontal;
    //��� �������� ���
    private static long time;
    private static long lc_time;
    private static int frames = 60;
    private static int fps;

    public static Vector camera = new Vector();

    //��������� ��������� ������
    public static void reserCam() {
        length = 20d;
        vertical = 1.5d;
        horizontal = 1.5d;
    }

    //��������� ������ ����� ��������������� ��������� �����������
    public static void calcCamera() {
        camera.x = (float) (length * Math.sin(vertical) * Math.cos(horizontal));
        camera.y = (float) (length * Math.cos(vertical));
        camera.z = (float) (length * Math.sin(vertical) * Math.sin(horizontal));
    }

    //������������� ��������
    public static boolean move = true;

    @Override
    public void display(GLAutoDrawable drawable) {
        //�������� ������
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        //��������� ���� � ������ �����
        gl.glLightiv(GL.GL_LIGHT0, GL.GL_POSITION, new int[]{1, 1, 1, 0}, 0);
        //�������� ��������� ��������
        gl.glLoadIdentity();

        calcCamera();
        //������ � ������ ����� ������ 3 - ������, ������ 3 - ���� � ����������� �������
        glu.gluLookAt(camera.x, camera.y, camera.z, 0d, 0d, 0d, 0d, 1d, 0d);

        time = System.currentTimeMillis();
        //���� ������ ������ ������
        if (time - lc_time >= 1000) {
            //��������� ���
            fps = frames;
            //����� � �������� ����������
            lc_time = time;
            //���������� �������
            frames = 0;
        }
        frames++;
        //����� �� � ��� ���� ����� �������� �� ������������
        float bias = -5.0f;
        for (Body body : bodyes) {
            if (move) {
                body.integrate();
            }
            gl.glPushMatrix();
            //�������
            gl.glTranslatef(bias, 0.0f, 0.0f);
            //������
            body.build(gl, glut);
            gl.glPopMatrix();
            //������ ���� - ������ ������
            bias += 5.0f;
        }
        drawText(drawable);
        gl.glFlush();
    }

    private static void drawText(GLAutoDrawable drawable) {
        gl.glColor4f(1.0f, 0.5f, 0.0f, 1.0f);
        gl.glWindowPos2i(10, drawable.getHeight() - 50);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, fps + "   fps");
        gl.glWindowPos2i(10, drawable.getHeight() - 75);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, 1.5f - Body.RC + "   Resist coefficient");
        gl.glWindowPos2i(drawable.getWidth() - 300, drawable.getHeight() - 50);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "Enter - on/off skeleton rendering");
        gl.glWindowPos2i(drawable.getWidth() - 300, drawable.getHeight() - 75);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "Mouse wheel - +/- resist coefficient");

    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
}
