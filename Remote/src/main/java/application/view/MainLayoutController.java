package application.view;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import application.model.FilePath;
import application.utils.CSVUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class MainLayoutController {

	private static final String DATABASE_URL = "https://remote-15dab.firebaseio.com/";
	private static DatabaseReference database;
	private boolean firstCommand;
	Robot robot;
	private Long timeCheck = 0L;

	@FXML
	TextField code;
	@FXML
	Label display;

	@FXML
	private TableView<FilePath> tableView;
	@FXML
	private TableColumn<FilePath, String> fileId;
	@FXML
	private TableColumn<FilePath, String> fileName;

	public MainLayoutController() {
	}

	@FXML
	private void initialize() {
		fileId.setCellValueFactory(new PropertyValueFactory<FilePath, String>("id"));
		fileName.setCellValueFactory(new PropertyValueFactory<FilePath, String>("file"));
		List<FilePath> list = parseUserList();
		tableView.getItems().setAll(list);
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		try {
			// [START initialize]
			FileInputStream serviceAccount = new FileInputStream("service-account.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl(DATABASE_URL).build();
			FirebaseApp.initializeApp(options);
			// [END initialize]
		} catch (IOException e) {
			System.out.println("ERROR: invalid service account credentials. See README.");
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	@FXML
	private void handleClick() {
		firstCommand = true;
		display.setText("Connecting...");
		String referenceString = code.getText().toString();
		// Shared Database reference
		database = FirebaseDatabase.getInstance().getReference(referenceString);
		// Smoke Test
		System.out.println(database.getKey());
		// Start listening to the Database
		database.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (firstCommand == false) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							String function = dataSnapshot.child("Function").getValue(String.class);
							display.setText(function);

							execute(function, dataSnapshot.child("Time").getValue(Long.class));
						}
					});
				} else {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							display.setText("Connected");
							firstCommand = false;
						}
					});
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	@FXML
	private void addBatFile() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("BATCH FILES (*.bat)", "*.bat");
		fileChooser.getExtensionFilters().add(extFilter);
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			List<FilePath> list = parseUserList();
			int id = 1;
			if (list != null && !list.isEmpty())
				id = Integer.parseInt(list.get(list.size() - 1).getId()) + 1;
			FilePath addFile = new FilePath(Integer.toString(id), selectedFile.getAbsolutePath());
			list.add(addFile);
			tableView.getItems().add(addFile);
			writeToFile(list);
			display.setText("File added");
		} else {
			display.setText("No file");
		}
	}

	@FXML
	private void removeBatFile() {
		int selection = tableView.getSelectionModel().getSelectedIndex();
		tableView.getItems().remove(selection);
		List<FilePath> list = parseUserList();
		list.remove(selection);
		writeToFile(list);
	}

	private void execute(String function, Long time) {
		if (!timeCheck.equals(time) && !timeCheck.equals(0L)) {
			System.out.println(function);
			if (function.equals("Enter")) {
				robot.keyPress(KeyEvent.VK_ENTER);
			}
			if (function.equals("Left")) {
				robot.keyPress(KeyEvent.VK_LEFT);
			}
			if (function.equals("Right")) {
				robot.keyPress(KeyEvent.VK_RIGHT);
			}
			if (function.equals("Up")) {
				robot.keyPress(KeyEvent.VK_UP);
			}
			if (function.equals("Down")) {
				robot.keyPress(KeyEvent.VK_DOWN);
			}
			if (function.equals("Pause")) {
				MediaKeyPause();
			}
			if (function.equals("JumpBackward")) {
				MediaKeyBack();
			}
			if (function.equals("JumpForward")) {
				MediaKeyForward();
			}
			if (function.startsWith("Command")) {
				String[] parts = function.split("_");
				executeFile(parts[1]);
				System.out.print("Reacjed");
			}
			if (function.equals("TurnOff")) {
				try {
					shutdown();
				} catch (RuntimeException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		timeCheck = time;
	}

	public static void MediaKeyForward() {
		GlobalScreen.postNativeEvent(
				new NativeKeyEvent(2401, 0, 176, 57369, org.jnativehook.keyboard.NativeKeyEvent.CHAR_UNDEFINED));

	}

	public static void MediaKeyBack() {
		GlobalScreen.postNativeEvent(
				new NativeKeyEvent(2401, 0, 177, 57360, org.jnativehook.keyboard.NativeKeyEvent.CHAR_UNDEFINED));

	}

	public static void MediaKeyPause() {
		GlobalScreen.postNativeEvent(
				new NativeKeyEvent(2401, 0, 179, 57378, org.jnativehook.keyboard.NativeKeyEvent.CHAR_UNDEFINED));

	}

	public static void shutdown() throws RuntimeException, IOException {
		String shutdownCommand;
		String operatingSystem = System.getProperty("os.name");
		if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
			shutdownCommand = "shutdown -h now";
		} else if ("Windows".equals(operatingSystem)) {
			shutdownCommand = "shutdown.exe -s -t 0";
		} else {
			throw new RuntimeException("Unsupported operating system.");
		}
		Runtime.getRuntime().exec(shutdownCommand);
		System.exit(0);
	}

	private List<FilePath> parseUserList() {
		List<FilePath> files = new LinkedList<FilePath>();
		File f = new File("files.csv");
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				String[] fileLine = line.split(cvsSplitBy);
				files.add(new FilePath(fileLine[0], fileLine[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return files;
	}

	private void writeToFile(List<FilePath> files) {
		File f = new File("files.csv");
		try {
			FileWriter writer = new FileWriter(f);
			for (FilePath d : files) {
				List<String> list = new ArrayList<>();
				list.add(d.getId());
				list.add(d.getFile());
				CSVUtils.writeLine(writer, list);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void executeFile(String selectedID) {
		List<FilePath> list = parseUserList();
		for (FilePath file : list) {
			System.out.print("Loop");
			if (file.getId().equals(selectedID)) {
				try {
					Runtime.getRuntime().exec(file.getFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
