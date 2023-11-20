import * as React from 'react';
import '../styles/dropContainerComponent.css';
const dropIcon = require('../images/dropIcon.svg');

export const DropContainerComponent = (label: string): React.ReactElement => {
	return (
		<div className="drop-container">
			<div className="drop-icon-container">
				<img src={dropIcon} className="drop-icon"></img>
				<br />
				{label}
			</div>
		</div>
	);
};
