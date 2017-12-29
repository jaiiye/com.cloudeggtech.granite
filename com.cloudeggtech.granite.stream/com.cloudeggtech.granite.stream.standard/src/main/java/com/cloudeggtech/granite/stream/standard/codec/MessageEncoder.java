package com.cloudeggtech.granite.stream.standard.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.cloudeggtech.basalt.protocol.Constants;

public class MessageEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		if (message instanceof String) {
			out.write(IoBuffer.wrap(((String)message).getBytes(Constants.DEFAULT_CHARSET)));
		}
	}

}
