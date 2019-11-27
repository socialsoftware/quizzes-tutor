declare module "showdown";
declare module "chart.js";
declare module "vue-underscore";
declare module "vuetify/lib";
declare module "vuetify-datetime-picker";
declare module "vue-simplemde";

declare module "*.vue" {
  import Vue from "vue";
  export default Vue;
}
