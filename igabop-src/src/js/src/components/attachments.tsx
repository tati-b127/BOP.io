import React, {Component} from 'react';
import SelectableTable from './selectableTable';
import {attachmentsResourcesColumns} from './formats/columns';

import '../styles/attachments.scss';

interface IProps{
	droppable: Function;
	data: any[];
	onAdd: Function;
	onDelete: Function;
	onSelect: Function;
}

interface IState{
	dragOver: boolean
}

class Attachments extends Component<IProps, IState> {
	protected ref: any;
	constructor(props: IProps){
		super(props);
        this.state = {
			dragOver: false
        }
        this.ref = React.createRef();
	}

	componentDidMount(){
		this.props.droppable(this.ref.current, {
			drop: (data: any) => this.onDocumentDrop(data)
		});
	}

	protected onDocumentDrop(data: any) {
		try {
			const { displayName, objectId } = JSON.parse(data).data.items[0];
			const params = {
				objectId
			};
			this.props.onAdd(params);
		} catch (e) {
			console.log(e)
		}
	};

	protected onChangeFile(event: any){
		const files = event.target.files;
		const file = files[0];
		const params = {
			file: file
		}
		this.props.onAdd(params);
	}

	render() {
		return (
			<div className="attachments-container">
				<div className="buttons-container">
					<button onClick={() => this.props.onDelete()}>Удалить</button>
					<input
						type="file"
						id="file"
						className="inputfile"
						onChange={(e: any) => this.onChangeFile(e)}
						onClick={(e: any) => this.onClick(e)}
						/>
					<label className="button" htmlFor={"file"}>Прикрепить документ</label>
				</div>
				<div
					ref={this.ref}
					onDragOver={() => this.setState({dragOver: true})}
					onDragLeave={() => this.setState({dragOver: false})}
					className={`attachments-dnd-container${this.state.dragOver?' dragging':''}`}>
						<SelectableTable
							columns={attachmentsResourcesColumns}
							data={this.props.data}
							onSelectRows={this.props.onSelect}/>
				</div>
			</div>
		);
	}

	protected onClick(event: any){
		//for chrome
		event.target.value = null;
	}
}

export default Attachments;
