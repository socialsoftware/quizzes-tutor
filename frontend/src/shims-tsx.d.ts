import { VNode } from 'vue';

declare global {
  namespace JSX {
    // tslint:disable no-empty-interface
    interface Element extends VNode {}
    // tslint:disable no-empty-interface
    interface ElementClass {
      $props: {};
    }
    interface IntrinsicElements {
      [elem: string]: any;
    }
  }
}
