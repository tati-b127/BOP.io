export const getDecimals = (x: number) => {
	if (String(x).split('.')[1]){
		return String(x).split('.')[1].length;
	}else{
        return 0;
    }
}

export const multiply = (a: number|string, b: number|string): number => {
    if(a==null) return 0;
    if(a==undefined) return 0;
    if(a==='') return 0;
    if(isNaN(parseFloat(a as string))) return 0;
    if(b==null) return 0;
    if(b==undefined) return 0;
    if(b==='') return 0;
    if(isNaN(parseFloat(b as string))) return 0;

    a = parseFloat(a as string);
    b = parseFloat(b as string);

    return parseFloat((a*b).toFixed(getDecimals(a)+getDecimals(b)))
}

export const sum = (a: number|string, b: number|string): number => {

    if(a==null) a = 0;
    if(a==undefined) a = 0;
    if(a==='') a = 0;
    if(isNaN(parseFloat(a as string))) a = 0;
    if(b==null) b = 0;
    if(b==undefined) b = 0;
    if(b==='') b = 0;
    if(isNaN(parseFloat(b as string))) b = 0;

    a = parseFloat(a as string);
    b = parseFloat(b as string);
    return  parseFloat((a + b).toFixed(Math.max(getDecimals(a),getDecimals(b))))
}