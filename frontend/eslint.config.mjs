import js from '@eslint/js';
import globals from 'globals';
import pluginVue from 'eslint-plugin-vue';
import pluginCypress from 'eslint-plugin-cypress';
import {
  defineConfigWithVueTs,
  vueTsConfigs,
} from '@vue/eslint-config-typescript';
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting';

// Flat-config port of the `eslintConfig` block the Vue 2 project carried in
// package.json: plugin:vue/essential + @vue/typescript + @vue/prettier.
// `vueTsConfigs.base` is the equivalent of the old @vue/typescript preset —
// parser setup plus eslint-recommended overrides, without the much stricter
// `recommended` rule set the package now defaults to.
export default defineConfigWithVueTs(
  {
    ignores: ['dist/**', 'coverage/**', 'node_modules/**', 'public/**'],
  },
  js.configs.recommended,
  pluginVue.configs['flat/essential'],
  vueTsConfigs.base,
  skipFormatting,
  {
    languageOptions: {
      globals: { ...globals.browser, ...globals.node },
    },
    rules: {
      'no-console': 'off',
      'no-debugger': 'off',
      quotes: ['error', 'single', { avoidEscape: true }],
    },
  },
  {
    files: ['cypress/**', 'tests/e2e/**'],
    ...pluginCypress.configs.recommended,
  },
  {
    files: ['tests/**'],
    languageOptions: { globals: { ...globals.node } },
  }
);
