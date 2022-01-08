package eg.edu.alexu.csd.oop.draw.cs23.drawing.service;

import eg.edu.alexu.csd.oop.draw.cs23.drawing.database.*;
import eg.edu.alexu.csd.oop.draw.cs23.drawing.model.*;

import org.springframework.stereotype.Service;

@Service
public class Delete_Shape {
    private final Shapes_Container container = new Shapes_Container();
    private final Undo_Redo undo_redo = new Undo_Redo();

    public void deleteShape(int ID) throws CloneNotSupportedException {
        Shape shape = container.deleteShapeByID(ID);
        addToStack(shape);
    }

    private void addToStack(Shape shape) throws CloneNotSupportedException {
        State state = new State();
        state.setPreviousState(shape);
        Shape nullShape = shape.clone();
        nullShape.setType(null);
        state.setCurrentState(nullShape);
        undo_redo.insert(state);
    }

}
