package com.arkheion.app.test;

import java.util.HashMap;
import java.util.UUID;

import com.arkheion.app.model.FileProperty;
import com.arkheion.app.model.FilePropertyType;
import com.arkheion.app.model.Priority;
import com.arkheion.app.model.service.FileWriteRequest;
import com.google.gson.Gson;

public class Test {

	public static void main(String[] args) {
//		String path = "/opt/voucher/20181001";
//		String[] ss = path.split("/");
//		for (String string : ss) {
//			if(!string.isEmpty()){
//				System.out.println(string);
//			}
//		}
//		System.out.println(FileArchiverUtil.generateSalt());
		
//		FileWriteRequest request = new FileWriteRequest();
//		request.setAdditionalProps(new HashMap<String, FileProperty>());
//		request.getAdditionalProps().put("EtsTestProperty",new FileProperty("test", FilePropertyType.String));
//		request.getAdditionalProps().put("CheckInDate",new FileProperty("12122018", FilePropertyType.Date));
//		request.setDocumentClass("Voucher");
//		request.setFilename("testabc.json");
//		request.setMimeType("application/json");
//		request.setPriority(Priority.TEST);
//		request.setData("{ \"test\" : \"test\" }".getBytes());
//		request.setHashkey("91y23123knlksmdfsdf");
//		System.out.println(new Gson().toJson(request));
		System.out.println(UUID.randomUUID());
		
//		FileReadRequest request = new FileReadRequest();
//		request.setReferenceCode("ca0f04e735f09e4348f6023f0cb9035f13a5a264");
//		request.setHashkey("91y23123knlksmdfsdf");
//		System.out.println("{ \"test\" : \"test\" }".getBytes());

	}

}
