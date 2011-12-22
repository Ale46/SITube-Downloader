/*******************************************************************************
 * Copyright (c) 2010 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

class COptions{

	private String name;
	private String acodec,vcodec,channel,frequency,tag,category;
	private String ext;


	COptions(String name, String acodec,String vcodec, String channel, String frequency, String tag, String ext, String category){
		this.name = name;
		this.acodec = acodec;
		this.vcodec = vcodec;
		this.channel = channel;
		this.frequency = frequency;
		this.tag = tag;
		this.ext  = ext;
		this.category = category;
	}

	COptions(){

	}

	public String getName() {
		return this.name;
	}

	public String getAcodec() {
		return this.acodec;
	}

	public String getVcodec() {
		return this.vcodec;
	}

	public String getChannel() {
		return this.channel;
	}

	public String getFrequency() {
		return this.frequency;
	}

	public String getTag() {
		return this.tag;
	}
	
	public String getCategory() {
		return this.category;
		
	}
	public String getExt() {
		return this.ext;
	}

}

public class Options{
	
	private InputStream xmlFile;

	Options(InputStream inputStream){
		this.xmlFile = inputStream;
	}

	public ArrayList<COptions> read(){
		String label,acodec,vcodec,channel,frequency,tag,ext,category;
		ArrayList<COptions> readed  = new ArrayList<COptions>();
		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(xmlFile);
			Element rootElement = document.getRootElement();
			List<?> children = rootElement.getChildren();
			Iterator<?> iterator = children.iterator();
			while (iterator.hasNext()){
				Element element = (Element)iterator.next();
				label=element.getChildText("label");
				acodec=element.getChildText("acodec");
				vcodec = element.getChildText("vcodec");
				channel = element.getChildText("channel");
				frequency = element.getChildText("frequency");
				tag = element.getChildText("tag");
				ext=element.getChildText("extension");
				category = element.getChildText("category");
				readed.add(new COptions(label,acodec,vcodec,channel,frequency,tag,ext,category));
			} 

		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		return readed;
	}

}
