// eslint-disable-next-line @typescript-eslint/no-var-requires
const fs = require('fs');

// eslint-disable-next-line no-template-curly-in-string
const configData = 'registry=${NPM_PROXY_REPO}\n_auth=${NPM_TOKEN}\n@raos:registry=https://nexus.igatec.com/repository/pdm-npm-private/\n';

fs.writeFileSync(`${__dirname}/.npmrc`, configData, {
    encoding: 'utf8'
});
