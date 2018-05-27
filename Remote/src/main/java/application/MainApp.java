package application;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import javax.imageio.ImageIO;

import application.view.MainLayoutController;

public class MainApp extends Application {

	private static final String iconImageLoc = "http://icons.iconarchive.com/icons/martz90/circle/16/android-icon.png";
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		try {

			this.primaryStage = primaryStage;
			javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
			Platform.setImplicitExit(false);
			AnchorPane root = null;

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainLayout.fxml"));
				loader.setController(new MainLayoutController());
				root = loader.load();
			} catch (Exception e) {
				System.out.println("Error loading layout");
			}

			// AnchorPane root =
			// FXMLLoader.load(getClass().getResource("view/MainLayout.fxml"));

			Scene scene = new Scene(root);

			primaryStage.setTitle("Remote Control Application");
			primaryStage.setScene(scene);
			primaryStage.show();

			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(("file:icon.png")));

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void addAppToTray() {
		try {
			java.awt.Toolkit.getDefaultToolkit();
			if (!java.awt.SystemTray.isSupported()) {
				System.out.println("No system tray support, application exiting.");
				Platform.exit();
			}

			java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();

			URL imageLoc = new URL(iconImageLoc);
			java.awt.Image image = ImageIO.read(imageLoc);

			java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

			trayIcon.addActionListener(event -> Platform.runLater(this::showStage));
			java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");

			exitItem.addActionListener(event -> {
				Platform.exit();
				tray.remove(trayIcon);
			});

			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);
			tray.add(trayIcon);

		} catch (java.awt.AWTException | IOException e) {
			System.out.println("Unable to init system tray");
			e.printStackTrace();
		}
	}

	private void showStage() {
		if (primaryStage != null) {
			primaryStage.show();
			primaryStage.toFront();
		}
	}
}