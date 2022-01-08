package eg.edu.alexu.csd.oop.draw.cs23.drawing.service;

import eg.edu.alexu.csd.oop.draw.cs23.drawing.database.*;
import eg.edu.alexu.csd.oop.draw.cs23.drawing.model.*;

import org.springframework.stereotype.Service;

@Service
public class Modify {
    private final Shapes_Container container = new Shapes_Container();
    private final Undo_Redo undo_redo = new Undo_Redo();

    public void update(Unknown_Shape unknown_shape) throws Exception {
        Shape shape = unknown_shape.map();
        Shape oldShape = container.deleteShapeByID(shape.getId());
        container.insert(shape ,true);
        addToStack(oldShape ,shape);
    }

    private void addToStack(Shape oldShape ,Shape shape){
        State state = new State();
        state.setPreviousState(oldShape);
        state.setCurrentState(shape);
        undo_redo.insert(state);
    }
}
