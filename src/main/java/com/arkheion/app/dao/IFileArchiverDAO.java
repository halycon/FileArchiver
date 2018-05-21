package com.arkheion.app.dao;

import java.time.LocalDateTime;
import java.util.Map;

import com.arkheion.app.model.Channel;
import com.arkheion.app.model.Error;
import com.arkheion.app.model.Implementation;
import com.arkheion.app.model.Profile;
import com.arkheion.app.model.FileModel;

public interface IFileArchiverDAO {

	public Channel getChannelById(long id);

	public Profile getProfileById(int id);

	public Implementation getImplementationById(long id);

	public FileModel addQueueRecord(FileModel queueRecord);

	public boolean checkDocumentClassExists(String document_class);

	public Error validateAdditionalProperties(String document_class, Map<String, String> addt_props);

	public Profile getProfileByHashkey(String hashkey);

	public FileModel getQueueRecord(String hashkey, int profileId);

	public FileModel getQueueRecordById(String id);

	public boolean updateQueueRecordProcessDateById(LocalDateTime processDate, boolean status, int id,
			String channel_ref_code, String errorMessage);
}
