module.exports = {
    root: true,
    parser: '@typescript-eslint/parser',
    plugins: ['@typescript-eslint', 'react', 'react-hooks', 'react-refresh'],
    extends: [
        'eslint:recommended',
        'plugin:react/recommended',
        'plugin:@typescript-eslint/recommended',
        'plugin:react-hooks/recommended',
        'prettier',
    ],
    settings: { react: { version: 'detect' } },
    parserOptions: { ecmaVersion: 2020, sourceType: 'module' },
    ignorePatterns: ['dist', 'node_modules'],
    rules: {
        'react/react-in-jsx-scope': 'off',
        'react-refresh/only-export-components': ['error', { allowConstantExport: true }],
    },
    overrides: [
        {
            files: ['src/components/ui/button.tsx'],
            rules: {
                'react-refresh/only-export-components': 'off'
            }
        }
    ]
};
