package com.arkheion.app.dao.impl.jdbc;

import com.arkheion.app.dao.IConnectionPool;
import com.arkheion.app.dao.IFileArchiverDAO;
import com.arkheion.app.model.*;
import com.arkheion.app.model.Error;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component("FileArchiverJdbcDOA")
public class FileArchiverJdbcDAO implements IFileArchiverDAO {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "connectionPool")
	private IConnectionPool pool;

	@Resource(name = "gson")
	private Gson gson;
	
	private Type mapType = new TypeToken<HashMap<String, FileProperty>>(){}.getType();

	@Override
	public Channel getChannelById(long id) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Channel channel = null;
		try {
			String query = " select * from arkheion.channel where id = ? ";
			connection = pool.getConnection();

			pstmt = connection.prepareStatement(query);
			pstmt.setLong(1, id);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				channel = new Channel();
				channel.setId(id);
				channel.setImplement_id(resultSet.getLong("implement_id"));
				channel.setName(resultSet.getString("name"));
				channel.setProfile_id(resultSet.getLong("profile_id"));
				channel.setCreate_date(resultSet.getObject("create_date", LocalDateTime.class));
				channel.setStatus(resultSet.getBoolean("status"));
			}


		} catch (Exception e) {
			logger.error("getChannelById db error : {}", e);
		} finally {
            closeStatementAndResultSet(pstmt, resultSet);

            pool.closeConnection(connection);
		}
		return channel;
	}

	@Override
	public Profile getProfileById(int id) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Profile profile = null;
		try {
			String query = " select * from arkheion.profile where id = ? ";
			connection = pool.getConnection();

			pstmt = connection.prepareStatement(query);
			pstmt.setLong(1, id);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				profile = new Profile();
				profile.setId(id);
				profile.setHashkey(resultSet.getString("hashkey"));
				profile.setName(resultSet.getString("name"));
				profile.setCreate_date(resultSet.getObject("create_date", LocalDateTime.class));
				profile.setStatus(resultSet.getBoolean("status"));
			}


		} catch (Exception e) {
			logger.error("getProfileById db error : {}", e);
		} finally {
            closeStatementAndResultSet(pstmt, resultSet);
            pool.closeConnection(connection);
		}
		return profile;
	}

	@Override
	public Implementation getImplementationById(long id) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Implementation implementation = null;
		try {
			String query = " select * from arkheion.implementation where id = ? ";
			connection = pool.getConnection();

			pstmt = connection.prepareStatement(query);
			pstmt.setLong(1, id);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				implementation = new Implementation();
				implementation.setId(id);
				implementation.setName(resultSet.getString("name"));
				implementation.setCreate_date(resultSet.getObject("create_date", LocalDateTime.class));
				implementation.setStatus(resultSet.getBoolean("status"));
			}

		} catch (Exception e) {
			logger.error("getImplementationById db error : {}", e);
		} finally {
            closeStatementAndResultSet(pstmt, resultSet);
            pool.closeConnection(connection);
		}
		return implementation;
	}

	@Override
	public FileModel addQueueRecord(FileModel queueRecord) {
		Connection con = null;
        ResultSet resultSet = null;
        PreparedStatement pstmt = null;
		try {
			con = pool.getConnection();

            pstmt = con.prepareStatement("insert into arkheion.queue "
					+ "(channel_id, ref_code, profile_id, status, create_date,  topic_id,"
					+ " document_class, addt_props , filename , localStoreFileName , mimetype) "
					+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, queueRecord.getChannel_id());
            pstmt.setString(2, queueRecord.getRef_code());
            pstmt.setInt(3, queueRecord.getProfile_id());
            pstmt.setBoolean(4, queueRecord.isStatus());
            pstmt.setTimestamp(5, Timestamp.valueOf(queueRecord.getCreate_date()));
            pstmt.setInt(6, queueRecord.getTopic_id().getId());
            pstmt.setString(7, queueRecord.getDocument_class());
            pstmt.setString(8, gson.toJson(queueRecord.getAddt_props()));
            pstmt.setString(9, queueRecord.getFilename());
            pstmt.setString(10, queueRecord.getLocalStoreFileName());
            pstmt.setString(11, queueRecord.getMimetype());
            pstmt.executeUpdate();
            resultSet = pstmt.getGeneratedKeys();
			if (resultSet.next()) {
				queueRecord.setId(resultSet.getInt(1));
			}


		} catch (Exception e) {
			logger.info("addQueueRecord {}", e);
		} finally {
            closeStatementAndResultSet(pstmt, resultSet);
            pool.closeConnection(con);
		}

		return queueRecord;
	}

	@Override
	public boolean checkDocumentClassExists(String document_class) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		boolean exists = false;
		try {
			String query = " select * from arkheion.document_class where name = ? ";
			connection = pool.getConnection();

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, document_class);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				exists = true;
			}


		} catch (Exception e) {
			logger.error("checkDocumentClassExists db error : {}", e);
		} finally {
            closeStatementAndResultSet(pstmt, resultSet);
            pool.closeConnection(connection);
		}
		return exists;
	}

	@Override
	public Error validateAdditionalProperties(String document_class, Map<String, String> addt_props) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Error error = null;
		try {

			String query = " select * from arkheion.document_class_prop where document_class_id = "
					+ "(select id FROM arkheion.document_class where name = ? ) ";//

			connection = pool.getConnection();

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, document_class);

			resultSet = pstmt.executeQuery();
			HashMap<String, String> addt_props_filtered = new HashMap<>();
			while (resultSet.next()) {
				if(addt_props.get(resultSet.getString("name"))==null){
					error = new Error(ErrorType.MissingField, "Missing "+resultSet.getString("name")+" propery.");
					break;
				}else{
					addt_props_filtered.put(resultSet.getString("name"), addt_props.get(resultSet.getString("name")));
				}
			}


		} catch (Exception e) {
			logger.error("validateAdditionalProperties db error : {}", e);
		} finally {
            closeStatementAndResultSet(pstmt, resultSet);
            pool.closeConnection(connection);
		}
		return error;
	}

	@Override
	public Profile getProfileByHashkey(String hashkey) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		Profile profile = null;
		try {
			String query = " select * from arkheion.profile where hashkey = ? ";
			connection = pool.getConnection();

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, hashkey);

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				profile = new Profile();
				profile.setId(resultSet.getInt("id"));
				profile.setHashkey(resultSet.getString("hashkey"));
				profile.setName(resultSet.getString("name"));
				profile.setCreate_date(resultSet.getObject("create_date", LocalDateTime.class));
				profile.setStatus(resultSet.getBoolean("status"));
			}


		} catch (Exception e) {
			logger.error("getProfileByHashkey db error : {}", e);
		} finally {
            closeStatementAndResultSet(pstmt, resultSet);
            pool.closeConnection(connection);
		}
		return profile;
	}

	@Override
	public FileModel getQueueRecord(String refcode, int profileId) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		FileModel fileModel = null;
		try {
			String query = " select * from arkheion.queue where ref_code = ? and profile_id = ? ";
			connection = pool.getConnection();
			logger.warn("query {} {} {}", query , refcode , profileId);

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, refcode);
			pstmt.setInt(2, profileId);
			
			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				fileModel = new FileModel();
				fileModel.setId(resultSet.getInt("id"));
				fileModel.setChannel_id(resultSet.getInt("channel_id"));
				fileModel.setRef_code(resultSet.getString("ref_code"));
				fileModel.setProfile_id(resultSet.getInt("profile_id"));
				fileModel.setStatus(resultSet.getBoolean("status"));
				fileModel.setCreate_date(resultSet.getObject("create_date", LocalDateTime.class));
				fileModel.setProces_date(resultSet.getObject("process_date", LocalDateTime.class));
				fileModel.setTopic_id(Priority.getById(resultSet.getInt("topic_id")));
				fileModel.setDocument_class(resultSet.getString("document_class"));
				fileModel.setMimetype(resultSet.getString("mimetype"));
				fileModel.setPath(resultSet.getString("path"));
				fileModel.setChannel_ref_code(resultSet.getString("channel_ref_code"));
				fileModel.setAddt_props(gson.fromJson(resultSet.getString("addt_props"),mapType));
				fileModel.setFilename(resultSet.getString("filename"));
				fileModel.setLocalStoreFileName(resultSet.getString("localStoreFileName"));
			}


		} catch (Exception e) {
			logger.error("getQueueRecord db error : {}", e);
		} finally {
            closeStatementAndResultSet(pstmt, resultSet);
            pool.closeConnection(connection);
		}
		return fileModel;
	}

	@Override
	public FileModel getQueueRecordById(String id) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		FileModel fileModel = null;
		try {
			String query = " select * from arkheion.queue where id = ? and status = 0 ";
			connection = pool.getConnection();
			// logger.warn("query {}", query);

			pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, Integer.valueOf(id));
			
			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				fileModel = new FileModel();
				fileModel.setId(resultSet.getInt("id"));
				fileModel.setChannel_id(resultSet.getInt("channel_id"));
				fileModel.setRef_code(resultSet.getString("ref_code"));
				fileModel.setProfile_id(resultSet.getInt("profile_id"));
				fileModel.setStatus(resultSet.getBoolean("status"));
				fileModel.setCreate_date(resultSet.getObject("create_date", LocalDateTime.class));
				fileModel.setProces_date(resultSet.getObject("process_date", LocalDateTime.class));
				fileModel.setTopic_id(Priority.getById(resultSet.getInt("topic_id")));
				fileModel.setDocument_class(resultSet.getString("document_class"));
				fileModel.setMimetype(resultSet.getString("mimetype"));
				fileModel.setPath(resultSet.getString("path"));
				fileModel.setChannel_ref_code(resultSet.getString("channel_ref_code"));
				fileModel.setAddt_props(gson.fromJson(resultSet.getString("addt_props"),mapType));
				fileModel.setFilename(resultSet.getString("filename"));
				fileModel.setLocalStoreFileName(resultSet.getString("localStoreFileName"));
			}


		} catch (Exception e) {
			logger.error("getQueueRecordById db error : {}", e);
		} finally {
            closeStatementAndResultSet(pstmt, resultSet);

            pool.closeConnection(connection);
		}
		return fileModel;
	}

    @Override
	public boolean updateQueueRecordProcessDateById(LocalDateTime processDate, boolean status, int id, String channel_ref_code, String errorMessage) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		int affectedRows = 0;
		try {

			connection = pool.getConnection();

			String query = " update arkheion.queue set process_date = ? , status = ? , channel_ref_code = ? , errorMessage = ? where id = ? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setTimestamp(1, Timestamp.valueOf(processDate));
			pstmt.setBoolean(2, status);
			pstmt.setString(3, channel_ref_code);
			pstmt.setString(4, errorMessage);
			pstmt.setInt(5, id);
			affectedRows = pstmt.executeUpdate();

		} catch (Exception e) {
            logger.error("updateQueueRecordProcessDateById db error : {}", e);
		} finally {
			try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                logger.error("updateQueueRecordProcessDateById db error : {}", e);
			}
			pool.closeConnection(connection);
		}
		return affectedRows != 0 ? true : false;
	}


    private void closeStatementAndResultSet(PreparedStatement pstmt, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            logger.error("closeStatementAndResultSet db error : {}", e);
        }
    }



}
