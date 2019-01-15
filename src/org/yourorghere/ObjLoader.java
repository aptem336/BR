package org.yourorghere;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ObjLoader {

    public static ArrayList<int[]> faces = new ArrayList<>();
    public static ArrayList<Vector> vertexs = new ArrayList<>();

    public static void readObj(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            //грани
            faces = new ArrayList<>();
            //вершины
            vertexs = new ArrayList<>();
            String line;
            //читаем с файла до конца
            //там особый формат задания строк, вертолёт также грузили
            while ((line = br.readLine()) != null) {
                //f - faces - грань
                if (line.startsWith("f")) {
                    //индексы первый столбик - вершины
                    String[] face_string = line.substring(2).trim().split(" ");
                    int[] indexes = new int[face_string.length];
                    for (int i = 0; i < face_string.length; i++) {
                        String[] vertex = face_string[i].split("/");
                        indexes[i] = Integer.parseInt(vertex[0]);
                    }
                    faces.add(indexes);
                    //нормаль - пропускаем
                } else if (line.startsWith("vn")) {
                    //текстура - пропускаем
                } else if (line.startsWith("vt")) {
                    //вершина
                } else if (line.startsWith("v")) {
                    String[] vertex_string = line.substring(2).trim().split(" ");
                    vertexs.add(new Vector(Float.parseFloat(vertex_string[0])/100, Float.parseFloat(vertex_string[1])/100, Float.parseFloat(vertex_string[2])/100));
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
