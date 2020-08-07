/*
 * Allison Poh
 * CIS 452 Project: Marathon Database Application
 */

package application;
	
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Main extends Application {	
	final static String driverClass = "org.sqlite.JDBC";
	final static String url = "jdbc:sqlite:marathonDB.sqlite";
	Connection connection = null;
	String query = null;
	String output = "";
	String header1 = String.format("%-7s%-5s%-15s%-15s%-10s%-7s%-19s%-7s\n", 
			"Place", "Bib", "First Name", "Last Name", "Time", "State", "Country", "Citizenship");
	String header2 = String.format("%-15s%-15s%-15s%-15s%-15s\n", "First Name", "Last Name", "Gender", "Age Group", "Place");
	String header3 = String.format("%-7s%-5s%-15.14s%-15.14s%-10s%-5s\n", "Place", "Bib", "First Name", "Last Name", "Time", "Age");

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage primaryStage) {
		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(url);
			Statement stmnt = connection.createStatement();
			
			BorderPane root = new BorderPane();
			root.setId("root");
			Scene scene = new Scene(root, 1280, 720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// User Selection Options:
			Text divisionTXT = new Text("Search By Division:");
			ComboBox divisionCB = new ComboBox();
			divisionCB.setPromptText("division");
			divisionCB.getItems().addAll("all", "male running", "female running", 
					"male wheelchair", "female wheelchair");
			
			Text blankTXT = new Text(" ");
			
			Text runnerTXT = new Text("Search By Runner:");
			TextField lastNameTF = new TextField();
			lastNameTF.setPromptText("last name");
			ComboBox genderCB = new ComboBox();
			genderCB.setPromptText("gender");
			genderCB.getItems().addAll("male", "female");
			
			Text blank2TXT = new Text(" ");
			
			Text awardTXT = new Text("Search By Award:");
			ComboBox gender2CB = new ComboBox();
			gender2CB.setPromptText("gender");
			gender2CB.getItems().addAll("male", "female");
			ComboBox ageGroupCB = new ComboBox();
			ageGroupCB.setPromptText("age group");
			ageGroupCB.getItems().addAll("18 - 19", "20 - 24", "25 - 29", "30 - 34", "35 - 39", "40 - 44", "45 - 49", "50 - 54", 
					"55 - 59", "60 - 64", "65 - 69", "70 - 74", "75 - 79", "80 - 89");
			
			Button search1BTN = new Button("Go");
			Button search2BTN = new Button("Go");
			Button search3BTN = new Button("Go");
			
			VBox search1VB = new VBox();
			search1VB.getChildren().add(search1BTN);
			search1VB.setId("search1VB");
			VBox search2VB = new VBox();
			search2VB.getChildren().add(search2BTN);
			search2VB.setId("search2VB");
			VBox search3VB = new VBox();
			search3VB.getChildren().add(search3BTN);
			search3VB.setId("search3VB");
			
			// Table: 
			TextArea dataTXT = new TextArea("");
			dataTXT.setId("dataTXT");	
			dataTXT.setEditable(false);
			dataTXT.setFont(Font.font("Courier New"));
			
			// Search Button Action for Division Input:
			search1BTN.setOnAction(e -> {	
				// clear all other search parameters
				lastNameTF.clear();  
				gender2CB.setValue(null); 
				ageGroupCB.setValue(null);
				
				if(divisionCB.getValue() == "all")
	        		query = "SELECT * FROM MaleRunners UNION"
	        				+ " SELECT * FROM FemaleRunners UNION"
	        				+ " SELECT * FROM MaleWheelChairs UNION"
	        				+ " SELECT * FROM FemaleWheelChairs"
	        				+ " ORDER BY Place ASC, BibNum ASC";
	        	else if(divisionCB.getValue() == "male running") 
	            	query = "SELECT * FROM MaleRunners;";
	        	else if(divisionCB.getValue() == "female running") 
	            	query = "SELECT * FROM FemaleRunners;";
	        	else if(divisionCB.getValue() == "male wheelchair") 
	            	query = "SELECT * FROM MaleWheelChairs;";
	            else if(divisionCB.getValue() == "female wheelchair") 
	            	query = "SELECT * FROM FemaleWheelChairs;";	
				
				try {
					ResultSet results = stmnt.executeQuery(query);
					
					while(results.next()) {
						String Place = results.getString(1);
						String BibNum = results.getString(2);
						String FName = results.getString(3);
						String LName = results.getString(4);
						String Time = results.getString(5);
						String State = results.getString(6);
						String Country = results.getString(7);
						String Citizenship = results.getString(8);
						
						output = output+String.format("%-7s%-5s%-15.14s%-15.14s%-10s%-7s%-19s%-7s\n", 
								Place, BibNum, FName, LName, Time, State, Country, Citizenship);
					}					
					dataTXT.setText(header1 + output);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				output = "";
			});			
			
			// Search Button Action for Runner Input: 
			search2BTN.setOnAction(e -> {	
				// clear all other search parameters
				divisionCB.setValue(null);
				gender2CB.setValue(null); 
				ageGroupCB.setValue(null);
				
				String ln = lastNameTF.getText();
				String gender = (String) genderCB.getValue();
				
				if(gender == "male") {
					query = "SELECT MaleRunners.Place, MaleRunners.BibNum, MaleRunners.FName, MaleRunners.LName, MaleRunners.Time, COALESCE(AwardRecipient.Age, '') FROM MaleRunners"
							+ " LEFT JOIN AwardRecipient ON MaleRunners.FName=AwardRecipient.FName"
							+ " WHERE MaleRunners.LName ='" +ln +"'";
				} else if(gender == "female") {	
					query = "SELECT FemaleRunners.Place, FemaleRunners.Bib, FemaleRunners.FName, FemaleRunners.LName, FemaleRunners.Time, COALESCE(AwardRecipient.Age, '') FROM FemaleRunners"
							+ " LEFT JOIN AwardRecipient ON FemaleRunners.FName=AwardRecipient.FName"
							+ " WHERE FemaleRunners.LName ='" +ln +"'";
				} 
				
				try {
					ResultSet results = stmnt.executeQuery(query);
					
					while(results.next()) {
						String Place = results.getString(1);
						String Bib = results.getString(2);
						String FName = results.getString(3);
						String LName = results.getString(4);
						String Time = results.getString(5);
						String Age = results.getString(6);
						
						output = output+String.format("%-7s%-5s%-15.14s%-15.14s%-10s%-5s\n", 
								Place, Bib, FName, LName, Time, Age);
					}					
					dataTXT.setText(header3 + output);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				output = "";				
			});
			
			// Search Button Action for Award Input:
			search3BTN.setOnAction(e -> {	
				// clear all other search parameters
				divisionCB.setValue(null);  
				lastNameTF.clear();  
				genderCB.setValue(null);
				
				String gender = (String) gender2CB.getValue();
				if(gender == "male")
					gender = "M";
				else if(gender == "female")
					gender = "F";
				
				String age = (String) ageGroupCB.getValue();
				
				query = "SELECT AwardRecipient.FName, AwardRecipient.LName, Gender, AgeGroup, Place FROM Awards"
						+ " JOIN AwardRecipient ON AwardRecipient.AwardID=Awards.AwardID"
						+ " WHERE Gender='" + gender +"' AND AgeGroup='" + age +"'";			
			
				try {
					ResultSet results = stmnt.executeQuery(query);
					
					while(results.next()) {
						String FName = results.getString(1);
						String LName = results.getString(2);
						String Gender = results.getString(3);
						String AgeGroup = results.getString(4);
						String Place = results.getString(5);
						
						output = output+String.format("%-15.14s%-15.14s%-15.14s%-15.14s%-15.15s\n", 
								FName, LName, Gender, AgeGroup, Place);
					}
					
					dataTXT.setText(header2 + output);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				output = "";
			});	
			
			VBox selectionsVB = new VBox();
			selectionsVB.getChildren().addAll(divisionTXT, divisionCB, search1VB, blankTXT, runnerTXT, lastNameTF, 
											  genderCB, search2VB, blank2TXT, awardTXT, gender2CB, 
											  ageGroupCB, search3VB);
			selectionsVB.setId("selectionsVB");

			// Title:
			Text titleTXT = new Text("TCS New York City Marathon 2017 Results");
			titleTXT.setId("titleTXT");
			
			VBox titleVB = new VBox();
			titleVB.getChildren().add(titleTXT);
			titleVB.setId("titleVB");
			
			// Bottom Info:
			Text sourceTXT = new Text("data source: www.tcsnycmarathon.org");
			sourceTXT.setId("sourceTXT");
					
			HBox bottomHB = new HBox();
			bottomHB.getChildren().add(sourceTXT);
			bottomHB.setId("bottomHB");
			bottomHB.setAlignment(Pos.BOTTOM_RIGHT);
					
			// Putting It All Together:
			root.setTop(titleVB);
			root.setLeft(selectionsVB);
			root.setCenter(dataTXT);
			root.setBottom(bottomHB);
	        
			primaryStage.setScene(scene);
			primaryStage.setTitle("TCS New York Marathon Database");
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		launch(args);
	}
}
