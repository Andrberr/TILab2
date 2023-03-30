package com.example.demo;

import java.io.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HelloController {

    @FXML
    private Button readFromFileButton;

    @FXML
    private Button encryptionButton;

    @FXML
    private TextField initialStateField;

    @FXML
    private TextArea plainTextArea;

    @FXML
    private TextArea encryptedTextArea;

    @FXML
    private TextArea keyArea;

    File fileObject;

    byte[] byteArray;

    byte[] encryptByteArray;

    String initialState = "";

    boolean isCiphering;

    int n = 38;

    @FXML
    void onReadFromFileButtonClick(ActionEvent event) {
        keyArea.clear();
        encryptedTextArea.clear();
        isCiphering = true;

        String path = "";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Connect");
        try {
            fileObject = fileChooser.showOpenDialog(new Stage());
            path = fileObject.getPath();
        } catch (Exception e) {
            System.err.println("Select file!");
            plainTextArea.clear();
        }

        readFromFile(path);
    }

    void readFromFile(String path) {
        if (path.length() != 0) {
            byteArray = new byte[(int) fileObject.length()];
            try {
                FileInputStream input = new FileInputStream(path);
                input.read(byteArray, 0, byteArray.length);
                plainTextArea.setText(divideTextByBytes());
                initialStateField.setDisable(false);
                input.close();
            } catch (Exception e) {
                System.err.println("Some error with file!");
            }
        }
    }

    String divideTextByBytes() {
        StringBuilder plaintext = new StringBuilder();

        for (byte b : byteArray) {
            String str = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            if (plaintext.length() < 5000) {
                plaintext.append(str).append(" ");
            }
        }
        return plaintext.toString();
    }


    @FXML
    void onKeyTyped(KeyEvent event) {
        char[] charArr = initialStateField.getText().toCharArray();
        int counter = 0;
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] == '0' || charArr[i] == '1')
                counter++;
            if (counter < n) {
                encryptionButton.setDisable(true);
            }
            if (counter == n) {
                initialState = initialStateField.getText();
                encryptionButton.setDisable(false);
            }
            if (counter > n) {
                encryptionButton.setDisable(true);
            }
        }
    }

    @FXML
    void onEncryptButtonClick(ActionEvent event) {
        int lengthOfFileName = fileObject.getName().length();
        String[] wordsArr = fileObject.getName().split("\\.");
        String path = fileObject.getPath().substring(0, fileObject.getPath().length() - lengthOfFileName) + wordsArr[0] + "chyp." + wordsArr[1];
        System.out.println(path);
        getCorrectInitState();
        if (isCiphering){
            readFromFile(fileObject.getPath());
        } else readFromFile(path);

        encrypt();
        outputCiphertext();
    }

    void getCorrectInitState() {
        char[] charArr = initialState.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] != '0' && charArr[i] != '1')
                charArr[i] = ' ';
        }
        StringBuilder initState = new StringBuilder();
        for (char c : charArr) {
            if (c != ' ')
                initState.append(c);
        }
        initialState = initState.toString();
    }

    public void encrypt() {
        long state = Long.parseLong(initialState, 2);
        encryptByteArray = new byte[byteArray.length];
        StringBuilder key = new StringBuilder();
        LSFR reg = new LSFR(state);
        for (int i = 0; i < byteArray.length; i++) {
            double stateBites = Math.pow(2, n) / 8;
            if (byteArray.length > stateBites) {
                if (i > stateBites - 1 && i % stateBites == 0)
                    reg = new LSFR(state);
            }
            byte keyByte = (byte) reg.shift();
            String str = String.format("%8s", Integer.toBinaryString(keyByte & 0xFF)).replace(' ', '0');
            if (key.length() < 5000) {
                key.append(str).append(" ");
            }
            encryptByteArray[i] = (byte) (keyByte ^ byteArray[i]);
        }
        keyArea.setText(key.toString());
        StringBuilder encrypt = new StringBuilder();
        for (int i = 0; i < encryptByteArray.length; i++) {
            String str = String.format("%8s", Integer.toBinaryString(encryptByteArray[i] & 0xFF)).replace(' ', '0');
            if (encrypt.length() < 5000) {
                encrypt.append(str).append(" ");
            }
        }
        encryptedTextArea.setText(encrypt.toString());
    }

    public void outputCiphertext() {
        try {
            String path;
            if (isCiphering) {
                path = fileObject.getPath();
            } else {
                int lengthOfFileName = fileObject.getName().length();
                String[] wordsArr = fileObject.getName().split("\\.");
                path = fileObject.getPath().substring(0, fileObject.getPath().length() - lengthOfFileName) + wordsArr[0] + "chyp." + wordsArr[1];
            }
            String[] words = path.split("\\.");
            FileOutputStream fw = new FileOutputStream((isCiphering ? words[0] : words[0].substring(0, words[0].length() - 3))  + (isCiphering ? "chyp" : "decr") + "." + words[1]);
            fw.write(encryptByteArray);
            fw.close();
            isCiphering = !isCiphering;
        } catch (IOException e) {
            System.out.println("Output error!");
        }
    }
}
