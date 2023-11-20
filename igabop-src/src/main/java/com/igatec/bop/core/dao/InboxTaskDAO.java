package com.igatec.bop.core.dao;

import com.igatec.bop.core.model.InboxTask;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.annotations.MatrixTransactional;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.common.dao.DBObjectDAO;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.*;

@Repository
@MatrixTransactional
@ObjectDAO("InboxTask")
public class InboxTaskDAO extends DBObjectDAO<InboxTask> {
    /**
     * Get all assigned to context user Inbox Tasks
     *
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> getMyTasks(Selector selects) {
        String assigneeSelect = String.format("from[%s].to.%s", RELATIONSHIP_PROJECT_TASK, SELECT_NAME);
        String where = String.format("'%s'=='%s'", assigneeSelect, getContext().getUser());
        List<DBObject> list = findObjects(TYPE_INBOX_TASK, VAULT_ESERVICE_PRODUCTION, where, selects);
        list = Util.sortByAttribute(list, ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
        Collections.reverse(list);
        return list;
    }

    /**
     * Get all active Route Templates
     *
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    public List<DBObject> getRouteTemplates(Selector selects) {
        String where = String.format("%s=='%s' && islast==TRUE", SELECT_CURRENT, STATE_ACTIVE);
        List<DBObject> list = findObjects(TYPE_ROUTE_TEMPLATE, VAULT_ESERVICE_PRODUCTION, where, selects);
        return list.stream().sorted(Comparator.comparing(o -> o.getBasicFieldName(SELECT_NAME))).collect(Collectors.toList());
    }
}
