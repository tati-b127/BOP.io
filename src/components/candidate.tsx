import React, { Component } from 'react';
import { Column, RowInfo } from 'react-table';

import SelectableTable from './selectableTable';

import '../styles/candidate.scss';
import InlineEditor from './inlineEditor';
import { op_types, resource_types, instruction_types } from './consts';

const iconKitAuxilTool = require('../images/filterIcons/I_KitAuxilTool.png');
const iconKitBenchTool = require('../images/filterIcons/I_KitBenchTool.png');
const iconKitCuttingTool = require('../images/filterIcons/I_KitCuttingTool.png');
const iconKitFixture = require('../images/filterIcons/I_KitFixture.png');
const iconKitIndustrialMachine = require('../images/filterIcons/I_KitIndustrialMachine.png');
const iconKitMeasureTool = require('../images/filterIcons/I_KitMeasureTool.png');
const iconKitProtector = require('../images/filterIcons/I_KitProtector.png');
const iconKitTechnologicalTooling = require('../images/filterIcons/I_KitTechnologicalTooling.png');
const iconKitWorker = require('../images/filterIcons/I_KitWorker.png');

interface IProps {
  data: any[];
  selectedObjects: any[];
  onSave: Function;
  onSelect: Function;
  onDelete: Function;
  onChangeCell: Function;
  onSaveCell: Function;
  onCreateInstruction: Function;
}

interface IState {
  selectedRows: any[];
  filters: any[];
  candidateData: any[];
  searchValue: string;
}

const filters = [
  {
    id: 'KitIndustrialMachine',
    icon: iconKitIndustrialMachine,
    title: 'Оборудование',
    filterTypes: [
      'Kit_IndustrialMachine',
      'ResourceNCMillMachine​',
      'ResourceNCMillTurnMachine​',
      'ResourceNCWireEDMMachine​',
      'ResourceNCPowderBedMachine​'
    ],
    checked: true
  },
  {
    id: 'KitFixture',
    icon: iconKitFixture,
    title: 'Приспособление',
    filterTypes: ['Kit_Fixture​', 'ResourceNCAccessory​'],
    checked: true
  },
  {
    id: 'KitBenchTool',
    icon: iconKitBenchTool,
    title: 'Слесарно-монтажный инструмент',
    filterTypes: ['Kit_BenchTool'],
    checked: false
  },
  {
    id: 'KitCuttingTool',
    icon: iconKitCuttingTool,
    title: 'Режущий инстумент',
    filterTypes: [
      'Kit_CuttingTool​',
      'ResourceNCReamerTool​',
      'ResourceNCDiamondInsert​',
      'ResourceNCEndMillTool​',
      'ResourceNCCounterboreMillTool​',
      'ResourceNCRoundInsert​',
      'ResourceNCFaceMillTool​',
      'ResourceNCThreadMillTool​',
      'ResourceNCTriangularInsert​',
      'ResourceNCConicalMillTool​',
      'ResourceNCTapTool​',
      'ResourceNCSquareInsert​',
      'ResourceNCTSlotterTool​',
      'ResourceNCBallStylusTool​',
      'ResourceNCTrigonInsert​',
      'ResourceNCBarrelMillTool​',
      'ResourceNCCylinderStylusTool​',
      'ResourceNCThreadInsert​',
      'ResourceNCDrillTool​',
      'ResourceNCExternalInsertHolder​',
      'ResourceNCGrooveInsert​',
      'ResourceNCSpotDrillTool​',
      'ResourceNCInternalInsertHolder​',
      'ResourceNCToolHolder​',
      'ResourceNCCenterDrillTool​',
      'ResourceNCExternalGrooveInsertHolder​',
      'ResourceNCMillAndDrillToolAssembly​',
      'ResourceNCCountersinkTool​',
      'ResourceNCFrontalGrooveInsertHolder​',
      'ResourceNCTurningToolAssembly​',
      'ResourceNCMultiDiamDrillTool​',
      'ResourceNCInternalGrooveInsertHolder​',
      'ResourceNCWire​',
      'ResourceNCTwoSidesChamferingTool​',
      'ResourceNCThreadExternalInsertHolder​',
      'ResourceNCBoringAndChamferingTool​',
      'ResourceNCThreadInternalInsertHolder​',
      'ResourceNCBoringBar​',
      'ResourceNCMillingGenericInsert​'
    ],
    checked: false
  },
  {
    id: 'KitTechnologicalTooling',
    icon: iconKitTechnologicalTooling,
    title: 'Вспомогательный инструмент',
    filterTypes: ['Kit_AuxilTool'],
    checked: false
  },
  {
    id: 'KitWorker',
    icon: iconKitWorker,
    title: 'Профессия',
    filterTypes: ['Kit_Worker'],
    checked: false
  },
  {
    id: 'KitProtector',
    icon: iconKitProtector,
    title: 'Средства защиты',
    filterTypes: ['Kit_Protector'],
    checked: false
  },
  {
    id: 'KitMeasureTool',
    icon: iconKitMeasureTool,
    title: 'Средства измерения',
    filterTypes: ['Kit_MeasureTool'],
    checked: false
  },
  {
    id: 'KitAuxilTool',
    icon: iconKitAuxilTool,
    title: 'Технологическая оснастка',
    filterTypes: ['Kit_TechnologicalTooling'],
    checked: false
  }
];

class Candidate extends Component<IProps, IState> {
  constructor(props: IProps) {
    super(props);
    this.state = {
      selectedRows: [],
      filters,
      candidateData: this.filterCheckboxCandidateData(props.data, filters),
      searchValue: ''
    };
  }

  componentDidUpdate(prevProps: IProps) {
    if (this.props.data !== prevProps.data) {
      this.setState({
        candidateData: this.filterCheckboxCandidateData(this.props.data, this.state.filters)
      });
    }
  }

  protected candidateResourcesColumns: Array<Column> = [
    {
      Header: 'Кол.',
      accessor: 'DELAsmProcessCanUseCnx.V_ResourcesQuantity',
      width: 40,
      Cell: (rowInfo: RowInfo) => {
        const attr = rowInfo.original.attributes['DELAsmProcessCanUseCnx.V_ResourcesQuantity'];
        const value = attr ? attr.value : '';
        const formatted = value === '' ? '' : Math.floor(attr.value);
        return (
          <div className="res-cell">
            {formatted !== '' ? (
              <InlineEditor
                // useControl={false}
                saveTimeout={1000}
                onChange={(content: any) => this.props.onChangeCell(rowInfo, content)}
                content={`${formatted}`}
                onSaveContent={(content: any) => this.props.onSaveCell(rowInfo, content)}
              />
            ) : (
              formatted
            )}
          </div>
        );
      }
    },
    {
      Header: 'Название',
      accessor: 'name',
      Cell: (rowInfo: RowInfo) => {
        const title = rowInfo.original.attributes['PLMEntity.V_Name'].value;
        const desc = rowInfo.original.attributes['PLMEntity.V_description'].value;
        const value = `${title} ${desc}`.trim();
        let typeIconURL = '';
        const icon = rowInfo.original.info['type.property[IPML.IconName].value'];
        if (icon) typeIconURL = require(`../images/icons/${icon}.png`);
        return (
          <div className="res-cell">
            <img src={typeIconURL} />
            <div>{value}</div>
          </div>
        );
      }
    }
  ];

  onSave = () => {
    this.setState({
      selectedRows: []
    });
    this.props.onSave();
  };

  onSelect = (selectedRows: any[]) => {
    this.setState({ selectedRows });
    this.props.onSelect(selectedRows);
  };

  onToggleCheckbox = (e: any) => {
    const { id } = e.target;

    const filters = this.state.filters.map((filter: any) => {
      const filterItem = filter;
      if (filterItem.id === id) {
        filterItem.checked = !filter.checked;
      }
      return filterItem;
    });

    this.setState({
      filters,
      candidateData: this.filterSearchCandidateData(
        this.filterCheckboxCandidateData(this.props.data, filters),
        this.state.searchValue
      )
    });
  };

  onChangeSearchValue = (e: any) => {
    const { value } = e.target;
    this.setState({
      searchValue: value,
      candidateData: this.filterSearchCandidateData(
        this.filterCheckboxCandidateData(this.props.data, this.state.filters),
        value
      )
    });
  };

  filterCheckboxCandidateData = (data: any[], filters: any[]) => {
    if (this.props.selectedObjects.length > 0) {
      const selectedRowType = this.props.selectedObjects[0].rowData.type;
      if (op_types.includes(selectedRowType)) {
        const selectedFilterTypes = filters
          .filter(filter => filter.checked)
          .map(filter => filter.filterTypes)
          .reduce((acc, cur) => acc.concat(cur), []);
        return data; // enable to debug when VPLMReference
        /* return data.filter((item) => {
					var type = item.basics.type.name;
					return selectedFilterTypes.includes(type);
				}); */
      }
      return data;
    }
    return data;
  };

  filterSearchCandidateData = (data: any[], searchValue: string) => {
    if (this.props.selectedObjects.length > 0) {
      const selectedRowType = this.props.selectedObjects[0].rowData.type;
      if (op_types.concat(resource_types).includes(selectedRowType)) {
        return data.filter(item => {
          const name = item.attributes['PLMEntity.V_Name'].value;
          if (name) {
            return name.toLowerCase().includes(searchValue.toLowerCase());
          }
          return false;
        });
      }
      return data;
    }
    return data;
  };

  render() {
    let createInstructionButDisabled = true;
    if (this.props.selectedObjects.length > 0) {
      const selectedRowType = this.props.selectedObjects[0].rowData.type;
      if (op_types.concat(instruction_types).includes(selectedRowType)) {
        createInstructionButDisabled = false;
      }
    }

    let saveButDisabled = true;
    if (this.props.selectedObjects.length > 0 && this.state.selectedRows.length > 0) {
      saveButDisabled = false;
    }
    return (
      <div className="candidate-container">
        <div className="buttons-container">
          <button onClick={this.onSave} disabled={saveButDisabled}>
            Добавить
          </button>
          <button onClick={() => this.props.onDelete()} disabled={saveButDisabled}>
            Удалить
          </button>
          <button onClick={() => this.props.onCreateInstruction()} disabled={createInstructionButDisabled}>
            Создать переход
          </button>
        </div>
        {this.props.selectedObjects.length > 0 &&
        op_types.includes(this.props.selectedObjects[0].rowData.type) &&
        this.state.candidateData &&
        false ? (
          <div className="filter-container">
            {this.state.filters.map((filter: any) => {
              return (
                <div className="filter-item">
                  <input
                    type="checkbox"
                    id={filter.id}
                    checked={filter.checked}
                    onChange={e => this.onToggleCheckbox(e)}
                  />
                  <label htmlFor={filter.id}>
                    <img src={filter.icon} title={filter.title} />
                  </label>
                </div>
              );
            })}
          </div>
        ) : null}
        {this.props.selectedObjects.length > 0 &&
        (resource_types.includes(this.props.selectedObjects[0].rowData.type) ||
          op_types.includes(this.props.selectedObjects[0].rowData.type)) &&
        this.state.candidateData ? (
          <div className="input-container">
            <input value={this.state.searchValue} onChange={this.onChangeSearchValue} />
          </div>
        ) : null}
        <SelectableTable
          columns={this.candidateResourcesColumns}
          data={this.state.candidateData}
          onSelectRows={this.onSelect}
          selectedRows={this.state.selectedRows}
        />
      </div>
    );
  }
}

export default Candidate;
