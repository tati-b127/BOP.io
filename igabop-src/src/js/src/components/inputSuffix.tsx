import React, {Component} from 'react';

import '../styles/inputSuffix.scss';

interface IProps{
    value: string | number;
    suffix: string;
    disabled: boolean;
    type?: string;
    onEdit: Function;
    addClassName: string;
}

interface IState{
    value: string | number;
}

class InputSuffix extends Component<IProps, IState> {
    private suffixRef: any;
    private canvas: HTMLCanvasElement = null;
    getTextWidth = (text: string | number, font: any): number => {
        if (!this.canvas) this.canvas = document.createElement("canvas");

        const context = this.canvas.getContext("2d");
        context.font = font;
        if(typeof text === "number")
            text = this.noExponents(text);
        const metrics = context.measureText('' + text);
        return metrics.width;
    }

    //copy-paste from https://stackoverflow.com/questions/18719775
     noExponents = (value: number) => {
         const data = String(value).split(/[eE]/);
         if(data.length== 1) return data[0];

         let z = '', sign = value < 0 ? '-' : '',
             str = data[0].replace('.', ''),
             mag = Number(data[1]) + 1;

         if(mag<0){
            z= sign + '0.';
            while(mag++) z += '0';
            return z + str.replace(/^\-/,'');
        }
        mag -= str.length;
        while(mag--) z += '0';
        return str + z;
    };

    onEdit = (e: any) => {
        let value = e.target.value;

        this.setState({
            value
        });
        this.props.onEdit(value);
    }

    constructor(props: IProps){
        super(props);
        this.state = {
            value: this.props.value ? this.props.value : ''
        }
        this.suffixRef = React.createRef<HTMLSpanElement>();
    }

    componentDidUpdate(prevProps: IState){
        if(prevProps.value!==this.props.value){
            this.setState({
                value: this.props.value
            })
        }
    }

    render() {
        const width = this.getTextWidth(this.state.value, '14px arial') + 4;
        let inputType = 'text';
        switch(this.props.type){
            case 'real':
                inputType = 'number';
                break;
            case 'string':
                inputType = 'text';
                break;
            case 'integer':
                inputType = 'number';
                break;
            case 'number':
                inputType = 'number';
                break;
            default:
                inputType = 'text';
        }
        return (
            <div className={`input-suffix-container ${this.props.addClassName}`}>
                <input type={inputType} min={0} value={this.state.value} onChange={this.onEdit} disabled={this.props.disabled}/>
                <span ref={this.suffixRef} className="suffix" style={{left: width+'px'}}>{this.props.suffix}</span>
            </div>
        );
    }
}

export default InputSuffix;
