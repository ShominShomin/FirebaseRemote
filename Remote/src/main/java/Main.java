import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;


public class Main {

	private static final String DATABASE_URL = "https://remote-15dab.firebaseio.com/";
	private static DatabaseReference database;

	public static void main(String[] args) {
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

		// Shared Database reference
		database = FirebaseDatabase.getInstance().getReference();

		System.out.println(database.child("Function").getKey());
		// Start listening to the Database


		database.child("Function").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String email = dataSnapshot.getValue(String.class);

				System.out.println(email);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

	}



}
