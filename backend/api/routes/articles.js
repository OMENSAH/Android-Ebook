const checkJwt  = require("../middleware/check-auth");
const scopes = require("../middleware/check-scope");
const express = require('express');
const router = express.Router();
router.get('/api/public', (req, res)=> {
    res.json({
      message: 'Hello from a public endpoint! You don\'t need to be authenticated to see this.'
    });
  });
  
  // This route need authentication
  router.get('/api/private', checkJwt, (req, res) =>{
    res.json({
      message: 'Hello from a private endpoint! You need to be authenticated to see this.'
    });
  });

  router.get('/api/private-scoped', checkJwt, scopes.readScope, function(req, res) {
  res.json({
    message: 'Hello from a private endpoint! You need to be authenticated and have a scope of read:messages to see this.'
  });
});


module.exports = router;