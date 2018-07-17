package com.cloudeggtech.granite.stream.standard.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDecoder extends CumulativeProtocolDecoder {
	private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
	
	private static final String GRANITE_DECODER_MESSAGE_PARSER = "granite.decoder.message.parser";

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		IMessageParser messageParser = (IMessageParser)session.getAttribute(GRANITE_DECODER_MESSAGE_PARSER);
		if (messageParser == null) {
			messageParser = new MessageParser();
			session.setAttribute(GRANITE_DECODER_MESSAGE_PARSER, messageParser);
		}
		
		String[] messages;
		try {
			messages = messageParser.parse(in);
		} catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.debug("Decode error.", e);
			
			throw e;
		}
		
		if (messages != null) {
			for (String message : messages) {
				if (logger.isTraceEnabled())
					logger.trace("Message decoded: {}.", message);
				
				out.write(message);
			}
		}
		
		return !in.hasRemaining();
	}
}
