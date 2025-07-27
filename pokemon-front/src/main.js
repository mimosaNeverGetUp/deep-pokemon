import './assets/main.css'
import './index.css'
import 'primevue/resources/themes/aura-light-green/theme.css'
import 'primeicons/primeicons.css';

import Tooltip from 'primevue/tooltip';
import {createApp} from 'vue'
import App from './App.vue'
import router from './router';
import PrimeVue from 'primevue/config';
import { createI18n } from 'vue-i18n'
import ToastService from 'primevue/toastservice';
import { zh } from './locales/zh/zh.js'
import { en } from './locales/en/en.js'

let locale = navigator.language?.startsWith("zh") ? "zh" : "en";
if (localStorage.getItem("locale") ) {
    locale = localStorage.getItem("locale");
}

const i18n = createI18n({
    locale: locale,
    fallbackLocale: 'en',
    messages: {
        en: en,
        zh: zh
    }
});

createApp(App)
    .use(router)
    .directive('tooltip', Tooltip)
    .use(PrimeVue, {
        locale: {
            monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
            monthNamesShort: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
        }
    })
    .use(i18n)
    .use(ToastService)
    .mount('#app')