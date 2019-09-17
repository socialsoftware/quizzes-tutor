declare module "showdown";
declare module "chart.js";
declare module "vue-underscore";
declare module "vue-datetime";
declare module "vuetify/lib";

declare module "*.vue" {
  import Vue from "vue";
  export default Vue;
}
