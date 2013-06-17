/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package desktopapplication3;

/**
 *
 * @author Sean
 */
public class FileData {
   String name = null;
   String date = null;
   String size = null;
   boolean absent = false;

   public FileData(){
   }

   public void setName(String str){
      this.name = str;
   }
   public void setDate(String str){
      this.date = str;
   }
   public void setSize(String str){
      this.size = str;
   }
   public String getName(){
      return this.name;
   }
   public String getDate(){
      return this.date;
   }
   public String getSize(){
      return this.size;
   }

   public void setAbsent(boolean bool){
      this.absent = bool;
   }

   public boolean getAbsent(){
      return absent;
   }

   @Override
   public String toString(){
      return this.name;
   }
}
