package com.cloudeggtech.granite.lite.leps.im.traceable;

import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cloudeggtech.basalt.leps.im.message.traceable.MsgStatus;
import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.granite.framework.core.config.IConfiguration;
import com.cloudeggtech.granite.framework.core.config.IConfigurationAware;
import com.cloudeggtech.granite.framework.core.plumbing.persistent.IPersistentObjectFactory;
import com.cloudeggtech.granite.framework.core.plumbing.persistent.IPersistentObjectFactoryAware;
import com.cloudeggtech.granite.framework.plumbing.mybatis.PersistentObjectIterator;
import com.cloudeggtech.granite.leps.im.traceable.ITraceStore;
import com.cloudeggtech.granite.leps.im.traceable.MsgTrace;

@Transactional
@Component
public class TraceStore implements ITraceStore, IConfigurationAware, IPersistentObjectFactoryAware {
	private static final String CONFIGURATION_KEY_TRACES_FETCH_SIZE = "traces.fetch.size";
	private static final int DEFAULT_FETCH_SIZE = 20;
	
	private IPersistentObjectFactory persistentObjectFactory;
	private int tracesFetchSize = DEFAULT_FETCH_SIZE;
	
	@Autowired
	private SqlSession sqlSession;

	@Override
	public void save(JabberId jid, MsgStatus msgStatus) {
		getMapper().insert(createMsgTrace(jid, msgStatus));
	}

	private MsgTrace createMsgTrace(JabberId jid, MsgStatus msgStatus) {
		MsgTrace msgTrace = persistentObjectFactory.create(MsgTrace.class);
		msgTrace.setJid(jid);
		msgTrace.setMessageId(msgStatus.getId());
		msgTrace.setStatus(msgStatus.getStatus());
		msgTrace.setFrom(msgStatus.getFrom());
		msgTrace.setStamp(msgStatus.getStamp().getJavaDate());
		
		return msgTrace;
	}

	@Override
	public void save(JabberId jid, List<MsgStatus> msgStatuses) {
		for (MsgStatus msgStatus : msgStatuses) {
			getMapper().insert(createMsgTrace(jid, msgStatus));
		}
	}

	@Override
	public void remove(JabberId jid, String messageId) {
		getMapper().deleteByJidAndMessageId(jid, messageId);
	}

	@Override
	public void remove(JabberId jid, List<String> messageIds) {
		for (String messageId : messageIds) {
			getMapper().deleteByJidAndMessageId(jid, messageId);
		}
	}

	@Override
	public Iterator<MsgTrace> iterator(final JabberId jid) {
		return new PersistentObjectIterator<MsgTrace>(tracesFetchSize) {
			@Override
			protected List<MsgTrace> doFetch(int offset, int limit) {
				return getMapper().selectByJid(jid, limit, offset);
			}
		};
	}

	@Override
	public boolean isEmpty(JabberId jid) {
		return getMapper().selectCountByJid(jid) == 0;
	}

	@Override
	public int getSize(JabberId jid) {
		return getMapper().selectCountByJid(jid);
	}
	
	private TraceMapper getMapper() {
		return sqlSession.getMapper(TraceMapper.class);
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		tracesFetchSize = configuration.getInteger(CONFIGURATION_KEY_TRACES_FETCH_SIZE, DEFAULT_FETCH_SIZE);
	}

	@Override
	public void setPersistentObjectFactory(IPersistentObjectFactory persistentObjectFactory) {
		this.persistentObjectFactory = persistentObjectFactory;
	}

}
