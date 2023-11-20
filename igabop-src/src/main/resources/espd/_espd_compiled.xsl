<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:exsl="http://exslt.org/common"
  xmlns:xpdm="http://www.3ds.com/xsd/XPDMXML"
  version="1.0">
<!-- ============================================================================================================ -->
<xsl:output method="xml"/>
<!-- ============================================================================================================ -->
<xsl:variable name="images_base" select="'IMAGE_DIR_URL'" />
<xsl:variable name="organization" select="'ORGANIZATION'" />
<!-- xsl:variable name="organization" select="'SkyWay'" / -->
<!-- xsl:variable name="organization" select="'ТЖДМ'" / -->
<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->
<xsl:variable name="GLOBAL_ROOT" select="/xpdm:XPDMXML" />
<xsl:variable name="GLOBAL_PS" select="/xpdm:XPDMXML/xpdm:ProductStructure" />
<xsl:variable name="GLOBAL_PTS" select="/xpdm:XPDMXML/xpdm:ProductTransformationStructure" />
<xsl:variable name="GLOBAL_PPS" select="/xpdm:XPDMXML/xpdm:ProcessPlanningStructure" />
<xsl:variable name="GLOBAL_CR"  select="/xpdm:XPDMXML/xpdm:ProcessPlanningStructure/xpdm:CapableReferenceResourceLink" />
<xsl:variable name="GLOBAL_RS"  select="/xpdm:XPDMXML/xpdm:ResourceStructure" />

<xsl:variable name="GLOBAL_SEQ" select="/" />
<xsl:variable name="GLOBAL_ATTR_ROOT" select="/" />
<xsl:variable name="GLOBAL_PSS" select="/" />
<!-- ============================================================================================================ -->

<xsl:template name="levelUp" >
	<xsl:param name="id" />
	<xsl:value-of select="$GLOBAL_PPS/*[xpdm:Instancing=$id and xpdm:Owned]/xpdm:Owned" />
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getWorkShop" >
	<xsl:param name="operationId" />

	<xsl:variable name="up1"><xsl:call-template name="levelUp"><xsl:with-param name="id" select="$operationId"/></xsl:call-template></xsl:variable>
	<xsl:variable name="up2"><xsl:call-template name="levelUp"><xsl:with-param name="id" select="$up1"/></xsl:call-template></xsl:variable>
	<xsl:variable name="up3"><xsl:call-template name="levelUp"><xsl:with-param name="id" select="$up2"/></xsl:call-template></xsl:variable>

	<xsl:variable name="theID">
		<xsl:choose>
			<!-- предприятие/цех/участок -->
			<xsl:when test="string-length($up3)" ><xsl:value-of select="$up2"/></xsl:when>
			<!-- предприятие/цех -->
			<xsl:when test="string-length($up2)" ><xsl:value-of select="$up1"/></xsl:when>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:value-of select="$GLOBAL_PPS/*[@id=$theID]/xpdm:Name" />
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getWorkArea" >
	<xsl:param name="operationId" />

	<xsl:variable name="up1"><xsl:call-template name="levelUp"><xsl:with-param name="id" select="$operationId"/></xsl:call-template></xsl:variable>
	<xsl:variable name="up2"><xsl:call-template name="levelUp"><xsl:with-param name="id" select="$up1"/></xsl:call-template></xsl:variable>
	<xsl:variable name="up3"><xsl:call-template name="levelUp"><xsl:with-param name="id" select="$up2"/></xsl:call-template></xsl:variable>

	<xsl:variable name="theID">
		<xsl:choose>
			<!-- предприятие/цех/участок -->
			<xsl:when test="string-length($up3)" ><xsl:value-of select="$up1"/></xsl:when>
			<!-- предприятие/цех -->
			<!-- xsl:when test="string-length($up2)" ><xsl:value-of select="$up1"/></xsl:when -->
		</xsl:choose>
	</xsl:variable>
	
	<xsl:value-of select="$GLOBAL_PPS/*[@id=$theID]/xpdm:Name" />
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getWorkPlace" >
	<xsl:param name="operationId" />
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getBar" >

	<xsl:variable name="bar" select="$GLOBAL_PTS/*[xpdm:Property[@name='Kit_StockLength' or @name='Kit_StockHightDiameter' or @name='Kit_StockWidth' or @name='Kit_StockWeight']]" />

	<xsl:if test="$bar" >
	
		<xsl:element name="Bar" >
			<xsl:attribute name="id"><xsl:value-of select="$bar/@id" /></xsl:attribute>
			<xsl:attribute name="Kit_StandardUnit"><xsl:value-of select="$bar/xpdm:Property[@name='Kit_StandardUnit']/xpdm:Value" /></xsl:attribute>

			<xsl:attribute name="Kit_UnitValue"><xsl:value-of select="$bar/xpdm:Property[@name='Kit_UnitValue']/xpdm:Value" /></xsl:attribute>
			<xsl:attribute name="unitValue">
				<xsl:call-template name="decodeUnit"><xsl:with-param name="v" select="$bar/xpdm:Property[@name='Kit_UnitValue']/xpdm:Value"/></xsl:call-template>		
			</xsl:attribute>
			
			<xsl:attribute name="Kit_MatConsumptionRate"><xsl:value-of select="$bar/xpdm:Property[@name='Kit_MatConsumptionRate']/xpdm:Value" /></xsl:attribute>
			<xsl:attribute name="Kit_UsageCoeffic"><xsl:value-of select="$bar/xpdm:Property[@name='Kit_UsageCoeffic']/xpdm:Value" /></xsl:attribute>
			<xsl:attribute name="Kit_BarCode"><xsl:value-of select="$bar/xpdm:Property[@name='Kit_BarCode']/xpdm:Value" /></xsl:attribute>
			<xsl:attribute name="Kit_NumberOfParts"><xsl:value-of select="$bar/xpdm:Property[@name='Kit_NumberOfParts']/xpdm:Value" /></xsl:attribute>
			<xsl:attribute name="Kit_RawType"><xsl:value-of select="$bar/xpdm:Property[@name='Kit_RawType']/xpdm:Value" /></xsl:attribute>


			<xsl:attribute name="Kit_StockWeight"><xsl:value-of select="$bar/xpdm:Property[@name='Kit_StockWeight']/xpdm:Value" /></xsl:attribute>

			<xsl:attribute name="Kit_StockHightDiameter">
				<xsl:call-template name="mul1000NoZero"><xsl:with-param name="v" select="$bar/xpdm:Property[@name='Kit_StockHightDiameter']/xpdm:Value"/></xsl:call-template>		
				<!-- xsl:value-of select="$bar/xpdm:Property[@name='Kit_StockHightDiameter']/xpdm:Value" / -->
			</xsl:attribute>

			<xsl:attribute name="Kit_StockWidth">
				<xsl:call-template name="mul1000NoZero"><xsl:with-param name="v" select="$bar/xpdm:Property[@name='Kit_StockWidth']/xpdm:Value"/></xsl:call-template>		
				<!-- xsl:value-of select="$bar/xpdm:Property[@name='Kit_StockWidth']/xpdm:Value" / -->
			</xsl:attribute>

			<xsl:attribute name="Kit_StockLength">
				<xsl:call-template name="mul1000NoZero"><xsl:with-param name="v" select="$bar/xpdm:Property[@name='Kit_StockLength']/xpdm:Value"/></xsl:call-template>		
				<!-- xsl:value-of select="$bar/xpdm:Property[@name='Kit_StockLength']/xpdm:Value" / -->
			</xsl:attribute>

		</xsl:element>
		
	</xsl:if>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="decodeUnit" >
	<xsl:param name="v"/>
	
	<xsl:choose>
		<xsl:when test="$v='1'">м2</xsl:when>
		<xsl:when test="$v='2'">кг</xsl:when>
		<xsl:when test="$v='3'">м.п.</xsl:when>
		<xsl:when test="$v='4'">шт</xsl:when>
	</xsl:choose>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getRoute" >
	<xsl:variable name="route" select="$GLOBAL_PPS/xpdm:GeneralSystem[xpdm:Route]/xpdm:Route" />
	<xsl:variable name="items" select="$route/xpdm:RouteItem[xpdm:Role and xpdm:Date]" />

	<xsl:element name="Route">
	
		<xsl:if test="count($items) = 0" > <!-- return dummy route -->
			<xsl:call-template name="makeDummyRouteItem"> <xsl:with-param name="index" select="1" /> <xsl:with-param name="role" select="'drw_developer'" /> </xsl:call-template>
			<xsl:call-template name="makeDummyRouteItem"> <xsl:with-param name="index" select="2" /> </xsl:call-template>
			<xsl:call-template name="makeDummyRouteItem"> <xsl:with-param name="index" select="3" /> </xsl:call-template>
			<xsl:call-template name="makeDummyRouteItem"> <xsl:with-param name="index" select="4" /> </xsl:call-template>
			<xsl:call-template name="makeDummyRouteItem"> <xsl:with-param name="index" select="5" /> <xsl:with-param name="role" select="'drw_ncontr'" /> </xsl:call-template>
		</xsl:if>
	
		<xsl:for-each select="$items">
			<xsl:sort data-type="number" select="@index" />
			<xsl:element name="RouteItem">
				<xsl:attribute name="index"><xsl:value-of select="position()" /></xsl:attribute>
				<xsl:attribute name="role"><xsl:value-of select="current()/xpdm:Role" /></xsl:attribute>
				<xsl:attribute name="lastName"><xsl:value-of select="current()/xpdm:LastName" /></xsl:attribute>
				<xsl:attribute name="firstName"><xsl:value-of select="current()/xpdm:FirstName" /></xsl:attribute>
				<xsl:attribute name="signature"><xsl:value-of select="current()/xpdm:Signature" /></xsl:attribute>

				<xsl:attribute name="date">
					<xsl:if test="string-length(xpdm:Date)" >
						<xsl:value-of select="substring(xpdm:Date,9,2)"/>-<xsl:value-of select="substring(xpdm:Date,6,2)"/>-<xsl:value-of select="substring(xpdm:Date,1,4)"/>
					</xsl:if>
				</xsl:attribute>
			</xsl:element>
		</xsl:for-each>
	</xsl:element>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="makeDummyRouteItem" >
	<xsl:param name="index"/>
	<xsl:param name="role"/>

	<xsl:element name="RouteItem">
		<xsl:attribute name="index"><xsl:value-of select="$index" /></xsl:attribute>
		<xsl:attribute name="role"><xsl:value-of select="$role" /></xsl:attribute>
	</xsl:element>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getAllOperations" >
	<xsl:for-each select="$GLOBAL_PPS/xpdm:HeaderOperation">
		<xsl:call-template name="getOperation">
			<xsl:with-param name="operationId" select="@id" />
			<xsl:with-param name="sortValue" select="xpdm:Property[@name='Kit_NumOperation']" />
		</xsl:call-template>
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getAllShops" >

	<xsl:for-each select="$GLOBAL_PPS/xpdm:GeneralSystem[@mappingType='Kit_ShopFloor']">

		<xsl:element name="GeneralSystem" >
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
		</xsl:element>
		
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getShopOperations" >
	<xsl:param name="shopId" />

	<xsl:variable name="shop"  select="$GLOBAL_PPS/xpdm:GeneralSystem[@id=$shopId]/@id" />
	<xsl:variable name="sub1"  select="$GLOBAL_PPS/xpdm:SystemInst[xpdm:Owned=$shopId]/xpdm:Instancing" />
	<xsl:variable name="sub2"  select="$GLOBAL_PPS/xpdm:SystemInst[xpdm:Owned=$sub1]/xpdm:Instancing" />
	<xsl:variable name="system"  select="$shop|$sub1|$sub2" />
	<xsl:variable name="operationInstances"  select="$GLOBAL_PPS/xpdm:OperationInst[xpdm:Owned=$system]" />

	<xsl:for-each select="$operationInstances">
		<xsl:call-template name="getOperation">
			<xsl:with-param name="operationId" select="xpdm:Instancing" />
			<xsl:with-param name="sortValue" select="xpdm:ChildOrder" />
		</xsl:call-template>
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getOperation" >
	<xsl:param name="operationId" />
	<xsl:param name="sortValue" />

	<xsl:variable name="_operation"  select="$GLOBAL_PPS/xpdm:HeaderOperation[@id=$operationId]" />

	<xsl:element name="HeaderOperation" >
		<xsl:attribute name="id"><xsl:value-of select="$operationId" /></xsl:attribute>
		<xsl:attribute name="sortValue"><xsl:value-of select="$sortValue" /></xsl:attribute>
		<xsl:attribute name="V_Name"><xsl:value-of select="$_operation/xpdm:Name" /></xsl:attribute>
		<xsl:attribute name="Kit_Control"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_Control']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_Kpcs"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_Kpcs']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_MechanizationCode"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_MechanizationCode']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_NumOperation"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_NumOperation']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_NumSimultaneousManufParts"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_NumSimultaneousManufParts']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_OperationCode"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_OperationCode']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_Percentage"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_Percentage']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_To"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_To']/xpdm:Value" /></xsl:attribute>


		<xsl:attribute name="Kit_PrepTime">
			<xsl:call-template name="timeSecToMinNoZero"><xsl:with-param name="v" select="$_operation/xpdm:Property[@name='Kit_PrepTime']/xpdm:Value"/></xsl:call-template>		
		</xsl:attribute>
		
		<xsl:attribute name="Kit_Tpcs">
			<xsl:call-template name="timeSecToMinNoZero"><xsl:with-param name="v" select="$_operation/xpdm:Property[@name='Kit_Tpcs']/xpdm:Value"/></xsl:call-template>		
		</xsl:attribute>

		<xsl:attribute name="Kit_Tv"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_Tv']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_VolProdBatchPieces"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_VolProdBatchPieces']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_WorkConditionCode"><xsl:value-of select="$_operation/xpdm:Property[@name='Kit_WorkConditionCode']/xpdm:Value" /></xsl:attribute>
	</xsl:element>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="concatIotNames" >
	<xsl:param name="operationId" />

	<xsl:variable name="instructionIds"  select="$GLOBAL_PPS/xpdm:InstructionInst[xpdm:Owned=$operationId]/xpdm:Instancing" />
	<xsl:variable name="docRef"  select="$GLOBAL_ROOT/xpdm:MultiStructure/xpdm:DocumentReferenceRelationship[xpdm:Owned=$operationId or xpdm:Owned=$instructionIds]" />
	<xsl:variable name="documents"  select="$GLOBAL_ROOT/xpdm:MultiStructure/xpdm:Document[@id=$docRef/xpdm:Instancing]" />
	
	<xsl:variable name="accum" >
		<xsl:for-each select="$documents" >
		
			<xsl:variable name="document"  select="current()" />
			<xsl:variable name="file"  select="$GLOBAL_ROOT/xpdm:File[@id=$document/xpdm:FileRef]" />
			
			<xsl:if test="contains(translate($file/xpdm:Location,'ИОТ','иот'),'иот')" >

				<xsl:value-of select="'; '" />
				<xsl:call-template name="stripRevision"> <xsl:with-param name="str" select="xpdm:Name"/> </xsl:call-template>
			</xsl:if>
			
		</xsl:for-each>
		
	</xsl:variable>
	
	<xsl:value-of select="substring($accum,3)" />

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getImages" >
	<xsl:param name="operationId" />

	<xsl:variable name="instructionIds"  select="$GLOBAL_PPS/xpdm:InstructionInst[xpdm:Owned=$operationId]/xpdm:Instancing" />
	<xsl:variable name="docRef"  select="$GLOBAL_ROOT/xpdm:MultiStructure/xpdm:DocumentReferenceRelationship[xpdm:Owned=$operationId or xpdm:Owned=$instructionIds]" />
	<xsl:variable name="documents"  select="$GLOBAL_ROOT/xpdm:MultiStructure/xpdm:Document[@id=$docRef/xpdm:Instancing]" />
	
	<xsl:for-each select="$documents" >
	
		<xsl:variable name="document"  select="current()" />
		<xsl:variable name="file"  select="$GLOBAL_ROOT/xpdm:File[@id=$document/xpdm:FileRef]" />
		
		<xsl:if test="not(contains(translate($file/xpdm:Location,'ИОТ','иот'),'иот'))
			and not(contains(translate($file/xpdm:Location,'XML','xml'),'.xml'))
			and not(contains(translate($file/xpdm:Location,'HTM','htm'),'.htm'))
			and not(contains(translate($file/xpdm:Location,'DOC','doc'),'.doc'))" >

			<xsl:element name="Image" >
				<xsl:attribute name="id"><xsl:value-of select="$file/@id" /></xsl:attribute>
				<xsl:attribute name="Name"><xsl:value-of select="$file/xpdm:Name" /></xsl:attribute>
				<xsl:attribute name="FileName"><xsl:value-of select="$file/xpdm:Location" /></xsl:attribute>
			</xsl:element>
		</xsl:if>
		
	</xsl:for-each>
	
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getInstructions" >
	<xsl:param name="operationId" />

	<xsl:for-each select="$GLOBAL_PPS/xpdm:InstructionInst[xpdm:Owned=$operationId]" >
		
		<xsl:call-template name="getInstruction" >
			<xsl:with-param name="instructionId" select="xpdm:Instancing" />
			<xsl:with-param name="_index" select="xpdm:ChildOrder" />
		</xsl:call-template>
		
	</xsl:for-each>
	
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getInstruction" >
	<xsl:param name="instructionId" />
	<xsl:param name="_index" />

	
	<xsl:variable name="_instruction"  select="$GLOBAL_PPS/xpdm:Instruction[@id=$instructionId]" />

	<xsl:element name="Instruction" >
		<xsl:attribute name="id"><xsl:value-of select="$_instruction/@id" /></xsl:attribute>
		<xsl:attribute name="Kit_TransitionNumber"><xsl:value-of select="$_instruction//xpdm:Property[@name='Kit_TransitionNumber']/xpdm:Value" /></xsl:attribute>

		<xsl:attribute name="_index"><xsl:value-of select="$_index" /></xsl:attribute>

		<xsl:variable name="rawText">
			<xsl:call-template name="stripTags">
				<xsl:with-param name="s" select="$_instruction/xpdm:Text" />
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:attribute name="_instruction_text">
			<xsl:if test="substring($rawText,1,12)='Html export '"><xsl:value-of select="substring($rawText,13)"/></xsl:if>
			<xsl:if test="substring($rawText,1,12)!='Html export '"><xsl:value-of select="$rawText"/></xsl:if>
		</xsl:attribute>
		
	</xsl:element>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getMachine" >
	<xsl:param name="operationId" />

	<xsl:variable name="instructionIds"  select="$GLOBAL_PPS/xpdm:InstructionInst[xpdm:Owned=$operationId]/xpdm:Instancing" />

	<xsl:variable name="instances" select="$GLOBAL_CR[xpdm:Owned=$operationId  or xpdm:Owned=$instructionIds]" />

	<xsl:variable name="machines" select="$GLOBAL_RS/xpdm:IndustrialMachine[@id=$instances/xpdm:ResourceRef] | $GLOBAL_RS/xpdm:NCMachine[@id=$instances/xpdm:ResourceRef]" />
	<xsl:variable name="machine" select="$machines[1]" />

	<xsl:element name="Machine" >
		<xsl:attribute name="id"><xsl:value-of select="$machine/@id" /></xsl:attribute>
			<xsl:attribute name="V_Name"><xsl:value-of select="$machine/xpdm:Name" /></xsl:attribute>
			<xsl:attribute name="V_description"><xsl:value-of select="$machine/xpdm:Description" /></xsl:attribute>
	</xsl:element>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getNonMachines" >
	<xsl:param name="operationId" />

	<!-- xsl:message>getNonMachines: operationId=<xsl:value-of select="$operationId"/></xsl:message -->

	<xsl:for-each select="$GLOBAL_CR[xpdm:Owned=$operationId]" >
		
		<xsl:variable name="instance" select="current()" />

		<!-- xsl:message>getNonMachines: operationId=<xsl:value-of select="$operationId"/> instance=<xsl:value-of select="($instance)"/></xsl:message -->
		
		<xsl:variable name="resource" select="$GLOBAL_RS/*[@id=$instance/xpdm:ResourceRef]" />
		
		<xsl:if test="local-name($resource)!='NCMachine'
						and local-name($resource)!='IndustrialMachine'
						and local-name($resource)!='Worker'" >

			<!-- xsl:message>getNonMachines: operationId=<xsl:value-of select="$operationId"/> resource=<xsl:value-of select="($resource)"/></xsl:message -->
						
			<xsl:element name="Resource" >
				<xsl:attribute name="id"><xsl:value-of select="$resource/@id" /></xsl:attribute>
				<xsl:attribute name="ResourceQuantity"><xsl:value-of select="$instance/xpdm:ResourcesQuantity" /></xsl:attribute>
				<xsl:attribute name="Kit_Category"><xsl:value-of select="$resource//xpdm:Property[@name='Kit_Category']/xpdm:Value" /></xsl:attribute>
				<xsl:attribute name="V_Name"><xsl:value-of select="$resource/xpdm:Name" /></xsl:attribute>
				<xsl:attribute name="V_description"><xsl:value-of select="$resource/xpdm:Description" /></xsl:attribute>
			</xsl:element>
			
		</xsl:if>
		
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getWorkers" >
	<xsl:param name="operationId" />

	<xsl:variable name="instructionIds"  select="$GLOBAL_PPS/xpdm:InstructionInst[xpdm:Owned=$operationId]/xpdm:Instancing" />

	<xsl:for-each select="$GLOBAL_CR[xpdm:Owned=$operationId or xpdm:Owned=$instructionIds]" >
		
		<xsl:variable name="instance" select="current()" />
		
		<xsl:variable name="resource" select="$GLOBAL_RS/xpdm:Worker[@id=$instance/xpdm:ResourceRef]" />
		
		<xsl:if test="$resource" >
						
			<xsl:element name="Worker" >
				<xsl:attribute name="id"><xsl:value-of select="$resource/@id" /></xsl:attribute>
				<xsl:attribute name="ResourceQuantity"><xsl:value-of select="$instance/xpdm:ResourcesQuantity" /></xsl:attribute>
				<xsl:attribute name="Kit_ProfessionCode"><xsl:value-of select="$resource//xpdm:Property[@name='Kit_ProfessionCode']/xpdm:Value" /></xsl:attribute>
				<xsl:attribute name="Kit_JobRank"><xsl:value-of select="$resource//xpdm:Property[@name='Kit_JobRank']/xpdm:Value" /></xsl:attribute>
				<xsl:attribute name="Kit_NumWorkers"><xsl:value-of select="$resource//xpdm:Property[@name='Kit_NumWorkers']/xpdm:Value" /></xsl:attribute>
				<xsl:attribute name="Kit_Category"><xsl:value-of select="$resource//xpdm:Property[@name='Kit_Category']/xpdm:Value" /></xsl:attribute>
			</xsl:element>
			
		</xsl:if>
		
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getMainMaterial" >

	<xsl:if test="not($is_assembly)">

		<xsl:variable name="materials" select="$GLOBAL_PTS/xpdm:ProvidedPart" />

		<xsl:if test="count($materials) &gt; 0 " >
		
			<xsl:variable name="material" select="$materials[1]" />
			
			<xsl:element name="Material" >
				<xsl:attribute name="id"><xsl:value-of select="$material/@id" /></xsl:attribute>
				<xsl:attribute name="Kit_M_PartNumber"><xsl:value-of select="$material//xpdm:Property[@name='Kit_M_PartNumber']/xpdm:Value" /></xsl:attribute>
				<xsl:attribute name="Kit_MatCode"><xsl:value-of select="$material//xpdm:Property[@name='Kit_MatCode']/xpdm:Value" /></xsl:attribute>
				<xsl:attribute name="V_ContQuantity"><xsl:value-of select="$material//xpdm:Property[@name='V_ContQuantity']/xpdm:Value" /></xsl:attribute>
				<xsl:attribute name="V_description"><xsl:value-of select="$material/xpdm:Description" /></xsl:attribute>
				<xsl:attribute name="V_Name"><xsl:value-of select="$material/xpdm:Name" /></xsl:attribute>
			</xsl:element>
			
		</xsl:if>
		
	</xsl:if>
	
</xsl:template>


<!-- ============================================================================================================ -->

<xsl:template name="returnMaterial" >
	<xsl:param name="material" />
	<xsl:param name="nrash" />

	<xsl:element name="ProvideProcess" >
		<xsl:attribute name="id"><xsl:value-of select="$material/@id" /></xsl:attribute>

		<xsl:attribute name="Kit_M_PartNumber"><xsl:value-of select="$material//xpdm:Property[@name='Kit_M_PartNumber']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="Kit_MatCode"><xsl:value-of select="$material//xpdm:Property[@name='Kit_MatCode']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="V_ContQuantity"><xsl:value-of select="$material//xpdm:Property[@name='V_ContQuantity']/xpdm:Value" /></xsl:attribute>
		<xsl:attribute name="V_description"><xsl:value-of select="$material/xpdm:Description" /></xsl:attribute>
		<xsl:attribute name="V_Name"><xsl:value-of select="$material/xpdm:Name" /></xsl:attribute>

		<xsl:attribute name="QuantityValue">
			<xsl:call-template name="getMaterialQuantityValue"><xsl:with-param name="material" select="$material"/></xsl:call-template>
		</xsl:attribute>

		<xsl:attribute name="QuantityUnit">
			<xsl:call-template name="getMaterialQuantityUnit"><xsl:with-param name="material" select="$material"/></xsl:call-template>
		</xsl:attribute>

		<xsl:attribute name="nrash"><xsl:value-of select="$nrash" /></xsl:attribute>
		
	</xsl:element>
	
</xsl:template>

<!-- ================================================== -->

<xsl:template name="getMaterialQuantityValue" >
	<xsl:param name="material" />
	<xsl:choose>
		<xsl:when test="$material/xpdm:AreaQuantity"><xsl:value-of select="$material/xpdm:AreaQuantity"/></xsl:when>
		<xsl:when test="$material/xpdm:LengthQuantity"><xsl:value-of select="$material/xpdm:LengthQuantity"/></xsl:when>
		<xsl:when test="$material/xpdm:MassQuantity"><xsl:value-of select="$material/xpdm:MassQuantity"/></xsl:when>
		<xsl:when test="$material/xpdm:VolumeQuantity"><xsl:value-of select="$material/xpdm:VolumeQuantity"/></xsl:when>
	</xsl:choose>
</xsl:template>


<!-- ================================================== -->

<xsl:template name="getMaterialQuantityUnit" >
	<xsl:param name="material" />
	<xsl:choose>
		<xsl:when test="$material/xpdm:AreaQuantity">м2</xsl:when>
		<xsl:when test="$material/xpdm:LengthQuantity">м</xsl:when>
		<xsl:when test="$material/xpdm:MassQuantity">кг</xsl:when>
		<xsl:when test="$material/xpdm:VolumeQuantity">м3</xsl:when>
	</xsl:choose>
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getAllMaterials" >

	<xsl:for-each select="$GLOBAL_PTS/xpdm:ContinuousProvidedMaterial" >

		<xsl:variable name="material" select="current()" />
		<xsl:variable name="instance" select="$GLOBAL_PTS/xpdm:TransformationInst[xpdm:Instancing=$material/@id]" />

		<xsl:call-template name="returnMaterial">
			<xsl:with-param name="material" select="$material"/>
			<xsl:with-param name="instance" select="$instance"/>
		</xsl:call-template>
		
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getMaterials" >
	<xsl:param name="operationIds" />

	<!-- xsl:message>getMaterials1</xsl:message -->

	<xsl:variable name="operationInstanceIds" select="$GLOBAL_PPS/xpdm:OperationInst[xpdm:Instancing=$operationIds]/@id" />
	<!-- xsl:message>operationInstanceIds:<xsl:value-of select="count($operationInstanceIds)"/></xsl:message -->
	<xsl:variable name="_processInstanceIds" select="$GLOBAL_PPS/xpdm:SystemImplementLink[xpdm:OperationRef/xpdm:PathItem=$operationInstanceIds]/xpdm:TransformationInstanceRef/xpdm:PathItem[position()=last()]"/>

	<xsl:variable name="_processIds" select="$GLOBAL_PTS/*[@id=$_processInstanceIds]/xpdm:Instancing"/>
	<xsl:variable name="_processes" select="$GLOBAL_PTS/*[@id=$_processIds]"/>

	<!-- xsl:message>_processInstanceIds:<xsl:value-of select="count($_processInstanceIds)"/></xsl:message -->

	<xsl:for-each select="$_processes" >
		<xsl:variable name="process" select="current()"/>
		<xsl:variable name="_processId" select="$process/@id"/>

		<xsl:variable name="_instanceIds" select="$GLOBAL_PTS/xpdm:TransformationInst[xpdm:Instancing=$_processId]/@id"/>

		<!-- xsl:message>
			_processId:<xsl:value-of select="$_processId"/>
			_SILs:<xsl:value-of select="count($_SILs)"/>
			_instanceIds2:<xsl:value-of select="count($_instanceIds2)"/>
		</xsl:message -->


		<xsl:if test="local-name($process) = 'ContinuousProvidedMaterial'">
						
			<xsl:call-template name="returnMaterial">
				<xsl:with-param name="material" select="$process"/>
				<xsl:with-param name="nrash">
					<xsl:call-template name="countNRash" >
						<xsl:with-param name="process" select="$process"/>
						<xsl:with-param name="operationInstanceIds" select="$operationInstanceIds" />
						<xsl:with-param name="processInstanceIds" select="$_instanceIds" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			
		</xsl:if>
		
	</xsl:for-each>

	<!-- xsl:message>getMaterials2</xsl:message -->

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="countNRash" >
	<xsl:param name="process" />
	<xsl:param name="operationInstanceIds" />
	<xsl:param name="processInstanceIds" />

	<xsl:variable name="SILs" select="$GLOBAL_PPS/xpdm:SystemImplementLink[xpdm:OperationRef/xpdm:PathItem=$operationInstanceIds and xpdm:TransformationInstanceRef/xpdm:PathItem=$processInstanceIds]" />
	<!-- xsl:variable name="_instanceIds2" select="$_instanceIds[$_SILs/xpdm:TransformationInstanceRef/xpdm:PathItem]"/ -->
	
	<!-- xsl:message>
		countNRash
		operationInstanceIds: <xsl:value-of select="count($operationInstanceIds)"/> : <xsl:value-of select="$operationInstanceIds[1]"/>, <xsl:value-of select="$operationInstanceIds[2]"/>, ...
		processInstanceIds: <xsl:value-of select="count($processInstanceIds)"/> : <xsl:value-of select="$processInstanceIds[1]"/>, <xsl:value-of select="$processInstanceIds[2]"/>, ...
		SILs: <xsl:value-of select="count($SILs)"/> : <xsl:value-of select="$SILs[1]/@id"/>, <xsl:value-of select="$SILs[2]/@id"/>, ...
	</xsl:message -->

	
	<xsl:variable name="_nrashs0">
		<xsl:for-each select="$SILs">
			<xsl:variable name="SIL" select="current()" />
			<xsl:variable name="processInstanceId" select="$processInstanceIds[self::node()=$SIL/xpdm:TransformationInstanceRef/xpdm:PathItem]"/>
			<xsl:variable name="processInstance" select="$GLOBAL_PTS/*[@id=$processInstanceId]"/>

			<xsl:if test="count($processInstance/xpdm:UsageCoefficient)" >
				<xsl:variable name="usageCoeffValue" select="translate( $processInstance/xpdm:UsageCoefficient, ',', '.' )" />
				<!-- xsl:message>usageCoeffValue:<xsl:value-of select="$usageCoeffValue"/></xsl:message -->
				<xsl:variable name="QuantityValue">
					<xsl:call-template name="getMaterialQuantityValue"><xsl:with-param name="material" select="$process"/></xsl:call-template>
				</xsl:variable>
				<!-- xsl:message>QuantityValue:<xsl:value-of select="$QuantityValue"/></xsl:message -->
				<xsl:variable name="nrash" select="format-number($usageCoeffValue*$QuantityValue,'0.###')" />
				<!-- xsl:message>nrash:<xsl:value-of select="$nrash"/></xsl:message -->
				<xsl:element name="nrash"><xsl:value-of select="$nrash"/></xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="_nrashs" select="exsl:node-set($_nrashs0)/*"/>

	<!-- xsl:message>
		_nrashs: <xsl:value-of select="count($_nrashs)"/> : <xsl:value-of select="$_nrashs[1]"/>, <xsl:value-of select="$_nrashs[2]"/>, ...
		sum(_nrashs): <xsl:value-of select="sum($_nrashs)"/>
	</xsl:message -->
	
	<xsl:value-of select="sum($_nrashs)"/>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getComponents" >
	<xsl:param name="operationId" />
	<xsl:param name="ProcessType" />
	<xsl:param name="mappingType" />

	<xsl:variable name="operationInstanceId" select="$GLOBAL_PPS/xpdm:OperationInst[xpdm:Instancing=$operationId]/@id" />
	<xsl:variable name="_processInstanceIds" select="$GLOBAL_PPS/xpdm:SystemImplementLink[xpdm:OperationRef/xpdm:PathItem=$operationInstanceId]/xpdm:TransformationInstanceRef/xpdm:PathItem[position()=last()]"/>

	<xsl:variable name="_processIds" select="$GLOBAL_PTS/xpdm:TransformationInst[@id=$_processInstanceIds]/xpdm:Instancing"/>
	<xsl:variable name="_processes" select="$GLOBAL_PTS/*[@id=$_processIds]"/>

	<xsl:for-each select="$_processes" >
		<xsl:variable name="process" select="current()"/>
		<xsl:variable name="_processId" select="$process/@id"/>
	
		<xsl:variable name="_instanceIds" select="$GLOBAL_PTS/xpdm:TransformationInst[xpdm:Instancing=$_processId]/@id"/>
		<xsl:variable name="_SILs" select="$GLOBAL_PPS/xpdm:SystemImplementLink[xpdm:OperationRef/xpdm:PathItem=$operationInstanceId and xpdm:TransformationInstanceRef/xpdm:PathItem=$_instanceIds]" />
		<xsl:variable name="_instanceIds2" select="$_instanceIds[self::node()=$_SILs/xpdm:TransformationInstanceRef/xpdm:PathItem]"/>

		<xsl:if test="$process/@mappingType = $mappingType">
						
			<xsl:element name="Component" >
				<xsl:attribute name="id"><xsl:value-of select="$_processId" /></xsl:attribute>
				<xsl:attribute name="V_Name"><xsl:value-of select="$process/xpdm:Name" /></xsl:attribute>
				<xsl:attribute name="V_description"><xsl:value-of select="$process/xpdm:Description" /></xsl:attribute>
				<xsl:attribute name="Kit_M_PartNumber"><xsl:value-of select="$process//xpdm:Property[@name='Kit_M_PartNumber']/xpdm:Value" /></xsl:attribute>
				<xsl:attribute name="count"><xsl:value-of select="count($_instanceIds2)" /></xsl:attribute>
			</xsl:element>
			
		</xsl:if>
		
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->

<!-- ####### TODO ####### -->
<xsl:template name="getTopProduct" >
	
	<xsl:variable name="_process0"> <xsl:call-template name="getTopProcess" /> </xsl:variable>
	<xsl:variable name="_process" select="exsl:node-set($_process0)/*"/>

	<!-- xsl:variable name="top_title" select="$_process/@V_Name" />
	<xsl:variable name="_product" select="$GLOBAL_PS/xpdm:Product[xpdm:Name=$top_title]" / -->

	<xsl:variable name="scopeId" select="$_process/@scopeId" />
	<xsl:variable name="_product" select="$GLOBAL_PS/xpdm:Product[@id=$scopeId]" />
	
	
	<xsl:element name="Product" >
		
		<xsl:attribute name="id"><xsl:value-of select="$_product/@id" /></xsl:attribute>
		
		<xsl:attribute name="V_WCG_Mass">
			<xsl:choose>
				<xsl:when test="string-length($_product/xpdm:Property[@name='XP_Mass']/xpdm:Value) != 0">
					<xsl:value-of select="$_product/xpdm:Property[@name='XP_Mass']/xpdm:Value" />
				</xsl:when>
				<xsl:when test="string-length($_product/xpdm:Property[@name='V_WCG_Declared_Mass']/xpdm:Value) != 0">
					<xsl:value-of select="$_product/xpdm:Property[@name='V_WCG_Declared_Mass']/xpdm:Value" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$_product/xpdm:Property[@name='V_WCG_Mass']/xpdm:Value" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
			
	</xsl:element>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getTopProcess" >

	<xsl:variable name="rootId" select="$GLOBAL_PTS/@rootRefs"/>

	<xsl:variable name="process" select="$GLOBAL_PTS/*[@id=$rootId]" />

	<xsl:if test="$process" >
		<xsl:element name="{local-name($process)}" >
			<xsl:attribute name="id"><xsl:value-of select="$process/@id" /></xsl:attribute>
			<xsl:attribute name="V_Name"><xsl:value-of select="$process/xpdm:Name" /></xsl:attribute>
			<xsl:attribute name="V_description"><xsl:value-of select="$process/xpdm:Description" /></xsl:attribute>
			<xsl:attribute name="scopeId"><xsl:value-of select="$process/xpdm:ProductImplementScopeRef" /></xsl:attribute>
		</xsl:element>
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->
<xsl:variable name="enablePartNumberComment"      select="1" />
<!-- ============================================================================================================ -->
<xsl:variable name="enablePageMK"      select="1" />
<xsl:variable name="enablePageImage"   select="1" />
<xsl:variable name="enablePageControl" select="1" />
<xsl:variable name="enablePageVO"      select="1" />
<xsl:variable name="enablePageVM"      select="1" />
<!-- ============================================================================================================ -->
<xsl:variable name="skip_images" select="0" />
<xsl:variable name="use_debug_mark" select="0" />
<xsl:variable name="use_debug_background" select="0" />
<!-- ============================================================================================================ -->
<xsl:variable name="markerLen"  select="15"/>
<xsl:variable name="rootMarker"  select="substring('xxxxxxxxxxxxxxxxxxxx',1,$markerLen)" />
<!-- ============================================================================================================ -->
<xsl:variable name="fontFamily"  select="'Gost'" />
<!-- xsl:variable name="fontFamily"  select="'Arial'" / -->
<xsl:variable name="fontSize"  select="'12pt'" />
<xsl:variable name="fontStyle"  select="'italic'" /><!-- normal / italic -->
<!-- ============================================================================================================ -->

<xsl:template match="/">

	<!-- ======================================================================================== -->
	
	<xsl:variable name="forms0">

		<xsl:call-template name="mkGenerate" />

		<xsl:variable name="_operations0"><xsl:call-template name="getAllOperations"/></xsl:variable>
		<xsl:variable name="_operations" select="exsl:node-set($_operations0)/*"/>
		
		<xsl:for-each select="$_operations">
			<xsl:sort data-type="number" select="@sortValue" />
			<xsl:variable name="operationId"  select="@id"/>
			
			<xsl:call-template name="imagesGenerate" >
				<xsl:with-param name="operationId" select="$operationId" />
			</xsl:call-template>
			
			<xsl:call-template name="controlGenerate" >
				<xsl:with-param name="operationId" select="$operationId" />
			</xsl:call-template>
			
		</xsl:for-each>
		
		<xsl:variable name="_operations0"><xsl:call-template name="getAllOperations"/></xsl:variable>
		<xsl:variable name="_operations" select="exsl:node-set($_operations0)/*"/>
		
		<xsl:call-template name="voGenerate" >
			<xsl:with-param name="operations" select="$_operations" />
		</xsl:call-template>
		
		<!--
		<xsl:variable name="_shops0"><xsl:call-template name="getAllShops"/></xsl:variable>
		<xsl:variable name="_shops" select="exsl:node-set($_shops0)/*"/>
		
		<xsl:for-each select="$_shops" >
			
			<xsl:variable name="shopId" select="@id"/>
			
			<xsl:variable name="_operations0"><xsl:call-template name="getShopOperations"><xsl:with-param name="shopId" select="$shopId"/></xsl:call-template></xsl:variable>
			<xsl:variable name="_operations" select="exsl:node-set($_operations0)/*"/>
			
			<xsl:call-template name="voGenerate" >
				<xsl:with-param name="shopId" select="$shopId" />
				<xsl:with-param name="operations" select="$_operations" />
			</xsl:call-template>
		</xsl:for-each>
		-->

		<xsl:call-template name="vmGenerate" />
		
	</xsl:variable>
	<xsl:variable name="forms" select="exsl:node-set($forms0)/*"/>
	
	<!-- ======================================================================================== -->
	<!-- xsl:copy-of select="$forms" / -->
	<!-- ======================================================================================== -->

	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" >

		<fo:layout-master-set>
		
			<fo:simple-page-master master-name="blank_page" margin-right="5.5mm" margin-left="5.5mm" margin-top="4.3mm" margin-bottom="3.5mm" page-width="297mm" page-height="210mm">
				<fo:region-body region-name="region-flow"  margin-top="0mm"  margin-bottom="0mm" />
				<fo:region-end region-name="region-static" extent="286mm" />
			</fo:simple-page-master>

			<fo:page-sequence-master master-name="blank_sequence">
			  <fo:repeatable-page-master-reference master-reference="blank_page"/>
			</fo:page-sequence-master>
			
				
		</fo:layout-master-set>
		
		<fo:page-sequence initial-page-number="1" force-page-count="no-force" master-reference="blank_sequence" font-family="{$fontFamily}" font-size="{$fontSize}" font-style="{$fontStyle}" >
			<fo:flow flow-name="region-flow" >

			<xsl:call-template name="writeForms" >
				<xsl:with-param name="forms" select="$forms" />
			</xsl:call-template>

			</fo:flow>
		</fo:page-sequence>
		
	</fo:root>

	<!-- ======================================================================================== -->

</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="writeForms" >
	<xsl:param name="forms" />
	
	<xsl:for-each select="$forms" >
	
		<xsl:variable name="form" select="current()" />
		<xsl:variable name="globalPageNumber" select="position()" />

		<xsl:choose>
			
			<xsl:when test="$form/@_type='MK'">
				<xsl:call-template name="writeMK" >
					<xsl:with-param name="form" select="$form" />
					<xsl:with-param name="globalPageNumber" select="$globalPageNumber" />
				</xsl:call-template>
			</xsl:when>
			
			<xsl:when test="$form/@_type='KE'">
				<xsl:call-template name="writeImage" >
					<xsl:with-param name="form" select="$form" />
					<xsl:with-param name="globalPageNumber" select="$globalPageNumber" />
				</xsl:call-template>
			</xsl:when>
			
			<xsl:when test="$form/@_type='KK'">
				<xsl:call-template name="writeControl" >
					<xsl:with-param name="form" select="$form" />
					<xsl:with-param name="globalPageNumber" select="$globalPageNumber" />
				</xsl:call-template>
			</xsl:when>
			
			<xsl:when test="$form/@_type='VO'">
				<xsl:call-template name="writeVO" >
					<xsl:with-param name="form" select="$form" />
					<xsl:with-param name="globalPageNumber" select="$globalPageNumber" />
				</xsl:call-template>
			</xsl:when>
			
			<xsl:when test="$form/@_type='VM'">
				<xsl:call-template name="writeVM" >
					<xsl:with-param name="form" select="$form" />
					<xsl:with-param name="globalPageNumber" select="$globalPageNumber" />
				</xsl:call-template>
			</xsl:when>

		</xsl:choose>
		
	</xsl:for-each>
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="mkGenerate" >

	<xsl:variable name="bar0"><xsl:call-template name="getBar" /></xsl:variable>
	<xsl:variable name="bar" select="exsl:node-set($bar0)/*"/>

	<xsl:variable name="_material0"> <xsl:call-template name="getMainMaterial" /> </xsl:variable>
	<xsl:variable name="_material" select="exsl:node-set($_material0)/*"/>

	<xsl:variable name="_process0"> <xsl:call-template name="getTopProcess" /> </xsl:variable>
	<xsl:variable name="_process" select="exsl:node-set($_process0)/*"/>

	<xsl:variable name="_product0"> <xsl:call-template name="getTopProduct" /> </xsl:variable>
	<xsl:variable name="_product" select="exsl:node-set($_product0)/*"/>

	<xsl:variable name="_process0"> <xsl:call-template name="getTopProcess" /> </xsl:variable>
	<xsl:variable name="_process" select="exsl:node-set($_process0)/*"/>
	
		<xsl:variable name="_operations0"><xsl:call-template name="getAllOperations"/></xsl:variable>
		<xsl:variable name="_operations" select="exsl:node-set($_operations0)/*"/>

	<xsl:variable name="_rows0">
		<xsl:for-each select="$_operations">
			<xsl:sort data-type="number" select="@sortValue" />
			
			<xsl:call-template name="generateOperation">
				<xsl:with-param name="_operation" select="current()" />
				<xsl:with-param name="isLast" select="position()=last()" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:variable>
	<xsl:variable name="_rows" select="exsl:node-set($_rows0)/*"/>

	<xsl:variable name="nFirstRows">
		<xsl:if test="not($is_assembly)">14</xsl:if><!-- Деталь -->
		<xsl:if test="$is_assembly">15</xsl:if><!-- Сборка -->
	</xsl:variable>
	<xsl:variable name="nNextRows">17</xsl:variable>

	<xsl:variable name="_pages0">
		<xsl:call-template name="rowsToPages">
			<xsl:with-param name="rows" select="$_rows" />
			<xsl:with-param name="nFirstRows" select="$nFirstRows" />
			<xsl:with-param name="nNextRows" select="$nNextRows" />
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="_pages" select="exsl:node-set($_pages0)/*"/>
	
	<!-- xsl:copy-of select="$_pages" / -->

	<xsl:for-each select="$_pages" >
		<xsl:variable name="_page" select="current()"/>

		<xsl:element name="form">
		
			<xsl:attribute name="_type">MK</xsl:attribute>
			<xsl:attribute name="_pages_total"><xsl:value-of select="count($_pages)"/></xsl:attribute>
			<xsl:attribute name="_page_number"><xsl:value-of select="position()"/></xsl:attribute>
			<xsl:attribute name="_is_assembly"><xsl:value-of select="$is_assembly"/></xsl:attribute>
			
			<xsl:attribute name="_use_m1m2"><xsl:value-of select="($n_M1M2_rows &gt; 0) and (position() = 1)"/></xsl:attribute>
			<xsl:attribute name="_use_km"><xsl:value-of select="($n_KM_rows &gt; 0) or (position() &gt; 1)"/></xsl:attribute>

			<xsl:attribute name="_row_delta">
				<xsl:if test="not($is_assembly) and (position()=1)">2</xsl:if>
				<xsl:if test="not( not($is_assembly) and (position()=1) )">0</xsl:if>
			</xsl:attribute>

			<xsl:attribute name="part-name"><xsl:value-of select="$_process/@V_description"/></xsl:attribute>
			<xsl:attribute name="part-code"><xsl:value-of select="$_process/@V_Name"/></xsl:attribute>

			<xsl:attribute name="m01-material"><xsl:value-of select="concat($_material/@V_Name,' ',$_material/@V_description)"/></xsl:attribute>
			<xsl:attribute name="m02-kodmat"><xsl:value-of select="$_material/@Kit_MatCode"/></xsl:attribute>
			
			<xsl:attribute name="m02-ev"><xsl:value-of select="$bar/@unitValue"/></xsl:attribute>
			<xsl:attribute name="m02-md"><xsl:call-template name="stripWeightNoZero"><xsl:with-param name="v" select="$_product/@V_WCG_Mass"/></xsl:call-template></xsl:attribute>
			
			<xsl:attribute name="m02-nrash"><xsl:call-template name="NormNoZero"><xsl:with-param name="v" select="$bar/@Kit_MatConsumptionRate"/></xsl:call-template></xsl:attribute>
			<xsl:attribute name="m02-kim"><xsl:call-template name="NormNoZero"><xsl:with-param name="v" select="$bar/@Kit_UsageCoeffic"/></xsl:call-template></xsl:attribute>
			
			<xsl:attribute name="m02-zagsize"><xsl:call-template name="getBarSize"><xsl:with-param name="bar" select="$bar"/></xsl:call-template></xsl:attribute>
			
			<xsl:attribute name="m02-kd"><xsl:value-of select="$bar/@Kit_NumberOfParts"/></xsl:attribute>
			<xsl:attribute name="m02-mz"><xsl:call-template name="stripWeightNoZero"><xsl:with-param name="v" select="$bar/@Kit_StockWeight"/></xsl:call-template></xsl:attribute>

			<xsl:copy-of select="$_page/*" />
			
		</xsl:element>
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="rowsToPages">
	<xsl:param name="rows" />
	<xsl:param name="nFirstRows" />
	<xsl:param name="nNextRows" />

	<xsl:if test="$rows">

		<xsl:message>rowsToPages:<xsl:value-of select="count($rows)"/>:<xsl:value-of select="$nFirstRows"/>:<xsl:value-of select="$nNextRows"/> [<xsl:value-of select="$rows[1]/@_type"/>]</xsl:message>
		<!-- xsl:call-template name="messageObject">
			<xsl:with-param name="title" select="$rows[1]"/>
			<xsl:with-param name="object" select="$rows[1]"/>
		</xsl:call-template -->
		
		<xsl:variable name="skipFirstLine" select="string-length($rows[1]/@_type)=0" />
		
		<xsl:if test="$skipFirstLine" >

			<xsl:call-template name="rowsToPages">
				<xsl:with-param name="rows" select="$rows[ position() &gt; 1 ]" />
				<xsl:with-param name="nFirstRows" select="$nFirstRows" />
				<xsl:with-param name="nNextRows" select="$nNextRows" />
			</xsl:call-template>

		</xsl:if>

		<xsl:if test="not($skipFirstLine)" >

			<xsl:call-template name="pageOfNRows">
				<xsl:with-param name="rows" select="$rows" />
				<xsl:with-param name="nRows" select="$nFirstRows" />
			</xsl:call-template>

			<xsl:call-template name="rowsToPages">
				<xsl:with-param name="rows" select="$rows[ position() &gt; $nFirstRows ]" />
				<xsl:with-param name="nFirstRows" select="$nNextRows" />
				<xsl:with-param name="nNextRows" select="$nNextRows" />
			</xsl:call-template>

		</xsl:if>
		
	</xsl:if>

</xsl:template>

<!-- xsl:template name="rowsToPages">
	<xsl:param name="rows" />
	<xsl:param name="nFirstRows" />
	<xsl:param name="nNextRows" />

	<xsl:if test="$rows">

		<xsl:message>rowsToPages:<xsl:value-of select="count($rows)"/>:<xsl:value-of select="$nFirstRows"/>:<xsl:value-of select="$nNextRows"/></xsl:message>
		<xsl:call-template name="pageOfNRows">
			<xsl:with-param name="rows" select="$rows" />
			<xsl:with-param name="nRows" select="$nFirstRows" />
		</xsl:call-template>

		<xsl:call-template name="rowsToPages">
			<xsl:with-param name="rows" select="$rows[ position() &gt; $nFirstRows ]" />
			<xsl:with-param name="nFirstRows" select="$nNextRows" />
			<xsl:with-param name="nNextRows" select="$nNextRows" />
		</xsl:call-template>
		
	</xsl:if>

</xsl:template -->

<!-- ============================================================================================================ -->

<xsl:template name="pageOfNRows">
	<xsl:param name="rows" />
	<xsl:param name="nRows" />
	
	<xsl:if test="count($rows[@_type]) &gt; 0">

	<xsl:element name="page" >
		
		<xsl:variable name="realRows" select="$rows[ not( position() &gt; $nRows) ]" />
		<xsl:copy-of select="$realRows" />
		
		<xsl:variable name="nRowsToFill" select="$nRows - count($realRows)" />
		
		<!-- xsl:message>nRowsToFill=<xsl:value-of select="$nRowsToFill"/></xsl:message -->
		
		<xsl:variable name="dummyData" select="$fontGostItalic/*[ not( position() &gt; $nRowsToFill) ]" />
		
		<xsl:for-each select="$dummyData">
			<xsl:element name="row" />
		</xsl:for-each>
		
	</xsl:element>
	
	</xsl:if>
		
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generateOperation" >
	<xsl:param name="_operation" />
	<xsl:param name="isLast" />
	<xsl:param name="sortValue" />

	<!-- xsl:message>generateOperation _operation=<xsl:value-of select="$_operation/@id"/></xsl:message -->

	<xsl:variable name="bar0"><xsl:call-template name="getBar" /></xsl:variable>
	<xsl:variable name="bar" select="exsl:node-set($bar0)/*"/>
	<xsl:variable name="bar" />

	<xsl:variable name="_opId"  select="$_operation/@id"/>

	<!-- Operation -->

	<xsl:variable name="_iotText" ><xsl:call-template name="concatIotNames" ><xsl:with-param name="operationId" select="$_opId"/></xsl:call-template></xsl:variable>

	<!-- xsl:variable name="_iotText" select="concat($_operation/@id,' ',$sortValue)" / -->
	
	<xsl:variable name="workShop"><xsl:call-template name="getWorkShop" ><xsl:with-param name="operationId" select="$_opId"/></xsl:call-template></xsl:variable>
	<xsl:variable name="workArea"><xsl:call-template name="getWorkArea" ><xsl:with-param name="operationId" select="$_opId"/></xsl:call-template></xsl:variable>
	<xsl:variable name="workPlace"><xsl:call-template name="getWorkPlace" ><xsl:with-param name="operationId" select="$_opId"/></xsl:call-template></xsl:variable>

	<!-- ===== WRAP ROW A ==================== -->

	<xsl:variable name="wrap0">
		<xsl:call-template name="wrapTextIterate">
			<xsl:with-param name="font" select="$fontGostItalic" />
			<xsl:with-param name="text" select="$_operation/@V_Name" />
			<xsl:with-param name="maxWidth" select="50" />
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="wrap" select="exsl:node-set($wrap0)/*"/>
	
	<xsl:element name="record" >
		<xsl:attribute name="_type">A</xsl:attribute>
		<xsl:attribute name="workShop"><xsl:call-template name="getWorkShop" ><xsl:with-param name="operationId" select="$_opId"/></xsl:call-template></xsl:attribute>
		<xsl:attribute name="workArea"><xsl:call-template name="getWorkArea" ><xsl:with-param name="operationId" select="$_opId"/></xsl:call-template></xsl:attribute>
		<xsl:attribute name="workPlace"><xsl:call-template name="getWorkPlace" ><xsl:with-param name="operationId" select="$_opId"/></xsl:call-template></xsl:attribute>
		<xsl:attribute name="operationNumber"><xsl:value-of select="substring( $_operation/@Kit_NumOperation, 1, 3 )" /></xsl:attribute>
		<xsl:attribute name="operationCode"  ><xsl:value-of select="$_operation/@Kit_OperationCode"/></xsl:attribute>
		<xsl:attribute name="operationName"  ><xsl:value-of select="$wrap[1]" /></xsl:attribute>
		<xsl:attribute name="iotDocs"><xsl:value-of select="$_iotText" /></xsl:attribute>
	</xsl:element>

	<xsl:for-each select="$wrap[position() &gt; 1]" >
	
		<xsl:element name="record" >
			<xsl:attribute name="_type">A1</xsl:attribute>
			<xsl:attribute name="operationName"  ><xsl:value-of select="current()" /></xsl:attribute>
		</xsl:element>
		
	</xsl:for-each>


	<!-- ===================================== -->
	
	<!-- Machine Resources and Workers -->

	<xsl:call-template name="generateComplexRowB" >
		<xsl:with-param name="_operation" select="$_operation" />
	</xsl:call-template>
	
	<!-- ================= -->

	<xsl:if test="$n_KM_rows &gt; 0" >
	
		<!-- ===================================== -->

		<xsl:call-template name="generateSomeKRows">
			<xsl:with-param name="operationId" select="$_opId" />
			<xsl:with-param name="ProcessType" select="'CreateAssemblyProcess'" />
			<xsl:with-param name="mappingType" select="'Kit_MfgAssembly'" />
		</xsl:call-template>

		<!-- ===================================== -->
		
		<xsl:call-template name="generateSomeKRows">
			<xsl:with-param name="operationId" select="$_opId" />
			<xsl:with-param name="ProcessType" select="'CreateMaterialProcess'" />
			<xsl:with-param name="mappingType" select="'Kit_MfgProducedPart'" />
		</xsl:call-template>

		<!-- ===================================== -->

		<xsl:call-template name="generateSomeKRows">
			<xsl:with-param name="operationId" select="$_opId" />
			<xsl:with-param name="ProcessType" select="'CreateKitProcess'" />
			<xsl:with-param name="mappingType" select="'Kit_MfgKit'" />
		</xsl:call-template>

		<!-- ===================================== -->

		<xsl:call-template name="generateSomeKRows">
			<xsl:with-param name="operationId" select="$_opId" />
			<xsl:with-param name="ProcessType" select="'ProvideProcess'" />
			<!-- xsl:with-param name="mappingType" select="'Kit_MfgRawMaterial'" / -->
			<xsl:with-param name="mappingType" select="'Kit_MfgStandardComponent'" />
		</xsl:call-template>

		<!-- ===================================== -->
		<!-- ===================================== -->

		<xsl:call-template name="generateSomeKRows">
			<xsl:with-param name="operationId" select="$_opId" />
			<xsl:with-param name="ProcessType" select="'ProvideProcess'" />
			<xsl:with-param name="mappingType" select="'Kit_MfgOEMComponent'" />
		</xsl:call-template>

		<!-- ===================================== -->

		<xsl:call-template name="generateSomeKRows">
			<xsl:with-param name="operationId" select="$_opId" />
			<xsl:with-param name="ProcessType" select="'ProvideProcess'" />
			<xsl:with-param name="mappingType" select="'Kit_MfgOEMKit'" />
		</xsl:call-template>

		<!-- ===================================== -->

		<xsl:call-template name="generateSomeKRows">
			<xsl:with-param name="operationId" select="$_opId" />
			<xsl:with-param name="ProcessType" select="'ProvideProcess'" />
			<xsl:with-param name="mappingType" select="'Kit_MfgKitPart'" />
		</xsl:call-template>

		<!-- ===================================== -->

		<xsl:variable name="_materials0"><xsl:call-template name="getMaterials"><xsl:with-param name="operationIds" select="$_operation/@id"/></xsl:call-template></xsl:variable>
		<xsl:variable name="_materials" select="exsl:node-set($_materials0)/*"/>
		
		<xsl:for-each select="$_materials" >
			<xsl:call-template name="generateRowM">
				<xsl:with-param name="title" select="@V_Name" />
				<xsl:with-param name="description" select="@V_description" />
				<xsl:with-param name="material" select="current()" />
			</xsl:call-template>
		</xsl:for-each>
		
	</xsl:if>
	
	<!-- Instruction -->
	
	<xsl:variable name="_instructions0"><xsl:call-template name="getInstructions"><xsl:with-param name="operationId" select="$_opId"/></xsl:call-template></xsl:variable>
	<xsl:variable name="_instructions" select="exsl:node-set($_instructions0)/*"/>
	
	<xsl:for-each select="$_instructions" >
		<xsl:sort data-type="number" select="@_index" />

		<xsl:call-template name="generateInstruction" >
		  <xsl:with-param name="_instruction" select="current()" />
		</xsl:call-template>
	
	</xsl:for-each>

	<!-- Non-machine Resources -->
	
	<xsl:call-template name="generatePossibleRowT" > <xsl:with-param name="operationId" select="$_opId" /> </xsl:call-template>

	<!-- Spacer -->

	<xsl:element name="record" />

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generateInstruction" >
	<xsl:param name="_instruction" />

	<xsl:variable name="instructionText">

		<xsl:if test="string-length($_instruction/@Kit_TransitionNumber)">
			<xsl:value-of select="concat($_instruction/@Kit_TransitionNumber,'.')"/>
		</xsl:if>
		
		<xsl:value-of select="'&#160;'"/>
		
		<xsl:value-of select="$_instruction/@_instruction_text"/>
		
	</xsl:variable>

	
	<xsl:if test="$_instruction/@_instruction_text != ''" >

		<xsl:variable name="wrap0">
			<xsl:call-template name="wrapTextIterate">
				<xsl:with-param name="font" select="$fontGostItalic" />
				<xsl:with-param name="text" select="$instructionText" />
				<xsl:with-param name="maxWidth" select="273" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="wrap" select="exsl:node-set($wrap0)/*"/>

		<xsl:element name="record" >
			<xsl:attribute name="_type">O</xsl:attribute>
			<xsl:attribute name="text"><xsl:value-of select="$wrap[1]" /></xsl:attribute>
		</xsl:element>

		<xsl:for-each select="$wrap[position() &gt; 1]" >
		
			<xsl:element name="record" >
				<xsl:attribute name="_type">O1</xsl:attribute>
				<xsl:attribute name="text"><xsl:value-of select="current()" /></xsl:attribute>
			</xsl:element>
			
		</xsl:for-each>

	</xsl:if>
	
	<xsl:call-template name="generatePossibleRowT" > <xsl:with-param name="operationId" select="$_instruction/@id" /> </xsl:call-template>

</xsl:template>

<!-- ============================================================================================================ -->
<!-- =====  ROW TYPES  ========================================================================================== -->
<!-- ============================================================================================================ -->

<!-- ============================================================================================================ -->


<!-- ============================================================================================================ -->

<xsl:template name="generateComplexRowB" >
	<xsl:param name="_operation" />
	<xsl:param name="standardUnit"/>

		<xsl:variable name="_machine0"><xsl:call-template name="getMachine"><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:variable>
		<xsl:variable name="_machine" select="exsl:node-set($_machine0)/*"/>
		
		<xsl:variable name="_workers0"><xsl:call-template name="getWorkers"><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:variable>
		<xsl:variable name="_workers" select="exsl:node-set($_workers0)/*"/>

		<!-- === if no real workers - select ResourceStructure itself as a stub - always single and no attributes === -->
		<xsl:variable name="_dummy_workers" select="$GLOBAL_ROOT[count($_workers) &lt; 1]" /> 

		<!-- =============== -->
		<xsl:element name="record" >
			<xsl:attribute name="_type">B</xsl:attribute>
			<xsl:attribute name="machineName"><xsl:value-of select="concat($_machine/@V_Name,' ',$_machine/@V_description)" /></xsl:attribute>
				<xsl:attribute name="mechanizationCode"><xsl:value-of select="$_operation/@Kit_MechanizationCode" /></xsl:attribute>
				<xsl:attribute name="workConditionCode"><xsl:value-of select="$_operation/@Kit_WorkConditionCode" /></xsl:attribute>
				<xsl:attribute name="koid"><xsl:value-of select="$_operation/@Kit_NumSimultaneousManufParts" /></xsl:attribute>
			<xsl:attribute name="standardUnit"><xsl:value-of select="$standardUnit"/></xsl:attribute>
			<xsl:attribute name="batchPieces"><xsl:value-of select="$_operation/@Kit_VolProdBatchPieces" /></xsl:attribute>
				<xsl:attribute name="kPcs"><xsl:value-of select="$_operation/@Kit_Kpcs" /></xsl:attribute>
			<xsl:attribute name="prepTime"><xsl:value-of select="$_operation/@Kit_PrepTime" /></xsl:attribute>
				<xsl:attribute name="tPcs"><xsl:value-of select="$_operation/@Kit_Tpcs" /></xsl:attribute>

			<xsl:attribute name="profCode"><xsl:value-of select="$_workers[1]/@Kit_JobRank"/></xsl:attribute>
			<xsl:attribute name="profRank"><xsl:value-of select="$_workers[1]/@Kit_ProfessionCode"/></xsl:attribute> <!-- Kostyl -->
			
			<xsl:attribute name="workersQty">
				<xsl:call-template name="toIntNoZero" >
					<xsl:with-param name="v" select="$_workers[1]/@ResourceQuantity"/>
				</xsl:call-template>
			</xsl:attribute>
			
		</xsl:element>

		<xsl:for-each select="$_workers[position() &gt; 1]" >

			<xsl:variable name="_worker" select="current()" />

			<xsl:element name="record" >

				<xsl:attribute name="_type">B1</xsl:attribute>

				<xsl:attribute name="mechanizationCode"><xsl:value-of select="$_operation/@Kit_MechanizationCode" /></xsl:attribute>
				<xsl:attribute name="workConditionCode"><xsl:value-of select="$_operation/@Kit_WorkConditionCode" /></xsl:attribute>
				<xsl:attribute name="koid"><xsl:value-of select="$_operation/@Kit_NumSimultaneousManufParts" /></xsl:attribute>
				<xsl:attribute name="batchPieces"><xsl:value-of select="$_operation/@Kit_VolProdBatchPieces" /></xsl:attribute>
				<xsl:attribute name="kPcs"><xsl:value-of select="$_operation/@Kit_Kpcs" /></xsl:attribute>
				<xsl:attribute name="tPcs"><xsl:value-of select="$_operation/@Kit_Tpcs" /></xsl:attribute>

				<xsl:attribute name="profCode"><xsl:value-of select="$_worker/@Kit_JobRank"/></xsl:attribute>
				<xsl:attribute name="profRank"><xsl:value-of select="$_worker/@Kit_ProfessionCode"/></xsl:attribute> <!-- Kostyl -->
				<xsl:attribute name="workersQty">
					<xsl:call-template name="toIntNoZero" >
						<xsl:with-param name="v" select="$_worker/@ResourceQuantity"/>
					</xsl:call-template>
				</xsl:attribute>
			</xsl:element>
			
		</xsl:for-each>

		<!-- =============== -->
		
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generateSomeKRows" >
	<xsl:param name="operationId" />
	<xsl:param name="ProcessType" />
	<xsl:param name="mappingType" />
	
	<xsl:variable name="_components0">
		<xsl:call-template name="getComponents">
			<xsl:with-param name="operationId" select="$operationId"/>
			<xsl:with-param name="ProcessType" select="$ProcessType"/>
			<xsl:with-param name="mappingType" select="$mappingType"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="_components" select="exsl:node-set($_components0)/*"/>

	
	<xsl:for-each select="$_components" >

		<xsl:call-template name="generateRowK" >
			<xsl:with-param name="_type">K</xsl:with-param>
			<xsl:with-param name="title"><xsl:value-of select="@V_Name" /></xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="@V_description" /></xsl:with-param>
			<xsl:with-param name="partNumber"><xsl:value-of select="@Kit_M_PartNumber" /></xsl:with-param>
			<xsl:with-param name="standardUnit">
				<xsl:if test="@count!='' and @count!='0'" >
					шт.
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="count"><xsl:value-of select="@count" /></xsl:with-param>
		</xsl:call-template>

	</xsl:for-each>
	
</xsl:template>


<!-- ============================================================================================================ -->

<xsl:template name="generateRowK" >
	<xsl:param name="_type" />
	<xsl:param name="title" />
	<xsl:param name="description" />
	<xsl:param name="partNumber" />
	<xsl:param name="standardUnit" />
	<xsl:param name="count" />

		<xsl:variable name="wrapText0">
			<xsl:call-template name="wrapTextIterate">
				<xsl:with-param name="font" select="$fontGostItalic" />
				<xsl:with-param name="text" select="$description" />
				<xsl:with-param name="maxWidth" select="110" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="wrapText" select="exsl:node-set($wrapText0)/*"/>

		<xsl:variable name="wrapCode0">
			<xsl:call-template name="wrapTextIterate">
				<xsl:with-param name="font" select="$fontGostItalic" />
				<xsl:with-param name="text">
					<xsl:value-of select="$title" />
					<xsl:if test="($enablePartNumberComment &gt; 0) and string-length($partNumber)">
						<xsl:value-of select="concat(' (',$partNumber,')')" />
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="maxWidth" select="70" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="wrapCode" select="exsl:node-set($wrapCode0)/*"/>

		<!-- xsl:message>wrapText: <xsl:value-of select="count($wrapText)"/></xsl:message>
		<xsl:message>wrapCode: <xsl:value-of select="count(	$wrapCode)"/></xsl:message -->

		<xsl:variable name="moreThanAny" select="$wrapText|$wrapCode"/> <!-- more than any of these, just to iterate -->

		<xsl:element name="record" >
			<xsl:attribute name="_type">K</xsl:attribute>
			<xsl:attribute name="description"><xsl:value-of select="$wrapText[1]" /></xsl:attribute>
			<xsl:attribute name="title"><xsl:value-of select="$wrapCode[1]" /></xsl:attribute>
			<!-- xsl:attribute name="partNumber"><xsl:value-of select="$partNumber" /></xsl:attribute -->
			<xsl:attribute name="standardUnit"><xsl:value-of select="$standardUnit" /></xsl:attribute>
			<xsl:attribute name="count"><xsl:value-of select="$count" /></xsl:attribute>
		</xsl:element>

		<xsl:for-each select="$moreThanAny[position() &gt; 1]" >
			<xsl:variable name="pos" select="position() + 1" />

			<xsl:if test="count($wrapText[$pos])!=0 or count($wrapCode[$pos])!=0" >
			
				<xsl:element name="record" >
					<xsl:attribute name="_type">K1</xsl:attribute>
					<xsl:attribute name="description"><xsl:value-of select="$wrapText[$pos]" /></xsl:attribute>
					<xsl:attribute name="title"><xsl:value-of select="$wrapCode[$pos]" /></xsl:attribute>
				</xsl:element>
				
			</xsl:if>
			
		</xsl:for-each>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generateRowM" >
	<xsl:param name="title"/>
	<xsl:param name="description"/>
	<xsl:param name="material" />

	<!-- xsl:call-template name="messageObject">
		<xsl:with-param name="title" select="'generateRowM'"/>
		<xsl:with-param name="object" select="$material"/>
	</xsl:call-template -->
	
	<xsl:variable name="text" select="concat($material/@V_Name,' ',$material/@V_description)" />
	
	<xsl:variable name="wrap0">
		<xsl:call-template name="wrapTextIterate">
			<xsl:with-param name="font" select="$fontGostItalic" />
			<xsl:with-param name="text" select="$text" />
			<xsl:with-param name="maxWidth" select="110" />
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="wrap" select="exsl:node-set($wrap0)/*"/>

	<!-- xsl:message>generateRowM: <xsl:value-of select="count($wrap)"/> =<xsl:value-of select="$text"/>= </xsl:message>
	<xsl:for-each select="$wrap">
		<xsl:message>=<xsl:value-of select="current()"/>=</xsl:message>
	</xsl:for-each -->

	<xsl:element name="record" >
		<xsl:attribute name="_type">M</xsl:attribute>
		<xsl:attribute name="description"><xsl:value-of select="$wrap[1]" /></xsl:attribute>
		<xsl:attribute name="standardUnit"><xsl:value-of select="$material/@QuantityUnit" /></xsl:attribute>
		<xsl:attribute name="nrash"><xsl:call-template name="NormNoZero"><xsl:with-param name="v" select="$material/@nrash"/></xsl:call-template></xsl:attribute>
	</xsl:element>

	<xsl:for-each select="$wrap[position() &gt; 1]" >
		<xsl:element name="record" >
			<xsl:attribute name="_type">M1</xsl:attribute>
			<xsl:attribute name="description"><xsl:value-of select="current()" /></xsl:attribute>
		</xsl:element>
	</xsl:for-each>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generatePossibleRowT" >
	<xsl:param name="operationId" />
	
	<xsl:variable name="_nonMachines0"><xsl:call-template name="getNonMachines"><xsl:with-param name="operationId" select="$operationId"/></xsl:call-template></xsl:variable>
	<xsl:variable name="_nonMachines" select="exsl:node-set($_nonMachines0)/*"/>

	<!-- xsl:message>generatePossibleRowT: operationId=<xsl:value-of select="$operationId"/> _nonMachines=<xsl:value-of select="count($_nonMachines)"/></xsl:message -->

	<xsl:if test="$_nonMachines">
		<xsl:call-template name="generateRowT"> <xsl:with-param name="_nonMachines" select="$_nonMachines" /> </xsl:call-template>
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generateRowT" >
	<xsl:param name="_nonMachines" />

	<!-- ============= -->
	
	<xsl:variable name="text">
		<xsl:for-each select="$_nonMachines" >
			<xsl:sort select="translate(  substring(@Kit_Category,1,1),  'FACBTPM','1234567')" />

			<xsl:if test="position() &gt; 1" >; </xsl:if> 
			<xsl:value-of select="concat(@V_Name,'&#160;',@V_description)" />
			
		</xsl:for-each>
	</xsl:variable>
	
	<!-- ============= -->
	<!-- xsl:message>generateRowT: text=<xsl:value-of select="$text"/> _nonMachines=<xsl:value-of select="count($_nonMachines)"/></xsl:message -->

	<xsl:variable name="wrap0">
		<xsl:call-template name="wrapTextIterate">
			<xsl:with-param name="font" select="$fontGostItalic" />
			<xsl:with-param name="text" select="$text" />
			<xsl:with-param name="maxWidth" select="273" /><!-- 148.2 -->
			<xsl:with-param name="separator" select="';'" />
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="wrap" select="exsl:node-set($wrap0)/*"/>

	<xsl:element name="record" >
		<xsl:attribute name="_type">T</xsl:attribute>
		<xsl:attribute name="text"><xsl:value-of select="$wrap[1]" /></xsl:attribute>
	</xsl:element>

	<xsl:for-each select="$wrap[position() &gt; 1]" >
	
		<xsl:element name="record" >
			<xsl:attribute name="_type">T1</xsl:attribute>
			<xsl:attribute name="text"><xsl:value-of select="current()" /></xsl:attribute>
		</xsl:element>
		
	</xsl:for-each>

	<!-- ============= -->
		
</xsl:template>

<!-- ============================================================================================================ -->
<!-- =====  UTILITIES  ========================================================================================== -->
<!-- ============================================================================================================ -->


<!-- ============================================================================================================ -->

<xsl:template name="getWorkerCR_ids" >
	<xsl:param name="operationId" />
	
	<xsl:variable name="_cr" select="/WKIXMLDataRoot/ProductionSystemStructure/CapableResource" />
	<xsl:variable name="_rs" select="/WKIXMLDataRoot/ResourceStructure" />
	
	<xsl:value-of select="','" />
	<xsl:for-each select="$_cr[ParentReference=$operationId]" >
		<xsl:variable name="_crId" select="@id" />
		<xsl:variable name="_resId" select="Result" />
		<xsl:if test="$_rs/ResourceWorker[@id=$_resId]" >
			<xsl:value-of select="$_crId" />
			<xsl:value-of select="','" />
		</xsl:if>
	</xsl:for-each>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getBarSize" >
	<xsl:param name="bar" />
	
	<xsl:if test="$bar" >
	
		<xsl:variable name="rt" select="$bar/@Kit_RawType"/>
		<xsl:variable name="ks_l" ><xsl:call-template name="stripLengthNoZero"><xsl:with-param name="v" select="$bar/@Kit_StockLength"/></xsl:call-template></xsl:variable>
		<xsl:variable name="ks_hd" ><xsl:call-template name="stripLengthNoZero"><xsl:with-param name="v" select="$bar/@Kit_StockHightDiameter"/></xsl:call-template></xsl:variable>
		<xsl:variable name="ks_w" ><xsl:call-template name="stripLengthNoZero"><xsl:with-param name="v" select="$bar/@Kit_StockWidth"/></xsl:call-template></xsl:variable>
		
		<xsl:variable name="concatSizes">
			<xsl:if test="number(translate($ks_l,',','.')) &gt; 0" > x <xsl:value-of select="$ks_l"/></xsl:if>
			<xsl:if test="number(translate($ks_hd,',','.')) &gt; 0"> x <xsl:value-of select="$ks_hd"/></xsl:if>
			<xsl:if test="number(translate($ks_w,',','.')) &gt; 0" > x <xsl:value-of select="$ks_w"/></xsl:if>
		</xsl:variable>
		
		<xsl:value-of select="concat($rt,' ',substring($concatSizes,4))"/>

	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="writeMK" >
	<xsl:param name="form" />
	<xsl:param name="globalPageNumber" />

	<xsl:variable name="isFirst" select="$form/@_page_number = 1" />

	<xsl:variable name="_route0">
		<xsl:if test="$isFirst"><xsl:call-template name="getRoute"/></xsl:if>
		<xsl:if test="not($isFirst)"><xsl:element name="Route"/></xsl:if>
	</xsl:variable>
	<xsl:variable name="_route" select="exsl:node-set($_route0)/*"/>

	<!-- xsl:message>Route: <xsl:value-of select="count($_route/RouteItem)" />, <xsl:value-of select="count($_route/*)" /></xsl:message>
	<xsl:for-each select="$_route/RouteItem">
		<xsl:call-template name="messageObject">
			<xsl:with-param name="title" select="'Item'" />
			<xsl:with-param name="object" select="current()" />
		</xsl:call-template>
	</xsl:for-each -->
	
	<fo:wrapper line-height="{$h_single}">

	<!-- xsl:message>_is_assembly:<xsl:value-of select="$form/@_is_assembly"/></xsl:message>
	<xsl:message>!_is_assembly:<xsl:value-of select="not($form/@_is_assembly)"/></xsl:message>
	<xsl:message>!!_is_assembly:<xsl:value-of select="not(not($form/@_is_assembly))"/></xsl:message>
	<xsl:message>t_is_assembly:<xsl:value-of select="$form/@_is_assembly='true'"/></xsl:message>
	<xsl:message>f_is_assembly:<xsl:value-of select="$form/@_is_assembly='false'"/></xsl:message -->

	<xsl:call-template name="formHeader">
		<xsl:with-param name="gost" select="'3.1118-82'" />
		<xsl:with-param name="isFirst" select="$isFirst" />
		<xsl:with-param name="formFirst">
			<xsl:if test="$form/@_is_assembly='false'">Форма 1</xsl:if>
			<xsl:if test="$form/@_is_assembly='true'">Форма 2</xsl:if>
		</xsl:with-param>
		<xsl:with-param name="formNonFirst">Форма 1б</xsl:with-param>
		<xsl:with-param name="no-markers" select="true()" />
		<xsl:with-param name="organization" select="$organization"/>
		<xsl:with-param name="part-name" select="$form/@part-name"/>
		<xsl:with-param name="part-code" select="$form/@part-code"/>
		<xsl:with-param name="page_number" select="$form/@_page_number"/>
		<xsl:with-param name="pages_total" select="$form/@_pages_total"/>
		<xsl:with-param name="route" select="$_route"/>
	</xsl:call-template>
	
	<xsl:if test="$form/@_use_m1m2 = 'true' " >
		<xsl:call-template name="partM1M2" >
			<xsl:with-param name="no-markers" select="true()" />
			<xsl:with-param name="m01-material"><xsl:value-of select="$form/@m01-material"/></xsl:with-param>
			<xsl:with-param name="m02-kodmat"><xsl:value-of select="$form/@m02-kodmat"/></xsl:with-param>
			<xsl:with-param name="m02-ev"><xsl:value-of select="$form/@m02-ev"/></xsl:with-param>
			<xsl:with-param name="m02-md"><xsl:value-of select="$form/@m02-md"/></xsl:with-param>
			<xsl:with-param name="m02-nrash"><xsl:value-of select="$form/@m02-nrash"/></xsl:with-param>
			<xsl:with-param name="m02-kim"><xsl:value-of select="$form/@m02-kim"/></xsl:with-param>
			<xsl:with-param name="m02-zagsize"><xsl:value-of select="$form/@m02-zagsize"/></xsl:with-param>
			<xsl:with-param name="m02-kd"><xsl:value-of select="$form/@m02-kd"/></xsl:with-param>
			<xsl:with-param name="m02-mz"><xsl:value-of select="$form/@m02-mz"/></xsl:with-param>
		</xsl:call-template>
	</xsl:if>
	
	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="75.4mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="20.8mm"/>
		<!-- body -->
		<fo:table-body>
		
			<xsl:call-template name="partA" />
			
		</fo:table-body>
	</fo:table>

	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="119.6mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="20.8mm"/>
		<!-- body -->
		<fo:table-body>

			<xsl:call-template name="partB" />
			
			<xsl:if test="$form/@_use_km = 'true'">
				<xsl:call-template name="partKM" />
			</xsl:if>
			
		</fo:table-body>
	</fo:table>
	
	<xsl:for-each select="$form/*" >
		<xsl:variable name="row" select="current()" />

		<!-- xsl:call-template name="messageObject">
			<xsl:with-param name="title" select="'mkRow'" />
			<xsl:with-param name="object" select="$row" />
		</xsl:call-template -->
	
		<xsl:variable name="rowNumber" select="position() + $form/@_row_delta" />

		<xsl:call-template name="writeMkRow" >
			<xsl:with-param name="row" select="$row" />
			<xsl:with-param name="rowNumber" select="$rowNumber" />
		</xsl:call-template>
		
	</xsl:for-each>

	<xsl:call-template name="formFooter" >
		<xsl:with-param name="no-markers" select="true()" />
		<xsl:with-param name="B3f1rows" select="position()=1" />
		<xsl:with-param name="document-type-code" select="'МК'" />
		<xsl:with-param name="document-type-title" select="'МАРШРУТНАЯ КАРТА'" />
		<xsl:with-param name="document-file-name" />
		<xsl:with-param name="document-file-version" select="$globalPageNumber" />
	</xsl:call-template>
	
	</fo:wrapper>

	<fo:block page-break-after="always"/>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeMkRow" >
	<xsl:param name="row" />
	<xsl:param name="rowNumber" />

	<xsl:variable name="label">
		<xsl:choose>
			<xsl:when test="$row/@_type = 'A'" >А</xsl:when>
			<xsl:when test="$row/@_type = 'B'" >Б</xsl:when>
			<xsl:when test="$row/@_type = 'K'" >К</xsl:when>
			<xsl:when test="$row/@_type = 'M'" >М</xsl:when>
			<xsl:when test="$row/@_type = 'O'" >О</xsl:when>
			<xsl:when test="$row/@_type = 'T'" >Т</xsl:when>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name="rowNumberText">
		<xsl:if test="number($rowNumber) &lt; 10">0</xsl:if><xsl:value-of select="$rowNumber"/>
	</xsl:variable>
			
	<!-- =========================== -->
	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
	<fo:table-column column-width="6.0mm"/>
	<fo:table-column column-width="7.0mm"/>
	<fo:table-column column-width="273mm"/>
		<fo:table-body>
		<fo:table-row height="{$h_double}">
			<fo:table-cell xsl:use-attribute-sets="cellLB"><fo:block font-size="4.78mm" margin-left="2mm" padding-top="2.6mm"><xsl:value-of select="$label"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block font-size="4.78mm" margin-left="1mm" padding-top="2.6mm"><xsl:value-of select="$rowNumberText"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="cellTBR">
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'10.4mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'20.8mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'31.2mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'44.2mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'119.6mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'130mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'148.2mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'158.6mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'171.6mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'182mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'195mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'208mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'221mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'234mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'252.2mm'" /></xsl:call-template>
				<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'273mm'" /></xsl:call-template>
	<!-- =========================== -->
	
	<xsl:choose>
	
		<xsl:when test="$row/@_type='A' or $row/@_type='A1'" >
			<xsl:call-template name="writeRowA" >
				<xsl:with-param name="row" select="$row" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$row/@_type='B' or $row/@_type='B1'" >
			<xsl:call-template name="writeRowB" >
				<xsl:with-param name="row" select="$row" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$row/@_type='K' or $row/@_type='K1' or $row/@_type='M' or $row/@_type='M1'" >
			<xsl:call-template name="writeRowKM" >
				<xsl:with-param name="row" select="$row" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$row/@_type='O' or $row/@_type='O1'" >
			<xsl:call-template name="writeRowT" >
				<xsl:with-param name="row" select="$row" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$row/@_type='T' or $row/@_type='T1'" >
			<xsl:call-template name="writeRowT" >
				<xsl:with-param name="row" select="$row" />
			</xsl:call-template>
		</xsl:when>
		
	</xsl:choose>

	<!-- =========================== -->
			</fo:table-cell>
		</fo:table-row>
		</fo:table-body>
	</fo:table>
	<!-- =========================== -->
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeRowA" >
	<xsl:param name="row" />

	<!-- =========================== -->
	<!-- width="286mm" border-width="0.5mm" morder-style="solid" border-color="red"  -->
	<fo:table table-layout="fixed" width="273mm" font-size="20pt" margin-left="-0.1mm" margin-top="2.7mm">
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="75.4mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="18.2mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="18.2mm"/>
	<fo:table-column column-width="20.8mm"/>
	<fo:table-body>

		<fo:table-row height="5mm">
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block font-weight="bold" text-align="center"><xsl:value-of select="$row/@workShop"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block font-weight="bold" text-align="center"><xsl:value-of select="$row/@workArea"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block font-weight="bold" text-align="center"><xsl:value-of select="$row/@workPlace"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block font-weight="bold" text-align="center"><xsl:value-of select="$row/@operationNumber"/></fo:block></fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block font-weight="bold" padding-left="5mm">
				<xsl:value-of select="normalize-space(concat($row/@operationCode,' ',$row/@operationName))"/> </fo:block></fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="data_cell" number-columns-spanned="11"><fo:block><xsl:value-of select="$row/@iotDocs"/></fo:block></fo:table-cell>

		</fo:table-row>

	</fo:table-body>
	</fo:table>
	<!-- =========================== -->
		
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeRowB" >
	<xsl:param name="row" />

	<!-- =========================== -->
	<!-- width="286mm" border-width="0.5mm" morder-style="solid" border-color="red"  -->
	<fo:table table-layout="fixed" width="273mm" font-size="20pt" margin-left="-0.1mm" margin-top="2.7mm">
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="75.4mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="18.2mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="10.4mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="13.0mm"/>
	<fo:table-column column-width="18.2mm"/>
	<fo:table-column column-width="20.8mm"/>
	<fo:table-body>

		<fo:table-row height="5mm">
		
			<fo:table-cell xsl:use-attribute-sets="data_cell" number-columns-spanned="5"><fo:block><xsl:value-of select="$row/@machineName"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@mechanizationCode"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@profCode"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@profRank"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@workConditionCode"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@workersQty"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@koid"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@standardUnit"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@batchPieces"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@kPcs"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@prepTime"/></fo:block></fo:table-cell>
			<!-- strip_prepTime -->
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@tPcs"/></fo:block></fo:table-cell>
			<!-- strip_tPcs -->

		</fo:table-row>

	</fo:table-body>
	</fo:table>
	<!-- =========================== -->
		
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeRowKM" >
	<xsl:param name="row" />

	<!-- xsl:call-template name="messageObject">
		<xsl:with-param name="title" select="'writeRowKM'"/>
		<xsl:with-param name="object" select="$row"/>
	</xsl:call-template -->


	<xsl:variable name="text" >
		
		<xsl:if test="string-length($row/@description)">
			<xsl:value-of select="concat(' ',$row/@description)" />
		</xsl:if>
		
	</xsl:variable>

	<xsl:variable name="code" >
		
		<xsl:if test="string-length($row/@title)">
			<xsl:value-of select="concat(' ',$row/@title)" />
		</xsl:if>
		
		<xsl:if test="($enablePartNumberComment &gt; 0) and string-length($row/@partNumber)">
			<xsl:value-of select="concat(' (',$row/@partNumber,')')" />
		</xsl:if>
		
	</xsl:variable>

	<!-- xsl:variable name="text" >
		
		<xsl:if test="string-length($row/@title)">
			<xsl:value-of select="concat(' ',$row/@title)" />
		</xsl:if>
		
		<xsl:if test="string-length($row/@description)">
			<xsl:value-of select="concat(' ',$row/@description)" />
		</xsl:if>
		
		<xsl:if test="($enablePartNumberComment &gt; 0) and string-length($row/@partNumber)">
			<xsl:value-of select="concat(' (',$row/@partNumber,')')" />
		</xsl:if>
		
	</xsl:variable -->

	<!-- =========================== -->
	<!-- width="286mm" border-width="0.5mm" morder-style="solid" border-color="red"  -->
	<fo:table table-layout="fixed" width="273mm" font-size="20pt" margin-left="-0.1mm" margin-top="2.7mm">
		<fo:table-column column-width="119.6mm"/>
		<fo:table-column column-width="75.4mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="20.8mm"/>
	<fo:table-body>

		<fo:table-row height="5mm">
		
			<xsl:variable name="storage" />
			<xsl:variable name="measureCode" />
			<xsl:variable name="standardUnit" />
			<xsl:variable name="nrash" select="'nrash'" />
		
			<fo:table-cell xsl:use-attribute-sets="data_cell" ><fo:block><xsl:value-of select="$text"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell" ><fo:block><xsl:value-of select="$code"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block><xsl:value-of select="$storage"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@standardUnit"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@count"/></fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="data_cell"><fo:block text-align="center"><xsl:value-of select="$row/@nrash"/></fo:block></fo:table-cell>
			
		</fo:table-row>

	</fo:table-body>
	</fo:table>
	<!-- =========================== -->
		
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeRowT" >
	<xsl:param name="row" />

	<!-- =========================== -->
	<!-- width="286mm" border-width="0.5mm" morder-style="solid" border-color="red"  -->
	<fo:table table-layout="fixed" width="273mm" font-size="20pt" margin-left="-0.1mm" margin-top="2.7mm">
	<fo:table-column column-width="273mm"/>
	<fo:table-body>

		<fo:table-row height="5mm">
			<fo:table-cell xsl:use-attribute-sets="data_cell" ><fo:block><xsl:value-of select="$row/@text"/></fo:block></fo:table-cell>
		</fo:table-row>

	</fo:table-body>
	</fo:table>
	<!-- =========================== -->
		
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="imagesGenerate" >
	<xsl:param name="sequenceId" />
	<xsl:param name="operationId"/>
	
	<xsl:variable name="_operation0"><xsl:call-template name="getOperation"><xsl:with-param name="operationId" select="$operationId"/></xsl:call-template></xsl:variable>
	<xsl:variable name="_operation" select="exsl:node-set($_operation0)/*"/>

	<xsl:variable name="_images0"><xsl:call-template name="getImages"><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:variable>
	<xsl:variable name="_images" select="exsl:node-set($_images0)/*"/>

	<xsl:variable name="_rawPages0">
		<xsl:call-template name="generateRawImagePages" >
			<xsl:with-param name="_images" select="$_images" />
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="_rawPages" select="exsl:node-set($_rawPages0)/*"/>

	<!-- xsl:message>_rawPages:<xsl:value-of select="count($_rawPages)"/>:<xsl:value-of select="local-name($_rawPages[1])"/></xsl:message -->
	<xsl:variable name="_process0"> <xsl:call-template name="getTopProcess" /> </xsl:variable>
	<xsl:variable name="_process" select="exsl:node-set($_process0)/*"/>

	<xsl:for-each select="$_rawPages" >
		
		<xsl:element name="form">
		
			<xsl:attribute name="_type">KE</xsl:attribute>
			<xsl:attribute name="_pages_total"><xsl:value-of select="count($_rawPages)"/></xsl:attribute>
			<xsl:attribute name="_page_number"><xsl:value-of select="position()"/></xsl:attribute>

			<xsl:attribute name="imageUrl"><xsl:value-of select="current()"/></xsl:attribute>

			<xsl:attribute name="part-name"><xsl:value-of select="$_process/@V_description"/></xsl:attribute>
			<xsl:attribute name="part-code"><xsl:value-of select="$_process/@V_Name"/></xsl:attribute>
			
			<xsl:attribute name="workShop"><xsl:call-template name="getWorkShop" ><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:attribute>
			<xsl:attribute name="workArea"><xsl:call-template name="getWorkArea" ><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:attribute>
			<xsl:attribute name="workPlace"><xsl:call-template name="getWorkPlace" ><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:attribute>
			
			<xsl:attribute name="operationNumber"><xsl:value-of select="substring( $_operation/@Kit_NumOperation, 1, 3 )" /></xsl:attribute>

		</xsl:element>
		
	</xsl:for-each>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generateRawImagePages" >
	<xsl:param name="_images" />

	<xsl:for-each select="$_images">

		<xsl:variable name="_image" select="current()" />

		<xsl:variable name="is_setup_card" select="starts-with($_image/@FileName,'Карта наладки') and contains($_image/@FileName,'.pdf')" />
		
		<xsl:if test="$is_setup_card" >
			
			<!-- xsl:message>Setup Card:<xsl:value-of select="$_image/@FileName"/></xsl:message -->

			<xsl:variable name="tail" select="substring-after($_image/@FileName,'__')" />
			<xsl:variable name="tail_pages" select="substring-before($tail,'.pdf')" />
			<xsl:variable name="tail_pages_safe"><xsl:value-of select="$tail_pages"/><xsl:if test="$tail_pages=''">1</xsl:if></xsl:variable>
		
			<xsl:variable name="n_pages" select="ceiling(number($tail_pages_safe))"/>
			
			<xsl:call-template name="generateOneImagePages" >
				<xsl:with-param name="_image" select="$_image" />
				<xsl:with-param name="n_pages" select="$n_pages" />
			</xsl:call-template>
			
		</xsl:if>

		<xsl:if test="not($is_setup_card)" >
		
			<!-- xsl:message>Not Setup Card:<xsl:value-of select="$_image/@FileName"/></xsl:message -->

			<xsl:call-template name="generateOneImageForm" >
				<xsl:with-param name="_image" select="$_image" />
			</xsl:call-template>
		</xsl:if>

	</xsl:for-each>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generateOneImagePages" >
	<xsl:param name="_image"/>
	<xsl:param name="n_pages" select="1" />
	<xsl:param name="page" select="1" />
	
	<xsl:if test="$n_pages &gt; 0" >
		<xsl:call-template name="generateOneImageForm" >
			<xsl:with-param name="_image" select="$_image" />
			<xsl:with-param name="page" select="$page" />
		</xsl:call-template>
		<xsl:call-template name="generateOneImagePages" >
			<xsl:with-param name="_image" select="$_image" />
			<xsl:with-param name="page" select="$page + 1" />
			<xsl:with-param name="n_pages" select="$n_pages - 1" />
		</xsl:call-template>
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generateOneImageForm" >
	<xsl:param name="_image"/>
	<xsl:param name="page" select="1" />

	
	<xsl:variable name="fpath">
		<xsl:call-template name="percentEncode" ><xsl:with-param name="str" select="concat($images_base,'/',$_image/@FileName)" /></xsl:call-template>
	</xsl:variable>

	<xsl:element name="image">
		<xsl:value-of select="concat( $fpath,'#page=',$page)" />
	</xsl:element>

</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="writeImage" >
	<xsl:param name="form" />
	<xsl:param name="globalPageNumber" />

	<!-- xsl:variable name="is_first" select="position()=1" / -->
	<xsl:variable name="is_first" select="$form/@_page_number = 1" />
	<!-- xsl:message>writeImage is_first=<xsl:value-of select="$is_first"/></xsl:message -->
	
	<fo:wrapper line-height="{$h_single}">
	<!-- =====  Header  ===== -->

	<xsl:call-template name="formHeader">
		<xsl:with-param name="gost" select="'3.1105-2011'" />
		<xsl:with-param name="isFirst" select="$is_first" />
		<xsl:with-param name="formFirst">Форма 7</xsl:with-param>
		<xsl:with-param name="formNonFirst">Форма 7а</xsl:with-param>
		<xsl:with-param name="f1_or_f3" select="'f3'" />
		<xsl:with-param name="no-markers" select="true()" />

		<xsl:with-param name="organization" select="$organization"/>
		<xsl:with-param name="workShop" select="$form/@workShop" />
		<xsl:with-param name="workArea" select="$form/@workArea" />
		<xsl:with-param name="workPlace" select="$form/@workPlace" />
		<xsl:with-param name="operation-number" select="$form/@operationNumber" />

		<xsl:with-param name="part-name" select="$form/@part-name"/>
		<xsl:with-param name="part-code" select="$form/@part-code"/>
		<xsl:with-param name="page_number" select="$form/@_page_number"/>
		<xsl:with-param name="pages_total" select="$form/@_pages_total"/>
	</xsl:call-template>
	
	<!-- =====  Main table ===== -->
	
	<xsl:variable name="contentHeight">
		<xsl:if test="$is_first" >146.0mm</xsl:if>
		<xsl:if test="not($is_first)" >154.8mm</xsl:if>
	</xsl:variable>
	
	<xsl:variable name="contentHeight1">
		<xsl:if test="$is_first" >144.4mm</xsl:if>
		<xsl:if test="not($is_first)" >153.2mm</xsl:if>
	</xsl:variable>
	
	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<fo:table-column column-width="286mm"/>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$contentHeight}">
				<fo:table-cell display-align="after" xsl:use-attribute-sets="cell" padding-top="0mm" padding-bottom="0" ><fo:block>

					<fo:block-container height="{$contentHeight}" display-align="center" >
					<fo:block text-align="center" >
						<xsl:if test="not($skip_images)" >

							<fo:external-graphic scaling="uniform" 
							padding-top="0mm" margin-top="0mm" border-style="none"
								content-width="scale-to-fit" content-height="scale-to-fit"  
								src="{$form/@imageUrl}"

								width="285.5mm" height="{$contentHeight1}" 
							/>
						</xsl:if>
					</fo:block>
					</fo:block-container>
				
				</fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	
	<!-- =====  Footer ===== -->
	
	<xsl:call-template name="formFooter" >
		<!-- xsl:with-param name="B3f1rows"> <xsl:if test="position()=1">1</xsl:if> <xsl:if test="position()!=1">0</xsl:if> </xsl:with-param -->
		<xsl:with-param name="no-markers" select="true()" />
		<xsl:with-param name="document-type-code" select="'КЭ'" />
		<xsl:with-param name="document-type-title" select="'КАРТА ЭСКИЗОВ'" />
		<xsl:with-param name="document-file-version" select="$globalPageNumber" />
	</xsl:call-template>
	
	<!-- =====   ===== -->
	</fo:wrapper>

	<fo:block page-break-after="always"/>

</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="controlGenerate" >
	<xsl:param name="operationId"/>

	<xsl:variable name="_process0"> <xsl:call-template name="getTopProcess" /> </xsl:variable>
	<xsl:variable name="_process" select="exsl:node-set($_process0)/*"/>

	<xsl:variable name="_product0"> <xsl:call-template name="getTopProduct" /> </xsl:variable>
	<xsl:variable name="_product" select="exsl:node-set($_product0)/*"/>


	<xsl:variable name="_operation0"><xsl:call-template name="getOperation"><xsl:with-param name="operationId" select="$operationId"/></xsl:call-template></xsl:variable>
	<xsl:variable name="_operation" select="exsl:node-set($_operation0)/*"/>

	<xsl:variable name="_material0"> <xsl:call-template name="getMainMaterial" /> </xsl:variable>
	<xsl:variable name="_material" select="exsl:node-set($_material0)/*"/>

	
	<!-- Machine Resources -->
	
	<xsl:variable name="_machine0"><xsl:call-template name="getMachine"><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:variable>
	<xsl:variable name="_machine" select="exsl:node-set($_machine0)/*"/>
	
	<!-- end Machine Resources -->

	<xsl:if test="$_operation/@Kit_Control='TRUE' or $_operation/@Kit_Control='True' or $_operation/@Kit_Control='true'">
	<!-- ==================================================== -->
		
		<xsl:variable name="_instructions0"><xsl:call-template name="getInstructions"><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:variable>
		<xsl:variable name="_instructions" select="exsl:node-set($_instructions0)/*"/>
		
		<xsl:variable name="_rows0">
		<!-- ======================= -->
		<xsl:for-each select="$_instructions" >
			<xsl:sort data-type="number" select="@_index" />

			<xsl:variable name="_instruction" select="current()"/>

			<xsl:variable name="instructionText">

				<xsl:if test="string-length($_instruction/@Kit_TransitionNumber)">
					<xsl:value-of select="concat($_instruction/@Kit_TransitionNumber,'.')"/>
				</xsl:if>
				
				<xsl:value-of select="'&#160;'"/>
				
				<xsl:value-of select="$_instruction/@_instruction_text"/>
				
			</xsl:variable>

			<xsl:variable name="_nonMachines0"><xsl:call-template name="getNonMachines"><xsl:with-param name="operationId" select="$_operation/@id|$_instruction/@id"/></xsl:call-template></xsl:variable>
			<xsl:variable name="_nonMachines" select="exsl:node-set($_nonMachines0)/*"/>

			<xsl:variable name="_measures" select="$_nonMachines[@Kit_Category='MeasureTool']" />
<!--xsl:message>generateControlItem _instruction=<xsl:value-of select="$_instruction/@id"/></xsl:message -->
			<xsl:call-template name="generateControlItem" >
				<xsl:with-param name="instructionText" select="$instructionText" />
				<xsl:with-param name="_measures" select="$_measures" />
				<xsl:with-param name="percentage">
					<xsl:call-template name="NormNoZero"><xsl:with-param name="v" select="$_operation/@Kit_Percentage"/></xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>

		</xsl:for-each>	
		<!-- ======================= -->
		</xsl:variable>
		<xsl:variable name="_rows" select="exsl:node-set($_rows0)/*"/>

		<!-- xsl:message>generateControlItem _rows=<xsl:value-of select="count($_rows)"/></xsl:message -->
		
		<xsl:variable name="_pages0">
			<xsl:call-template name="rowsToPages">
				<xsl:with-param name="rows" select="$_rows" />
				<xsl:with-param name="nFirstRows" select="12" />
				<xsl:with-param name="nNextRows" select="17" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="_pages" select="exsl:node-set($_pages0)/*"/>
		
		<xsl:for-each select="$_pages" >
			<xsl:variable name="_page" select="current()"/>

			<xsl:element name="form">
				<xsl:attribute name="_type">KK</xsl:attribute>
				<xsl:attribute name="_pages_total"><xsl:value-of select="count($_pages)"/></xsl:attribute>
				<xsl:attribute name="_page_number"><xsl:value-of select="position()"/></xsl:attribute>

				<xsl:attribute name="part-name"><xsl:value-of select="$_process/@V_description"/></xsl:attribute>
				<xsl:attribute name="part-code"><xsl:value-of select="$_process/@V_Name"/></xsl:attribute>

				<xsl:attribute name="workShop"><xsl:call-template name="getWorkShop" ><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:attribute>
				<xsl:attribute name="workArea"><xsl:call-template name="getWorkArea" ><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:attribute>
				<xsl:attribute name="workPlace"><xsl:call-template name="getWorkPlace" ><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:attribute>

				<xsl:attribute name="operationNumber"><xsl:value-of select="substring( $_operation/@Kit_NumOperation, 1, 3 )"/></xsl:attribute>
				<xsl:attribute name="operationName"><xsl:value-of select="$_operation/@V_Name"/></xsl:attribute>
				<xsl:attribute name="kk-to"><xsl:call-template name="stripTimeNoZero"><xsl:with-param name="v" select="$_operation/@Kit_To"/></xsl:call-template></xsl:attribute>
				<xsl:attribute name="kk-tv"><xsl:call-template name="stripTimeNoZero"><xsl:with-param name="v" select="$_operation/@Kit_Tv"/></xsl:call-template></xsl:attribute>
				<xsl:attribute name="kk-md"><xsl:call-template name="stripWeightNoZero"><xsl:with-param name="v" select="$_product/@V_WCG_Mass"/></xsl:call-template></xsl:attribute>
				<xsl:attribute name="kk-iot" ><xsl:call-template name="concatIotNames" ><xsl:with-param name="operationId" select="$operationId"/></xsl:call-template></xsl:attribute>
				<xsl:attribute name="kk-material"><xsl:value-of select="$_material/@V_Name"/></xsl:attribute>
				<xsl:attribute name="machineName"><xsl:value-of select="$_machine/@V_description"/></xsl:attribute>

				<xsl:copy-of select="$_page/*" />
				
			</xsl:element>
			
		</xsl:for-each>

	<!-- ==================================================== -->
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="generateControlItem" >

	<xsl:param name="instructionText" />
	<xsl:param name="_measures" />
	<xsl:param name="percentage" />

	<xsl:variable name="wrapInstr0">
		<xsl:call-template name="wrapTextIterate">
			<xsl:with-param name="font" select="$fontGostItalic" />
			<xsl:with-param name="text" select="$instructionText" />
			<xsl:with-param name="maxWidth" select="65" />
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="wrapInstr" select="exsl:node-set($wrapInstr0)/*"/>
	
	<xsl:variable name="moreThanAny" select="$wrapInstr|$_measures"/> <!-- more than any of these, just to iterate -->

	<xsl:for-each select="$moreThanAny" >
		<xsl:variable name="pos" select="position()" />
		
		<xsl:variable name="instrRow" select="$wrapInstr[$pos]" />
		<xsl:variable name="measure" select="$_measures[$pos]" />

		<!-- xsl:message>generateControlItem 
			instrRow=<xsl:value-of select="$instrRow"/>
			measure=<xsl:value-of select="$measure/@V_Name"/>
		</xsl:message -->
		
		<xsl:if test="count($instrRow)!=0 or count($measure)!=0" >
		
			<xsl:element name="row">
				<xsl:attribute name="_type">
					<xsl:if test="$pos=1">C</xsl:if>
					<xsl:if test="$pos!=1">C1</xsl:if>
				</xsl:attribute>
				<xsl:attribute name="instructionText"><xsl:value-of select="$instrRow" /></xsl:attribute>
				<xsl:attribute name="toolCode"><xsl:value-of select="$measure/@V_Name" /></xsl:attribute>
				<xsl:attribute name="toolName"><xsl:value-of select="$measure/@V_description" /></xsl:attribute>
				<xsl:attribute name="percentage">
					<xsl:if test="$pos=1"><xsl:value-of select="$percentage" /></xsl:if>
				</xsl:attribute>
			</xsl:element>
			
		</xsl:if>
		
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="writeControl" >
	<xsl:param name="form" />
	<xsl:param name="globalPageNumber" />

	<xsl:variable name="isFirst" select="$form/@_page_number = 1" />

	<fo:wrapper line-height="{$h_single}" > <!-- wrap-option="no-wrap" -->
	<!-- ==================================================== -->
	<xsl:call-template name="formHeader">
		<xsl:with-param name="gost" select="'3.1502-85'" />
		<xsl:with-param name="f1_or_f3" select="'f3'" /> <!-- отменяли, но вроде должно быть -->
		<xsl:with-param name="isFirst" select="$isFirst" />
		<xsl:with-param name="formFirst">Форма 2</xsl:with-param>
		<xsl:with-param name="formNonFirst">Форма 2а</xsl:with-param>
		<xsl:with-param name="no-markers" select="true()" />
		<xsl:with-param name="organization" select="$organization"/>

		<xsl:with-param name="workShop" select="$form/@workShop"/>
		<xsl:with-param name="workArea" select="$form/@workArea"/>
		<xsl:with-param name="workPlace" select="$form/@workPlace"/>

		<xsl:with-param name="operation-number" select="$form/@operationNumber"/>

		<xsl:with-param name="part-name" select="$form/@part-name"/>
		<xsl:with-param name="part-code" select="$form/@part-code"/>
		<xsl:with-param name="page_number" select="$form/@_page_number"/>
		<xsl:with-param name="pages_total" select="$form/@_pages_total"/>
	</xsl:call-template>
	<!-- ==================================================== -->
	
	<xsl:if test="$isFirst">
		<xsl:call-template name="kkFormHeader" >
			<xsl:with-param name="form" select="$form" />
		</xsl:call-template>
	</xsl:if>

	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<fo:table-column column-width="13mm"/>
		<fo:table-column column-width="65mm"/>
		<fo:table-column column-width="65mm"/>
		<fo:table-column column-width="104mm"/>
		<fo:table-column column-width="20.8mm"/>
		<fo:table-column column-width="18.2mm"/>
		<!-- body -->
		<fo:table-body>
		
		<xsl:call-template name="kkHeader" />
				
		<xsl:for-each select="$form/*" >
			<xsl:variable name="row" select="current()" />

			<xsl:variable name="rowNumber" select="position()" />

			<xsl:call-template name="writeControlRow">
				<xsl:with-param name="row" select="$row" />
				<xsl:with-param name="rowNumber" select="$rowNumber" />
			</xsl:call-template>
			
		</xsl:for-each>	

		</fo:table-body>
	</fo:table>

	<!-- ==================================================== -->

	<xsl:call-template name="formFooter" >
		<xsl:with-param name="no-markers" select="true()" />
		<xsl:with-param name="B3f1rows" select="$isFirst" />
		<xsl:with-param name="document-type-code" select="'КК'" />
		<xsl:with-param name="document-type-title" select="'КОНТРОЛЬНАЯ КАРТА'" />
		<xsl:with-param name="document-file-name" />
		<xsl:with-param name="document-file-version" select="$globalPageNumber" />
	</xsl:call-template>
	
	<!-- ==================================================== -->
	</fo:wrapper>

	<fo:block page-break-after="always"/>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="kkFormHeader" >
	<xsl:param name="form" />

	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<fo:table-column column-width="143mm"/>
		<fo:table-column column-width="124.8mm"/>
		<fo:table-column column-width="18.2mm"/>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_single}">
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Наименование операции</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Наименование, марка материала</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">МД</fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row height="{$h_double}">
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block font-size="5mm" padding-top="2.5mm" text-align="center" >
							<xsl:value-of select="$form/@operationName"/>
					</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell" display-align="center"><fo:block text-align="center">
							<xsl:value-of select="$form/@kk-material"/>
					</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block font-size="5mm" padding-top="2.5mm" text-align="center" >
							<xsl:value-of select="$form/@kk-md"/>
					</fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	
	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<fo:table-column column-width="104mm"/>
		<fo:table-column column-width="20.8mm"/>
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="104mm"/>
		<fo:table-column column-width="39mm"/>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_single}">
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Наименование оборудования</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">То</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Тв</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center"></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Обознач. ИОТ</fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row height="{$h_triple}">
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block font-size="5mm" padding-top="4mm" text-align="center">
							<xsl:value-of select="$form/@machineName"/>
					</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block font-size="5mm" padding-top="4mm" text-align="center">
							<xsl:value-of select="$form/@kk-to"/>
					</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block font-size="5mm" padding-top="4mm" text-align="center">
							<xsl:value-of select="$form/@kk-tv"/>
					</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell" display-align="center"><fo:block text-align="center">
							<xsl:value-of select="$form/@kk-iot"/>
					</fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="kkHeader" >
	<fo:table-row height="{$h_double}">
			<fo:table-cell xsl:use-attribute-sets="cell"><fo:block padding-top="2mm" text-align="center">р</fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="cell"><fo:block padding-top="2mm" text-align="center">Контролируемые параметры</fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="cell"><fo:block padding-top="2mm" text-align="center">Код средств ТО</fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="cell"><fo:block padding-top="2mm" text-align="center">Наименование средств ТО</fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="cell"><fo:block padding-top="2mm" text-align="center">Объем и ПК</fo:block></fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="cell"><fo:block padding-top="2mm" text-align="center">То/Тв</fo:block></fo:table-cell>
	</fo:table-row>
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeControlRow" >
	<xsl:param name="row" />
	<xsl:param name="rowNumber" />

	<xsl:variable name="rowNumberText">
		<xsl:if test="number($rowNumber) &lt; 10">0</xsl:if><xsl:value-of select="$rowNumber"/>
	</xsl:variable>

	<fo:table-row height="{$h_double}">
		<fo:table-cell xsl:use-attribute-sets="cellLBR"><fo:block font-size="4.78mm" margin-left="5mm" padding-top="2.6mm"><xsl:value-of select="$rowNumberText"/></fo:block></fo:table-cell>
		<fo:table-cell xsl:use-attribute-sets="cellB"><fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm"><xsl:value-of select="$row/@instructionText" /></fo:block></fo:table-cell>
		<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick" /><fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm"><xsl:value-of select="$row/@toolCode" /></fo:block></fo:table-cell>
		<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick" /><fo:block font-size="4.78mm" margin-left="1.85mm" padding-top="2.6mm"><xsl:value-of select="$row/@toolName" /></fo:block></fo:table-cell>
		
		<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick" /><fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm" text-align="center">
			<xsl:call-template name="NormNoZero"><xsl:with-param name="v" select="$row/@percentage"/></xsl:call-template>
		</fo:block></fo:table-cell>
		
		<fo:table-cell xsl:use-attribute-sets="cellBR"><xsl:call-template name="tick" /><fo:block/></fo:table-cell>
	</fo:table-row>
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->
<xsl:variable name="w_f1" select="'13mm'" />
<xsl:variable name="w_f2" select="'10.4mm'" />
<xsl:variable name="w_f3" select="'59.8mm'" />
<xsl:variable name="w_f4" select="'13mm'" />
<xsl:variable name="w_f5" select="'174.2mm'" />
<xsl:variable name="w_f6" select="'15.6mm'" />

<xsl:variable name="w_f23" select="'70.2mm'" />
<xsl:variable name="w_f234" select="'83.2mm'" />
<xsl:variable name="w_f2345" select="'257.4mm'" />
<xsl:variable name="w_f23456" select="'273mm'" />
<!-- ============================================================================================================ -->

<xsl:template name="voGenerate" >
	<xsl:param name="operations" />

	<xsl:variable name="_process0"> <xsl:call-template name="getTopProcess" /> </xsl:variable>
	<xsl:variable name="_process" select="exsl:node-set($_process0)/*"/>

	<xsl:variable name="_rows0">
	<!-- ======================= -->

	<xsl:for-each select="$operations" >
		<xsl:sort data-type="number" select="@sortValue" />
	
		<xsl:variable name="_operation" select="current()" />

		<xsl:variable name="_instructions0"><xsl:call-template name="getInstructions"><xsl:with-param name="operationId" select="$_operation/@id"/></xsl:call-template></xsl:variable>
		<xsl:variable name="_instructions" select="exsl:node-set($_instructions0)/*"/>
		
		<xsl:variable name="_ids" select="$_operation/@id | $_instructions/@id" />
		<!-- xsl:message>voGenerate: _operation=<xsl:value-of select="$_operation/@id"/> _ids=<xsl:value-of select="count($_ids)"/>: 
			<xsl:value-of select="$_ids[1]"/> <xsl:value-of select="$_ids[2]"/> <xsl:value-of select="$_ids[3]"/>
		</xsl:message -->
		
		<xsl:variable name="_nonMachines0"><xsl:call-template name="getNonMachines"><xsl:with-param name="operationId" select="$_ids"/></xsl:call-template></xsl:variable>
		<xsl:variable name="_nonMachines" select="exsl:node-set($_nonMachines0)/*"/>
		
		<xsl:call-template name="writeSortedRowsT">
			<xsl:with-param name="_nonMachines" select="$_nonMachines" />
			<xsl:with-param name="operationNum" select="$_operation/@Kit_NumOperation" />
		</xsl:call-template>
	</xsl:for-each>

	<!-- ======================= -->
	</xsl:variable>
	<xsl:variable name="_rows" select="exsl:node-set($_rows0)/*"/>

	<xsl:variable name="_pages0">
		<xsl:call-template name="rowsToPages">
			<xsl:with-param name="rows" select="$_rows" />
			<xsl:with-param name="nFirstRows" select="16" />
			<xsl:with-param name="nNextRows" select="17" />
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="_pages" select="exsl:node-set($_pages0)/*"/>
	
	<xsl:for-each select="$_pages" >
		<xsl:variable name="_page" select="current()"/>

		<xsl:element name="form">
			<xsl:attribute name="_type">VO</xsl:attribute>
			<xsl:attribute name="_pages_total"><xsl:value-of select="count($_pages)"/></xsl:attribute>
			<xsl:attribute name="_page_number"><xsl:value-of select="position()"/></xsl:attribute>

			<xsl:attribute name="part-name"><xsl:value-of select="$_process/@V_description"/></xsl:attribute>
			<xsl:attribute name="part-code"><xsl:value-of select="$_process/@V_Name"/></xsl:attribute>

			<xsl:copy-of select="$_page/*" />
			
		</xsl:element>
		
	</xsl:for-each>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeSortedRowsT">
	<xsl:param name="_nonMachines" />
	<xsl:param name="operationNum" />
	
	<xsl:for-each select="$_nonMachines" >
		<xsl:sort select="translate(  substring(@Kit_Category,1,1),  'FACBTPM','1234567')" />

		<xsl:element name="row">
			<xsl:attribute name="_type">T</xsl:attribute>
			<xsl:attribute name="operationNumber"><xsl:value-of select="$operationNum" /></xsl:attribute>
			<xsl:attribute name="TOCode"><xsl:value-of select="@V_Name" /></xsl:attribute>
			<xsl:attribute name="count">
				<xsl:call-template name="toIntNoZero" >
					<xsl:with-param name="v" select="@ResourceQuantity" />
				</xsl:call-template>
			</xsl:attribute>
			<xsl:attribute name="TOName"><xsl:value-of select="@V_description" /></xsl:attribute>
		</xsl:element>

	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->
<xsl:variable name="w_mf1" select="'13mm'" />
<xsl:variable name="w_mf2" select="'10.4mm'" />
<xsl:variable name="w_mf3" select="'23.4mm'" />
<xsl:variable name="w_mf4" select="'10.4mm'" />
<xsl:variable name="w_mf5" select="'26mm'" />
<xsl:variable name="w_mf6" select="'18.2mm'" />
<xsl:variable name="w_mf7" select="'18.2mm'" />
<xsl:variable name="w_mf8" select="'15.6mm'" />
<xsl:variable name="w_mf9" select="'33.8mm'" />
<xsl:variable name="w_mfa" select="'62.4mm'" />
<xsl:variable name="w_mfb" select="'18.2mm'" />
<xsl:variable name="w_mfc" select="'18.2mm'" />
<xsl:variable name="w_mfd" select="'18.2mm'" />


<xsl:variable name="w_mf56" select="'44.2mm'" />

<xsl:variable name="w_mf23456789a" select="'218.4mm'" />
<xsl:variable name="w_mf345"       select="'59.8mm'" />
<xsl:variable name="w_mf6789abcd"  select="'202.8mm'" />
<xsl:variable name="w_mf23456789abcd"  select="'273mm'" />

<xsl:variable name="w_mf23"     select="'33.8mm'" />
<xsl:variable name="w_mf234"  select="'44.2mm'" />
<xsl:variable name="w_mf2345"  select="'70.2mm'" />
<xsl:variable name="w_mf23456"  select="'88.4mm'" />
<xsl:variable name="w_mf234567"  select="'106.6mm'" />
<xsl:variable name="w_mf2345678"  select="'122.2mm'" />
<xsl:variable name="w_mf23456789"  select="'156mm'" />
<xsl:variable name="w_mf23456789a"  select="'218.4mm'" />
<xsl:variable name="w_mf23456789ab"  select="'236.6mm'" />
<xsl:variable name="w_mf23456789abc"  select="'254.8mm'" />

<xsl:variable name="w_mfab"     select="'80.6mm'" />
<!-- ============================================================================================================ -->

<xsl:template name="writeVO" >
	<xsl:param name="form" />
	<xsl:param name="globalPageNumber" />

	<xsl:variable name="isFirst" select="$form/@_page_number = 1" />

	<fo:wrapper line-height="{$h_single}" > <!-- wrap-option="no-wrap" -->
	<!-- ==================================================== -->
	<xsl:call-template name="formHeader">
		<xsl:with-param name="gost" select="'3.1122-84'" />
		<!-- xsl:with-param name="f1_or_f3" select="'f3'" / -->
		<xsl:with-param name="isFirst" select="$isFirst" />
		<xsl:with-param name="formFirst">Форма 3</xsl:with-param>
		<xsl:with-param name="formNonFirst">Форма 3а</xsl:with-param>
		<xsl:with-param name="no-markers" select="true()" />
		<xsl:with-param name="organization" select="$organization"/>

		<xsl:with-param name="workShop" select="$form/@workShop"/>
		<xsl:with-param name="workArea" select="$form/@workArea"/>
		<xsl:with-param name="workPlace" select="$form/@workPlace"/>

		<xsl:with-param name="operation-number" select="$form/@operationNumber"/>

		<xsl:with-param name="part-name" select="$form/@part-name"/>
		<xsl:with-param name="part-code" select="$form/@part-code"/>
		<xsl:with-param name="page_number" select="$form/@_page_number"/>
		<xsl:with-param name="pages_total" select="$form/@_pages_total"/>
	</xsl:call-template>
	<!-- ==================================================== -->
	<xsl:call-template name="partST" />
	<!-- ==================================================== -->
	
	<xsl:for-each select="$form/*" >
		<xsl:variable name="row" select="current()" />

		<xsl:variable name="rowNumber" select="position()" />

		<xsl:call-template name="writeVoRowT">
			<xsl:with-param name="row" select="$row" />
			<xsl:with-param name="rowNumber" select="$rowNumber" />
		</xsl:call-template>
		
	</xsl:for-each>	
	
	
	<!-- ==================================================== -->

	<xsl:call-template name="formFooter" >
		<xsl:with-param name="no-markers" select="true()" />
		<xsl:with-param name="B3f1rows" select="$isFirst" />
		<xsl:with-param name="document-type-code" select="'ВО'" />
		<xsl:with-param name="document-type-title" select="'ВЕДОМОСТЬ ОСНАСТКИ'" />
		<xsl:with-param name="document-file-name" />
		<xsl:with-param name="document-file-version" select="$globalPageNumber" />
	</xsl:call-template>
	
	<!-- ==================================================== -->
	</fo:wrapper>

	<fo:block page-break-after="always"/>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeVoRowT">
	<xsl:param name="row" />
	<xsl:param name="rowNumber" />

	<xsl:variable name="rowNumberText">
		<xsl:if test="number($rowNumber) &lt; 10">0</xsl:if><xsl:value-of select="$rowNumber"/>
	</xsl:variable>
	
	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="6.0mm"/>
		<fo:table-column column-width="7.0mm"/>
		<fo:table-column column-width="{$w_f2}"/>
		<fo:table-column column-width="{$w_f3}"/>
		<fo:table-column column-width="{$w_f4}"/>
		<fo:table-column column-width="{$w_f5}"/>
		<fo:table-column column-width="{$w_f6}"/>
		<!-- body -->
		<fo:table-body>

			<fo:table-row height="{$h_double}">
				<fo:table-cell xsl:use-attribute-sets="cellLB"><fo:block font-size="4.78mm" margin-left="2mm" padding-top="2.6mm"><xsl:value-of select="$row/@_type"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block font-size="4.78mm" margin-left="1mm" padding-top="2.6mm"><xsl:value-of select="$rowNumberText"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@operationNumber"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick" /><fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm"><xsl:value-of select="$row/@TOCode"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick" /><fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@count"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick" /><fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm"><xsl:value-of select="$row/@TOName"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR"><xsl:call-template name="tick" /><fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm"></fo:block></fo:table-cell>
			</fo:table-row>
		
		</fo:table-body>
	</fo:table>


</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partST" >

	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="{$w_f1}"/>
		<fo:table-column column-width="{$w_f2}"/>
		<fo:table-column column-width="{$w_f3}"/>
		<fo:table-column column-width="{$w_f4}"/>
		<fo:table-column column-width="{$w_f5}"/>
		<fo:table-column column-width="{$w_f6}"/>
		<!-- body -->
		<fo:table-body>
		
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="5mm">С</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">НПП</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Обозначение ДСЕ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell" number-columns-spanned="2"><fo:block text-align="center">Наименование ДСЕ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">КП</fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="5mm">Т</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Опер</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Обозначение ТО</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Кол.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell" number-columns-spanned="2"><fo:block text-align="center">Наименование ТО</fo:block></fo:table-cell>
			</fo:table-row>

		</fo:table-body>
	</fo:table>
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="vmGenerate" >

	<xsl:variable name="_process0"> <xsl:call-template name="getTopProcess" /> </xsl:variable>
	<xsl:variable name="_process" select="exsl:node-set($_process0)/*"/>

	<!-- fo:marker marker-class-name="document-type-code">ВМ</fo:marker>
	<fo:marker marker-class-name="document-type-title">ВЕДОМОСТЬ МАТЕРИАЛОВ</fo:marker>
	
	<fo:marker marker-class-name="organization"><xsl:value-of select="$organization"/></fo:marker>
	<fo:marker marker-class-name="part-name"><xsl:value-of select="$_process/@V_description"/></fo:marker>
	<fo:marker marker-class-name="part-code"><xsl:value-of select="$_process/@V_Name"/></fo:marker -->

	
	<xsl:variable name="_rows0">
	<!-- ================================================= -->
	
	<xsl:variable name="_material0"> <xsl:call-template name="getMainMaterial" /> </xsl:variable>
	<xsl:variable name="_material" select="exsl:node-set($_material0)/*"/>
	
	<xsl:variable name="bar0"><xsl:call-template name="getBar" /></xsl:variable>
	<xsl:variable name="bar" select="exsl:node-set($bar0)/*"/>

	<xsl:if test="$_material">
		<xsl:variable name="matTitle" select="concat($_material/@V_Name,' ',$_material/@V_description)" />
		<xsl:variable name="matPartNumber" select="$_material/@Kit_M_PartNumber" />
		<xsl:variable name="matCode" select="$_material/@Kit_MatCode" />

		<xsl:variable name="_product0"> <xsl:call-template name="getTopProduct" /> </xsl:variable>
		<xsl:variable name="_product" select="exsl:node-set($_product0)/*"/>
		
		<xsl:element name="row">
			<xsl:attribute name="_type">M1</xsl:attribute>
			<xsl:attribute name="title"><xsl:value-of select="$matTitle" /></xsl:attribute>
		</xsl:element>

		<xsl:element name="row">
			<xsl:attribute name="_type">M2</xsl:attribute>
			<xsl:attribute name="kodmat"><xsl:value-of select="$_material/@Kit_MatCode" /></xsl:attribute>
			<xsl:attribute name="unit"><xsl:value-of select="$bar/@unitValue"/></xsl:attribute>

			<xsl:attribute name="md"><xsl:call-template name="stripWeightNoZero"><xsl:with-param name="v" select="$_product/@V_WCG_Mass"/></xsl:call-template></xsl:attribute>
			
			<xsl:attribute name="nrash"><xsl:call-template name="NormNoZero"><xsl:with-param name="v" select="$bar/@Kit_MatConsumptionRate"/></xsl:call-template></xsl:attribute>
			<xsl:attribute name="kim"><xsl:call-template name="NormNoZero"><xsl:with-param name="v" select="$bar/@Kit_UsageCoeffic"/></xsl:call-template></xsl:attribute>
			
			<xsl:attribute name="zagsize"><xsl:call-template name="getBarSize"><xsl:with-param name="bar" select="$bar"/></xsl:call-template></xsl:attribute>
			<xsl:attribute name="kd"><xsl:value-of select="$bar/@Kit_NumberOfParts" /></xsl:attribute>
			<xsl:attribute name="mz"><xsl:call-template name="stripWeightNoZero"><xsl:with-param name="v" select="$bar/@Kit_StockWeight"/></xsl:call-template></xsl:attribute>

		</xsl:element>

		<xsl:element name="rrow" />
		
	</xsl:if>

	<xsl:variable name="_operations0"><xsl:call-template name="getAllOperations"/></xsl:variable>
	<xsl:variable name="_operations" select="exsl:node-set($_operations0)/*"/>
	
	<xsl:variable name="_materials0"><xsl:call-template name="getMaterials"><xsl:with-param name="operationIds" select="$_operations/@id"/></xsl:call-template></xsl:variable>
	<xsl:variable name="_materials" select="exsl:node-set($_materials0)/*"/>

	<xsl:for-each select="$_materials" >
		<!-- xsl:call-template name="messageObject">
			<xsl:with-param name="title" select="'writeCommonMaterial'"/>
			<xsl:with-param name="object" select="current()"/>
		</xsl:call-template -->

		<xsl:call-template name="writeCommonMaterial">
			<xsl:with-param name="_material" select="current()" />
		</xsl:call-template>
		
	</xsl:for-each>
	<!-- ================================================= -->
	</xsl:variable>
	<xsl:variable name="_rows" select="exsl:node-set($_rows0)/*"/>

	<xsl:variable name="_pages0">
		<xsl:call-template name="rowsToPages">
			<xsl:with-param name="rows" select="$_rows" />
			<xsl:with-param name="nFirstRows" select="14" />
			<xsl:with-param name="nNextRows" select="15" /> <!-- 15 -->
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="_pages" select="exsl:node-set($_pages0)/*"/>
	
	<xsl:for-each select="$_pages" >
		<xsl:variable name="_page" select="current()"/>

		<xsl:element name="form">
			<xsl:attribute name="_type">VM</xsl:attribute>
			<xsl:attribute name="_pages_total"><xsl:value-of select="count($_pages)"/></xsl:attribute>
			<xsl:attribute name="_page_number"><xsl:value-of select="position()"/></xsl:attribute>

			<xsl:attribute name="part-name"><xsl:value-of select="$_process/@V_description"/></xsl:attribute>
			<xsl:attribute name="part-code"><xsl:value-of select="$_process/@V_Name"/></xsl:attribute>

			<xsl:copy-of select="$_page/*" />
			
		</xsl:element>
		
	</xsl:for-each>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeCommonMaterial">
	<xsl:param name="_material" />

	<xsl:element name="row">
		<xsl:attribute name="_type">M1</xsl:attribute>
		<xsl:attribute name="title"><xsl:value-of select="concat($_material/@V_Name,' ',$_material/@V_description)" /></xsl:attribute>
	</xsl:element>

	<xsl:element name="row">
		<xsl:attribute name="_type">M2</xsl:attribute>
		<xsl:attribute name="kodmat"><xsl:value-of select="$_material/@Kit_MatCode" /></xsl:attribute>
		<xsl:attribute name="unit"><xsl:value-of select="$_material/@QuantityUnit" /></xsl:attribute>
		<xsl:attribute name="nrash"><xsl:call-template name="NormNoZero"><xsl:with-param name="v" select="$_material/@nrash"/></xsl:call-template></xsl:attribute>
	</xsl:element>


	<xsl:element name="rrow" />

	<!-- ======= -->
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->
<xsl:variable name="w_mf1" select="'13mm'" />
<xsl:variable name="w_mf2" select="'10.4mm'" />
<xsl:variable name="w_mf3" select="'23.4mm'" />
<xsl:variable name="w_mf4" select="'10.4mm'" />
<xsl:variable name="w_mf5" select="'26mm'" />
<xsl:variable name="w_mf6" select="'18.2mm'" />
<xsl:variable name="w_mf7" select="'18.2mm'" />
<xsl:variable name="w_mf8" select="'15.6mm'" />
<xsl:variable name="w_mf9" select="'33.8mm'" />
<xsl:variable name="w_mfa" select="'62.4mm'" />
<xsl:variable name="w_mfb" select="'18.2mm'" />
<xsl:variable name="w_mfc" select="'18.2mm'" />
<xsl:variable name="w_mfd" select="'18.2mm'" />


<xsl:variable name="w_mf56" select="'44.2mm'" />

<xsl:variable name="w_mf23456789a" select="'218.4mm'" />
<xsl:variable name="w_mf345"       select="'59.8mm'" />
<xsl:variable name="w_mf6789abcd"  select="'202.8mm'" />
<xsl:variable name="w_mf23456789abcd"  select="'273mm'" />

<xsl:variable name="w_mf23"     select="'33.8mm'" />
<xsl:variable name="w_mf234"  select="'44.2mm'" />
<xsl:variable name="w_mf2345"  select="'70.2mm'" />
<xsl:variable name="w_mf23456"  select="'88.4mm'" />
<xsl:variable name="w_mf234567"  select="'106.6mm'" />
<xsl:variable name="w_mf2345678"  select="'122.2mm'" />
<xsl:variable name="w_mf23456789"  select="'156mm'" />
<xsl:variable name="w_mf23456789a"  select="'218.4mm'" />
<xsl:variable name="w_mf23456789ab"  select="'236.6mm'" />
<xsl:variable name="w_mf23456789abc"  select="'254.8mm'" />

<xsl:variable name="w_mfab"     select="'80.6mm'" />
<!-- ============================================================================================================ -->

<xsl:template name="writeVM" >
	<xsl:param name="form" />
	<xsl:param name="globalPageNumber" />

	<xsl:variable name="isFirst" select="$form/@_page_number = 1" />

	<fo:wrapper line-height="{$h_single}" > <!-- wrap-option="no-wrap" -->
	<!-- ==================================================== -->
	<xsl:call-template name="formHeader">
		<xsl:with-param name="gost" select="'3.1123-84'" />
		<!-- xsl:with-param name="f1_or_f3" select="'f3'" / -->
		<xsl:with-param name="isFirst" select="$isFirst" />
		<xsl:with-param name="formFirst">Форма 2</xsl:with-param>
		<xsl:with-param name="formNonFirst">Форма 2а</xsl:with-param>
		<xsl:with-param name="no-markers" select="true()" />
		<xsl:with-param name="organization" select="$organization"/>

		<xsl:with-param name="workShop" select="$form/@workShop"/>
		<xsl:with-param name="workArea" select="$form/@workArea"/>
		<xsl:with-param name="workPlace" select="$form/@workPlace"/>

		<xsl:with-param name="operation-number" select="$form/@operationNumber"/>

		<xsl:with-param name="part-name" select="$form/@part-name"/>
		<xsl:with-param name="part-code" select="$form/@part-code"/>
		<xsl:with-param name="page_number" select="$form/@_page_number"/>
		<xsl:with-param name="pages_total" select="$form/@_pages_total"/>
	</xsl:call-template>
	<!-- ==================================================== -->
	<xsl:call-template name="partSMPSh" />
	<!-- ==================================================== -->
	
	<xsl:for-each select="$form/*" >
		<xsl:variable name="row" select="current()" />

		<xsl:variable name="rowNumber" select="position()" />

		<xsl:call-template name="writeMaterialRow">
			<xsl:with-param name="row" select="$row" />
			<xsl:with-param name="rowNumber" select="$rowNumber" />
		</xsl:call-template>
		
	</xsl:for-each>	
	
	
	<!-- ==================================================== -->

	<xsl:call-template name="formFooter" >
		<xsl:with-param name="no-markers" select="true()" />
		<xsl:with-param name="B3f1rows" select="$isFirst" />
		<xsl:with-param name="document-type-code" select="'ВМ'" />
		<xsl:with-param name="document-type-title" select="'ВЕДОМОСТЬ МАТЕРИАЛОВ'" />
		<xsl:with-param name="document-file-name" />
		<xsl:with-param name="document-file-version" select="$globalPageNumber" />
	</xsl:call-template>
	
	<!-- ==================================================== -->
	</fo:wrapper>

	<fo:block page-break-after="always"/>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeMaterialRow">
	<xsl:param name="row" />
	<xsl:param name="rowNumber" />

	<xsl:variable name="rowNumberText">
		<xsl:if test="number($rowNumber) &lt; 10">0</xsl:if><xsl:value-of select="$rowNumber"/>
	</xsl:variable>
	
	<xsl:choose>
		
		<xsl:when test="$row/@_type='M1'">
			<xsl:call-template name="writeRowM1">
				<xsl:with-param name="row" select="$row" />
				<xsl:with-param name="rowNumberText" select="$rowNumberText" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:when test="$row/@_type='M2'">
			<xsl:call-template name="writeRowM2">
				<xsl:with-param name="row" select="$row" />
				<xsl:with-param name="rowNumberText" select="$rowNumberText" />
			</xsl:call-template>
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:call-template name="writeRowSpace">
				<xsl:with-param name="row" select="$row" />
				<xsl:with-param name="rowNumberText" select="$rowNumberText" />
			</xsl:call-template>
		</xsl:otherwise>
		
	</xsl:choose>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeRowM1">
	<xsl:param name="row" />
	<xsl:param name="rowNumberText" />
	
	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="6.0mm"/>
		<fo:table-column column-width="7.0mm"/>
		<fo:table-column column-width="273mm"/>
		<!-- body -->
		<fo:table-body>

			<fo:table-row height="{$h_double}">
				<fo:table-cell xsl:use-attribute-sets="cellLB"><fo:block font-size="4.78mm" margin-left="2mm" padding-top="2.6mm"><xsl:value-of select="'M'"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block font-size="4.78mm" margin-left="1mm" padding-top="2.6mm"><xsl:value-of select="$rowNumberText"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR">
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf2" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf234" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf2345" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf234567" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf2345678" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456789" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456789a" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456789ab" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456789abc" /></xsl:call-template>
					<fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm"><xsl:value-of select="$row/@title"/></fo:block>
				</fo:table-cell>
			</fo:table-row>
		
		</fo:table-body>
	</fo:table>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeRowM2">
	<xsl:param name="row" />
	<xsl:param name="rowNumberText" />

	<xsl:variable name="nrash1"><xsl:call-template name="noZeroOne"><xsl:with-param name="v" select="$row/@nrash"/></xsl:call-template></xsl:variable>
	<xsl:variable name="kim1"><xsl:call-template name="noZeroOne"><xsl:with-param name="v" select="$row/@kim"/></xsl:call-template></xsl:variable>
	
	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="6.0mm"/>
		<fo:table-column column-width="7.0mm"/>
		<fo:table-column column-width="{$w_mf23}"/>
		<fo:table-column column-width="{$w_mf4}"/>
		<fo:table-column column-width="{$w_mf5}"/>
		<fo:table-column column-width="{$w_mf6}"/>
		<fo:table-column column-width="{$w_mf7}"/>

		<fo:table-column column-width="{$w_mf8}"/>
		<fo:table-column column-width="{$w_mf9}"/>
		<fo:table-column column-width="{$w_mfa}"/>
		<fo:table-column column-width="{$w_mfb}"/>
		<fo:table-column column-width="{$w_mfc}"/>
		<fo:table-column column-width="{$w_mfd}"/>
		<!-- body -->
		<fo:table-body>

			<fo:table-row height="{$h_double}">
				<fo:table-cell xsl:use-attribute-sets="cellLB"><fo:block font-size="4.78mm" margin-left="2mm" padding-top="2.6mm"><xsl:value-of select="'M'"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block font-size="4.78mm" margin-left="1mm" padding-top="2.6mm"><xsl:value-of select="$rowNumberText"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><fo:block font-size="4.78mm" margin-left="1.9mm" padding-top="2.6mm"><xsl:value-of select="$row/@kodmat"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@unit"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@md"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@en"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@nrash"/></fo:block></fo:table-cell>
				
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@kim"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@kodzag"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@zagsize"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@kd"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR"><xsl:call-template name="tick"/><fo:block font-size="4.78mm" padding-top="2.6mm" text-align="center"><xsl:value-of select="$row/@mz"/></fo:block></fo:table-cell>

			</fo:table-row>
		
		</fo:table-body>
	</fo:table>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="writeRowSpace">
	<xsl:param name="row" />
	<xsl:param name="rowNumberText" />
	
	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="6.0mm"/>
		<fo:table-column column-width="7.0mm"/>
		<fo:table-column column-width="273mm"/>
		<!-- body -->
		<fo:table-body>

			<fo:table-row height="{$h_data}">
				<fo:table-cell xsl:use-attribute-sets="cellLB"><fo:block font-size="4.78mm" margin-left="2mm" padding-top="2.6mm"><xsl:value-of select="''"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block font-size="4.78mm" margin-left="1mm" padding-top="2.6mm"><xsl:value-of select="$rowNumberText"/></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR">
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf2" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf234" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf2345" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf234567" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf2345678" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456789" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456789a" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456789ab" /></xsl:call-template>
					<xsl:call-template name="tick" ><xsl:with-param name="pos" select="$w_mf23456789abc" /></xsl:call-template>
					<fo:block/>
				</fo:table-cell>
			</fo:table-row>
		
		</fo:table-body>
	</fo:table>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partSMPSh" >

	<!-- ====================== -->

	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="{$w_mf1}"/>
		<fo:table-column column-width="{$w_mf2}"/>
		<fo:table-column column-width="{$w_mf345}"/>
		<fo:table-column column-width="{$w_mf6789abcd}"/>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="5mm">С</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">НПП</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Обозначение ДСЕ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Наименование ДСЕ</fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>

	<!-- ====================== -->

	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="{$w_mf1}"/>
		<fo:table-column column-width="{$w_mf23}"/>
		<fo:table-column column-width="{$w_mf4}"/>
		<fo:table-column column-width="{$w_mf5}"/>
		<fo:table-column column-width="{$w_mf6}"/>
		<fo:table-column column-width="{$w_mf7}"/>
		<fo:table-column column-width="{$w_mf8}"/>
		<fo:table-column column-width="{$w_mf9}"/>
		<fo:table-column column-width="{$w_mfab}"/>
		<fo:table-column column-width="{$w_mfc}"/>
		<fo:table-column column-width="{$w_mfd}"/>
		<!-- body -->
		<fo:table-body>

			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="2"><fo:block margin-left="5mm">М</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell" number-columns-spanned="10"><fo:block text-align="center">Наименование, марка</fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Код</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">ЕВ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">МД</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">ЕН</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Н.расх.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">КИМ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Код загот.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Профиль и размеры</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">КД</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">МЗ</fo:block></fo:table-cell>
			</fo:table-row>

		</fo:table-body>
	</fo:table>

	<!-- ====================== -->

	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="{$w_mf1}"/>
		<fo:table-column column-width="{$w_mf23456789a}"/>
		<fo:table-column column-width="{$w_mfb}"/>
		<fo:table-column column-width="{$w_mfc}"/>
		<fo:table-column column-width="{$w_mfd}"/>
		<!-- body -->
		<fo:table-body>

			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="5mm">П</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Куда входит</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">КСЕ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">КИ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Н.расх.</fo:block></fo:table-cell>
			</fo:table-row>

		</fo:table-body>
	</fo:table>

	<!-- ====================== -->

	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="{$w_mf1}"/>
		<fo:table-column column-width="{$w_mf23456789abcd}"/>
		<!-- body -->
		<fo:table-body>

			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="5mm">Ш</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block text-align="center">Маршрут</fo:block></fo:table-cell>
			</fo:table-row>

		</fo:table-body>
	</fo:table>

	<!-- ====================== -->

</xsl:template>

<!-- ============================================================================================================ -->

<!-- ======  DEBUG  ============================================================================================= -->

<xsl:variable name="debug_background" >
<xsl:if test="$use_debug_background!=0" ><xsl:value-of select="'#c0ffff'"/></xsl:if>
<xsl:if test="$use_debug_background= 0" ><xsl:value-of select="'transparent'"/></xsl:if>
</xsl:variable>

<xsl:variable name="debug_mark" >
<xsl:if test="$use_debug_mark!=0" ><xsl:value-of select="'00000'"/></xsl:if>
<xsl:if test="$use_debug_mark= 0" ><xsl:value-of select="''"/></xsl:if>
</xsl:variable>

<!-- ======  FOR MEASURE.XSL ONLY  ============================================================================== -->

<xsl:variable name="skip-markers" select="false()" />

<!-- ======  BORDERS  =========================================================================================== -->

<xsl:variable name="border_thickness" select="'0.5mm'" />

<!-- for border_thickness 0.5mm -->
<xsl:variable name="h_single" select="'3.9mm'" />
<xsl:variable name="h_double" select="'7.8mm'" />
<xsl:variable name="h_double" select="'7.8mm'" />
<xsl:variable name="h_triple" select="'11.7mm'" />
<xsl:variable name="h_data" select="'8.3mm'" />

<xsl:variable name="border_color" select="'#c0c0c0'" />

<!-- ======  CELLS  ============================================================================================= -->

<xsl:attribute-set name="data_cell">
	<xsl:attribute name="padding-left">1.5mm</xsl:attribute>
	<xsl:attribute name="padding-right">1mm</xsl:attribute>
	<xsl:attribute name="font-size">4.78mm</xsl:attribute>
	<xsl:attribute name="color">black</xsl:attribute>
</xsl:attribute-set>

<!-- xsl:attribute-set name="data_cell">
	<xsl:attribute name="padding-left">1.5mm</xsl:attribute>
	<xsl:attribute name="padding-right">1mm</xsl:attribute>
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="color">black</xsl:attribute>
</xsl:attribute-set -->

<xsl:attribute-set name="label_cell">
	<xsl:attribute name="border-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-style">solid</xsl:attribute>
	<xsl:attribute name="border-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="font-size">4.78mm</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cell">
	<xsl:attribute name="border-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-style">solid</xsl:attribute>
	<xsl:attribute name="border-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellTB">
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="border-top-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-top-style">solid</xsl:attribute>
	<xsl:attribute name="border-top-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="border-bottom-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	<xsl:attribute name="border-bottom-color"><xsl:value-of select="$border_color"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellLB">
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="border-left-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-left-style">solid</xsl:attribute>
	<xsl:attribute name="border-left-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="border-bottom-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	<xsl:attribute name="border-bottom-color"><xsl:value-of select="$border_color"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellB">
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="border-bottom-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	<xsl:attribute name="border-bottom-color"><xsl:value-of select="$border_color"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellTBR">
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="border-top-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-top-style">solid</xsl:attribute>
	<xsl:attribute name="border-top-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="border-bottom-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	<xsl:attribute name="border-bottom-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="border-right-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-right-style">solid</xsl:attribute>
	<xsl:attribute name="border-right-color"><xsl:value-of select="$border_color"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellLR">
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="border-left-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-left-style">solid</xsl:attribute>
	<xsl:attribute name="border-left-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="border-right-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-right-style">solid</xsl:attribute>
	<xsl:attribute name="border-right-color"><xsl:value-of select="$border_color"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellL">
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="border-left-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-left-style">solid</xsl:attribute>
	<xsl:attribute name="border-left-color"><xsl:value-of select="$border_color"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellLBR">
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="border-left-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-left-style">solid</xsl:attribute>
	<xsl:attribute name="border-left-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="border-bottom-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	<xsl:attribute name="border-bottom-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="border-right-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-right-style">solid</xsl:attribute>
	<xsl:attribute name="border-right-color"><xsl:value-of select="$border_color"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellBR">
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="border-bottom-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	<xsl:attribute name="border-bottom-color"><xsl:value-of select="$border_color"/></xsl:attribute>
	<xsl:attribute name="border-right-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-right-style">solid</xsl:attribute>
	<xsl:attribute name="border-right-color"><xsl:value-of select="$border_color"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellR">
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
	<xsl:attribute name="border-right-width"><xsl:value-of select="$border_thickness"/></xsl:attribute>
	<xsl:attribute name="border-right-style">solid</xsl:attribute>
	<xsl:attribute name="border-right-color"><xsl:value-of select="$border_color"/></xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="cellNoB">
	<!-- xsl:attribute name="padding-left">3mm</xsl:attribute>
	<xsl:attribute name="padding-right">3mm</xsl:attribute -->
	<xsl:attribute name="font-size">3.5mm</xsl:attribute>
</xsl:attribute-set>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:variable name="_top_process0"> <xsl:call-template name="getTopProcess" /> </xsl:variable>
<xsl:variable name="_top_process" select="exsl:node-set($_top_process0)/*"/>

<xsl:variable name="is_assembly" select="contains(local-name($_top_process),'Assembly')"/>

<!-- ======= -->

<xsl:variable name="n_sborka" >
	<xsl:if test="$is_assembly">1</xsl:if>
	<xsl:if test="not($is_assembly)">0</xsl:if>
</xsl:variable>

<!-- ======= -->

<xsl:variable name="n_KM_rows" select="$n_sborka" />

<xsl:variable name="h_KM_row" select="4.4" />

<!-- ======= -->

<xsl:variable name="n_M1M2_rows" >
	<xsl:if test="$is_assembly">0</xsl:if>
	<xsl:if test="not($is_assembly)">1</xsl:if>
</xsl:variable>

<xsl:variable name="h_M1M2_row" select="17.1" />

<!-- ============================================================================================================ -->
<!--
first-top = 57.35 + nesborka*17.1 + sborka*4.4
first-bottom = 12.4 - sborka*3.7
nonfirst-top = 48.55 + 4.4

sborka
	first-top = 61.75
	first-bottom = 8.7
	nonfirst-top = 52.95

not(sorka)
	first-top = 74.45
	first-bottom = 12.4
	nonfirst-top = 52.95
-->
<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="messageObject">
	<xsl:param name="title"/>
	<xsl:param name="object"/>
	
	<xsl:message>
	
		<xsl:value-of select="$title"/><xsl:value-of select="'&#10;'"/>
		<xsl:for-each select="$object/@*">
			<xsl:variable name="name" select="name(current())" />
			<xsl:variable name="value" select="current()" />
			<xsl:value-of select="'    '"/><xsl:value-of select="$name"/>=<xsl:value-of select="$value"/><xsl:value-of select="'&#10;'"/>
		</xsl:for-each>
		
	</xsl:message>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="use_marker">
	<xsl:param name="no-markers"/>
	<xsl:param name="name"/>
	<xsl:param name="value"/>
	
	<xsl:if test="not($skip-markers)" >
		<xsl:if test="not($no-markers)" >
			<fo:retrieve-marker retrieve-class-name="{$name}"/>
		</xsl:if>
		<xsl:if test="$no-markers" >
			<xsl:value-of select="$value" />
		</xsl:if>
	</xsl:if>
	<xsl:if test="$skip-markers" >
		?
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="stripTags" >
	<xsl:param name="s" />

	<xsl:variable name="a"  select="substring-before($s,'&lt;')" />
	<xsl:variable name="b0"  select="substring-after($s,'&lt;')" />

	<xsl:choose>
		
		<xsl:when test="contains($b0,'&gt;')" >
			<xsl:variable name="b"  select="substring-after($b0,'&gt;')" />
			<xsl:call-template name="stripTags"><xsl:with-param name="s" select="concat($a,' ',$b)" /></xsl:call-template>
			<!-- xsl:value-of select="concat($a,'@',$b)" / -->
		</xsl:when>
		
		<xsl:otherwise>
			<xsl:value-of select="normalize-space($s)" />
		</xsl:otherwise>
		
	</xsl:choose>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="NormNoZero" > <!-- CLEAR -->
	<xsl:param name="v" />
	
	<xsl:variable name="v1" select="normalize-space( translate($v,'.',',') )" />
	
	<xsl:if test=" $v1!='0' and $v1!='0.0' and $v1!='0,0' and $v1!='NaN' " >
		<xsl:value-of select="$v1" />
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="noZeroOne" > <!-- CLEAR --> <!-- нужно ли в ВМ ? -->
	<xsl:param name="v" />
	
	<xsl:variable name="v1" select="normalize-space($v)" />
	
	<xsl:if test=" $v1!='0' and $v1!='0.0' and $v1!='0,0' and $v1!='1' and $v1!='NaN' " >
		<xsl:value-of select="$v1" />
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="stripRevision" >
	<xsl:param name="str" />

	<xsl:variable name="cutSpace">
		<xsl:call-template name="cutTail" >
			<xsl:with-param name="str" select="$str"/>
			<xsl:with-param name="sep" select="' '"/>
		</xsl:call-template>
	</xsl:variable>

	<xsl:variable name="cutDot">
		<xsl:call-template name="cutTail" >
			<xsl:with-param name="str" select="$str"/>
			<xsl:with-param name="sep" select="'.'"/>
		</xsl:call-template>
	</xsl:variable>
	
	<xsl:variable name="result">

		<xsl:choose>
		
			<xsl:when test="string-length($cutSpace) &gt; string-length($cutDot)" >
				<xsl:value-of select="$cutSpace" />
			</xsl:when>
		
			<xsl:when test="string-length($cutDot) &gt; string-length($cutSpace)" >
				<xsl:value-of select="$cutDot" />
			</xsl:when>
			
			<xsl:otherwise>
				<xsl:value-of select="$str" />
			</xsl:otherwise>
			
		</xsl:choose>

	</xsl:variable>

	<xsl:value-of select="$result" />
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="cutTail" >
	<xsl:param name="str" />
	<xsl:param name="sep" />
	
	<xsl:choose>
	
		<!-- Дорезали до пустой строки, СТОП -->
		<xsl:when test="string-length($str)=0" >
			<xsl:value-of select="$str" />
		</xsl:when>
		
		<!-- Дорезали до сепаратора, отрезаем и на этом СТОП -->
		<xsl:when test="substring($str,string-length($str))=$sep" >
			<xsl:value-of select="substring($str,1,string-length($str)-1)" />
		</xsl:when>

		<xsl:otherwise>
			<xsl:call-template name="cutTail">
				<xsl:with-param name="str" select="substring($str,1,string-length($str)-1)" />
				<xsl:with-param name="sep" select="$sep" />
			</xsl:call-template>
		</xsl:otherwise>
		
	</xsl:choose>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="stripWeightNoZero" > <!-- CLEAR -->
	<xsl:param name="v" />
	
	<xsl:if test="string-length($v)" >
	
		<xsl:variable name="vdot" select="translate($v,',','.')" />
		<xsl:variable name="kg" select="substring-before($vdot,'kg')" />
		<xsl:variable name="g" select="substring-before($vdot,'g')" />

		<xsl:variable name="selected">
			<xsl:choose>
				<xsl:when test="string-length($kg)"><xsl:value-of select="$kg" /></xsl:when>
				<xsl:when test="string-length($g)"><xsl:value-of select="$g div 1000" /></xsl:when>
				<xsl:otherwise><xsl:value-of select="$vdot" /></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="number" select="translate(format-number($selected,'0.###'),'.',',')" />

		<xsl:if test="$number!='0' and $number!='0,0' and $number!='NaN'" >
			<xsl:value-of select="$number" />
		</xsl:if>

	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="stripLengthNoZero" > <!-- CLEAR -->
	<xsl:param name="v" />
	
	<xsl:variable name="vdot" select="translate($v,',','.')" />
	<xsl:variable name="mm" select="substring-before($vdot,'mm')" />
	<xsl:variable name="cm" select="substring-before($vdot,'cm')" />

	<xsl:variable name="selected">
		<xsl:choose>
			<xsl:when test="string-length($cm)"><xsl:value-of select="$cm*10" /></xsl:when>
			<xsl:when test="string-length($mm)"><xsl:value-of select="$mm" /></xsl:when>
			<xsl:otherwise><xsl:value-of select="$vdot" /></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:variable name="number" select="translate(format-number($selected,'0.###'),'.',',')" />

	<xsl:if test="$number!='0' and $number!='0,0' and $number!='NaN'" >
		<xsl:value-of select="$number" />
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="stripTimeNoZero" > <!-- CLEAR -->
	<xsl:param name="v" />
	
	<xsl:variable name="vdot" select="translate($v,',','.')" />
	<xsl:variable name="minutes" select="substring-before($vdot,'mn')" />
	<xsl:variable name="seconds" select="substring-before($vdot,'s')" />

	<xsl:variable name="selected">
		<xsl:choose>
			<xsl:when test="string-length($minutes)"><xsl:value-of select="$minutes" /></xsl:when>
			<xsl:when test="string-length($seconds)"><xsl:value-of select="$seconds div 60" /></xsl:when>
			<xsl:otherwise><xsl:value-of select="$vdot" /></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<!-- xsl:value-of select="translate(format-number($selected,'0.##'),'.',',')" / -->
	<xsl:variable name="number" select="translate(format-number($selected,'0.##'),'.',',')" />
	
	<xsl:if test="$number!='0' and $number!='0,0' and $number!='NaN'" >
		<xsl:value-of select="$number" />
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="timeSecToMinNoZero" > <!-- CLEAR -->
	<xsl:param name="v" />
	
	<xsl:variable name="vdot" select="translate($v,',','.')" />

	<xsl:variable name="number" select="translate(format-number($vdot div 60,'0.##'),'.',',')" />
	
	<xsl:if test="$number!='0' and $number!='0,0' and $number!='NaN'" >
		<xsl:value-of select="$number" />
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="toIntNoZero" > <!-- CLEAR -->
	<xsl:param name="v" />

	<xsl:if test="string-length($v)!=0" >
	
		<xsl:variable name="vdot" select="translate($v,',','.')" />
		
		<xsl:variable name="number" select="translate(format-number($vdot,'#'),'.',',')" />
		
		<xsl:if test="$number!='0' and $number!='0,0' and $number!='NaN'" >
			<xsl:value-of select="$number" />
		</xsl:if>
		
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="mul1000NoZero" > <!-- CLEAR -->
	<xsl:param name="v" />
	
	<xsl:if test="string-length($v)!=0" >

		<xsl:variable name="vdot" select="translate($v,',','.')" />

		<xsl:variable name="number" select="translate(format-number($vdot * 1000,'0.##'),'.',',')" />
		
		<xsl:if test="$number!='0' and $number!='0,0' and $number!='NaN'" >
			<xsl:value-of select="$number" />
		</xsl:if>

	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="percentEncode" >
	<xsl:param name="str" />
	
	<!-- xsl:value-of select="'qqq='" / -->
	
	<xsl:if test="string-length($str) &gt; 0" >
		<xsl:variable name="head" select="substring($str,1,1)" />
		<xsl:variable name="tail" select="substring($str,2)" />
		<xsl:variable name="headTran"><xsl:call-template name="percentEncodeChar"><xsl:with-param name="ch" select="$head"/></xsl:call-template></xsl:variable>
		<xsl:variable name="tailTran"><xsl:call-template name="percentEncode"><xsl:with-param name="str" select="$tail"/></xsl:call-template></xsl:variable>
		<xsl:value-of select="concat($headTran,$tailTran)" />
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="percentEncodeChar" >
	<xsl:param name="ch" />
	
	<xsl:choose>
		<xsl:when test="$ch=' '"><xsl:value-of select="'%20'"/></xsl:when>
		<xsl:when test="$ch='\'"><xsl:value-of select="'/'"/></xsl:when>
		<xsl:when test="$ch='А'"><xsl:value-of select="'%D0%90'"/></xsl:when>
		<xsl:when test="$ch='Б'"><xsl:value-of select="'%D0%91'"/></xsl:when>
		<xsl:when test="$ch='В'"><xsl:value-of select="'%D0%92'"/></xsl:when>
		<xsl:when test="$ch='Г'"><xsl:value-of select="'%D0%93'"/></xsl:when>
		<xsl:when test="$ch='Д'"><xsl:value-of select="'%D0%94'"/></xsl:when>
		<xsl:when test="$ch='Е'"><xsl:value-of select="'%D0%95'"/></xsl:when>
		<xsl:when test="$ch='Ё'"><xsl:value-of select="'%D0%81'"/></xsl:when>
		<xsl:when test="$ch='Ж'"><xsl:value-of select="'%D0%96'"/></xsl:when>
		<xsl:when test="$ch='З'"><xsl:value-of select="'%D0%97'"/></xsl:when>
		<xsl:when test="$ch='И'"><xsl:value-of select="'%D0%98'"/></xsl:when>
		<xsl:when test="$ch='Й'"><xsl:value-of select="'%D0%99'"/></xsl:when>
		<xsl:when test="$ch='К'"><xsl:value-of select="'%D0%9A'"/></xsl:when>
		<xsl:when test="$ch='Л'"><xsl:value-of select="'%D0%9B'"/></xsl:when>
		<xsl:when test="$ch='М'"><xsl:value-of select="'%D0%9C'"/></xsl:when>
		<xsl:when test="$ch='Н'"><xsl:value-of select="'%D0%9D'"/></xsl:when>
		<xsl:when test="$ch='О'"><xsl:value-of select="'%D0%9E'"/></xsl:when>
		<xsl:when test="$ch='П'"><xsl:value-of select="'%D0%9F'"/></xsl:when>
		<xsl:when test="$ch='Р'"><xsl:value-of select="'%D0%A0'"/></xsl:when>
		<xsl:when test="$ch='С'"><xsl:value-of select="'%D0%A1'"/></xsl:when>
		<xsl:when test="$ch='Т'"><xsl:value-of select="'%D0%A2'"/></xsl:when>
		<xsl:when test="$ch='У'"><xsl:value-of select="'%D0%A3'"/></xsl:when>
		<xsl:when test="$ch='Ф'"><xsl:value-of select="'%D0%A4'"/></xsl:when>
		<xsl:when test="$ch='Х'"><xsl:value-of select="'%D0%A5'"/></xsl:when>
		<xsl:when test="$ch='Ц'"><xsl:value-of select="'%D0%A6'"/></xsl:when>
		<xsl:when test="$ch='Ч'"><xsl:value-of select="'%D0%A7'"/></xsl:when>
		<xsl:when test="$ch='Ш'"><xsl:value-of select="'%D0%A8'"/></xsl:when>
		<xsl:when test="$ch='Щ'"><xsl:value-of select="'%D0%A9'"/></xsl:when>
		<xsl:when test="$ch='Ъ'"><xsl:value-of select="'%D0%AA'"/></xsl:when>
		<xsl:when test="$ch='Ы'"><xsl:value-of select="'%D0%AB'"/></xsl:when>
		<xsl:when test="$ch='Ь'"><xsl:value-of select="'%D0%AC'"/></xsl:when>
		<xsl:when test="$ch='Э'"><xsl:value-of select="'%D0%AD'"/></xsl:when>
		<xsl:when test="$ch='Ю'"><xsl:value-of select="'%D0%AE'"/></xsl:when>
		<xsl:when test="$ch='Я'"><xsl:value-of select="'%D0%AF'"/></xsl:when>
		<xsl:when test="$ch='а'"><xsl:value-of select="'%D0%B0'"/></xsl:when>
		<xsl:when test="$ch='б'"><xsl:value-of select="'%D0%B1'"/></xsl:when>
		<xsl:when test="$ch='в'"><xsl:value-of select="'%D0%B2'"/></xsl:when>
		<xsl:when test="$ch='г'"><xsl:value-of select="'%D0%B3'"/></xsl:when>
		<xsl:when test="$ch='д'"><xsl:value-of select="'%D0%B4'"/></xsl:when>
		<xsl:when test="$ch='е'"><xsl:value-of select="'%D0%B5'"/></xsl:when>
		<xsl:when test="$ch='ё'"><xsl:value-of select="'%D1%91'"/></xsl:when>
		<xsl:when test="$ch='ж'"><xsl:value-of select="'%D0%B6'"/></xsl:when>
		<xsl:when test="$ch='з'"><xsl:value-of select="'%D0%B7'"/></xsl:when>
		<xsl:when test="$ch='и'"><xsl:value-of select="'%D0%B8'"/></xsl:when>
		<xsl:when test="$ch='й'"><xsl:value-of select="'%D0%B9'"/></xsl:when>
		<xsl:when test="$ch='к'"><xsl:value-of select="'%D0%BA'"/></xsl:when>
		<xsl:when test="$ch='л'"><xsl:value-of select="'%D0%BB'"/></xsl:when>
		<xsl:when test="$ch='м'"><xsl:value-of select="'%D0%BC'"/></xsl:when>
		<xsl:when test="$ch='н'"><xsl:value-of select="'%D0%BD'"/></xsl:when>
		<xsl:when test="$ch='о'"><xsl:value-of select="'%D0%BE'"/></xsl:when>
		<xsl:when test="$ch='п'"><xsl:value-of select="'%D0%BF'"/></xsl:when>
		<xsl:when test="$ch='р'"><xsl:value-of select="'%D1%80'"/></xsl:when>
		<xsl:when test="$ch='с'"><xsl:value-of select="'%D1%81'"/></xsl:when>
		<xsl:when test="$ch='т'"><xsl:value-of select="'%D1%82'"/></xsl:when>
		<xsl:when test="$ch='у'"><xsl:value-of select="'%D1%83'"/></xsl:when>
		<xsl:when test="$ch='ф'"><xsl:value-of select="'%D1%84'"/></xsl:when>
		<xsl:when test="$ch='х'"><xsl:value-of select="'%D1%85'"/></xsl:when>
		<xsl:when test="$ch='ц'"><xsl:value-of select="'%D1%86'"/></xsl:when>
		<xsl:when test="$ch='ч'"><xsl:value-of select="'%D1%87'"/></xsl:when>
		<xsl:when test="$ch='ш'"><xsl:value-of select="'%D1%88'"/></xsl:when>
		<xsl:when test="$ch='щ'"><xsl:value-of select="'%D1%89'"/></xsl:when>
		<xsl:when test="$ch='ъ'"><xsl:value-of select="'%D1%8A'"/></xsl:when>
		<xsl:when test="$ch='ы'"><xsl:value-of select="'%D1%8B'"/></xsl:when>
		<xsl:when test="$ch='ь'"><xsl:value-of select="'%D1%8C'"/></xsl:when>
		<xsl:when test="$ch='э'"><xsl:value-of select="'%D1%8D'"/></xsl:when>
		<xsl:when test="$ch='ю'"><xsl:value-of select="'%D1%8E'"/></xsl:when>
		<xsl:when test="$ch='я'"><xsl:value-of select="'%D1%8F'"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="$ch" /></xsl:otherwise>
	</xsl:choose>
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:template name="formHeader" >
	<xsl:param name="sequenceId" />
	<xsl:param name="isFirst" select="true()"></xsl:param>
	<xsl:param name="gost">ERROR</xsl:param>
	<xsl:param name="formFirst">ERROR</xsl:param>
	<xsl:param name="formNonFirst">ERROR</xsl:param>
	<xsl:param name="f1_or_f3" >f1</xsl:param>
	<xsl:param name="no-markers" />
	<xsl:param name="organization" />
	<xsl:param name="part-code" />
	<xsl:param name="part-name" />
	<xsl:param name="image_title" />
	<xsl:param name="workShop" />
	<xsl:param name="workArea" />
	<xsl:param name="workPlace" />
	<xsl:param name="operation-number" />
	<xsl:param name="page_number" />
	<xsl:param name="pages_total" />
	<xsl:param name="route" select="/*[false()]" />

	<xsl:if test="$isFirst" >
		<xsl:call-template name="formHeaderFirst" >
			<xsl:with-param name="sequenceId" select="$sequenceId" />
			<xsl:with-param name="gost" select="$gost" />
			<xsl:with-param name="form" select="$formFirst" />
			<xsl:with-param name="f1_or_f3" select="$f1_or_f3" />
			<xsl:with-param name="no-markers" select="$no-markers" />
			<xsl:with-param name="organization" select="$organization" />
			<xsl:with-param name="part-code" select="$part-code" />
			<xsl:with-param name="part-name" select="$part-name" />
			<xsl:with-param name="image_title" select="$image_title"/>
			<xsl:with-param name="workShop" select="$workShop" />
			<xsl:with-param name="workArea" select="$workArea" />
			<xsl:with-param name="workPlace" select="$workPlace" />
			<xsl:with-param name="operation-number" select="$operation-number" />
			<xsl:with-param name="page_number" select="$page_number"/>
			<xsl:with-param name="pages_total" select="$pages_total"/>
			<xsl:with-param name="route" select="$route"/>
		</xsl:call-template>
	</xsl:if>

	<xsl:if test="not($isFirst)" >
		<xsl:call-template name="formHeaderNonFirst" >
			<xsl:with-param name="sequenceId" select="$sequenceId" />
			<xsl:with-param name="gost" select="$gost" />
			<xsl:with-param name="form" select="$formNonFirst" />
			<xsl:with-param name="f1_or_f3" select="$f1_or_f3" />
			<xsl:with-param name="no-markers" select="$no-markers" />
			<xsl:with-param name="organization" select="$organization" />
			<xsl:with-param name="part-code" select="$part-code" />
			<xsl:with-param name="part-name" select="$part-name" />
			<xsl:with-param name="image_title" select="$image_title"/>
			<xsl:with-param name="workShop" select="$workShop" />
			<xsl:with-param name="workArea" select="$workArea" />
			<xsl:with-param name="workPlace" select="$workPlace" />
			<xsl:with-param name="operation-number" select="$operation-number" />
			<xsl:with-param name="page_number" select="$page_number"/>
			<xsl:with-param name="pages_total" select="$pages_total"/>
		</xsl:call-template>
	</xsl:if>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="formHeaderFirst" >
	<xsl:param name="sequenceId" />
	<xsl:param name="gost">ERROR</xsl:param>
	<xsl:param name="form">ERROR</xsl:param>
	<xsl:param name="f1_or_f3" >f1</xsl:param>
	<xsl:param name="no-markers" />
	<xsl:param name="organization" />
	<xsl:param name="part-code" />
	<xsl:param name="part-name" />
	<xsl:param name="image_title" />
	<xsl:param name="workShop" />
	<xsl:param name="workArea" />
	<xsl:param name="workPlace" />
	<xsl:param name="operation-number" />
	<xsl:param name="page_number" />
	<xsl:param name="pages_total" />
	<xsl:param name="route" />

	<fo:wrapper font-style="normal">
	<!-- =====  GOST and Marker  ===== -->
	
	<xsl:call-template name="GOST" >
		<xsl:with-param name="gost" select="$gost" />
		<xsl:with-param name="form" select="$form" />
	</xsl:call-template>
	
	<xsl:call-template name="centerMarker" />

	<!-- =====  Header ===== -->
	
	<xsl:call-template name="partB4B3" />

	<xsl:call-template name="partB5f1" >
		<xsl:with-param name="sequenceId" select="$sequenceId" />
		<xsl:with-param name="no-markers" select="$no-markers" />
		<xsl:with-param name="image_title" select="$image_title"/>
		<xsl:with-param name="page_number" select="$page_number"/>
		<xsl:with-param name="pages_total" select="$pages_total"/>
	</xsl:call-template>

	<xsl:call-template name="partB2f1B1f1_3" >
		<xsl:with-param name="f1_or_f3" select="$f1_or_f3" />
		<xsl:with-param name="no-markers" select="$no-markers" />
		<xsl:with-param name="organization" select="$organization" />
		<xsl:with-param name="part-code" select="$part-code" />
		<xsl:with-param name="part-name" select="$part-name" />
		<xsl:with-param name="workShop" select="$workShop" />
		<xsl:with-param name="workArea" select="$workArea" />
		<xsl:with-param name="workPlace" select="$workPlace" />
		<xsl:with-param name="operation-number" select="$operation-number" />
		<xsl:with-param name="route" select="$route"/>
	</xsl:call-template>
	<!-- =====  end  ===== -->
	</fo:wrapper>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="formHeaderNonFirst" >
	<xsl:param name="sequenceId" />
	<xsl:param name="gost">ERROR</xsl:param>
	<xsl:param name="form">ERROR</xsl:param>
	<xsl:param name="f1_or_f3" >f1</xsl:param>
	<xsl:param name="no-markers" />
	<xsl:param name="part-code" />
	<xsl:param name="image_title" />
	<xsl:param name="operation-number" />
	<xsl:param name="page_number" />
	<xsl:param name="pages_total" />

	<fo:wrapper font-style="normal">
	<!-- =====  GOST and Marker  ===== -->
	
	<xsl:call-template name="GOST" >
		<xsl:with-param name="gost" select="$gost" />
		<xsl:with-param name="form" select="$form" />
	</xsl:call-template>
	
	<xsl:call-template name="centerMarker" />

	<!-- =====  Header ===== -->
	
	<xsl:call-template name="partB4B3" />

	<xsl:call-template name="partB5f1a" >
		<xsl:with-param name="no-markers" select="$no-markers" />
		<xsl:with-param name="image_title" select="$image_title"/>
		<xsl:with-param name="page_number" select="$page_number"/>
		<xsl:with-param name="pages_total" select="$pages_total"/>
	</xsl:call-template>

	<xsl:call-template name="partB3f1bB3f1bB1f1_3a" >
		<xsl:with-param name="f1_or_f3" select="$f1_or_f3" />
		<xsl:with-param name="no-markers" select="$no-markers" />
		<xsl:with-param name="part-code" select="$part-code" />
		<xsl:with-param name="operation-number" select="$operation-number" />
	</xsl:call-template>
	<!-- =====  end  ===== -->
	</fo:wrapper>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="formFooter" >
	<xsl:param name="B3f1rows" />
	<xsl:param name="no-markers" />
	<xsl:param name="document-type-code" />
	<xsl:param name="document-type-title" />
	<xsl:param name="document-file-name" />
	<xsl:param name="document-file-version" />

	<xsl:if test="$B3f1rows &gt; 0" >
	
		<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
			<!-- columns -->
			<fo:table-column column-width="44.2mm"/>
			<!-- -->
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="23.4mm"/>
			<fo:table-column column-width="20.8mm"/>
			<fo:table-column column-width="15.6mm"/>
			<!-- -->
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="23.4mm"/>
			<fo:table-column column-width="20.8mm"/>
			<fo:table-column column-width="15.6mm"/>
			<!-- -->
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="23.4mm"/>
			<fo:table-column column-width="20.8mm"/>
			<fo:table-column column-width="15.6mm"/>
			<!-- body -->
			<fo:table-body>
				<fo:table-row height="{$h_single}">
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<!-- -->
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<!-- -->
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<!-- -->
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block/></fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		
	</xsl:if>

	<xsl:call-template name="partB6f1" >
		<xsl:with-param name="no-markers" select="$no-markers" />
		<xsl:with-param name="document-type-code" select="$document-type-code" />
		<xsl:with-param name="document-type-title" select="$document-type-title" />
		<xsl:with-param name="document-file-name" select="$document-file-name" />
		<xsl:with-param name="document-file-version" select="$document-file-version" />
	</xsl:call-template>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="GOST" >
	<xsl:param name="gost" />
	<xsl:param name="form" />

	<fo:block-container>
	<fo:block-container position="absolute" top="-4mm" left="210mm" >
		<fo:block font-size="3.5mm" line-height="3.5mm">ГОСТ  <xsl:value-of select="$gost"/>
			<fo:inline padding-left="1cm"><xsl:value-of select="$form"/></fo:inline></fo:block>
	</fo:block-container>
	</fo:block-container>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="centerMarker" >

	<xsl:variable name="tick_size" select="'8mm'" />

	<fo:block-container>
	<fo:block-container position="absolute" top="-1.5mm" left="143mm"
		border-left-style="solid" border-left-color="{$border_color}" border-left-width="{$border_thickness}">
		<fo:block color="white" font-size="0.0pt" line-height="{$tick_size}">.</fo:block>
	</fo:block-container>
	</fo:block-container>
		
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="tick" >
	<xsl:param name="pos">0mm</xsl:param>

	<xsl:variable name="tick_size" select="'1mm'" />
	
	<!-- xsl:variable name="color"><xsl:if test="$pos!='0mm'">red</xsl:if><xsl:if test="$pos='0mm'">green</xsl:if></xsl:variable -->
	<!-- xsl:variable name="color" select="'black'" / -->

	<fo:block-container>
	<fo:block-container position="absolute" top="0mm" left="{$pos}"
		border-left-style="solid" border-left-color="{$border_color}" border-left-width="{$border_thickness}">
		<fo:block color="white" font-size="0.0pt" line-height="{$tick_size}">.</fo:block>
	</fo:block-container>
	</fo:block-container>
		
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="tickS" >

	<xsl:variable name="tick_size" select="'1mm'" />

	<fo:block-container>
	<fo:block-container position="absolute" top="0mm" left="0.2mm"
		border-left-style="solid" border-left-color="{$border_color}" border-left-width="{$border_thickness}">
		<fo:block color="white" font-size="0.0pt" line-height="{$tick_size}">.</fo:block>
	</fo:block-container>
	</fo:block-container>
		
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partB4B3" >
	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="20.8mm"/>
		<fo:table-column column-width="20.8mm"/>
		<fo:table-column column-width="15.6mm"/>
		<fo:table-column column-width="49.4mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="23.4mm"/>
		<fo:table-column column-width="20.8mm"/>
		<fo:table-column column-width="15.6mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="23.4mm"/>
		<fo:table-column column-width="20.8mm"/>
		<fo:table-column column-width="15.6mm"/>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_single}">
				<fo:table-cell number-columns-spanned="10"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Дубл.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell number-columns-spanned="6"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Взам.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell" number-columns-spanned="3"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellNoB"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Подл.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellNoB"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Изм.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Шифр</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Документ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Подпись</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Дата</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Изм.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Шифр</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Документ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Подпись</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block>Дата</fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partB5f1" >
	<xsl:param name="sequenceId" />
	<xsl:param name="no-markers" />
	<xsl:param name="image_title" />
	<xsl:param name="pages_total" />
	<xsl:param name="page_number" />

	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<fo:table-column column-width="148.2mm"/>
		<fo:table-column column-width="59.8mm"/>
		<fo:table-column column-width="46.8mm"/>
		<fo:table-column column-width="15.6mm"/>
		<fo:table-column column-width="15.6mm"/>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_double}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block line-height="6mm" font-size="7mm" padding-top="1.5mm" text-align="center" font-weight="bold" font-style="{$fontStyle}">
					<xsl:call-template name="use_marker">
						<xsl:with-param name="no-markers" select="$no-markers" />
						<xsl:with-param name="name" select="'image_title'"/>
						<xsl:with-param name="value" select="$image_title"/>
					</xsl:call-template>
				</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block line-height="6mm" font-size="7mm" padding-top="1.5mm" text-align="center" font-style="{$fontStyle}" >
					<!-- fo:page-number-citation-last ref-id="{$sequenceId}"/ -->
					<xsl:value-of select="$pages_total"/>
					</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block line-height="6mm" font-size="7mm" padding-top="1.5mm" text-align="center" font-style="{$fontStyle}" >
					<!-- fo:page-number/ -->
					<xsl:value-of select="$page_number"/>
					</fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partB5f1a" >
	<xsl:param name="no-markers" />
	<xsl:param name="image_title" />
	<xsl:param name="page_number" />

	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<fo:table-column column-width="163.8mm"/>
		<fo:table-column column-width="59.8mm"/>
		<fo:table-column column-width="46.8mm"/>
		<fo:table-column column-width="15.6mm"/>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_double}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block line-height="6mm" font-size="7mm" padding-top="1.5mm" text-align="center" font-weight="bold" font-style="{$fontStyle}">
					<xsl:call-template name="use_marker">
						<xsl:with-param name="no-markers" select="$no-markers" />
						<xsl:with-param name="name" select="'image_title'"/>
						<xsl:with-param name="value" select="$image_title"/>
					</xsl:call-template>
				</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block line-height="6mm" font-size="7mm" padding-top="1.5mm" text-align="center" font-style="{$fontStyle}" >
					<!-- fo:page-number/ -->
					<xsl:value-of select="$page_number"/>
					</fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partB2f1B1f1_3" >
	<xsl:param name="f1_or_f3" /> <!-- literally 'f1' or 'f3' -->
	<xsl:param name="no-markers" />
	<xsl:param name="organization" />
	<xsl:param name="part-code" />
	<xsl:param name="part-name" />
	<xsl:param name="workShop" />
	<xsl:param name="workArea" />
	<xsl:param name="workPlace" />
	<xsl:param name="operation-number" />
	<xsl:param name="route" />
	
	<fo:table table-layout="fixed" font-size="11pt" width="286mm">
		<!-- columns -->
		<fo:table-column column-width="96.2mm"/>
		<!-- -->
		<fo:table-column column-width="41.6mm"/>
		<fo:table-column column-width="59.8mm"/>
		<fo:table-column column-width="41.6mm"/>
		<fo:table-column column-width="46.8mm"/>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_triple}">
				<fo:table-cell xsl:use-attribute-sets="cellL">
				
					<!-- fo:block/ -->
					
					<fo:table table-layout="fixed" width="95.7mm" font-size="11pt">
						<!-- columns -->
						<fo:table-column column-width="23.15mm"/><!-- 23.4mm -->
						<fo:table-column column-width="36.4mm"/>
						<fo:table-column column-width="20.8mm"/>
						<fo:table-column column-width="15.1mm"/><!-- 15.6mm -->
						<!-- body -->
						<fo:table-body>
							<fo:table-row height="{$h_single}">
								<fo:table-cell xsl:use-attribute-sets="cellBR">
									<xsl:call-template name="getRouteLabel"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="1"/></xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR">
									<xsl:call-template name="getRoutePerson"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="1"/></xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR" margin-bottom="0" padding-top="0mm" padding-bottom="-2mm">
									<xsl:call-template name="getRouteSignature"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="1"/></xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellB">
									<xsl:call-template name="getRouteDate"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="1"/></xsl:call-template>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row height="{$h_single}">
								<fo:table-cell xsl:use-attribute-sets="cellBR">
									<xsl:call-template name="getRouteLabel"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="2"/></xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR">
									<xsl:call-template name="getRoutePerson"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="2"/></xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR" margin-bottom="0" padding-top="0mm" padding-bottom="-2mm">
									<xsl:call-template name="getRouteSignature"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="2"/></xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellB">
									<xsl:call-template name="getRouteDate"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="2"/></xsl:call-template>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row height="{$h_single}">
								<fo:table-cell xsl:use-attribute-sets="cellBR">
									<xsl:call-template name="getRouteLabel"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="3"/></xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR">
									<xsl:call-template name="getRoutePerson"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="3"/></xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR" margin-bottom="0" padding-top="0mm" padding-bottom="-2mm">
									<xsl:call-template name="getRouteSignature"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="3"/></xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellB">
									<xsl:call-template name="getRouteDate"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="3"/></xsl:call-template>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					
				</fo:table-cell>
				<!-- -->
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block line-height="5mm" font-size="5mm" padding-top="4mm" text-align="center" font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'organization'"/>
							<xsl:with-param name="value" select="$organization"/>
						</xsl:call-template>
					</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block font-size="5mm" padding-top="4mm" text-align="center" font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'part-code'"/>
							<xsl:with-param name="value" select="$part-code"/>
						</xsl:call-template><!-- (2) -->
					</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>

	<fo:table table-layout="fixed" font-size="11pt" width="286mm"><!-- background-color="red" -->
		<!-- columns -->
		<fo:table-column column-width="23.4mm"/>
		<fo:table-column column-width="36.4mm"/>
		<fo:table-column column-width="20.8mm"/>
		<fo:table-column column-width="15.6mm"/>
		<!-- -->
		<xsl:if test="$f1_or_f3 = 'f1'" >
			<fo:table-column column-width="13.0mm"/>
			<fo:table-column column-width="145.6mm"/>
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="10.4mm"/>
		</xsl:if>
		<xsl:if test="$f1_or_f3 = 'f3'" >
			<fo:table-column column-width="13.0mm"/>
			<fo:table-column column-width="132.6mm"/>
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="10.4mm"/>
			<fo:table-column column-width="13.0mm"/>
		</xsl:if>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_single}">
					<fo:table-cell xsl:use-attribute-sets="cellLBR">
						<xsl:call-template name="getRouteLabel"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="4"/></xsl:call-template>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cellBR">
						<xsl:call-template name="getRoutePerson"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="4"/></xsl:call-template>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cellBR" margin-bottom="0" padding-top="0mm" padding-bottom="-2mm">
						<xsl:call-template name="getRouteSignature"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="4"/></xsl:call-template>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cellB">
						<xsl:call-template name="getRouteDate"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="4"/></xsl:call-template>
					</fo:table-cell>
				<!-- -->
				<fo:table-cell xsl:use-attribute-sets="cell" border-right-style="none" number-rows-spanned="2"><fo:block ></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell" border-left-style="none" number-rows-spanned="2"><xsl:call-template name="tick"/>
					<fo:block line-height="6mm" font-size="6mm" padding-top="1.5mm" text-align="center"  font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'part-name'"/>
							<xsl:with-param name="value" select="$part-name"/>
						</xsl:call-template>
					</fo:block></fo:table-cell>
				<xsl:if test="$f1_or_f3 = 'f1'" >
					<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="2"><fo:block></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="2"><fo:block></fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="2"><fo:block></fo:block></fo:table-cell>
				</xsl:if>
				<xsl:if test="$f1_or_f3 = 'f3'" >
					<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="2" display-align="center"><fo:block font-size="4mm" text-align="center"  font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'workShop'"/>
							<xsl:with-param name="value" select="$workShop"/>
						</xsl:call-template>
					</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="2" display-align="center"><fo:block font-size="4mm" text-align="center"  font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'workArea'"/>
							<xsl:with-param name="value" select="$workArea"/>
						</xsl:call-template>
					</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="2" display-align="center"><fo:block font-size="4mm" text-align="center"  font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'workPlace'"/>
							<xsl:with-param name="value" select="$workPlace"/>
						</xsl:call-template>
					</fo:block></fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="2" display-align="center"><fo:block font-size="4mm" text-align="center"  font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'operation-number'"/>
							<xsl:with-param name="value" select="$operation-number"/>
						</xsl:call-template>
					</fo:block></fo:table-cell>
				</xsl:if>
			</fo:table-row>
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cellLBR">
					<xsl:call-template name="getRouteLabel"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="5"/></xsl:call-template>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR">
					<xsl:call-template name="getRoutePerson"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="5"/></xsl:call-template>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellBR" margin-bottom="0" padding-top="0mm" padding-bottom="-2mm">
					<xsl:call-template name="getRouteSignature"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="5"/></xsl:call-template>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellB">
					<xsl:call-template name="getRouteDate"><xsl:with-param name="route" select="$route"/><xsl:with-param name="index" select="5"/></xsl:call-template>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getRouteLabel" >
	<xsl:param name="route" />
	<xsl:param name="index" />
		
		<xsl:variable name="role" select="$route/*[$index]/@role" />
		
		<fo:block margin-left="2mm">
		<xsl:choose>
			<xsl:when test="$role='drw_developer'">Разраб.</xsl:when>
			<xsl:when test="$role='drw_checker'">Пров.</xsl:when>
			<xsl:when test="$role='drw_tcontr'">Т.контр.</xsl:when>
			<xsl:when test="$role='drw_lead'">Вед.констр.</xsl:when>
			<xsl:when test="$role='drw_ncontr'">Н.контр.</xsl:when>
			<xsl:when test="$role='drw_peo'">ПЭО</xsl:when>
			<xsl:when test="$role='drw_approver'">Утв.</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		</fo:block>
		
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getRoutePerson" >
	<xsl:param name="route" />
	<xsl:param name="index" />
		<fo:block><xsl:value-of select="$route/*[$index]/@lastName"/> <!-- xsl:value-of select="$route/*[$index]/@firstName"/ --></fo:block>
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getRouteSignature" >
	<xsl:param name="route" />
	<xsl:param name="index" />
		<xsl:variable name="image" select="$route/*[$index]/@signature" />
		<!-- xsl:message>getRouteSignature:<xsl:value-of select="$image"/>=</xsl:message -->
		<fo:block text-align="center"><xsl:if test="string-length($image)!=0"><fo:external-graphic scaling="uniform" 
			border-color="red" border-style="none" 
			content-width="scale-to-fit" content-height="scale-to-fit"  
			width="20mm" height="{$h_single}" 
			src="{$image}"
		/></xsl:if></fo:block>
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="getRouteDate" >
	<xsl:param name="route" />
	<xsl:param name="index" />
		<fo:block><xsl:value-of select="$route/*[$index]/@date"/></fo:block>
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partB3f1bB3f1bB1f1_3a" >
	<xsl:param name="f1_or_f3" /> <!-- literally 'f1' or 'f3' -->
	<xsl:param name="no-markers" />
	<xsl:param name="part-code" />
	<xsl:param name="operation-number" />

	<!-- ======================================= -->

	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<xsl:if test="$f1_or_f3 = 'f1'" >
			<fo:table-column column-width="18.2mm"/>
		</xsl:if>
		<xsl:if test="$f1_or_f3 = 'f3'" >
			<fo:table-column column-width="5.2mm"/>
		</xsl:if>
		<!-- -->
		<fo:table-column column-width="161.2mm"/>
		<!-- -->
		<fo:table-column column-width="59.8mm"/>
		<fo:table-column column-width="46.8mm"/>
		<xsl:if test="$f1_or_f3 = 'f3'" >
			<fo:table-column column-width="13mm"/>
		</xsl:if>
		
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellL">
				
					<fo:table table-layout="fixed" font-size="11pt" width="160.7mm" ><!-- 161.2mm -->
						<!-- columns -->
						<fo:table-column column-width="10.4mm"/>
						<fo:table-column column-width="10.4mm"/>
						<fo:table-column column-width="23.4mm"/>
						<fo:table-column column-width="20.8mm"/>
						<fo:table-column column-width="15.6mm"/>
						<!-- -->
						<fo:table-column column-width="10.3mm"/>
						<fo:table-column column-width="10.3mm"/>
						<fo:table-column column-width="23.3mm"/>
						<fo:table-column column-width="20.7mm"/>
						<fo:table-column column-width="15.5mm"/>
						<!-- -->
						
						<!-- body -->
						<fo:table-body>
							<fo:table-row height="{$h_single}">
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellB"><fo:block></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row height="{$h_single}">
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellBR"><fo:block></fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellB"><fo:block></fo:block></fo:table-cell>
							</fo:table-row>
							<fo:table-row height="{$h_single}">
								<fo:table-cell xsl:use-attribute-sets="cellR"><fo:block>Изм.</fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellR"><fo:block>Шифр</fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellR"><fo:block>Документ</fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellR"><fo:block>Подпись</fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellR"><fo:block>Дата</fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellR"><fo:block>Изм.</fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellR"><fo:block>Шифр</fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellR"><fo:block>Документ</fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellR"><fo:block>Подпись</fo:block></fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets="cellNoB"><fo:block>Дата</fo:block></fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
	
				</fo:table-cell>
				
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block font-size="5mm" padding-top="4mm" text-align="center" font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'part-code'"/>
							<xsl:with-param name="value" select="$part-code"/>
						</xsl:call-template><!-- (2) -->
					</fo:block>
				</fo:table-cell>
				
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block></fo:block></fo:table-cell><!-- (4) -->
				
				<xsl:if test="$f1_or_f3 = 'f3'" >
					<fo:table-cell xsl:use-attribute-sets="cell"><fo:block font-size="5mm" padding-top="4mm" text-align="center"  font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'operation-number'"/>
							<xsl:with-param name="value" select="$operation-number"/>
						</xsl:call-template>
					</fo:block></fo:table-cell>
				</xsl:if>
				
			</fo:table-row>
		</fo:table-body>
	</fo:table>

	<!-- ======================================= -->

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partM1M2" >
	<xsl:param name="no-markers" />
	<xsl:param name="m01-material" />
	<xsl:param name="m02-kodmat" />
	<xsl:param name="m02-ev" />
	<xsl:param name="m02-md" />
	<xsl:param name="m02-en" />
	<xsl:param name="m02-nrash" />
	<xsl:param name="m02-kim" />
	<xsl:param name="m02-kodzag" />
	<xsl:param name="m02-zagsize" />
	<xsl:param name="m02-kd" />
	<xsl:param name="m02-mz" />

	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="33.8mm"/>
		<fo:table-column column-width="10.4mm"/>
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="15.6mm"/>
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="13.0mm"/>
		<fo:table-column column-width="33.8mm"/>
		<fo:table-column column-width="54.6mm"/>
		<fo:table-column column-width="15.6mm"/>
		<fo:table-column column-width="18.2mm"/>
		<fo:table-column column-width="41.6mm"/>
		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_double}">
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block margin-left="3mm" padding-top="1.8mm">М01</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell" number-columns-spanned="10">
					<fo:block margin-left="2mm" padding-top="1.5mm" font-size="5mm" font-weight="bold" font-style="{$fontStyle}">
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m01-material'"/>
							<xsl:with-param name="value" select="$m01-material"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="3"><fo:block></fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row height="{$h_single}">
				
				<fo:table-cell xsl:use-attribute-sets="cell" number-rows-spanned="2">
					<fo:block margin-left="3mm" padding-top="1.8mm">М02</fo:block>
				</fo:table-cell>
				
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Код</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">ЕВ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">МД</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Ен</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Н.расх.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">КИМ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Код загот.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Профиль и размеры</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">КД</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">МЗ</fo:block></fo:table-cell>
			</fo:table-row>
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cellTB">
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-kodmat'"/>
							<xsl:with-param name="value" select="$m02-kodmat"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellTB">
					<xsl:call-template name="tickS"/>
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-ev'"/>
							<xsl:with-param name="value" select="$m02-ev"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellTB">
					<xsl:call-template name="tickS"/>
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-md'"/>
							<xsl:with-param name="value" select="$m02-md"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellTB">
					<xsl:call-template name="tickS"/>
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-en'"/>
							<xsl:with-param name="value" select="$m02-en"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellTB">
					<xsl:call-template name="tickS"/>
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-nrash'"/>
							<xsl:with-param name="value" select="$m02-nrash"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-kim'"/>
							<xsl:with-param name="value" select="$m02-kim"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-kodzag'"/>
							<xsl:with-param name="value" select="$m02-kodzag"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-zagsize'"/>
							<xsl:with-param name="value" select="$m02-zagsize"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellTB">
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-kd'"/>
							<xsl:with-param name="value" select="$m02-kd"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cellTBR">
					<xsl:call-template name="tickS"/>
					<fo:block margin-left="2mm" font-weight="bold" font-style="{$fontStyle}" >
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'m02-mz'"/>
							<xsl:with-param name="value" select="$m02-mz"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partA" >

			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="5mm">А</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Цех</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Уч.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">РМ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Опер.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Код, наименование операции</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell" number-columns-spanned="11" padding-left="25mm"><fo:block>Обозначение документа</fo:block></fo:table-cell>
			</fo:table-row>
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partB" >
			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="5mm">Б</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Код, наименование оборудования</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">СМ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Проф.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Р</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">УТ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">КР</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">КОИД</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">ЕН</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">ОП</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Кшт.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Т п.з.</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Т шт.</fo:block></fo:table-cell>
			</fo:table-row>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partKM" >

			<fo:table-row height="{$h_single}">
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="5mm">К/М</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Наименование детали, сб.единицы или материала</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell" number-columns-spanned="6"><fo:block margin-left="2mm">Обозначение, код</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">ОПП</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">ЕВ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">ЕН</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">КИ</fo:block></fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="2mm">Н.расх.</fo:block></fo:table-cell>
			</fo:table-row>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="partB6f1" >
	<xsl:param name="no-markers" />
	<xsl:param name="document-type-code" />
	<xsl:param name="document-type-title" />
	<xsl:param name="document-file-name" />
	<xsl:param name="document-file-version" />
	
	<fo:table table-layout="fixed" font-size="11pt" width="286mm" >
		<!-- columns -->
		<!-- fo:table-column column-width="23.4mm"/>
		<fo:table-column column-width="231mm"/>
		<fo:table-column column-width="16mm"/>
		<fo:table-column column-width="15.6mm"/ -->
		<fo:table-column column-width="23.4mm"/>
		<fo:table-column column-width="231mm"/>
		<fo:table-column column-width="16mm"/>
		<fo:table-column column-width="15.6mm"/>

		<!-- body -->
		<fo:table-body>
			<fo:table-row height="{$h_double}">
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block font-size="5mm" padding-top="2.7mm" text-align="center" font-weight="normal"><!--  font-style="{$fontStyle}" -->
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'document-type-code'"/>
							<xsl:with-param name="value" select="$document-type-code"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block font-size="5mm" padding-top="2.7mm" margin-left="3mm"  font-weight="normal"><!--  font-style="{$fontStyle}" -->
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'document-type-title'"/>
							<xsl:with-param name="value" select="$document-type-title"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block font-size="3.5mm" padding-top="2.7mm" margin-left="3mm"  font-weight="normal"><!--  font-style="{$fontStyle}" -->
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'document-file-name'"/>
							<xsl:with-param name="value" select="$document-file-name"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell xsl:use-attribute-sets="cell">
					<fo:block font-size="5mm" padding-top="2.7mm" margin-left="3mm"  font-weight="normal"><!--  font-style="{$fontStyle}" -->
						<xsl:call-template name="use_marker">
							<xsl:with-param name="no-markers" select="$no-markers" />
							<xsl:with-param name="name" select="'document-file-version'"/>
							<xsl:with-param name="value" select="$document-file-version"/>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="normalRow" >
	<!-- xsl:param name="label" / -->
	<xsl:param name="rowNum" />
	
	<xsl:variable name="label"><xsl:if test="$rowNum &lt; 10">0</xsl:if><xsl:value-of select="$rowNum" /></xsl:variable>
	
	<fo:table-row height="{$h_double}">
		<!-- fo:table-cell xsl:use-attribute-sets="cell"><fo:block margin-left="5mm" padding-top="1.8mm"><xsl:value-of select="$label"/></fo:block></fo:table-cell -->
		<fo:table-cell xsl:use-attribute-sets="label_cell"><!-- xsl:call-template name="tick"/ --><fo:block margin-left="5mm" padding-top="2.6mm"><xsl:value-of select="$label"/></fo:block></fo:table-cell>
		<fo:table-cell xsl:use-attribute-sets="cellTBR">
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'10.4mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'20.8mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'31.2mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'44.2mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'119.6mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'130mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'148.2mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'158.6mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'171.6mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'182mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'195mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'208mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'221mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'234mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'252.2mm'" /></xsl:call-template>
			<xsl:call-template name="tick" ><xsl:with-param name="pos" select="'273mm'" /></xsl:call-template>
			<!-- fo:block></fo:block -->
		</fo:table-cell>
	</fo:table-row>
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->
	
<!-- xsl:variable name="fontGostItalic" select="document('font_gost_a_italic.xml')/*" / -->

<!-- ============================================================================================================ -->

<xsl:template name="wrapText" >
	<xsl:param name="font" />
	<xsl:param name="text" />
	
	<xsl:message>== wrapText ===== </xsl:message>
	<xsl:message>text: <xsl:value-of select="$text" /></xsl:message>
	<!-- xsl:message>toSpace: <xsl:value-of select="substring-before($text,' ')" /></xsl:message>
	<xsl:message>toSpace_len: <xsl:value-of select="string-length(substring-before($text,' ')+1)" /></xsl:message -->

	<xsl:variable name="wrap0">
		<xsl:call-template name="wrapTextIterate">
			<xsl:with-param name="font" select="$font" />
			<xsl:with-param name="text" select="$text" />
			<xsl:with-param name="maxWidth" select="50" />
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="wrap" select="exsl:node-set($wrap0)/*"/>

	<xsl:message>wrap: <xsl:value-of select="count($wrap)" /></xsl:message>
	<xsl:for-each select="$wrap">
		<xsl:message>wrap[i]: <xsl:value-of select="current()" /></xsl:message>
	</xsl:for-each>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="wrapTextIterate" >
	<xsl:param name="font" />
	<xsl:param name="text" />
	<xsl:param name="maxWidth" />
	<xsl:param name="separator" select="' '" />

	<!-- xsl:message>wrapTextIterate(<xsl:value-of select="$separator" />): <xsl:value-of select="$text" /> </xsl:message -->
	<xsl:variable name="cut_size">
		<xsl:call-template name="cutToWidth">
			<xsl:with-param name="font" select="$font" />
			<!-- xsl:with-param name="text" select="$text" / -->
			<xsl:with-param name="text" select="substring($text,1,200)" />
			<xsl:with-param name="maxWidth" select="$maxWidth" />
			<xsl:with-param name="separator" select="$separator" />
		</xsl:call-template>
	</xsl:variable>
	<!-- xsl:message>cut_size: <xsl:value-of select="$cut_size" />: <xsl:value-of select="substring($text,1,$cut_size)" /></xsl:message -->

	<xsl:if test="$cut_size = 0" >
		<xsl:element name="A"><xsl:copy-of select="$text"/></xsl:element>
	</xsl:if>

	<xsl:if test="$cut_size != 0" >
	
		<xsl:variable name="head" select="substring($text,1,$cut_size)" />
		<xsl:variable name="tail" select="substring($text,$cut_size+1)" />

		<xsl:element name="A"><xsl:copy-of select="$head"/></xsl:element>
		
		<xsl:if test="string-length($tail)" >
		
			<xsl:call-template name="wrapTextIterate">
				<xsl:with-param name="font" select="$font" />
				<xsl:with-param name="text" select="normalize-space($tail)" />
				<xsl:with-param name="maxWidth" select="$maxWidth" />
				<xsl:with-param name="separator" select="$separator" />
			</xsl:call-template>
			
		</xsl:if>
		
	</xsl:if>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="cutToWidth" >
	<xsl:param name="font" />
	<xsl:param name="text" />
	<xsl:param name="maxWidth" />
	<xsl:param name="separator" />
	<xsl:param name="headLen" select="0" />
	<xsl:param name="headW" select="0" />

	<!-- xsl:message>cutToWidth:<xsl:value-of select="$text" /> </xsl:message -->

	<xsl:variable name="head" select="substring($text,1,$headLen)" />
	<xsl:variable name="tail" select="substring($text,1+$headLen)" />

	<!-- xsl:message>head=<xsl:value-of select="$head" /></xsl:message>
	<xsl:message>headW=<xsl:value-of select="$headW" /></xsl:message>
	<xsl:message>tail=<xsl:value-of select="$tail" /></xsl:message -->


	<xsl:variable name="subtail0" select="substring-before($tail,$separator)" />
	<!-- xsl:message>subtail0: <xsl:value-of select="$subtail0" /></xsl:message -->

	<xsl:variable name="subtail">
		<xsl:if test="string-length($subtail0) != 0"> <xsl:value-of select="concat($subtail0,$separator)"/> </xsl:if>
		<xsl:if test="string-length($subtail0)  = 0"> <xsl:value-of select="$tail"/> </xsl:if>
	</xsl:variable>

	<xsl:variable name="subtailLen" select="string-length($subtail)" />
	<!-- xsl:message>subtailLen: <xsl:value-of select="$subtailLen" /></xsl:message -->

	<xsl:variable name="subtailW">
		<xsl:call-template name="textWidth">
			<xsl:with-param name="font" select="$font" />
			<xsl:with-param name="text" select="$subtail" />
		</xsl:call-template>
	</xsl:variable>

	<!-- xsl:message>subtailW: <xsl:value-of select="$subtailW" /></xsl:message -->
	
	<xsl:if test="$subtailLen = 0">
		<xsl:value-of select="string-length($text)" />
	</xsl:if>
	
	<xsl:if test="$subtailLen &gt; 0">
		<xsl:variable name="newHeadLen" select="$headLen + $subtailLen" />
		<xsl:variable name="newHeadW"   select="$headW   + $subtailW" />
		<!-- xsl:message>newHeadLen: <xsl:value-of select="$newHeadLen" /></xsl:message>
		<xsl:message>newHeadW: <xsl:value-of select="$newHeadW" /></xsl:message -->

		<xsl:if test="$newHeadW &gt; $maxWidth">
			<xsl:value-of select="$headLen" />
		</xsl:if>

		<xsl:if test="not($newHeadW &gt; $maxWidth)">
			<xsl:call-template name="cutToWidth">
				<xsl:with-param name="font" select="$font" />
				<xsl:with-param name="text" select="$text" />
				<xsl:with-param name="maxWidth" select="$maxWidth" />
				<xsl:with-param name="headLen" select="$newHeadLen" />
				<xsl:with-param name="headW" select="$newHeadW" />
				<xsl:with-param name="separator" select="$separator" />
			</xsl:call-template>
		</xsl:if>
	
	</xsl:if>

</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="textWidth" >
	<xsl:param name="font" />
	<xsl:param name="text" />
	
	<xsl:if test="string-length($text) = 0">
		<xsl:value-of select="0"/>
	</xsl:if>
	
	<xsl:if test="string-length($text) != 0">
		<xsl:variable name="head" select="substring($text,1,1)" />
		<xsl:variable name="tail" select="substring($text,2)" />

		<xsl:variable name="w_head">
			<xsl:call-template name="charWidth">
				<xsl:with-param name="font" select="$font" />
				<xsl:with-param name="text" select="$head" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="w_tail">
			<xsl:call-template name="textWidth">
				<xsl:with-param name="font" select="$font" />
				<xsl:with-param name="text" select="$tail" />
			</xsl:call-template>
		</xsl:variable>
		
		<!-- xsl:message>=====textWidth <xsl:value-of select="$w_head"/> + <xsl:value-of select="$w_tail"/> = <xsl:value-of select="$w_head + $w_tail"/></xsl:message -->
		<xsl:value-of select="$w_head + $w_tail"/>
		
	</xsl:if>
	
</xsl:template>

<!-- ============================================================================================================ -->

<xsl:template name="charWidth" >
	<xsl:param name="font" />
	<xsl:param name="text" />

	<xsl:variable name="ch" select="$font/*[@char=$text]" />
	<xsl:variable name="ch0" select="$font/*[@char='W']" />
	
	<xsl:variable name="w">
		<xsl:if test="$ch"> <xsl:value-of select="$ch/@width * $font/@pixelsToMM"/> </xsl:if>
		<xsl:if test="not($ch)"> <xsl:value-of select="$ch0/@width * $font/@pixelsToMM"/> </xsl:if>
	</xsl:variable>
	
	<!-- xsl:message>=====w( <xsl:value-of select="$text"/> ) = <xsl:value-of select="$w"/></xsl:message -->
	<xsl:value-of select="$w"/>
	
</xsl:template>

<!-- ============================================================================================================ -->
<!-- ============================================================================================================ -->

<xsl:variable name="_fontGostItalic0"><xsl:call-template name="generateFont_gost_a_italic"/></xsl:variable>
<xsl:variable name="fontGostItalic" select="exsl:node-set($_fontGostItalic0)/*"/>

<!-- ============================================================================================================ -->

<xsl:template name="generateFont_gost_a_italic" >

	<xsl:element name="font">
		<xsl:attribute name="pixelsToMM">0.00234</xsl:attribute>
		<xsl:attribute name="measuredForMM">4.78</xsl:attribute>

			<xsl:element name="ch"><xsl:attribute name="char"> </xsl:attribute><xsl:attribute name="width">447</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">!</xsl:attribute><xsl:attribute name="width">490</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">&quot;</xsl:attribute><xsl:attribute name="width">542</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">#</xsl:attribute><xsl:attribute name="width">1167</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">$</xsl:attribute><xsl:attribute name="width">1051</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">%</xsl:attribute><xsl:attribute name="width">1744</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">&amp;</xsl:attribute><xsl:attribute name="width">1024</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">'</xsl:attribute><xsl:attribute name="width">392</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">(</xsl:attribute><xsl:attribute name="width">432</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">)</xsl:attribute><xsl:attribute name="width">472</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">*</xsl:attribute><xsl:attribute name="width">564</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">+</xsl:attribute><xsl:attribute name="width">705</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">,</xsl:attribute><xsl:attribute name="width">267</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">-</xsl:attribute><xsl:attribute name="width">842</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">.</xsl:attribute><xsl:attribute name="width">268</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">/</xsl:attribute><xsl:attribute name="width">939</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">0</xsl:attribute><xsl:attribute name="width">891</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">1</xsl:attribute><xsl:attribute name="width">597</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">2</xsl:attribute><xsl:attribute name="width">916</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">3</xsl:attribute><xsl:attribute name="width">864</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">4</xsl:attribute><xsl:attribute name="width">799</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">5</xsl:attribute><xsl:attribute name="width">835</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">6</xsl:attribute><xsl:attribute name="width">839</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">7</xsl:attribute><xsl:attribute name="width">755</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">8</xsl:attribute><xsl:attribute name="width">895</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">9</xsl:attribute><xsl:attribute name="width">762</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">:</xsl:attribute><xsl:attribute name="width">292</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">;</xsl:attribute><xsl:attribute name="width">342</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">&lt;</xsl:attribute><xsl:attribute name="width">722</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">=</xsl:attribute><xsl:attribute name="width">739</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">&gt;</xsl:attribute><xsl:attribute name="width">679</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">?</xsl:attribute><xsl:attribute name="width">840</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">@</xsl:attribute><xsl:attribute name="width">1599</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">A</xsl:attribute><xsl:attribute name="width">861</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">B</xsl:attribute><xsl:attribute name="width">869</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">C</xsl:attribute><xsl:attribute name="width">742</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">D</xsl:attribute><xsl:attribute name="width">865</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">E</xsl:attribute><xsl:attribute name="width">785</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">F</xsl:attribute><xsl:attribute name="width">809</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">G</xsl:attribute><xsl:attribute name="width">905</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">H</xsl:attribute><xsl:attribute name="width">855</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">I</xsl:attribute><xsl:attribute name="width">492</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">J</xsl:attribute><xsl:attribute name="width">735</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">K</xsl:attribute><xsl:attribute name="width">882</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">L</xsl:attribute><xsl:attribute name="width">652</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">M</xsl:attribute><xsl:attribute name="width">1085</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">N</xsl:attribute><xsl:attribute name="width">932</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">O</xsl:attribute><xsl:attribute name="width">919</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">P</xsl:attribute><xsl:attribute name="width">895</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Q</xsl:attribute><xsl:attribute name="width">925</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">R</xsl:attribute><xsl:attribute name="width">889</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">S</xsl:attribute><xsl:attribute name="width">862</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">T</xsl:attribute><xsl:attribute name="width">855</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">U</xsl:attribute><xsl:attribute name="width">932</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">V</xsl:attribute><xsl:attribute name="width">861</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">W</xsl:attribute><xsl:attribute name="width">1333</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">X</xsl:attribute><xsl:attribute name="width">989</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Y</xsl:attribute><xsl:attribute name="width">861</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Z</xsl:attribute><xsl:attribute name="width">889</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">[</xsl:attribute><xsl:attribute name="width">696</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">\</xsl:attribute><xsl:attribute name="width">534</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">]</xsl:attribute><xsl:attribute name="width">696</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">^</xsl:attribute><xsl:attribute name="width">634</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">_</xsl:attribute><xsl:attribute name="width">880</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">`</xsl:attribute><xsl:attribute name="width"></xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">a</xsl:attribute><xsl:attribute name="width">779</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">b</xsl:attribute><xsl:attribute name="width">809</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">c</xsl:attribute><xsl:attribute name="width">775</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">d</xsl:attribute><xsl:attribute name="width">769</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">e</xsl:attribute><xsl:attribute name="width">775</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">f</xsl:attribute><xsl:attribute name="width">495</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">g</xsl:attribute><xsl:attribute name="width">799</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">h</xsl:attribute><xsl:attribute name="width">789</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">i</xsl:attribute><xsl:attribute name="width">302</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">j</xsl:attribute><xsl:attribute name="width">452</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">k</xsl:attribute><xsl:attribute name="width">732</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">l</xsl:attribute><xsl:attribute name="width">418</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">m</xsl:attribute><xsl:attribute name="width">1042</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">n</xsl:attribute><xsl:attribute name="width">762</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">o</xsl:attribute><xsl:attribute name="width">759</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">p</xsl:attribute><xsl:attribute name="width">752</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">q</xsl:attribute><xsl:attribute name="width">749</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">r</xsl:attribute><xsl:attribute name="width">628</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">s</xsl:attribute><xsl:attribute name="width">729</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">t</xsl:attribute><xsl:attribute name="width">518</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">u</xsl:attribute><xsl:attribute name="width">775</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">v</xsl:attribute><xsl:attribute name="width">662</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">w</xsl:attribute><xsl:attribute name="width">1085</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">x</xsl:attribute><xsl:attribute name="width">715</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">y</xsl:attribute><xsl:attribute name="width">732</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">z</xsl:attribute><xsl:attribute name="width">759</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">{</xsl:attribute><xsl:attribute name="width">625</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">|</xsl:attribute><xsl:attribute name="width">492</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">}</xsl:attribute><xsl:attribute name="width">624</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">~</xsl:attribute><xsl:attribute name="width">766</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char"></xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">А</xsl:attribute><xsl:attribute name="width">861</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Б</xsl:attribute><xsl:attribute name="width">899</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">В</xsl:attribute><xsl:attribute name="width">892</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Г</xsl:attribute><xsl:attribute name="width">819</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Д</xsl:attribute><xsl:attribute name="width">912</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Е</xsl:attribute><xsl:attribute name="width">795</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Ж</xsl:attribute><xsl:attribute name="width">1072</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">З</xsl:attribute><xsl:attribute name="width">902</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">И</xsl:attribute><xsl:attribute name="width">939</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Й</xsl:attribute><xsl:attribute name="width">919</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">К</xsl:attribute><xsl:attribute name="width">875</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Л</xsl:attribute><xsl:attribute name="width">995</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">М</xsl:attribute><xsl:attribute name="width">1095</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Н</xsl:attribute><xsl:attribute name="width">869</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">О</xsl:attribute><xsl:attribute name="width">909</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">П</xsl:attribute><xsl:attribute name="width">885</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Р</xsl:attribute><xsl:attribute name="width">889</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">С</xsl:attribute><xsl:attribute name="width">779</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Т</xsl:attribute><xsl:attribute name="width">859</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">У</xsl:attribute><xsl:attribute name="width">895</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Ф</xsl:attribute><xsl:attribute name="width">1282</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Х</xsl:attribute><xsl:attribute name="width">989</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Ц</xsl:attribute><xsl:attribute name="width">985</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Ч</xsl:attribute><xsl:attribute name="width">882</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Ш</xsl:attribute><xsl:attribute name="width">1112</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Щ</xsl:attribute><xsl:attribute name="width">1179</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Ъ</xsl:attribute><xsl:attribute name="width">899</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Ы</xsl:attribute><xsl:attribute name="width">1019</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Ь</xsl:attribute><xsl:attribute name="width">889</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Э</xsl:attribute><xsl:attribute name="width">909</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Ю</xsl:attribute><xsl:attribute name="width">999</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">Я</xsl:attribute><xsl:attribute name="width">922</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">а</xsl:attribute><xsl:attribute name="width">769</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">б</xsl:attribute><xsl:attribute name="width">765</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">в</xsl:attribute><xsl:attribute name="width">751</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">г</xsl:attribute><xsl:attribute name="width">715</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">д</xsl:attribute><xsl:attribute name="width">775</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">е</xsl:attribute><xsl:attribute name="width">755</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">ж</xsl:attribute><xsl:attribute name="width">889</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">з</xsl:attribute><xsl:attribute name="width">655</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">и</xsl:attribute><xsl:attribute name="width">749</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">й</xsl:attribute><xsl:attribute name="width">779</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">к</xsl:attribute><xsl:attribute name="width">732</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">л</xsl:attribute><xsl:attribute name="width">722</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">м</xsl:attribute><xsl:attribute name="width">819</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">н</xsl:attribute><xsl:attribute name="width">802</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">о</xsl:attribute><xsl:attribute name="width">739</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">п</xsl:attribute><xsl:attribute name="width">732</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">р</xsl:attribute><xsl:attribute name="width">725</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">с</xsl:attribute><xsl:attribute name="width">729</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">т</xsl:attribute><xsl:attribute name="width">1045</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">у</xsl:attribute><xsl:attribute name="width">725</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">ф</xsl:attribute><xsl:attribute name="width">1015</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">х</xsl:attribute><xsl:attribute name="width">702</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">ц</xsl:attribute><xsl:attribute name="width">792</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">ч</xsl:attribute><xsl:attribute name="width">735</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">ш</xsl:attribute><xsl:attribute name="width">1059</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">щ</xsl:attribute><xsl:attribute name="width">1115</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">ъ</xsl:attribute><xsl:attribute name="width">832</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">ы</xsl:attribute><xsl:attribute name="width">872</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">ь</xsl:attribute><xsl:attribute name="width">722</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">э</xsl:attribute><xsl:attribute name="width">742</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">ю</xsl:attribute><xsl:attribute name="width">839</xsl:attribute></xsl:element>
			<xsl:element name="ch"><xsl:attribute name="char">я</xsl:attribute><xsl:attribute name="width">732</xsl:attribute></xsl:element>

	</xsl:element>

</xsl:template>

<!-- ============================================================================================================ -->
</xsl:stylesheet>