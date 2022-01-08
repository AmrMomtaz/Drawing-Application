package eg.edu.alexu.csd.oop.draw.cs23.drawing.database;

import eg.edu.alexu.csd.oop.draw.cs23.drawing.model.*;
import java.util.ArrayList;
import java.util.List;

public class Shapes_Container
{
    public static List<Shape> shapes = new ArrayList<Shape>();
    public static int ctr = 1;

    public int insert(Shape shape ,boolean isInitialized){
        if(!isInitialized)
            shape.setId(ctr++);
        shapes.add(shape);
        return 1;
    }

    public Shape getShapeByID(int ID){
        if(shapes.isEmpty())
            return null;
        for(Shape shape : shapes){
            if(shape.getId() == ID)
                return shape;
        }
        return null;
    }

    public Shape deleteShapeByID(int ID){
        if(shapes.isEmpty())
            return null;
        for(Shape shape : shapes){
            if(shape.getId() == ID) {
                shapes.remove(shape);
                return shape;
            }
        }
        return null;
    }
}
