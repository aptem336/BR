package org.yourorghere;

import javax.media.opengl.GL;
import static org.yourorghere.BR.gl;

public class Body {

    //����� ����� ������������ ����
    public static float mass = 1.0f;
    //����������� ������������� �����
    public static float resist = 0.5f;
    //������������ ������� ��������
    private static Vector w;
    //������������ ���� ��������
    private static Vector fi;

    public static void addMomentum(Vector a, Vector F) {
        //������ ���� - ������������, �������� ������������ ������-������� � ����
        Vector M = Vector.getCrossProduct(a, F);
        //������� ��������� E = M/J [���/c^2]
        Vector E = Vector.getDiv(M, J * mass);
        //������� �������� w [���/�]
        w.add(E);
    }

    //����� �������
    private static final float DT = 0.01f;

    public static void integrate() {
        //����� ��������
        w.mul((float) (0.95f + 0.025f / resist));
        //��������� � ���� �������� �� ��� �������
        //� ���� ������������� ��������� � ��� � ���� ��������
        fi.add(w, DT);
        updateGeometry();
    }

    private static void updateGeometry() {
        //����� �����
        double len = fi.len();
        Vector axis;
        //����� ���
        if (len == 0) {
            axis = new Vector(0.0f, 1.0f, 0.0f);
        } else {
            axis = fi.getNormalized();
        }
        float cos = (float) Math.cos(len);
        float sin = (float) Math.sin(len);
        //������� �������� ������ ������������ ���
        Vector[] matrix = {
            new Vector(cos + (1 - cos) * axis.x * axis.x, (1 - cos) * axis.x * axis.y - sin * axis.z, (1 - cos) * axis.x * axis.z + sin * axis.y),
            new Vector((1 - cos) * axis.x * axis.y + sin * axis.z, cos + (1 - cos) * axis.y * axis.y, (1 - cos) * axis.y * axis.z - sin * axis.x),
            new Vector((1 - cos) * axis.x * axis.z - sin * axis.y, (1 - cos) * axis.y * axis.z + sin * axis.x, cos + (1 - cos) * axis.z * axis.z)
        };
        for (int i = 0; i < offsets.length; i++) {
            points[i].set(Vector.getDotProduct(offsets[i], matrix[0]), Vector.getDotProduct(offsets[i], matrix[1]), Vector.getDotProduct(offsets[i], matrix[2]));
        }
    }

    //����� ����
    private static Vector[] offsets;
    private static Vector[] points;
    //����� ����
    private static Vector Rc;
    //������ �������
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
