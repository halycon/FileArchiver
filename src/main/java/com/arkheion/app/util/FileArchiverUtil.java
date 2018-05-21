package com.arkheion.app.util;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component("FileArchiverUtil")
public class FileArchiverUtil implements Serializable {

	/**
	 * App utility class
	 */
	private static final long serialVersionUID = -8284000056644150548L;
	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
	
	public String generateSalt() {
	    String ts = String.valueOf(System.currentTimeMillis());
	    String rand = UUID.randomUUID().toString();
	    return DigestUtils.sha1Hex(ts + rand);
	}
}
