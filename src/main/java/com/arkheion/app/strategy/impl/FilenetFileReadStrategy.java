package com.arkheion.app.strategy.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.security.auth.Subject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.arkheion.app.model.FileModel;
import com.arkheion.app.model.FileProperty;
import com.arkheion.app.model.FilePropertyType;
import com.arkheion.app.strategy.IFileReadStrategy;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.PageIterator;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.UserContext;
import com.google.gson.Gson;

@SuppressWarnings("deprecation")
@Component("FilenetFileReadStrategy")
public class FilenetFileReadStrategy implements IFileReadStrategy {

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
	// private String searchClassScope = true ? "INCLUDESUBCLASSES" :
	// "EXCLUDESUBCLASSES";

	private ISO8601DateFormat iso8601Formatter = new ISO8601DateFormat();
	
	private SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
	
	private SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
	
	private Pattern p = Pattern.compile("\\w+(?:\\.\\*)?$");

	@Override
	public List<FileModel> readFile(String filename, String path, ArrayList<HashMap<String, FileProperty>> addt_props, String documentClass) {
		// Make connection.
		if (documentClass == null)
			documentClass = "DOCUMENT";

		Connection conn = Factory.Connection.getConnection(uri);
		Subject subject = UserContext.createSubject(conn, username, password, null);
		UserContext.get().pushSubject(subject);
		List<FileModel> files = null; 
		try {
			// Get default domain.
			Domain domain = Factory.Domain.fetchInstance(conn, null, null);
			logger.info("Domain: " + domain.get_Name());

			// Get object stores for domain.
			ObjectStore os = Factory.ObjectStore.getInstance(domain, objectstore);

			boolean isIdSearch = false;
			if (filename != null && filename.startsWith("{") && filename.endsWith("}"))
				isIdSearch = true;

			String filterParams = "";
			if (isIdSearch)
				filterParams = " doc.[Id] = " + filename + " ";

			
			if (filename == null && addt_props != null && addt_props.size() > 0) {
				filterParams = " AND ( ";
				

					
					int i = 0;
					for (Map<String, FileProperty> map : addt_props) {
						if(i>0)
							filterParams += " OR ";
						
						int f = 0;
						for (Map.Entry<String, FileProperty> entry : map.entrySet()) {
							if(f==0)
								filterParams += " ( ";							
							if(f>0)
								filterParams += " AND ";
							
							if(FilePropertyType.String.equals(entry.getValue().getType()))
								filterParams += "  doc.[" + entry.getKey() + "] = '" + entry.getValue().getValue() + "' ";
							else
							if(FilePropertyType.Date.equals(entry.getValue().getType()))
								filterParams += " ( doc.[" + entry.getKey() + "] > " + getDateValue(entry.getValue().getValue())+" AND "+
									" doc.[" + entry.getKey() + "] < " + getDateValue2(entry.getValue().getValue())+" ) ";
								
							if(f==map.entrySet().size()-1)
								filterParams += " ) ";
							f++;
						}
						i++;
					}				
				
				
				filterParams = p.matcher(filterParams).replaceFirst("");
				filterParams += " ) ";
			}
			
			
//			String queryPart1 = "  LEFT JOIN [ReferentialContainmentRelationship] rcr "
//					+ "  ON doc.[VersionSeries] = rcr.[VersionSeries] .. rcr.[Head] IS NULL AND ";
			String queryPart2 = " (doc.[IsCurrentVersion] = TRUE OR doc.[VersionStatus] = 3) ";

			String searchQ = "SELECT doc.* FROM [" + documentClass + "] doc  ";

			searchQ += " WHERE ";
			if (!isIdSearch)
				searchQ += queryPart2;
			searchQ += filterParams;

			
//			searchQ = " SELECT doc.* FROM [Voucher] doc where (doc.[IsCurrentVersion] = TRUE  OR doc.[VersionStatus] = 3) and doc.[CheckInDate] = '12.12.2018 00:00' ";
			
			logger.info("searchQ :: {}", searchQ);
			
			SearchSQL sqlObject = new SearchSQL(searchQ);
			SearchScope searchScope = new SearchScope(os);

			IndependentObjectSet objectSet = searchScope.fetchObjects(sqlObject, null, null, new Boolean(true));
			PageIterator p = objectSet.pageIterator();
		
			
			// Loop through each page
			while(p.nextPage()) {
				if(files==null) 
					files = new ArrayList<>();
				
				// Loop through each item in the page
				for (Object obj : p.getCurrentPage()) {
					
					// Get the document object and write Document Title
					Document doc = (Document) obj;
//					logger.info("doc {}", gson.toJson(doc) );
					
					FileModel fileModel = new FileModel();
					fileModel.setFilename(doc.get_Name());
					fileModel.setDocument_class(doc.getClassName());
					fileModel.setMimetype(doc.get_MimeType());
					fileModel.setData(IOUtils.toByteArray(doc.accessContentStream(0)));
					fileModel.setChannel_ref_code(doc.get_Id().toString());
					fileModel.setCreate_date(LocalDateTime.of(
							Instant.ofEpochMilli(doc.get_DateCreated().getTime()).atZone(ZoneId.systemDefault())
									.toLocalDate(),
							LocalTime.of(doc.get_DateCreated().getHours(), doc.get_DateCreated().getMinutes(),
									doc.get_DateCreated().getSeconds())));
					
				
//					fileModel.setAddt_props(addt_props);

					files.add(fileModel);
				}
				
			}

			logger.info("Connection to Content Platform Engine successful");
		} catch (EngineRuntimeException err) {
			
			logger.error("error readFile  EngineRuntimeException :: ", err);
		} catch (IOException err) {
						
			logger.error("error readFile IOException :: ", err);
		} finally {
			UserContext.get().popSubject();
		}
		return files;
	}
	
	private String getDateValue(String value){
		try {
			Date date = (Date)formatter.parse(value);
			date.setHours(12);
			return formatter2.format(date)+"T000000Z";			
		} catch (Exception e) {
			return null;
		}
	}
	
	private String getDateValue2(String value){
		try {
			Date date = (Date)formatter.parse(value);
			date.setHours(12);
			return formatter2.format(date)+"T235959Z";			
		} catch (Exception e) {
			return null;
		}
	}

}
