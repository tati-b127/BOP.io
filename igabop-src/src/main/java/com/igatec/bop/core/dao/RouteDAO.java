package com.igatec.bop.core.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igatec.bop.core.model.Route;
import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.annotations.MatrixTransactional;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.igatec.utilsspring.utils.common.core.StandardSelectors;
import com.igatec.utilsspring.utils.common.dao.DBObjectDAO;
import com.igatec.utilsspring.utils.common.db.Owner;
import com.igatec.utilsspring.utils.services.ObjectDAO;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.common.util.ComponentsUIUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import matrix.db.Context;
import matrix.db.Policy;
import matrix.db.RelationshipType;
import matrix.util.StringList;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.*;
import static com.matrixone.apps.domain.DomainConstants.ATTRIBUTE_TITLE;
import static com.matrixone.apps.domain.DomainConstants.SELECT_ATTRIBUTE_TITLE;

@Repository
@ObjectDAO("Route")
@Slf4j
public class RouteDAO extends DBObjectDAO<Route> {
    //Params
    private static final String PARAM_TASK = "task";
    private static final String PARAM_RT = "rt";
    private static final String PARAM_PERSON = "person";
    private static final String PARAM_START = "start";
    private static final String PARAM_OWNERSHIP = "ownership";
    private static final String PARAM_VALUE = "value";
    private static final String PARAM_ROUTE = "route";
    private static final String START_IMMEDIATELY = "immediately";
    private static final String START_MANUALLY = "manually";
    private static final String OWNERSHIP_YES = "yes";
    private static final String OWNERSHIP_NO = "no";

    private static final String ATTRIBUTE_OPEN_BRACE = "attribute[";
    private static final String TEMPLATE_TASK = "Template Task";
    private static final String ATTRIBUTE_IGA_DRW_APPROVER = "IGADrwApprover";

    /**
     * Add Route content
     *
     * @param selects Comma separated list with object selects
     * @param pBody   contains client info required to content adding (selected Route and objects as content)
     */
    @SneakyThrows
    @MatrixTransactional
    public List<DBObject> addContent(String pBody, Selector selects) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> params = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        List<Map> selected = (List<Map>) params.get(PARAM_SELECTED);
        Set<String> ids = new HashSet<>();
        HashMap<String, String> idToState = new HashMap<>();
        selected.forEach(map -> {
            String pid = (String) map.get(PARAM_PHYSICALID);
            Map stateMap = (Map) map.get(PARAM_CURRENT);
            String state = (String) stateMap.get(PARAM_NAME);
            idToState.put(pid, state);
            ids.add(pid);
        });

        Map routeMap = (Map) params.get(PARAM_ROUTE);
        String routePID = (String) routeMap.get(PARAM_PHYSICALID);

        Context context = getContext();
        com.matrixone.apps.common.Route route = new com.matrixone.apps.common.Route(routePID);
        DomainRelationship.connect(context, route, RELATIONSHIP_OBJECT_ROUTE, false, ids.toArray(new String[]{}));

        return Util.extendDataPro(getContext(), new ArrayList<>(ids), null, null, selects);
    }

    /**
     * Create Route
     *
     * @param pBody   contains client info required to creation (Route Template id, my Inbox Task id and assignee)
     * @param selects Comma separated list with object selects
     */
    @SneakyThrows
    @MatrixTransactional
    public List<DBObject> createRoute(String pBody, Selector selects) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(pBody);
        Map<String, Object> params = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {
        });

        List<Map> selected = (List<Map>) params.get(PARAM_SELECTED);
        Map taskMap = (Map) params.get(PARAM_TASK);
        Map rt = (Map) params.get(PARAM_RT);
        Map personMap = (Map) params.get(PARAM_PERSON);
        Map startMap = (Map) params.get(PARAM_START);
        Map ownershipMap = (Map) params.get(PARAM_OWNERSHIP);
        //Map current = (Map) selected.get(PARAM_CURRENT);

        //for response calculate ids tobe potentially processed
        List<String> ids = selected.stream().map(m -> (String) m.get(SELECT_PHYSICALID)).collect(Collectors.toList());

        //define title
        List<Map> pprs = selected.stream().filter(m -> TYPE_PPR_CONTEXT.equals(m.get(PARAM_TYPE_ID))).collect(Collectors.toList());
        String title = null;
        if (pprs.size() == 0) {
            List<Map> mboms = selected.stream().filter(m -> TYPES_MBOM.contains(m.get(PARAM_TYPE_ID))).collect(Collectors.toList());
            if (mboms.size() == 1)
                title = (String) mboms.get(0).get(PARAM_TITLE);
            else {
                List<Map> eboms = selected.stream().filter(m -> TYPES_EBOM.contains(m.get(PARAM_TYPE_ID))).collect(Collectors.toList());
                if (eboms.size() == 1)
                    title = (String) eboms.get(0).get(PARAM_TITLE);
            }
        } else
            title = (String) pprs.get(0).get(PARAM_TITLE);

        Context context = getContext();
        String strObjectId = (String) selected.get(0).get(SELECT_PHYSICALID); //object id of Route content object
        // Start the route if this is the current state of the object
        //String strState = (String) current.get(PARAM_NAME); //state name
        String strRouteTemplateId = (String) rt.get(PARAM_VALUE); //route template id
        String routeId = createRouteFromTemplate(context, strObjectId, null, strRouteTemplateId, true);

        //mark route as BOP Route for automation issue
        MqlUtil.mqlCommand(context, "mod bus $1 add interface $2", routeId, INTERFACE_IGABOP_ROUTE);

        //add selected object to Route except for first one
        //selected.remove(0);//already connected in emxLifecycle.createRouteFromTemplate
        List<String> idsToConnect = ids.stream().filter(id -> !strObjectId.equals(id)).collect(Collectors.toList());
        com.matrixone.apps.common.Route route = new com.matrixone.apps.common.Route(routeId);
        DomainRelationship.connect(context, route, RELATIONSHIP_OBJECT_ROUTE, false, idsToConnect.toArray(new String[]{}));

        //extend Route name and Route node Titles by PPRContext (or MBOM) title
        if (title != null) {
            String titleForRoute = title.replaceAll("[#$@%*,?<>|:'.]", " ");

            String routeNewName = String.format("%s %s", route.getInfo(context, SELECT_NAME), titleForRoute);
            MqlUtil.mqlCommand(context, "mod bus $1 name '$2'", routeId, routeNewName);
        }

        //assign selected Person to the first task
        String personName = (String) personMap.get(PARAM_VALUE);
        Person person = Person.getPerson(context, personName);
        String personPID = person.getId(context);

        StringList relSelects = new StringList();
        relSelects.add(SELECT_PHYSICALID);
        relSelects.add(SELECT_ATTRIBUTE_TITLE);
        relSelects.add(SELECT_ATTRIBUTE_ROUTE_SEQUENCE);
        MapList ml = route.getRelatedObjects(context, DomainConstants.RELATIONSHIP_ROUTE_NODE, QUERY_WILDCARD, new StringList(), relSelects,
                false, true, (short) 1, null, null, 0);
        for (Object o : ml) {
            Map map = (Map) o;
            String routeNodePID = (String) map.get(SELECT_PHYSICALID);

            //only for first tasks
            String routeNodeSequence = (String) map.get(SELECT_ATTRIBUTE_ROUTE_SEQUENCE);
            if ("1".equals(routeNodeSequence))
                MqlUtil.mqlCommand(context, "mod connection $1 to $2", routeNodePID, personPID);

            //change title
            if (title != null) {
                String routeNodeTitle = (String) map.get(SELECT_ATTRIBUTE_TITLE);
                String routeNodeNewTitle = String.format("%s %s", title, routeNodeTitle);
                MqlUtil.mqlCommand(context, "mod connection $1 '$2' '$3'", routeNodePID, ATTRIBUTE_TITLE, routeNodeNewTitle);
            }
        }

        //set Route Completion Action  attribute values as Promote Connected Object
        route.setAttributeValue(context, ATTRIBUTE_ROUTE_COMPLETION_ACTION, RANGE_PROMOTE_CONNECTED_OBJECT);

        //optionally change Route owner by selected Person
        String ownership = (String) ownershipMap.get(PARAM_VALUE);
        if (OWNERSHIP_YES.equals(ownership))
            route.setOwner(context, personName);

        //Transfer ownership, unreserve and reserve
        List<Map> unReleased = selected.stream().filter(m -> {
            Map mCurrent = (Map) m.get(PARAM_CURRENT);
            String current = (String) mCurrent.get(PARAM_NAME);
            return !STATE_RELEASED.equals(current);
        }).collect(Collectors.toList());
        List<String> unReleasedIds = unReleased.stream().map(m -> (String) m.get(SELECT_PHYSICALID)).collect(Collectors.toList());
        ContextUtil.pushContext(context);
        for (String id : unReleasedIds) {
            MqlUtil.mqlCommand(context, "mod bus $1 owner '$2'", id, personName);
            MqlUtil.mqlCommand(context, "mod bus $1 reserve user '$2'", id, personName);
        }
        ContextUtil.popContext(context);

        //add route as sub route to my task
        if (taskMap != null && !taskMap.isEmpty()) {
            String sTaskPID = (String) taskMap.get(PARAM_VALUE);
            MqlUtil.mqlCommand(context, "add connection '$1' from '$2' to '$3'",
                    DomainConstants.RELATIONSHIP_TASK_SUBROUTE, sTaskPID, routeId);
        }

        //start route if start option is 'immediately'
        String start = (String) startMap.get(PARAM_VALUE);
        if (START_IMMEDIATELY.equals(start)) {
            ContextUtil.pushContext(context);
            route.promote(context);
            ContextUtil.popContext(context);
        }

        return Util.extendDataPro(context, ids, null, null, selects);
    }

    /**
     * Copy from emxLifecycle.createRouteFromTemplate method
     */
    private String createRouteFromTemplate(Context context, String contextObjectId, String contextObjectState, String routeTemplateId, boolean forceTemplate) throws Exception {
        try {
            //int nReturnCode = 0;
            String strLanguage = context.getSession().getLanguage();
            i18nNow loc = new i18nNow();
            DomainObject domRouteTemplateObject = new DomainObject(routeTemplateId);

            final String RESOURCE_BUNDLE = "emxFrameworkStringResource";
            final String ROUTE_FINISHED = "Finished";
            final String SELECT_ROUTE_TASK_ASSIGNEE_TYPE = "from[" + DomainObject.RELATIONSHIP_PROJECT_TASK + "].to.type";
            final String SELECT_ATTRIBUTE_ROUTE_STATUS = ATTRIBUTE_OPEN_BRACE + DomainObject.ATTRIBUTE_ROUTE_STATUS + "]";
            final String ATTRIBUTE_CURRENT_ROUTE_NODE = "Current Route Node";
            final String SELECT_ATTRIBUTE_CURRENT_ROUTE_NODE = ATTRIBUTE_OPEN_BRACE + ATTRIBUTE_CURRENT_ROUTE_NODE + "]";
            final String SELECT_REL_ATTRIBUTE_ROUTE_SEQUENCE = DomainRelationship.getAttributeSelect(DomainObject.ATTRIBUTE_ROUTE_SEQUENCE);
            final String ATTRIBUTE_AUTO_STOP_ON_REJECTION = "Auto Stop On Rejection";
            final String ATTRIBUTE_ROUTE_BASE_PURPOSE = "Route Base Purpose";
            final String SELECT_ATTRIBUTE_AUTO_STOP_REJECTION = ATTRIBUTE_OPEN_BRACE + ATTRIBUTE_AUTO_STOP_ON_REJECTION + "]";
            final String SELECT_ATTRIBUTE_ROUTE_BASE_PURPOSE = ATTRIBUTE_OPEN_BRACE + ATTRIBUTE_ROUTE_BASE_PURPOSE + "]";
            final String COMPLETED_ROUTE = loc.GetString(RESOURCE_BUNDLE, strLanguage, "emxFramework.Alert.CannotAddTaskToCompletedRoute");
            String sAttrRestrictMembers = "Restrict Members";
            final String SELECT_ATTRIBUTE_ROUTE_SCOPE = ATTRIBUTE_OPEN_BRACE + sAttrRestrictMembers + "]";
            String sAttrRoutePreserveTaskOwner = "Preserve Task Owner";
            final String SELECT_ROUTE_PRESERVE_TASK_OWNER = DomainObject.getAttributeSelect(sAttrRoutePreserveTaskOwner);

//          For Bug 347000
            final String STRING_APPROVER_CANNOT_BE_ADDED_INVALID_ROUTE_TEMPLATE = loc.GetString(RESOURCE_BUNDLE, strLanguage, "emxFramework.Lifecycle.ApproversCannotBeAddedInvalidRouteTemplate");

            Map mapRelAttributesNew = new HashMap();

            StringList slBusSelect = new StringList();
            slBusSelect.add(DomainObject.SELECT_POLICY);
            slBusSelect.add(DomainObject.SELECT_CURRENT);
            slBusSelect.add("current.access[promote]");

            DomainObject dmoParentObject = new DomainObject(contextObjectId);
            Map mapObjectInfo = dmoParentObject.getInfo(context, slBusSelect);

            String strParentObjectPolicy = (String) mapObjectInfo.get(DomainObject.SELECT_POLICY);
            String hasPromoteAccess = (String) mapObjectInfo.get("current.access[promote]");

            String strSymbolicObjectPolicyName = FrameworkUtil.getAliasForAdmin(context, "Policy", strParentObjectPolicy, false);
            String strSymbolicParentObjectState = FrameworkUtil.reverseLookupStateName(context, strParentObjectPolicy, contextObjectState);
            String RELATIONSHIP_INITIATING_ROUTE_TEMPLATE = "Initiating Route Template";

            String proxyGoupType = "Group Proxy";
            DomainRelationship dmoRelationship = null;
            Map mapRelAttributes = new HashMap();
            HashMap mapObjectToState = new HashMap();           // used while adding this object as content to the route
            com.matrixone.apps.common.Route route = (com.matrixone.apps.common.Route) DomainObject.newInstance(context, DomainConstants.TYPE_ROUTE);
            String strRouteId = null;
            boolean isRouteToBeStarted = false;
            boolean isTaskOnCurrentLevelToBeStarted = false;
            boolean isTaskOnCurrentLevelToBeStartedForStoppedRoute = false;

            // Find all Routes  connected to the parent state
            String strRelPattern = DomainObject.RELATIONSHIP_OBJECT_ROUTE;
            String strTypePattern = DomainObject.TYPE_ROUTE;
            StringList slRelSelect = new StringList(DomainRelationship.ATTRIBUTE_ROUTE_SEQUENCE);
            short nRecurseToLevel = (short) 1;

            slBusSelect = new StringList();
            slBusSelect.add(DomainObject.SELECT_ID);
            slBusSelect.add(SELECT_ROUTE_TASK_ASSIGNEE_TYPE);
            slBusSelect.add(SELECT_ATTRIBUTE_ROUTE_STATUS);
            slBusSelect.add(SELECT_ATTRIBUTE_CURRENT_ROUTE_NODE);

            String strRelWhere = ATTRIBUTE_OPEN_BRACE + DomainObject.ATTRIBUTE_ROUTE_BASE_POLICY + "]=='" + strSymbolicObjectPolicyName + "' && attribute[" + DomainObject.ATTRIBUTE_ROUTE_BASE_STATE + "]=='" + strSymbolicParentObjectState + "'";
            String strObjectWhere = "";
            String strRouteWhere = "";
            if (forceTemplate) {
                strRouteWhere = "from[" + RELATIONSHIP_INITIATING_ROUTE_TEMPLATE + "].to.id == " + routeTemplateId;
                strRouteWhere += "&& current != Complete && current != Archive";
            }
            final boolean GET_TO = true;
            final boolean GET_FROM = true;

            MapList mlRoutes = dmoParentObject.getRelatedObjects(context,
                    strRelPattern,
                    strTypePattern,
                    slBusSelect,
                    slRelSelect,
                    !GET_TO,
                    GET_FROM,
                    nRecurseToLevel,
                    strRouteWhere,
                    strRelWhere);

            if (mlRoutes.size() == 0) {
                //Create new Route
                com.matrixone.apps.common.Person objPerson = com.matrixone.apps.common.Person.getPerson(context);
                route = (com.matrixone.apps.common.Route) DomainObject.newInstance(context, DomainConstants.TYPE_ROUTE);
                String strRouteName = null;

                // Create route object
                strRouteName = FrameworkUtil.autoName(context,
                        "type_Route",
                        new Policy(DomainObject.POLICY_ROUTE).getFirstInSequence(context),
                        "policy_Route",
                        null,
                        null,
                        true,
                        true);
                route.createObject(context, DomainConstants.TYPE_ROUTE, strRouteName, null, DomainObject.POLICY_ROUTE, null);
                strRouteId = route.getId();

                // Connect route to the owner
                route.connect(context, new RelationshipType(DomainObject.RELATIONSHIP_PROJECT_ROUTE), true, objPerson);

                // Connect route to the route template
                route.connectTemplate(context, routeTemplateId);

                //Getting the current auto stop on rejection attribute for the ROUTE
                StringList selects = new StringList();
                selects.add(SELECT_ATTRIBUTE_AUTO_STOP_REJECTION);
                selects.add(SELECT_ATTRIBUTE_ROUTE_BASE_PURPOSE);
                selects.add(SELECT_ATTRIBUTE_ROUTE_SCOPE);
                selects.add(SELECT_ROUTE_PRESERVE_TASK_OWNER);

                selects.add(DomainObject.SELECT_DESCRIPTION);

                Map routeTemplateMap = domRouteTemplateObject.getInfo(context, selects);


                String sAutoStopOnRejection = (String) routeTemplateMap.get(SELECT_ATTRIBUTE_AUTO_STOP_REJECTION);
                String sRouteBasePurpose = (String) routeTemplateMap.get(SELECT_ATTRIBUTE_ROUTE_BASE_PURPOSE);
                String sRouteScope = (String) routeTemplateMap.get(SELECT_ATTRIBUTE_ROUTE_SCOPE);
                String sRoutePreserveTask = (String) routeTemplateMap.get(SELECT_ROUTE_PRESERVE_TASK_OWNER);
                String sRouteDescription = (String) routeTemplateMap.get(DomainObject.SELECT_DESCRIPTION);

                // Find all the attributes on Route Node relationship and form select list
                mapRelAttributes = DomainRelationship.getTypeAttributes(context, DomainObject.RELATIONSHIP_ROUTE_NODE);
                mapRelAttributes.put(ATTRIBUTE_IGA_DRW_APPROVER, new HashMap<>());
                StringList slRelSelects = new StringList();
                for (Iterator itrAttributes = mapRelAttributes.keySet().iterator(); itrAttributes.hasNext(); ) {
                    slRelSelects.add(DomainRelationship.getAttributeSelect((String) itrAttributes.next()));
                }//for

                // Form the bus select
                String strKindOfProxyGroup = "type.kindof[" + proxyGoupType + "]";
                StringList slBusSelects = new StringList(DomainObject.SELECT_ID);
                slBusSelects.add(DomainObject.SELECT_TYPE);
                slBusSelects.add(strKindOfProxyGroup);

                // Expand and find the route template tasks, users
                DomainObject dmoRouteTemplate = new DomainObject(routeTemplateId);

                strTypePattern = DomainObject.TYPE_PERSON + "," + DomainObject.TYPE_ROUTE_TASK_USER + "," + proxyGoupType;
                strRelPattern = DomainObject.RELATIONSHIP_ROUTE_NODE;
                boolean getTo = false;
                boolean getFrom = true;
                strRelWhere = null;

                MapList mlTasks = dmoRouteTemplate.getRelatedObjects(context,
                        strRelPattern,
                        strTypePattern,
                        slBusSelects,
                        slRelSelects,
                        getTo,
                        getFrom,
                        nRecurseToLevel,
                        strObjectWhere,
                        strRelWhere);

                Map mapTask = null;
                dmoRelationship = null;
                DomainObject toObject = null;
                String strUserId = null;
                String strUserType = null;
                String isKindOfProxyGroup = null;
                String strAttributeName = null;
                String strAttributeValue = null;
                String strRouteTaskUser = null;
                String strTitle = null;
                Map mapRelAttributesToSet = null;

                // We could have found all the user objects and connect them simultaneously.
                // But there might be multiple tasks to a same user. In this scenario, when all the users are connected,
                // we will not know for which relationship attributes are to be updated.
                // So find one user (i.e. task) and complete it, then go for next user (i.e. task).
                for (Iterator itrTasks = mlTasks.iterator(); itrTasks.hasNext(); ) {

                    // Get each task in route template
                    mapTask = (Map) itrTasks.next();

                    // Create the same tasks for route object
                    strUserId = (String) mapTask.get(DomainObject.SELECT_ID);
                    strUserType = (String) mapTask.get(DomainObject.SELECT_TYPE);
                    isKindOfProxyGroup = (String) mapTask.get(strKindOfProxyGroup);
                    // Bug 347000 : Check if the assignee is present and task title is provided
                    strRouteTaskUser = (String) mapTask.get(DomainRelationship.getAttributeSelect(DomainObject.ATTRIBUTE_ROUTE_TASK_USER));
                    strTitle = (String) mapTask.get(DomainRelationship.getAttributeSelect(DomainObject.ATTRIBUTE_TITLE));
                    if ((DomainObject.TYPE_ROUTE_TASK_USER).equals(strUserType)) {
                        if (strRouteTaskUser == null || "".equals(strRouteTaskUser.trim())) {
                            throw new Exception(STRING_APPROVER_CANNOT_BE_ADDED_INVALID_ROUTE_TEMPLATE);
                        }
                    }
                    if (strTitle == null || "".equals(strTitle.trim())) {
                        throw new Exception(STRING_APPROVER_CANNOT_BE_ADDED_INVALID_ROUTE_TEMPLATE);
                    }

                    //If the task is of RTU type then create new RTU
                    if ((DomainObject.TYPE_ROUTE_TASK_USER).equals(strUserType)) {
                        toObject = DomainObject.newInstance(context);
                        toObject.createObject(context, DomainObject.TYPE_ROUTE_TASK_USER, null, null, null, null);
                    } else {
                        //Normal task
                        toObject = new DomainObject(strUserId);
                    }

                    dmoRelationship = DomainRelationship.connect(context, route, DomainObject.RELATIONSHIP_ROUTE_NODE, toObject);

                    // Copy all the attributes from route template Route Node relationship to Route
                    mapRelAttributesToSet = new HashMap();
                    for (Iterator itrAttributes = mapRelAttributes.keySet().iterator(); itrAttributes.hasNext(); ) {
                        strAttributeName = (String) itrAttributes.next();
                        strAttributeValue = (String) mapTask.get(DomainRelationship.getAttributeSelect(strAttributeName));
                        mapRelAttributesToSet.put(strAttributeName, strAttributeValue);
                    }//for
                    mapRelAttributesToSet.put(TEMPLATE_TASK, "Yes");
                    MqlUtil.mqlCommand(context, "mod connection $1 add interface $2", dmoRelationship.getName(), ATTRIBUTE_IGA_DRW_APPROVER);
                    dmoRelationship.setAttributeValues(context, mapRelAttributesToSet);
                }//for

                // Add this object as content to this route
                mapObjectToState.put(contextObjectId, contextObjectState);
                DomainObject domainObject = new DomainObject(contextObjectId);
                String strCurrentStateOfObject = domainObject.getInfo(context, DomainObject.SELECT_CURRENT);

                try {
                    ContextUtil.pushContext(context, "User Agent", null, context.getVault().getName());
                    dmoRelationship = DomainRelationship.connect(context, domainObject, DomainObject.RELATIONSHIP_OBJECT_ROUTE, route);

                    String strPolicyName = domainObject.getInfo(context, DomainObject.SELECT_POLICY);
                    String strStateNameSymbolic = FrameworkUtil.reverseLookupStateName(context, strPolicyName, contextObjectState);
                    String strPolicyNameSymbolic = FrameworkUtil.getAliasForAdmin(context, "Policy", strPolicyName, true);

                    mapRelAttributesNew = new HashMap();
                    mapRelAttributesNew.put(DomainObject.ATTRIBUTE_ROUTE_BASE_POLICY, strPolicyNameSymbolic);

                    if ("true".equalsIgnoreCase(hasPromoteAccess)) {
                        mapRelAttributesNew.put(DomainObject.ATTRIBUTE_ROUTE_BASE_STATE, strStateNameSymbolic);
                    } else {
                        mapRelAttributesNew.put(DomainObject.ATTRIBUTE_ROUTE_BASE_STATE, "Ad Hoc");
                    }
                    dmoRelationship.setAttributeValues(context, mapRelAttributesNew);
                } finally {
                    ContextUtil.popContext(context);
                }
                //Setting attribute Auto stop on rejection
                route.setAttributeValue(context, ATTRIBUTE_AUTO_STOP_ON_REJECTION, sAutoStopOnRejection);
                route.setAttributeValue(context, ATTRIBUTE_ROUTE_BASE_PURPOSE, sRouteBasePurpose);
                route.setAttributeValue(context, sAttrRestrictMembers, sRouteScope);
                route.setAttributeValue(context, sAttrRoutePreserveTaskOwner, sRoutePreserveTask);
                route.setDescription(context, sRouteDescription);


                // Start the route if this is the current state of the object
                if (strCurrentStateOfObject.equals(contextObjectState)) {
                    isRouteToBeStarted = true;
                }
            } else {
                //Route already exists  then use it.
                // Filter finished Routes
                Map mapTemp = null;
                MapList mlTemp = new MapList();

                for (Iterator itrActiveTasks = mlRoutes.iterator(); itrActiveTasks.hasNext(); ) {
                    mapTemp = (Map) itrActiveTasks.next();
                    if (!ROUTE_FINISHED.equals((String) mapTemp.get(SELECT_ATTRIBUTE_ROUTE_STATUS))) {
                        mlTemp.add(mapTemp);
                    }
                }

                mlRoutes = mlTemp;

                if (mlRoutes.size() == 0) {
                    //All routes are finished Routes
                    log.error(COMPLETED_ROUTE);
                    ContextUtil.abortTransaction(context);
                    return null;
                } else {
                    //Non Finished ROUTES processing
                    String strRouteStatus = null;
                    Map mapRouteInfo = null;
                    Map mapStartedRoute = null;
                    Map mapNotStartedRoute = null;
                    Map mapStoppededRoute = null;
                    for (Iterator itrActiveRoutes = mlRoutes.iterator(); itrActiveRoutes.hasNext(); ) {
                        mapRouteInfo = (Map) itrActiveRoutes.next();
                        strRouteStatus = (String) mapRouteInfo.get(SELECT_ATTRIBUTE_ROUTE_STATUS);
                        //Find if started status
                        if ("Started".equals(strRouteStatus)) {
                            mapStartedRoute = mapRouteInfo;
                        }
                        //Find if Not Started status
                        else if ("Not Started".equals(strRouteStatus)) {
                            mapNotStartedRoute = mapRouteInfo;
                        }
                        //Find if Stopped status
                        else if ("Stopped".equals(strRouteStatus)) {
                            mapStoppededRoute = mapRouteInfo;
                        }
                    }

                    //set Route to be used
                    Map mapRouteToBeUsed = null;
                    if (mapStartedRoute != null) {
                        mapRouteToBeUsed = mapStartedRoute;
                    } else if (mapStoppededRoute != null) {
                        mapRouteToBeUsed = mapStoppededRoute;
                    } else if (mapNotStartedRoute != null) {
                        mapRouteToBeUsed = mapNotStartedRoute;
                    }

                    if (mapRouteToBeUsed == null) {
                        String[] formatArgs = {contextObjectId, contextObjectState};
                        String message = ComponentsUIUtil.getI18NString(context, "emxComponents.LifeCycle.UsableStateBasedRouteNotFound", formatArgs);
                        throw new Exception(message);
                    }

                    //end of Route to be used setting

                    strRouteId = (String) mapRouteToBeUsed.get(DomainObject.SELECT_ID);
                    route = new com.matrixone.apps.common.Route(strRouteId);

                    // Find all the attributes on Route Node relationship and form select list
                    mapRelAttributes.clear();
                    mapRelAttributes = DomainRelationship.getTypeAttributes(context, DomainObject.RELATIONSHIP_ROUTE_NODE);
                    mapRelAttributes.put(ATTRIBUTE_IGA_DRW_APPROVER, new HashMap<>());
                    StringList slRelSelects = new StringList();

                    for (Iterator itrAttributes = mapRelAttributes.keySet().iterator(); itrAttributes.hasNext(); ) {
                        slRelSelects.add(DomainRelationship.getAttributeSelect((String) itrAttributes.next()));
                    }//for

                    //Getting the current sequence number for the ROUTE
                    String strCurrentRouteSequenceNo = route.getInfo(context, SELECT_ATTRIBUTE_CURRENT_ROUTE_NODE);

                    //Finding tasks related to ROUTE Template
                    slRelSelects.add(DomainRelationship.SELECT_ID);
                    slRelSelects.add(SELECT_REL_ATTRIBUTE_ROUTE_SEQUENCE);

                    StringList slBusSelects = new StringList(DomainObject.SELECT_ID);
                    slBusSelects.add(DomainObject.SELECT_TYPE);

                    DomainObject dmoRouteTemplate = new DomainObject(routeTemplateId);

                    strTypePattern = DomainObject.TYPE_PERSON + "," + DomainObject.TYPE_ROUTE_TASK_USER + "," + proxyGoupType;
                    strRelPattern = DomainObject.RELATIONSHIP_ROUTE_NODE;
                    boolean getTo = false;
                    boolean getFrom = true;
                    nRecurseToLevel = (short) 1;
                    strObjectWhere = null;
                    strRelWhere = null;

                    MapList mlTasks = dmoRouteTemplate.getRelatedObjects(context,
                            strRelPattern,
                            strTypePattern,
                            slBusSelects,
                            slRelSelects,
                            getTo,
                            getFrom,
                            nRecurseToLevel,
                            strObjectWhere,
                            strRelWhere);

                    Map mapTask = null;
                    dmoRelationship = null;
                    DomainObject fromObject = route;
                    DomainObject toObject = null;
                    String strUserId = null;
                    String strAttributeName = null;
                    String strAttributeValue = null;
                    Map mapRelAttributesToSet = null;
                    String strUserType = null;
                    String currTaskSeqStr = null;

                    //Iterating through each task
                    for (Iterator itrTasks = mlTasks.iterator(); itrTasks.hasNext(); ) {

                        mapTask = (Map) itrTasks.next();

                        strUserId = (String) mapTask.get(DomainObject.SELECT_ID);
                        strUserType = (String) mapTask.get(DomainObject.SELECT_TYPE);
                        currTaskSeqStr = (String) mapTask.get(SELECT_REL_ATTRIBUTE_ROUTE_SEQUENCE);

                        //If the task is of RTU type then create new RTU
                        if ((DomainObject.TYPE_ROUTE_TASK_USER).equals(strUserType)) {
                            toObject = DomainObject.newInstance(context);
                            toObject.createObject(context, DomainObject.TYPE_ROUTE_TASK_USER, null, null, null, null);
                        } else {
                            //Normal task
                            toObject = new DomainObject(strUserId);
                        }

                        //connect assinee to Route
                        dmoRelationship = DomainRelationship.connect(context, fromObject, DomainObject.RELATIONSHIP_ROUTE_NODE, toObject);

                        //Copy all the attributes from route template Route Node relationship to Route
                        mapRelAttributesToSet = new HashMap();
                        for (Iterator itrAttributes = mapRelAttributes.keySet().iterator(); itrAttributes.hasNext(); ) {
                            strAttributeName = (String) itrAttributes.next();
                            strAttributeValue = (String) mapTask.get(DomainRelationship.getAttributeSelect(strAttributeName));
                            mapRelAttributesToSet.put(strAttributeName, strAttributeValue);
                        }//for
                        mapRelAttributesToSet.put(TEMPLATE_TASK, "Yes");
                        //set all attributes to new Route
                        MqlUtil.mqlCommand(context, "mod connection $1 add interface $2", dmoRelationship.getName(), ATTRIBUTE_IGA_DRW_APPROVER);
                        dmoRelationship.setAttributeValues(context, mapRelAttributesToSet);

                        //Check for Route Status
                        strRouteStatus = (String) mapRouteToBeUsed.get(SELECT_ATTRIBUTE_ROUTE_STATUS);

                        //
                        // Add the new tasks to the route and adjust the order of the tasks
                        // If route is not started then add tge new tasks at their own orders, else
                        // add the tasks from the current level of the route.
                        //
                        int nNewSequenceNumber = 1;
                        if ("Not Started".equals(strRouteStatus)) {
                            nNewSequenceNumber = Integer.parseInt(currTaskSeqStr);
                        } else {
                            nNewSequenceNumber = Integer.parseInt(strCurrentRouteSequenceNo) + Integer.parseInt(currTaskSeqStr) - 1;
                        }

                        dmoRelationship.setAttributeValue(context, DomainObject.ATTRIBUTE_ROUTE_SEQUENCE, String.valueOf(nNewSequenceNumber));
                    }//for

                    //Activate tasks on current level accordingly
                    if ("Started".equals(strRouteStatus)) {
                        isTaskOnCurrentLevelToBeStarted = true;
                    } else if ("Stopped".equals(strRouteStatus)) {
                        isTaskOnCurrentLevelToBeStartedForStoppedRoute = true;
                    }
                }
            }

            ///////////////////////////////////////////////////////////////////////////
            // Update the Route Node Id attribute on the relationships
            //
            strRelPattern = DomainObject.RELATIONSHIP_ROUTE_NODE;
            strTypePattern = "*";
            StringList slBusSelects = new StringList();
            StringList slRelSelects = new StringList(DomainRelationship.SELECT_ID);
            boolean getTo = false;
            boolean getFrom = true;
            nRecurseToLevel = (short) 1;
            strObjectWhere = "";
            strRelWhere = "";

            MapList mlRouteNodeRelInfo = route.getRelatedObjects(context,
                    strRelPattern,
                    strTypePattern,
                    slBusSelects,
                    slRelSelects,
                    getTo,
                    getFrom,
                    nRecurseToLevel,
                    strObjectWhere,
                    strRelWhere);
            Map mapRouteNodeRelInfo = null;
            String strRouteNodeRelId = null;
            DomainRelationship dmrRouteNode = null;
            for (Iterator itrRouteNodeRelInfo = mlRouteNodeRelInfo.iterator(); itrRouteNodeRelInfo.hasNext(); ) {
                mapRouteNodeRelInfo = (Map) itrRouteNodeRelInfo.next();
                strRouteNodeRelId = (String) mapRouteNodeRelInfo.get(DomainRelationship.SELECT_ID);
                dmrRouteNode = new DomainRelationship(strRouteNodeRelId);
                strRouteNodeRelId = MqlUtil.mqlCommand(context, "print connection $1 select $2 dump", strRouteNodeRelId, "physicalid");
                dmrRouteNode.setAttributeValue(context, DomainObject.ATTRIBUTE_ROUTE_NODE_ID, strRouteNodeRelId);
            }
            //
            //
            ///////////////////////////////////////////////////////////////////////////

            String strCurrentRouteSequenceNo = route.getInfo(context, SELECT_ATTRIBUTE_CURRENT_ROUTE_NODE);
            if (strCurrentRouteSequenceNo == null || "".equals(strCurrentRouteSequenceNo.trim())) {
                strCurrentRouteSequenceNo = "1";
            }
            if (isRouteToBeStarted) {
                String attrDueDateOffset = "Due Date Offset";
                String attrDueDateOffsetFrom = "Date Offset From";
                String selDueDateOffset = ATTRIBUTE_OPEN_BRACE + attrDueDateOffset + "]";
                String selDueDateOffsetFrom = ATTRIBUTE_OPEN_BRACE + attrDueDateOffsetFrom + "]";
                String selRouteNodeRelId = DomainObject.SELECT_RELATIONSHIP_ID;
                String selSequence = ATTRIBUTE_OPEN_BRACE + DomainObject.ATTRIBUTE_ROUTE_SEQUENCE + "]";
                String sWhereExp = "";
                String OFFSET_FROM_ROUTE_START_DATE = "Route Start Date";
                sWhereExp += "(" + selDueDateOffset + " !~~ \"\")";
                sWhereExp += " && (" + selDueDateOffsetFrom + " ~~ \"" + OFFSET_FROM_ROUTE_START_DATE + "\")";
                sWhereExp += " || (" + selSequence + " == \"1\")";
                StringList relSelects = new StringList();
                relSelects.addElement(selDueDateOffset);
                relSelects.addElement(selDueDateOffsetFrom);
                relSelects.addElement(selRouteNodeRelId);
                relSelects.addElement(selSequence);
                MapList routeFirstOrderOffsetList = com.matrixone.apps.common.Route.getFirstOrderOffsetTasks(context, route, relSelects, sWhereExp);
                // set Scheduled Due Date attribute for all delta offset ORDER 1 Route Nodes offset From Task create which is same as Route start
                com.matrixone.apps.common.Route.setDueDatesFromOffset(context, routeFirstOrderOffsetList);
                route.promote(context);
                route.setDueDateFromOffsetForGivenLevelTasks(context, 1);
            } else if (isTaskOnCurrentLevelToBeStarted) {
                route.setDueDateFromOffsetForGivenLevelTasks(context, Integer.parseInt(strCurrentRouteSequenceNo));
                route.startTasksOnCurrentLevel(context);
            } else if (isTaskOnCurrentLevelToBeStartedForStoppedRoute) {
                route.setAttributeValue(context, DomainObject.ATTRIBUTE_ROUTE_STATUS, "Started");
                route.setDueDateFromOffsetForGivenLevelTasks(context, Integer.parseInt(strCurrentRouteSequenceNo));
                route.startTasksOnCurrentLevel(context);
                route.setAttributeValue(context, DomainObject.ATTRIBUTE_ROUTE_STATUS, "Stopped");
            }

            return route.getObjectId(context);
        } catch (Exception exp) {
            exp.printStackTrace();
            throw exp;
        }
    }

    /**
     * Disconnect contents from a current route
     *
     * @param routeId route id
     * @param content list of content
     * @param selector selector
     * @return list of changed objects
     */
    @MatrixTransactional
    public void disconnectContent(String routeId, List<String> content, Selector selector) throws FrameworkException {
        Set<String> connectionIds = new HashSet<>();
        DBObject routeDB = get(routeId, getDefaultSelectorWithOwner());
        List<DBObject> contentDB = Util.getRouteContents(getContext(),routeDB, selector, "");
        contentDB.forEach(object -> {
            if (content.contains(object.getPhysicalid())) {
                List<String> linkedRoute = Util.normalizeToList(object.getInfo("from[Object Route].to.physicalid"));
                List<String> connectionId = Util.normalizeToList(object.getInfo("from[Object Route].physicalid"));
                if (linkedRoute.contains(routeId)) {
                    connectionIds.add(connectionId.get(linkedRoute.indexOf(routeId)));
                }
            }
        });

        String routeOwner = ((Owner) routeDB.getInfo("owner")).getName();
        if (connectionIds.size() > 0 && routeOwner!=null && Util.checkUser(getContext(), routeOwner))  {
            disconnectRelations(connectionIds);
        } else {
            log.info(String.format("Route owner('%s') does not equal current owner('%s')!", routeOwner, getContext().getUser()));
        }
    }

    private Selector getDefaultSelectorWithOwner(){
        return new Selector.Builder().selector(StandardSelectors.DEFAULT).basic("owner").build();
    }

    private void disconnectRelations(Collection<String> relId) throws FrameworkException {
        long l = System.currentTimeMillis();
        DomainRelationship.disconnect(getContext(),
                relId.stream()
                        .toArray(String[]::new));
        log.debug(String.format("%s\t%s\t%s", "RouteDAO.disconnectRelations",
                "disconnect", System.currentTimeMillis() - l));
    }
}
