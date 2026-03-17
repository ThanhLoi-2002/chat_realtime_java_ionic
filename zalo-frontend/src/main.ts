import { createApp } from 'vue';
import { IonicVue } from '@ionic/vue';
import { createPinia } from "pinia"

import router from './router';

import App from './App.vue';
import './assets/tailwind.css'

/* Core CSS required for Ionic components to work properly */
import '@ionic/vue/css/core.css';

/* Basic CSS for apps built with Ionic */
// import '@ionic/vue/css/normalize.css';
import '@ionic/vue/css/structure.css';
import '@ionic/vue/css/typography.css';

(window as any).global = window

const app = createApp(App)
  .use(IonicVue)
  .use(createPinia())
  .use(router);

router.isReady().then(() => {
  app.mount('#app');
});