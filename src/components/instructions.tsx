import React, { Component } from 'react';

import Wysiwyg from './wysiwyg';

import '../styles/instructionsEditor.scss';

import { op_types, instruction_types } from './consts';

interface IProps {
  data: any;
  onCreateInstruction: Function;
  onSaveInstruction: Function;
  selectedObjects: any[];
}

interface IState {
  content: any;
}

class InstructionsEditor extends Component<IProps, IState> {
  constructor(props: IProps) {
    super(props);
    this.state = {
      content: props.data
    };
  }

  onSaveInstruction = (content: any) => {
    const span = document.createElement('span');
    span.innerHTML = content;
    this.props.onSaveInstruction(span.innerText);
  };

  onCreateInstruction = () => {
    this.props.onCreateInstruction();
  };

  componentDidUpdate(prevProps: IProps) {
    if (prevProps.data !== this.props.data) {
      this.setState({
        content: this.props.data
      });
    }
  }

  render() {
    let createButDisabled = true;
    if (this.props.selectedObjects.length > 0) {
      const selectedRowType = this.props.selectedObjects[0].rowData.type;
      if (op_types.concat(instruction_types).includes(selectedRowType)) {
        createButDisabled = false;
      }
    }
    return (
      <div className="instructions-container">
        <div className="buttons-container">
          <button onClick={this.onCreateInstruction} disabled={createButDisabled}>
            Создать переход
          </button>
        </div>
        <Wysiwyg content={this.state.content} onSaveContent={this.onSaveInstruction} />
      </div>
    );
  }
}

export default InstructionsEditor;
