package com.cloudeggtech.granite.stream.standard.codec;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudeggtech.basalt.protocol.Constants;
import com.cloudeggtech.basalt.protocol.oxm.OxmService;
import com.cloudeggtech.basalt.protocol.oxm.preprocessing.IProtocolPreprocessor;

public class MessageParser implements IMessageParser {

	private Logger logger = LoggerFactory.getLogger(MessageParser.class);
	
	private static String DEFAULT_CHARSET = Constants.DEFAULT_CHARSET;
	private static int DEFAULT_MAX_BUFFER_SIZE = 1024 * 1024;
	
	private Charset charset;
	
	private IProtocolPreprocessor preprocessor;
	
	public MessageParser() {
		this(DEFAULT_CHARSET);
	}
	
	public MessageParser(String sCharset) {
		this(sCharset, DEFAULT_MAX_BUFFER_SIZE);
	}
	
	public MessageParser(String sCharset, int maxBufferSize) {
		charset = Charset.forName(sCharset);
		preprocessor = OxmService.createProtocolPreprocessor();
		
	}
	
	@Override
	public synchronized String[] parse(IoBuffer in) throws Exception {
		CharBuffer charBuffer = charset.decode(in.buf());
		char[] bytes = charBuffer.array();
		int readBytes = charBuffer.remaining();
		
		if (readBytes == 0) {
			return preprocessor.getDocuments();
		}
		
		char lastChar = bytes[readBytes - 1];
		if (lastChar >= 0xfff0) {
			logger.debug("Waiting to get complete char: {}.", String.valueOf(bytes));
			
			in.position(in.position() - 1);
			readBytes--;
			
			if (readBytes == 0) {
				return preprocessor.getDocuments();
			}
		}
		
		if (logger.isTraceEnabled())
			logger.trace("Message decoding: {}.", new String(bytes, 0, readBytes));
		
		return preprocessor.parse(bytes, readBytes);
	}

	@Override
	public void setMaxBufferSize(int maxBufferSize) {
		preprocessor.setMaxBufferSize(maxBufferSize);
	}

	@Override
	public int getMaxBufferSize() {
		return preprocessor.getMaxBufferSize();
	}

}
