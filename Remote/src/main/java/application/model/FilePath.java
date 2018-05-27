package application.model;


import javafx.beans.property.SimpleStringProperty;

public class FilePath {
	public SimpleStringProperty  id;
	public SimpleStringProperty  file;
	
	public FilePath(String id, String file) {
        this.id = new SimpleStringProperty(id);
        this.file = new SimpleStringProperty(file);
    }

	
	public String getId() {
		return id.get();
	}
	public void setId(String idx) {
		id.set(idx);
	}
	
	public String getFile() {
		return file.get();
	}
	public void setFile(String filex) {
		file.set(filex);
	}
}
