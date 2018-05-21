package com.arkheion.app.service.impl.soap;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.arkheion.app.model.service.FileReadRequest;
import com.arkheion.app.model.service.FileReadResponse;
import com.arkheion.app.model.service.FileWriteRequest;
import com.arkheion.app.model.service.FileWriteResponse;
import com.arkheion.app.service.IFileArchiverService;


@Component(value = "FileArchiverSoapEndpoint")
@WebService(targetNamespace = "")
public class FileArchiverSoapEndpoint {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "FileArchiverService")
	private IFileArchiverService fileArchiverService;

	@WebMethod(operationName="writeFile")
	public FileWriteResponse writeFile(FileWriteRequest fileStoreRequest) {
		return fileArchiverService.writeFile(fileStoreRequest);
	}

	@WebMethod(operationName="readFile")
	public FileReadResponse readFile(FileReadRequest fileReadRequest) {
		return fileArchiverService.readFile(fileReadRequest);
	}
	
}
