import React from 'react';
import JoditEditor from 'jodit-react';

import '../styles/wysiwyg.css';
import { specialCharacters } from './consts';

const config = {
  readonly: false, // all options from https://xdsoft.net/jodit/doc/
  usePopupForSpecialCharacters: true,
  controls: {
    symbol: {
      icon: 'omega',
      hotkeys: ['ctrl+shift+i', 'cmd+shift+i'],
      tooltip: 'Insert Special Character',
      popup: (editor: any, current: any, control: any, close: any): any => {
        const container: HTMLElement | undefined = editor.e.fire('generateSpecialCharactersTable.symbols');

        if (container) {
          if (editor.o.usePopupForSpecialCharacters) {
            const box = editor.c.div();

            box.classList.add('jodit-symbols');
            box.appendChild(container);
            // editor.e.on(container, 'close_dialog', close);
            return box;
          }
          const a = container.querySelector('a');

          a && a.focus();

          return container;
        }

        return null;
      }
    }
  },
  buttons: 'undo,redo,symbol',
  buttonsXS: 'undo,redo,symbol',
  buttonsSM: 'undo,redo,symbol',
  buttonsMD: 'undo,redo,symbol',
  specialCharacters
  // height: '100%'
};

interface IWysiwygProps {
  content: any;
  onSaveContent: (content: any) => void;
}

interface IWysiwygState {
  content: any;
  initialContent: any;
}
export class Wysiwyg extends React.Component<IWysiwygProps, IWysiwygState> {
  constructor(props: IWysiwygProps) {
    super(props);
    this.state = {
      content: this.props.content,
      initialContent: this.props.content
    };
  }

  componentDidUpdate(prevProps: IWysiwygProps) {
    if (prevProps.content !== this.props.content) {
      this.setState({
        content: this.props.content
      });
    }
  }

  onSave() {
    this.props.onSaveContent(this.state.content);
    this.setState({
      initialContent: this.state.content
    });
  }

  onCancel() {
    this.setState({
      content: this.state.initialContent
    });
  }

  onChange(content: any) {
    this.setState({
      content
    });
  }

  render() {
    return (
      <div className="wysiwyg-container">
        <div className="editor-wrapper">
          <JoditEditor config={config} value={this.state.content} onChange={content => this.onChange(content)} />
        </div>
        <div className="buttons-container">
          <button onClick={e => this.onSave()}>Сохранить</button>
          <button onClick={e => this.onCancel()}>Отменить</button>
        </div>
      </div>
    );
  }
}

export default Wysiwyg;
