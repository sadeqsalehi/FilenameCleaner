package sample;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class Controller implements Runnable {

    ListView<String> list = new ListView<>();
    List<File> files;
    List<File> filesInFolders;
    ObservableList<String> items = FXCollections.observableArrayList();
    String[] itemsRenamed;
    String folder;
    @FXML
    private CheckBox chkOriginal;
    @FXML
    private Label lblResult;
    @FXML
    private ListView list1;
    @FXML
    private TextField txtDirectory;
    private String[] filesInFoldersRenamed;

    public void onButtonClicked() {

        renameAllFiles();
//

        if (chkOriginal.isSelected()) {
            Thread t1 = new Thread(() -> moveFiles());
            t1.start();
        } else {
            if (!txtDirectory.getText().isEmpty()) {
                Thread t1 = new Thread(() -> copyFiles());
                t1.start();
            } else {
                lblResult.setText("select the target directory please");
            }
        }

    }


    private void renameAllFiles() {
        itemsRenamed = items.toArray(new String[0]);
        filesInFoldersRenamed = ListToArray(filesInFolders);

        String fileName;
        String directoryName;
        for (int i = 0; i < items.size(); i++) {
            String file = itemsRenamed[i];
            fileName = Paths.get(file).getFileName().toString();
            directoryName = Paths.get(file).getParent().toString();
            itemsRenamed[i] = directoryName + "\\" + renameFile(fileName);
        }
        for (int i = 0; i < filesInFolders.size(); i++) {
            String file = filesInFolders.get(i).toString();
            fileName = Paths.get(file).getFileName().toString();
            directoryName = Paths.get(file).getParent().toString();
            filesInFoldersRenamed[i] = directoryName + "\\" + renameFile(fileName);
        }

    }

    private String[] ListToArray(List<File> filesInFolders) {
        String[] result = new String[filesInFolders.size()];
        for (int i = 0; i < filesInFolders.size(); i++) {
            result[i] = filesInFolders.get(i).toString();
        }
        return result;
    }

    private String renameFile(String fileName) {
        String result = "";
        String fileExt = "";
        try {
            result = fileName.substring(0, fileName.lastIndexOf("."));

            fileExt = fileName.substring(fileName.lastIndexOf("."));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            result = result.toLowerCase();
            result = result.replace("-", "_");
            result = result.replace(" ", "_");
            result = result.replace(".", "_");
            result = result.replace("(", "_");
            result = result.replace(")", "_");


            return result + fileExt;
        }
    }


    public void onDragDroped(DragEvent dragEvent) throws IOException {

        files = dragEvent.getDragboard().getFiles();
        for (File file : files) {
            items.add(file.getAbsolutePath());
            if (file.isDirectory()) {
                //      System.out.println("Directory:" + file.getAbsolutePath());
                folder = file.getAbsoluteFile().toString();
                filesInFolders = Files.walk(Paths.get(folder))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
            }
        }
        list1.setItems(items);
    }

    public void onDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.LINK);
        }

    }

    public void chooseDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory == null) {
            //No Directory selected
        } else {
            System.out.println(selectedDirectory.getAbsolutePath());
            txtDirectory.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void copyFiles() {
        Path source = null;
        Path target = null;

        if (txtDirectory.getText() != "") {
            for (int i = 0; i < items.size(); i++) {
                String file = items.get(i);
                source = Paths.get(file);
                target = Paths.get(itemsRenamed[i]).getFileName();
                target = Paths.get(txtDirectory.getText() + "\\" + target);
                try {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            for (int i = 0; i < filesInFolders.size(); i++) {
                String file = filesInFolders.get(i).toString();
                source = Paths.get(file);
                target = Paths.get(filesInFoldersRenamed[i]).getFileName();
                target = Paths.get(txtDirectory.getText() + "\\" + target);
                try {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            Platform.runLater(() -> lblResult.setText("operation completed successfully"));

        }
    }

    private void moveFiles() {

        for (int i = 0; i < items.size(); i++) {
            String file = items.get(i);
            String newFile = itemsRenamed[i];
            File source = new File(file);
            try {
                boolean renameResult = source.renameTo(new File(newFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> lblResult.setText("operation completed successfully"));
        }
        for (int i = 0; i < filesInFolders.size(); i++) {
            String file = filesInFolders.get(i).toString();
            String newFile = filesInFoldersRenamed[i];
            File source = new File(file);
            try {
                boolean renameResult = source.renameTo(new File(newFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> lblResult.setText("operation completed successfully"));
        }
    }

    @Override
    public void run() {
        //
    }

    public void onClearList(ActionEvent actionEvent) {
        list1.getItems().clear();
        lblResult.setText("");

    }
}
