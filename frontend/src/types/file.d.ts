import axios from 'axios'
declare module 'axios' {
    export interface AxiosResponse<T = any> extends Promise<T> { }
}
declare module '*.png' {
    const content: string
    export default content;
}
declare module "*.svg" {
    const content: string;
    export default content;
}