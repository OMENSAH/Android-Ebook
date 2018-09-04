const jwtAuthz = require('express-jwt-authz');
const readScope = jwtAuthz([ 'read:messages' ]);

module.exports = {
    readScope
};