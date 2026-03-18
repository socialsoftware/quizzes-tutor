import { createApp, defineComponent, h } from 'vue';
import App from '@/App.vue';
import router from '@/router';
import { createPinia } from 'pinia';
import vuetify from '@/vuetify';
import { VDataTable } from 'vuetify/components/VDataTable';

const pinia = createPinia();
const app = createApp(App);

app.use(pinia);

// Pinia requires a created pinia instance before we invoke useStore in router beforeEach guards
import { useStore } from '@/store';
const store = useStore();
store.initialiseStore();

app.use(router);
app.use(vuetify);

app.mount('#app');
