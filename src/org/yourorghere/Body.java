package org.yourorghere;

import com.sun.opengl.util.GLUT;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.media.opengl.GL;

public class Body {

    //коэффициент погашения угловой скорости
    public static float RC = 1.0f;
    //переключатель отрисовки
    public static boolean solid = true;
    //квант времени
    private static final float DT = 1.0f / 60.0f;
    //центр масс
    private static Vector Rc;
    //момент инерации
    private float J;
    //угловая скорость
    public final Vector w = new Vector();
    //угол поворота
    public final Vector fi = new Vector();

    public Body(String path, float scale) {
        init(path, scale);
    }

    public void twist(Vector impulse) {
        //точка приложения силы будет положение камеры
        Vector A = BR.camera.getInvert();
        //момент силы - векторное произвдедение плеча и вектора импульса
        Vector M = Vector.getCrossProduct(A, impulse);
        //угловое ускорение - момент силы / момент инерции
        Vector E = Vector.getDiv(M, J);
        //добавляем ускорение к угловой скорости
        w.add(E);
    }

    public void integrate() {
        //гасим скорость
        w.mul((float) (RC - 6.0f * Math.PI * Math.sqrt(J) * 0.000065f));
        //добавляем к углу скорость за квант времени
        fi.add(Vector.getProduct(w, DT));
    }

    public void build(GL gl, GLUT glut) {
        float proportion = (float) w.len() / 5.0f;
        gl.glColor3f(proportion, 0.5f, 1.0f - proportion);
        for (int[][] face_indexes : f) {
            if (solid) {
                gl.glBegin(GL.GL_POLYGON);
            } else {
                gl.glBegin(GL.GL_LINE_LOOP);
            }
            //проходим по индексам
            for (int[] face_index : face_indexes) {
                //нумеровка с нуля а там с 1 => уменьшаем
                int vi = face_index[0] - 1;
                int ni = face_index[1] - 1;
                //из массива по индексу
                //индекс из массива индексов
                //тоже самое с нормалью
                Vector vertex = Vector.getRotated(v.get(vi), fi);
                Vector normal = Vector.getRotated(n.get(ni), fi);
                gl.glNormal3f(normal.x, normal.y, normal.z);
                gl.glVertex3f(vertex.x, vertex.y, vertex.z);
            }
            gl.glEnd();
        }
        if (!solid) {
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glPushMatrix();
            gl.glTranslatef(0.0f, 0.0f, 0.0f);
            //мы сдвинули все точки на вектор центра масс => теперь он в 0,0,0
            glut.glutSolidSphere(0.05d, 10, 10);
            gl.glPopMatrix();
        }
    }

    private void init(String path, float scale) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            //грани
            f = new ArrayList<>();
            //вершины
            v = new ArrayList<>();
            //нормали
            n = new ArrayList<>();
            String line;
            //центр масс
            Rc = new Vector();
            J = 0;
            //читаем с файла до конца
            //там особый формат задания строк, вертолёт также грузили
            while ((line = br.readLine()) != null) {
                //f - faces - грань
                if (line.startsWith("f")) {
                    //индексы первый столбик - вершины, второй нормали
                    String[] face_string = line.substring(2).trim().split(" ");
                    int[][] indexes = new int[face_string.length][0];
                    for (int i = 0; i < face_string.length; i++) {
                        String[] vertex = face_string[i].split("/");
                        indexes[i] = new int[]{Integer.parseInt(vertex[0]), Integer.parseInt(vertex[2])};
                    }
                    f.add(indexes);
                    //нормаль
                } else if (line.startsWith("vn")) {
                    String[] normal_string = line.substring(3).trim().split(" ");
                    n.add(new Vector(Float.parseFloat(normal_string[0]), Float.parseFloat(normal_string[1]), Float.parseFloat(normal_string[2])));
                    //текстура - пропускаем
                } else if (line.startsWith("vt")) {
                    //вершина
                } else if (line.startsWith("v")) {
                    String[] vertex_string = line.substring(2).trim().split(" ");
                    Vector vertex = new Vector(Float.parseFloat(vertex_string[0]), Float.parseFloat(vertex_string[1]), Float.parseFloat(vertex_string[2]));
                    vertex.mul(scale);
                    Rc.add(vertex);
                    J += vertex.squareLen();
                    v.add(vertex);
                }
            }
            Rc.div(v.size());
            J /= v.size();
            //смещаем в центр масс
            v.forEach((vertex) -> {
                vertex.sub(Rc);
            });
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private ArrayList<Vector> v;
    private ArrayList<Vector> n;
    private ArrayList<int[][]> f;

}
