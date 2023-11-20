import React, {Component} from 'react';
import ContentEditable from "react-contenteditable";
import '../styles/inlineEditor.css';
import {faCheckCircle, faTimesCircle,} from '@fortawesome/free-solid-svg-icons';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

interface IInlineEditorProps{
	content: string;
	saveTimeout?: number;
	useControls?: boolean;
	onChange: Function;
	onSaveContent: (content: string) => void;
	forceUpdate?: boolean;
	type?: string;
}

interface IInlineEditorState{
	content: string;
	initialContent: string;
	hasChanges: boolean;
}

//let typingTimer;

class InlineEditor extends Component<IInlineEditorProps, IInlineEditorState> {
	protected containerRef: any;
	constructor(props: IInlineEditorProps) {
		super(props);
		this.containerRef = React.createRef();
		this.state = {
			content: this.props.content,
			initialContent: this.props.content,
			hasChanges: false
		};
	}


	componentDidMount() {
		document.addEventListener("mouseup", this.onDocumentMouseUp);
	}

	componentWillUnmount() {
		document.removeEventListener("mouseup", this.onDocumentMouseUp);
	}

	componentDidUpdate(prevProps: IInlineEditorProps) {
		if (prevProps.content !== this.props.content) {
			this.setState({
				content: this.props.content
			});
		}

		if(prevProps.forceUpdate !== this.props.forceUpdate){
			this.setState({
				content: this.props.content
			});
		}
	}

	render() {
		return (
			<div className="editor-container" ref={this.containerRef}>
				<ContentEditable
					html={this.state.content}
					disabled={false}
					onChange={(e: any) => this.handleContentChange(e)}
				/>
				{this.state.hasChanges && this.props.useControls?
					<div className="editor-controls">
						<FontAwesomeIcon
							title="Сохранить"
							onClick={() => this.handleSave()}
							className="icon save"
							icon={faCheckCircle}
						/>
						<FontAwesomeIcon
							title="Отменить"
							onClick={() => this.handleCancel()}
							className="icon cancel"
							icon={faTimesCircle}
						/>
					</div>
				:null}
			</div>
		);
	}

	protected handleContentChange(e: any) {
		const temp = document.createElement('div');
		temp.innerHTML = e.target.value;
		let content = temp.textContent || temp.innerText;
		content = content.replace(/(\r\n|\n|\r)/gm, '');

		if(this.props.type === 'real')
			content = content.replace(/[^0-9.]/g, '');//replace all non digit chars

		this.setState({
			content: content,
			hasChanges: true
		});
		this.props.onChange();
	}

	protected handleSave() {
		this.props.onSaveContent(this.state.content);
		this.setState({
			initialContent: this.state.content,
			hasChanges: false
		});
	}

	protected handleCancel() {
		this.setState({
			content: this.state.initialContent,
			hasChanges: false
		})
	}

	protected onDocumentMouseUp = (event: any) => {
		if(this.containerRef.current && !this.containerRef.current.contains(event.target)){
			// focus out from editor
			if (this.state.hasChanges) {
				this.handleSave();
			}
		}
	}

}

export default InlineEditor;
