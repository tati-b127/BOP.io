import React from 'react';
import '../styles/loader.scss';

interface ILoaderProps{
	size?: number;
	style?: any;
}

const Loader = (props: ILoaderProps) => {
	return (
		<div className={'mask'}>
			<div className={`loader${props.style?' '+props.style:''}`}/>
		</div>
	);
};

export const RowLoader = (props: ILoaderProps) => {
	return (
		<div className={`loader${props.style?' '+props.style:''}`}/>
	);
};

export default Loader;
