import './assets/main.css'
import './index.css'
import 'primevue/resources/themes/aura-light-green/theme.css'
import 'primeicons/primeicons.css';

import Tooltip from 'primevue/tooltip';
import {createApp} from 'vue'
import App from './App.vue'
import router from './router';
import PrimeVue from 'primevue/config';

createApp(App)
    .use(router)
    .directive('tooltip', Tooltip)
    .use(PrimeVue, {
        locale: {
            monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
            monthNamesShort: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
        }
    })
    .mount('#app')