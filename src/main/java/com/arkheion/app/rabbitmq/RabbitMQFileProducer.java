package com.arkheion.app.rabbitmq;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arkheion.app.model.CustomMessage;
import com.arkheion.app.model.CustomMessagePostProcessor;
import com.arkheion.app.model.FileModel;
import com.arkheion.app.strategy.IFileWriteStrategy;
import com.arkheion.app.util.FileArchiverUtil;
import com.google.gson.Gson;

@Component("RabbitMQFileProducer")
public class RabbitMQFileProducer  {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final RabbitTemplate rabbitTemplate;
	
	private final MessagePostProcessor messagePostProcessor = new CustomMessagePostProcessor((long) 86400000);

    @Autowired
    public RabbitMQFileProducer(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
}
		
	@Resource(name = "UnixSystemFileWriteStrategy")
	private IFileWriteStrategy unixSystemFileWriteStrategy;
	
	@Resource(name = "FileArchiverUtil")
	private FileArchiverUtil fileArchiverUtil;
	
	@Resource(name = "gson")
	private Gson gson;
	
	public boolean produceFile(FileModel fileModel) throws Exception {
		CustomMessage customMessage = new CustomMessage();
		customMessage.setRef_code(fileModel.getRef_code());
		customMessage.setId(fileModel.getId());
		customMessage.setLocalStoreFileName(fileModel.getLocalStoreFileName());
//		rabbitTemplate.convertAndSend(ArchiveApplication.EXCHANGE_NAME, ArchiveApplication.ROUTING_KEY, customMessage,messagePostProcessor);
		rabbitTemplate.convertAndSend(fileModel.getTopic_id().toString(), customMessage,messagePostProcessor);
		logger.info("file added to kafka queue :: {} :: {} :: {}",fileModel.getFilename(),fileModel.getLocalStoreFileName(), fileModel.getTopic_id().toString());
		
		return true;
	}

}
