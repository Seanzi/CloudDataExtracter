package desktopapplication3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {

   final String USERNAME = System.getProperty("user.name");
   ArrayList<FileData> googleFiles = new ArrayList();
   ArrayList<FileData> skyDriveFiles = new ArrayList();
   boolean dropBoxInstalled = false;
   boolean googleDriveInstalled = false;
   boolean skyDriveInstalled = false;
   File dbDir = new File("C:\\Users\\" + USERNAME
           + "\\AppData\\Roaming\\Dropbox");
   File gdDir = new File("C:\\Users\\" + USERNAME
           + "\\AppData\\Local\\Google\\Drive");
   File sdDir = new File("C:\\Users\\" + USERNAME
           + "\\AppData\\Local\\Microsoft\\SkyDrive\\setup\\logs");

   public Driver() {
      dropBoxInstalled = dbDir.exists();
      googleDriveInstalled = gdDir.exists();
      skyDriveInstalled = sdDir.exists();
   }

   public String findDropBoxData() {
      return findDropBoxData(dbDir);
   }

   public String findDropBoxData(File dir) {
      if (dropBoxInstalled) {
         return "DropBox has existed at one point.";
      }
      return "No DropBox remnants found.";
   }

   public String findGoogleDriveData() {
      return findGoogleDriveData(gdDir);
   }

   public String findGoogleDriveData(File dir) {
      dir = new File(dir.toString() + "\\sync_log.log");
      if (dir.exists()) {
         return extractGoogleDriveData(dir);
      }
      return "Directory does not exist.";
   }

   public String findSkyDriveData() {
      return findSkyDriveData(sdDir);
   }

   public String findSkyDriveData(File dir) {
      String str = "";
      File logDir = new File("C:\\Users\\" + USERNAME
              + "\\AppData\\Local\\Microsoft\\SkyDrive\\logs\\SyncDiagnostics.log");
      if (logDir.exists()) {
         str = extractSkyDriveData(logDir);
      }

      if (str.equals("")) {
         //str = findLastSkyDriveActivity(dir);
      }
      return str;

   }

   public String findLastSkyDriveActivity(File dir) {
      File[] listOfFiles = dir.listFiles();
      String dateActivity = listOfFiles[listOfFiles.length - 1].toString();
      dateActivity = dateActivity.substring(dateActivity.length() - 29);
      dateActivity = dateActivity.substring(0, 16);
      return "Last Activity " + dateActivity;
   }

   public String extractSkyDriveData(File log) {
      String out = "";
      Scanner in;
      System.out.println(log.canRead());
      log.setReadable(true);
      ArrayList<FileData> files = new ArrayList<FileData>();
      try {
         in = new Scanner(log);
         while (in.hasNext()) {
            String str = in.nextLine();
            if (str.contains("- file")) {
               FileData file = new FileData();
               boolean alreadyInList = false;
               int index = str.indexOf("'");
               int endIndex = str.indexOf("'", index + 1);
               alreadyInList = index == -1 ? true : false;
               String tempStr = str.substring(index, endIndex);
               tempStr = tempStr.substring(tempStr.lastIndexOf('\\') + 1, tempStr.length());
               file.setName(tempStr);
               index = str.indexOf("size=");
               endIndex = str.indexOf(",", index);
               file.setSize(str.substring(index + 5, endIndex));

               for (FileData temp : skyDriveFiles) {
                  if (file.getName().equals(temp.getName())) {
                     alreadyInList = true;
                  }
               }
               if (!alreadyInList && Integer.parseInt(file.getSize()) > 100) {
                  skyDriveFiles.add(file);
               }
            }
         }
         out = "SkyDrive file names: ";
         for (FileData file : skyDriveFiles) {
            out += "\nName:" + file.getName() + "\n      Size: " + file.getSize() + "B";
         }
      } catch (FileNotFoundException e) {
         out = "Error reading file, file not found";
      }
      return out;
   }

   public String findAbsentFilesSkyDrive(){
      if(skyDriveFiles.isEmpty()){
         findSkyDriveData();
         if(skyDriveFiles.isEmpty())
            return "No files found\n" + findSkyDriveData();
      }
      File dir = new File("C:\\Users\\" + USERNAME
           + "\\SkyDrive");
      File[] listOfFiles = dir.listFiles();
      for (FileData data : skyDriveFiles) {
            boolean match = false;
            for(File file : listOfFiles){
               if(data.getName().equals(file.getName())){
                  match = true;
               }
            }
            if(!match){
               data.setAbsent(true);
            }
         }
      String out = "Absent Files : \n";
      for(FileData file : skyDriveFiles){
         if(file.getAbsent()){
            out+= file.getName() + "\n";
         }
      }
      return out;
   }

   public String extractGoogleDriveData(File log) {
      String out = "";
      Scanner in;
      FilePermission permission = new FilePermission(log.toString(), "read");
      log.setReadable(true);
      googleFiles = new ArrayList<FileData>();
      try {
         in = new Scanner(log);
         while (in.hasNext()) {
            String str = in.nextLine();
            if (str.contains("Action.CREATE")) {
               FileData file = new FileData();
               boolean alreadyInList = false;
               int index = str.indexOf("name:");
               int endIndex = str.indexOf("'", index);
               alreadyInList = index == -1 ? true : false;
               file.setName(str.substring(index + 6, endIndex));
               file.setDate(str.substring(0, 19));
               index = str.indexOf("size:");
               endIndex = str.indexOf("'", index);
               file.setSize(str.substring(index + 5, endIndex) + "B");

               for (FileData temp : googleFiles) {
                  if (file.getName().equals(temp.getName())) {
                     alreadyInList = true;
                  }
               }
               if (!alreadyInList) {
                  googleFiles.add(file);
               }
            }
         }
         out = "GoogleDrive file names: ";
         for (FileData file : googleFiles) {
            out += "\nName: " + file.getName() + "\n      Date: "
                    + file.getDate() + "\n      Size: " + file.getSize();
         }
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return out;
   }
   public String findAbsentFilesGoogle(){
      if(googleFiles.isEmpty())
         findGoogleDriveData();
      File dir = new File("C:\\Users\\" + USERNAME
           + "\\Google Drive");
      File[] listOfFiles = dir.listFiles();
      for (FileData data : googleFiles) {
            boolean match = false;
            for(File file : listOfFiles){
               if(data.getName().equals(file.getName())){
                  match = true;
               }
            }
            if(!match){
               data.setAbsent(true);
            }
         }
      String out = "Absent Files: \n";
      for(FileData file : googleFiles){
         if(file.getAbsent()){
            out+= file.getName() + "\n";
         }
      }
      return out;
   }
           /* Getting error when trying to read logs in skydrive, throwing fileNotFoundException (Access is denied)
            * As you can see, I tried changing the permission, setting readable, still getting the error
            * Next is implementing the sqlite reader to get data from the chrome web_data file to determine if something has been used
            *
            * Going to skip thumbcache for the moment since it only applies to images
            *
            * */
}
