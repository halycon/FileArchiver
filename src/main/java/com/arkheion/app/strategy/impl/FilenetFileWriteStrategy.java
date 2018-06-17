package com.arkheion.app.strategy.impl;

import com.arkheion.app.model.FileModel;
import com.arkheion.app.model.FileProperty;
import com.arkheion.app.model.FilePropertyType;
import com.arkheion.app.strategy.IFileWriteStrategy;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.*;
import com.filenet.api.core.*;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import com.filenet.api.util.UserContext;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.security.auth.Subject;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component("FilenetFileWriteStrategy")
public class FilenetFileWriteStrategy implements IFileWriteStrategy {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${filenet.uri}")
	private String uri;

	@Value("${filenet.username}")
	private String username;

	@Value("${filenet.password}")
	private String password;

	@Value("${filenet.objectstore}")
	private String objectstore;

	@Value("${filenet.rootFolderName}")
	private String rootFolderName;

	@Resource(name = "gson")
	private Gson gson;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");

	@SuppressWarnings("unchecked")
	@Override
	public FileModel writeFile(FileModel fileModel, boolean addVersion) throws Exception {

		// Make connection.
		Connection conn = Factory.Connection.getConnection(uri);
		Subject subject = UserContext.createSubject(conn, username, password, null);
		UserContext.get().pushSubject(subject);

		try {
			// Get default domain.
			Domain domain = Factory.Domain.fetchInstance(conn, null, null);
			logger.info("Domain: " + domain.get_Name());

			// Get object stores for domain.
			ObjectStore os = Factory.ObjectStore.getInstance(domain, objectstore);

			
			ContentTransfer ct = Factory.ContentTransfer.createInstance();
			ByteArrayInputStream bis = new ByteArrayInputStream(fileModel.getData());
			ct.setCaptureSource(bis);
			ct.set_RetrievalName(fileModel.getFilename());
			ct.set_ContentType(fileModel.getMimetype());

			ContentElementList cel = Factory.ContentElement.createList();
			cel.add(ct);

			if(!addVersion) {
				addNewFileToFilenet(fileModel, os, cel);
			} else{
				addNewVersionToExistingFile(fileModel, os, cel);
				
			}

			logger.info("Connection to Content Platform Engine successful");
		} catch (EngineRuntimeException err) {
			logger.error("error writeFile :: ", err);
			if (err instanceof EngineRuntimeException) {
				EngineRuntimeException fnEx = err;
				if (fnEx.getExceptionCode().equals(ExceptionCode.E_NOT_UNIQUE)) {
					fileModel.setErrorMessage(fileModel.getFilename()+" isminde bir dosya daha önce yaratılmış lütfen unique bir döküman ismi ile tekrar deneyiniz.");
				} else
					fileModel.setErrorMessage(err.getMessage());
			} else{
				fileModel.setErrorMessage(err.getMessage());
			}
			return fileModel;
		} finally {
			UserContext.get().popSubject();
		}
		return fileModel;
	}

	private void addNewVersionToExistingFile(FileModel fileModel, ObjectStore os, ContentElementList cel) {
		Document doc = Factory.Document.getInstance(os, fileModel.getDocument_class(),"/" + rootFolderName + "/"+fileModel.getFilename());
		doc.checkout(ReservationType.EXCLUSIVE, null, null, null);
		doc.save(RefreshMode.REFRESH);
		// Get the reservation object
		Document res = (Document)doc.get_Reservation();
		res.getProperties().putValue("DocumentTitle", fileModel.getFilename());
		if (fileModel.getAddt_props() != null) {
            for (Map.Entry<String, FileProperty> entry : fileModel.getAddt_props().entrySet()) {
                if(FilePropertyType.String.equals(entry.getValue().getType()))
                    res.getProperties().putValue(entry.getKey(), entry.getValue().getValue());
                else
                if(FilePropertyType.Date.equals(entry.getValue().getType())){
                    res.getProperties().putValue(entry.getKey(), getDateValue(entry.getValue().getValue()));
                }
            }
        }
//				logger.info("doc {}",gson.toJson(doc));
		res.set_ContentElements(cel);
		res.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		res.set_MimeType(fileModel.getMimetype());
		res.save(RefreshMode.REFRESH);
		fileModel.setChannel_ref_code(res.get_Id() != null ? res.get_Id().toString() : null);
	}

	private void addNewFileToFilenet(FileModel fileModel, ObjectStore os, ContentElementList cel) {
		Folder folder = getFolder(os, "/" + rootFolderName + ""); // Factory.Folder.getInstance(os,null,"/"+rootFolderName+path);

		if (folder == null) {
            logger.info("folder is null");
            String[] pathSplittedArray = "".split("/"); // fileModel.getPath().split("/");
            Folder rootFolder = getFolder(os, "/" + rootFolderName);

            for (String string : pathSplittedArray) {
                if (!string.isEmpty()) {
                    folder = Factory.Folder.createInstance(os, null);
                    folder.set_Parent(rootFolder);
                    folder.set_FolderName(string);
                    folder.save(RefreshMode.NO_REFRESH);
                    rootFolder = folder;
                }
            }
        }

		Document doc = Factory.Document.createInstance(os, fileModel.getDocument_class());

		doc.getProperties().putValue("DocumentTitle", fileModel.getFilename());
		if (fileModel.getAddt_props() != null) {
            for (Map.Entry<String, FileProperty> entry : fileModel.getAddt_props().entrySet()) {
                if(FilePropertyType.String.equals(entry.getValue().getType()))
                    doc.getProperties().putValue(entry.getKey(), entry.getValue().getValue());
                else
                if(FilePropertyType.Date.equals(entry.getValue().getType()))
                    doc.getProperties().putValue(entry.getKey(), getDateValue(entry.getValue().getValue()));
            }
        }
//				logger.info("doc {}",gson.toJson(doc));
		doc.set_ContentElements(cel);
		doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		doc.set_MimeType(fileModel.getMimetype());
		doc.save(RefreshMode.REFRESH);

		ReferentialContainmentRelationship rcr = null;
		if (folder != null) {
            rcr = folder.file(doc, AutoUniqueName.NOT_AUTO_UNIQUE,
fileModel.getFilename(), DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
        }
		rcr.save(RefreshMode.REFRESH);
		fileModel.setChannel_ref_code(doc.get_Id() != null ? doc.get_Id().toString() : null);
	}

	@SuppressWarnings("deprecation")
	private Date getDateValue(String value){
		try {
			Date date = formatter.parse(value);
			date.setHours(12);
			return date;
		} catch (Exception e) {
			return null;
		}
	}

	private Folder getFolder(ObjectStore os, String path) {
		try {
			return Factory.Folder.fetchInstance(os, "/" + path, null);
		} catch (Exception e) {
			return null;
		}
	}


}
