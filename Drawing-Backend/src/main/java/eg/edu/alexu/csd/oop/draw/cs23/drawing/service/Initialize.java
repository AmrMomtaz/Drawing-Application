package eg.edu.alexu.csd.oop.draw.cs23.drawing.service;

import eg.edu.alexu.csd.oop.draw.cs23.drawing.database.*;
import eg.edu.alexu.csd.oop.draw.cs23.drawing.model.*;
import org.springframework.stereotype.Service;

@Service
public class Initialize {
    private final Shapes_Container container = new Shapes_Container();
    private final Undo_Redo undo_redo = new Undo_Redo();

    public int initialize(Unknown_Shape unknown_shape) throws Exception {
        Shape shape = unknown_shape.map();
        container.insert(shape ,false);
        addToStack(shape);
        return shape.getId();
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