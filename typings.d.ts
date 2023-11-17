import theme from '@styles/theme';

declare module '@images/*';
declare module '@components/*';
declare module '@modules/*';
declare module '@components';
declare module '@utils/*';
declare module '@interfaces/*';
declare module '@styles/*';


declare module 'styled-components' {
  // @ts-ignore
  type Theme = typeof theme;
  export interface DefaultTheme extends Theme{}
}

declare global {
  interface Dictionary<T> {
    [Key: string]: T;
  }
}

declare module 'lodash' {
  interface LoDashStatic {
    get<TReturnType extends any>(object: any, path: string, defaultValue: any): TReturnType;
  }
}


