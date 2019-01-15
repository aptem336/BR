package org.yourorghere;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class BR implements GLEventListener {

    public static int SIZE;
    public static int[] VIEWPORT = new int[4];

    public static void main(String[] args) {
        Frame frame = new Frame("Rotation");
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

    public static GL gl;
    private static GLU glu;
    private static GLUT glut;

    public static void addMomentum(Vector a, Vector F) {
        Body.addMomentum(a, F);
    }

    public static void changeModel(int i) {
        switch (i) {
            case 1:
                Body.init(offsets1, faces1, 1.0f);
                break;
            case 2:
                Body.init(offsets2, faces2, 3.0f);
                break;
            case 3:
                Body.init(offsets3, faces3, 3.0f);
                break;
        }
    }

    private static Vector[] offsets1, offsets2, offsets3;
    private static int[][] faces1, faces2, faces3;

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL();
        glu = new GLU();
        glut = new GLUT();

        ObjLoader.readObj("cat.obj");
        offsets1 = new Vector[ObjLoader.vertexs.size()];
        ObjLoader.vertexs.toArray(offsets1);
        faces1 = new int[ObjLoader.faces.size()][3];
        ObjLoader.faces.toArray(faces1);

        ObjLoader.readObj("wolf.obj");
        offsets2 = new Vector[ObjLoader.vertexs.size()];
        ObjLoader.vertexs.toArray(offsets2);
        faces2 = new int[ObjLoader.faces.size()][3];
        ObjLoader.faces.toArray(faces2);

        ObjLoader.readObj("deer.obj");
        offsets3 = new Vector[ObjLoader.vertexs.size()];
        ObjLoader.vertexs.toArray(offsets3);
        faces3 = new int[ObjLoader.faces.size()][3];
        ObjLoader.faces.toArray(faces3);

        changeModel(2);
        reset();

        //установка освещения
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glEnable(GL.GL_NORMALIZE);
        gl.glAlphaFunc(GL.GL_GREATER, 0);
        gl.glEnable(GL.GL_BLEND);
        //обработки альфа канала
        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        //модель теней
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

    //расстояние от 0,0,0 до камеры
    public static double length;
    //верикальный и горизонтальный углы в параметрическом уравнении окружуности (камера движется по окружности вокруг 0,0,0
    public static double vertical, horizontal;
    //для подсчёта фпс
    private static long time;
    private static long lc_time;
    private static int frames = 60;
    private static int fps;

    public static Vector camera = new Vector();

    //начальное положение камеры
    public static void reset() {
        length = 50d;
        vertical = 1.5d;
        horizontal = 0.5d;
        Body.reset();
    }

    //положение камеры через параметрическое уравнение окржнуостий
    public static void calcCamera() {
        camera.x = (float) (length * Math.sin(vertical) * Math.cos(horizontal));
        camera.y = (float) (length * Math.cos(vertical));
        camera.z = (float) (length * Math.sin(vertical) * Math.sin(horizontal));
    }

    //переключатель движения
    public static boolean move = true;

    @Override
    public void display(GLAutoDrawable drawable) {
        //очистили буферы
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        //поставили свет в нужном месте
        gl.glLightiv(GL.GL_LIGHT0, GL.GL_POSITION, new int[]{1, 1, 1, 0}, 0);
        //очистили единичной матрицей
        gl.glLoadIdentity();

        calcCamera();
        //камера в нужном месте первые 3 - откуда, вторые 3 - куда и направление взгляда
        glu.gluLookAt(camera.x, camera.y, camera.z, 0d, 0d, 0d, 0d, 1d, 0d);

        time = System.currentTimeMillis();
        //если прошло больше секнды
        if (time - lc_time >= 1000) {
            //обновляем фпс
            fps = frames;
            //время с прошлого обновления
            lc_time = time;
            //сбрасываем счётчик
            frames = 0;
        }
        frames++;
        if (move) {
            Body.integrate();
        }
        Body.draw();

        drawText(drawable);
        gl.glFlush();
    }

    private static void drawText(GLAutoDrawable drawable) {
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glWindowPos2i(10, drawable.getHeight() - 50);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, fps + "   fps");
        gl.glWindowPos2i(10, drawable.getHeight() - 70);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Body.mass + "   mass");
        gl.glWindowPos2i(10, drawable.getHeight() - 90);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Body.resist * 2.0f - 1.0f + "   resist");
        gl.glWindowPos2i(10, drawable.getHeight() - 110);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Body.J * Body.mass + "   J");

        gl.glWindowPos2i(drawable.getWidth() - 200, drawable.getHeight() - 50);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Esc - reset");
        gl.glWindowPos2i(drawable.getWidth() - 200, drawable.getHeight() - 70);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "W,S,D,A,E,Q - camera");
        gl.glWindowPos2i(drawable.getWidth() - 200, drawable.getHeight() - 90);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Space - pause");
        gl.glWindowPos2i(drawable.getWidth() - 200, drawable.getHeight() - 110);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Mouse drag - add momemtum");
        gl.glWindowPos2i(drawable.getWidth() - 200, drawable.getHeight() - 130);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "[, ] - +/- resist");
        gl.glWindowPos2i(drawable.getWidth() - 200, drawable.getHeight() - 150);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "1,2,3 - change model");

    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
}
