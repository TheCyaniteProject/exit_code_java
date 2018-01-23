/**
 * Simple draggable node.
 *
 * Dragging code based on {@link http://blog.ngopal.com.np/2011/06/09/draggable-node-in-javafx-2-0/}
 *
 * @author (Original) Michael Hoffer <info@michaelhoffer.de> (Edited by) TheCyaniteProject Team
 */

package exitcode;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

// Internal dependancies: Desktop

class DraggableNode extends BorderPane {
    public static boolean tile_windows_on_edge=true;//feel free to change this variable name, the only use is in tileWindowIfOnEdge funtion
    // node position
    private double x = 0;
    private double y = 0;
    // mouse position
    private double mousex = 0;
    private double mousey = 0;
    private Node view;
    private boolean dragging = false;
    private boolean moveToFront = true;
    private boolean onEdge= false;
    private double nonMaxSizeHeight;
    private double nonMaxSizeWidth;
    private double layoutXnoMax;
    private double layoutYnoMax;

    public DraggableNode() {
        init();
    }

    public DraggableNode(Node view) {
        this.view = view;
        getChildren().add(view);
        init();
    }

    private void init() {
        layoutXnoMax= getLayoutX();
        layoutYnoMax= getLayoutY();
        onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                // record the current mouse X and Y position on Node
                mousex = event.getSceneX();
                mousey = event.getSceneY();

                x = getLayoutX();
                y = getLayoutY();

                if (isMoveToFront()) {
                    toFront();
                }
            }
        });

        //Event Listener for MouseDragged
        onMouseDraggedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                // Get the exact moved X and Y
                double offsetX = event.getSceneX() - mousex;
                double offsetY = event.getSceneY() - mousey;

                x += offsetX;
                y += offsetY;

                double scaledX = x;
                double scaledY = y;

                setLayoutX(scaledX);
                setLayoutY(scaledY);

                dragging = true;

                layoutXnoMax=x;
                layoutYnoMax=y;
                tileWindowIfOnEdge(event);

                // again set current Mouse x AND y position
                mousex = event.getSceneX();
                mousey = event.getSceneY();


                event.consume();
            }
        });

        onMouseClickedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                dragging = false;
            }
        });

    }
    //I present to you the most inefficient and ugly function ever
    private void tileWindowIfOnEdge(MouseEvent event) {
        if(!tile_windows_on_edge) return; //check if setting is set to true

        int cornerSpace = 70;
        double desktopWidth = Desktop.main_root.getWidth();
        double desktopHeight = Desktop.main_root.getHeight() - Desktop.desktop_taskbar.getHeight();

        if (mousex <= 0 && mousey < cornerSpace || mousey <= 0 && mousex < cornerSpace) {
            snapOnEdge(0, 0, desktopWidth / 2, desktopHeight / 2);
        } else if (mousex <= 0 && mousey >= desktopHeight - cornerSpace || mousey >= desktopHeight && mousex <= cornerSpace) {
            snapOnEdge(0, desktopHeight / 2, desktopWidth / 2, desktopHeight / 2);
        } else if (mousex >= desktopWidth - 1 && mousey < cornerSpace || mousey <= 0 && mousex > desktopWidth - cornerSpace) {
            snapOnEdge(desktopWidth / 2, 0, desktopWidth / 2, desktopHeight / 2);
        } else if (mousex >= desktopWidth - 1 && mousey >= desktopHeight - cornerSpace || mousey >= desktopHeight && mousex > desktopWidth - cornerSpace) {
            snapOnEdge(desktopWidth / 2, desktopHeight / 2, desktopWidth / 2, desktopHeight / 2);
        } else if (mousey <= 0) {
            maximize();
        } else if (mousex <= 0) {
            snapOnEdge(0, 0, desktopWidth / 2, desktopHeight);
        } else if (mousex >= desktopWidth - 1) {
            snapOnEdge(desktopWidth / 2, 0, desktopWidth / 2, desktopHeight);
        } else if (onEdge) {
            restoreSize();
            x = event.getSceneX() - nonMaxSizeWidth / 2;
            setLayoutX(x);
            onEdge = false;
        }
    }

    private void snapOnEdge(double x, double y, double width, double height){
        HBox titleBar=(HBox) getTop();
        Button btnMax= (Button) titleBar.getChildren().get(3);
        btnMax.setGraphic(new ImageView(Apps.maximize_img));

        setLayoutX(x);
        setLayoutY(y);
        setPrefWidth(width);
        setPrefHeight(height);
        onEdge=true;

    }

    @Override
    public void setPrefSize(double prefWidth, double prefHeight) {
        this.nonMaxSizeHeight=prefHeight;
        this.nonMaxSizeWidth= prefWidth;
        super.setPrefSize(prefWidth, prefHeight);
    }

    /**
     * @return the dragging
     */
    protected boolean isDragging() {
        return dragging;
    }


    /**
     * @return the view
     */
    public Node getView() {
        return view;
    }

    /**
     * @param moveToFront the moveToFront to set
     */
    public void setMoveToFront(boolean moveToFront) {
        this.moveToFront = moveToFront;
    }

    /**
     * @return the moveToFront
     */
    public boolean isMoveToFront() {
        return moveToFront;
    }

    public void removeNode(Node n) {
        getChildren().remove(n);
    }

    public double getNonMaxSizeHeight() {
        return nonMaxSizeHeight;
    }

    public double getNonMaxSizeWidth() {
        return nonMaxSizeWidth;
    }

    public void restoreSize(){
        HBox titleBar=(HBox) getTop();
        Button btnMax= (Button) titleBar.getChildren().get(3);
        btnMax.setGraphic(new ImageView(Apps.maximize_img));

        setPrefWidth(nonMaxSizeWidth);
        setPrefHeight(nonMaxSizeHeight);
        setLayoutX(layoutXnoMax);
        setLayoutY(layoutYnoMax);
        onEdge=false;
    }

    public void maximize(){
        HBox titleBar=(HBox) getTop();
        Button btnMax= (Button) titleBar.getChildren().get(3);
        btnMax.setGraphic(new ImageView(Apps.restore_img));

        setPrefWidth(Desktop.main_root.getWidth());
        setPrefHeight(Desktop.main_root.getHeight() - Desktop.desktop_taskbar.getHeight());
        setLayoutX(0);
        setLayoutY(0);
        onEdge=true;
    }


    public boolean isMaximized(){
        return getLayoutX()==0 && getLayoutY()==0 &&
                getHeight()== Desktop.main_root.getHeight()-Desktop.desktop_taskbar.getHeight() &&
                getWidth() == Desktop.main_root.getWidth();

    }

}
