package org.thesis.utility;

import javafx.stage.Stage;

/**
 * This class represents a modal window to display short messages.
 */
public class ModalWindow {

    private Stage modalStage;
    private String modalFxml;
    private String newTitle;
    private String newFxml;
    private String newMessage;
    private Integer modalParameter;

    public ModalWindow(Stage modalStage, String modalFxml, String newTitle, String newFxml, String newMessage) {
        this.modalStage = modalStage;
        this.modalFxml = modalFxml;
        this.newTitle = newTitle;
        this.newFxml = newFxml;
        this.newMessage = newMessage;
    }

    public ModalWindow(Stage modalStage, String modalFxml, String newTitle, String newFxml, String newMessage, Integer modalParameter) {
        this.modalStage = modalStage;
        this.modalFxml = modalFxml;
        this.newTitle = newTitle;
        this.newFxml = newFxml;
        this.newMessage = newMessage;
        this.modalParameter = modalParameter;
    }

    public Stage getModalStage() {
        return modalStage;
    }

    public void setModalStage(Stage modalStage) {
        this.modalStage = modalStage;
    }

    public String getModalFxml() {
        return modalFxml;
    }

    public void setModalFxml(String modalFxml) {
        this.modalFxml = modalFxml;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public String getNewFxml() {
        return newFxml;
    }

    public void setNewFxml(String newFxml) {
        this.newFxml = newFxml;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public Integer getModalParameter() {
        return modalParameter;
    }

    public void setModalParameter(Integer modalParameter) {
        this.modalParameter = modalParameter;
    }
}
