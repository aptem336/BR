package org.yourorghere;

public class Vector {

    public static Vector NULL = new Vector();

    public float x, y, z;

    public Vector() {
        x = y = z = 0;
    }

    public Vector(Vector v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public Vector(float val) {
        x = y = z = val;
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float squareLen() {
        return x * x + y * y + z * z;
    }

    public double len() {
        return Math.sqrt(squareLen());
    }

    public void set(Vector v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(Vector v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public void sub(Vector v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    public void mul(float val) {
        x *= val;
        y *= val;
        z *= val;
    }

    public void div(float val) {
        x /= val;
        y /= val;
        z /= val;
    }

    public static Vector getSum(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector getDiff(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vector getProduct(Vector a, float val) {
        return new Vector(a.x * val, a.y * val, a.z * val);
    }

    public static Vector getDiv(Vector a, float val) {
        return new Vector(a.x / val, a.y / val, a.z / val);
    }

    //скалярное произведение - сумма произведений компонент
    public static float getDotProduct(Vector a, Vector b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    //векторное произведение даёт вектор перпендикулярный перемноженным
    public static Vector getCrossProduct(Vector a, Vector b) {
        return new Vector(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    //получение повёрнутой точки
    public static Vector getRotated(Vector vector, Vector fi) {
        //если угол = 0 - возращаем точку
        if (fi.len() == 0) {
            return vector;
        }
        //ось = нормализованному вектору поворота, не знаю как, но это работает) 
        Vector axis = fi.getNormalized();
        //угол поворота = длине вектора поворота
        double o = fi.len();
        return getRotated(vector, axis, o);
    }

    private static Vector getRotated(Vector a, Vector axis, double o) {
        float cos = (float) Math.cos(o);
        float sin = (float) Math.sin(o);
        //матрица поворота вокруг произвольной оси
        Vector[] matrix = {
            new Vector(cos + (1 - cos) * axis.x * axis.x, (1 - cos) * axis.x * axis.y - sin * axis.z, (1 - cos) * axis.x * axis.z + sin * axis.y),
            new Vector((1 - cos) * axis.x * axis.y + sin * axis.z, cos + (1 - cos) * axis.y * axis.y, (1 - cos) * axis.y * axis.z - sin * axis.x),
            new Vector((1 - cos) * axis.x * axis.z - sin * axis.y, (1 - cos) * axis.y * axis.z + sin * axis.x, cos + (1 - cos) * axis.z * axis.z)
        };
        //произедение матриц есть комбинпция всех возможных скалярных произведений...в отчёте есть это
        return new Vector(getDotProduct(a, matrix[0]), getDotProduct(a, matrix[1]), getDotProduct(a, matrix[2]));
    }

    //нормализация - укорачивания до длины 1 нужно для поворота т.к. ось должна быть единичной
    public Vector getNormalized() {
        float len = (float) len();
        return new Vector(x / len, y / len, z / len);
    }

    public Vector getInvert() {
        return new Vector(-x, -y, -z);
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }
}
