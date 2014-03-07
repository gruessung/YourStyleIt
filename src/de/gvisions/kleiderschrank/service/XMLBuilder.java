package de.gvisions.kleiderschrank.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class XMLBuilder
{
  private SQLiteDatabase connection;
  static final String BACKUP_PATH = Environment.getExternalStorageDirectory() + "/kleiderschrank_backup/";
  
  
  public XMLBuilder(SQLiteDatabase paramSQLiteDatabase)
  {
    this.connection = paramSQLiteDatabase;
  }

  public boolean backup() throws ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError
  {
	  
		//Check directory
		File path = new File(BACKUP_PATH);
		if (path.exists() == false)
		{
			path.mkdirs();
			
		}
		//Check directory2
		path = new File(BACKUP_PATH+"/kleiderschrank");
		if (path.exists() == false)
		{
			path.mkdirs();
		}
		
	  
	  String[] tables = { "bilder", "sachen", "outfit", "outfit_link", "tags", "cats"};
	  
//    try
//    {
      Document localDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      Element localElement1 = localDocument.createElement("backup");
      localDocument.appendChild(localElement1);
      
      for (int a = 0; a < tables.length; a++)
      {

      
	      Cursor localCursor = this.connection.rawQuery("select * from "+tables[a], null);
	        Element localElement55 = localDocument.createElement("table");
	        localElement55.setAttribute("name", tables[a]);
	        localElement1.appendChild(localElement55);
	      while (localCursor.moveToNext())
	      {
	        String[] arrayOfString = localCursor.getColumnNames();
	        Element localElement2 = localDocument.createElement("item");
	        localElement55.appendChild(localElement2);
	        for (int i = 0; i < arrayOfString.length; i++)
	        {
	          Element localElement3 = localDocument.createElement(arrayOfString[i]);

	          Log.d("col "+i+"/"+(arrayOfString.length-1), arrayOfString[i]);
	          String content = "";
	          if (localCursor.getString(i) == null)
	          {
	        	  content = "";
	          }
	          else
	          {
	        	  content = localCursor.getString(i);
	          }
	          localElement3.appendChild(localDocument.createTextNode(content));
	          localElement2.appendChild(localElement3);
	          if (arrayOfString[i].equals("pfad"))
	          {
	        	  String filename = localCursor.getString(i);
	        	  String[] split= filename.split("/");
	        	  filename = split[split.length -1];
	        	  File file = new File(BACKUP_PATH+"kleiderschrank/"+filename);
	        	  copy(new File(localCursor.getString(i).replace("file://",  "")), file);
	     
	          }
	          
	        }
	      } // ende while
      } //ende for tables
      
      TransformerFactory.newInstance().newTransformer().transform(new DOMSource(localDocument), new StreamResult(new File(BACKUP_PATH+ "/database.xml")));
      
      
     
      Log.d("BACKUP", "FINISHED to "+BACKUP_PATH);
      
      
      
      
      
//    }
//    catch (Exception localException)
//    {
//      Log.d("ERROR", localException.getMessage());
//     StackTraceElement[] se = localException.getStackTrace();
//     for (StackTraceElement e : se) {
//		Log.d("STACK", e.getClassName()+":"+e.getMethodName()+" on line " + e.getLineNumber() + " in file " + e.getFileName());
//	}
      
//    }
    return false;
  }

  public Boolean restore()
  {
    try
    {
      File localFile = new File(BACKUP_PATH + "/database.xml");
      Document localDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(localFile);
      localDocument.getDocumentElement().normalize();
      NodeList localNodeList = localDocument.getElementsByTagName("table");

      for (int i = 0; i<localNodeList.getLength() ; i++)
      {

        Node localNode = localNodeList.item(i);
        if (localNode.getNodeType() == 1)
        {
        	
          Element localElement = (Element)localNode;
      	Log.d("TABLE",  localElement.getAttribute("name"));

          this.connection.execSQL("DELETE FROM " + localElement.getAttribute("name"));
          
          NodeList nl = localElement.getElementsByTagName("item");
          for (int a = 0; a < nl.getLength(); a++)
          {
        	  
        	  ArrayList<String> aSpalten = new ArrayList<String>();
        	  ArrayList<String> aWerte = new ArrayList<String>();

        	  Node el = nl.item(a);

              NodeList spalten = el.getChildNodes();
                
              
              for (int s = 0; s < spalten.getLength(); s++)
              {
            	  Node node = spalten.item(s);
            	  
            	  aSpalten.add(node.getNodeName());
            	  aWerte.add(node.getTextContent());
              }
              
              
              String sql ="INSERT INTO " +localElement.getAttribute("name") + "(";
             
              for  (String s : aSpalten)
              {
            	  sql = sql + "`"+s+"`";
            	  if (aSpalten.get(aSpalten.size()-1)!=s)
            	  {
            		  sql = sql + ",";
            	  }
              }
              
              sql = sql + ") VALUES (";
              
              for  (String s : aWerte)
              {
            	  sql = sql + "'"+s+"'";
            	  if (aWerte.get(aWerte.size()-1)!=s)
            	  {
            		  sql = sql + ",";
            	  }
              }
              
              sql = sql + ");";
              Log.d("SQL", sql);
              this.connection.execSQL(sql);  
              
              aSpalten.clear(); aWerte.clear();
              
          }

          
          
          
        }
      }
      
      File folder = new File(BACKUP_PATH+"kleiderschrank");
      File[] listOfFiles = folder.listFiles();

      new File(Environment.getExternalStorageDirectory()+"/kleiderschrank").mkdirs();
      
          for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
              System.out.println("File " + listOfFiles[i].getName());
              copy(new File (listOfFiles[i].getPath()), new File(Environment.getExternalStorageDirectory()+"/kleiderschrank/"+listOfFiles[i].getName()));
            }
          }
      
    }
    catch (Exception localException)
    {
      Log.d("ERROR", localException.getMessage());
      localException.printStackTrace();
    }
    return Boolean.valueOf(false);
  }
  
  
  
  public static  void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
  
  

  
}