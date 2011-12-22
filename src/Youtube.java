/*******************************************************************************
 * Copyright (c) 2010 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashMap;


public class Youtube{

	private String videoID;
	private String infoData;
	
	public static HashMap<String,String> fmtList = initFmt();
	
	public Youtube(String url){
		int start = url.indexOf("v=")+2;
		initFmt();
		this.videoID = url.substring(start,start+11); //videoid value lenght = 11;
		this.infoData = getInfoData();
	}
	
	

	private String getInfoData(){
		String data = "";
		try {

			URL yt = new URL("http://youtube.com/get_video_info?&video_id="+videoID+"&el=detailpage&ps=default&eurl=&gl=US&hl=en");
			URLConnection yc = yt.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc
					.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {

				data +=inputLine;

			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public HashMap<String, String> getLinks() throws UnsupportedEncodingException{
		HashMap<String, String> ytlinks = new HashMap<String, String>();
		String url = infoData;
		int start = url.indexOf("url_encoded_fmt_stream_map=")+27;
		String url1 = url.substring(start, url.indexOf("&",start));
		String[] urls = url1.split("url%3D");

		for (String u : urls){

			if(!u.isEmpty()){
				u = URLDecoder.decode(u,"UTF-8");
				u = u.replaceAll("%25", "%").replaceAll("%26", "&").replaceAll("%3F", "?").replaceAll("%2F", "/").replaceAll("%3A", ":").replaceAll("%3D", "=");
				int q = u.indexOf("&itag=")+6;

				String quality=(u.substring(q,u.indexOf("&",q)));
				System.out.println(quality);
				
				ytlinks.put(quality,u.substring(0,u.indexOf("&quality",q)));
			}
		}

		return ytlinks;
	}


	public String getVideoTitle() throws UnsupportedEncodingException{
		String title="";
		int start  = infoData.indexOf("&title=")+7;
		title = infoData.substring(start,infoData.indexOf("&",start));
		return URLDecoder.decode(title,"UTF-8");
	}


	private static HashMap<String, String> initFmt(){
		HashMap<String, String> fmt = new HashMap<String,String>();
		
		fmt.put("3GP - 176x144", "17");      
		
		fmt.put("FLV - 400x240","5");                     
		fmt.put("FLV - 480x270","6");
		fmt.put("FLV - 640x360", "34");                     
		fmt.put("FLV - 854x480", "35"); 

		              

		fmt.put("MP4 - 640x360", "18");                    
		fmt.put("MP4 - 1280x720","22");                               
		fmt.put("MP4 - 1920x1080", "37"); 
		fmt.put("MP4 - 4096x3072", "38");    

		fmt.put("WEBM - 640x360", "43"); 
		fmt.put("WEBM - 854x480", "44"); 
		fmt.put("WEBM - 1280x720", "45"); 
		return fmt;
	}
	
	public HashMap<String,String> getFmtList(){
		return fmtList;
	}

	public String getExt(String fmtList){
		if (fmtList.equals("5") ||fmtList.equals("34")||fmtList.equals("35") ) return ".flv";
		if (fmtList.equals("17")) return ".3gp";
		if (fmtList.equals("18")||fmtList.equals("22")||fmtList.equals("37")) return ".mp4";
		if (fmtList.equals("43")||fmtList.equals("44")||fmtList.equals("45")) return ".WEBM";
		return null;
	}

}
