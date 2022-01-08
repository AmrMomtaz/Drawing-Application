package eg.edu.alexu.csd.oop.draw.cs23.drawing.service;

import eg.edu.alexu.csd.oop.draw.cs23.drawing.database.*;
import eg.edu.alexu.csd.oop.draw.cs23.drawing.model.*;

import org.springframework.stereotype.Service;

@Service
public class Copy_Shape {
    private final Shapes_Container container = new Shapes_Container();
    private final Undo_Redo undo_redo = new Undo_Redo();

    public Shape makeCopy(int ID) throws CloneNotSupportedException {
        Shape newShape = this.container.getShapeByID(ID).clone();
        newShape.shift();
        this.container.insert(newShape ,false);
        addToStack(newShape);
        return newShape;
    }

    private void addToStack(Shape shape) throws CloneNotSupportedException {
        State state = new State();
        state.setCurrentState(shape);
        Shape nullShape = shape.clone();
        nullShape.setType(null);
        state.setPreviousState(nullShape);
        undo_redo.insert(state);
    }
}
