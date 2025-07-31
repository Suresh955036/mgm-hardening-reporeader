package com.mgmresorts.reporeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import JGITDownload.com.mgmresorts.jgit.DirectoryDeletion;

public class GitRepoReader {

    public static void main(String[] args) throws Exception {
        String excelPath = "C:\\repo\\repos.xlsx"; // Excel file path
        DirectoryDeletion delete= new DirectoryDeletion();
        try {
            List<String> repoUrls = readGitReposFromExcel(excelPath);
            System.out.println("Repositories found Successfully:");
            for (String url : repoUrls) {
                System.out.println(url);
                //Clone logic can be added here
                System.out.println("started");
            	String gitBashPath = "C:/Users/SG/AppData/Local/Programs/Git/bin/bash.exe"; // Adjust path as needed
            	System.out.println("after bash");
               // String repoUrl = "https://github.com/MGMResorts/booking-show-reservation.git";
                System.out.println("after git");
              //  String targetDirectory = "C:/zip8"; // Optional: specify target directory
                
                // Generate timestamp (e.g., 2025-07-14_16-45-12)
                String baseDirectory = "C:\\gitclone";
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
         
                // Append timestamp to base directory
                String targetDirectory = baseDirectory + "_"+url+"_" + timestamp;
         
                // Print the result
                System.out.println("Target directory: " + targetDirectory);
                
                String[] command;
                if (targetDirectory != null && !targetDirectory.isEmpty()) {
                    command = new String[]{gitBashPath, "-c", "git clone " + url + " " + targetDirectory};
                } else {
                    command = new String[]{gitBashPath, "-c", "git clone " + url};
                }
             
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                try {
            		processBuilder.start();
            		generateOWASPReport(targetDirectory,url);
            		
            		
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
            }
            
        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
        }
    }
    public  static void generateOWASPReport(String targetDirectory, String url) throws Exception
    {
    	String projectDir = System.getProperty("user.dir");
        String scanTarget =  targetDirectory; 
        String dependencyCheckDir = "C:\\Users\\SG\\Downloads\\dependency-check-12.1.0-release\\dependency-check\\bin";
        String batchFile = "dependency-check.bat";
        String projectName = "Tet";
        String scanPath = "C:\\May20_Profile_Core\\profile-core";
        String nvdApiKey = "19eaed5c-5c24-47c4-a885-6ece6543b1c1";
        // Construct the full command string
//        String command = String.format(
//            "cd \"%s\" && %s --project \"%s\" --scan \"%s\" --nvdApiKey \"%s\"",
//            dependencyCheckDir, batchFile, projectName, scanPath, nvdApiKey
//        );
        
        
        String command = String.format(
        	    "cd \"%s\" && %s --project \"%s\" --scan \"%s\" --nvdApiKey \"%s\" --format HTML --format JSON --out \"%s\"",
        	    dependencyCheckDir,         // Path to Dependency-Check directory
        	    batchFile,                  // Path to dependency-check.bat or shell script
        	    projectName,                // Project name for the report
        	    scanPath,                   // Directory or file to scan
        	    nvdApiKey,                  // NVD API Key
        	    dependencyCheckDir                // Directory where reports should be saved
        	);
        // Create the ProcessBuilder
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process p;
		try {
			p = builder.start();
			Path sourcePath = Paths.get("C:\\Users\\SG\\Downloads\\dependency-check-12.1.0-release\\dependency-check\\bin\\dependency-check-report.html"); // Replace with your source file path
			 
			// Destination file path
			
	        String[] parts = url.split("/");
	        String repo = parts[parts.length - 1].replace(".git", "");
	        System.out.println("Repository Name: " + repo);
			String baseDirectory= "C:/DestinationDirectory/";
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
	         
            // Append timestamp to base directory
           // String destinationDirectory = baseDirectory + "_" + timestamp;
			String fileName = "Dependency-Check-Report" +"_"+repo;
     
            // Print the result
            System.out.println("Target directory: " + targetDirectory);

			Path destinationPath = Paths.get(baseDirectory+fileName+".html"); // Replace with your destination file path
			 
			Files.copy(sourcePath, destinationPath);
			 
			System.out.println("File copied successfully!");
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
        String line;
    }
    public static List<String> readGitReposFromExcel(String filePath) throws IOException {
        List<String> repoUrls = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // Read first sheet
            for (Row row : sheet) {
                Cell cell = row.getCell(0); // Assuming repo URLs are in column A
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String url = cell.getStringCellValue().trim();
                    if (url.startsWith("https") || url.startsWith("git@")) {
                        repoUrls.add(url);
                    }
                }
            }
        }
        System.out.println("repoUrls"+repoUrls);
        return repoUrls;
    }
}

