import "./class-component-hooks";
import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import Axios from "axios";

import "material-design-icons-iconfont/dist/material-design-icons.css";
import vuetify from "@/plugins/vuetify";

Vue.config.productionTip = false;
Vue.prototype.$http = Axios;

/* TODO remove me
const token = localStorage.getItem("token");
if (token) {
  Vue.prototype.$http.defaults.headers.common["Authorization"] = token;
}*/

new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount("#app");
