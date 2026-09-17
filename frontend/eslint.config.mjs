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
      // A binding that has to stay but is deliberately not read is marked by
      // naming it `_foo`, the usual convention; the codebase already did this.
      'no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
          caughtErrorsIgnorePattern: '^_',
        },
      ],
    },
  },
  {
    // vue-eslint-parser reports a `<script setup>` component import as unused
    // when the template refers to it in kebab-case (`<edit-course-dialog>` for
    // `EditCourseDialog`), which Vue resolves but the rule does not follow.
    // Capitalised bindings in an SFC are components, so skip them here; the
    // template is what decides whether they are used.
    files: ['**/*.vue', '**/*.ts'],
    rules: {
      // The base rule does not understand TypeScript parameter properties
      // (`constructor(public slotNumber: number)` declares a class field), so
      // the TypeScript-aware one replaces it here.
      'no-unused-vars': 'off',
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^(_|[A-Z])',
          caughtErrorsIgnorePattern: '^_',
        },
      ],
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
