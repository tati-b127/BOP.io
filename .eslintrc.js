// eslint-disable-next-line @typescript-eslint/no-var-requires
const tsConfig = require('./tsconfig.json');

const importRuleList = Object.keys(tsConfig.compilerOptions.paths).map(item => ({
  pattern: `${item}*`,
  group: 'external',
  position: 'after'
}));

const forNodeFiles = {
  overrides: [
    {
      files: ['config/**/*.js', '.storybook/**/*.js'],
      extends: ['plugin:node/recommended'],
      rules: {
        strict: 'off',
        '@typescript-eslint/no-var-requires': 'off',
        'node/no-unsupported-features/es-syntax': 'off',
        'node/no-unpublished-require': 'off',
        'no-return-assign': 'off',
        'no-console': 'off',
        'global-require': 'off',
        'import/no-dynamic-require': 'off'
      }
    }
  ]
};

module.exports = {
  parser: '@typescript-eslint/parser', // Specifies the ESLint parser
  parserOptions: {
    ecmaVersion: 2020, // Allows for the parsing of modern ECMAScript features
    sourceType: 'module', // Allows for the use of imports
    ecmaFeatures: {
      jsx: true // Allows for the parsing of JSX
    }
  },
  settings: {
    react: {
      version: 'detect' // Tells eslint-plugin-react to automatically detect the version of React to use
    }
  },
  extends: [
    'airbnb',
    'airbnb/hooks',
    'plugin:react/recommended', // Uses the recommended rules from @eslint-plugin-react
    'plugin:@typescript-eslint/recommended', // Uses the recommended rules from the @typescript-eslint/eslint-plugin
    'prettier/@typescript-eslint', // Uses eslint-config-prettier to disable ESLint rules from @typescript-eslint/eslint-plugin that would conflict with prettier
    'plugin:prettier/recommended' // Enables eslint-plugin-prettier and eslint-config-prettier. This will display prettier errors as ESLint errors. Make sure this is always the last configuration in the extends array.
  ],
  ...forNodeFiles,
  rules: {
    // Place to specify ESLint rules. Can be used to overwrite rules specified from the extended configs
    // e.g. '@typescript-eslint/explicit-function-return-type': 'off',
    'import/no-extraneous-dependencies': 'off',
    'import/extensions': ['error', 'never', { svg: 'always', style: 'always' }],
    'import/no-unresolved': 'off',
    'no-console': ['error', { allow: Object.keys(console).filter(item => item !== 'log') }],
    'no-unused-vars': 'off',
    'no-unused-expressions': ['error', { allowShortCircuit: true }],
    'no-continue': 'off',
    '@typescript-eslint/no-unused-vars': ['error'],
    '@typescript-eslint/no-var-requires': 'off',
    'import/prefer-default-export': 'off',
    'no-use-before-define': ['error', { functions: false }],
    'import/order': [
      'error',
      {
        pathGroups: importRuleList,
        pathGroupsExcludedImportTypes: ['builtin'],
        'newlines-between': 'always'
      }
    ],
    'no-plusplus': ['error', { allowForLoopAfterthoughts: true }],
    'prefer-destructuring': ['error', { object: true, array: false }],
    'object-shorthand': ['error'],
    'jsx-a11y/click-events-have-key-events': 'off',
    'jsx-a11y/no-static-element-interactions': 'off',
    'react/jsx-filename-extension': [1, { extensions: ['.jsx', '.tsx'] }],
    'react/jsx-indent': [0, 'tab'],
    'react/prop-types': [0, {}],
    'react/display-name': 'off',
    'react/jsx-boolean-value': 'off'
  }
};
