import React, { Component } from 'react';

import SelectableTable from './selectableTable';
import { consumedResourcesColumns } from './formats/columns';

import '../styles/consumed.scss';

interface IProps {
  data: any[];
  onSelect: Function;
  onDelete: Function;
  selectedTab: string;
}

interface IState {}

class Consumed extends Component<IProps, IState> {
  constructor(props: IProps) {
    super(props);
  }

  render() {
    return (
      <div className="consumed-container">
        <div className="buttons-container">
          <button onClick={() => this.props.onDelete()}>Удалить</button>
        </div>
        <SelectableTable
          columns={consumedResourcesColumns}
          data={this.props.data}
          onSelectRows={this.props.onSelect}
          selectedTab={this.props.selectedTab}
        />
      </div>
    );
  }
}

export default Consumed;
