package com.karmanchik.mdk.figure;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Pyramid {
    public static MeshView create(float height, float hypotenuse) {
        TriangleMesh colouredPyramid = new TriangleMesh();
        colouredPyramid.getPoints().addAll(0, 0, 0); //0-index:: top
        colouredPyramid.getPoints().addAll(0, height, -hypotenuse / 2); //1-index:: x=0, z=-hyp/2 ==> Closest to user
        colouredPyramid.getPoints().addAll(-hypotenuse / 2, height, 0); //2-index:: x=hyp/2,  z=0 ==> Leftest
        colouredPyramid.getPoints().addAll(hypotenuse / 2, height, 0);  //3-index:: x=hyp/2,  z=0 ==> rightest
        colouredPyramid.getPoints().addAll(0, height, hypotenuse / 2); ////4-index:: x=0, z=hyp/2  ==> Furthest from user

        //Next statement copied from stackoverflow.com/questions/26831871/coloring-individual-triangles-in-a-triangle-mesh-on-javafx
        colouredPyramid.getTexCoords().addAll(
                0.1f, 0.5f, // 0 red
                0.3f, 0.5f, // 1 green
                0.5f, 0.5f, // 2 blue
                0.7f, 0.5f, // 3 yellow
                0.9f, 0.5f  // 4 orange
        );

        colouredPyramid.getFaces().addAll(0, 0, 2, 0, 1, 0); //Left front face ---> RED
        colouredPyramid.getFaces().addAll(0, 1, 1, 1, 3, 1); //Right front face ---> GREEN
        colouredPyramid.getFaces().addAll(0, 2, 3, 2, 4, 2); //Right back face ---> BLUE
        colouredPyramid.getFaces().addAll(0, 3, 4, 3, 2, 3); //Left back face ---> RED
        colouredPyramid.getFaces().addAll(4, 4, 1, 4, 2, 4); //Base: left triangle face ---> YELLOW
        colouredPyramid.getFaces().addAll(4, 0, 3, 0, 1, 0); //Base: right triangle face ---> ORANGE

        return new MeshView(colouredPyramid);
    }
}
