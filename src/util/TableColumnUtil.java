
package util;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class TableColumnUtil<E> {

    public <T> void alinharConteudo(TableColumn<E,T> coluna, Pos pos) {
         coluna.setCellFactory((TableColumn<E, T> param) -> {
            return new TableCell<E, T>() {
                @Override
                protected void updateItem(T t, boolean empty) {
                    setText("");
                    if (!empty) {
                        setText(String.valueOf(t));
                        setAlignment(pos);
                    }
                }
            };
        });
    }
    
}
