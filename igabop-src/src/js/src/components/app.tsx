import React from 'react';

import { CustomReactComponent, ICustomReactComponentProps, ICustomReactComponentState } from './CustomReactComponent';
import Treegrid from './treegrid';
import BopToolbar from './bopToolbar';
import Resources from './resources';

import 'react-notifications-component/dist/theme.css';
import ReactNotification from 'react-notifications-component';
import { v4 as uuidv4 } from 'uuid';

import { CustomizeColumnsComponent, ICustomizeColumnsTableItem } from '../components/CustomizeColumnsComponent';

import {
  ATTRIBUTE_KIT_NUM_OPERATION,
  ATTRIBUTE_KIT_PREP_TIME,
  ATTRIBUTE_KIT_TPCS,
  ATTRIBUTE_KIT_TRANSITION_NUMBER,
  ATTRIBUTE_TITLE,
  ATTRIBUTE_V_DESCRIPTION,
  ATTRIBUTE_V_NAME,
  ATTRIBUTE_V_WIINSTRUCTION_TEXT,
  BOP_PATH,
  doc_types,
  ebom_types,
  instruction_types,
  material2_types,
  material_types,
  mbom_types,
  op_types,
  product_types,
  selects,
  sys_types,
  TYPE_INSTRUCTION_REFERENCE,
  TYPE_KIT_FACTORY,
  TYPE_KIT_MAIN_OP,
  TYPE_KIT_MFG_ASSEMBLY,
  TYPE_KIT_MFG_PRODUCED_PART,
  TYPE_KIT_SHOP_FLOOR,
  TYPE_KIT_WORK_CELL
} from './consts';
import { DropContainerComponent } from './dropContainerComponent';

import 'react-contexify/dist/ReactContexify.min.css';
import { contextMenu, Item, Menu, Separator } from 'react-contexify';

import Loader from './loader';
import { Panel, PanelsLayout, View } from './PanelsLayout';

import { Route, TreeObj } from '../store/interfaces';

import { bopColumns } from './formats/columns';

const expandIcon = require('../images/tree-structure.svg');
const refreshIcon = require('../images/refresh.svg');
const rightIcon = require('../images/arrowRightSolid.svg');
const leftIcon = require('../images/arrowLeftSolid.svg');

const rmbItemStyle = {
  width: 20,
  height: 20,
  marginRight: 5
};

interface IAppProps extends ICustomReactComponentProps {
  droppable: Function;
  draggable: Function;
  clean: Function;
  widgetId: string;
}

interface IAppState extends ICustomReactComponentState {
  treeData: TreeObj[];
  expandedRowKeys: any[];
  alreadyFetched: any;
  selectedRows: any[];
  forceUpdate: boolean;
  file: File;
  loadingGhostRows: string[];
  objectId: string;
  isCustomizing: boolean;
  doRefresh: boolean;
}

const api = BOP_PATH;

export default class App extends CustomReactComponent<IAppProps, IAppState> {
  protected appRef: any;

  constructor(props: IAppProps) {
    super(props);

    this.state = {
      isLoading: false,
      treeData: [],
      expandedRowKeys: [],
      alreadyFetched: {},
      selectedRows: [],
      forceUpdate: false,
      file: null,
      loadingGhostRows: [],
      objectId: this.widget.getValue('objectId'),
      isCustomizing: false,
      doRefresh: false
    };

    this.appRef = React.createRef();

    this.contextMenuItemClick = this.contextMenuItemClick.bind(this);
    this.contextMenuItemClick0 = this.contextMenuItemClick0.bind(this);
    this.contextMenuItemClick1 = this.contextMenuItemClick1.bind(this);
    this.contextMenuItemClick2 = this.contextMenuItemClick2.bind(this);
    this.handleRefreshClick = this.handleRefreshClick.bind(this);
    this.doLifecycle = this.doLifecycle.bind(this);
    this.doPromote = this.doPromote.bind(this);
    this.doDemote = this.doDemote.bind(this);
  }

  componentDidMount() {
    if (this.state.objectId !== '') {
      this.loadRoot(this.state.objectId);
    }
    this.props.droppable(this.appRef.current, {
      drop: (data: any) => this.onDocumentDrop(data)
    });
  }

  render() {
    return (
      <div className="root" ref={this.appRef}>
        <ReactNotification />
        <Menu id="menu_id" style={{ zIndex: 1 }}>
          <Item onClick={this.contextMenuItemClick1}>
            <img src={expandIcon} style={rmbItemStyle} />
            Expand 1 level
          </Item>
          <Item onClick={this.contextMenuItemClick2}>
            <img src={expandIcon} style={rmbItemStyle} />
            Expand 2 level
          </Item>
          <Item onClick={this.contextMenuItemClick0}>
            <img src={expandIcon} style={rmbItemStyle} />
            Expand all
          </Item>
          <Separator />
          <Item onClick={this.handleRefreshClick}>
            <img src={refreshIcon} style={rmbItemStyle} />
            Refresh
          </Item>
          <Separator />
          <Item onClick={this.doPromote}>
            <img src={rightIcon} style={rmbItemStyle} />
            Promote
          </Item>
          <Item onClick={this.doDemote}>
            <img src={leftIcon} style={rmbItemStyle} />
            Demote
          </Item>
        </Menu>
        {this.state.objectId ? (
          <>
            <PanelsLayout componentId="bop">
              <View>
                {!this.state.isCustomizing ? (
                  <>
                    <Treegrid
                      forceUpdate={this.state.forceUpdate}
                      treeData={this.state.treeData}
                      onExpand={this.handleExpandedTreeNode}
                      onSelect={this.handleSelectedTreeRow}
                      onSaveCellContent={this.handleSaveCellContent}
                      onDrop={this.handleDropDocument}
                      droppable={this.props.droppable}
                      draggable={this.props.draggable}
                      clean={this.props.clean}
                      widgetId={this.props.widgetId}
                      loadingRows={this.state.loadingGhostRows}
                      expandedRowKeys={this.state.expandedRowKeys}
                      onContextMenu={this.onContextMenu}
                      multiselect={true}
                      selectedRows={this.state.selectedRows}
                      componentId="bopTable"
                    />
                    <BopToolbar selectedRows={this.state.selectedRows} onAction={this.handleToolbarAction} />
                  </>
                ) : (
                  <CustomizeColumnsComponent
                    componentId="bopTable"
                    columns={bopColumns as ICustomizeColumnsTableItem[]}
                    onSave={this.handleCustomizeClose}
                    onClose={this.handleCustomizeClose}
                    url={null}
                  />
                )}
              </View>
              <Panel direction="right" collapsed={false} width={450}>
                <Resources
                  droppable={this.props.droppable}
                  doRefresh={this.state.doRefresh}
                  api={api}
                  url={this.props.url}
                  scHeader={this.props.scHeader}
                  selectedObjects={this.state.selectedRows}
                  updateTableData={this.updateTableData}
                  onAddToStructure={this.handleAddToStructure}
                  setIsLoading={this.handleSetIsLoading}
                  treeData={this.state.treeData}
                  onRefreshIds={this.updateTableData}
                  rootId={this.state.objectId}
                />
              </Panel>
            </PanelsLayout>
          </>
        ) : (
          DropContainerComponent('Drop here')
        )}
        {this.state.isLoading ? <Loader style="resources-loader" /> : null}
      </div>
    );
  }

  protected onDocumentDrop(data: any) {
    try {
      const { objectType, objectId } = JSON.parse(data).data.items[0];
      if (objectType === 'PPRContext' || ebom_types.includes(objectType)) {
        this.loadRoot(objectId);
        this.setState({
          objectId
        });
        this.widget.setValue('objectId', objectId);
      } else {
        alert('Drop PPR or EBOM');
      }
    } catch (e) {
      console.log(e);
    }
  }

  // called when user click rmb row in treegrid
  protected onContextMenu(row: any) {
    row.event.preventDefault();
    contextMenu.show({
      id: 'menu_id',
      event: row.event,
      props: row.rowData
    });
  }

  // called when user click item in rmb menu
  protected contextMenuItemClick(event: any, recurseToLevel: number) {
    // don't know why props is inside event
    this.doExpand(event.props, recurseToLevel, false);
  }

  // todo awful, remove and use only above function
  protected contextMenuItemClick0(event: any) {
    this.contextMenuItemClick(event, 0);
  }

  protected contextMenuItemClick1(event: any) {
    this.contextMenuItemClick(event, 1);
  }

  protected contextMenuItemClick2(event: any) {
    this.contextMenuItemClick(event, 2);
  }

  protected doLifecycle(action: string) {
    const selRows = this.state.selectedRows.map(row => row.rowData);
    const selStates = selRows.map(row => row.current.name);
    const data = {
      selected: selRows,
      states: selStates
    };

    const url = `${api}/systems/${action}?selects=${selects}`;
    this.Fetch(url, 'PUT', { data: JSON.stringify(data) })
      .then((data: any) => this.updateTableData(data, true))
      .catch(error => console.log(error));
  }

  protected doPromote() {
    this.doLifecycle('promote');
  }

  protected doDemote() {
    this.doLifecycle('demote');
  }

  protected doPrepare() {
    const treeData = this.state.treeData.filter(row => row.title !== 'Loading...');
    const toPrepare = treeData.filter(
      row => row.current.name === 'IN_WORK' && row.routes.length > 0 && row.routes[0].status === 'Started'
    );
    const data = { selected: toPrepare };

    const url = `${api}/systems/prepare?selects=${selects}`;
    this.Fetch(url, 'PUT', { data: JSON.stringify(data) })
      .then((data: any) => this.updateTableData(data, true))
      .catch(error => console.log(error));
  }

  protected doReport(selectedObjectId: string) {
    // alert( "doReport " + selectedObjectId + " " + typeof(selectedObjectId) )
    const pprId: string = this.widget.getValue('objectId');
    const url = `${api}/export/${pprId}/pdf/${selectedObjectId}?selects=${selects}`;
    this.Fetch(url, 'GET')
      .then((data: any) => this.updateTableData(data, true))
      .catch(error => console.log(error));
  }

  protected emptyOrValue(s: any) {
    return s ? s.value : '';
  }

  protected format(json: any): TreeObj {
    return {
      id: this.formatID(json),
      key: this.formatID(json),
      physicalid: json.physicalid,
      name: json.basics.name,
      originated: json.basics.originated?.date,
      modified: json.basics.modified?.date,
      owner: json.basics.owner?.name,
      current: json.basics.current,
      revision: json.basics.revision,
      typeId: json.basics.type.name,
      type: json.basics.type.name,
      title: this.formatTitle(json),
      WItext: this.emptyOrValue(json.attributes[ATTRIBUTE_V_WIINSTRUCTION_TEXT]),
      Kit_PrepTime: this.emptyOrValue(json.attributes[ATTRIBUTE_KIT_PREP_TIME]),
      Kit_Tpcs: this.emptyOrValue(json.attributes[ATTRIBUTE_KIT_TPCS]),
      description: this.emptyOrValue(json.attributes[ATTRIBUTE_V_DESCRIPTION]),
      file: this.emptyOrValue(json.attributes[ATTRIBUTE_TITLE]),
      NumOperation: this.formatNumber(json),
      leaf: !this.hasChildren(json),
      icon: json.info['type.property[IPML.IconName].value'],
      instanceId: json.info.instanceId,
      reservedby: json.info.reservedby,
      parentId: json.info.parentId,
      routes: this.formatRoutes(json.info),
      docs: this.formatDocs(json.info),
      path: json.info.path,
      scoped: this.formatScoped(json.info),
      info: json.info
    };
  }

  protected formatID(json: any) {
    return json.info.instanceId ? json.info.instanceId : json.physicalid;
  }

  protected formatNumber(json: any) {
    let number = '';
    if (op_types.includes(json.basics.type.name)) number = json.attributes[ATTRIBUTE_KIT_NUM_OPERATION].value;
    else if (instruction_types.includes(json.basics.type.name))
      number = json.attributes[ATTRIBUTE_KIT_TRANSITION_NUMBER].value;

    return number;
  }

  protected formatTitle(json: any) {
    let title = this.emptyOrValue(json.attributes[ATTRIBUTE_V_NAME]);
    if (json.basics.type.name === 'Document') title = this.emptyOrValue(json.attributes[ATTRIBUTE_TITLE]);

    if (title === '') title = json.basics.name;

    return title;
  }

  protected formatRoutes(info: any): Route[] {
    let newArr = [];
    let name = info['from[Object Route].to'];
    let owner = info['from[Object Route].to.owner'];
    let status = info['from[Object Route].to.attribute[Route Status]'];
    let action = info['from[Object Route].to.attribute[Route Completion Action]'];
    let physicalid = info['from[Object Route].to.physicalid'];
    if (physicalid) {
      if (name.includes('\u0007')) {
        name = name.split('\u0007');
        owner = owner.split('\u0007');
        status = status.split('\u0007');
        action = action.split('\u0007');
        physicalid = physicalid.split('\u0007');
      }

      if (Array.isArray(name))
        for (let i = 0; i < name.length; i++)
          newArr.push({
            name: name[i],
            owner: owner[i],
            status: status[i],
            action: action[i],
            physicalid: physicalid[i]
          });
      else newArr.push({ name, owner, status, action, physicalid });
    }

    newArr = newArr.filter(r => r.status !== 'Finished' && r.action === 'Promote Connected Object');

    return newArr;
  }

  protected formatDocs(info: any): TreeObj[] {
    let newArr = [];
    if (info.docs) newArr = info.docs.map((e: any) => this.format(e));
    return newArr;
  }

  protected formatScoped(info: any): string[] {
    let newArr = [];
    if (info.scoped) newArr = info.scoped;
    return newArr;
  }

  protected removeLoadingRowsFromTree(parentId: string, tree?: TreeObj[]) {
    let treeData = tree || this.state.treeData;
    if (parentId) {
      const parentIds = parentId.split(',');
      treeData = treeData.filter((row: any) => !(parentIds.includes(row.parentId) && row.title === 'Loading...'));
    }
    return treeData;
  }

  // todo transfer this logic to BE
  protected hasChildren(json: any) {
    const type = json.basics.type.name;
    let hasChildren = false;
    if (type === 'PPRContext') {
      hasChildren = json.info.hasOwnProperty(
        'from[VPLMrel/PLMConnection/V_Owner].to.paths[SemanticRelation].path.element[0].type'
      );
    } else if (mbom_types.includes(type)) {
      hasChildren =
        json.info.hasOwnProperty('from[DELFmiFunctionIdentifiedInstance].to') ||
        json.info.hasOwnProperty('from[ProcessInstanceContinuous].to');
    } else if (sys_types.includes(type)) {
      hasChildren =
        json.info.hasOwnProperty('from[DELLmiHeaderOperationInstance].to') ||
        json.info.hasOwnProperty('from[DELLmiGeneralSystemInstance].to');
    } else if (op_types.includes(type)) {
      hasChildren = json.info.hasOwnProperty('from[DELWkiInstructionInstance].to');
      // || json.info.hasOwnProperty('from[VPLMrel/PLMConnection/V_Owner].to.paths[SemanticRelation].path.element[0].type')
    } else if (product_types.includes(type)) {
      hasChildren = json.info.hasOwnProperty('from[VPMInstance].to');
    }

    return hasChildren;
  }

  protected getCreateUrl(parentType: string, parentId: string, newObjectType?: string, objectId?: string) {
    // "PPRContext" === type by default
    let url = `${api}/pprcontexts/${parentId}/insertGeneralSystem?${
      objectId ? `gsys=${objectId}` : 'type=Kit_Factory'
    }&selects=${selects}`;

    if (parentType === 'DELLmiGeneralSystemReference')
      url = `${api}/systems/${parentId}/insertGeneralSystem?${
        objectId ? `gsys=${objectId}` : 'type=Kit_Factory'
      }&selects=${selects}`;

    if (parentType === 'Kit_Factory')
      url = `${api}/systems/${parentId}/insertGeneralSystem?${
        objectId ? `gsys=${objectId}` : 'type=Kit_ShopFloor'
      }&selects=${selects}`;

    if (parentType === 'Kit_ShopFloor')
      url = `${api}/systems/${parentId}/insertGeneralSystem?${
        objectId ? `gsys=${objectId}` : 'type=Kit_WorkCell'
      }&selects=${selects}`;

    if (parentType === 'Kit_WorkCell')
      url = `${api}/systems/${parentId}/insertHeaderOperation?${
        objectId ? `gsys=${objectId}` : 'type=Kit_MainOp'
      }&selects=${selects}`;

    if (op_types.includes(parentType)) url = `${api}/operations/${parentId}/createInstruction?selects=${selects}`;

    return url;
  }

  protected getDeleteUrl(parentType: string, parentId: string, idsToDelete: string) {
    // by default for All types
    let url = `${api}/systems/${parentId}/?ids=${idsToDelete}&selects=${selects}`;

    if (op_types.includes(parentType)) url = `${api}/operations/${parentId}/?ids=${idsToDelete}&selects=${selects}`;

    return url;
  }

  protected executeAction(action: string, selectedObject: any, params?: any) {
    let url = '';
    let selId = '';
    let selType = '';

    switch (action) {
      case 'report':
        this.doReport(selectedObject.rowData.id);
        break;
      case 'edit':
        selId = selectedObject.rowData.physicalid; // get selected row from Tree
        url = `${api}/instructions/${selId}?selects=${selects}`;
        this.Fetch(url, 'PUT', {
          data: JSON.stringify(params)
        })
          .then((response: any) => {
            this.updateTableData(response, true);
          })
          .catch(error => {
            console.log(error);
            this.setState({
              forceUpdate: !this.state.forceUpdate
            });
          });
        break;
      case 'delete':
        this.doDelete();
        break;
      case 'setNumOperation':
        this.doSetNumbers(selectedObject.rowData);
        break;
      case 'up':
      case 'down':
        const { instanceId } = selectedObject.rowData;
        const { parentId } = selectedObject.rowData;
        const parentPID = this.state.treeData.find(row => row.id === parentId).physicalid;
        selType = selectedObject.rowData.type; // get selected row from Tree

        if (
          op_types.includes(selType) ||
          (sys_types.includes(selType) && selType !== 'Kit_Factory') ||
          instruction_types.includes(selType)
        ) {
          url = `${api}/systems/setTreeOrder_v2?relId=${instanceId}&parentId=${parentId}&direction=${action}&selects=${selects}&parentPID=${parentPID}`;
          this.Fetch(url, 'PUT')
            .then((data: any) => this.shiftRow(data, parentId))
            .catch(error => console.log(error));
        } else alert(`This button doesn't work for type '${selType}'`);
        break;
      case 'expand':
        this.doExpand(selectedObject.rowData, 0, false);
        break;
      case 'reserve':
        this.doReserve();
        break;
      case 'unreserve':
        this.doUnreserve();
        break;
      case 'promote':
        this.doPromote();
        break;
      case 'demote':
        this.doDemote();
        break;
      default:
        break;
    }
  }

  protected doDelete = () => {
    if (confirm('Вы действительно хотите удалить выделенные объекты?')) {
      const selRows = this.state.selectedRows;
      const selIDs = selRows.map(row => row.rowData.id);
      const selObjs = selRows.map(row => {
        return {
          instanceId: row.rowData.instanceId,
          physicalid: row.rowData.physicalid,
          type: row.rowData.typeId
        };
      });
      const url = `${api}/systems`;
      const deleteJSON = { selected: selObjs };
      this.Fetch(url, 'DELETE', {
        data: JSON.stringify(deleteJSON)
      })
        .then(data => {
          let { treeData } = this.state;
          treeData = treeData.filter((node: any) => !selIDs.includes(node.id));
          this.setState({
            treeData,
            selectedRows: []
          });
          return data;
        })
        .then(data => {
          const instructionsParents = selRows
            .filter(row => row.rowData.typeId === 'DELWkiInstructionReference')
            .map(row => row.rowData.parentId);
          this.getObjectsFromTreeDataByKey(instructionsParents).forEach(operation => this.doSetNumbers(operation));
          this.updateTableData(data, true);
        })
        .catch(error => console.log(error));
    }
  };

  /**
   * Searches object from state by provided key.
   */
  protected getObjectsFromTreeDataByKey = (instructions: string[], key: 'instanceId' | 'physicalid' = 'instanceId') => {
    return this.state.treeData.filter(row => instructions.indexOf(row[key]) >= 0);
  };

  protected handleToolbarAction = (actionType: string, objectType: string) => {
    const selectedRow = this.state.selectedRows;
    if (selectedRow[0]) {
      const params = { objectType };
      this.executeAction(actionType, selectedRow[0], params);
    } else if (actionType === 'report') {
      const pprId: string = this.widget.getValue('objectId');
      this.doReport(pprId);
    }

    // no need to select any objects
    if (actionType === 'refresh') this.doRefresh();
    if (actionType === 'prepare') this.doPrepare();

    if (actionType === 'customize') {
      this.setState({
        isCustomizing: true
      });
    }
  };

  protected handleCustomizeClose = () => {
    this.setState({
      isCustomizing: false
    });
  };

  protected handleCustomizeSave = () => {};

  protected toggleReserve = (action: string) => {
    const selected = this.state.selectedRows.map(row => row.rowData);
    const data = { selected };

    const url = `${api}/systems/${action}?selects=${selects}`;
    this.Fetch(url, 'PUT', { data: JSON.stringify(data) })
      .then((data: any) => this.updateTableData(data, true))
      .catch(error => console.log(error));
  };

  protected doReserve = () => {
    this.toggleReserve('reserve');
  };

  protected doUnreserve = () => {
    this.toggleReserve('unreserve');
  };

  protected doRefresh = () => {
    const ids = this.state.treeData.map(node => node.physicalid);
    const params = { ids };
    const url = `${api}/systems/refresh?selects=${selects}`;
    this.Fetch(url, 'POST', { data: JSON.stringify(params) })
      .then((data: any) => this.updateTableData(data))
      .catch(error => console.log(error));
  };

  protected handleRefreshClick = () => {
    this.doRefresh();
  };

  protected doExpand = (row: TreeObj, recurseTolevel: number, isCreation: boolean) => {
    const selType = row.typeId;
    const selPID = row.physicalid;
    const selId = row.id;
    if (selType === 'DELWkiInstructionReference') alert(`This button doesn't work for type '${selType}'`);
    else {
      // show 'loading' row before getting data
      const { expandedRowKeys } = this.state;
      expandedRowKeys.push(selId);
      this.setState({ expandedRowKeys });

      let url = `${api}/systems/${selPID}/structure?selects=${selects}&recurseToLevel=${recurseTolevel}&parentId=${selId}`;
      if (selType === 'PPRContext')
        url = `${api}/pprcontexts/${selPID}/structure?selects=${selects}&recurseToLevel=${recurseTolevel}&parentId=${selId}`;
      else if (product_types.includes(selType))
        url = `${api}/resources/${selPID}/structure?selects=${selects}&recurseToLevel=${recurseTolevel}&parentId=${selId}`;
      else if (mbom_types.includes(selType))
        url = `${api}/functions/${selPID}/structure?selects=${selects}&recurseToLevel=${recurseTolevel}&parentId=${selId}&pprContextId=${this.state.objectId}`;
      else if (op_types.includes(selType))
        url = `${api}/operations/${selPID}/instructions?selects=${selects}&parentId=${selId}`;

      this.Fetch(url, 'GET')
        .then((data: any) => this.expandStructure(data, selId, isCreation, false))
        .catch(error => console.log(error));
    }
  };

  protected updateTableData = (response: any, ignoreInstanceId?: boolean) => {
    let { treeData } = this.state;
    treeData = treeData.map((node: TreeObj) => {
      const newEl = response.filter((item: any) => {
        let isFiltered = node.physicalid === item.physicalid && node.type === item.basics.type.name;
        if (!ignoreInstanceId) {
          isFiltered = isFiltered && node.instanceId === item.info.instanceId;
        }
        return isFiltered;
      });
      if (newEl.length > 0) {
        const newNode = this.format(newEl[0]);

        // need store ids from old version and update only business attrs
        newNode.id = node.id;
        newNode.key = node.key;
        newNode.parentId = node.parentId;
        newNode.instanceId = node.instanceId;

        node = newNode;
      }
      return node;
    });

    this.setState({ treeData });

    this.updateSelectedRows(response);
  };

  protected updateSelectedRows = (response: any) => {
    let { selectedRows } = this.state;
    selectedRows = selectedRows.map((node: any) => {
      const newEl = response.filter((item: any) => node.rowData.physicalid == item.physicalid);
      if (newEl.length > 0) {
        // todo need full update so future changes can lead to partial update
        const newNode = this.format(newEl[0]);
        node.rowData.current = newNode.current;
        node.rowData.routes = newNode.routes;
      }
      return node;
    });

    this.setState({ selectedRows });
  };

  protected shiftRow(data: any, parentId: string) {
    const ids = data.map((obj: any) => obj.physicalid);
    const filteredTreeData = this.state.treeData.filter(row => !ids.includes(row.physicalid));
    const updatedTreeData = this.getFormattedData(data, parentId, filteredTreeData);
    this.setState({ treeData: updatedTreeData });
  }

  protected updateNumOperations(data: any, type: string) {
    const attrName = TYPE_KIT_FACTORY === type ? ATTRIBUTE_KIT_NUM_OPERATION : ATTRIBUTE_KIT_TRANSITION_NUMBER;
    const idToValue = new Map();
    data.forEach((i: any) => idToValue.set(i.physicalid, i.attributes[attrName].value));

    const { treeData } = this.state;
    treeData.forEach((node: any) => {
      if (idToValue.has(node.physicalid)) node.NumOperation = idToValue.get(node.physicalid);
    });

    this.setState({ treeData });
  }

  private doSetNumbers(rowData: TreeObj) {
    const selId = rowData.physicalid;
    const selType = rowData.type;
    const { id } = rowData;
    let url = `${api}/systems/${selId}/setNumOperation?selects=${selects}`;
    if (TYPE_KIT_MAIN_OP === selType)
      url = `${api}/operations/${selId}/setTransitionNumbers?parentId=${id}&selects=${selects}`;

    if (TYPE_KIT_FACTORY === selType || TYPE_KIT_MAIN_OP === selType) {
      this.Fetch(url, 'PUT')
        .then((data: any) => this.updateNumOperations(data, selType))
        .catch(error => console.log(error));
    } else alert(`This button doesn't work for '${selType}' objects`);
  }

  protected getFormattedData(fetchResponse: any, parentId: string, tree?: TreeObj[]): TreeObj[] {
    let treeData = this.removeLoadingRowsFromTree(parentId, tree);

    if (!Array.isArray(fetchResponse)) fetchResponse = [fetchResponse];

    const fetchedTreeData: any = [];
    fetchResponse.forEach((row: any) => {
      const formattedRow = this.format(row);
      const wipObj = treeData.find((node: any) => node.id === formattedRow.id);

      if (!wipObj) {
        if (!formattedRow.parentId) formattedRow.parentId = parentId;

        fetchedTreeData.push(formattedRow);

        const childId = treeData.find(node => node.parentId === formattedRow.id);

        // if(!formattedRow.leaf && op_types.includes(formattedRow.typeId)) {
        // 	this.setExpanded(formattedRow.id);
        // 	this.setFetched(formattedRow.id);
        // }

        if (!formattedRow.leaf && !childId) {
          const loadingChild: any = {};
          loadingChild.parentId = formattedRow.id;
          loadingChild.title = 'Loading...';
          loadingChild.id = uuidv4();
          loadingChild.key = uuidv4();
          fetchedTreeData.push(loadingChild);

          const { loadingGhostRows } = this.state;
          loadingGhostRows.push(loadingChild.id);
          this.setState({ loadingGhostRows });
        }
      } else {
        // if exist in the structure need update
        formattedRow.parentId = wipObj.parentId;
        fetchedTreeData.push(formattedRow); // required for expandStructure and createResource cases

        const index = treeData.indexOf(wipObj);
        treeData.splice(index, 1);
      }
    });

    treeData = treeData.concat(fetchedTreeData);

    return treeData;
  }

  protected handleExpandedTreeNode = (nodeData: any) => {
    const { alreadyFetched } = this.state;
    const expandedRowKeys = [...this.state.expandedRowKeys];
    const { id } = nodeData.rowData;

    if (nodeData.expanded) {
      expandedRowKeys.push(id);
      if (!alreadyFetched[id]) {
        alreadyFetched[id] = true;
        this.doExpand(nodeData.rowData, 1, false);
      }
    } else {
      const index = expandedRowKeys.indexOf(id);
      if (index > -1) expandedRowKeys.splice(index, 1);
    }

    this.setState({
      alreadyFetched,
      expandedRowKeys
    });
  };

  protected handleDropDocument = (pid: string, type: string, path: string[], row: TreeObj) => {
    if (type === 'PPRContext' || ebom_types.includes(type)) {
      // case of PPRContext as dropped element
      this.loadRoot(pid);
      this.widget.setValue('objectId', pid);
    } else if (mbom_types.includes(type) && row) {
      // case of MBOM obj as dropped element
      if (material_types.includes(type) && row.typeId === 'Kit_MfgBar') {
        // insert Predecessor
        this.doInsertPredecessor(row, pid);
      } else if (
        material2_types.includes(type) &&
        (row.typeId === 'Kit_MfgProducedPart' || row.typeId === 'Kit_MfgAssembly')
      ) {
        this.doInsertPredecessor2(row, pid);
      } else {
        this.doCreateImplementLink(row, pid, path);
      }
    } else if (
      (TYPE_KIT_MAIN_OP === type ||
        TYPE_KIT_WORK_CELL === type ||
        TYPE_KIT_SHOP_FLOOR === type ||
        TYPE_INSTRUCTION_REFERENCE === type) &&
      row
    ) {
      this.doCloneStructure(row, type, pid);
    } else if (doc_types.includes(type) && row) {
      // todo think about dropping other types
      // this.executeAction('insert', null, params);
    }
  };

  private doInsertPredecessor(row: TreeObj, pid: string) {
    const url = `${api}/functions/${row.physicalid}/insertPredecessor?instanceId=${row.instanceId}&parentId=${row.parentId}&predecessor=${pid}&selects=${selects}`;
    this.Fetch(url, 'POST')
      .then((data: any) => {
        if (data && data.length === 0) alert('Основной материал уже назначен');
        else this.setState({ treeData: this.getFormattedData(data, null) });
      })
      .catch(error => console.log(error));
  }

  private doInsertPredecessor2(row: TreeObj, pid: string) {
    const url = `${api}/functions/${row.physicalid}/insertPredecessor2?parentId=${row.id}&predecessor=${pid}&selects=${selects}`;
    this.Fetch(url, 'POST')
      .then((data: any) => this.setState({ treeData: this.getFormattedData(data, null) }))
      .catch(error => console.log(error));
  }

  private doCreateImplementLink(row: TreeObj, pid: string, mbomPath: string[]) {
    mbomPath = this.getMBOMPath(pid, mbomPath);

    // validating scope
    const bopRoot = this.getBOPRoot(row);
    const scopedInSession = this.state.treeData.find(node => bopRoot.scoped.includes(node.physicalid));

    if (scopedInSession) {
      const url = `${api}/operations/${row.physicalid}/createImplementLink?bopRootPID=${bopRoot.physicalid}&pprContextId=${this.state.objectId}&bopPath=${row.path}&mbomPath=${mbomPath}&selects=${selects}`;
      this.Fetch(url, 'PUT')
        .then((reqData: any) => {
          this.setState({ doRefresh: !this.state.doRefresh });
          this.updateTableData(reqData.filter((item: any) => item.physicalid === pid));
        })
        .catch(error => console.log(error));
    } else alert(`${bopRoot.title} has no scoped MBOM object in session`);
  }

  private getMBOMPath(pid: string, mbomPath: string[]) {
    // if root MBOM obj is dropped then path consists of reference id
    const mbom = this.state.treeData.find(node => node.physicalid === pid);
    if (
      mbom &&
      (mbom.typeId === TYPE_KIT_MFG_PRODUCED_PART || mbom.typeId === TYPE_KIT_MFG_ASSEMBLY) &&
      mbom.path.length === 0
    )
      mbomPath = [pid];

    // if mbom object was inserted as a predecessor it has no path from server
    // todo work only if inserted mbom ref is THE ONLY instance in current mbom structure
    if (!mbomPath && mbom.instanceId) {
      const parent = this.state.treeData.find(node => node.id === mbom.parentId);
      mbomPath = [mbom.instanceId];
      if (parent && mbom.typeId !== TYPE_KIT_MFG_PRODUCED_PART && mbom.typeId !== TYPE_KIT_MFG_ASSEMBLY) {
        mbomPath = [...parent.path];
        mbomPath.push(mbom.instanceId);
      }
    }

    return mbomPath;
  }

  private getBOPRoot(row: TreeObj) {
    let parent = this.state.treeData.find(node => node.id === row.parentId);

    if (parent && row.typeId !== TYPE_KIT_FACTORY) parent = this.getBOPRoot(parent);
    else parent = row;

    return parent;
  }

  private doCloneStructure(row: TreeObj, droppedType: string, pid: string) {
    const targetType = row.typeId;
    let parent = this.state.treeData.find(node => node.physicalid === row.physicalid);
    if (droppedType === targetType) {
      parent = this.state.treeData.find(node => node.id === row.parentId);
    }
    const url = `${api}/systems/${pid}/cloneStructure?parentPID=${parent.physicalid}&instanceId=${row.instanceId}&selects=${selects}`;
    this.Fetch(url, 'PUT')
      .then((data: any) => {
        if (data.length === 0) {
          alert(
            'Копирование в структуру запрещено. Выберите объект разрешенный к использованию в указанной структуре.'
          );
        } else {
          this.expandStructure(data, parent.id, false, true);
        }
      })
      .then(() => {
        if (droppedType === TYPE_INSTRUCTION_REFERENCE && targetType === TYPE_INSTRUCTION_REFERENCE) {
          const instructionsParents: string[] = this.getObjectsFromTreeDataByKey([row.instanceId]).map(
            row => row.parentId
          );
          this.getObjectsFromTreeDataByKey(instructionsParents).forEach(operation => this.doSetNumbers(operation));
        } else if (droppedType === TYPE_INSTRUCTION_REFERENCE && targetType === TYPE_KIT_MAIN_OP) {
          this.doSetNumbers(row);
        }
      })
      .catch(error => console.log(error));
  }

  protected loadRoot(pid: string) {
    // service to get root PPRContext, EBOM or MBOM obj
    this.setState({ treeData: [], objectId: pid }, () => {
      this.Fetch(`${api}/pprcontexts/${pid}?selects=${selects}`, 'GET')
        .then(data => this.expandStructure(data, null, false, true))
        .catch(error => console.log(error));
    });
  }

  protected handleAddToStructure = (data: any, selected: TreeObj) => {
    const { expandedRowKeys } = this.state;
    const { alreadyFetched } = this.state;
    const { selectedRows } = this.state;
    const selId = selected.id;
    const selType = selected.type;

    if (selectedRows[0]) {
      const opIds = [] as string[];
      data.forEach((row: any) => {
        const formattedRow = this.format(row);
        if (op_types.includes(formattedRow.typeId)) opIds.push(formattedRow.id);
      });

      if (!alreadyFetched[selId] && !selected.leaf) {
        // if child data aren't get from server but we sure they are exist
        this.doExpand(selectedRows[0].rowData, 1, true);
        if (opIds.length > 0)
          // add expanded Operations to parent
          this.setState({ treeData: this.getFormattedData(data, selId) });
      } else {
        let treeData = this.getFormattedData(data, selId);

        if (!expandedRowKeys.includes(selId))
          // if child are there but collapsed
          opIds.push(selId);

        this.setExpanded(opIds);
        this.setFetched(opIds);
        treeData = this.removeLoadingRowsFromTree(opIds.join(','), treeData);
        data.forEach((rowData: any) => {
          const treeRow = treeData.find(treeItem => treeItem.id === rowData.info?.instanceId);
          rowData.info.parentId = treeRow?.parentId;
        });

        this.setState({ treeData }, () => {
          this.setSelected(data);
        });
      }
    }
  };

  protected handleSelectedTreeRow = (selectedRows: any[]) => {
    this.setState({
      selectedRows
    });
  };

  protected handleSaveCellContent = (cell: any, newCellData: string) => {
    this.executeAction('edit', cell, { [cell.column.attribute]: newCellData });
  };

  protected getChildren(childrenList: any, parentId: string) {
    this.state.treeData
      .filter((row: any) => row.parentId == parentId)
      .map((row: any) => {
        const child = row.id;
        if (this.state.loadingGhostRows.some((loadingRow: any) => child === loadingRow)) {
          childrenList.push(child);
          childrenList.concat(this.getChildren(childrenList, child));
        }
      });
    return childrenList;
  }

  protected handleSetIsLoading = (isLoading: boolean) => {
    this.setState({ isLoading });
  };

  private expandStructure(data: any, selId: any, isCreation: boolean, hide: boolean) {
    let treeData = this.getFormattedData(data, selId);
    const { alreadyFetched } = this.state;
    let { expandedRowKeys } = this.state;
    if (selId && !alreadyFetched[selId]) alreadyFetched[selId] = true;

    const parentIds: any[] = [];
    data.forEach((row: any) => {
      const formattedRow = this.format(row);

      // only those obj that has children at the server side and not client side
      const childId = treeData.find(node => node.parentId === formattedRow.id && node.title !== 'Loading...');
      if (childId) {
        parentIds.push(formattedRow.id);
        alreadyFetched[formattedRow.id] = true;
      }
    });

    treeData = this.removeLoadingRowsFromTree(parentIds.join(','), treeData);
    this.setState({ treeData });

    if (!hide) {
      expandedRowKeys = expandedRowKeys.concat(parentIds);
      this.setState({ expandedRowKeys });
    }

    if (isCreation) {
      let data1 = data;
      const size = data.length;
      if (size > 1) data1 = [data[size - 1]];
      this.setSelected(data1);
    }
  }

  private setExpanded(ids: string[]) {
    const { expandedRowKeys } = this.state;

    ids.forEach(id => {
      if (!expandedRowKeys.includes(id)) expandedRowKeys.push(id);
    });

    this.setState({ expandedRowKeys });
  }

  private setFetched(ids: string[]) {
    const { alreadyFetched } = this.state;

    ids.forEach(id => {
      if (!alreadyFetched[id]) alreadyFetched[id] = true;
    });

    this.setState({ alreadyFetched });
  }

  private setSelected(data: any[]) {
    const { selectedRows } = this.state;
    if (selectedRows.length === 1) {
      const selected = selectedRows[0];
      let rowIndex;
      let rowData;
      let needUpdate;
      // special case for Kit_WorkCell
      if (TYPE_KIT_WORK_CELL === selected.rowData.typeId && data.length === 2) {
        rowIndex = selected.rowData.children.length + selected.rowIndex + 1;
        rowData = this.format(data[0]);
        needUpdate = true;
      } else if (data.length === 1) {
        rowIndex = selected.rowData.children.length + selected.rowIndex;
        rowData = this.format(data[0]);
        needUpdate = true;
      }

      if (needUpdate) {
        rowData.children = [];
        const rows = [
          {
            rowData,
            event: { ctrlKey: false, shiftKey: false },
            rowKey: rowData?.id,
            rowIndex
          }
        ];
        this.setState({ selectedRows: rows });
      }
    }
  }
}
