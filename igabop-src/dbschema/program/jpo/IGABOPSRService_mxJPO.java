import com.dassault_systemes.platform.model.*;
import com.dassault_systemes.platform.model.itf.IOxidService;
import com.dassault_systemes.platform.model.services.IdentificationServicesProvider;
import com.dassault_systemes.platform.model.services.MdDictionaryServices;
import com.dassault_systemes.vplmcoredictionaryinterfaces.IVPLMCoreDictionaryEnumerator;
import matrix.db.*;
import matrix.util.MatrixException;
import matrix.util.StringList;

import java.util.*;

/**copy from com.dassault_systemes.platform.model.implement.SRService class*/
public class IGABOPSRService_mxJPO {
    private static Map<String, String> rolesByNames = new HashMap<>();
    private static Map<String, String> semanticsByNames = new HashMap<>();
    private static Map<String, Integer> idRelMaxMap = new HashMap<>();

    private static void InitMapFromDictionary(Context var0, String var1, String var2, Map<String, String> var3) throws MatrixException {
        MdDictionaryServices var4 = MdDictionaryServices.getDictionaryServices();
        List var5 = var4.getEnumeratorsFromDictionary(var0, var1, var2);

        for (Object o : var5) {
            IVPLMCoreDictionaryEnumerator var7 = (IVPLMCoreDictionaryEnumerator) o;
            var3.put(var7.getName(var0), var7.getValue());
        }

    }

    public List<String> createSR(Context context, Oxid oxid, SRHolder srHolder) throws CommonWebException, MatrixException {
        List var4 = srHolder.getPaths();

        if (!this.isAuthorizedOwner(context, oxid)) {
            throw new CommonWebException("Owner type not supported " + oxid.getType() + ". For this type, no role/semantics available in metadata files " + "VPMCfgEffectivity|RFLPLMFunctional");
        } else {
            Iterator var5 = var4.iterator();
            ArrayList var6 = new ArrayList();
            int var7 = srHolder.getAppIndex();
            int var8 = 0;
            if (rolesByNames.isEmpty()) {
                this.initRoles(context);
            }

            if (semanticsByNames.isEmpty()) {
                this.initSemantics(context);
            }

            while(true) {
                PathHolder var9;
                do {
                    if (!var5.hasNext()) {
                        return var6;
                    }

                    var9 = (PathHolder)var5.next();
                    ++var8;
                } while(var9.length() <= 0);

                if (srHolder.getIdrel() == -1) {
                    throw new CommonWebException("IDRel attribute is missing");
                }

                if (srHolder.getRole() == null) {
                    throw new CommonWebException("Role attribute is missing");
                }

                if (srHolder.getSemantics() == null) {
                    throw new CommonWebException("Semantics attribute is missing");
                }

                int var10 = var9.length();
                Path.Element[] var11 = this.getPathElements(context, var9, srHolder.getSemantics());
                AttributeList var12 = new AttributeList();
                int var13 = this.getNextIdRelValue(context, oxid, srHolder.getIdrel());
                var12.addElement(new Attribute(new AttributeType("IDRel"), Integer.toString(var13)));
                var12.addElement(new Attribute(new AttributeType("Semantics"), (String)semanticsByNames.get(srHolder.getSemantics())));
                var12.addElement(new Attribute(new AttributeType("Role"), (String)rolesByNames.get(srHolder.getRole())));
                var12.addElement(new Attribute(new AttributeType("RoleSemantics"), srHolder.getRole() + srHolder.getSemantics()));
                if (srHolder.getPrivateData() != null) {
                    var12.addElement(new Attribute(new AttributeType("PrivateData"), srHolder.getPrivateData()));
                }

                if (srHolder.isOrdered()) {
                    var7 = var13;
                }

                var12.addElement(new Attribute(new AttributeType("AppIndex"), Integer.toString(var7)));
                String var14 = this.getOutOfScopeForElements(var9.getPathElements());
                var12.addElement(new Attribute(new AttributeType("OutOfScopes"), var14));
                String var15 = (var9.getPathElements().get(var10 - 1)).getPID() + "|" + (String)rolesByNames.get(srHolder.getRole());
                var12.addElement(new Attribute(new AttributeType("LastPIDAndRole"), var15));
                BusinessInterfaceList var16 = new BusinessInterfaceList();
                List var17 = srHolder.getSRQuads();
                if (var17 != null) {
                    StringBuffer var18 = new StringBuffer();
                    StringBuffer var19 = new StringBuffer();
                    StringBuffer var20 = new StringBuffer();
                    Iterator var21 = var17.iterator();

                    while(var21.hasNext()) {
                        SRHolder.SRQuad var22 = (SRHolder.SRQuad)var21.next();
                        var18.append(var22.getSyncDomain());
                        if (var21.hasNext()) {
                            var18.append("|");
                        }

                        List var23 = var22.getSyncSpecs();
                        Iterator var24 = var23.iterator();

                        while(var24.hasNext()) {
                            var19.append((String)var24.next());
                            if (var24.hasNext()) {
                                var19.append(",");
                            }
                        }

                        if (var21.hasNext()) {
                            var19.append("|");
                        }

                        var20.append(var22.getSyncValue());
                        if (var21.hasNext()) {
                            var20.append("|");
                        }
                    }

                    var12.addElement(new Attribute(new AttributeType("SyncDomain"), var18.toString()));
                    var12.addElement(new Attribute(new AttributeType("SyncSpec"), var19.toString()));
                    var12.addElement(new Attribute(new AttributeType("SyncValue"), var20.toString()));
                    BusinessInterface var28 = new BusinessInterface("SRonAttribute", context.getVault());
                    var16.addElement(var28);
                }

                try {
                    if (oxid.getKind().equals(BaseKind.businessobject)) {
                        BusinessObject var26 = new BusinessObject(oxid.getPhysicalid());
                        var6.add(var26.addPath(context, "SemanticRelation", var11, var12, var16));
                    } else {
                        Relationship var27 = new Relationship(oxid.getPhysicalid());
                        var6.add(var27.addPath(context, "SemanticRelation", var11, var12, var16));
                    }
                } catch (MatrixException var25) {
                    throw var25;
                }
            }
        }
    }

    private boolean isAuthorizedOwner(Context var1, Oxid var2) throws MatrixException {
        boolean var3 = true;
        if (!var3) {
            MdDictionaryServices var4 = MdDictionaryServices.getDictionaryServices();
            String[] var5 = "VPMCfgEffectivity|RFLPLMFunctional".split("\\\\|");
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String var8 = var5[var7];
                List var9 = var4.getPathTypesFromDictionary(var1, var8, var2.getType());
                if (var9 != null) {
                    var3 = true;
                    break;
                }
            }
        }

        return var3;
    }

    private void initRoles(Context var1) throws MatrixException {
        InitMapFromDictionary(var1, "RELATION_ENUMS", "C_RoleEnum", rolesByNames);
    }

    private void initSemantics(Context var1) throws MatrixException {
        InitMapFromDictionary(var1, "RELATION_ENUMS", "C_SemanticsEnum", semanticsByNames);
    }

    private Path.Element[] getPathElements(Context var1, PathHolder var2, String var3) throws CommonWebException, MatrixException {
        if (var2.getPathMatrixElements() != null) {
            return var2.getPathMatrixElements();
        } else {
            List var4 = var2.getPathElements();
            int var5 = var4.size();
            StringList var6 = new StringList(4);
            var6.addElement("physicalid");
            var6.addElement("logicalid");
            var6.addElement("majorid");
            var6.addElement("updatestamp");
            Path.Element[] var7 = new Path.Element[var5];
            ArrayList var8 = new ArrayList(var5);
            ArrayList var9 = new ArrayList(var5);
            List var10 = var2.getPathElementsPID();
            HashMap var11 = new HashMap(var5);
            List var12 = var2.getPathElementsOxid();
            if (var12 == null) {
                IOxidService var13 = IdentificationServicesProvider.getOxidService();
                var12 = var13.getOxidsFromPids(var1, var10);
            }

            for (Object o : var12) {
                Oxid var14 = (Oxid) o;
                String var15 = var14.getPhysicalid();
                BaseKind var16 = var14.getKind();
                var11.put(var15, var14);
                if (var16 == BaseKind.businessobject) {
                    var8.add(var15);
                } else if (var16 == BaseKind.connection) {
                    var9.add(var15);
                }
            }

            String var18;
            String var19;
            String var20;
            String var21;
            int var22;
            Oxid var23;
            String var24;
            boolean var25;
            byte var27;
            String[] var28;
            Iterator var31;
            if (!var8.isEmpty()) {
                var27 = 1;
                var28 = new String[var8.size()];
                var28 = (String[])var8.toArray(var28);
                BusinessObjectWithSelectList var29 = BusinessObject.getSelectBusinessObjectData(var1, var28, var6);

                for(var31 = var29.iterator(); var31.hasNext(); var7[var22] = new Path.Element(var22, var27, var24, var19, var21, var20, var18, var25)) {
                    BusinessObjectWithSelect var17 = (BusinessObjectWithSelect)var31.next();
                    var18 = var17.getSelectData("updatestamp");
                    var19 = var17.getSelectData("physicalid");
                    var20 = var17.getSelectData("logicalid");
                    var21 = var17.getSelectData("majorid");
                    var22 = var10.indexOf(var19);
                    var23 = (Oxid)var11.get(var19);
                    var24 = var23.getType();
                    var25 = this.isRelevant(var3, var22 == var5 - 1);
                }
            }

            if (!var9.isEmpty()) {
                var27 = 2;
                var28 = new String[var9.size()];
                var28 = (String[])var9.toArray(var28);
                RelationshipWithSelectList var30 = Relationship.getSelectRelationshipData(var1, var28, var6);

                for(var31 = var30.iterator(); var31.hasNext(); var7[var22] = new Path.Element(var22, var27, var24, var19, var21, var20, var18, var25)) {
                    RelationshipWithSelect var32 = (RelationshipWithSelect)var31.next();
                    var18 = var32.getSelectData("updatestamp");
                    var19 = var32.getSelectData("physicalid");
                    var20 = var32.getSelectData("logicalid");
                    var21 = var32.getSelectData("majorid");
                    var22 = var10.indexOf(var19);
                    var23 = (Oxid)var11.get(var19);
                    var24 = var23.getType();
                    var25 = this.isRelevant(var3, var22 == var5 - 1);
                }
            }

            return var7;
        }
    }

    private boolean isRelevant(String var1, boolean var2) throws CommonWebException {
        byte var4 = -1;
        switch(var1.hashCode()) {
            case -916552089:
                if (var1.equals("Reference2")) {
                    var4 = 1;
                }
                break;
            case -916552088:
                if (var1.equals("Reference3")) {
                    var4 = 2;
                }
                break;
            case -916552087:
                if (var1.equals("Reference4")) {
                    var4 = 3;
                }
                break;
            case -916552086:
                if (var1.equals("Reference5")) {
                    var4 = 4;
                }
                break;
            case -916552085:
                if (var1.equals("Reference6")) {
                    var4 = 5;
                }
                break;
            case -916552084:
                if (var1.equals("Reference7")) {
                    var4 = 6;
                }
                break;
            case 1078812459:
                if (var1.equals("Reference")) {
                    var4 = 0;
                }
        }

        switch(var4) {
            case 0:
            case 1:
                return false;
            case 2:
                return true;
            case 3:
            case 4:
            case 5:
            case 6:
                return var2;
            default:
                throw new CommonWebException("Semantics attribute is not supported : ", var1);
        }
    }

    private int getNextIdRelValue(Context var1, Oxid var2, int var3) throws MatrixException {
        String var4 = var2.getPhysicalid();
        int var5 = var3;
        Integer var6 = idRelMaxMap.get(var4);
        if (var6 != null) {
            var5 = var6 + 1;
            idRelMaxMap.put(var4, var5);
        } else {
            StringList var7 = new StringList();
            var7.add("physicalid");
            var7.add("paths[SemanticRelation].path.attribute[IDRel]");
            ArrayList var8 = new ArrayList();
            String[] var9 = new String[]{var4};
            Iterator var11;
            StringList var13;
            if (var2.getKind().equals(BaseKind.businessobject)) {
                BusinessObjectWithSelectList var14 = BusinessObject.getSelectBusinessObjectData(var1, var9, var7, false);
                var11 = var14.iterator();

                while(var11.hasNext()) {
                    BusinessObjectWithSelect var17 = (BusinessObjectWithSelect)var11.next();
                    var13 = var17.getSelectDataList("paths[SemanticRelation].path.attribute[IDRel]");
                    if (var13 != null) {
                        var8.addAll(var13.toList());
                    }
                }
            } else {
                RelationshipWithSelectList var10 = Relationship.getSelectRelationshipData(var1, var9, var7, false);
                var11 = var10.iterator();

                while(var11.hasNext()) {
                    RelationshipWithSelect var12 = (RelationshipWithSelect)var11.next();
                    var13 = var12.getSelectDataList("paths[SemanticRelation].path.attribute[IDRel]");
                   if (var13 != null) {
                        var8.addAll(var13.toList());
                    }
                }
            }

            if (!var8.isEmpty()) {

                for (Object o : var8) {
                    String var16 = (String) o;
                    int var18 = Integer.parseInt(var16);
                    if (var18 > var5) {
                        var5 = var18;
                    }
                }

                ++var5;
            }

            idRelMaxMap.put(var4, var5);
        }

        return var5;
    }

    private String getOutOfScopeForElements(List<com.dassault_systemes.platform.model.PathHolder.Element> var1) throws CommonWebException {
        StringBuffer var2 = new StringBuffer(64);

        for(int var3 = 0; var3 < var1.size(); ++var3) {
            com.dassault_systemes.platform.model.PathHolder.Element var4 = (com.dassault_systemes.platform.model.PathHolder.Element)var1.get(var3);
            if (null == var4) {
                throw new CommonWebException("Current PathElement is null");
            }

            int var5 = var4.getOutOfScope();
            var2.append(String.valueOf(var5));
            if (var3 < var1.size() - 1) {
                var2.append("|");
            }
        }

        return var2.toString();
    }

}
