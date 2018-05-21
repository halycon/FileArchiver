package com.arkheion.app.service.impl.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arkheion.app.model.service.FileReadRequest;
import com.arkheion.app.model.service.FileReadResponse;
import com.arkheion.app.model.service.FileWriteRequest;
import com.arkheion.app.model.service.FileWriteResponse;
import com.arkheion.app.service.IFileArchiverService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("FileArchiverRest")
public class FileArchiverRestEndpoint {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "FileArchiverService")
	private IFileArchiverService fileArchiverService;

	@ApiOperation(value = "Operation to store file in FileNet repo")
	@RequestMapping(value = "/writeFile", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public FileWriteResponse writeFile(@RequestBody FileWriteRequest fileStoreRequest) {
		return fileArchiverService.writeFile(fileStoreRequest);
	}

	@ApiOperation(value = "Operation to read file/files from FileNet repo")
	@RequestMapping(value = "/readFile", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public FileReadResponse readFile(@RequestBody FileReadRequest fileReadRequest) {
		return fileArchiverService.readFile(fileReadRequest);
	}

	@ApiOperation(value = "Operation to view file from FileNet repo")
	@RequestMapping(value = "/viewFile", method = RequestMethod.GET)
	public ResponseEntity<byte[]> viewFile(@RequestParam(value = "hashkey", required = true) String hashkey,
			@RequestParam(value = "referenceCode", required = true) String referenceCode) {
		FileReadResponse response = fileArchiverService.readFile(new FileReadRequest(hashkey, referenceCode));
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.parseMediaType(response.getFiles().get(0).getMimeType()));
		return new ResponseEntity<byte[]>(response.getFiles().get(0).getData(), responseHeaders, HttpStatus.CREATED);
	}

	public String test() {
		try {
			fileArchiverService.test();
		} catch (Exception e) {
			logger.error("test rest error :: {} ", e);
		}
		return "";
	}

}
