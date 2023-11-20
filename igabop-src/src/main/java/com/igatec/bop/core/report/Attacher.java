package com.igatec.bop.core.report;

import com.igatec.bop.utils.Util;
import com.igatec.utilsspring.utils.common.core.DBObject;
import com.igatec.utilsspring.utils.common.core.Selector;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import matrix.db.Context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.igatec.bop.utils.Constants.DELIMITER_COMMA;
import static com.igatec.bop.utils.Constants.PARAM_COMPOSEE;
import static com.matrixone.apps.domain.DomainConstants.ATTRIBUTE_TITLE;

/**
 * Class represents methods to work with file which will be attached to document
 */
public class Attacher {

    public final static String FILE_NAME = "report.pdf";

    public Attacher() {
    }

    /**
     * It attaches report to document
     *
     * @param context Context
     * @param pid id document
     * @param fileName report name
     * @param pdfData report data
     * @return id document with attached report
     */
    public static String attachFile(Context context,
                                    String pid,
                                    String fileName,
                                    byte[] pdfData
    ) throws Exception {
        String filePath = System.getProperty("user.dir");

        if (fileName == null) {
            fileName = FILE_NAME;
        }

        File file = new File(filePath + File.separator + fileName);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(pdfData);
        stream.close();

        Selector selector = new Selector.Builder().build();
        DBObject object = getInfo(context, pid, selector);
        String sDocPID = Util.attachDocument(context, object, fileName, filePath);
        return sDocPID;
    }

    /**
     * Returns information
     *
     * @param context context
     * @param objectId id document
     * @param selector selector to get information
     * @return DBObject with select
     * @throws FrameworkException if it breaks in getInfo
     */
    public static DBObject getInfo(Context context,
                                   String objectId,
                                   Selector selector
    ) throws FrameworkException {
        List<DBObject> objects = getInfo(context, new String[]{objectId}, selector);
        return objects.get(0);
    }

    /**
     * Returns information for many objects
     *
     * @param context context
     * @param objectIds ids objects
     * @param selector selector to get information
     * @return list of DBObjects
     * @throws FrameworkException if it breaks in getInfo of DomainObject
     */
    public static List<DBObject> getInfo(Context context,
                                         String[] objectIds,
                                         Selector selector
    ) throws FrameworkException {
        List<Map> results = DomainObject.getInfo(context, objectIds, selector.toStringList());
        return results.stream().map(DBObject::fromMap).collect(Collectors.toList());
    }

    /**
     * Removes old attached report
     *
     * @param context context
     * @param pid id document
     * @param title title of report
     */
    public static void removeOldReports(Context context,
                                        String pid,
                                        String title
    ) throws Exception {
        Selector selector = new Selector.Builder().build();
        DBObject object = getInfo(context, pid, selector);
        List<DBObject> docs = Util.getDocuments(context, object, selector);

        List<DBObject> onlyReports = docs.stream().filter(o -> title.equals(o.getAttributeValue(ATTRIBUTE_TITLE))).collect(Collectors.toList());
        List<String> idsOfComposee = onlyReports.stream().map(o -> (String) o.getInfo(PARAM_COMPOSEE)).collect(Collectors.toList());

        if (!idsOfComposee.isEmpty()) {
            //some of reports can be attached under User Agent so remove them under UA also
            try {
                ContextUtil.pushContext(context);
                Util.delete(context, String.join(DELIMITER_COMMA, idsOfComposee), true);
            } finally {
                ContextUtil.popContext(context);
            }
        }
    }
}
