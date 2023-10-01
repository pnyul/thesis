package org.thesis.utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

/**
 * This final static class performs the configuration of the two types of ComboBox-es.
 */
public final class ComboBoxInitializer {

    private ComboBoxInitializer() {
    }

    public enum Type {

        TIME("TIME"),
        INTEGER("INTEGER");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public static ObservableList<Object> initializeComboBox(int min, int max, String type) {

        ObservableList<Object> list = FXCollections.observableArrayList();

        for (int i = min; i <= max; i++) {
            if (type.equals(Type.INTEGER.getValue())) {
                list.add(Integer.valueOf(i));
            } else {
                if (String.valueOf(i).length() > 1) {
                    list.add(String.valueOf(i));
                } else {
                    list.add("0" + i);
                }
            }
        }

        return list;

    }

    public static void setComboBox(ComboBox comboBox, int min, int max, int select, String type) {
        comboBox.setItems(initializeComboBox(min, max, type));
        comboBox.getSelectionModel().select(select);
    }

}
