package com.arkheion.app.rabbitmq;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.arkheion.app.dao.IFileArchiverDAO;
import com.arkheion.app.model.CustomMessage;
import com.arkheion.app.model.FileModel;
import com.arkheion.app.model.FileProperty;
import com.arkheion.app.model.FilePropertyType;
import com.arkheion.app.strategy.IFileDeleteStrategy;
import com.arkheion.app.strategy.IFileReadStrategy;
import com.arkheion.app.strategy.IFileWriteStrategy;
import com.google.gson.Gson;

@Component("RabbitMQFileConsumer")
public class RabbitMQFileConsumer {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "FileArchiverJdbcDOA")
	private IFileArchiverDAO fileArchiverJdbcDOA;

	@Resource(name = "FilenetFileWriteStrategy")
	private IFileWriteStrategy filenetFileWriteStrategy;

	@Resource(name = "FilenetFileReadStrategy")
	private IFileReadStrategy filenetFileReadStrategy;

	@Resource(name = "UnixSystemFileReadStrategy")
	private IFileReadStrategy unixSystemFileReadStrategy;

	@Resource(name = "UnixSystemFileDeleteStrategy")
	private IFileDeleteStrategy unixSystemFileDeleteStrategy;

	@Resource(name = "gson")
	private Gson gson;

	@RabbitListener(queues = "GENERAL")
	public void receiveMessageGENERAL(final CustomMessage message) {

		logger.info("Received message on topic {} ", message);
		handleListener(message);
	}
	
	@RabbitListener(queues = "LEVEL1")
	public void receiveMessageLEVEL1(final CustomMessage message) {

		logger.info("Received message on topic {} ", message);
		handleListener(message);
	}
	
	@RabbitListener(queues = "LEVEL2")
	public void receiveMessageLEVEL2(final CustomMessage message) {

		logger.info("Received message on topic {} ", message);
		handleListener(message);
	}
	
	@RabbitListener(queues = "LEVEL3")
	public void receiveMessageLEVEL3(final CustomMessage message) {

		logger.info("Received message on topic {} ", message);
		handleListener(message);
	}
	
	@RabbitListener(queues = "LEVEL4")
	public void receiveMessageLEVEL4(final CustomMessage message) {

		logger.info("Received message on topic {} ", message);
		handleListener(message);
	}
	
	@RabbitListener(queues = "LEVEL5")
	public void receiveMessageLEVEL5(final CustomMessage message) {

		logger.info("Received message on topic {} ", message);
		handleListener(message);
	}
	
	@RabbitListener(queues = "TEST")
	public void receiveMessageTEST(final CustomMessage message) {

		logger.info("Received message on topic {} ", message);
		handleListener(message);
	}

	public void handleListener(final CustomMessage message) {
		FileModel fileModel = fileArchiverJdbcDOA.getQueueRecordById(message.getId() + "");
		if (fileModel != null) {
			boolean success = false;
			List<FileModel> files = unixSystemFileReadStrategy.readFile(fileModel.getLocalStoreFileName(), "", null,
					fileModel.getDocument_class());
			fileModel = files != null && files.size() > 0 ? files.get(0) : null;

			try {

				if (fileModel != null && fileModel.getData() != null) {
					ArrayList<HashMap<String, FileProperty>> addt_list = new ArrayList<>();
					addt_list.add(new HashMap<>());
					FileProperty fileProperty = new FileProperty();
					fileProperty.setType(FilePropertyType.String);
					fileProperty.setValue(fileModel.getFilename());
					addt_list.get(0).put("DocumentTitle",fileProperty);
					
					logger.info("readFile name {} :: addt_list :: {} documentClass :: {}",fileModel.getFilename(),gson.toJson(addt_list),fileModel.getDocument_class());
					
					List<FileModel> files1 = filenetFileReadStrategy.readFile(null, "", addt_list,
							fileModel.getDocument_class());

					FileModel existingFileModel = files1 != null && files1.size() > 0 ? files1.get(0) : null;

					fileModel = filenetFileWriteStrategy.writeFile(fileModel, existingFileModel != null ? true : false);
					success = fileModel.getErrorMessage() == null ? true : false;

					if (fileModel.getChannel_ref_code() != null) {
						success = unixSystemFileDeleteStrategy.deleteFile(fileModel.getLocalStoreFileName(), "",
								fileModel.getAddt_props(), fileModel.getDocument_class());
					}
					if (success)
						logger.info("Successfully proccessed message on topic {}  ", message);
					else
						logger.info("Message proccessed failed on topic {} ", message);
				}

			} catch (Exception err) {
				logger.error("error messages on topic {} :: {}", message, err);
			}
			if (success) {
				fileModel.setProces_date(LocalDateTime.now());
				success = fileArchiverJdbcDOA.updateQueueRecordProcessDateById(LocalDateTime.now(), true,
						fileModel.getId(), fileModel.getChannel_ref_code(), fileModel.getErrorMessage());
			} else {
				success = fileArchiverJdbcDOA.updateQueueRecordProcessDateById(LocalDateTime.now(), false,
						fileModel.getId(), fileModel.getChannel_ref_code(), fileModel.getErrorMessage());
				logger.info("Proccess message error :: {} :: {} :: {}", false, fileModel.getId(),
						fileModel.getErrorMessage());
			}
		}
	}

}
