package uvigo.tfgalmacen.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.util.function.Function;

public final class ComboFilters {

    private ComboFilters() {
    }

    /**
     * Convierte un ComboBox en editable + filtrable según lo que escriba el usuario.
     *
     * @param combo       ComboBox a configurar (quedará editable)
     * @param sourceItems Lista completa (sin filtrar)
     * @param toText      Cómo convertir cada ítem a texto para filtrarlo (p.ej. x -> x.toString())
     */
    public static <T> void makeFilterable(ComboBox<T> combo,
                                          ObservableList<T> sourceItems,
                                          Function<T, String> toText) {

        combo.setEditable(true);

        // Lista filtrada que se muestra en el combo
        FilteredList<T> filtered = new FilteredList<>(sourceItems, _it -> true);
        combo.setItems(filtered);

        // Muestra el valor seleccionado como texto usando el mismo mapeo
        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(T obj) {
                return obj == null ? "" : toText.apply(obj);
            }

            @Override
            public T fromString(String s) {
                // Intentar casar por texto exacto (opcional)
                return sourceItems.stream()
                        .filter(it -> toText.apply(it).equals(s))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Filtrado en vivo según lo que se escribe
        combo.getEditor().textProperty().addListener((obs, old, typed) -> {
            final String term = typed == null ? "" : typed.trim().toLowerCase();

            // Si hay un valor seleccionado y el usuario no ha cambiado el editor manualmente,
            // evitamos que el filtrado borre la lista al abrir el popup.
            if (combo.getSelectionModel().getSelectedItem() != null
                    && toText.apply(combo.getSelectionModel().getSelectedItem()).equals(typed)) {
                filtered.setPredicate(_it -> true);
                return;
            }

            filtered.setPredicate(item -> {
                if (term.isEmpty()) return true;
                String text = toText.apply(item);
                return text != null && text.toLowerCase().contains(term);
            });

            // Reabrir el popup para ver cambios al teclear
            if (!combo.isShowing()) combo.show();
        });

        // Cuando se selecciona un ítem del desplegable, reflejarlo en el editor
        combo.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                combo.getEditor().setText(toText.apply(newSel));
            }
        });

        // Al perder foco, si el texto no coincide con ningún ítem, limpiar selección (opcional)
        combo.getEditor().focusedProperty().addListener((obs, was, is) -> {
            if (!is) {
                String txt = combo.getEditor().getText();
                T match = sourceItems.stream()
                        .filter(it -> toText.apply(it).equals(txt))
                        .findFirst()
                        .orElse(null);
                combo.getSelectionModel().select(match);
                // Restaurar lista completa
                filtered.setPredicate(_it -> true);
            }
        });
    }
}
