import '@fortawesome/fontawesome-free/css/all.css';
import '@mdi/font/css/materialdesignicons.css';
import 'material-design-icons-iconfont/dist/material-design-icons.css';
import 'vuetify/styles';
import { createVuetify } from 'vuetify';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';
import { aliases, mdi } from 'vuetify/iconsets/mdi';
import { fa } from 'vuetify/iconsets/fa';

import { h } from 'vue';
import type { IconSet, IconProps } from 'vuetify';

const custom: IconSet = {
  component: (props: IconProps) => {
    const icon = props.icon as string;
    if (typeof icon !== 'string') return h(props.tag);

    if (icon.startsWith('fa') && icon.includes('-')) {
      return h(props.tag, { class: [icon] });
    } else if (icon.startsWith('mdi-')) {
      return h(props.tag, { class: ['mdi', icon] });
    } else {
      return h(props.tag, { class: ['material-icons'] }, icon);
    }
  },
};

export default createVuetify({
  components,
  directives,
  defaults: {
    VBtn: {
      variant: 'elevated',
      style: 'text-transform: uppercase; letter-spacing: 0.0892857143em;',
    },
    VCardActions: {
      VBtn: {
        variant: 'elevated',
      },
    },
    VTextField: {
      variant: 'underlined',
    },
    VSelect: {
      variant: 'underlined',
    },
    VDataTable: {
      itemsPerPageText: 'Rows per page:',
    },
    VDataTableServer: {
      itemsPerPageText: 'Rows per page:',
    },
  },
  icons: {
    defaultSet: 'custom',
    aliases,
    sets: {
      custom,
    },
  },
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        colors: {
          primary: '#1976D2',
          secondary: '#424242',
          accent: '#8c9eff',
          error: '#7f0000',   // vermelho mais escuro e carregado
        }
      },
      dark: {
        colors: {
          primary: '#1976D2',
          secondary: '#424242',
          accent: '#8c9eff',
          error: '#7f0000',   // vermelho mais escuro e carregado
        }
      },
    },
  },
});
