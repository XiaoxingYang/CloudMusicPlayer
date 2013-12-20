/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ds.coursework.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.bigdatapro.service.TransferManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author liguo
 */
public class MusicManagementService extends HttpServlet
{

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws java.io.IOException if an I/O error occurs
     */
    private final String CREATEPLAYLIST = "createplaylist";
    private final String MUSICLISTS = "fetchmusiclists";
    private final String DELETEPLAYLIST = "deleteplaylist";
//    private final String UPLOADMUSIC = "uploadmusic";
    private final String DELETEMUSIC = "deletemusic";
    private final String PLAYLISTS = "fetchplaylists";
    private final String DOWNLOADMUSIC = "downloadmusic";
    private AWSCredentials s3Credentials;
    private RestS3Service s3Service;
    public MusicManagementService()
    {
        try
        {
            s3Credentials = TransferManager.loadKeys();
            // To communicate with S3 use the RestS3Service.
            s3Service = TransferManager.connectToS3(s3Credentials);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String parameter, String choice)
            throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
            //String methodType = request.getParameter("select"); //get action value from the URL
            //String bucketName=request.getParameter("textfield");
            if (choice.equalsIgnoreCase(PLAYLISTS))
            {
                // A good test to see if your S3Service can connect to S3 is to list all the buckets you own.
                // Fetching all the buckets that you own and return them as a JSON array for client to process
            	
            	//1.there is no playlist exists,error
            	//2.display playlist
            	
                S3Bucket[] myBuckets = TransferManager.listAllBuckets(s3Service); //get all buckets
            	JSONObject jObj = new JSONObject();
                JSONArray jArray = new JSONArray();
                if (myBuckets.length==0){
                    jObj.put("result", "no playlist exists");
                    request.setAttribute("result", jObj);
                }else if(myBuckets.length>0){
	                for (S3Bucket bucket : myBuckets)
	                {
	                    jArray.add(bucket.getName());  //put bucket list into JSON array
	                }
	                request.setAttribute("result", jArray);
                }else{
                    jObj.put("result", "error");
                    request.setAttribute("result", jObj);
                }
                request.getRequestDispatcher("mainpage.jsp").forward(request,response);
            
            } else if (choice.equalsIgnoreCase(CREATEPLAYLIST))
            {
            	//1.the name of playlist already exist,error
            	//2.the playlist is empty, error
            	//3.create playlist successfully

                S3Bucket[] buckets=TransferManager.listAllBuckets(s3Service);
                JSONObject jObj=new JSONObject();

            	parameter = parameter.replaceAll("\\s","");
                boolean existlist=false;
                for(int i=0;i<buckets.length;i++){
                	if (buckets[i].getName().equals(parameter)){
                        jObj.put("result"," already exists, please rename");
                        existlist=true;
                	}
                }
                if(existlist==false){	

                	S3Bucket myBucket = TransferManager.createBucket(s3Service, parameter); //get all buckets
	                if (myBucket == null)
	                {
	                    jObj.put("result", "can't create,please change name");
	                } else if(myBucket!=null)
	                {
	                    jObj.put("result","succeed create ");
	                }else{
	                    jObj.put("result", "error");
	                }
                }
                request.setAttribute("result", jObj);
                request.getRequestDispatcher("mainpage.jsp").forward(request,response);
            } else if (choice.equalsIgnoreCase(DELETEPLAYLIST))
            {
                //Leave to the students to complete
            	//1.the playlist exist but there are music in it, can't delete
            	//2.the playlist exist, delete succeed
            	//3.the playlist doesn't exist
            	
                S3Bucket[] buckets=TransferManager.listAllBuckets(s3Service);
                JSONObject jObj=new JSONObject();
                boolean exist=false;
                for(int i=0;i<buckets.length;i++){
                	if(parameter.equals(buckets[i].getName())){
                		S3Object[] objects =TransferManager.listAllObjects(s3Service,parameter);
                        if(objects.length>0){
                        	jObj.put("result", " playlist is not empty,please delete music in it first");
                        }else if(objects.length==0){
	                        TransferManager.deleteBucket(s3Service,buckets[i]);
	                        jObj.put("result","successful delete ");
                        }else{
                        	jObj.put("result","error ");
                        }
                		exist=true;
                	}
                }
                if(!exist){
                	jObj.put("result"," doesn't exist");
                }
                request.setAttribute("result", jObj);
                request.getRequestDispatcher("mainpage.jsp").forward(request,response);
            } else if (choice.equalsIgnoreCase(DELETEMUSIC))
            {
                //Leave to the students to complete
            	//1.find music successfully and delete
            	//2.no such music in playlist,error
            	
                S3Bucket[] buckets=TransferManager.listAllBuckets(s3Service);
                JSONObject jObj=new JSONObject();
                Boolean delete=false;
                for(int i=0;i<buckets.length;i++){
                    S3Object[] objects=TransferManager.listAllObjects(s3Service,buckets[i].getName());
                    for(int j=0;j<objects.length;j++){
                        if(parameter.equals(objects[j].getName())){
                            TransferManager.deleteObject(s3Service,buckets[i],objects[j].getName());
                            jObj.put("result","successful delete ");
                            delete=true;
                        }
                    }
                }
                if(delete==false){
                    jObj.put("result","this music doesn't exist");
                }
                request.setAttribute("result", jObj);
                request.getRequestDispatcher("mainpage.jsp").forward(request,response);
            } else if (choice.equalsIgnoreCase(MUSICLISTS))
            {
            	//1.show the musicname in JSONArray
            	//2.there is no playlistname in cloud,error
                //String playListName = request.getParameter("playlistname");
                
                S3Bucket[] buckets=TransferManager.listAllBuckets(s3Service);
            	JSONObject jObj=new JSONObject();
                boolean exist=false;
                for(int i=0;i<buckets.length;i++){
                	if(parameter.contains(buckets[i].getName())){
                        exist=true;
                		S3Object[] s3Objects = TransferManager.listAllObjects(s3Service, parameter);
                		if(s3Objects.length==0){
                			jObj.put("result", "musiclist is empty");
                			request.setAttribute("result", jObj);
        	                request.getRequestDispatcher("mainpage.jsp").forward(request,response);
                		}else{
                			JSONArray jArray = new JSONArray();
                            for (S3Object s3Object : s3Objects)
                            {
                                JSONObject musicJson = new JSONObject();
                                musicJson.put("musicname", s3Object.getName());
                                //Think about what more information you will need on your music player side for each individual music file?
                                jArray.add("<br>"+musicJson);
                            }
                            request.setAttribute("result", jArray);
        	                request.getRequestDispatcher("mainpage.jsp").forward(request,response);
                		}
                	}
                }
                if(!exist){
                	jObj.put("result","musiclist doesn't exist");
                	request.setAttribute("result", jObj);
	                request.getRequestDispatcher("mainpage.jsp").forward(request,response);
                }
                
            }
            else{
            	JSONObject jObj=new JSONObject();
            	jObj.put("result", "error action");
            	//pass result to mainpage and display
            	request.setAttribute("result", jObj);
                request.getRequestDispatcher("mainpage.jsp").forward(request,response);
            }

        } finally
        {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws java.io.IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    	//get parameter from mainpage
    	String parameter=request.getParameter("textfield");
    	String choice=request.getParameter("select");
        processRequest(request, response,parameter,choice);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws java.io.IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    	//get parameter from mainpage
    	String parameter=request.getParameter("textfield");
    	String choice=request.getParameter("select");
        processRequest(request, response,parameter,choice);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>
}
