import com.dassault_systemes.platform.model.services.MdIDBaseServices;
import com.igatec.bop.core.report.ConfigurableSource;
import com.igatec.bop.core.report.FopConvertor;
import com.igatec.bop.core.report.quickstruct.QSDocument;
import com.igatec.bop.utils.Constants;
import com.igatec.bop.utils.Util;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IGABOPReport_mxJPO extends Constants implements DomainConstants {
    public static int generateReport(Context context, String routeId) throws MatrixException {
        int result = 0;
        String filePath = context.createWorkspace();
        System.out.println(filePath);
        String organizationTitle = "SkyWay";
        try {
            ContextUtil.startTransaction(context, true);

            String factoryPID = getFactoryPID(context, routeId);
            if(factoryPID != null) {
                String pprPID = getPPRPID(context, factoryPID);
                QSDocument qsDoc = new QSDocument(context, pprPID, factoryPID);
                qsDoc.traverseStructure();
                String xmlText = qsDoc.convertToXML();
                String xmlTextNoHeader = xmlText.substring(xmlText.indexOf("?>") + 2);

                //debug
                storeToFile(filePath + File.separator + "XPDMXML.xml", xmlText.getBytes(StandardCharsets.UTF_8));

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                String imageDirUrl = qsDoc.getCheckoutURL();

                ConfigurableSource xslConfSource = new ConfigurableSource("espd/_espd_compiled.xsl");
                xslConfSource.setConfig("IMAGE_DIR_URL", imageDirUrl);
                xslConfSource.setConfig("ORGANIZATION", organizationTitle);

                FopConvertor fopConvertor = new FopConvertor();
                fopConvertor.convert(xslConfSource, xmlTextNoHeader, outputStream);

                byte[] pdfData = outputStream.toByteArray();
                //debug
                storeToFile(filePath + File.separator + "report.pdf", pdfData);

                String fileName = String.format("%s_TD.pdf", qsDoc.getFactoryTitle());
                removeOldReports(context, factoryPID, fileName);
                attachFile(context, factoryPID, filePath, fileName, pdfData);
            }

            ContextUtil.commitTransaction(context);
            return result;
        } catch (Exception ex) {
            ContextUtil.abortTransaction(context);
            ex.printStackTrace();
            return 1;
        }
    }

    private static String getPPRPID(Context context, String factoryID) throws FrameworkException {
        String id = null;
        if (factoryID != null) {
            StringList selects = new StringList();
            selects.add(Constants.SELECT_PHYSICALID);
            String sSelect = String.format("from[%s].to.%s", RELATIONSHIP_PLM_CONNECTION, SELECT_PATHS_SR_EL_PHYSICALID);
            String objWhere = String.format("%s match '%s'", sSelect, factoryID);
            MapList ml = DomainObject.findObjects(context, TYPE_PPR_CONTEXT, VAULT_VPLM, objWhere, selects);
            System.out.println(ml);
            if (!ml.isEmpty())
                id = (String) ((Map) ml.get(0)).get(Constants.SELECT_PHYSICALID);
            System.out.println(id);
        }
        return id;
    }

    private static String getFactoryPID(Context context, String routeId) throws FrameworkException {
        String pid = null;

        DomainObject route = DomainObject.newInstance(context, routeId);
        StringList objectSelects = new StringList();
        objectSelects.add(SELECT_PHYSICALID);
        objectSelects.add(Constants.SELECT_TYPE);
        StringList relSelects = new StringList();;
        MapList objectList = route.getRelatedObjects(context,
                Constants.RELATIONSHIP_OBJECT_ROUTE,
                Constants.QUERY_WILDCARD,
                objectSelects,
                relSelects,
                true,
                false,
                (short)0,
                null,
                null,
                0);

        if (!objectList.isEmpty()) {
            System.out.println(objectList);
            List<Map> filtered = ((List<Map>) objectList).stream()
                    .filter(map -> TYPE_KIT_FACTORY.equals(map.get(Constants.SELECT_TYPE)))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty())
                pid = (String) filtered.get(0).get(SELECT_PHYSICALID);
        }
        System.out.println(pid);
        return pid;
    }

    private static void storeToFile(String filePath, byte[] data) throws IOException {
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
        stream.write(data);
        stream.close();
    }

    private static void removeOldReports(Context context, String pid, String title) throws Exception {
        String sSelect = String.format("from[%s].to.%s", RELATIONSHIP_PLM_CONNECTION, SELECT_PATHS_SR_EL_PHYSICALID);
        String sResult = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", pid, sSelect);
        String[] pids = sResult.split(DELIMITER_COMMA);

        StringList objSelects = new StringList();
        objSelects.add(SELECT_PHYSICALID);
        objSelects.add(SELECT_ATTRIBUTE_TITLE);
        List<Map> docs = (List<Map>) DomainObject.getInfo(context, pids, objSelects);

        System.out.println(docs);

        List<String> onlyReportPIDs = docs.stream()
                .filter(m -> title.equals(m.get(SELECT_ATTRIBUTE_TITLE)))
                .map(m -> (String) m.get(SELECT_PHYSICALID))
                .collect(Collectors.toList());

        if (!onlyReportPIDs.isEmpty()) {
            //some of reports can be attached under User Agent so remove them under UA also
            try {
                ContextUtil.pushContext(context);
                System.out.println("onlyReportPIDs="+onlyReportPIDs);
                Util.delete(context, String.join(DELIMITER_COMMA, onlyReportPIDs), true);
            } finally {
                ContextUtil.popContext(context);
            }
        }
    }

    private static String attachFile(Context context, String pid, String filePath, String fileName, byte[] pdfData) throws Exception {
        File file = new File(filePath + File.separator + fileName);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(pdfData);
        stream.close();

        return attachDocument(context, pid, fileName, filePath);
    }

    private static String attachDocument(Context context, String pid, String fileName, String filePath) throws Exception {
        String sDocPLMID = Util.createDocument(context, fileName, filePath);
        System.out.println("sDocPLMID="+sDocPLMID);
        createDocComposeeUnderUA(context, pid, sDocPLMID);
        return MdIDBaseServices.getPhysicalIDfromPLMId(sDocPLMID);
    }

    private static void createDocComposeeUnderUA(Context context, String pid, String sDocPLMID) throws Exception {
        DomainObject obj = DomainObject.newInstance(context, pid);
        StringList objSelects = new StringList();
        objSelects.add(Constants.SELECT_TYPE);
        objSelects.add(Constants.SELECT_CURRENT);
        Map map = obj.getInfo(context, objSelects);
        String type = (String) map.get(Constants.SELECT_TYPE);
        String current = (String) map.get(Constants.SELECT_CURRENT);

        boolean isFrozenKitFactory = TYPE_KIT_FACTORY.equals(type) && STATE_FROZEN.equals(current);
        if (isFrozenKitFactory)
            ContextUtil.pushContext(context);
        try {
            String sComposeePLMID = Util.createDocComposee(context, pid);
            Util.createSR(context, sComposeePLMID, new String[]{sDocPLMID}, SEMANTICS_REFERENCE, ROLE_DMT_DOCUMENT, DEFAULT_ID_REL);
        } finally {
            if (isFrozenKitFactory)
                ContextUtil.popContext(context);
        }
    }
}