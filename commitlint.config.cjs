module.exports = {
    extends: ['@commitlint/config-conventional'],
    rules: {
        'subject-case': [0, 'never'],
        'subject-full-stop': [0],
        'body-max-line-length': [0, 'always'],
    },
};
