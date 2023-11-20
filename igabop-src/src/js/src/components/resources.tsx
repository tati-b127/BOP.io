import React from 'react';
import '../styles/resources.scss';
import CustomReactComponent, {
  ICustomReactComponentProps,
  ICustomReactComponentState
} from '@raos/widget-utils/lib/sharedComponents/customReactComponent';

import { RequestAttributeData, TreeObj } from '../store/interfaces';

import { instruction_types, normalization_types, op_types, selects } from './consts';
import Form from './form';
import Attachments from './attachments';
import { Tab, Tabs } from './Tabs';
import Consumed from './consumed';
import Capable from './capable';
import Candidate from './candidate';
import Normalization from './normalization';
import InstructionsEditor from './instructions';

const routesIcon = require('../images/ENXROUT_AP_AppIcon.png');
const capableIcon = require('../images/I_DELRMOrganizationalSystem.png');
const normalizationIcon = require('../images/icons/I_MFG_Bar.png');
const consumedIcon = require('../images/I_InsertUnitCreateAssemblyProcess.png');
const editorIcon = require('../images/I_D3M_Blanking.png');
const attachmentsIcon = require('../images/I_CatalogAddPLMItemCBPRef.png');
const candidatesIcon = require('../images/I_BBSystemGeneral.png');

interface IRow {
  rowKey: string;
  rowIndex: number;
  rowData: TreeObj;
  [id: string]: any;
}

interface IResourcesProps extends ICustomReactComponentProps {
  selectedObjects: IRow[];
  doRefresh?: boolean;
  onRefreshIds: (ids: any[]) => void;
  api: string;
  droppable: Function;
  updateTableData?: Function;
  onAddToStructure?: Function;
  setIsLoading?: Function;
  treeData: TreeObj[];
  rootId: string;
}

interface IResourcesState extends ICustomReactComponentState {
  selectedTab: string;
  capableResourcesData: any[];
  candidateResourcesData: any[];
  consumedResourcesData: any[];
  attachmentsResourcesData: any[];
  selectedCandidateResourcesData: any[];
  selectedConsumedResourcesData: any[];
  selectedCapableResourcesData: any[];
  selectedAttachmentsResourcesData: any[];
  normalizationResourcesData: any;
  instructionText: any;
  dragOver: boolean;
}

// same as 'appendByParentInstanceId' server method
function appendByParentInstanceId(path: string[], obj: TreeObj, treeData: TreeObj[]) {
  const { instanceId } = obj;
  if (instanceId) {
    path.push(obj.instanceId);

    const { parentId } = obj;
    if (parentId) {
      const parent = treeData.find(row => row.id === parentId);
      path = appendByParentInstanceId(path, parent, treeData);
    }
  }

  return path;
}

class Resources extends CustomReactComponent<IResourcesProps, IResourcesState> {
  constructor(props: IResourcesProps) {
    super(props);
    this.state = {
      selectedTab: 'routes',
      capableResourcesData: [],
      candidateResourcesData: [],
      consumedResourcesData: [],
      attachmentsResourcesData: [],
      selectedCandidateResourcesData: [],
      selectedConsumedResourcesData: [],
      selectedCapableResourcesData: [],
      selectedAttachmentsResourcesData: [],
      normalizationResourcesData: null,
      instructionText: null,
      dragOver: false
    };
  }

  // todo it works but awful, make it more correct
  onSaveCellContent = (rowInfo: any, content: any) => {
    const params = { [rowInfo.column.id]: content };
    const composeeId = rowInfo.original.info.composee;
    const resId = rowInfo.original.physicalid;
    const url = `${this.props.api}/instructions/${composeeId}?selects=${selects}`;
    this.props.setIsLoading(true);
    this.Fetch(url, 'PUT', { data: JSON.stringify(params) })
      .then((response: any) => {
        this.state.candidateResourcesData.forEach(el => {
          if (resId === el.physicalid) el.attributes['DELAsmProcessCanUseCnx.V_ResourcesQuantity'].value = content;
        });
        this.setState({ candidateResourcesData: this.state.candidateResourcesData });
      })
      .catch(error => {
        console.log(error);
      })
      .finally(() => this.props.setIsLoading(false));
  };

  onChangeCell = (rowInfo: any, content: any) => {
    // console.log("onChangeCell")
  };

  componentDidMount() {
    if (this.props.selectedObjects) {
      this.getData(this.state.selectedTab);
    }
  }

  componentDidUpdate(prevProps: IResourcesProps) {
    const prevKey = prevProps.selectedObjects ? prevProps.selectedObjects[0]?.rowKey : null;
    const curKey = this.props.selectedObjects ? this.props.selectedObjects[0]?.rowKey : null;

    if (prevKey !== curKey || this.props.doRefresh !== prevProps.doRefresh) {
      this.getData(this.state.selectedTab);
    }
  }

  render() {
    return (
      <div className="resources-container">
        <Tabs centered={true} onChangeTab={(tab: string) => this.onChangeTab(tab)}>
          <Tab key="routes" tabKey="routes" name="" title="Согласование" icon={routesIcon} active>
            <Form
              api={this.props.api}
              url={this.props.url}
              selected={
                this.props.selectedObjects.length > 0 ? this.props.selectedObjects.map((row: any) => row.rowData) : []
              }
              updateTableData={this.props.updateTableData}
              setIsLoading={this.props.setIsLoading}
            />
          </Tab>
          <Tab key="consumed" tabKey="consumed" name="" title="Назначение MBOM" icon={consumedIcon}>
            <Consumed
              data={this.state.consumedResourcesData}
              onSelect={this.onSelectConsumedResourcesData}
              onDelete={this.onDelete}
              selectedTab={this.state.selectedTab}
            />
          </Tab>
          <Tab
            key="normalization"
            tabKey="normalization"
            name=""
            title="Материальное нормирование"
            icon={normalizationIcon}
          >
            <Normalization
              data={this.state.normalizationResourcesData}
              onSaveInstance={this.onSaveNormalizationInstanceData}
            />
          </Tab>
          <Tab key="capable" tabKey="capable" name="" title="Ресурсы" icon={capableIcon}>
            <Capable
              data={this.state.capableResourcesData}
              onSelect={this.onSelectCapableResourcesData}
              onDelete={this.onDelete}
            />
          </Tab>
          <Tab key="editor" tabKey="editor" name="" title="Редактор переходов" icon={editorIcon}>
            <InstructionsEditor
              data={this.state.instructionText}
              selectedObjects={this.props.selectedObjects}
              onCreateInstruction={this.onCreateInstruction}
              onSaveInstruction={this.onSaveInstruction}
            />
          </Tab>
          <Tab key="attachments" tabKey="attachments" name="" title="Вложения" icon={attachmentsIcon}>
            <Attachments
              droppable={this.props.droppable}
              data={this.state.attachmentsResourcesData}
              onAdd={this.onAdd}
              onDelete={this.onDelete}
              onSelect={this.onSelectAttachmentsResourcesData}
            />
          </Tab>
          <Tab key="candidate" tabKey="candidate" name="" title="Создание BOP" icon={candidatesIcon}>
            <Candidate
              data={this.state.candidateResourcesData}
              selectedObjects={this.props.selectedObjects}
              onSelect={this.onSelectCandidateResourcesData}
              onSave={this.onCreate}
              onDelete={this.onDelete}
              onSaveCell={this.onSaveCellContent}
              onChangeCell={this.onChangeCell}
              onCreateInstruction={this.onCreateInstruction}
            />
          </Tab>
        </Tabs>
      </div>
    );
  }

  onSaveInstruction = (content: string) => {
    if (this.props.selectedObjects.length > 0) {
      const instructionId = this.props.selectedObjects[0].rowData.physicalid;
      const json = { 'DELWkiInstructionReference.V_WIInstruction_Text': content };

      const url = `${this.props.api}/instructions/${instructionId}?selects=${selects}`;
      this.props.setIsLoading(true);
      this.Fetch(url, 'PUT', { data: JSON.stringify(json) })
        .then((response: any) => this.props.updateTableData(response, true))
        .catch(error => console.log(error))
        .finally(() => this.props.setIsLoading(false));
    }
  };

  onSaveNormalizationInstanceData = (data: RequestAttributeData) => {
    const pid = this.props.selectedObjects[0].rowData.physicalid;
    const { instanceId } = this.props.selectedObjects[0].rowData;
    const json = data;

    const url = `${this.props.api}/functions/${pid}/instance?instanceId=${instanceId}`;
    this.props.setIsLoading(true);
    this.Fetch(url, 'PUT', { data: JSON.stringify(json) })
      .then((response: any) => {
        this.setState({
          normalizationResourcesData: response
        });
      })
      .catch(error => {
        const initialnormalizationResourcesData = this.state.normalizationResourcesData;
        this.setState({
          normalizationResourcesData: { ...initialnormalizationResourcesData }
        });
      })
      .finally(() => this.props.setIsLoading(false));
  };

  onSelectCandidateResourcesData = (selected: any) => {
    this.setState({ selectedCandidateResourcesData: selected });
  };

  onSelectConsumedResourcesData = (selected: any) => {
    this.setState({ selectedConsumedResourcesData: selected });
  };

  onSelectCapableResourcesData = (selected: any) => {
    this.setState({ selectedCapableResourcesData: selected });
  };

  onSelectAttachmentsResourcesData = (selected: any) => {
    this.setState({ selectedAttachmentsResourcesData: selected });
  };

  protected onChangeTab(tabName: string) {
    this.setState({
      selectedTab: tabName
    });
    this.getData(tabName);
  }

  onCreateInstruction = () => {
    if (this.props.selectedObjects.length > 0) {
      let selected = this.props.selectedObjects[0].rowData;
      let operationPID = selected.physicalid;
      const objectType = selected.type;
      if (instruction_types.includes(objectType)) {
        // type is operation by default
        // if type is instruction, find parent Operation and make fake select
        const parents = this.props.treeData.filter(row => row.id === selected.parentId);
        if (parents.length > 0) {
          selected = parents[0];
          operationPID = parents[0].physicalid;
        }
      }

      if (op_types.concat(instruction_types).includes(objectType)) {
        const url = `${this.props.api}/operations/${operationPID}/createInstruction?selects=${selects}`;
        this.props.setIsLoading(true);
        this.Fetch(url, 'POST')
          .then((data: any) => this.props.onAddToStructure(data, selected))
          .catch((error: any) => console.log(error))
          .finally(() => this.props.setIsLoading(false));
      }
    }
  };

  onCreate = () => {
    if (this.props.selectedObjects.length > 0 && this.state.candidateResourcesData.length > 0) {
      const selected = this.props.selectedObjects[0].rowData;
      const selectedPID = selected.physicalid;
      const selectedType = selected.type;
      const candidateId = this.state.selectedCandidateResourcesData.join(',');
      let url = '';
      switch (selectedType) {
        case 'PPRContext':
          url = `${this.props.api}/pprcontexts/${selectedPID}/insertGeneralSystem?gsys=${candidateId}&duplicate=true&selects=${selects}`;
          break;
        case 'Kit_Factory':
        case 'Kit_ShopFloor':
          url = `${this.props.api}/systems/${selectedPID}/insertGeneralSystem?gsys=${candidateId}&duplicate=true&selects=${selects}`;
          break;
        case 'Kit_WorkCell':
          url = `${this.props.api}/systems/${selectedPID}/insertHeaderOperation?operation=${candidateId}&duplicate=true&selects=${selects}`;
          break;
        case 'Kit_MainOp':
          url = `${this.props.api}/operations/${selectedPID}/createResource?resource=${candidateId}&duplicate=true&selects=${selects}`;
          break;
        default:
          url = `${this.props.api}/pprcontexts/${selectedPID}/insertGeneralSystem?gsys=${candidateId}&duplicate=true&selects=${selects}`;
          break;
      }
      this.props.setIsLoading(true);
      this.Fetch(url, 'POST')
        .then((data: any) => {
          if (op_types.includes(selectedType)) {
            // no need to update candidateResourcesData
            // after successful request resources are updated by calling new request to get candidates (resources)
            // this.setState({candidateResourcesData: data});

            // response data are only operations
            this.props.updateTableData(data, true);
          } else this.props.onAddToStructure(data, selected);
          this.getData(this.state.selectedTab);
        })
        .catch((error: any) => console.log(error))
        .finally(() => this.props.setIsLoading(false));
    }
  };

  onDelete = () => {
    if (this.props.selectedObjects.length > 0) {
      const parentId = this.props.selectedObjects[0].rowData.physicalid;
      const bopPath = this.props.selectedObjects[0].rowData.path.join(',');
      const tab = this.state.selectedTab;
      let data: any[] = [];
      let selected: any[] = [];
      let selectedPhysicalIds: string[] = [];

      if (tab === 'consumed') {
        data = this.state.consumedResourcesData;
        selected = this.state.selectedConsumedResourcesData;
        selectedPhysicalIds = data.filter(item => selected.includes(item.id)).map(item => item.physicalid);
      } else if (tab === 'capable') {
        data = this.state.capableResourcesData;
        selected = this.state.selectedCapableResourcesData;
      } else if (tab === 'candidate') {
        data = this.state.candidateResourcesData;
        selected = this.state.selectedCandidateResourcesData;
      } else if (tab === 'attachments') {
        data = this.state.attachmentsResourcesData;
        selected = this.state.selectedAttachmentsResourcesData;
      }

      if (selected.length > 0) {
        const composee: any[] = []; // array of composee ids which stand for connection
        const result: any[] = []; // client array cleared from deleted objects

        data.forEach(el => {
          const elId = tab === 'consumed' ? el.id : el.physicalid;
          if (selected.includes(elId)) {
            composee.push(el.info.composee);
            if (tab === 'candidate') {
              // todo value is cleared before success response =(
              const newEl = el;
              newEl.attributes['DELAsmProcessCanUseCnx.V_ResourcesQuantity'].value = '';
              result.push(newEl);
            }
          } else result.push(el);
        });

        if (composee.length > 0) {
          const url = `${this.props.api}/instructions/${parentId}?ids=${composee.join(
            ','
          )}&selects=${selects}&bopPath=${bopPath}&pprContextId=${this.props.rootId}`;
          this.props.setIsLoading(true);
          this.Fetch(url, 'DELETE', { data: JSON.stringify({ ids: composee }) })
            .then((reqData: any[]) => {
              if (tab === 'consumed') {
                this.props.onRefreshIds(reqData.filter(item => selectedPhysicalIds.includes(item.physicalid)));
                this.setState({ consumedResourcesData: result });
              } else if (tab === 'capable') this.setState({ capableResourcesData: result });
              else if (tab === 'candidate') this.setState({ candidateResourcesData: result });
              else if (tab === 'attachments') {
                this.setState({ attachmentsResourcesData: result });
                this.props.updateTableData(data, true);
              }
            })
            .catch(error => console.log(error))
            .finally(() => {
              this.props.setIsLoading(false);
            });
        }
      }
    }
  };

  onAdd = (params: any) => {
    if (this.props.selectedObjects.length > 0) {
      const parentId = this.props.selectedObjects[0].rowData.physicalid;
      switch (this.state.selectedTab) {
        case 'consumed':
          break;
        case 'capable':
          const resourceId = params.objectId;
          this.props.setIsLoading(true);
          this.Fetch(
            encodeURI(`${this.props.api}/operations/${parentId}/createResource?resource=${resourceId}`),
            'POST'
          )
            .then((resources: any) => this.getData(this.state.selectedTab))
            .catch((error: any) => console.log(error))
            .finally(() => this.props.setIsLoading(false));
          break;
        case 'editor':
          break;
        case 'attachments':
          const data = new FormData();
          data.append('file', params.file);
          this.props.setIsLoading(true);
          this.FetchBlob(
            encodeURI(`${this.props.url}${this.props.api}/instructions/${parentId}/createDocument?selects=${selects}`),
            'POST',
            this.props.scHeader,
            data,
            'json'
          )
            .then((data: any) => {
              this.getData(this.state.selectedTab);
              this.props.updateTableData(data, true);
            })
            .catch((error: any) => console.log(error))
            .finally(() => this.props.setIsLoading(false));
          break;
      }
    }
  };

  protected getData(resourceType: string) {
    if (this.props.selectedObjects.length > 0) {
      const selectedObject = this.props.selectedObjects[0];
      const parentId = selectedObject.rowData.physicalid;
      const { instanceId } = selectedObject.rowData;
      let { path } = selectedObject.rowData;
      if (!path || path.length === 0) {
        const clientPath: string[] = [];
        path = appendByParentInstanceId(clientPath, selectedObject.rowData, this.props.treeData).reverse();
      }
      const selectedType = selectedObject.rowData.type;
      switch (resourceType) {
        case 'consumed':
          if (op_types.includes(selectedType)) {
            this.props.setIsLoading(true);
            this.Fetch(`${this.props.api}/operations/${parentId}/mbom?instanceId=${path}&selects=${selects}`, 'GET')
              .then((data: any) => {
                this.setState({
                  consumedResourcesData: data
                });
              })
              .catch((error: any) => this.setState({ consumedResourcesData: [] }))
              .finally(() => this.props.setIsLoading(false));
          } else this.setState({ consumedResourcesData: [] });
          break;
        case 'capable':
          if (op_types.includes(selectedType) || selectedType === 'DELWkiInstructionReference') {
            this.props.setIsLoading(true);
            this.Fetch(`${this.props.api}/operations/${parentId}/resources?selects=${selects}`, 'GET')
              .then((data: any) => {
                this.setState({
                  capableResourcesData: data
                });
              })
              .finally(() => this.props.setIsLoading(false));
          }
          break;
        case 'normalization':
          if (normalization_types.includes(selectedType)) {
            this.props.setIsLoading(true);
            // parentId = 'D35565C900003B645E9FE0DF0000A4B7';
            // instanceId = 'D35565C900003B645E9FE0DF0000A6FE';
            this.Fetch(`${this.props.api}/functions/${parentId}?instanceId=${instanceId}`, 'GET')
              .then((data: any) => {
                this.setState({
                  normalizationResourcesData: data
                });
              })
              .finally(() => this.props.setIsLoading(false));
          } else {
            this.setState({
              normalizationResourcesData: null
            });
          }
          break;
        case 'editor':
          if (selectedType === 'DELWkiInstructionReference') {
            const data: any = [];

            // get Instruction texts
            this.props.setIsLoading(true);
            this.Fetch(`${this.props.api}/instructions/${parentId}?selects=${selects}`, 'GET')
              .then((instruction: any) => {
                this.setState({
                  instructionText: instruction.attributes['DELWkiInstructionReference.V_WIInstruction_Text'].value
                });
              })
              .finally(() => this.props.setIsLoading(false));
          }
          break;
        case 'attachments':
          this.props.setIsLoading(true);
          this.Fetch(`${this.props.api}/instructions/${parentId}/documents?selects=${selects}`, 'GET')
            .then((documents: any) => this.setState({ attachmentsResourcesData: documents }))
            .finally(() => this.props.setIsLoading(false));
          break;
        case 'candidate':
          let url = '';
          switch (selectedType) {
            case 'PPRContext':
              url = `${this.props.api}/pprcontexts/${parentId}/candidates?selects=${selects}`;
              break;
            case 'Kit_Factory':
            case 'Kit_ShopFloor':
            case 'Kit_WorkCell':
              url = `${this.props.api}/systems/${parentId}/candidates?selects=${selects}`;
              break;
            case 'Kit_MainOp':
              url = `${this.props.api}/operations/${parentId}/candidates?selects=${selects}`;
              break;
          }

          if (url) {
            this.props.setIsLoading(true);
            this.Fetch(url, 'GET')
              .then((data: any) => {
                this.setState({
                  candidateResourcesData: data
                });
              })
              .catch((error: any) => {
                this.setState({
                  candidateResourcesData: []
                });
              })
              .finally(() => this.props.setIsLoading(false));
          } else this.setState({ candidateResourcesData: [] });

          break;
      }
    } else {
      this.setState({
        consumedResourcesData: [],
        candidateResourcesData: [],
        attachmentsResourcesData: []
      });
    }
  }
}

export default Resources;
