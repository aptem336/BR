package org.yourorghere;

import javax.media.opengl.GL;
import static org.yourorghere.BR.gl;

public class Body {

    //масса точек составляющих тело
    public static float mass = 1.0f;
    //коэффициент сопротивления среды
    public static float resist = 0.5f;
    //псевдовектор угловой скорости
    private static Vector w;
    //псевдовектор угла поворота
    private static Vector fi;

    public static void addMomentum(Vector a, Vector F) {
        //момент силы - псевдовектор, вектрное произведение радиус-вектора и силы
        Vector M = Vector.getCrossProduct(a, F);
        //угловое ускорение E = M/J [рад/c^2]
        Vector E = Vector.getDiv(M, J * mass);
        //угловая скорость w [рад/с]
        w.add(E);
    }

    //квант времени
    private static final float DT = 0.01f;

    public static void integrate() {
        //гасим скорость
        w.mul((float) (0.95f + 0.025f / resist));
        //добаление к углу скорость за шаг времени
        //в этом псевдовекторе храниться и ось и угол поворота
        fi.add(w, DT);
        updateGeometry();
    }

    private static void updateGeometry() {
        //взяли длину
        double len = fi.len();
        Vector axis;
        //взяли ось
        if (len == 0) {
            axis = new Vector(0.0f, 1.0f, 0.0f);
        } else {
            axis = fi.getNormalized();
        }
        float cos = (float) Math.cos(len);
        float sin = (float) Math.sin(len);
        //матрица поворота вокруг произвольной оси
        Vector[] matrix = {
            new Vector(cos + (1 - cos) * axis.x * axis.x, (1 - cos) * axis.x * axis.y - sin * axis.z, (1 - cos) * axis.x * axis.z + sin * axis.y),
            new Vector((1 - cos) * axis.x * axis.y + sin * axis.z, cos + (1 - cos) * axis.y * axis.y, (1 - cos) * axis.y * axis.z - sin * axis.x),
            new Vector((1 - cos) * axis.x * axis.z - sin * axis.y, (1 - cos) * axis.y * axis.z + sin * axis.x, cos + (1 - cos) * axis.z * axis.z)
        };
        for (int i = 0; i < offsets.length; i++) {
            points[i].set(Vector.getDotProduct(offsets[i], matrix[0]), Vector.getDotProduct(offsets[i], matrix[1]), Vector.getDotProduct(offsets[i], matrix[2]));
        }
    }

    //точки тела
    private static Vector[] offsets;
    private static Vector[] points;
    //центр масс
    private static Vector Rc;
    //момент инерции
    static float J;

    public static void init(Vector[] offsets, int[][] faces, float scale) {
        w = new Vector();
        fi = new Vector();
        Body.offsets = new Vector[offsets.length];
        for (int i = 0; i < offsets.length; i++) {
            Body.offsets[i] = new Vector(offsets[i]);
            Body.offsets[i].mul(scale);
        }
        Rc = new Vector();
        J = 0;
        for (Vector point : Body.offsets) {
            Rc.add(point);
            J += point.squareLen();
        }
        Rc.div(offsets.length);
        for (Vector offset : Body.offsets) {
            offset.sub(Rc);
        }
        Body.points = new Vector[offsets.length];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector(offsets[i]);
        }
        Body.faces = faces;
    }

    private static int[][] faces;

    public static void draw() {
        gl.glColor3f(1.0f / mass, 1.0f / mass, 1.0f / mass);
        for (int[] face_indexes : faces) {
            gl.glBegin(GL.GL_TRIANGLES);
            Vector a = points[face_indexes[0] - 1];
            Vector b = points[face_indexes[1] - 1];
            Vector c = points[face_indexes[2] - 1];
            Vector n = Vector.getCrossProduct(Vector.getDiff(a, b), Vector.getDiff(a, c));
            gl.glNormal3f(n.x, n.y, n.z);
            gl.glVertex3f(a.x, a.y, a.z);
            gl.glVertex3f(b.x, b.y, b.z);
            gl.glVertex3f(c.x, c.y, c.z);
            gl.glEnd();
        }
    }

    public static void reset() {
        fi.set(Vector.NULL);
        w.set(Vector.NULL);
    }

}
