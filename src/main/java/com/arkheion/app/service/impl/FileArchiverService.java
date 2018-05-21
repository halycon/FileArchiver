package com.arkheion.app.service.impl;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.arkheion.app.dao.IFileArchiverDAO;
import com.arkheion.app.model.Error;
import com.arkheion.app.model.ErrorType;
import com.arkheion.app.model.FileModel;
import com.arkheion.app.model.FileProperty;
import com.arkheion.app.model.FilePropertyType;
import com.arkheion.app.model.Profile;
import com.arkheion.app.model.service.FileReadRequest;
import com.arkheion.app.model.service.FileReadResponse;
import com.arkheion.app.model.service.FileResponseModel;
import com.arkheion.app.model.service.FileWriteRequest;
import com.arkheion.app.model.service.FileWriteResponse;
import com.arkheion.app.rabbitmq.RabbitMQFileProducer;
import com.arkheion.app.service.IFileArchiverService;
import com.arkheion.app.strategy.IFileArchiverContext;
import com.arkheion.app.strategy.IFileReadStrategy;
import com.arkheion.app.strategy.IFileWriteStrategy;
import com.arkheion.app.strategy.impl.FileArchiverContext;
import com.arkheion.app.util.FileArchiverUtil;
import com.google.gson.Gson;

@Component("FileArchiverService")
public class FileArchiverService implements IFileArchiverService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "FileArchiverJdbcDOA")
	private IFileArchiverDAO fileArchiverJdbcDOA;

	@Resource(name = "FilenetFileWriteStrategy")
	private IFileWriteStrategy filenetFileWriteStrategy;

	@Resource(name = "UnixSystemFileWriteStrategy")
	private IFileWriteStrategy unixSystemFileWriteStrategy;

	@Resource(name = "FilenetFileReadStrategy")
	private IFileReadStrategy filenetFileReadStrategy;

	@Resource(name = "UnixSystemFileReadStrategy")
	private IFileReadStrategy unixSystemFileReadStrategy;

	@Resource(name = "RabbitMQFileProducer")
	private RabbitMQFileProducer rabbitMQFileProducer;

	@Resource(name = "FileArchiverUtil")
	private FileArchiverUtil fileArchiverUtil;
	
	@Resource(name = "gson")
	private Gson gson;

	@Override
	public FileWriteResponse writeFile(FileWriteRequest request) {

		FileWriteResponse response = new FileWriteResponse();
		if (request.getFilename() == null) {
			response.setError(new Error(ErrorType.MissingField, "filename missing"));
			return response;
		}

		if (request.getData() == null) {
			response.setError(new Error(ErrorType.MissingField, "data missing"));
			return response;
		}

		if (StringUtils.isEmpty(request.getHashkey())) {
			response.setError(new Error(ErrorType.MissingField, "hashkey missing"));
			return response;
		}

		Profile profile = fileArchiverJdbcDOA.getProfileByHashkey(request.getHashkey());

		if (profile == null) {
			response.setError(new Error(ErrorType.InvalidValue, "invalid hashkey"));
			return response;
		}

		logger.info("additional params {}",gson.toJson(request.getAdditionalProps()));
		
		IFileArchiverContext fileArchiverContext = new FileArchiverContext();
		fileArchiverContext.setFileWriteStrategy(unixSystemFileWriteStrategy);

		FileModel fileModel = new FileModel();
		fileModel.setPath("");
		fileModel.setFilename(request.getFilename());
		fileModel.setAddt_props(request.getAdditionalProps());
		fileModel.setData(request.getData());
		fileModel.setMimetype(request.getMimeType());
		fileModel.setRef_code(fileArchiverUtil.generateSalt());
		fileModel.setLocalStoreFileName(fileModel.getRef_code() + "≈"
				+ FileArchiverUtil.formatter.format(LocalDateTime.now()) + "≈" + request.getFilename());
		fileModel.setDocument_class(request.getDocumentClass());
		fileModel.setCreate_date(LocalDateTime.now());
		fileModel.setTopic_id(request.getPriority());
		fileModel.setDocument_class(request.getDocumentClass());
		fileModel.setAddt_props(request.getAdditionalProps());
		fileModel.setProfile_id(profile.getId());
		fileModel = fileArchiverJdbcDOA.addQueueRecord(fileModel);

		try {
			if (fileModel.getId() > 0) {

				fileModel = fileArchiverContext.writeFile(fileModel, false);

				if (fileModel != null) {

					boolean kafkaProduceFileStatus = rabbitMQFileProducer.produceFile(fileModel);
					if (kafkaProduceFileStatus) {
						response.setCreationDate(LocalDateTime.now());
						response.setReferenceCode(fileModel.getRef_code());
						response.setViewUrl("http://"+InetAddress.getLocalHost().getHostAddress()+"/FileArchiverRest/viewFile?hashkey="+request.getHashkey()+"&referenceCode="+fileModel.getRef_code());
						response.setSuccess(true);
					} else {
						response.setError(new Error(ErrorType.InternalException, "kafka queue add error"));
					}

				} else {
					response.setError(new Error(ErrorType.InternalException, "temporary file store error"));
					return response;
				}
			} else {
				response.setError(new Error(ErrorType.InternalException, "db queue add error"));
				return response;
			}

		} catch (Exception e) {
			logger.error("FileArchiverService :: {}", e);
		}

		return response;
	}

	@Override
	public FileReadResponse readFile(FileReadRequest request) {
		FileReadResponse response = new FileReadResponse();

		if (request.getReferenceCode() == null
				&& (request.getDocumentClass() == null && request.getAddt_props() == null)) {
			response.setError(new Error(ErrorType.MissingField, "referenceCode is missing"));
			return response;
		}

		if (request.getReferenceCode() == null
				&& (request.getDocumentClass() != null && request.getAddt_props() == null)) {
			response.setError(new Error(ErrorType.MissingField, "additional properties  missing"));
			return response;
		}

		if (request.getReferenceCode() == null
				&& (request.getDocumentClass() == null && request.getAddt_props() != null)) {
			response.setError(new Error(ErrorType.MissingField, "document class missing"));
			return response;
		}

		IFileArchiverContext fileArchiverContext = new FileArchiverContext();
		fileArchiverContext.setFileReadStrategy(filenetFileReadStrategy);

		Profile profile = fileArchiverJdbcDOA.getProfileByHashkey(request.getHashkey());

		if (profile == null) {
			response.setError(new Error(ErrorType.InvalidValue, "invalid hashkey"));
			return response;
		}

		try {
			String fileName = null;
			if(request.getReferenceCode()!=null){
				
				if (request.getReferenceCode() != null && request.getReferenceCode().startsWith("{") &&
						request.getReferenceCode().endsWith("}")){
					
					fileName = request.getReferenceCode();
				}else{
				
					FileModel dbFileModel = fileArchiverJdbcDOA.getQueueRecord(request.getReferenceCode(), profile.getId());
					if (dbFileModel != null && dbFileModel.getDocument_class() == null) {
						response.setError(new Error(ErrorType.InternalException, "file not found"));
						return response;
					}
					fileName = dbFileModel!=null ? dbFileModel.getChannel_ref_code() : null;	
				}
			}
			
			
			List<FileModel> fileModels = fileArchiverContext.readFile(
					fileName, "", request.getAddt_props(),
					request.getDocumentClass());
			response.setSuccess(fileModels != null ? true : false);
			if (response.isSuccess()) {
				// logger.info( new String(fileModel.getData(),
				// StandardCharsets.UTF_8));
				if(fileModels!=null && fileModels.size()>0){
					response.setFiles(new ArrayList<>());				
					for (FileModel fileModel : fileModels) {
						FileResponseModel fileResponse = new FileResponseModel();						
						fileResponse.setMimeType(fileModel.getMimetype());
						fileResponse.setDocumentClass(fileModel.getDocument_class());
						fileResponse.setCreationDate(fileModel.getCreate_date());
						fileResponse.setData(fileModel.getData());
						fileResponse.setFileName(fileModel.getFilename());
						fileResponse.setViewUrl("http://"+InetAddress.getLocalHost().getHostAddress()+"/FileArchiverRest/viewFile?hashkey="+request.getHashkey()+"&referenceCode="+URLEncoder.encode(fileModel.getChannel_ref_code()));
						response.getFiles().add(fileResponse);
					}
				}
				
				
			}
		} catch (Exception e) {
			logger.error("readFile rest error :: {} ", e);
		}

		return response;
	}

	@Override
	public void test() throws Exception {
		// test function

		FileModel fileModel = new FileModel();
		fileModel.setPath("");
		fileModel.setFilename("footer-bg-termal1.jpg");
		fileModel.setData(unixSystemFileReadStrategy
				.readFile("footer-bg-termal.jpg", "/Users/volkancetin/Downloads/", null, null).get(0).getData());
		fileModel.setRef_code("ibm_filenet.pdf");
		HashMap<String, FileProperty> additionalProps = new HashMap<String, FileProperty>();
		additionalProps.put("EtsTestProperty", new FileProperty("test",FilePropertyType.Date));
		fileModel.setAddt_props(additionalProps);
		fileModel.setMimetype("image/jpg");
		filenetFileWriteStrategy.writeFile(fileModel, false);

	}

}
