import React, { Component } from 'react';
import SelectableTable from './selectableTable';
import {capableResourcesColumns} from './formats/columns';

import '../styles/capable.scss';

interface IProps{
    data: any[];
    onSelect: Function;
    onDelete: Function;
}

interface IState{

}

class Capable extends Component<IProps, IState> {
    constructor(props: IProps){
        super(props);
    }
    render() {
        return (
            <div className="capable-container">
                <div className="buttons-container">
					<button onClick={() => this.props.onDelete()}>Удалить</button>
				</div>
                <SelectableTable
                    columns={capableResourcesColumns}
                    data={this.props.data}
                    onSelectRows={this.props.onSelect}/>
            </div>
        );
    }
}

export default Capable;
